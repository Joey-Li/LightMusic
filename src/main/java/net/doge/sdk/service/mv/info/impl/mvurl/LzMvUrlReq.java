package net.doge.sdk.service.mv.info.impl.mvurl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.entity.service.NetMvInfo;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class LzMvUrlReq {
    private static LzMvUrlReq instance;

    private LzMvUrlReq() {
    }

    public static LzMvUrlReq getInstance() {
        if (instance == null) instance = new LzMvUrlReq();
        return instance;
    }

    // 视频链接获取 API (李志)
    private final String VIDEO_URL_LZ_API = "https://www.lizhinb.com/lz-video/%s/";

    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public String fetchMvUrl(NetMvInfo mvInfo) {
        String id = mvInfo.getId();
        String mvBody = HttpRequest.get(String.format(VIDEO_URL_LZ_API, id))
                .executeAsStr();
        Document doc = Jsoup.parse(mvBody);
        Elements video = doc.select(".nbvp-player");
        String mvJsonStr = video.attr("data-items");
        JSONArray mvArray = JSONArray.parseArray(mvJsonStr);
        JSONObject mvJson = mvArray.getJSONObject(0);
        return mvJson.getString("url");
    }
}
