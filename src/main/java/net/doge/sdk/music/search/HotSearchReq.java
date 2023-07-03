package net.doge.sdk.music.search;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import net.doge.constants.GlobalExecutors;
import net.doge.sdk.common.SdkCommon;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HotSearchReq {
    // 热搜 API
    private final String HOT_SEARCH_API = SdkCommon.prefix + "/search/hot";
    // 热搜 API (酷狗)
    private final String HOT_SEARCH_KG_API = "http://gateway.kugou.com/api/v3/search/hot_tab?signature=ee44edb9d7155821412d220bcaf509dd&appid=1005&clientver=10026&plat=0";
    // 热搜 API (QQ)
    private final String HOT_SEARCH_QQ_API = "https://u.y.qq.com/cgi-bin/musicu.fcg";
    // 热搜 API (酷我)
    private final String HOT_SEARCH_KW_API
            = "http://hotword.kuwo.cn/hotword.s?prod=kwplayer_ar_9.3.0.1&corp=kuwo&newver=2&vipver=9.3.0.1" +
            "&source=kwplayer_ar_9.3.0.1_40.apk&p2p=1&notrace=0&uid=0&plat=kwplayer_ar&rformat=json&encoding=utf8&tabid=1";
    // 热搜 API (咪咕)
    private final String HOT_SEARCH_MG_API = "http://jadeite.migu.cn:7090/music_search/v3/search/hotword";

    /**
     * 获取热搜
     *
     * @return
     */
    public Set<String> getHotSearch() {
        Set<String> results = new LinkedHashSet<>();

        // 网易云
        Callable<List<String>> getHotSearch = () -> {
            LinkedList<String> res = new LinkedList<>();

            String hotSearchBody = HttpRequest.get(String.format(HOT_SEARCH_API))
                    .execute()
                    .body();
            JSONObject hotSearchJson = JSONObject.fromObject(hotSearchBody);
            JSONObject result = hotSearchJson.getJSONObject("result");
            JSONArray hotSearchArray = result.getJSONArray("hots");
            for (int i = 0, len = hotSearchArray.size(); i < len; i++) {
                JSONObject keywordJson = hotSearchArray.getJSONObject(i);
                res.add(keywordJson.getString("first").trim());
            }
            return res;
        };

        // 酷狗
        Callable<List<String>> getHotSearchKg = () -> {
            LinkedList<String> res = new LinkedList<>();

            String hotSearchBody = HttpRequest.get(String.format(HOT_SEARCH_KG_API))
                    .header("dfid", "1ssiv93oVqMp27cirf2CvoF1")
                    .header("mid", "156798703528610303473757548878786007104")
                    .header("clienttime", "1584257267")
                    .header("x-router", "msearch.kugou.com")
                    .header("user-agent", "Android9-AndroidPhone-10020-130-0-searchrecommendprotocol-wifi")
                    .header("kg-rc", "1")
                    .execute()
                    .body();
            JSONArray hotkeys = JSONObject.fromObject(hotSearchBody).getJSONObject("data").getJSONArray("list").getJSONObject(0).getJSONArray("keywords");
            for (int i = 0, len = hotkeys.size(); i < len; i++) {
                JSONObject keywordJson = hotkeys.getJSONObject(i);
                res.add(keywordJson.getString("keyword"));
            }
            return res;
        };

        // QQ
        Callable<List<String>> getHotSearchQq = () -> {
            LinkedList<String> res = new LinkedList<>();

            String hotSearchBody = HttpRequest.post(String.format(HOT_SEARCH_QQ_API))
                    .body("{\"comm\":{\"ct\":\"19\",\"cv\":\"1803\",\"guid\":\"0\",\"patch\":\"118\",\"psrf_access_token_expiresAt\":0,\"psrf_qqaccess_token\":\"\",\"psrf_qqopenid\":\"\",\"psrf_qqunionid\":\"\",\"tmeAppID\":\"qqmusic\",\"tmeLoginType\":0,\"uin\":\"0\",\"wid\":\"0\"},\"hotkey\":{\"method\":\"GetHotkeyForQQMusicPC\",\"module\":\"tencent_musicsoso_hotkey.HotkeyService\",\"param\":{\"search_id\":\"\",\"uin\":0}}}")
                    .execute()
                    .body();
            JSONArray hotkeys = JSONObject.fromObject(hotSearchBody).getJSONObject("hotkey").getJSONObject("data").getJSONArray("vec_hotkey");
            for (int i = 0, len = hotkeys.size(); i < len; i++) {
                JSONObject keywordJson = hotkeys.getJSONObject(i);
                res.add(keywordJson.getString("title"));
            }
            return res;
        };

        // 酷我
        Callable<List<String>> getHotSearchKw = () -> {
            LinkedList<String> res = new LinkedList<>();

            HttpResponse resp = HttpRequest.get(String.format(HOT_SEARCH_KW_API)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                JSONArray hotkeys = JSONObject.fromObject(resp.body()).getJSONArray("tagvalue");
                for (int i = 0, len = hotkeys.size(); i < len; i++) {
                    res.add(hotkeys.getJSONObject(i).getString("key"));
                }
            }
            return res;
        };

        // 咪咕
        Callable<List<String>> getHotSearchMg = () -> {
            LinkedList<String> res = new LinkedList<>();

            HttpResponse resp = HttpRequest.get(String.format(HOT_SEARCH_MG_API)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                JSONObject data = JSONObject.fromObject(resp.body()).getJSONObject("data");
                JSONArray hotkeys = data.getJSONArray("hotwords").getJSONObject(0).getJSONArray("hotwordList");
                for (int i = 0, len = hotkeys.size(); i < len; i++) {
                    res.add(hotkeys.getJSONObject(i).getString("word"));
                }
                hotkeys = data.getJSONArray("discovery");
                for (int i = 0, len = hotkeys.size(); i < len; i++) {
                    res.add(hotkeys.getJSONObject(i).getString("word"));
                }
            }
            return res;
        };

        List<Future<List<String>>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(getHotSearch));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotSearchKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotSearchQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotSearchKw));
        taskList.add(GlobalExecutors.requestExecutor.submit(getHotSearchMg));

        taskList.forEach(task -> {
            try {
                results.addAll(task.get());
            } catch (InterruptedException e) {
//                e.printStackTrace();
            } catch (ExecutionException e) {
//                e.printStackTrace();
            }
        });

        return results;
    }
}
