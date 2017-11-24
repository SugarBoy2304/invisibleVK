package myvk.utils;

import com.vk.api.sdk.objects.market.MarketItem;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

import java.util.ArrayList;

public class MyItem {

    private static int fid = 0;

    private int id;
    private int coast;
    private boolean add = true;
    private String description;
    private String title;
    private PhotoField photoList;

    private MarketItem vkItem;

    public MyItem(MarketItem vkItem) {
        fid++;
        this.id = fid;
        this.title = vkItem.getTitle();
        this.vkItem = vkItem;
        this.coast = Integer.valueOf(vkItem.getPrice().getAmount()) / 100;
    }

    public MyItem(MarketItem vkItem, PhotoField photos) {
        this(vkItem);
        this.photoList = photos;
    }

    public MarketItem getVkItem() {
        return this.vkItem;
    }

    public StringProperty getTitle() {
        return new SimpleStringProperty(title);
    }

    public SimpleObjectProperty getCheckBox() {
        CheckBox box = new CheckBox();
        box.setSelected(add);
        box.setOnAction(event -> {if (event.getSource() instanceof CheckBox) add = !add; });
        return new SimpleObjectProperty(box);
    }
    
    public SimpleIntegerProperty getId() {
        return new SimpleIntegerProperty(id);
    }
    
    public SimpleIntegerProperty getCoast() {
        return new SimpleIntegerProperty(coast);
    }

    public String getUrlMainImg() {
        return photoList.getImages(ShopParser.ImageSize.VERYSMALL).get(0);
    }

    public ArrayList<String> getUrlOtherImg() {
        return photoList.getImages(ShopParser.ImageSize.VERYSMALL);
    }

    public String getDescription() {
            if (description == null) {
                return this.vkItem.getDescription();
            } else return description.replace("%name%", getTitle().getValue()).replace("%price%", getCoast().getValue().toString());
    }

    public String getDescription(String schem, int price) {
        return schem.replace("%name%", title).replace("%price%", price + "");
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCoast(int coast) {
        this.coast = coast;
    }

    public ArrayList<String> getNormalImage() {
        return photoList.getImages(ShopParser.ImageSize.NORMAL);
    }

    public boolean isAdd() {
        return add;
    }

}
