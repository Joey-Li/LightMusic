package net.doge.sdk.common.builder;

import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.constant.qq.QqSearchDevice;
import net.doge.sdk.common.constant.qq.QqSearchType;
import net.doge.util.core.RandomUtil;
import net.doge.util.core.http.HttpRequest;

public class QqReqBuilder {
    private static QqReqBuilder instance;

    private QqReqBuilder() {
    }

    public static QqReqBuilder getInstance() {
        if (instance == null) instance = new QqReqBuilder();
        return instance;
    }

    public HttpRequest buildSearchRequest(QqSearchDevice device, QqSearchType type, String keyword, int page, int limit) {
        String json = "{\"comm\":{\"ct\":\"11\",\"cv\":\"14090508\",\"v\":\"14090508\",\"tmeAppID\":\"qqmusic\",\"phonetype\":\"EBG-AN10\",\"deviceScore\":\"553.47\"," +
                "\"devicelevel\":\"50\",\"newdevicelevel\":\"20\",\"rom\":\"HuaWei/EMOTION/EmotionUI_14.2.0\",\"os_ver\":\"12\",\"OpenUDID\":\"0\",\"OpenUDID2\":\"0\"," +
                "\"QIMEI36\":\"0\",\"udid\":\"0\",\"chid\":\"0\",\"aid\":\"0\",\"oaid\":\"0\",\"taid\":\"0\",\"tid\":\"0\",\"wid\":\"0\",\"uid\":\"0\",\"sid\":\"0\"," +
                "\"modeSwitch\":\"6\",\"teenMode\":\"0\",\"ui_mode\":\"2\",\"nettype\":\"1020\",\"v4ip\":\"\"},\"req\":{\"module\":\"music.search.SearchCgiService\"," +
                "\"method\":\"%s\",\"param\":{\"searchid\":\"%s\",\"search_type\":%s,\"query\":\"%s\",\"page_num\":%s,\"num_per_page\":%s,\"highlight\":0," +
                "\"nqc_flag\":0,\"multi_zhida\":0,\"cat\":2,\"grp\":1,\"sin\":0,\"sem\":0}}}";
        int t;
        switch (type) {
            case SONG:
            default:
                t = 0;
                break;
            case LYRIC:
                t = 7;
                break;
            case PLAYLIST:
                t = 3;
                break;
            case ALBUM:
                t = 2;
                break;
            case ARTIST:
                t = 1;
                break;
            case MV:
                t = 4;
                break;
            case USER:
                t = 8;
                break;
        }
        String method;
        switch (device) {
            case MOBILE:
            default:
                method = "DoSearchForQQMusicMobile";
                break;
            case PC:
                method = "DoSearchForQQMusicDesktop";
                break;
        }
        String searchId = RandomUtil.randomNumbers(16);
        return HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .jsonBody(String.format(json, method, searchId, t, keyword, page, limit));
    }
}
