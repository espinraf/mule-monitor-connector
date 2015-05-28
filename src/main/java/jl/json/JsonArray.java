package jl.json;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON array implementation
 * 
 * @author Tomas Ljunggren
 */
public class JsonArray {

   private List<Object> values = new ArrayList<Object>();

   public void addJsonObject(JsonObject value) {
      values.add(value);
   }

   public void addJsonArray(JsonArray value) {
      values.add(value);
   }

   public void addString(String value) {
      values.add(value);
   }

   public void addLong(Long value) {
      values.add(value);
   }

   public void addDouble(Double value) {
      values.add(value);
   }

   public void addBoolean(Boolean value) {
      values.add(value);
   }

   public int size() {
      return values.size();
   }

   public String getString(int index) {
      return (String) values.get(index);
   }

   public Long getLong(int index) {
      return (Long) values.get(index);
   }

   public Double getDouble(int index) {
      return (Double) values.get(index);
   }

   public Boolean getBoolean(int index) {
      return (Boolean) values.get(index);
   }

   public JsonObject getJsonObject(int index) {
      return (JsonObject) values.get(index);
   }

   public JsonArray getJsonArray(int index) {
      return (JsonArray) values.get(index);
   }
   
   public boolean isType(int index, Class<?> type) {
      return (values.get(index).getClass() == type);
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append('[');
      boolean firstEntry = true;
      for (Object v : values) {
         if (!firstEntry) {
            builder.append(',');
         } else {
            firstEntry = false;
         }
         if (v instanceof String) {
            builder.append('\"');
            builder.append(v);
            builder.append('\"');
         } else {
            builder.append(v);
         }
      }
      builder.append(']');
      return builder.toString();
   }

}
