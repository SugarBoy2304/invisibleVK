package myvk.view.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.api.sdk.client.ApiRequest;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.market.responses.AddResponse;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.responses.GetMarketUploadServerResponse;
import com.vk.api.sdk.queries.market.MarketAddQuery;
import com.vk.api.sdk.queries.photos.PhotosSaveMarketPhotoQuery;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import myvk.PullUp;
import myvk.screens.CopyScreen;
import myvk.utils.Log;
import myvk.utils.MyItem;
import myvk.utils.exception.MyException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class CopyControllers implements Initializable {


    private Stage stage;
    private boolean addAll = false;

    private int groupId;
    private int albumId;

    private String priceController;
    private boolean changePrice;
    private boolean myDescription;
    private String schemDescription;


    @FXML
    private Label status;

    @FXML
    private ImageView img;

    @FXML
    private TextField name;

    @FXML
    private TextField price;

    private CopyScreen field;
    private ObservableList<MyItem> items;

    public void setField(CopyScreen field, ObservableList<MyItem> items) {
        this.field = field;
        this.items = items;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setGroup(int groupId, int albumId) {
        this.groupId = groupId;
        this.albumId = albumId;
    }

    public void setSettings(String priceController, boolean changePrice, boolean myDescription, String schemDescription) {
        this.priceController = priceController;
        this.changePrice = changePrice;
        this.myDescription = myDescription;
        this.schemDescription = schemDescription;
        next();
    }

    public int getCoast(int startPrice) {
        if (changePrice) {
            if (priceController.endsWith("%")) {
                double sale = ((double) startPrice) / 100D * Double.valueOf(priceController.replace("%", ""));
                String price = String.format("%d", (int) sale);
                return Integer.valueOf(price.substring(0, price.length() - 2) + "90");
            } else return startPrice - Integer.valueOf(priceController);
        } else {
            return startPrice;
        }
    }

    public boolean next() {

        /** Обработка завершения копирования **/
        if (items.size() == 0) {
            stage.close();
            MyException.generate("Операция завершена успешно").setMsg("Копирование завершено").setLevelError(Alert.AlertType.CONFIRMATION).show(false);
            return false;
        }

        MyItem item = items.get(0);
        img.setImage(new Image(item.getNormalImage().get(0)));
        price.setText(getCoast(item.getCoast().getValue()) + "");
        name.setText(item.getTitle().getValue());

        return true;
    }

    @FXML
    public void addItem() {
        new Thread(()-> {
            try {

            Thread.sleep(400L);
            MyItem item = items.get(0);

            /** Пропускаем ненужные товары **/
            if (!item.isAdd()) {
                items.remove(0);
                next();
                return;
            }

            /** Начинаем парсить **/
            JsonParser parser = new JsonParser();

            /** Id основной фотографии **/
            int mainPhoto = 0;
            /** Массив айди других фотографий***/
            ArrayList<Integer> otherPhoto = new ArrayList<>();

            /** Загрузка каждого изображения на сервер **/
            for (int q = 0; q < item.getNormalImage().size(); q++) {

                Thread.sleep(350L);

                /** Создание сервера в зависимости от типа фото **/
                GetMarketUploadServerResponse server;
                if (q == 0) {
                    Image img = new Image(item.getNormalImage().get(q));
                    double sizeImg = img.getWidth() > img.getHeight() ? img.getHeight() : img.getWidth();
                    server = PullUp.getApp().api().photos().getMarketUploadServer(PullUp.getApp().user(), groupId).mainPhoto(true).cropY(0).cropX(0).cropWidth((int) sizeImg).execute();
                } else {
                    server = PullUp.getApp().api().photos().getMarketUploadServer(PullUp.getApp().user(), groupId).mainPhoto(false).execute();
                }

                /** Отправка на сервер фотографии **/
                String out = loadPhoto(server.getUploadUrl(), item.getNormalImage().get(q));

                /** Парсим ответ сервера **/
                JsonObject object = parser.parse(out).getAsJsonObject();

                Thread.sleep(350L);

                /** Подтверждаем загрузку фотографии **/
                PhotosSaveMarketPhotoQuery saveQuery = PullUp.getApp().api().photos().saveMarketPhoto(PullUp.getApp().user(),
                        object.get("photo").toString().substring(1, object.get("photo").toString().length() - 1).replace("\\", ""),
                        object.get("server").getAsInt(),
                        object.get("hash").getAsString());

                /** Указываем айди сообшества **/
                saveQuery.groupId(groupId);

                /** Дополнительные параметры для главной фотографии **/
                if (q == 0) {
                    saveQuery = saveQuery.cropData(object.get("crop_data").getAsString()).cropHash(object.get("crop_hash").getAsString());
                }

                /** Получаем эту фотографию **/
                Photo photo = saveQuery.execute().get(0);

                /** Добавляем в память в зависимости от типа фото (главная, второстепенная) **/
                if (q == 0) mainPhoto = photo.getId();
                else otherPhoto.add(photo.getId());

            }

            Thread.sleep(350L);

            /** Высчитываем цену товара в зависимости от нашей схемы **/
            int price = getCoast(item.getCoast().getValue());

            /** Генерируерм описание **/
            String description = myDescription ? item.getDescription(schemDescription, price) : item.getDescription();

            /** Генерируем запрос добавления товара**/
            MarketAddQuery query = PullUp.getApp().api().market().
                    add(PullUp.getApp().user(),
                            // ID Сообщества
                            -groupId,
                            // Имя товара
                            item.getTitle().getValue(),
                            // Описание товара
                            description,
                            // Категория товара
                            1,
                            // Цена товара
                            price,
                            // Обложка товара
                            mainPhoto);
            /** Добавляем второстепенные фотографии **/
            query.photoIds(otherPhoto);

            /** Получаем айди загруженного товара **/
            AddResponse response = query.execute();

            Thread.sleep(350L);

            /** Переопредляем товар в альбом **/
            PullUp.getApp().api().market().addToAlbum(PullUp.getApp().user(), -groupId, response.getMarketItemId(), albumId).execute();

            /** Логгируем***/
            Log.send("SHOP", String.format("Item %s successful added", item.getTitle().getValue()));

            /** Удаляем товар из списка копируемых **/
            items.remove(0);

            /** Запускаем следующий товар **/
            next();
            if (addAll) addItem();

            } catch (ApiException ex) {
                MyException.generate("Ошибка загрузки товара").setLevelError(Alert.AlertType.ERROR).setException(ex).setMsg(String.format("Товар: %s", items.get(0).getTitle().getValue())).show(true);
            } catch (InterruptedException ex) {
                MyException.generate("Ошибка загрузки товара").setLevelError(Alert.AlertType.ERROR).setException(ex).setMsg(String.format("Товар: %s", items.get(0).getTitle().getValue())).show(true);
            } catch (IOException ex) {
                MyException.generate("Ошибка загрузки товара").setLevelError(Alert.AlertType.ERROR).setException(ex).setMsg(String.format("Товар: %s", items.get(0).getTitle().getValue())).show(true);
            } catch (ClientException ex) {
                MyException.generate("Ошибка загрузки товара").setLevelError(Alert.AlertType.ERROR).setException(ex).setMsg(String.format("Товар: %s", items.get(0).getTitle().getValue())).show(true);
            }

        }).start();
    }

    @FXML
    public void addAll() throws InterruptedException, IOException, ApiException, ClientException {
        addAll = true;
        addItem();
    }

    @FXML
    public void skipItem() {
        items.remove(0);
        next();
    }

    public String loadPhoto(String server, String photo) throws IOException {
        InputStream in = new URL(photo).openStream();
        String random = System.currentTimeMillis() + "_" + new Random().nextInt(100);
        Files.copy(in, Paths.get("C:/Unique/cashe/" + random + ".jpg"));

        HttpClientBuilder builder = HttpClientBuilder.create();
        CloseableHttpClient httpClient = builder.build();

        HttpPost httpPost = new HttpPost(server);

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.addPart("file", new FileBody(new File("C:/Unique/cashe/" + random + ".jpg")));
        // entityBuilder.addPart("fileb", new FileBody(fileb));
        final HttpEntity entity = entityBuilder.build();
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);
        InputStreamReader reader = new InputStreamReader(response.getEntity().getContent());
        BufferedReader in2 = new BufferedReader(reader);


        String inputLine;
        StringBuffer response2 = new StringBuffer();

        while ((inputLine = in2.readLine()) != null) {
            response2.append(inputLine);
        }
        reader.close();

        // print result
        //System.out.println(response2.toString());
        return response2.toString();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
