"""Mirror a signed Central bundle without rebuilding or replacing existing bytes."""

import argparse
import base64
import os
from pathlib import Path, PurePosixPath
import shutil
import subprocess
import tempfile
from urllib.error import HTTPError
from urllib.request import Request, urlopen
import xml.etree.ElementTree as ET
from zipfile import ZipFile

NS = {"m": "http://maven.apache.org/POM/4.0.0"}


def unpack(bundle, destination, group, version):
    with ZipFile(bundle) as archive:
        for entry in archive.infolist():
            path = PurePosixPath(entry.filename)
            if path.is_absolute() or ".." in path.parts or "\\" in entry.filename:
                raise ValueError(f"Invalid bundle path: {entry.filename}")
        archive.extractall(destination)
    artifacts = []
    poms = sorted(destination.rglob("*.pom"))
    if not poms:
        raise ValueError("The Central bundle contains no POMs")
    for pom in poms:
        model = ET.parse(pom).getroot()
        def value(key):
            return model.findtext(f"m:{key}", namespaces=NS)
        artifact = value("artifactId")
        actual_group = value("groupId") or value("parent/m:groupId")
        actual_version = value("version") or value("parent/m:version")
        if actual_group != group or actual_version != version:
            raise ValueError(f"Unexpected coordinates in {pom.name}")
        if not value("name"):
            raise ValueError(f"Missing name in {pom.name}")
        packaging = value("packaging") or "jar"
        if packaging not in ("pom", "jar"):
            raise ValueError(f"Unsupported packaging: {packaging}")
        prefix = f"{artifact}-{version}"
        expected = Path(*group.split("."), artifact, version, prefix + ".pom")
        if pom.relative_to(destination) != expected:
            raise ValueError(f"Unexpected Maven repository path: {pom}")
        files = [("pom", "")]
        if packaging == "jar":
            files += [("jar", ""), ("jar", "sources"), ("jar", "javadoc")]
        for extension, classifier in list(files):
            files.append((extension + ".asc", classifier))
        payloads = []
        for extension, classifier in files:
            suffix = f"-{classifier}" if classifier else ""
            file = pom.parent / f"{prefix}{suffix}.{extension}"
            if not file.is_file():
                raise ValueError(f"Missing signed release artifact: {file.name}")
            payloads.append((file, extension, classifier))
        artifacts.append((packaging, artifact, pom, payloads))
    return sorted(artifacts, key=lambda artifact: (artifact[0] != "pom", artifact[1]))


def existing_bytes(repository, relative):
    request = Request(repository.rstrip("/") + "/" + relative.as_posix())
    if repository.startswith("https://"):
        credentials = f"{os.environ['GITHUB_ACTOR']}:{os.environ['GITHUB_TOKEN']}"
        encoded = base64.b64encode(credentials.encode()).decode()
        request.add_header("Authorization", "Basic " + encoded)
    try:
        with urlopen(request, timeout=60) as response:
            return response.read()
    except HTTPError as error:
        if error.code == 404:
            return None
        raise
    except OSError:
        if repository.startswith("file://"):
            # urlopen wraps FileNotFoundError in URLError for a file repository.
            from urllib.parse import unquote, urlparse
            file = Path(unquote(urlparse(request.full_url).path))
            if not file.exists():
                return None
        raise


def mirror(bundle, group, version, repository, assets):
    with tempfile.TemporaryDirectory(prefix="github-maven-") as temporary:
        root = Path(temporary)
        extracted = root / "bundle"
        artifacts = unpack(bundle, extracted, group, version)
        assets.mkdir(parents=True, exist_ok=True)
        pending = []
        # Check the whole bundle before writing anything to the registry.
        for _, artifact, pom, payloads in artifacts:
            for file, extension, classifier in payloads:
                remote = existing_bytes(repository, file.relative_to(extracted))
                if remote is not None and remote != file.read_bytes():
                    raise ValueError(f"Existing GitHub artifact differs: {file.name}")
                shutil.copyfile(file, assets / file.name)
                if remote is None:
                    pending.append((artifact, pom, file, extension, classifier))
        if not pending:
            print("All GitHub Maven artifacts already match the Central bundle.")
            return
        # One Maven invocation; one deploy-file execution per missing payload.
        # Multi-part extensions such as pom.asc must be set explicitly.
        project = ET.Element("project", xmlns=NS["m"])
        for key, value in [("modelVersion", "4.0.0"), ("groupId", "release.tools"),
                           ("artifactId", "github-package-mirror"), ("version", "1"),
                           ("packaging", "pom")]:
            ET.SubElement(project, key).text = value
        plugins = ET.SubElement(ET.SubElement(project, "build"), "plugins")
        plugin = ET.SubElement(plugins, "plugin")
        for key, value in [("groupId", "org.apache.maven.plugins"),
                           ("artifactId", "maven-deploy-plugin"), ("version", "3.2.0")]:
            ET.SubElement(plugin, key).text = value
        executions = ET.SubElement(plugin, "executions")
        for index, (artifact, pom, file, extension, classifier) in enumerate(pending):
            execution = ET.SubElement(executions, "execution")
            ET.SubElement(execution, "id").text = f"payload-{index}"
            ET.SubElement(execution, "phase").text = "package"
            ET.SubElement(ET.SubElement(execution, "goals"), "goal").text = "deploy-file"
            config = ET.SubElement(execution, "configuration")
            values = {"repositoryId": "github", "url": repository,
                      "groupId": group, "artifactId": artifact, "version": version,
                      "file": str(file), "pomFile": str(pom),
                      # Suppress automatic POM attachment: the signed POM is a
                      # separate payload, and may already have been published.
                      "packaging": "pom", "extension": extension,
                      "classifier": classifier, "generatePom": "false",
                      "retryFailedDeploymentCount": "3"}
            for key, value in values.items():
                ET.SubElement(config, key).text = value
        plan = root / "pom.xml"
        ET.ElementTree(project).write(plan, encoding="utf-8", xml_declaration=True)
        subprocess.run(["mvn", "-B", "-ntp", "-f", str(plan), "package"],
                       cwd=root, check=True)
        for _, _, _, payloads in artifacts:
            for file, _, _ in payloads:
                if existing_bytes(repository, file.relative_to(extracted)) != file.read_bytes():
                    raise ValueError(f"GitHub artifact failed verification: {file.name}")
        print(f"Published and verified {len(pending)} GitHub Maven payloads.")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--bundle", required=True, type=Path)
    parser.add_argument("--group", required=True)
    parser.add_argument("--version", required=True)
    parser.add_argument("--repository", required=True)
    parser.add_argument("--assets", required=True, type=Path)
    args = parser.parse_args()
    mirror(args.bundle, args.group, args.version, args.repository, args.assets)
