package common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

public class CommonUtilNora {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                    LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                    LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
            .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (je, type, ctx) -> {
                try {
                    return Date.valueOf(je.getAsString());
                } catch (IllegalArgumentException e) {
                    throw new JsonParseException(je.getAsString(), e);
                }
            })
            .create();

    public static <T> T json2Pojo(HttpServletRequest req, Class<T> clazz)
            throws IOException, ServletException {
        String ct = req.getContentType();
        if (ct != null && ct.toLowerCase().startsWith("multipart/")) {
            Part jsonPart = req.getPart("json");
            if (jsonPart == null) {
                throw new IOException("找不到名為 json 的欄位");
            }
            try (Reader reader = new java.io.InputStreamReader(
                    jsonPart.getInputStream(), StandardCharsets.UTF_8)) {
                return GSON.fromJson(reader, clazz);
            }
        }
        try (BufferedReader reader = req.getReader()) {
            return GSON.fromJson(reader, clazz);
        }
    }

    public static <P> void writePojo2Json(HttpServletResponse resp, P pojo) {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            PrintWriter writer = resp.getWriter();
            writer.write(GSON.toJson(pojo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeSuccess(HttpServletResponse response, String message, Object data) {
        Map<String, Object> result = Map.of("successful", true, "message", message, "data", data);
        writePojo2Json(response, result);
    }

    public static void writeError(HttpServletResponse response, String message) {
        Map<String, Object> result = Map.of("successful", false, "message", message);
        writePojo2Json(response, result);
    }
} 