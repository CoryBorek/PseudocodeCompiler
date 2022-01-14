package com.github.CoryBorek.PseudocodeCompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class Util {

    /**
     * Reads individual lines from a file, and places it in a String ArrayList.
     * @param file File to read
     * @return String list of read lines
     * @throws FileNotFoundException
     */
    public static ArrayList<String> readLinesFromFile(File file) throws FileNotFoundException {
        //Output string
        ArrayList<String> out = new ArrayList<>();
        //Reads the file
        BufferedReader in = new BufferedReader(new FileReader(file));

        //Adds lines to ArrayList
        in.lines().forEach(line -> {
            out.add(line);
        });
        //Return the output.
        return out;
    }

    /**
     * Checks if a String is a number. This line is adapted from: https://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-numeric-in-java
     * @param item Item to check
     * @return true if it is a number.
     */
    public static boolean isNumber(String item) {
        return item.matches("-?\\d+(\\.\\d+)?");
    }

    public static boolean isDouble(String item) {
        if (isNumber(item) && item.indexOf(".") >=0) return true;
        return false;
    }

    public static boolean isInteger(String item) {
        if (isNumber(item) && item.indexOf(".") == -1) return true;
        return false;
    }

    /**
     * Checks if a string is a boolean
     * @param item
     * @return
     */
    public static boolean isBoolean(String item) {
        if (item.equals("true") || item.equals("false") || item.contains("=="))
            return true;
        else return false;
    }

}
