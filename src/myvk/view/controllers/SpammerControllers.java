package myvk.view.controllers;

import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.wall.WallPostFull;
import com.vk.api.sdk.queries.wall.WallCreateCommentQuery;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import myvk.Main;
import myvk.auth.Auth;
import myvk.utils.Config;
import myvk.utils.Log;
import myvk.utils.exception.MyException;

import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class SpammerControllers implements Initializable {

    @FXML
    public TextArea accounts;

    @FXML
    public TextArea groups;

    @FXML
    public TextArea textMsg;

    @FXML
    public TextArea logger;

    private List<UserActor> users = new ArrayList<>();

    @FXML
    public void startSpam() throws ParseException, ClientException, ApiException, InterruptedException {

        Config.a().setSpammer_accounts(Arrays.asList(accounts.getText().split("\n")));
        Config.a().setSpammer_groups(Arrays.asList(groups.getText().split("\n")));
        Config.a().setSpammer_message(Arrays.asList(textMsg.getText().split("\n")));
        Config.a().save();

        for (String url : accounts.getText().split("\n")) {
            try {
                String[] data = Auth.parseRedirectUrl(url);
                users.add(new UserActor(Integer.valueOf(data[1]), data[0]));
                log("Успешно авторизирован пользователь с айди: " + data[0]);
            } catch (Exception ex) {
                MyException.generate("Ошибка авторизации").setLevelError(Alert.AlertType.INFORMATION).setException(ex).setMsg("Неудавшая авторизация пользователя:\n" + url).show(false);
            }
        }

        for (String url : groups.getText().split("\n")) {
            //int groupId = Main.getApp().api().groups().getById(rnd()).groupId(url.replace("https://vk.com/", "")).execute().get(0).getId();

            List<WallPostFull> walls = Main.getApp().api().wall().get(rnd()).domain(url.replace("https://vk.com/", "")).count(3).execute().getItems();
            log(String.format("Записи группы %s были успешно загружены.", url));

            for (WallPostFull post : walls) {

                try {

                    Thread.sleep(15000L);

                    WallCreateCommentQuery query = Main.getApp().api().wall().createComment(rnd(), post.getId()).message(textMsg.getText()).guid(ThreadLocalRandom.current().longs() + "").ownerId(post.getOwnerId());
                    if (post.getComments().getCount() != 0) query.replyToComment(post.getComments().getCount());
                    query.execute();

                    log("Successful create comment in " + url);

                } catch (Exception ex) {

                    if (ex.getMessage().contains("(213)")) {
                        log("Комментарии у группы " + url + " закрыты");
                        break;
                    }

                    log("Ошибка при создании коммента: " + ex.getMessage());
                }

            }

            Thread.sleep(15000L);
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accounts.setText(msg(Config.a().getSpammer_accounts()));
        groups.setText(msg(Config.a().getSpammer_groups()));
        textMsg.setText(msg(Config.a().getSpammer_message()));
    }

    public UserActor rnd() {
        return users.get(ThreadLocalRandom.current().nextInt(users.size()));
    }

    public void log(String s) {
        if (logger.getText().split("\n").length >= 25) {

        }

        logger.setText(logger.getText() + "\n" + s);
        Log.send("Spammer", s);
    }

    public String msg(List<String> list) {
        try {
            String s = "";
            for (String ss : list) s += ss + "\n";
            return s;
        } catch (Exception ex) {
            return "";
        }
    }

}
