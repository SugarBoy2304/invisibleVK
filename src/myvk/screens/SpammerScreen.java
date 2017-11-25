package myvk.screens;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import myvk.Main;
import myvk.screens.logic.IScreen;
import myvk.screens.logic.IScreenManager;
import myvk.utils.MyItem;
import myvk.utils.exception.MyException;
import myvk.view.controllers.SpammerControllers;

import java.io.IOException;

public class SpammerScreen implements IScreen {

    private static SpammerControllers controller;

    private ObservableList<MyItem> items;

    @Override
    public SpammerScreen init(IScreenManager manager) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/fxml/SpammerView.fxml"));
            Parent p = loader.load();

            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(p);
            Stage stage = new Stage();
            stage.setTitle("VK Pull Up");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            controller = loader.getController();
            return this;

        } catch (IOException ex) {
            MyException.generate().setException(ex).setMsg("Ошибка загрузки CopyScreen:").setLevelError(Alert.AlertType.ERROR).setError("Подробнее:").show(true);
        }
        return null;
    }
    @Override
    public void close() {

    }


}
