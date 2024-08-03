package ru.make.account.load.util;

import com.fasterxml.jackson.core.type.TypeReference;
import ru.make.account.load.model.AccountDto;
import ru.make.account.load.model.stuff.LoadData;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class AccountUtil {
    private final AccessUtil accessUtil;
    private final LoadData loadData;

    private final List<AccountDto> accounts = new ArrayList<>();

    private String userId;
    private Long accountId;

    public AccountUtil(AccessUtil accessUtil, LoadData loadData) {
        this.accessUtil = accessUtil;
        this.loadData = loadData;
    }

    public void initData() throws Exception {
        var uri = loadData.getUrl("account/getAccounts");
        System.out.println("url: " + uri.toString());
        var rq = HttpRequest.newBuilder(uri)
                .headers("Authorization", accessUtil.getAccessToken())
                .GET()
                .build();
        var rs = HttpClient.newHttpClient().send(rq, HttpResponse.BodyHandlers.ofString());
        Utils.getMapper().readValue(
                rs.body(),
                new TypeReference<List<AccountDto>>() {
                }).forEach(accounts::add);
    }

    public AccountDto getAccount(String accountName) {
        return accounts.stream()
                .filter(item -> item.getName().equals(accountName))
                .findFirst().orElseThrow(() -> new NullPointerException("Счёт не найден"));
    }
}
