package de.kjosu.kgdx;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.HashMap;
import java.util.Map;

public class KGDX {

	private static final Map<Class<? extends KGDXScreen>, KGDXScreen> screens = new HashMap<>();
	static KGDXScreen activeScreen;

	public static Lwjgl3Application app;
	public static Lwjgl3Graphics graphics;
	public static Audio audio;
	public static Lwjgl3Input input;
	public static Lwjgl3Files files;
	public static Lwjgl3Net net;
	public static Lwjgl3Clipboard clipboard;

	public static GL20 gl;
	public static GL20 gl20;
	public static GL30 gl30;

	public static KGDXApplication main;
	public static InputMultiplexer inputMultiplexer;

	private static float glClearRed, glClearGreen, glClearBlue, glClearAlpha;
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

		gl = Gdx.gl;
		gl20 = Gdx.gl20;
		gl30 = Gdx.gl30;

		inputMultiplexer = new InputMultiplexer();

		setGlClearColor(0f, 0f, 0f, 1f);
		setGlClearMask(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		if (graphics.getBufferFormat().coverageSampling) {
			addGlClearMask(GL20.GL_COVERAGE_BUFFER_BIT_NV);
		}
	}

	public static void switchScreen(Class<? extends KGDXScreen> screenClass, boolean loadFromCache) {
		if (screenClass == null) {
			throw new IllegalArgumentException("Screen class can't be null");
		}

		KGDXScreen nextScreen;
		if (!loadFromCache || !screens.containsKey(screenClass)) {
			try {
				nextScreen = screenClass.getConstructor().newInstance();
				screens.put(screenClass, nextScreen);
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(String.format("Failed creating screen: %s", screenClass), e);
			}
		} else {
			nextScreen = screens.get(screenClass);
		}

		if (activeScreen != null) {
			inputMultiplexer.removeProcessor(activeScreen);
			activeScreen.hide();
		}

		activeScreen = nextScreen;
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

	public static KGDX setGlClearMask(int mask) {
		glClearMask = mask;
		return null;
	}

	public static KGDX addGlClearMask(int mask) {
		glClearMask |= mask;
		return null;
	}

	static void dispose() {
		for (KGDXScreen screen : screens.values()) {
			screen.dispose();
		}

		screens.clear();
		activeScreen = null;
	}
}
