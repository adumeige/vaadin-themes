# Migration Step 01: vaadin-core 24.10.3 migration requirements

## Scope

- Plan: adumeige/vaadin-themes · vaadin-core 24.10.1->24.10.3 · 2026-09-24T07:31
- Step: 1 of 1
- Component: vaadin-core
- Version: 24.10.1 -> 24.10.3
- Category: PLATFORM_REQUIREMENT
- Kind: spring

## Objective

Update the repository so the codebase is compatible with vaadin-core 24.10.3 for this migration step. Keep the change focused on the requirements listed below and avoid unrelated refactors.

## Implementation Plan

1. Inspect the repository for code, build files, configuration, tests, and documentation that reference the surfaces below.
2. Apply the required source, dependency, configuration, or test changes for this step.
3. Run the narrowest relevant verification available in the repository, then broaden only if the change touches shared behavior.
4. Leave only the files required for this step changed; the execution workflow owns the Git commit.

## Required Changes

- [24.10.3] PLATFORM_REQUIREMENT: dependencyversion-maven-plugin 1.0.1 (io.reformanda.semper) used in vaadin-core build. If you fork or mirror the vaadin-core build, grep for `dependencyversion-maven-plugin` and `io.reformanda.semper`. Version 1.0.1 is bound to the `set-version` goal with execution id `set-all` in 24.10.3; any publisher pipeline that rewrites versions must account for this plugin execution or it will fail on the version filter.
  - Search hint: plugin: io.reformanda.semper:dependencyversion-maven-plugin:
  - Search hint: execution: set-all
  - Evidence: https://repo1.maven.org/maven2/com/vaadin/vaadin-core/24.10.3/vaadin-core-24.10.3.pom
- [24.10.3] PLATFORM_REQUIREMENT: karaf-maven-plugin excludes flow-server, flow-data, flow-client from vaadin-core bundle. If you build OSGi bundles from vaadin-core, grep your Karaf feature/bundle build for `excludedArtifactId` entries matching flow-server, flow-data, flow-lit-template, flow-polymer-template, flow-push, flow-client, flow-html-components, flow-dnd, slf4j-api, or vaadin-grid-flow. In 24.10.3 these are excluded from the vaadin-core Karaf plugin configuration, so any feature referencing them via vaadin-core will need them declared separately.
  - Search hint: plugin: org.apache.karaf.tooling:karaf-maven-plugin
  - Search hint: scope: OSGi
  - Evidence: https://repo1.maven.org/maven2/com/vaadin/vaadin-core/24.10.3/vaadin-core-24.10.3.pom
- [24.10.3] DEPENDENCY_CHANGED: vaadin-core 24.10.3 declares compile deps vaadin-core-internal and vaadin-dev. Grep your POM for `com.vaadin:vaadin-core` and pin the managed version to `24.10.3`. This release pulls `vaadin-core-internal:24.10.3` and `vaadin-dev:24.10.3` at compile scope; if you previously excluded `vaadin-dev` for production packaging, that exclusion must remain because Vaadin now ships it as a direct compile dependency of vaadin-core.
  - Search hint: package: com.vaadin:vaadin-core
  - Search hint: dep: vaadin-core-internal:24.10.3
  - Search hint: dep: vaadin-dev:24.10.3
  - Evidence: https://repo1.maven.org/maven2/com/vaadin/vaadin-core/24.10.3/vaadin-core-24.10.3.pom
- [24.10.3] DEPENDENCY_CHANGED: vaadin-core 24.10.3 parents to vaadin-platform-parent 24.10.3. Grep for `vaadin-platform-parent` in your POM hierarchy. The 24.10.3 vaadin-core inherits from `com.vaadin:vaadin-platform-parent:24.10.3`; any locally declared parent version mismatched with vaadin-core (e.g. still 24.9.x) will cause dependencyManagement entries to diverge. Align both to 24.10.3.
  - Search hint: dep: com.vaadin:vaadin-platform-parent:24.10.3
  - Evidence: https://repo1.maven.org/maven2/com/vaadin/vaadin-core/24.10.3/vaadin-core-24.10.3.pom

## Repository Search Hints

- covers 1 version
- 4 requirements
- package: com.vaadin:vaadin-core
- dep: vaadin-core-internal:24.10.3

## Primary Evidence

- https://repo1.maven.org/maven2/com/vaadin/vaadin-core/24.10.3/vaadin-core-24.10.3.pom

## Done When

- The repository no longer contains code or configuration that violates the required changes for this step.
- Relevant tests or build checks pass, or any remaining failure is documented with the next concrete action.
- The change is committed as one focused migration step.
