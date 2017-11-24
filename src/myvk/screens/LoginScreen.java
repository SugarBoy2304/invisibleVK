package myvk.screens;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import myvk.Browser;
import myvk.PullUp;
import myvk.screens.logic.IScreen;
import myvk.screens.logic.IScreenManager;

public class LoginScreen implements IScreen {

    private static Stage browserView;

    @Override
    public IScreen init(IScreenManager manager) {
        if (browserView != null) {
            browserView.close();
        }

        // Отображаем сцену, содержащую корневой макет.
        Scene scene = new Scene(new Browser(PullUp.getApp(), Browser.Move.LOGIN), 750, 500, Color.web("#666970"));

        browserView = new Stage();
        browserView.setTitle("Авторизация");
        browserView.initModality(Modality.WINDOW_MODAL);
        browserView.initOwner(manager.getStage());
        browserView.setResizable(false);
        browserView.setScene(scene);

        // Отображаем диалоговое окно и ждём, пока пользователь его не закроет
        browserView.showAndWait();

        return this;
    }

    @Override
    public void close() {
        browserView.close();
    }

    public static void closeBrowser() {
        browserView.close();
    }
}
