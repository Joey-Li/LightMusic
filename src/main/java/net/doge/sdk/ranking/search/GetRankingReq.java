package net.doge.sdk.ranking.search;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import net.doge.constants.GlobalExecutors;
import net.doge.constants.NetMusicSource;
import net.doge.models.entities.NetRankingInfo;
import net.doge.models.server.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.utils.ListUtil;
import net.doge.utils.TimeUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class GetRankingReq {
    // 获取榜单 API
    private final String GET_RANKING_API = SdkCommon.prefix + "/toplist";
    // 获取榜单 API (酷狗)
    private final String GET_RANKING_KG_API = "http://mobilecdnbj.kugou.com/api/v3/rank/list?apiver=6&area_code=1";
    // 获取榜单 API (QQ)
    private final String GET_RANKING_QQ_API = SdkCommon.prefixQQ33 + "/top/category";
    // 获取榜单 API 2 (QQ)
    private final String GET_RANKING_QQ_API_2
            = "https://c.y.qq.com/v8/fcg-bin/fcg_myqq_toplist.fcg?g_tk=1928093487&inCharset=utf-8&outCharset=utf-8&notice=0&format=json&uin=0&needNewCode=1&platform=h5";
    private final String GET_RANKING_KW_API = "http://www.kuwo.cn/api/www/bang/bang/bangMenu?&httpsStatus=1";
    // 获取榜单 API 2 (酷我)
    private final String GET_RANKING_KW_API_2 = "http://qukudata.kuwo.cn/q.k?op=query&cont=tree&node=2&pn=0&rn=1000&fmt=json&level=2";
    // 获取推荐榜单 API (酷我)
//    private final String GET_REC_RANKING_KW_API = "http://www.kuwo.cn/api/www/bang/index/bangList?&httpsStatus=1";
    // 获取榜单 API (咪咕)
    private final String GET_RANKING_MG_API = "https://app.c.nf.migu.cn/MIGUM3.0/v1.0/template/rank-list";
    // 获取榜单 API (千千)
    private final String GET_RANKING_QI_API = "https://music.91q.com/v1/bd/category?appid=16073360&timestamp=%s";
    // 获取榜单 API (猫耳)
    private final String GET_RANKING_ME_API = "https://www.missevan.com/mobileWeb/albumList";
    
    /**
     * 获取所有榜单
     */
    public CommonResult<NetRankingInfo> getRankings(int src) {
        AtomicInteger total = new AtomicInteger();
        List<NetRankingInfo> rankingInfos = new LinkedList<>();

        // 网易云
        Callable<CommonResult<NetRankingInfo>> getRankings = () -> {
            LinkedList<NetRankingInfo> res = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(String.format(GET_RANKING_API))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONArray rankingArray = rankingInfoJson.getJSONArray("list");
            for (int i = 0, len = rankingArray.size(); i < len; i++) {
                JSONObject rankingJson = rankingArray.getJSONObject(i);

                String rankingId = rankingJson.getString("id");
                String rankingName = rankingJson.getString("name");
                String coverImgUrl = rankingJson.getString("coverImgUrl");
                String description = rankingJson.getString("description");
                Long playCount = rankingJson.getLong("playCount");
                String updateFre = rankingJson.getString("updateFrequency");
                String updateTime = TimeUtil.msToDate(rankingJson.getLong("trackUpdateTime"));

                NetRankingInfo rankingInfo = new NetRankingInfo();
                rankingInfo.setId(rankingId);
                rankingInfo.setName(rankingName);
                rankingInfo.setCoverImgUrl(coverImgUrl);
                rankingInfo.setDescription(description.equals("null") ? "" : description);
                rankingInfo.setPlayCount(playCount);
                rankingInfo.setUpdateFre(updateFre);
                rankingInfo.setUpdateTime(updateTime);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                    rankingInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(rankingInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 酷狗
        Callable<CommonResult<NetRankingInfo>> getRankingsKg = () -> {
            LinkedList<NetRankingInfo> res = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(String.format(GET_RANKING_KG_API))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONArray rankingArray = rankingInfoJson.getJSONObject("data").getJSONArray("info");
            for (int i = 0, len = rankingArray.size(); i < len; i++) {
                JSONObject rankingJson = rankingArray.getJSONObject(i);

                String rankingId = rankingJson.getString("rankid");
                String rankingName = rankingJson.getString("rankname");
                String coverImgUrl = rankingJson.getString("banner_9").replace("/{size}", "");
                String description = rankingJson.getString("intro");
                String updateFre = rankingJson.getString("update_frequency");
                Long playCount = rankingJson.getLong("play_times");

                NetRankingInfo rankingInfo = new NetRankingInfo();
                rankingInfo.setSource(NetMusicSource.KG);
                rankingInfo.setId(rankingId);
                rankingInfo.setName(rankingName);
                rankingInfo.setCoverImgUrl(coverImgUrl);
                rankingInfo.setDescription(description);
                rankingInfo.setUpdateFre(updateFre);
                rankingInfo.setPlayCount(playCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                    rankingInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(rankingInfo);
            }
            return new CommonResult<>(res, t);
        };

        // QQ
        Callable<CommonResult<NetRankingInfo>> getRankingsQq = () -> {
            LinkedList<NetRankingInfo> res = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(String.format(GET_RANKING_QQ_API))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONArray data = rankingInfoJson.getJSONArray("data");
            for (int i = 0, len = data.size(); i < len; i++) {
                JSONArray rankingArray = data.getJSONObject(i).getJSONArray("list");
                for (int j = 0, s = rankingArray.size(); j < s; j++) {
                    JSONObject rankingJson = rankingArray.getJSONObject(j);

                    String rankingId = rankingJson.getString("topId");
                    String rankingName = rankingJson.getString("label");
                    String coverImgUrl = rankingJson.getString("picUrl").replaceFirst("http:", "https:");
                    Long playCount = rankingJson.getLong("listenNum");
                    String updateTime = rankingJson.getString("updateTime");

                    NetRankingInfo rankingInfo = new NetRankingInfo();
                    rankingInfo.setSource(NetMusicSource.QQ);
                    rankingInfo.setId(rankingId);
                    rankingInfo.setName(rankingName);
                    rankingInfo.setCoverImgUrl(coverImgUrl);
                    rankingInfo.setPlayCount(playCount);
                    rankingInfo.setUpdateTime(updateTime);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                        rankingInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(rankingInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        Callable<CommonResult<NetRankingInfo>> getRankingsQq2 = () -> {
            LinkedList<NetRankingInfo> res = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(String.format(GET_RANKING_QQ_API_2))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONArray data = rankingInfoJson.getJSONObject("data").getJSONArray("topList");
            for (int i = 0, len = data.size(); i < len; i++) {
                JSONObject rankingJson = data.getJSONObject(i);

                String rankingId = rankingJson.getString("id");
                String rankingName = rankingJson.getString("topTitle");
                String coverImgUrl = rankingJson.getString("picUrl").replaceFirst("http:", "https:");
                Long playCount = rankingJson.getLong("listenCount");

                NetRankingInfo rankingInfo = new NetRankingInfo();
                rankingInfo.setSource(NetMusicSource.QQ);
                rankingInfo.setId(rankingId);
                rankingInfo.setName(rankingName);
                rankingInfo.setCoverImgUrl(coverImgUrl);
                rankingInfo.setPlayCount(playCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                    rankingInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(rankingInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 酷我
        // 所有榜单
        Callable<CommonResult<NetRankingInfo>> getRankingsKw = () -> {
            LinkedList<NetRankingInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = SdkCommon.kwRequest(String.format(GET_RANKING_KW_API)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String rankingInfoBody = resp.body();
                JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
                JSONArray data = rankingInfoJson.getJSONArray("data");
                for (int i = 0, len = data.size(); i < len; i++) {
                    JSONArray rankingArray = data.getJSONObject(i).getJSONArray("list");
                    for (int j = 0, s = rankingArray.size(); j < s; j++) {
                        JSONObject rankingJson = rankingArray.getJSONObject(j);

                        String rankingId = rankingJson.getString("sourceid");
                        String rankingName = rankingJson.getString("name");
                        String coverImgUrl = rankingJson.getString("pic");
                        String description = rankingJson.getString("intro");
                        String updateFre = rankingJson.getString("pub");

                        NetRankingInfo rankingInfo = new NetRankingInfo();
                        rankingInfo.setSource(NetMusicSource.KW);
                        rankingInfo.setId(rankingId);
                        rankingInfo.setName(rankingName);
                        rankingInfo.setCoverImgUrl(coverImgUrl);
                        rankingInfo.setUpdateFre(updateFre);
                        rankingInfo.setDescription(description);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                            rankingInfo.setCoverImgThumb(coverImgThumb);
                        });

                        res.add(rankingInfo);
                    }
                }
            }
            return new CommonResult<>(res, t);
        };
        Callable<CommonResult<NetRankingInfo>> getRankingsKw2 = () -> {
            LinkedList<NetRankingInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(GET_RANKING_KW_API_2)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String rankingInfoBody = resp.body();
                JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
                JSONArray data = rankingInfoJson.getJSONArray("child");
                for (int i = 0, len = data.size(); i < len; i++) {
                    JSONObject rankingJson = data.getJSONObject(i);

                    String rankingId = rankingJson.getString("sourceid");
                    String rankingName = rankingJson.getString("name");
                    String coverImgUrl = rankingJson.getString("pic");
                    String updateTime = rankingJson.getString("info").replaceFirst("更新于", "");

                    NetRankingInfo rankingInfo = new NetRankingInfo();
                    rankingInfo.setSource(NetMusicSource.KW);
                    rankingInfo.setId(rankingId);
                    rankingInfo.setName(rankingName);
                    rankingInfo.setCoverImgUrl(coverImgUrl);
                    rankingInfo.setUpdateTime(updateTime);
                    rankingInfo.setDescription("");
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                        rankingInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(rankingInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 推荐榜单
//        Callable<CommonResult<NetRankingInfo>> getRecRankingsKw = () -> {
//            LinkedList<NetRankingInfo> res = new LinkedList<>();
//            Integer t = 0;
//
//            HttpResponse resp = kwRequest(String.format(GET_REC_RANKING_KW_API))
//                    .header(Header.REFERER, "http://www.kuwo.cn/rankList")
//                    .execute();
//            if (resp.getStatus() == HttpStatus.HTTP_OK) {
//                String rankingInfoBody = resp.body();
//                JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
//                JSONArray data = rankingInfoJson.getJSONArray("data");
//                for (int i = 0, len = data.size(); i < len; i++) {
//                    JSONObject rankingJson = data.getJSONObject(i);
//
//                    String rankingId = rankingJson.getString("id");
//                    String rankingName = rankingJson.getString("name");
//                    String coverImgUrl = rankingJson.getString("pic");
//                    String updateTime = rankingJson.getString("pub");
//                    String desc = "";
//
//                    NetRankingInfo rankingInfo = new NetRankingInfo();
//                    rankingInfo.setSource(NetMusicSource.KW);
//                    rankingInfo.setId(rankingId);
//                    rankingInfo.setName(rankingName);
//                    rankingInfo.setCoverImgUrl(coverImgUrl);
//                    rankingInfo.setUpdateTime(updateTime);
//                    rankingInfo.setDescription(desc);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
//                        rankingInfo.setCoverImgThumb(coverImgThumb);
//                    });
//
//                    res.add(rankingInfo);
//                }
//            }
//            return new CommonResult<>(res, t);
//        };

        // 咪咕
        Callable<CommonResult<NetRankingInfo>> getRankingsMg = () -> {
            LinkedList<NetRankingInfo> res = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(String.format(GET_RANKING_MG_API))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONObject data = rankingInfoJson.getJSONObject("data");
            JSONArray contentItemList = data.getJSONArray("contentItemList");
            for (int i = 0, len = contentItemList.size(); i < len; i++) {
                JSONArray itemList = contentItemList.getJSONObject(i).optJSONArray("itemList");
                if (itemList != null) {
                    for (int j = 0, s = itemList.size(); j < s; j++) {
                        JSONObject item = itemList.getJSONObject(j);

                        String template = item.getString("template");
                        if (template.equals("row1") || template.equals("grid1")) {
                            JSONObject param = item.getJSONObject("displayLogId").getJSONObject("param");

                            String rankingId = param.getString("rankId");
                            String rankingName = param.getString("rankName");
                            String coverImgUrl = item.getString("imageUrl");
                            String updateFre = item.getJSONArray("barList").getJSONObject(0).getString("title");

                            NetRankingInfo rankingInfo = new NetRankingInfo();
                            rankingInfo.setSource(NetMusicSource.MG);
                            rankingInfo.setId(rankingId);
                            rankingInfo.setName(rankingName);
                            rankingInfo.setUpdateFre(updateFre);
                            rankingInfo.setCoverImgUrl(coverImgUrl);
//                        rankingInfo.setPlayCount(playCount);
//                        rankingInfo.setUpdateTime(updateTime);
                            GlobalExecutors.imageExecutor.execute(() -> {
                                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                                rankingInfo.setCoverImgThumb(coverImgThumb);
                            });

                            res.add(rankingInfo);
                        }
                    }
                }
            }
            return new CommonResult<>(res, t);
        };

        // 千千
        Callable<CommonResult<NetRankingInfo>> getRankingsQi = () -> {
            LinkedList<NetRankingInfo> res = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(GET_RANKING_QI_API, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONArray rankingArray = rankingInfoJson.getJSONArray("data");
            for (int i = 0, len = rankingArray.size(); i < len; i++) {
                JSONObject rankingJson = rankingArray.getJSONObject(i);

                String rankingId = rankingJson.getString("bdid");
                String rankingName = rankingJson.getString("title");
                String coverImgUrl = rankingJson.getString("pic");

                NetRankingInfo rankingInfo = new NetRankingInfo();
                rankingInfo.setSource(NetMusicSource.QI);
                rankingInfo.setId(rankingId);
                rankingInfo.setName(rankingName);
                rankingInfo.setCoverImgUrl(coverImgUrl);
                rankingInfo.setDescription("");
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                    rankingInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(rankingInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 猫耳
        Callable<CommonResult<NetRankingInfo>> getRankingsMe = () -> {
            LinkedList<NetRankingInfo> res = new LinkedList<>();
            Integer t = 0;

            String rankingInfoBody = HttpRequest.get(String.format(GET_RANKING_ME_API))
                    .execute()
                    .body();
            JSONObject rankingInfoJson = JSONObject.fromObject(rankingInfoBody);
            JSONArray rankingArray = rankingInfoJson.getJSONArray("info");
            for (int i = 0, len = rankingArray.size(); i < len; i++) {
                JSONObject rankingJson = rankingArray.getJSONObject(i).getJSONObject("album");

                String rankingId = rankingJson.getString("id");
                String rankingName = rankingJson.getString("title");
                String coverImgUrl = rankingJson.getString("front_cover");
                String updateTime = TimeUtil.msToDate(rankingJson.getLong("last_update_time") * 1000);
                Long playCount = rankingJson.getLong("view_count");

                NetRankingInfo rankingInfo = new NetRankingInfo();
                rankingInfo.setSource(NetMusicSource.ME);
                rankingInfo.setId(rankingId);
                rankingInfo.setName(rankingName);
                rankingInfo.setCoverImgUrl(coverImgUrl);
                rankingInfo.setUpdateTime(updateTime);
                rankingInfo.setPlayCount(playCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                    rankingInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(rankingInfo);
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetRankingInfo>>> taskList = new LinkedList<>();

        if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankings));
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsKg));
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsQq));
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsQq2));
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsKw));
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsKw2));
//        taskList.add(GlobalExecutors.requestExecutor.submit(getRecRankingsKw));
        if (src == NetMusicSource.MG || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsMg));
        if (src == NetMusicSource.QI || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsQi));
        if (src == NetMusicSource.ME || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(getRankingsMe));

        List<List<NetRankingInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetRankingInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        rankingInfos.addAll(ListUtil.joinAll(rl));

        return new CommonResult<>(rankingInfos, total.get());
    }
}
