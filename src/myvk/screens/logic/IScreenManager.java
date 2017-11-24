package myvk.screens.logic;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.util.ArrayList;

public interface IScreenManager {

    FXMLLoader loader = new FXMLLoader();
    ArrayList<IScreen> scene = new ArrayList<>();

    /**
     * @param screen new screen
     * set firstly new screen**/
    void set(IScreen screen);

    /**remove last screen*/
    void del();

    /**
     * @param screen new screen
     * add firstly new screen**/
    void add(IScreen screen);

    /** init Stage **/
    Stage initStage(Stage stage);

    /** Get Stage **/
    Stage getStage();

    /** Get firstly screen **/
    IScreen getScreen();

    /** Get screen index **/
    IScreen getScreen(int index);

    FXMLLoader getLoader();

}
