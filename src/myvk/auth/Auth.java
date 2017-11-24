package myvk.auth;

import com.vk.api.sdk.client.VkApiClient;
import myvk.utils.Log;
import myvk.utils.exception.MyException;

import java.net.URLEncoder;
import java.text.ParseException;

public class Auth {

    private static final String TAG = "Unique.Auth";
    public static String redirect_url = "https://oauth.myvk.com/blank.html";

    public static String getUrl(String api_id, VkApiClient vk) {
        String url = "https://oauth.myvk.com/authorize?client_id=" + api_id + "&display=mobile&scope=" + getSettings() + "&redirect_uri=" + URLEncoder.encode(redirect_url) + "&response_type=token"
                + "&v=" + URLEncoder.encode(vk.getVersion());
        return url;
    }

    public static String getSettings() {
        //http://vk.com/dev/permissions
        return "notify,friends,photos,audio,video,docs,status,notes,pages,wall,groups,messages,offline,notifications";
    }

    public static String[] parseRedirectUrl(String url) throws ParseException {
        //url is something like http://api.vkontakte.ru/blank.html#access_token=66e8f7a266af0dd477fcd3916366b17436e66af77ac352aeb270be99df7deeb&expires_in=0&user_id=7657164
        String access_token = Utils.extractPattern(url, "access_token=(.*?)&");
        //Log.send(TAG, "access_token=" + access_token);
        String user_id = Utils.extractPattern(url, "user_id=(\\d*)");
        //Log.send(TAG, "user_id=" + user_id);
        if (user_id == null || user_id.length() == 0 || access_token == null || access_token.length() == 0) {
            throw new ParseException("Failed to parse redirect url", 0);
            //MyException.generate().setMsg("Ошибка авторизации").setError("Подробнее:").setException(new Exception("Failed to parse redirect url")).show(true);
        }
        return new String[]{access_token, user_id};
    }

}
