package net.doge.sdk.entity.artist.search;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.model.entity.NetArtistInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.StringUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ArtistSearchReq {
    // 关键词搜索歌手 API
    private final String SEARCH_ARTIST_API = SdkCommon.prefix + "/cloudsearch?type=100&keywords=%s&limit=%s&offset=%s";
    private final String SEARCH_ARTIST_KW_API = "http://www.kuwo.cn/api/www/search/searchArtistBykeyWord?key=%s&pn=%s&rn=%s&httpsStatus=1";
    // 关键词搜索歌手 API (咪咕)
    private final String SEARCH_ARTIST_MG_API = SdkCommon.prefixMg + "/search?type=singer&keyword=%s&pageNo=%s&pageSize=%s";
    // 关键词搜索歌手 API (千千)
    private final String SEARCH_ARTIST_QI_API = "https://music.91q.com/v1/search?appid=16073360&pageNo=%s&pageSize=%s&timestamp=%s&type=2&word=%s";
    // 关键词搜索声优 API (猫耳)
    private final String SEARCH_CV_ME_API = "https://www.missevan.com/sound/getsearch?s=%s&type=4&p=%s&page_size=%s";
    // 关键词搜索歌手 API (豆瓣)
    private final String SEARCH_ARTIST_DB_API = "https://movie.douban.com/celebrities/search?search_text=%s&start=%s";

    // 歌手图片 API (QQ)
    private final String ARTIST_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T001R500x500M000%s.jpg";

    /**
     * 根据关键词获取歌手
     */
    public CommonResult<NetArtistInfo> searchArtists(int src, String keyword, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetArtistInfo> artistInfos = new LinkedList<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = StringUtil.urlEncode(keyword);

        // 网易云
        Callable<CommonResult<NetArtistInfo>> searchArtists = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            String artistInfoBody = HttpRequest.get(String.format(SEARCH_ARTIST_API, encodedKeyword, limit, (page - 1) * limit))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject result = artistInfoJson.getJSONObject("result");
            if (result != null) {
                t = result.getIntValue("artistCount");
                JSONArray artistArray = result.getJSONArray("artists");
                if (artistArray != null) {
                    for (int i = 0, len = artistArray.size(); i < len; i++) {
                        JSONObject artistJson = artistArray.getJSONObject(i);

                        String artistId = artistJson.getString("id");
                        String artistName = artistJson.getString("name");
                        Integer albumNum = artistJson.getIntValue("albumSize");
                        Integer mvNum = artistJson.getIntValue("mvSize");
                        String coverImgThumbUrl = artistJson.getString("img1v1Url");

                        NetArtistInfo artistInfo = new NetArtistInfo();
                        artistInfo.setId(artistId);
                        artistInfo.setName(artistName);
                        artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                        artistInfo.setAlbumNum(albumNum);
                        artistInfo.setMvNum(mvNum);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                            artistInfo.setCoverImgThumb(coverImgThumb);
                        });
                        res.add(artistInfo);
                    }
                }
            }
            return new CommonResult<>(res, t);
        };

        // QQ
        Callable<CommonResult<NetArtistInfo>> searchArtistsQq = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            int lim = Math.min(40, limit);
            String artistInfoBody = HttpRequest.post(String.format(SdkCommon.qqSearchApi))
                    .body(String.format(SdkCommon.qqSearchJson, page, lim, keyword, 1))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
            int sum = data.getJSONObject("meta").getIntValue("sum");
            t = (sum * lim == 0 ? sum / lim : sum / lim + 1) * limit;
            JSONArray artistArray = data.getJSONObject("body").getJSONObject("singer").getJSONArray("list");
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("singerMID");
                String artistName = artistJson.getString("singerName");
                Integer songNum = artistJson.getIntValue("songNum");
                Integer albumNum = artistJson.getIntValue("albumNum");
                Integer mvNum = artistJson.getIntValue("mvNum");
                String coverImgThumbUrl = String.format(ARTIST_IMG_QQ_API, artistId);

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.QQ);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setSongNum(songNum);
                artistInfo.setAlbumNum(albumNum);
                artistInfo.setMvNum(mvNum);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(artistInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 酷我
        Callable<CommonResult<NetArtistInfo>> searchArtistsKw = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = SdkCommon.kwRequest(String.format(SEARCH_ARTIST_KW_API, encodedKeyword, page, limit)).execute();
            // 酷我有时候会崩，先验证是否请求成功
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String artistInfoBody = resp.body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject data = artistInfoJson.getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray artistArray = data.getJSONArray("list");
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("id");
                    String artistName = artistJson.getString("name");
                    Integer songNum = artistJson.getIntValue("musicNum");
                    String coverImgThumbUrl = artistJson.getString("pic300");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.KW);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setSongNum(songNum);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 咪咕
        Callable<CommonResult<NetArtistInfo>> searchArtistsMg = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            String artistInfoBody = HttpRequest.get(String.format(SEARCH_ARTIST_MG_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            // 咪咕可能接口异常，需要判空！
            if (data != null) {
                t = data.getIntValue("total");
                JSONArray artistArray = data.getJSONArray("list");
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("id");
                    String artistName = artistJson.getString("name");
                    Integer songNum = artistJson.getIntValue("songCount");
                    Integer albumNum = artistJson.getIntValue("albumCount");
                    String coverImgThumbUrl = artistJson.getString("picUrl");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.MG);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    artistInfo.setSongNum(songNum);
                    artistInfo.setAlbumNum(albumNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 千千
        Callable<CommonResult<NetArtistInfo>> searchArtistsQi = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            String artistInfoBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(SEARCH_ARTIST_QI_API, page, limit, System.currentTimeMillis(), encodedKeyword)))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray artistArray = data.getJSONArray("typeArtist");
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("artistCode");
                String artistName = artistJson.getString("name");
                Integer songNum = artistJson.getIntValue("trackTotal");
                String coverImgThumbUrl = artistJson.getString("pic");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.QI);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                artistInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(artistInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 猫耳
        Callable<CommonResult<NetArtistInfo>> searchCVsMe = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            String artistInfoBody = HttpRequest.get(String.format(SEARCH_CV_ME_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject info = artistInfoJson.getJSONObject("info");
            t = info.getJSONObject("pagination").getIntValue("count");
            JSONArray artistArray = info.getJSONArray("Datas");
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("id");
                String artistName = artistJson.getString("name");
                String coverImgThumbUrl = artistJson.getString("icon");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.ME);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(artistInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 豆瓣
        Callable<CommonResult<NetArtistInfo>> searchArtistsDb = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            String artistInfoBody = HttpRequest.get(String.format(SEARCH_ARTIST_DB_API, encodedKeyword, (page - 1) * 15))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            t = Integer.parseInt(doc.select("div.rr").first().text().split("共")[1]);
            t += t / 15 * 5;
            Elements result = doc.select("div.result");
            for (int i = 0, len = result.size(); i < len; i++) {
                Element artist = result.get(i);
                Element a = artist.select(".content a").first();
                Element img = artist.select(".pic img").first();

                String artistId = ReUtil.get("celebrity/(\\d+)/", a.attr("href"), 1);
                String artistName = a.text();
                String coverImgThumbUrl = img.attr("src");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.DB);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(artistInfo);
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetArtistInfo>>> taskList = new LinkedList<>();

        if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchArtists));
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchArtistsQq));
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchArtistsKw));
        if (src == NetMusicSource.MG || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchArtistsMg));
        if (src == NetMusicSource.QI || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchArtistsQi));
        if (src == NetMusicSource.ME || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchCVsMe));
        if (src == NetMusicSource.DB || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchArtistsDb));

        List<List<NetArtistInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetArtistInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        artistInfos.addAll(ListUtil.joinAll(rl));

        return new CommonResult<>(artistInfos, total.get());
    }
}