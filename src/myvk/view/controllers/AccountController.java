package myvk.view.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import myvk.PullUp;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountController implements Initializable {

    private PullUp pullUp;

    @FXML
    private GridPane grid;

    public AccountController() {

    }

    @FXML
    private GridPane getGrid() {
        return this.grid;
    }

    public void setPullUp(PullUp pullUp) {
        this.pullUp = pullUp;

        // Добавление в таблицу данных из наблюдаемого списка
        //listView.setItems(pullUp.accounts);

        //grid.addRow(0, new Label("sss"));
        //grid.addRow(1, new Label("aaa"));

        AnchorPane anchor = new AnchorPane();

        anchor.setPrefHeight(128);
        anchor.setPrefWidth(400);

        anchor.getChildren().addAll(createList("Никита Сельский", "Вы: Ты петух", "19:47"));

        grid.addRow(0, anchor);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //listView.setItems(pullUp.accounts);
    }

    public Node[] createList(String author, String msg, String time) {

        Node[] nodes = new Node[4];

        nodes[0] = (newLabel(author, 75, 20));
        nodes[1] = (newLabel(msg, 75, 55));
        nodes[2] = (newLabel(time, 350, 20));
        Rectangle rec = new Rectangle(10, 20, 60, 60);
        nodes[3] = rec;

        return nodes;
    }

    public Label newLabel(String text, double x, double y) {
        Label l = new Label(text);
        l.setLayoutX(x);
        l.setLayoutY(y);
        return l;
    }

}
