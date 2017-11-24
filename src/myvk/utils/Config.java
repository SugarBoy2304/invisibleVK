package myvk.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import myvk.utils.exception.MyException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Config {

    private static File configFile;
    private static ObjectMapper mapper;

    /****/

    private String token;
    private String last_url;
    private String last_club;
    private String last_almub;

    private static Config config;

    public static Config a() {
        return config;
    }

    public static void load() {
        mapper = new ObjectMapper(new YAMLFactory());

        try {
            configFile = new File(System.getenv("APPDATA") + "/myvk/config.yaml");
            File schem = new File(Config.class.getClassLoader().getResource("config.yaml").getFile());
            configFile.getParentFile().mkdir();

            if (!configFile.exists()) FileUtils.copyFile(schem, configFile);

            config = mapper.readValue(configFile, Config.class);

        } catch (Exception e) {
            MyException.generate().setMsg("Ошибка конфига").setError(e.getMessage()).setException(e).show(false);
        }
    }

    public void save() {
        try {
            mapper.writeValue(configFile, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /****/

    public String getToken() {
        return this.token;
    }

    public String setToken(String token) {
        return this.token = token;
    }

    public String getLastUrl() {
        return last_url;
    }

    public void setLastUrl(String last_url) {
        this.last_url = last_url;
    }

    public String getLastClub() {
        return last_club;
    }

    public void setLastClub(String last_club) {
        this.last_club = last_club;
    }

    public String getLastAlmub() {
        return last_almub;
    }

    public void setLastAlmub(String last_almub) {
        this.last_almub = last_almub;
    }
}
