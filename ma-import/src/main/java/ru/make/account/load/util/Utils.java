package ru.make.account.load.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Utils {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static LocalDate convert(String date) {
        return LocalDate.parse(date, FORMATTER);
    }

    public static ObjectMapper getMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new SimpleDateFormat());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return objectMapper;
    }

    public static boolean nonNull(String value) {
        return Objects.nonNull(value) && !value.isEmpty() && !value.isBlank();
    }

    public static BigDecimal convertNumber(String value) {
        if (nonNull(value)) {
            if (value.contains(",")) {
                return new BigDecimal(value.replace(",", "."));
            } else return new BigDecimal(value);
        }
        return null;
    }
}
