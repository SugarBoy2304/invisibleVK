package myvk.screens.logic;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import myvk.screens.logic.IScreenManager;
import myvk.screens.logic.IScreen;

public class ScreenManager implements IScreenManager {

    Stage stage;

    @Override
    public void set(IScreen screen) {
        for (IScreen s : scene) s.close();
        scene.clear();
        scene.add(screen);
    }

    @Override
    public void del() {
        scene.remove(0).close();
    }

    @Override
    public void add(IScreen screen) {
        scene.add(screen);
    }

    @Override
    public Stage initStage(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("VK Pull Up");
        this.stage.setWidth(1280);
        this.stage.setHeight(720);
        return this.stage;
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public IScreen getScreen() {
        return scene.get(scene.size());
    }

    @Override
    public IScreen getScreen(int index) {
        return scene.get(index);
    }

    @Override
    public FXMLLoader getLoader() {
        return loader;
    }

}
