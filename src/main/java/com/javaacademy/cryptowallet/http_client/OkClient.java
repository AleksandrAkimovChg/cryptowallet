package com.javaacademy.cryptowallet.http_client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

    public Response sendRequest(Request request) throws IOException {
        return client.newCall(request).execute();
    }

    public String getResponseBody(Response response) throws IOException, RuntimeException {
        if (response == null || !response.isSuccessful() || response.body() == null) {
            throw new RuntimeException("Response неуспешен или пустой");
        }
        return response.body().string();
    }
}
