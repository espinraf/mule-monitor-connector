package jl.utilities;


/**
 * Use this class to avoid using String.split() when just splitting on a single
 * character.
 */
public class SplitString {

   private String source;
   private char character;
   private int index;

   /**
    * Constructor create SplitString using specified source and split character
    * 
    * @param source
    *           Source string to split
    * @param character
    *           Split character
    */
   public SplitString(String source, char character) {
      this.source = source;
      this.character = character;
      this.index = 0;
   }

   /**
    * Get next string
    * 
    * @return Next string
    */
   public String next() {
      int length = source.length();
      while (index < length && source.charAt(index) == character) {
         index++;
      }
      int start = index;
      while (index < length && source.charAt(index) != character) {
         index++;
      }
      if (index > start) {
         return source.substring(start, index);
      }
      return null;
   }

}
