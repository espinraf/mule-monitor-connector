package jl.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

public class Json {

   public static JsonObject toJsonObject(Object object) throws JsonException {
      JsonObject result = new JsonObject();
      Class<? extends Object> clazz = object.getClass();
      try {
         for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            if (isIntegerType(fieldType)) {
               result.putLong(field.getName(), field.getLong(object));
            } else if (isFloatingPointType(fieldType)) {
               result.putDouble(field.getName(), field.getDouble(object));
            } else if (fieldType == String.class) {
               String value = (String) field.get(object);
               if (value != null) {
                  result.putString(field.getName(), (String) field.get(object));
               }
            } else if (fieldType == Boolean.class || fieldType == boolean.class) {
               result.putBoolean(field.getName(), field.getBoolean(object));
            } else if (fieldType.isAssignableFrom(List.class)) {
               List<?> value = (List<?>) field.get(object);
               if (value != null) {
                  result.putJsonArray(field.getName(), toJsonArray(value));
               }
            }
         }
      } catch (IllegalArgumentException | IllegalAccessException e) {
         throw new JsonException(e.getMessage());
      }
      return result;
   }

   public static JsonArray toJsonArray(Object[] objectArray) throws JsonException {
      JsonArray result = new JsonArray();
      for (Object object : objectArray) {
         Class<? extends Object> clazz = object.getClass();
         if (isIntType(clazz)) {
            result.addLong(0L);
         } else {
            result.addJsonObject(toJsonObject(object));
         }
      }
      return result;
   }
   
   public static JsonArray toJsonArray(List<?> objectList) throws JsonException {
      JsonArray result = new JsonArray();
      for (Object object : objectList) {
         result.addJsonObject(toJsonObject(object));
      }
      return result;
   }

   public static <T> T construct(JsonObject source, Class<T> clazz) throws JsonException {
      T instance = null;
      try {
         instance = clazz.newInstance();
         for (Field field : clazz.getDeclaredFields()) {
            assign(field, instance, source);
         }
      } catch (InstantiationException | IllegalAccessException e) {
         throw new JsonException("Unable to create instance of " + clazz);
      }
      return instance;
   }

   private static <T> void assign(Field field, Object instance, JsonObject source) throws IllegalArgumentException,
         IllegalAccessException, JsonException {
      field.setAccessible(true);
      Class<?> type = field.getType();
      if (type == String.class) {
         field.set(instance, source.getString(field.getName()));
      } else if (isIntegerType(type)) {
         assignInteger(field, instance, source.getLong(field.getName()), type);
      } else if (type == Boolean.class || type == boolean.class) {
         field.setBoolean(instance, source.getBoolean(field.getName()));
      } else if (isFloatingPointType(type)) {
         assignFloatingPoint(field, instance, source.getDouble(field.getName()), type);
      } else if (type == String[].class) {
         assignStringArray(field, instance, source.getJsonArray(field.getName()));
      } else if (isIntegerArrayType(type)) {
         assignIntegerArray(field, instance, source.getJsonArray(field.getName()), type);
      } else if (type == Boolean[].class || type == boolean[].class) {
         assignBooleanArray(field, instance, source.getJsonArray(field.getName()), type);
      } else if (isFloatingPointArrayType(type)) {
         assignFloatingPointArray(field, instance, source.getJsonArray(field.getName()), type);
      }
   }

   private static boolean isIntType(Class<?> type) {
      return (type == Integer.class || type == int.class);
   }

   private static boolean isIntArrayType(Class<?> type) {
      return (type == Integer[].class || type == int[].class);
   }

   private static boolean isLongType(Class<?> type) {
      return (type == Long.class || type == long.class);
   }

   private static boolean isLongArrayType(Class<?> type) {
      return (type == Long[].class || type == long[].class);
   }

   private static boolean isShortType(Class<?> type) {
      return (type == Short.class || type == short.class);
   }

   private static boolean isShortArrayType(Class<?> type) {
      return (type == Short[].class || type == short[].class);
   }

   private static boolean isByteType(Class<?> type) {
      return (type == Byte.class || type == byte.class);
   }

   private static boolean isByteArrayType(Class<?> type) {
      return (type == Byte[].class || type == byte[].class);
   }

   private static boolean isIntegerType(Class<?> type) {
      return (isIntType(type) || isByteType(type) || isShortType(type) || isLongType(type));
   }

   private static boolean isIntegerArrayType(Class<?> type) {
      return (isIntArrayType(type) || isLongArrayType(type) || isShortArrayType(type) || isByteArrayType(type));
   }

   private static boolean isFloatType(Class<?> type) {
      return (type == Float.class || type == float.class);
   }

   private static boolean isFloatArrayType(Class<?> type) {
      return (type == Float[].class || type == float[].class);
   }

   private static boolean isDoubleType(Class<?> type) {
      return (type == Double.class || type == double.class);
   }

   private static boolean isDoubleArrayType(Class<?> type) {
      return (type == Double[].class || type == double[].class);
   }

   private static boolean isFloatingPointType(Class<?> type) {
      return (isFloatType(type) || isDoubleType(type));
   }

   private static boolean isFloatingPointArrayType(Class<?> type) {
      return (isFloatArrayType(type) || isDoubleArrayType(type));
   }

   private static <T> void assignInteger(Field field, T instance, Long value, Class<?> type) throws JsonException {
      if (value == null) {
         return;
      }
      try {
         if (isLongType(type)) {
            field.setLong(instance, value);
         } else if (isIntType(type)) {
            field.set(instance, value.intValue());
         } else if (isShortType(type)) {
            field.setShort(instance, value.shortValue());
         } else if (isByteType(type)) {
            field.setByte(instance, value.byteValue());
         }
      } catch (IllegalArgumentException | IllegalAccessException e) {
         throw new JsonException("Could not assign value " + value + " to field " + field.getName());
      }
   }

   private static void assignFloatingPoint(Field field, Object instance, Double value, Class<?> type)
         throws JsonException {
      if (value == null) {
         return;
      }
      try {
         if (isDoubleType(type)) {
            field.set(instance, value);
         } else if (isFloatType(type)) {
            field.set(instance, value.floatValue());
         }
      } catch (IllegalArgumentException | IllegalAccessException e) {
         throw new JsonException("Could not assign value " + value + " to field " + field.getName());
      }
   }

   private static void assignStringArray(Field field, Object instance, JsonArray array) throws JsonException {
      String[] stringArray = new String[array.size()];
      for (int i = 0; i < stringArray.length; i++) {
         stringArray[i] = array.getString(i);
      }
      try {
         field.set(instance, stringArray);
      } catch (IllegalArgumentException | IllegalAccessException e) {
         throw new JsonException("Could not assign string array value to field " + field.getName());
      }
   }

   private static void assignIntegerArray(Field field, Object instance, JsonArray jsonArray, Class<?> type)
         throws JsonException {
      try {
         Object array = Array.newInstance(type, jsonArray.size());
         for (int i = 0; i < jsonArray.size(); i++) {
            Long value = jsonArray.getLong(i);
            if (isLongArrayType(type)) {
               Array.setLong(array, i, value.longValue());
            } else if (isIntArrayType(type)) {
               Array.setInt(array, i, value.intValue());
            } else if (isShortArrayType(type)) {
               Array.setShort(array, i, value.shortValue());
            } else if (isByteArrayType(type)) {
               Array.setByte(array, i, value.byteValue());
            }
         }
         field.set(instance, array);
      } catch (IllegalAccessException e) {
         throw new JsonException("Could not assign array value to field " + field.getName());
      }
   }

   private static void assignBooleanArray(Field field, Object instance, JsonArray jsonArray, Class<?> type)
         throws JsonException {
      Object array = Array.newInstance(type, jsonArray.size());
      for (int i = 0; i < jsonArray.size(); i++) {
         Array.setBoolean(array, i, jsonArray.getBoolean(i));
      }
      try {
         field.set(instance, array);
      } catch (IllegalAccessException e) {
         throw new JsonException("Could not assign boolean array value to field " + field.getName());
      }
   }

   private static void assignFloatingPointArray(Field field, Object instance, JsonArray jsonArray, Class<?> type)
         throws JsonException {
      try {
         Object array = Array.newInstance(type, jsonArray.size());
         for (int i = 0; i < jsonArray.size(); i++) {
            Double value = jsonArray.getDouble(i);
            if (isDoubleArrayType(type)) {
               Array.setDouble(array, i, value.doubleValue());
            } else if (isFloatArrayType(type)) {
               Array.setFloat(array, i, value.floatValue());
            }
         }
         field.set(instance, array);
      } catch (IllegalAccessException e) {
         throw new JsonException("Could not assign array value to field " + field.getName());
      }
   }

}
