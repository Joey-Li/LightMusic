package net.doge.sdk.entity.music.info;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import net.doge.constant.player.Format;
import net.doge.constant.system.NetMusicSource;
import net.doge.model.entity.NetMusicInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.MusicCandidate;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.entity.music.search.MusicSearchReq;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.common.StringUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class MusicUrlReq {
    // 歌曲 URL 获取 API
    private final String GET_SONG_URL_API_NEW = SdkCommon.prefix + "/song/url/v1?id=%s&level=jymaster";
    private final String GET_SONG_URL_API = SdkCommon.prefix + "/song/url?id=%s";
    // 歌曲 URL 获取 API (QQ)
//    private final String GET_SONG_URL_QQ_API = prefixQQ33 + "/song/url?id=%s";
    // 歌曲 URL 获取 API (酷我)
//    private final String GET_SONG_URL_KW_API = "http://www.kuwo.cn/api/v1/www/music/playUrl?mid=%s&type=music&httpsStatus=1";
//    private final String GET_SONG_URL_KW_API = "http://antiserver.kuwo.cn/anti.s?type=convert_url&format=mp3&response=url&rid=%s";
    private final String GET_SONG_URL_KW_API = "http://www.kuwo.cn/api/v1/www/music/playUrl?mid=%s&type=convert_url3&br=320kmp3";
    // 歌曲 URL 获取 API (千千)
    private final String GET_SONG_URL_QI_API = "https://music.91q.com/v1/song/tracklink?TSID=%s&appid=16073360&timestamp=%s";
    // 歌曲 URL 获取 API (喜马拉雅)
    private final String GET_SONG_URL_XM_API = "https://www.ximalaya.com/revision/play/v1/audio?id=%s&ptype=1";
    // 歌曲 URL 获取 API (哔哩哔哩)
    private final String GET_SONG_URL_BI_API = "https://www.bilibili.com/audio/music-service-c/web/url?sid=%s";
    // 歌曲 URL 获取 API (5sing)
    private final String GET_SONG_URL_FS_API = "http://service.5sing.kugou.com/song/getsongurl?songtype=%s&songid=%s";

    // 歌曲信息 API (酷狗)
    private final String SINGLE_SONG_DETAIL_KG_API = "https://www.kugou.com/yy/index.php?r=play/getdata&album_audio_id=%s";
    // 歌曲信息 API (咪咕)
    private final String SINGLE_SONG_DETAIL_MG_API = SdkCommon.prefixMg + "/song?cid=%s";
    // 歌曲信息 API (音乐磁场)
    private final String SINGLE_SONG_DETAIL_HF_API = "https://www.hifini.com/thread-%s.htm";
    // 歌曲信息 API (咕咕咕音乐)
    private final String SINGLE_SONG_DETAIL_GG_API = "http://www.gggmusic.com/thread-%s.htm";
    // 歌曲信息 API (猫耳)
    private final String SINGLE_SONG_DETAIL_ME_API = "https://www.missevan.com/sound/getsound?soundid=%s";

    /**
     * 补充 NetMusicInfo 的 url
     */
    public void fillMusicUrl(NetMusicInfo musicInfo) {
        // 歌曲信息是完整的
        if (musicInfo.isIntegrated()) return;

        String songId = musicInfo.getId();
        int source = musicInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String url = fetchMusicUrl(songId, NetMusicSource.NET_CLOUD);
            // 排除试听部分，直接换源
            if (StringUtil.isNotEmpty(url)) musicInfo.setUrl(url);
            else fillAvailableMusicUrl(musicInfo);
            // 网易云音乐里面有的电台节目是 flac 格式！
            if (url.endsWith(Format.FLAC)) musicInfo.setFormat(Format.FLAC);
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            // 排除试听部分，直接换源
            String url = fetchMusicUrl(songId, NetMusicSource.KG);
            if (StringUtil.isNotEmpty(url)) musicInfo.setUrl(url);
            else fillAvailableMusicUrl(musicInfo);
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            // 排除试听部分，直接换源
            String url = fetchMusicUrl(songId, NetMusicSource.QQ);
            if (StringUtil.isNotEmpty(url)) musicInfo.setUrl(url);
            else fillAvailableMusicUrl(musicInfo);
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            // 无链接，直接换源
            String url = fetchMusicUrl(songId, NetMusicSource.KW);
            if (StringUtil.isNotEmpty(url)) musicInfo.setUrl(url);
            else fillAvailableMusicUrl(musicInfo);
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            // 无链接，直接换源
            String url = fetchMusicUrl(songId, NetMusicSource.MG);
            if (StringUtil.isNotEmpty(url)) musicInfo.setUrl(url);
            else fillAvailableMusicUrl(musicInfo);
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            // 无链接或试听，直接换源
            String url = fetchMusicUrl(songId, NetMusicSource.QI);
            if (StringUtil.isNotEmpty(url)) musicInfo.setUrl(url);
            else fillAvailableMusicUrl(musicInfo);
        }

        // 音乐磁场
        else if (source == NetMusicSource.HF) {
            // 无链接或试听，直接换源
            String url = fetchMusicUrl(songId, NetMusicSource.HF);
            if (url.contains(".m4a")) musicInfo.setFormat(Format.M4A);
            if (StringUtil.isNotEmpty(url)) musicInfo.setUrl(url);
            else fillAvailableMusicUrl(musicInfo);
        }

        // 咕咕咕音乐
        else if (source == NetMusicSource.GG) {
            // 无链接或试听，直接换源
            String url = fetchMusicUrl(songId, NetMusicSource.GG);
            if (url.contains(".m4a")) musicInfo.setFormat(Format.M4A);
            if (StringUtil.isNotEmpty(url)) musicInfo.setUrl(url);
            else fillAvailableMusicUrl(musicInfo);
        }

        // 5sing
        else if (source == NetMusicSource.FS) {
            // 无链接或试听，直接换源
            String url = fetchMusicUrl(songId, NetMusicSource.FS);
            if (StringUtil.isNotEmpty(url)) musicInfo.setUrl(url);
            else fillAvailableMusicUrl(musicInfo);
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            String url = fetchMusicUrl(songId, NetMusicSource.XM);
            if (url.contains(".m4a")) musicInfo.setFormat(Format.M4A);
            musicInfo.setUrl(url);
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String url = fetchMusicUrl(songId, NetMusicSource.ME);
            musicInfo.setUrl(url);
            if (url.contains(".m4a")) musicInfo.setFormat(Format.M4A);
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            String url = fetchMusicUrl(songId, NetMusicSource.BI);
            if (url.contains(".m4a")) musicInfo.setFormat(Format.M4A);
            musicInfo.setUrl(url);
        }
    }

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public String fetchMusicUrl(String songId, int source) {
        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            // 首选高音质接口
            String songBody = HttpRequest.get(String.format(GET_SONG_URL_API_NEW, songId))
                    .execute()
                    .body();
            JSONArray data = JSONObject.parseObject(songBody).getJSONArray("data");
            // 次选普通音质
            if (data == null) {
                songBody = HttpRequest.get(String.format(GET_SONG_URL_API, songId))
                        .execute()
                        .body();
                data = JSONObject.parseObject(songBody).getJSONArray("data");
            }
            if (data != null) {
                JSONObject urlJson = data.getJSONObject(0);
                // 排除试听部分，直接换源
                if (urlJson.getJSONObject("freeTrialInfo") == null) {
                    String url = urlJson.getString("url");
                    if (StringUtil.isNotEmpty(url)) return url;
                }
            }
        }

        // 酷狗(歌曲详情能直接请求到 url，不需要单独调用此方法)
        else if (source == NetMusicSource.KG) {
            // 酷狗接口请求需要带上 cookie ！
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_KG_API, songId))
                    .header(Header.COOKIE, SdkCommon.COOKIE)
                    .execute()
                    .body();
            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
            String url = data.getString("play_url");
            if (data.getIntValue("is_free_part") == 0) return url;
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String playUrlBody = HttpRequest.get(SdkCommon.qqSearchApi + "?format=json&data=" +
                            StringUtil.encode(String.format("{\"req_0\":{\"module\":\"vkey.GetVkeyServer\",\"method\"" +
                                    ":\"CgiGetVkey\",\"param\":{\"filename\":[\"M500%s%s.mp3\"],\"guid\":\"10000\"" +
                                    ",\"songmid\":[\"%s\"],\"songtype\":[0],\"uin\":\"0\",\"loginflag\":1,\"platform\":\"20\"}}" +
                                    ",\"loginUin\":\"0\",\"comm\":{\"uin\":\"0\",\"format\":\"json\",\"ct\":24,\"cv\":0}}", songId, songId, songId)))
                    .execute()
                    .body();
            JSONObject urlJson = JSONObject.parseObject(playUrlBody);
            JSONObject data = urlJson.getJSONObject("req_0").getJSONObject("data");
            String sip = data.getJSONArray("sip").getString(0);
            String url = data.getJSONArray("midurlinfo").getJSONObject(0).getString("purl");
            return url.isEmpty() ? "" : sip + url;
        }

        // 酷我(解锁付费音乐)
        else if (source == NetMusicSource.KW) {
//            String urlBody = HttpRequest.get(String.format(GET_SONG_URL_KW_API, songId))
//                    .header(Header.REFERER, "https://www.kuwo.cn/")
//                    .execute()
//                    .body();
//            return urlBody;
            String urlBody = HttpRequest.get(String.format(GET_SONG_URL_KW_API, songId))
                    .execute()
                    .body();
            JSONObject urlJson = JSONObject.parseObject(urlBody);
            JSONObject data = urlJson.getJSONObject("data");
            return data.getString("url");
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_MG_API, songId))
                    .execute()
                    .body();
            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
            return data.getString("320");
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String playUrlBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(GET_SONG_URL_QI_API, songId, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject urlJson = JSONObject.parseObject(playUrlBody).getJSONObject("data");
            // 排除试听部分，直接换源
            if (urlJson.getIntValue("isVip") == 0) {
                String url = urlJson.getString("path");
                if (url.isEmpty()) url = urlJson.getJSONObject("trail_audio_info").getString("path");
                return url;
            }
        }

        // 音乐磁场
        else if (source == NetMusicSource.HF) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_HF_API, songId))
                    .cookie(SdkCommon.HF_COOKIE)
                    .execute()
                    .body();
            Document doc = Jsoup.parse(songBody);
            String dataStr = ReUtil.get("music: \\[.*?(\\{.*?\\}).*?\\]", doc.html(), 1);
            if (StringUtil.isEmpty(dataStr)) return "";
            JSONObject data = JSONObject.parseObject(dataStr);
            String url = data.getString("url").replace(" ", "%20");
            if (url.startsWith("http")) return url;
            return SdkUtil.getRedirectUrl("https://www.hifini.com/" + url);
        }

        // 咕咕咕音乐
        else if (source == NetMusicSource.GG) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_GG_API, songId))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(songBody);
            String dataStr = ReUtil.get("(?:audio|music): \\[.*?(\\{.*?\\}).*?\\]", doc.html(), 1);

            if (StringUtil.isEmpty(dataStr)) return "";
            String base64Pattern = "base64_decode\\(\"(.*?)\"\\)";
            String base64Str = ReUtil.get(base64Pattern, dataStr, 1);
            if (StringUtil.isNotEmpty(base64Str))
                dataStr = dataStr.replaceFirst(base64Pattern, String.format("\"%s\"", StringUtil.base64Decode(base64Str)));

            JSONObject data = JSONObject.parseObject(dataStr);
            String url = data.getString("url").replace(" ", "%20");
            if (url.startsWith("http")) return url;
            else {
                try {
                    // 获取重定向之后的 url
                    String startUrl = "http://www.gggmusic.com" + url;
                    HttpURLConnection conn = (HttpURLConnection) new URL(startUrl).openConnection();
                    conn.setInstanceFollowRedirects(false);
                    conn.setConnectTimeout(SdkCommon.TIME_OUT);
                    String newUrl = conn.getHeaderField("Location");
                    return StringUtil.isEmpty(newUrl) ? startUrl : newUrl;
                } catch (IOException e) {
                    return "";
                }
            }
        }

        // 5sing
        else if (source == NetMusicSource.FS) {
            String[] sp = songId.split("_");
            String songBody = HttpRequest.get(String.format(GET_SONG_URL_FS_API, sp[0], sp[1]))
                    .execute()
                    .body();
            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
            String url = data.getString("squrl");
            if (StringUtil.isEmpty(url)) url = data.getString("hqurl");
            if (StringUtil.isEmpty(url)) url = data.getString("lqurl");
            return url;
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            String playUrlBody = HttpRequest.get(String.format(GET_SONG_URL_XM_API, songId))
                    .execute()
                    .body();
            JSONObject urlJson = JSONObject.parseObject(playUrlBody);
            String url = urlJson.getJSONObject("data").getString("src");
            return url;
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_ME_API, songId))
                    .execute()
                    .body();
            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("info").getJSONObject("sound");
            String url = data.getString("soundurl");
            return url;
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            String playUrlBody = HttpRequest.get(String.format(GET_SONG_URL_BI_API, songId))
                    .cookie(SdkCommon.BI_COOKIE)
                    .execute()
                    .body();
            JSONObject urlJson = JSONObject.parseObject(playUrlBody);
            String url = urlJson.getJSONObject("data").getJSONArray("cdns").getString(0);
            return url;
        }

        return "";
    }

    /**
     * 歌曲换源
     *
     * @param musicInfo
     * @return
     */
    public void fillAvailableMusicUrl(NetMusicInfo musicInfo) {
        CommonResult<NetMusicInfo> result = new MusicSearchReq().searchMusic(NetMusicSource.ALL, 0, "默认", musicInfo.toKeywords(), 10, 1);
        List<NetMusicInfo> data = result.data;
        List<MusicCandidate> candidates = new LinkedList<>();
        MusicInfoReq musicInfoReq = new MusicInfoReq();
        for (int i = 0, size = data.size(); i < size; i++) {
            NetMusicInfo info = data.get(i);
            // 部分歌曲没有时长，先填充时长，准备判断
            if (!info.hasDuration()) musicInfoReq.fillDuration(info);
            double nameSimi = StringUtil.similar(info.getName(), musicInfo.getName());
            double artistSimi = StringUtil.similar(info.getArtist(), musicInfo.getArtist());
            double albumSimi = StringUtil.similar(info.getAlbumName(), musicInfo.getAlbumName());
            // 匹配依据：歌名、歌手相似度，时长之差绝对值。如果合适，纳入候选者
            if (info.equals(musicInfo)
                    || nameSimi == 0
                    || artistSimi == 0
                    || info.hasDuration() && musicInfo.hasDuration() && Math.abs(info.getDuration() - musicInfo.getDuration()) > 3)
                continue;
            double weight = nameSimi + artistSimi + albumSimi;
            candidates.add(new MusicCandidate(info, weight));
        }
        // 将所有候选的匹配按照相关度排序
        candidates.sort((c1, c2) -> Double.compare(c2.weight, c1.weight));
        for (MusicCandidate candidate : candidates) {
            NetMusicInfo info = candidate.musicInfo;
            String url = fetchMusicUrl(info.getId(), info.getSource());
            if (StringUtil.isNotEmpty(url)) {
                if (url.contains(".m4a")) musicInfo.setFormat(Format.M4A);
                musicInfo.setUrl(url);
                if (!musicInfo.hasDuration()) musicInfo.setDuration(info.getDuration());
                return;
            }
        }
    }
}
