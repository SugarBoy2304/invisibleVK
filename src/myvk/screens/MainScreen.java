package myvk.screens;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import myvk.PullUp;
import myvk.screens.logic.IScreen;
import myvk.screens.logic.IScreenManager;
import myvk.utils.exception.MyException;
import myvk.view.controllers.MainControllers;

import java.io.IOException;

public class MainScreen implements IScreen {

    private static MainControllers controller;

    public IScreen init(IScreenManager manager) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(PullUp.class.getResource("view/fxml/MainView.fxml"));
            Parent p = loader.load();

            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(p);
            manager.getStage().setScene(scene);
            manager.getStage().setResizable(false);
            manager.getStage().show();

            controller = loader.getController();
            return this;
        } catch (IOException ex) {
            MyException.generate().setException(ex).setMsg("Ошибка загрузки MainScreen:").setLevelError(Alert.AlertType.ERROR).setError("Подробнее:").show(true);
        }
        return null;
    }

    public static MainControllers get() {
        return controller;
    }

    public void close() {

    }
}
