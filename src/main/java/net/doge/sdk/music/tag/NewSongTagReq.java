package net.doge.sdk.music.tag;

import cn.hutool.http.HttpRequest;
import net.doge.constants.GlobalExecutors;
import net.doge.constants.Tags;
import net.doge.sdk.common.SdkCommon;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class NewSongTagReq {
    // 曲风 API
    private final String STYLE_API = SdkCommon.prefix + "/style/list";
    
    /**
     * 加载新歌标签
     *
     * @return
     */
    public void initNewSongTag() {
        // 网易云 网易云 酷狗 QQ 音乐磁场 咕咕咕音乐
        Tags.newSongTag.put("默认", new String[]{"0", "", "1", "0", "", ""});

        Tags.newSongTag.put("华语", new String[]{"7", "", "1", "0", "forum-1", "forum-1"});
        Tags.newSongTag.put("内地", new String[]{"", "", "", "1", "", ""});
        Tags.newSongTag.put("港台", new String[]{"", "", "", "2", "", ""});
        Tags.newSongTag.put("欧美", new String[]{"96", "", "2", "3", "forum-10", "forum-3"});
        Tags.newSongTag.put("韩国", new String[]{"16", "", "4", "4", "", ""});
        Tags.newSongTag.put("日本", new String[]{"8", "", "5", "5", "", ""});
        Tags.newSongTag.put("日韩", new String[]{"", "", "3", "", "forum-15", "forum-7"});

        // 音乐磁场
        Tags.newSongTag.put("Remix", new String[]{"", "", "", "", "forum-11", ""});
        Tags.newSongTag.put("纯音乐", new String[]{"", "", "", "", "forum-12", ""});
        Tags.newSongTag.put("异次元", new String[]{"", "", "", "", "forum-13", ""});
        Tags.newSongTag.put("特供", new String[]{"", "", "", "", "forum-17", ""});
        Tags.newSongTag.put("百科", new String[]{"", "", "", "", "forum-18", ""});
        Tags.newSongTag.put("站务", new String[]{"", "", "", "", "forum-9", ""});

        // 咕咕咕音乐
        Tags.newSongTag.put("音乐分享区", new String[]{"", "", "", "", "", "forum-12"});
        Tags.newSongTag.put("伤感", new String[]{"", "", "", "", "", "forum-8"});
        Tags.newSongTag.put("粤语", new String[]{"", "", "", "", "", "forum-2"});
        Tags.newSongTag.put("青春", new String[]{"", "", "", "", "", "forum-5"});
        Tags.newSongTag.put("分享", new String[]{"", "", "", "", "", "forum-11"});
        Tags.newSongTag.put("温柔男友音", new String[]{"", "", "", "", "", "forum-10"});
        Tags.newSongTag.put("DJ", new String[]{"", "", "", "", "", "forum-9"});

        final int c = 6;
        // 网易云曲风
        Runnable initNewSongTag = () -> {
            String tagBody = HttpRequest.get(String.format(STYLE_API))
                    .execute()
                    .body();
            JSONObject tagJson = JSONObject.fromObject(tagBody);
            JSONArray tags = tagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tag = tags.getJSONObject(i);

                String name = tag.getString("tagName");
                String id = tag.getString("tagId");

                if (!Tags.newSongTag.containsKey(name)) Tags.newSongTag.put(name, new String[c]);
                Tags.newSongTag.get(name)[1] = id;
                // 子标签
                JSONArray subTags = tag.optJSONArray("childrenTags");
                if (subTags == null) continue;
                for (int j = 0, s = subTags.size(); j < s; j++) {
                    JSONObject subTag = subTags.getJSONObject(j);

                    String subName = subTag.getString("tagName");
                    String subId = subTag.getString("tagId");

                    if (!Tags.newSongTag.containsKey(subName)) Tags.newSongTag.put(subName, new String[c]);
                    Tags.newSongTag.get(subName)[1] = subId;
                    // 孙子标签
                    JSONArray ssTags = subTag.optJSONArray("childrenTags");
                    if (ssTags == null) continue;
                    for (int k = 0, l = ssTags.size(); k < l; k++) {
                        JSONObject ssTag = ssTags.getJSONObject(k);

                        String ssName = ssTag.getString("tagName");
                        String ssId = ssTag.getString("tagId");

                        if (!Tags.newSongTag.containsKey(ssName)) Tags.newSongTag.put(ssName, new String[c]);
                        Tags.newSongTag.get(ssName)[1] = ssId;
                    }
                }
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initNewSongTag));

        taskList.forEach(task -> {
            try {
                task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
