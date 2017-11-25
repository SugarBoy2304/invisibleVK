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
import myvk.view.controllers.CopyControllers;

import java.io.IOException;

public class CopyScreen implements IScreen {

    private static CopyControllers controller;

    private ObservableList<MyItem> items;

    public CopyScreen(ObservableList<MyItem> items) {
        this.items = items;
    }

    @Override
    public CopyScreen init(IScreenManager manager) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/fxml/ScreenView.fxml"));
            Parent p = loader.load();

            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(p);
            Stage stage = new Stage();
            stage.setTitle("VK Pull Up");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            controller = loader.getController();
            controller.setField(this, items);
            controller.setStage(stage);
            return this;

        } catch (IOException ex) {
            MyException.generate().setException(ex).setMsg("Ошибка загрузки CopyScreen:").setLevelError(Alert.AlertType.ERROR).setError("Подробнее:").show(true);
        }
        return null;
    }

    public void setSettings(String priceController, boolean changePrice, boolean myDescription, String schemDescription, int groupId, int almubId) {
        controller.setSettings(priceController, changePrice, myDescription, schemDescription);
        controller.setGroup(groupId, almubId);
    }

    @Override
    public void close() {

    }


}
