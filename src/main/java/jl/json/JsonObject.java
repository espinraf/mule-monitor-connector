package jl.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * JSON object implementation
 * 
 * @author Tomas Ljunggren
 */
public class JsonObject {

   private Map<String, Object> keyValuePairs = new HashMap<String, Object>();

   public void putString(String key, String value) {
      keyValuePairs.put(key, value);
   }

   public void putJsonObject(String key, JsonObject value) {
      keyValuePairs.put(key, value);
   }

   public void putJsonArray(String key, JsonArray value) {
      keyValuePairs.put(key, value);
   }

   public void putLong(String key, Long value) {
      keyValuePairs.put(key, value);
   }

   public void putDouble(String key, Double value) {
      keyValuePairs.put(key, value);
   }

   public void putBoolean(String key, Boolean value) {
      keyValuePairs.put(key, value);
   }

   public String getString(String key) {
      Object object = keyValuePairs.get(key);
      if (object != null) {
         return object.toString();
      }
      return null;
   }

   public JsonObject getJsonObject(String key) {
      return (JsonObject) keyValuePairs.get(key);
   }

   public JsonArray getJsonArray(String key) {
      return (JsonArray) keyValuePairs.get(key);
   }

   public Long getLong(String key) {
      Object object = keyValuePairs.get(key);
      if (object != null) {
         return (Long) keyValuePairs.get(key);
      }
      return null;
   }

   public Double getDouble(String key) {
      return (Double) keyValuePairs.get(key);
   }

   public Boolean getBoolean(String key) {
      return (Boolean) keyValuePairs.get(key);
   }

   public Set<String> getKeys() {
      return keyValuePairs.keySet();
   }

   public void remove(String key) {
      keyValuePairs.remove(key);
   }
   
   public boolean isType(String key, Class<?> type) {
      return (keyValuePairs.get(key).getClass() == type);
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append('{');
      boolean firstEntry = true;
      Set<Entry<String, Object>> entrySet = keyValuePairs.entrySet();
      for (Entry<String, Object> e : entrySet) {
         if (!firstEntry) {
            builder.append(',');
         } else {
            firstEntry = false;
         }
         builder.append('\"');
         builder.append(e.getKey());
         builder.append('\"');
         builder.append(':');
         Object value = e.getValue();
         if (value instanceof String) {
            builder.append('\"');
            builder.append(value);
            builder.append('\"');
         } else {
            builder.append(value);
         }
      }
      builder.append('}');
      return builder.toString();
   }
}
