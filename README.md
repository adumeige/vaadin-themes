# Vaadin Themes

A small collection of packaged Vaadin 24 Lumo-based themes, each published as a Maven module and previewable in the included test app.

## Themes

### Seagod

![Seagod theme screenshot](resources/seagod-00.png)

### Fjord

![Fjord theme screenshot 1](resources/fjord-00.png)
![Fjord theme screenshot 2](resources/fjord-01.png)

### Terminal Synth

![Terminal Synth theme screenshot 1](resources/terminal-synth-00.png)
![Terminal Synth theme screenshot 2](resources/terminal-synth-01.png)

### Novelist

![Novelist theme screenshot 1](resources/novelist-00.png)
![Novelist theme screenshot 2](resources/novelist-01.png)

## Use a theme

Add the theme module as a dependency:

```xml
<dependency>
  <groupId>org.antoined</groupId>
  <artifactId>theme-fjord</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Then select it in your Vaadin app:

```java
@Theme("fjord")
public class AppShell implements AppShellConfigurator {
}
```

Available theme names: `seagod`, `fjord`, `terminal-synth`, `novelist`.

## Build

```bash
mvn install
```

Run the preview app:

```bash
mvn spring-boot:run -pl test-app
```
