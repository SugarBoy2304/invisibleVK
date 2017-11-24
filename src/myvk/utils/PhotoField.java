package myvk.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PhotoField {

    private Map<ShopParser.ImageSize, ArrayList<String>> map = new HashMap<>();

    public PhotoField() {
        map.put(ShopParser.ImageSize.VERYSMALL, new ArrayList<>());
        map.put(ShopParser.ImageSize.SMALL, new ArrayList<>());
        map.put(ShopParser.ImageSize.NORMAL, new ArrayList<>());
    }

    public void add(String url, ShopParser.ImageSize size) {
        map.get(size).add(url);
    }

    public ArrayList<String> getImages(ShopParser.ImageSize size) {
        return map.get(size);
    }


}
