package org.jboss.aerogear.syncdemo.sync;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JsonUtil {

    private static final ObjectMapper OM = new ObjectMapper();
    
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
    public static JsonNode toJsonNode(final Info info) {
        try {
            final ObjectNode content = OM.createObjectNode();
            content.put("name", info.getName());
            content.put("profession", info.getProfession());
            final ArrayNode hobbies = OM.createArrayNode();
            for (String hobby : info.getHobbies()) {
                hobbies.add(OM.createObjectNode().put("description", hobby));
            }
            content.put("hobbies", hobbies);
            return content;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    public static Info fromJsonNode(final JsonNode json) {
        try {
            final ArrayNode hobbies = (ArrayNode) json.get("hobbies");
            return new Info(json.get("name").asText(),
                    json.get("profession").asText(),
                    hobbies.get(0).get("description").asText(),
                    hobbies.get(1).get("description").asText(),
                    hobbies.get(2).get("description").asText(),
                    hobbies.get(3).get("description").asText());
        } catch (Exception e) {
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
