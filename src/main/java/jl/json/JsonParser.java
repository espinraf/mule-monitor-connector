package jl.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

/**
 * Parse data in JSON format from file or text string
 */
public class JsonParser {

   private static int position;

   /**
    * Parse specified JSON formatted text file and return its representation
    * 
    * @param file
    *           File input to read and parse
    * @return A JSONObject or JSONArray depending on the input
    * @throws IOException
    *            If an I/O error error occurred
    * @throws ParseException
    *            If parse failed
    */
   public static Object toJSON(File file) throws IOException, ParseException {

      BufferedReader reader = null;

      try {
         reader = new BufferedReader(new FileReader(file));
         String line = null;
         StringBuilder builder = new StringBuilder();

         line = reader.readLine();
         while (line != null) {
            builder.append(line);
            line = reader.readLine();
         }

         return toJSON(builder.toString());
      } finally {
         if (reader != null) {
            try {
               reader.close();
            } catch (IOException e) {
               // Ignore
            }
         }
      }
   }

   /**
    * Parse specified JSON formatted text string and return its representation
    * 
    * @param string
    *           String input to parse
    * @return A JSONObject or JSONArray depending on the input
    * @throws ParseException
    *            If parse failed
    */
   public static Object toJSON(String string) throws ParseException {

      position = 0;

      nextNonWhitespace(string);

      char c = string.charAt(position);
      if (c == '{') {
         return parseObject(string);
      } else if (c == '[') {
         return parseArray(string);
      }

      throw new ParseException("Expected '{' or '['", position);

   }

   private static void nextNonWhitespace(String string) throws ParseException {
      while (Character.isWhitespace(string.charAt(position))) {
         advancePosition(string);
      }
   }

   private static JsonObject parseObject(String string) throws ParseException {

      JsonObject jsonObject = new JsonObject();

      while (true) {
         advancePosition(string);
         nextNonWhitespace(string);
         if (string.charAt(position) != '"') {
            throw new ParseException("Expected '\"'", position);
         }

         String id = parseString(string);
         advancePosition(string);
         nextNonWhitespace(string);
         if (string.charAt(position) != ':') {
            throw new ParseException("Expected ':'", position);
         }

         advancePosition(string);
         nextNonWhitespace(string);

         char c = string.charAt(position);
         if (c == '{') {
            JsonObject value = parseObject(string);
            jsonObject.putJsonObject(id, value);
         } else if (c == '[') {
            JsonArray value = parseArray(string);
            jsonObject.putJsonArray(id, value);
         } else if (c == '"') {
            String value = parseString(string);
            jsonObject.putString(id, value);
         } else if (Character.isDigit(c)) {
            Object value = parseNumber(string);
            if (value instanceof Long) {
               jsonObject.putLong(id, (Long) value);
            } else if (value instanceof Double) {
               jsonObject.putDouble(id, (Double) value);
            }
            position--;
         } else {
            Boolean value = parseTrueFalseOrNull(string);
            jsonObject.putBoolean(id, value);
            position--;
         }

         advancePosition(string);
         nextNonWhitespace(string);

         if (string.charAt(position) != ',') {
            break;
         }

      }

      if (string.charAt(position) != '}') {
         throw new ParseException("Expected '}'", position);
      }

      return jsonObject;

   }

   private static JsonArray parseArray(String string) throws ParseException {

      JsonArray jsonArray = new JsonArray();

      while (true) {
         advancePosition(string);
         nextNonWhitespace(string);
         char c = string.charAt(position);
         if (c == '{') {
            JsonObject value = parseObject(string);
            jsonArray.addJsonObject(value);
         } else if (c == '[') {
            JsonArray value = parseArray(string);
            jsonArray.addJsonArray(value);
         } else if (c == '"') {
            String value = parseString(string);
            jsonArray.addString(value);
         } else if (Character.isDigit(c)) {
            Object value = parseNumber(string);
            if (value instanceof Long) {
               jsonArray.addLong((Long) value);
            } else if (value instanceof Double) {
               jsonArray.addDouble((Double) value);
            }
            position--;
         } else if (c != ']') {
            Boolean value = parseTrueFalseOrNull(string);
            jsonArray.addBoolean(value);
            position--;
         } else {
            position--;
         }

         advancePosition(string);
         nextNonWhitespace(string);

         if (string.charAt(position) != ',') {
            break;
         }

      }

      if (string.charAt(position) != ']') {
         throw new ParseException("Expected ']'", position);
      }

      return jsonArray;

   }

   private static String parseString(String string) throws ParseException {
      advancePosition(string);
      int s = position;
      while (string.charAt(position) != '"') {
         advancePosition(string);
      }
      return string.substring(s, position);
   }

   private static Object parseNumber(String string) throws ParseException {
      int s = position;
      while (Character.isDigit(string.charAt(position))) {
         advancePosition(string);
      }
      if (string.charAt(position) == '.') {
         advancePosition(string);
         while (Character.isDigit(string.charAt(position))) {
            advancePosition(string);
         }
         return Double.parseDouble(string.substring(s, position));
      }

      return Long.parseLong(string.substring(s, position));
   }

   private static Boolean parseTrueFalseOrNull(String string) throws ParseException {
      int s = position;
      while (Character.isLetter(string.charAt(position))) {
         advancePosition(string);
      }
      String value = string.substring(s, position);
      if ("true".equals(value) || "false".equals(value)) {
         return new Boolean(value);
      }
      if ("null".equals(value)) {
         return null;
      }
      throw new ParseException("Expected true, false or null", position);
   }

   private static void advancePosition(String string) throws ParseException {
      position++;
      if (position == string.length()) {
         throw new ParseException("End of input", position);
      }
   }

}
