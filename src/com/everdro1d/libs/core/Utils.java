/**************************************************************************************************
 * Copyright (c) dro1dDev 2024.                                                                   *
 **************************************************************************************************/

package com.everdro1d.libs.core;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class Utils {
    private Utils() {}

    /**
     * Open a link in the default browser.
     * @param url the link to open
     */
    public static void openLink(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void copyToClipboard(String copyString) {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(copyString), null);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Get the current time as a string.
     * @param includeDate whether to include the date - yyyy-MM-dd
     * @param includeTime whether to include the time - HH:mm:ss
     * @param includeMillis whether to include milliseconds - .SSS
     * @return the current time as a string
     */
    public static String getCurrentTime(boolean includeDate, boolean includeTime, boolean includeMillis) {
        String now = LocalDateTime.now().toString();
        String date = now.split("T")[0];
        String time = now.split("T")[1].split("\\.")[0];
        String millis = now.split("\\.")[1];

        String currentTime = "";
        if (includeDate) currentTime += date + (includeTime ? " " : "");
        if (includeTime) currentTime += time;
        if (includeMillis) currentTime += "." + millis;

        return currentTime;
    }

    /**
     * Test if the string contains any of the strings in the array.
     * @param matchingArray the array of strings to match
     * @param testString the string to test
     * @return boolean
     * <p>Example: containsAny(new String[]{"a", "b", "c"}, "abc") -> true
     * <p>Example: containsAny(new String[]{"a", "b", "c"}, "def") -> false
     */
    public static boolean containsAny(String[] matchingArray, String testString) {
        for (String s : matchingArray) {
            if (testString.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public static String replaceCharAt(String string, int i, String s) {
        return string.substring(0, i) + s + string.substring(i + s.length());
    }

    /**
     * Get a set of unique values from the Inner Map under conditions
     * @param property the set of values to get
     * @param filter the conditions to get the values
     * @return a set of values
     * <p>Example: An inner map with video extensions and audio codecs,
     * I want to get all the video extensions where the audio codec is "video only"
     * <p>Set<String> values = getUniqueValues("EXT", option -> option.get("ACODEC").equals("video only"));
     * <p>System.out.println(values);
     * <p>Output: [mp4, webm, mkv]
     */
    public static Set<String> extractUniqueValuesByPredicate(
            String property, Predicate<Map<String, String>> filter, Map<String, Map<String, String>> map)
    {
        Set<String> uniqueValues = new HashSet<>();
        for (Map<String, String> option : map.values()) {
            if (filter == null || filter.test(option)) {
                uniqueValues.add(option.get(property));
            }
        }
        return uniqueValues;
    }

    /**
     * Prints a json formatted nested map (allows for infinite nesting). See example below.
     *
     * <p>Example:</p>
     * <p></p>
     * <pre>
     * {
     *   "Key1": {
     *     "SubKey1-1": {
     *       "SubKey1-2": "SubValue1-2",
     *       "SubKey2-2": "SubValue2-2",
     *       "SubKey3-2": "SubValue3-2"
     *      }
     *   },
     *   "Key2": {
     *     "SubKey1-1": {
     *       "SubKey1-2": "SubValue1-2",
     *       "SubKey2-2": "SubValue2-2",
     *       "SubKey3-2": "SubValue3-2"
     *     }
     *   },
     *   "Key3": {
     *     "SubKey1": "SubValue1",
     *     "SubKey2": "SubValue2",
     *     "SubKey3": "SubValue3",
     *     "SubKey4": "SubValue4"
     *   }
     * }
     * </pre>
     */
    public static void printNestedMapFormatted(Object map, int indentLeaveAsZero) {
        String indentString = new String(new char[indentLeaveAsZero]).replace("\0", "  ");

        // Use a separate indentation for nested content
        String nestedIndent = indentString + "  ";

        if (map instanceof Map) {
            Map<String, ?> mapObj = (Map<String, ?>) map;
            boolean isFirstEntry = true;
            System.out.print("{");
            for (Map.Entry<String, ?> entry : mapObj.entrySet()) {
                if (!isFirstEntry) {
                    System.out.print(",");
                }
                isFirstEntry = false;
                System.out.print("\n" + nestedIndent + "\"" + entry.getKey() + "\" : ");
                printNestedMapFormatted(entry.getValue(), indentLeaveAsZero + 1);
            }
            // Check if it's the last entry and only print newline then
            if (!isFirstEntry) {
                System.out.println();
            }
            System.out.print(indentString + "}");
        } else {
            System.out.print("\"" + map + "\"");
        }
    }
}
