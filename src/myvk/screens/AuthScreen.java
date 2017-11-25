package myvk.screens;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import myvk.Main;
import myvk.screens.logic.IScreen;
import myvk.screens.logic.IScreenManager;
import myvk.utils.exception.MyException;
import myvk.view.controllers.AuthController;

import java.io.IOException;

public class AuthScreen implements IScreen {

    public IScreen init(IScreenManager manager) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/fxml/AuthView.fxml"));
            Parent p = loader.load();

            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(p);
            manager.getStage().setScene(scene);
            manager.getStage().setResizable(false);
            manager.getStage().show();

            AuthController controller = loader.getController();
            controller.setPullUp(Main.getApp());
            return this;
        } catch (IOException ex) {
            MyException.generate().setException(ex).setMsg("Ошибка загрузки AuthScreen:").setLevelError(Alert.AlertType.ERROR).setError("Подробнее:").show(true);
        }
        return null;
    }

    public void close() {

    }
}
