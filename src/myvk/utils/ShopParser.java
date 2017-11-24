package myvk.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.groups.GroupFull;
import com.vk.api.sdk.objects.market.MarketItem;
import com.vk.api.sdk.queries.market.MarketGetQuery;
import myvk.PullUp;
import myvk.utils.exception.MyException;

import java.util.*;

public class ShopParser {

    private int id;
    private GroupFull group;

    public ShopParser(int id) {
        this.id = id;
        try {
            this.group = PullUp.getApp().api().groups().getById(PullUp.getApp().user()).groupId(id + "").execute().get(0);
        } catch (ApiException e) {
            MyException.generate().setException(e).setMsg("Ошибка в загрузке группы").show(true);
        } catch (ClientException e) {
            MyException.generate().setException(e).setMsg("Ошибка в загрузке группы").show(true);
        }
    }

    /** @param album номер альбома
     *  Возвращает список товаров List<MarketItem>
     *  и фото Map<Integer, List<String>>
     *  **/
    public Object[] getItems(int album) {

        List<MarketItem> all = new ArrayList<>();
        Map<Integer, PhotoField> map = new HashMap<>();

        boolean b = true;
        int offset = 0;

        while (b) {
            try {

                /** Пауза между запросами **/
                Thread.sleep(350L);

                /** Генерируем запрос к вк апи**/
                // @id - айди группы, offset - смещени (получать можно лишь 100 товаров за один запрос)
                MarketGetQuery query = PullUp.getApp().api().market().get(PullUp.getApp().user(), -id).offset(offset * 100).albumId(album);
                Map<String, Integer> extended = new HashMap<>();
                extended.put("photos", 1); // Добавляем кастомный параметр photos, чтобы в запросе получить фото
                query.unsafeParam("extended", extended);

                /** Выполняем запрос **/
                List<MarketItem> cache = query.execute().getItems();

                System.out.println(cache.get(0).getThumbPhoto());

                /****/
                map.putAll(parseImg(query.executeAsString()));

                Log.send("Load items " + (offset * 100 + cache.size()));
                offset++;
                if (cache.size() < 100) b = false;
                all.addAll(cache);

            } catch (ApiException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return new Object[]{ all, map };
    }

    public Map<Integer, PhotoField> parseImg(String text) {

        Map<Integer, PhotoField> map = new HashMap<>();

        JsonParser parser = new JsonParser();
        JsonArray json = parser.parse(text).getAsJsonObject().getAsJsonObject("response").getAsJsonArray("items");

        for (int q = 0; q < json.size(); q++) {

            PhotoField field = new PhotoField();

            JsonObject obj = json.get(q).getAsJsonObject();
            int id = obj.get("id").getAsInt();
            JsonArray photosJson = obj.getAsJsonArray("photos");

            for (ImageSize size : ImageSize.values()) {

                for (int a = 0; a < photosJson.size(); a++)
                    field.add(photosJson.get(a).getAsJsonObject().get("photo_" + size.size).getAsString(), size);

            }

            map.put(id, field);

        }

        return map;
    }

    public enum ImageSize {

        VERYSMALL(75),
        SMALL(130),
        NORMAL(604);


        int size;

        ImageSize(int size) {
           this.size = size;
        }

        int size() {
            return this.size;
        }

    }

}
