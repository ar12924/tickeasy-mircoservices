package user.member.util;

import static user.member.util.CommonContants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.DataSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;


public class CommonUtil {
	
    public static final Gson GSON = new GsonBuilder()
            // 针对 java.sql.Date，直接用 Date.valueOf(str)
            .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (je, type, ctx) -> {
                try {
                    return Date.valueOf(je.getAsString());
                } catch (IllegalArgumentException e) {
                    throw new JsonParseException(je.getAsString(), e);
                }
            })
            .create();

	public static Connection getConnection() throws NamingException, SQLException {
		if (DATASOURCE == null) {
			DATASOURCE = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/example");
		}
		return DATASOURCE.getConnection();
	}

	//	依據ContentType判斷，application/json
	 public static <T> T json2Pojo(HttpServletRequest req, Class<T> clazz)
	            throws IOException, ServletException {
	        String ct = req.getContentType();
	        // multipart
	        if (ct != null && ct.toLowerCase().startsWith("multipart/")) {
	            Part jsonPart = req.getPart("json");
	            if (jsonPart == null) {
	                throw new IOException("找不到名為 json 的欄位");
	            }
	            try (Reader reader = new InputStreamReader(
	                    jsonPart.getInputStream(), StandardCharsets.UTF_8)) {
	                return GSON.fromJson(reader, clazz);
	            }
	        }
	        // application/json
	        try (BufferedReader reader = req.getReader()) {
	            return GSON.fromJson(reader, clazz);
	        }
	    }

	// Java Object → JSON response
	public static <P> void writePojo2Json(HttpServletResponse resp, P pojo) {
		resp.setContentType(JSON_MIME_TYPE);
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
