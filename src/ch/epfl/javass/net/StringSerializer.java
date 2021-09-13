package ch.epfl.javass.net;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Class which contains methods to serialize information represented as Strings
 * @author Liam Mouzaoui (295797)
 * @author Remi Delacourt (300849)
 */
public final class StringSerializer {

    /** Constant representing base 16 **/
    private static final int HEX_BASE = 16;

    /**
     * Private constructor of the class
     */
    private StringSerializer() {}

   /**
    * Method to serialize an integer into a string
    * @param value (int): the integer to serialize
    * @return (String): the integer serialized into a string
    */
    public static String serializeInt(int value) {
        return Integer.toUnsignedString(value, HEX_BASE);
    }

    /**
     * Method to deserialize a int from its string representation to its integer value
     * @param s (String): the string we want to deserialize
     * @return (int): the integer value
     */
    public static int deserializeInt(String s) {
        return Integer.parseUnsignedInt(s, HEX_BASE);
    }

    /**
     * Method to serialize a long into a string
     * @param value (long): the long to serialize
     * @return (String): the long serialized into a string
     */
    public static String serializeLong(long value) {
        return Long.toUnsignedString(value, HEX_BASE);
    }

    /**
     * Method to deserialize a long from its string representation to its long value
     * @param s (String): the string we want to deserialize
     * @return (long): the long value
     */
    public static long deserializeLong(String s) {
        return Long.parseUnsignedLong(s, HEX_BASE);
    }

    /**
     * Method to serialize a string UTF-8 to Base 64 encoding
     * @param s (String): the string to serialize
     * @return (String): the string serialized in UTF-8 encoding
     */
    public static String serializeString(String s) {
        byte[] byteArray = s.getBytes(StandardCharsets.UTF_8);
        String serializedString = Base64.getEncoder().encodeToString(byteArray);
        return serializedString;
    }

    /**
     * Method to deserialize a string from Base 64 to UTF-8 encoding 
     * @param s (String):  the string we want to deserialize
     * @return (String): the deserialized string
     */
    public static String deserializeString(String s) {
        byte[] byteArray = Base64.getDecoder().decode(s);
        String deserializedString = new String(byteArray, StandardCharsets.UTF_8);
        return deserializedString;
    }

    /**
     * Combines multiple strings separated by the given delimiter into a single string
     * @param delimiter (String) : the delimiter of the strings
     * @param strings (String...): the strings we want to combine
     * @return (String) : the string resulting from the combination
     */
    public static String combine(String delimiter, String... strings) {
        for (String str: strings) {
            if (str.contains(delimiter)) {throw new IllegalArgumentException("a String contain the delimiter");}
        }
        String fullString = String.join(delimiter, strings);
        return fullString;
    }

    /**
     * Splits a string into the different strings that were separated by the given delimiter
     * @param string (String): the string to split
     * @param delimiter (String): the delimiter
     * @return (String[]): the array containing all the splited strings
     */
    public static String[] split(String string, String delimiter) {
        String[] stringArray = string.split(delimiter);
        return stringArray;
    }
}
