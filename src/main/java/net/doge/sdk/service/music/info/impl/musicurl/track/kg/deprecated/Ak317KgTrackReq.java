//package net.doge.sdk.service.music.info.impl.musicurl.track.kg;
//
//import com.alibaba.fastjson2.JSONObject;
//import net.doge.constant.core.media.AudioQuality;
//import net.doge.util.core.RandomUtil;
//import net.doge.util.core.StringUtil;
//import net.doge.util.core.crypto.CryptoUtil;
//import net.doge.util.core.http.HttpRequest;
//import net.doge.util.core.json.JsonUtil;
//import net.doge.util.core.log.LogUtil;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//public class Ak317KgTrackReq {
//    private static Ak317KgTrackReq instance;
//
//    private Ak317KgTrackReq() {
//        initMap();
//        initBlacklist();
//    }
//
//    public static Ak317KgTrackReq getInstance() {
//        if (instance == null) instance = new Ak317KgTrackReq();
//        return instance;
//    }
//
//    // 歌曲 URL 获取 API (酷狗)
//    private final String SONG_URL_KG_API = "https://api.317ak.cn/api/yinyue/kugou?ckey=%s&i=%s&br=%s&type=json&lrc=1";
//
//    private final String[] REQUEST_KEYS = {
//            "UE9WTUhLSklYOEE3SUdIMkZNMVA=",
//            "WE1VS0lBSjNQOExQWDNQOTcxS1U=",
//            "N0tUSTUyVDdWTE9EUjZTVDM3UFQ="
//    };
//
//    private Map<String, String> qualityMap = new HashMap<>();
//    // url 黑名单
//    private Set<String> urlBlacklist = new HashSet<>();
//
//    private void initMap() {
//        // 标准品质
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "1");
//        // HQ
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "2");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.SUPER], "3");
//        // 无损
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "4");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "5");
//        // 至臻全景声
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "6");
//        // 至臻母带
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "6");
//    }
//
//    private void initBlacklist() {
//        urlBlacklist.add("&API=api.xcvts.cn");
//    }
//
//    private String decodeRequestKey(String requestKey) {
//        return CryptoUtil.base64DecodeStr(requestKey);
//    }
//
//    /**
//     * 获取酷狗音乐歌曲链接
//     *
//     * @param hash     歌曲 hash
//     * @param quality 品质
//     * @return
//     */
//    public String getTrackUrl(String hash, String quality) {
//        try {
//            String key = decodeRequestKey(RandomUtil.randomChoose(REQUEST_KEYS));
//            String songBody = HttpRequest.get(String.format(SONG_URL_KG_API, key, hash, qualityMap.get(quality)))
//                    .executeAsStr();
//            JSONObject songJson = JSONObject.parseObject(songBody);
//            if (songJson.getIntValue("code") != 0) return "";
//            JSONObject data = songJson.getJSONObject("data");
//            if (JsonUtil.isEmpty(data)) return "";
//            String trackUrl = data.getString("music");
//            if (StringUtil.isEmpty(trackUrl) || urlBlacklist.contains(trackUrl)) return "";
//            return trackUrl;
//        } catch (Exception e) {
//            LogUtil.error(e);
//            return "";
//        }
//    }
//
//    public static void main(String[] args) {
//        Ak317KgTrackReq trackReq = getInstance();
//        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.STANDARD]));
//        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.HIGH]));
//        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.SUPER]));
//        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
//        System.out.println(trackReq.getTrackUrl("38A1E141897E5E5A01B914A90F8A1EA9", AudioQuality.KEYS[AudioQuality.HI_RES]));
//    }
//}
