package ru.make.account.load.util;

import com.fasterxml.jackson.core.type.TypeReference;
import ru.make.account.load.model.CategoryDto;
import ru.make.account.load.model.stuff.LoadData;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class CategoryUtil {
    private final AccessUtil accessUtil;
    private final LoadData loadData;

    private final List<CategoryDto> categories = new ArrayList<>();

    public CategoryUtil(AccessUtil accessUtil, LoadData loadData) {
        this.accessUtil = accessUtil;
        this.loadData = loadData;
    }

    public void initData(Long accountId) throws Exception {
        var rq = HttpRequest.newBuilder(loadData.getUrl("category/getCategories?request=" + accountId))
                .headers("Authorization", accessUtil.getAccessToken())
                .GET()
                .build();
        var rs = HttpClient.newHttpClient().send(rq, HttpResponse.BodyHandlers.ofString());
        Utils.getMapper().readValue(
                rs.body(),
                new TypeReference<List<CategoryDto>>() {
                }).forEach(categories::add);
    }

    public CategoryDto getCategory(String categoryName) {
        var categoryOpt = categories.stream()
                .filter(item -> item.getName().equals(categoryName))
                .findFirst();

        if (categoryOpt.isEmpty())
            throw new NullPointerException("Категория не найдена '" + categoryName + "'");
        return categoryOpt.get();
    }
}
