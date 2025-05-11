package user.member.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



public class CommonUtil {

    private static final Gson GSON = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .create();

    // JSON → Java Object
    public static <T> T json2Pojo(HttpServletRequest req, Class<T> clazz) {
        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return GSON.fromJson(json.toString(), clazz);
    }

    // Java Object → JSON response
    public static <P> void writePojo2Json(HttpServletResponse resp, P pojo) {
    	resp.setContentType("application/json;charset=UTF-8");
    	try {
            PrintWriter writer = resp.getWriter();
            writer.write(GSON.toJson(pojo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
