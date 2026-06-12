package net.doge.util.core.http;

import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;

/**
 * HTTP 响应封装
 */
public class HttpResponse {
    private Response response;

    // 私有构造
    private HttpResponse(Response response) {
        this.response = response;
    }

    public static HttpResponse of(Response response) {
        return new HttpResponse(response);
    }

    // 请求是否成功
    public boolean isSuccessful() {
        return response.isSuccessful();
    }

    // Header
    public String header(String name) {
        return response.header(name);
    }

    // 获取重定向地址
    public String getFinalUrl() {
        return response.request().url().toString();
    }

    // 关闭
    public void close() {
        response.close();
    }

    // Body
    public String body() {
        try {
            ResponseBody body = response.body();
            return body != null ? body.string() : null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close();
        }
    }

    public byte[] bodyBytes() {
        try {
            ResponseBody body = response.body();
            return body != null ? body.bytes() : null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close();
        }
    }

    public InputStream bodyStream() {
        ResponseBody body = response.body();
        return body != null ? body.byteStream() : null;
    }

    public long contentLength() {
        ResponseBody body = response.body();
        return body != null ? body.contentLength() : 0;
    }
}
