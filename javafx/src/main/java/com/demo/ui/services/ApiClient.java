package com.demo.ui.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String baseUrl;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    public String get(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        ensureOk(res);
        return res.body();
    }

    public String post(String path, String json) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        ensureCreatedOrOk(res);
        return res.body();
    }

    public String put(String path, String json) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        ensureOk(res);
        return res.body();
    }

    public void delete(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .DELETE()
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 204 && res.statusCode() != 200) {
            throw new RuntimeException("DELETE failed: " + res.statusCode() + " - " + res.body());
        }
    }

    private void ensureOk(HttpResponse<String> res) {
        if (res.statusCode() != 200) {
            throw new RuntimeException("HTTP " + res.statusCode() + ": " + res.body());
        }
    }

    private void ensureCreatedOrOk(HttpResponse<String> res) {
        int code = res.statusCode();
        if (code != 201 && code != 200) {
            throw new RuntimeException("HTTP " + code + ": " + res.body());
        }
    }
}
