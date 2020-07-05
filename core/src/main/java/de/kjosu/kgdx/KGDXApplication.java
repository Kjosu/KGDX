package de.kjosu.kgdx;

import com.badlogic.gdx.ApplicationListener;

/**
 *
 * When overloading Methods of {@link ApplicationListener}, make sure to call the {@link KGDXApplication} super method.
 *
 */
public abstract class KGDXApplication implements ApplicationListener {

	private float maxRenderDelta;

	@Override
	public final void create() {
		KGDX.init();

		setMaxRenderDelta(0.25f);

		onCreate();
	}

	public abstract void onCreate();

	@Override
	public void resize(int width, int height) {
		KGDX.activeScreen.resize(width, height);
	}

	@Override
	public void render() {
		KGDX.glClear();

		float delta = KGDX.graphics.getRawDeltaTime();

		if (maxRenderDelta > 0) {
			delta = Math.min(maxRenderDelta, delta);
		}

		KGDX.activeScreen.render(delta);
	}

	@Override
	public void pause() {
		KGDX.activeScreen.pause();
	}

	@Override
	public void resume() {
		KGDX.activeScreen.resume();
	}

	@Override
	public void dispose() {
		KGDX.dispose();
	}

	public void setMaxRenderDelta(float maxRenderDelta) {
		this.maxRenderDelta = maxRenderDelta;
	}

	public float getMaxRenderDelta() {
		return maxRenderDelta;
	}

}
