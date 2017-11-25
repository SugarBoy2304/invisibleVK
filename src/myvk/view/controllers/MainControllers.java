package myvk.view.controllers;

import com.vk.api.sdk.objects.market.MarketItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import myvk.Main;
import myvk.auth.Utils;
import myvk.screens.AuthScreen;
import myvk.screens.CopyScreen;
import myvk.screens.SpammerScreen;
import myvk.utils.*;
import myvk.utils.exception.MyException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MainControllers implements Initializable {

    private boolean blockParse = false;
    private ObservableList<MyItem> items = FXCollections.observableArrayList();
    private static int loadId = -1;

    @FXML
    private TextField url;

    @FXML
    private Button startFind;

    @FXML
    private MenuItem exit_name;

    /** Информация о товаре **/

    @FXML
    private ImageView item_img;

    @FXML
    private HBox item_pics;

    @FXML
    private TextField item_name;

    @FXML
    private Label item_price;

    @FXML
    private TextArea item_description;

    /** Таблица **/

    @FXML
    private TableView<MyItem> tableItems;

    @FXML
    private TableColumn<MyItem, Integer> id_column;

    @FXML
    private TableColumn<MyItem, String> item_column;

    @FXML
    private TableColumn<MyItem, Integer> price_column;

    @FXML
    private TableColumn<MyItem, CheckBox> isAddItem_column;

    @FXML
    private CheckBox changePrice;

    @FXML
    private TextField priceController;

    @FXML
    private TextArea schemDescription;

    @FXML
    private CheckBox myDescription;

    @FXML
    private TextField groupId;

    @FXML
    private TextField almubId;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        url.setText(Config.a().getLastUrl());
        groupId.setText(Config.a().getLastClub());
        almubId.setText(Config.a().getLastAlmub());
    }

    public void setName(String name) {
        this.exit_name.setText(name);
    }

    @FXML
    public void startFind() throws InterruptedException {

        if (blockParse) {
            MyException.generate().setMsg("Ошибка загрузки альбома").setError("Процесс уже запущен").setLevelError(Alert.AlertType.ERROR).show(true);
            return;
        }

        if (url.getText().length() == 0) {
            MyException.generate().setMsg("Ошибка загрузки альбома").setError("Укажите ссылку").show(true);
            return;
        }

        int market;
        int albumId;

        try {

            market = Integer.valueOf(Utils.extractPattern(url.getText(), "market-(\\d*)?"));
            albumId = Integer.valueOf(Utils.extractPattern(url.getText(), "album_(\\d*)"));

        } catch (Exception ex) {
            MyException.generate().setMsg("Ошибка загрузки альбома").setError("Указана некорректная ссылка").setException(ex).show(true);
            return;
        }

        blockParse = true;

        ShopParser parser = new ShopParser(market);

        Object[] data = parser.getItems(albumId);

        List<MarketItem> array = (List<MarketItem>) data[0];
        Map<Integer, PhotoField> dataPhoto = (Map<Integer, PhotoField>) data[1];

        for (MarketItem i : array) items.add(new MyItem(i, dataPhoto.get(i.getId())));

        Thread.sleep(500L);
        blockParse = false;

        Config.a().setLastUrl(url.getText());
        Config.a().save();

        updateColumn();

        tableItems.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showItemInfo(newValue));

    }

    public void updateColumn() {
        tableItems.setItems(items);

        item_column.setCellValueFactory(cell -> cell.getValue().getTitle());
        isAddItem_column.setCellValueFactory(cell -> cell.getValue().getCheckBox());
        id_column.setCellValueFactory(cell -> cell.getValue().getId().asObject());
        price_column.setCellValueFactory(cell -> cell.getValue().getCoast().asObject());
    }

    @FXML
    public void loadOtherPhoto() {
        ArrayList<String> list = tableItems.getItems().get(tableItems.getSelectionModel().getSelectedIndex()).getUrlOtherImg();
        for (int q = 1; q < list.size(); q++) {
            ImageView img = new ImageView(list.get(q));
            item_pics.getChildren().add(img);
        }
    }

    @FXML
    public void saveEditItem() {
        MyItem item = tableItems.getItems().get(tableItems.getSelectionModel().getSelectedIndex());
        item.setTitle(item_name.getText());
        item.setDescription(item_description.getText());
        updateColumn();
    }


    private void showItemInfo(MyItem item) {
        loadId = item.getId().getValue();
        try {
            new Task<Void>() {
                @Override
                protected Void call() throws Exception {

                    item_name.setText(item.getTitle().getValue());
                    item_price.setText(item.getCoast().getValue().toString() + " Руб.");
                    item_description.setText(item.getDescription());
                    item_pics.getChildren().clear();

                    Image img = new Image(item.getUrlMainImg());
                    if (item.getId().getValue() == loadId) item_img.setImage(img);
                    return null;
                }
            }.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void startCloneProcess() {

        if (groupId.getText().length() == 0 || almubId.getText().length() == 0) {
            MyException.generate().setMsg("Вы не указали информацию о вашем магазине:").setLevelError(Alert.AlertType.WARNING).show(true);
            return;
        }

        try {
            if (items.size() == 0) {
                throw new NullPointerException("Not selected item");
            }

            Config.a().setLastClub(groupId.getText());
            Config.a().setLastAlmub(almubId.getText());
            Config.a().save();

            new CopyScreen(items).
                    init(Main.getApp().manager())
                    .setSettings(
                            priceController.getText(),
                            changePrice.isSelected(),
                            myDescription.isSelected(),
                            schemDescription.getText(),
                            Integer.valueOf(groupId.getText()),
                            Integer.valueOf(almubId.getText()));
        } catch (NullPointerException ex) {
            MyException.generate().setMsg("Список копируемых вещей пуст").setException(ex).setLevelError(Alert.AlertType.ERROR).show(true);
        }
    }

    @FXML
    public void openSpammer() {
        new SpammerScreen().init(Main.getApp().manager());
    }

    @FXML
    public void quitUser() throws Exception {
        Config.a().setToken("");
        Config.a().save();
        Main.getApp().manager().set(new AuthScreen().init(Main.getApp().manager()));
    }

}
