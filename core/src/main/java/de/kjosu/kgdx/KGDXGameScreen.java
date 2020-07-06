package de.kjosu.kgdx;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class KGDXGameScreen extends KGDXScreen {

	private int maxUpdatesPerSecond;
	private int maxFramesPerSecond;

	private float updateTimeStep;
	private float updateAccumulator;

	private float frameTimeStep;
	private float frameAccumulator;

	private float accumulator;
	private int updatesPerSecond;
	private int framesPerSecond;
	private int currentUpdate;
	private int currentFrame;

	private final SpriteBatch batch = new SpriteBatch();
	private final BitmapFont font = new BitmapFont();

	public KGDXGameScreen() {
		setMaxUpdatesPerSecond(20);
		setMaxFramesPerSecond(60);
	}

	@Override
	public final void render(float delta) {
		// Update
		if (maxUpdatesPerSecond == 0) {
			fixedUpdate(delta);
			currentUpdate++;
		} else {
			updateAccumulator += delta;

			while (updateAccumulator >= updateTimeStep) {
				fixedUpdate(updateTimeStep);
				currentUpdate++;
				updateAccumulator -= updateTimeStep;
			}
		}

		// Render
		if (maxFramesPerSecond == 0) {
			fixedRender();
			currentFrame++;
		} else {
			frameAccumulator += delta;

			if (frameAccumulator >= frameTimeStep) {
				fixedRender();
				currentFrame++;
				frameAccumulator %= frameTimeStep;
			}
		}

		accumulator += delta;

		if (accumulator >= 1f) {
			updatesPerSecond = currentUpdate;
			framesPerSecond = currentFrame;

			currentUpdate = 0;
			currentFrame = 0;

			accumulator -= 1f;
		}
	}

	public abstract void fixedUpdate(float timeStep);

	public abstract void fixedRender();

	public void drawDebugInfo() {
		batch.begin();
		font.draw(batch, String.format("UPS: %s\r\nFPS: %s", updatesPerSecond, framesPerSecond), 10, KGDX.graphics.getHeight() - 10);
		batch.end();
	}

	public void setMaxUpdatesPerSecond(int maxUpdatesPerSecond) {
		if (maxUpdatesPerSecond > 0) {
			this.maxUpdatesPerSecond = maxUpdatesPerSecond;
			this.updateTimeStep = 1f / maxUpdatesPerSecond;
		} else {
			this.maxUpdatesPerSecond = 0;
			this.updateTimeStep = 0;
		}

		this.updateAccumulator = 0;
	}

	public int getMaxUpdatesPerSecond() {
		return maxUpdatesPerSecond;
	}

	public void setMaxFramesPerSecond(int maxFramesPerSecond) {
		if (maxFramesPerSecond > 0) {
			this.maxFramesPerSecond = maxFramesPerSecond;
			this.frameTimeStep = 1f / maxFramesPerSecond;
		} else {
			this.maxFramesPerSecond = 0;
			this.frameTimeStep = 0;
		}

		this.frameAccumulator = 0;
	}

	public int getMaxFramesPerSecond() {
		return maxFramesPerSecond;
	}

	public int getFramesPerSecond() {
		return framesPerSecond;
	}

	public int getUpdatesPerSecond() {
		return updatesPerSecond;
	}
}
