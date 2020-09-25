package de.kjosu.kgdx;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;

import java.util.HashMap;
import java.util.Map;

public class KGDX {

	/**
	 * Map containing all cached screens mapped to its class.<br/>
	 * Used by {@link KGDX#switchScreen(KGDXScreen, boolean)} and {@link KGDX#switchScreen(Class, boolean)}.
	 */
	private static final Map<Class<? extends KGDXScreen>, KGDXScreen> screens = new HashMap<>();

	/**
	 * Instance of the currently active/rendered screen.
	 */
	static KGDXScreen activeScreen;

	/**
	 * Reference to the current {@link Lwjgl3Application} instance.
	 * @see Gdx#app
	 */
	public static Lwjgl3Application app;
	/**
	 * Reference to the applications {@link Lwjgl3Graphics} instance.
	 * @see Gdx#graphics
	 */
	public static Lwjgl3Graphics graphics;
	/**
	 * Reference to the applications {@link Audio} instance.
	 * @see Gdx#audio
	 */
	public static Audio audio;
	/**
	 * Reference to the applications {@link Lwjgl3Input} instance.
	 * @see Gdx#input
	 */
	public static Lwjgl3Input input;
	/**
	 * Reference to the applications {@link Lwjgl3Files} instance.
	 * @see Gdx#files
	 */
	public static Lwjgl3Files files;
	/**
	 * Reference to the applications {@link Lwjgl3Net} instance.
	 * @see Gdx#net
	 */
	public static Lwjgl3Net net;
	/**
	 * Reference to the applications {@link Lwjgl3Clipboard} instance.
	 */
	public static Lwjgl3Clipboard clipboard;
	/**
	 * Just a global {@link Lwjgl3Preferences} instance.<br/>
	 * <br/>
	 * Use to your heart's desire.
	 */
	public static Lwjgl3Preferences preferences;

	/**
	 * @see Gdx#gl
	 */
	public static GL20 gl;
	/**
	 * @see Gdx#gl20
	 */
	public static GL20 gl20;
	/**
	 * @see Gdx#gl30
	 */
	public static GL30 gl30;

	/**
	 * Reference to the main {@link KGDXApplication} instance.
	 */
	public static KGDXApplication main;
	/**
	 * Just a global {@link KGDXLogger} instance.<br/>
	 * Also used by the {@link Lwjgl3Application}'s log methods.
	 *
	 * @see Lwjgl3Application#setApplicationLogger(ApplicationLogger)
	 * @see Lwjgl3Application#log(String, String, Throwable)
	 * @see Lwjgl3Application#error(String, String, Throwable)
	 * @see Lwjgl3Application#debug(String, String, Throwable)
	 */
	public static KGDXLogger logger;
	/**
	 * Just a global {@link InputMultiplexer} instance.<br/>
	 * When switching screens, the old screen is automatically replaced with the new screen as input processor.<br/>
	 * <br/>
	 * Use to your heart's desire.
	 */
	public static InputMultiplexer inputMultiplexer;
	/**
	 * Just a global {@link AssetManager} instance.<br/>
	 * <br/>
	 * Use to your heart's desire.
	 */
	public static AssetManager assets;

	/**
	 * Color variables used for screen clearing.
	 *
	 * @see GL20#glClearColor(float, float, float, float)
	 * @see GL20#glClear(int)
	 */
	private static float glClearRed, glClearGreen, glClearBlue, glClearAlpha;
	/**
	 * Mask used for screen clearing.
	 *
	 * @see GL20#glClear(int)
	 */
	private static int glClearMask;

	private KGDX() {

	}

	static void init(KGDXApplication main) {
		KGDX.main = main;

		app = (Lwjgl3Application) Gdx.app;
		graphics = (Lwjgl3Graphics) Gdx.graphics;
		audio = Gdx.audio;
		input = (Lwjgl3Input) Gdx.input;
		files = (Lwjgl3Files) Gdx.files;
		net = (Lwjgl3Net) Gdx.net;
		clipboard = (Lwjgl3Clipboard) app.getClipboard();
		preferences = (Lwjgl3Preferences) app.getPreferences("app");

		gl = Gdx.gl;
		gl20 = Gdx.gl20;
		gl30 = Gdx.gl30;

		app.setApplicationLogger(logger = new KGDXLogger());
		app.setLogLevel(Lwjgl3Application.LOG_DEBUG);
		input.setInputProcessor(inputMultiplexer = new InputMultiplexer());
		assets = new AssetManager();

		setGlClearColor(0f, 0f, 0f, 1f);
		setGlClearMask(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		if (graphics.getBufferFormat().coverageSampling) {
			addGlClearMask(GL20.GL_COVERAGE_BUFFER_BIT_NV);
		}
	}

	/**
	 * Creates a new instance or loads a cached version of the given screens class.<br/>
	 * If no cached instance was found a new one will be created.<br/>
	 * Whenever a new screen instance is created by this method it will automatically overwrite the currently cached screen with the same class.<br/>
	 * The old screen will be disposed.<br/>
	 *
	 * @exception
	 *
	 * @param screenClass class of the screen that should be changed to
	 * @param loadFromCache if true load a screen from cache. if false or no screen is cached, create a new instance
	 */
	public static void switchScreen(Class<? extends KGDXScreen> screenClass, boolean loadFromCache, boolean saveToCache) {
		if (screenClass == null) {
			throw new IllegalArgumentException("Screen class can't be null");
		}

		KGDXScreen nextScreen;
		if (!loadFromCache || !screens.containsKey(screenClass)) {
			try {
				nextScreen = screenClass.getConstructor().newInstance();

				if (saveToCache) {
					cacheScreen(nextScreen);
				}
			} catch (ReflectiveOperationException e) {
				logger.error(KGDX.class.getSimpleName(), String.format("Failed creating screen: %s", screenClass), e);
				return;
			}
		} else {
			nextScreen = screens.get(screenClass);
		}

		handleScreenSwitch(nextScreen);
	}

	/**
	 * @param screen the screen that should be changed to
	 * @param saveToCache if true the screen instance will be saved to the cache. if false not
	 */
	public static void switchScreen(KGDXScreen screen, boolean saveToCache) {
		if (screen == null) {
			throw new IllegalArgumentException("Screen instance can't be null");
		}

		if (saveToCache) {
			cacheScreen(screen);
		}

		handleScreenSwitch(screen);
	}

	private static void cacheScreen(KGDXScreen screen) {
		KGDXScreen lastScreen = screens.put(screen.getClass(), screen);

		if (lastScreen != null) {
			lastScreen.dispose();
		}
	}

	private static void handleScreenSwitch(KGDXScreen nextScreen) {
		if (activeScreen == nextScreen) {
			return;
		}

		if (activeScreen != null) {
			inputMultiplexer.removeProcessor(activeScreen);
			activeScreen.hide();

			if (!screens.containsKey(activeScreen.getClass())) {
				activeScreen.dispose();
			}
		}

		activeScreen = nextScreen;
		activeScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		activeScreen.show();
		inputMultiplexer.addProcessor(activeScreen);
	}

	public static KGDXScreen getActiveScreen() {
		return activeScreen;
	}

	public static void glClear() {
		gl.glClearColor(glClearRed, glClearGreen, glClearBlue, glClearAlpha);
		gl.glClear(glClearMask);
	}

	public static void setGlClearColor(float r, float g, float b, float a) {
		glClearRed = r;
		glClearGreen = g;
		glClearBlue = b;
		glClearAlpha = a;
	}

	public static void setGlClearMask(int mask) {
		glClearMask = mask;
	}

	public static void addGlClearMask(int mask) {
		glClearMask |= mask;
	}

	static void dispose() {
		for (KGDXScreen screen : screens.values()) {
			screen.dispose();
		}

		screens.clear();
		activeScreen = null;
		assets.dispose();
	}
}
