package net.doge.sdk.service.album.rcmd.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetAlbumInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class LzNewAlbumReq {
    private static LzNewAlbumReq instance;

    private LzNewAlbumReq() {
    }

    public static LzNewAlbumReq getInstance() {
        if (instance == null) instance = new LzNewAlbumReq();
        return instance;
    }

    // 专辑 API (李志)
    private final String ALBUM_LZ_API = "https://www.lizhinb.com/gequ/";

    /**
     * 专辑
     */
    public CommonResult<NetAlbumInfo> getAlbums(int page, int limit) {
        List<NetAlbumInfo> r = new LinkedList<>();
        int t;

        String albumInfoBody = HttpRequest.get(ALBUM_LZ_API)
                .executeAsStr();
        Document doc = Jsoup.parse(albumInfoBody);
        Elements albums = doc.select(".oa-album-card");
        t = albums.size();
        for (int i = (page - 1) * limit, len = Math.min(page * limit, albums.size()); i < len; i++) {
            Element album = albums.get(i);
            Elements title = album.select(".oa-album-card-title");
            Elements count = album.select(".oa-album-card-count");
            Elements img = album.select(".oa-album-cover-img");

            String albumId = album.attr("data-album-id");
            String albumName = title.text();
            String artist = "李志";
            Integer songNum = Integer.parseInt(RegexUtil.getGroup1("(\\d+) 首歌", count.text()));
            String coverImgThumbUrl = img.attr("src");

            NetAlbumInfo albumInfo = new NetAlbumInfo();
            albumInfo.setSource(NetResourceSource.LZ);
            albumInfo.setId(albumId);
            albumInfo.setName(albumName);
            albumInfo.setArtist(artist);
            albumInfo.setSongNum(songNum);
            albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                albumInfo.setCoverImgThumb(coverImgThumb);
            });

            r.add(albumInfo);
        }
        return new CommonResult<>(r, t);
    }
}
