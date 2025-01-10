package ru.make.account.core.arving.util;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollUtils {
    public static <T, M extends Collection<R>, R> List<R> collectAll(Collection<T> coll, Function<T, M> func) {
        return collect(coll, func).stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static <T, R> List<R> collect(Collection<T> coll, Function<T, R> func) {
        if (coll == null) {
            return Collections.emptyList();
        }

        List<R> resultList = new ArrayList<>();

        for (T object : coll) {
            var value = func.apply(object);
            if (Objects.nonNull(value))
                resultList.add(value);
        }

        return resultList;
    }
}
