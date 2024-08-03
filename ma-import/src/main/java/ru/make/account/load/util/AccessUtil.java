package ru.make.account.load.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;

public class AccessUtil {
    private final ObjectMapper objectMapper;
    private final String host;
    private final String port;
    private final String user;
    private final String password;
    private final String realm;
    private final String userId;
    private String accessToken;

    public AccessUtil(String host, String port, String user, String password, String realm, String userId) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.realm = realm;
        this.userId = userId;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    public String getAccessToken() throws Exception {
        String code = "Bearer ";
        if (Objects.isNull(accessToken))
            code += getAccessCode();
        else code += accessToken;
        return code;
    }

    private String getAccessCode() throws Exception {
        var url = format("http://%s:%s/realms/%s/protocol/openid-connect/token", host, port, realm);
        System.out.println("url: " + url);

        Map<String, String> formData = new HashMap<>();
        formData.put("client_id", "account-key");
        formData.put("grant_type", "password");
        formData.put("scope", "openid");
        formData.put("username", user);
        formData.put("password", password);
        var request = HttpRequest.newBuilder(new URI(url))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(getFormDataAsString(formData)))
                .build();

        var responseJson = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        Map<String, Object> result = objectMapper.readValue(responseJson.body(), new TypeReference<>() {
        });

        accessToken = result.get("access_token").toString();
        return accessToken;
    }

    private String getFormDataAsString(Map<String, String> formData) {
        StringBuilder formBodyBuilder = new StringBuilder();
        for (Map.Entry<String, String> singleEntry : formData.entrySet()) {
            if (formBodyBuilder.length() > 0) {
                formBodyBuilder.append("&");
            }
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getKey(), StandardCharsets.UTF_8));
            formBodyBuilder.append("=");
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getValue(), StandardCharsets.UTF_8));
        }
        return formBodyBuilder.toString();
    }

    public String getUserId() {
        return userId;
    }
}
