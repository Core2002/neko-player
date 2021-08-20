package fun.fifu.nekoplayer;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.nio.charset.StandardCharsets;

public class Configer {
    public static final String configPath = "plugins/NekoPlayer/config.json";

    public static JSONObject readConfig() {
        return JSONUtil.readJSONObject(FileUtil.file(configPath), StandardCharsets.UTF_8);
    }

    public static void saveConfig(JSONObject config) {
        FileUtil.writeString(config.toJSONString(2), configPath, StandardCharsets.UTF_8);
    }

    public static String getFrameName(String animation, int frame) {
        return Configer.readConfig().getJSONObject(animation).getStr("file-name").replace("${frame}", frame + "");
    }

    public static Character getBlackBit(String animation) {
        return readConfig().getJSONObject(animation).getChar("black-bit", '1');
    }

    public static Character getWhiteBit(String animation) {
        return readConfig().getJSONObject(animation).getChar("white-bit", ' ');
    }

}
