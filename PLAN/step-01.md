# Migration Step 01: vaadin-spring-boot-starter 24.10.3 migration requirements

## Scope

- Plan: adumeige/vaadin-themes · vaadin-spring-boot-starter 24.10.1->24.10.3 · 2026-09-23T23:43
- Step: 1 of 1
- Component: vaadin-spring-boot-starter
- Version: 24.10.1 -> 24.10.3
- Category: DEPENDENCY_CHANGED
- Kind: framework

## Objective

Update the repository so the codebase is compatible with vaadin-spring-boot-starter 24.10.3 for this migration step. Keep the change focused on the requirements listed below and avoid unrelated refactors.

## Implementation Plan

1. Inspect the repository for code, build files, configuration, tests, and documentation that reference the surfaces below.
2. Apply the required source, dependency, configuration, or test changes for this step.
3. Run the narrowest relevant verification available in the repository, then broaden only if the change touches shared behavior.
4. Leave only the files required for this step changed; the execution workflow owns the Git commit.

## Required Changes

- [24.10.3] DEPENDENCY_CHANGED: spring-boot-starter-web version updated to 3.5.14. The vaadin-spring-boot-starter 24.10.3 POM declares a dependency on org.springframework.boot:spring-boot-starter-web version 3.5.14. Verify your application is compatible with this Spring Boot version; if you override the Spring Boot version, check that it still works with this Vaadin release.
  - Search hint: dependency: org.springframework.boot:spring-boot-starter-web
  - Search hint: scope: compile
  - Evidence: https://repo1.maven.org/maven2/com/vaadin/vaadin-spring-boot-starter/24.10.3/vaadin-spring-boot-starter-24.10.3.pom

## Repository Search Hints

- covers 1 version
- 1 requirement
- dependency: org.springframework.boot:spring-boot-starter-web
- scope: compile

## Primary Evidence

- https://repo1.maven.org/maven2/com/vaadin/vaadin-spring-boot-starter/24.10.3/vaadin-spring-boot-starter-24.10.3.pom

## Done When

- The repository no longer contains code or configuration that violates the required changes for this step.
- Relevant tests or build checks pass, or any remaining failure is documented with the next concrete action.
- The change is committed as one focused migration step.
