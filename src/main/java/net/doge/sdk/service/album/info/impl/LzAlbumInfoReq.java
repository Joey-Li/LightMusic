package net.doge.sdk.service.album.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

public class LzAlbumInfoReq {
    private static LzAlbumInfoReq instance;

    private LzAlbumInfoReq() {
    }

    public static LzAlbumInfoReq getInstance() {
        if (instance == null) instance = new LzAlbumInfoReq();
        return instance;
    }

    // 专辑歌曲 API (李志)
    private final String ALBUM_SONGS_LZ_API = "https://www.lizhinb.com/gequ/";

    /**
     * 根据专辑 id 补全专辑信息(包括封面图、描述)
     */
    public void fillAlbumInfo(NetAlbumInfo albumInfo) {
        GlobalExecutors.imageExecutor.execute(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(albumInfo.getCoverImgThumbUrl())));
        albumInfo.setDescription("");
    }

    /**
     * 根据专辑 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInAlbum(NetAlbumInfo albumInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;
        String id = albumInfo.getId();

        String albumSongBody = HttpRequest.get(ALBUM_SONGS_LZ_API)
                .executeAsStr();
        Document doc = Jsoup.parse(albumSongBody);
        Elements article = doc.select(String.format(".oa-album-card[data-album-id=\"%s\"]", id));
        String albumSongJsonStr = article.attr("data-tracks");
        JSONArray songArray = JSONArray.parseArray(albumSongJsonStr);
        total = songArray.size();
        for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
            JSONObject songJson = songArray.getJSONObject(i);

            String songId = songJson.getString("track_id");
            String name = songJson.getString("title");
            String artist = "李志";
            String albumName = songJson.getString("album");
            String albumId = songJson.getString("album_id");

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetResourceSource.LZ);
            musicInfo.setId(songId);
            musicInfo.setName(name);
            musicInfo.setArtist(artist);
            musicInfo.setAlbumName(albumName);
            musicInfo.setAlbumId(albumId);

            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}
