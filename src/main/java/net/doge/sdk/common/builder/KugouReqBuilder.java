package net.doge.sdk.common.builder;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import net.doge.sdk.common.opt.kg.KugouReqOptConstants;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.util.common.CryptoUtil;
import net.doge.util.common.StringUtil;

import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

public class KugouReqBuilder {
    private static final String appid = "1005";
    private static final String apiver = "20";
    private static final String clientver = "12029";
    private static final String pidversec = "57ae12eb6890223e355ccfcb74edf70d";
    private static final String userid = "0";
    private static final String mid = "114514";
    private static final String androidSignKey = "OIlwieks28dk2k092lksi2UIkp";

    public static HttpRequest buildRequest(Map<String, Object> params, String data, Map<KugouReqOptEnum, String> options) {
        String url = options.getOrDefault(KugouReqOptEnum.URL, "");
        if (!url.startsWith("http")) url = "https://gateway.kugou.com" + url;
        Method method = Method.valueOf(options.get(KugouReqOptEnum.METHOD));

        // 初始化默认参数
        if (params == null) params = new TreeMap<>();
        String ct = String.valueOf(System.currentTimeMillis() / 1000);
//        params.put("key", CryptoUtil.md5(pidversec + appid + mid + userid));
        params.put("dfid", "-");
        params.put("mid", mid);
        params.put("uuid", "-");
        params.put("appid", appid);
        params.put("apiver", apiver);
        params.put("clientver", clientver);
        params.put("userid", userid);
        params.put("clienttime", ct);

        String crypto = options.get(KugouReqOptEnum.CRYPTO);
        switch (crypto) {
            case KugouReqOptConstants.ANDROID:
                params.put("signature", signAndroid(params, data));
                break;
        }
        url += "?" + requestParams(params);
        return HttpUtil.createRequest(method, url)
                .header("dfid", "-")
                .header("mid", mid)
                .header("clienttime", ct)
                .body(data);
    }

    // 构造请求参数
    private static String requestParams(Map<String, Object> params) {
        StringJoiner sj = new StringJoiner("&");
        for (String k : params.keySet()) {
            Object o = params.get(k);
            Object v = o instanceof String ? StringUtil.urlEncodeAll((String) o) : o;
            sj.add(k + "=" + v);
        }
        return sj.toString();
    }

    // 构造签名参数
    private static String signParams(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        for (String k : params.keySet()) sb.append(k).append("=").append(params.get(k));
        return sb.toString();
    }

    // 安卓签名
    private static String signAndroid(Map<String, Object> params, String data) {
//        Map<String, Object> paramsTreeMap = new TreeMap<>(params);
        String sign = signParams(params);
        return CryptoUtil.md5(androidSignKey + sign + (StringUtil.notEmpty(data) ? data : "") + androidSignKey);
    }
}