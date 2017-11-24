package myvk;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiAuthException;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.account.UserSettings;
import com.vk.api.sdk.objects.market.MarketItem;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import myvk.auth.Auth;
import myvk.screens.AuthScreen;
import myvk.screens.MainScreen;
import myvk.screens.logic.ScreenManager;
import myvk.utils.Config;
import myvk.utils.ShopParser;

import java.text.ParseException;
import java.util.List;

public class PullUp extends Application {

    /** постоянная приложения **/
    public static PullUp app;

    /**постоянная пользователя **/
    private UserActor user;

    /** постоянная экранного менеджера **/
    private ScreenManager manager;

    /** Постоянная VK API **/
    private VkApiClient api;

    public static PullUp getApp() {
        return app;
    }

    /** Загрузка приложения **/
    @Override
    public void start(Stage primaryStage) throws Exception {
        app = this;

        api = new VkApiClient(HttpTransportClient.getInstance());

        manager = new ScreenManager();
        manager.initStage(primaryStage);

        Config.load();

        try {
            initUser();
            // Успешная авторизация
        } catch (ApiAuthException ex) {
            // Проавальная авторизация
            manager.set(new AuthScreen().init(manager));

            return;
        }
    }

    public UserActor initUser() throws ApiAuthException {
        String[] data;
        try {
            data = Auth.parseRedirectUrl(Config.a().getToken());
        } catch (ParseException ex) {
            throw new ApiAuthException("Failed parse token");
        }

        this.user = new UserActor(Integer.valueOf(data[1]), data[0]);

        //removeAll();
        //if (true) return null;

        manager.set(new MainScreen().init(manager));

        try {
            new Task<Void>() {
                @Override protected Void call() throws Exception {
                    try

                    {
                        UserSettings query = api().account().getProfileInfo(user).execute();
                        MainScreen.get().setName(String.format("%s %s", query.getFirstName(), query.getLastName()));
                    } catch(
                            ApiException e)
                    {
                        e.printStackTrace();
                    } catch(
                            ClientException e)
                    {
                        e.printStackTrace();
                    }
                    return null;
                }}.call();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return this.user;
    }

    /** getters **/

    public VkApiClient api() {
        return api;
    }

    public ScreenManager manager() {
        return this.manager;
    }

    public UserActor user() {
        return this.user;
    }

    private void removeAll() {
        ShopParser parser = new ShopParser(134278196);
        List<MarketItem> list = (List<MarketItem>) parser.getItems(0)[0];
        for (MarketItem item : list) {
            try {
                PullUp.getApp().api().market().delete(PullUp.getApp().user(), -134278196, item.getId()).execute();
                Thread.sleep(350L);
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
