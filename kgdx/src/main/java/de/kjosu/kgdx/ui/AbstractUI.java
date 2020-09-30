package de.kjosu.kgdx.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.kjosu.kgdx.KGDX;

public abstract class AbstractUI {

	private final Stage stage;
	private boolean centerCamera = true;

	public AbstractUI() {
		stage = new Stage(new ScreenViewport());
	}

	public void show() {
		KGDX.inputMultiplexer.addProcessor(stage);
	}

	public void hide() {
		KGDX.inputMultiplexer.removeProcessor(stage);
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, centerCamera);
	}

	public void render(float delta) {
		stage.act(delta);
		stage.draw();
	}

	public boolean isCenterCamera() {
		return centerCamera;
	}

	public void setCenterCamera(boolean centerCamera) {
		this.centerCamera = centerCamera;
	}

	public Stage getStage() {
		return stage;
	}

	public void dispose() {
		KGDX.inputMultiplexer.removeProcessor(stage);
		stage.dispose();
	}
}
