"""Exercise exact artifact copying and interrupted publication against real Maven."""

import io
from pathlib import Path
import shutil
import tempfile
import unittest
from unittest.mock import patch
from zipfile import ZipFile

from publish_github_packages import mirror, unpack

GROUP = "io.github.example.themes"
VERSION = "1.2.3"


def fixture():
    files = {}
    for artifact, packaging in [("themes", "pom"), ("theme-glass", "jar")]:
        prefix = f"{GROUP.replace('.', '/')}/{artifact}/{VERSION}/{artifact}-{VERSION}"
        parent = "" if packaging == "pom" else (
            f"<parent><groupId>{GROUP}</groupId><artifactId>themes</artifactId>"
            f"<version>{VERSION}</version></parent>")
        coordinates = (f"<groupId>{GROUP}</groupId><version>{VERSION}</version>"
                       if packaging == "pom" else "")
        pom = (f'<project xmlns="http://maven.apache.org/POM/4.0.0">'
               f"<modelVersion>4.0.0</modelVersion>{parent}{coordinates}"
               f"<artifactId>{artifact}</artifactId><packaging>{packaging}</packaging>"
               f"<name>{artifact}</name></project>").encode()
        files[prefix + ".pom"] = pom
        if packaging == "jar":
            # Include Maven's embedded POM to catch deploy-file auto-attachment
            # accidentally overwriting the signed POM during a partial retry.
            for classifier in ["", "-sources", "-javadoc"]:
                stream = io.BytesIO()
                with ZipFile(stream, "w") as jar:
                    jar.writestr(f"META-INF/maven/{GROUP}/{artifact}/pom.xml", pom)
                    jar.writestr("theme.css", "body { color: red; }")
                files[prefix + classifier + ".jar"] = stream.getvalue()
    for path in list(files):
        files[path + ".asc"] = b"fixture-signature:" + path.encode()
    return files


class MirrorTests(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory()
        self.addCleanup(self.temporary.cleanup)
        self.root = Path(self.temporary.name)
        self.bundle = self.root / "central-bundle.zip"
        self.repository = self.root / "repository"
        self.repository.mkdir()
        self.files = fixture()
        self.write_bundle()

    def write_bundle(self):
        with ZipFile(self.bundle, "w") as bundle:
            for name, data in self.files.items():
                bundle.writestr(name, data)

    def publish(self):
        mirror(self.bundle, GROUP, VERSION, self.repository.as_uri(), self.root / "assets")

    def test_requires_signature_before_any_publication(self):
        del self.files[next(path for path in self.files if path.endswith(".jar.asc"))]
        self.write_bundle()
        with patch("publish_github_packages.subprocess.run") as run:
            with self.assertRaisesRegex(ValueError, "Missing signed"):
                self.publish()
            run.assert_not_called()

    def test_rejects_other_version(self):
        with self.assertRaisesRegex(ValueError, "Unexpected coordinates"):
            unpack(self.bundle, self.root / "unpacked", GROUP, "9.9.9")

    def test_refuses_conflicting_existing_artifact(self):
        name = next(iter(self.files))
        file = self.repository / name
        file.parent.mkdir(parents=True)
        file.write_bytes(b"another release")
        with patch("publish_github_packages.subprocess.run") as run:
            with self.assertRaisesRegex(ValueError, "differs"):
                self.publish()
            run.assert_not_called()

    def test_complete_retry_needs_no_maven(self):
        for name, data in self.files.items():
            file = self.repository / name
            file.parent.mkdir(parents=True, exist_ok=True)
            file.write_bytes(data)
        with patch("publish_github_packages.subprocess.run") as run:
            self.publish()
            run.assert_not_called()
        for name, data in self.files.items():
            self.assertEqual((self.root / "assets" / Path(name).name).read_bytes(), data)

    @unittest.skipUnless(shutil.which("mvn"), "Maven integration runs in GitHub CI")
    def test_real_maven_publication_and_partial_retry(self):
        self.publish()
        for name, data in self.files.items():
            self.assertEqual((self.repository / name).read_bytes(), data, name)
        # Retain POMs, remove a JAR and a multi-part signature, then retry.
        missing = [next(path for path in self.files if path.endswith("-sources.jar")),
                   next(path for path in self.files if path.endswith(".pom.asc"))]
        for name in missing:
            (self.repository / name).unlink()
        poms = {name: (self.repository / name).stat().st_mtime_ns
                for name in self.files if name.endswith(".pom")}
        self.publish()
        for name, data in self.files.items():
            self.assertEqual((self.repository / name).read_bytes(), data, name)
        for name, modified in poms.items():
            self.assertEqual((self.repository / name).stat().st_mtime_ns, modified,
                             "A retry must not replace an existing signed POM")
        metadata = next(self.repository.rglob("maven-metadata.xml"))
        self.assertIn(VERSION, metadata.read_text())


if __name__ == "__main__":
    unittest.main()
