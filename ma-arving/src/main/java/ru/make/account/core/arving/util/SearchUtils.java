package ru.make.account.core.arving.util;

import org.springframework.util.StringUtils;

public class SearchUtils {
    public static String likeText(String text) {
        if (StringUtils.hasLength(text)) {
            text = text.replace(" ", "%");
            text = text.toUpperCase();
            return "%" + text + "%";
        }
        return null;
    }
}
