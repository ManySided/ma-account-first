package ru.make.account.load.util;

import ru.make.account.load.model.TicketDto;
import ru.make.account.load.model.stuff.LoadData;

import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TicketUtil {
    private final AccessUtil accessUtil;
    private final LoadData loadData;

    public TicketUtil(AccessUtil accessUtil, LoadData loadData) {
        this.accessUtil = accessUtil;
        this.loadData = loadData;
    }

    public void saveTicket(TicketDto request) throws Exception {
        var rq = HttpRequest.newBuilder(loadData.getUrl("ticket"))
                .headers("Authorization", accessUtil.getAccessToken())
                .headers("Content-Type", "application/json; charset=utf8")
                .headers("Accept", "application/json")
                .POST(
                        HttpRequest.BodyPublishers.ofString(
                                //URLEncoder.encode(
                                Utils.getMapper().writeValueAsString(request)//,
                                //        StandardCharsets.UTF_8
                                //)
                        )
                )
                .build();
        System.out.println("url: " + rq);
        System.out.println("rq: " + Utils.getMapper().writeValueAsString(request));
        var rs = HttpClient.newHttpClient().send(rq, HttpResponse.BodyHandlers.ofString());
        if (rs.statusCode() != HttpURLConnection.HTTP_OK) {
            System.out.println("code: " + rs.statusCode());
            throw new IllegalStateException("Ошибка сохранения чека");
        }
    }
}
