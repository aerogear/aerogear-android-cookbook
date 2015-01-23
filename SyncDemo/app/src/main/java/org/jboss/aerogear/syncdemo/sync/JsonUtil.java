package org.jboss.aerogear.syncdemo.sync;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JsonUtil {
    
    private JsonUtil() {
    }
    
    public static String toJson(final Info info) {
        try {
            final JSONObject content = new JSONObject();
            content.put("name", info.getName());
            content.put("profession", info.getProfession());
            final JSONArray hobbies = new JSONArray();
            for (String hobby : info.getHobbies()) {
                hobbies.put(new JSONObject().put("description", hobby));
            }
            content.put("hobbies", hobbies);
            return content.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public static Info fromJson(final String json) {
        try {
            final JSONObject jsonObject = new JSONObject(json);
            final JSONArray hobbies = jsonObject.getJSONArray("hobbies");
            return new Info(jsonObject.get("name").toString(), 
                    jsonObject.get("profession").toString(),
                    hobbies.getJSONObject(0).get("description").toString(),
                    hobbies.getJSONObject(1).get("description").toString(),
                    hobbies.getJSONObject(2).get("description").toString(),
                    hobbies.getJSONObject(3).get("description").toString());
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
