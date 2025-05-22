package user.member.util;

import javax.sql.DataSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CommonContants {

	public static DataSource DATASOURCE;
	public static final Gson GSON = new GsonBuilder().create();
	public static final String JSON_MIME_TYPE = "application/json";

//	public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
//			.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
//				@Override
//				public Date deserialize(JsonElement je, Type type, JsonDeserializationContext ctx)
//						throws JsonParseException {
//					try {
//						return Date.valueOf(je.getAsString());
//					} catch (IllegalArgumentException e) {
//						throw new JsonParseException("無法解析 birthDate: " + je.getAsString(), e);
//					}
//				}
//			}).create();
}
