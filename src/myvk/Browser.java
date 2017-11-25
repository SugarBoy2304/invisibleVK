package myvk;

import com.vk.api.sdk.exceptions.ApiAuthException;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import myvk.screens.LoginScreen;
import myvk.utils.Config;


public class Browser extends Region {

    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();

    public Browser(Main pullUp) {
        this(pullUp, Move.DEFAULT);
    }

    public Browser(Main pullUp, Move move) {
        //apply the styles
        getStyleClass().add("browser");
        // load the web page
        webEngine.load(move.url);
        // add the web view to the scene
        getChildren().add(browser);

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.SUCCEEDED.equals(newValue) && webEngine.getLocation().contains("https://oauth.vk.com/blank.html#access_token=")) {
                Config.a().setToken(webEngine.getLocation());
                Config.a().save();
                LoginScreen.closeBrowser();

                try {
                    Main.getApp().initUser();
                } catch (ApiAuthException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0, w, h,0, HPos.CENTER, VPos.CENTER);
    }

    @Override protected double computePrefWidth(double height) {
        return 750;
    }

    @Override protected double computePrefHeight(double width) {
        return 500;
    }

    public static enum Move {

        DEFAULT("https://google.com"),
        LOGIN("https://oauth.vk.com/oauth/authorize?client_id=6136168&display=popup&scope=notify,friends,photos,audio,video,pages,status,notes,messages,wall,ads,offline,docs,groups,notifications,stats,email,market&redirect_uri=https://oauth.vk.com/blank.html&response_type=token&v=5.69"),
        REGISTER("https://m.vk.com/join");

        private String url;

        Move(String url) {
            this.url = url;
        }

    }

}
