package com.javaacademy.cryptowallet.http_client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class OkClient {
    private final OkHttpClient client = new OkHttpClient();

    public Request getGetRequest(String urlPath) {
        return new Request.Builder().get().url(urlPath).build();
    }

    public Request getGetRequest(String urlPath, String headerKey, String headerValue) {
        return new Request.Builder().addHeader(headerKey, headerValue).get().url(urlPath).build();
    }

    public Response sendRequest(Request request) throws RuntimeException {
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        if (response == null || !response.isSuccessful() || response.body() == null) {
            throw new RuntimeException("Response неуспешен или пустой");
        }
        return response;
    }

    public ResponseBody getResponseBody(Response response) {
        return response.body();
    }
}
