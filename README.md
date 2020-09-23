# KGDX
Helper library for libGDX to not reinvent the wheel every f***ing time

## The KGDX Class
```java
KGDX.app              // Lwjgl3Application
KGDX.graphics         // Lwjgl3Graphics
KGDX.audio            // Audio
KGDX.input            // Lwjgl3Input
KGDX.files            // Lwjgl3Files
KGDX.net              // Lwjgl3Net
KGDX.clipboard        // Lwjgl3Clipboard
KGDX.preferences      // Lwjgl3Preferences (app.prefs)

KGDX.gl               // GL20
KGDX.gl20             // GL20
KGDX.gl30             // GL30

KGDX.main             // KGDXApplication (main application extending KGDXApplication class)
KGDX.logger           // KGDXLogger
KGDX.inputMultiplexer // InputMultiplexer (global input multiplexer)
```
## Clearing screens
```java
/**
  default-color: 0, 0, 0, 1
  default-mask: GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
  
  if coverage sampling is active GL_COVERAGE_BUFFER_BIT_NV is also added by default
**/

KGDX.setGlClearColor(float r, float g, float b, float a)
KGDX.setGlClearMask(int mask)
KGDX.addGlClearMask(int mask)

KGDX.glClear() // Needs to be called manually in render() or fixedRender() method
```
## Switching between screens
```java
KGDX.switchScreen(Class<? extends KGDXScreen> screenClass, boolean loadFromCache)
KGDX.switchScreen(KGDXScreen newScreen, boolean saveToCache)
```
## Initialise KGDX
```java
public void MyMainApplication extends KGDXApplication {
  
  public MyMainApplication() {
    super("My cool application");
  }
  
  public void onCreate() {
    // If screen class has no constructor parameters
    KGDX.switchScreen(MyEntryScreen.class, false);
    // or else
    KGDX.switchScreen(new MyEntryScreen("parameter"), true);
  }
}
```
## IDEAS
- KGDXServer / KGDXClient?
