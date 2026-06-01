//package net.doge.sdk.service.music.info.impl.musicurl.track.nc;
//
//import com.alibaba.fastjson2.JSONObject;
//import net.doge.constant.core.media.AudioQuality;
//import net.doge.util.core.StringUtil;
//import net.doge.util.core.crypto.CryptoUtil;
//import net.doge.util.core.http.HttpRequest;
//import net.doge.util.core.log.LogUtil;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class RrvennNcTrackReq {
//    private static RrvennNcTrackReq instance;
//
//    private RrvennNcTrackReq() {
//        initMap();
//    }
//
//    public static RrvennNcTrackReq getInstance() {
//        if (instance == null) instance = new RrvennNcTrackReq();
//        return instance;
//    }
//
//    // 歌曲 URL 获取 API (网易云)
//    private final String SONG_URL_NC_API = "https://music.rrvenn.cn/api/api.php?action=music&url=%s&level=%s&type=json&timestamp=%s&signature=%s";
//
//    private Map<String, String> qualityMap = new HashMap<>();
//
//    private void initMap() {
//        // standard => 标准, exhigh => 极高, lossless => 无损, hires => Hi-Res, jyeffect => 高清环绕声, sky => 沉浸环绕声, jymaster => 超清母带
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.STANDARD], "standard");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HIGH], "exhigh");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.LOSSLESS], "lossless");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.HI_RES], "hires");
//        qualityMap.put("jyeffect", "jyeffect");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.ATMOSPHERE], "sky");
//        qualityMap.put(AudioQuality.KEYS[AudioQuality.MASTER], "jymaster");
//        qualityMap.put("dolby", "dolby");
//    }
//
//    /**
//     * 获取网易云音乐歌曲链接
//     *
//     * @param id      歌曲 id
//     * @param quality 品质
//     * @return
//     */
//    public String getTrackUrl(String id, String quality) {
//        try {
//            long ts = System.currentTimeMillis() / 1000;
//            String sign = CryptoUtil.md5(ts + "kxz_163music_secret_key_2024");
//            String songBody = HttpRequest.get(String.format(SONG_URL_NC_API, id, qualityMap.get(quality), ts, sign))
//                    .executeAsStr();
//            JSONObject data = JSONObject.parseObject(songBody);
//            String trackUrl = data.getString("url");
//            if (StringUtil.isEmpty(trackUrl)) return "";
//            return trackUrl;
//        } catch (Exception e) {
//            LogUtil.error(e);
//            return "";
//        }
//    }
//
//    public static void main(String[] args) {
//        RrvennNcTrackReq trackReq = getInstance();
//        System.out.println(trackReq.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.STANDARD]));
//        System.out.println(trackReq.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.HIGH]));
//        System.out.println(trackReq.getTrackUrl("2600493765", AudioQuality.KEYS[AudioQuality.LOSSLESS]));
//    }
//}
