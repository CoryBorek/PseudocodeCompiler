package com.github.CoryBorek.PseudocodeCompiler;

import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseClass;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;

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
     * Returns the indent of a line in a file
     * @param line Line in file
     * @return The indent
     */
    public static String getIndent (String line) {
        char[] characters = line.toCharArray();
        int firstItem = 0;
        for (int i = 0; i < line.length(); i++) {
            if (characters[i] != ' ') {
                firstItem = i;
                break;
            }
        }
        if (firstItem == 0) return "";
        else return line.substring(0, firstItem);
    }

    /**
     * Checks to see if a given line as a comment
     * @param line
     * @return
     */
    public static boolean hasComment(String line) {
        String[] commentsArr = line.split("§§");
        return commentsArr.length > 1 || line.startsWith("//");
    }

    /**
     * Checks if a String is a number. This line is adapted from: https://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-numeric-in-java
     * @param item Item to check
     * @return true if it is a number.
     */
    public static boolean isNumber(String item) {
        return item.matches("-?\\d+(\\.\\d+)?");
    }

    /**
     * Checks if a given variable is a double
     * @param item var
     * @return if it's a double
     */
    public static boolean isDouble(String item) {
        if (isNumber(item) && item.indexOf(".") >=0) return true;
        return false;
    }

    /**
     * Checks if a given variable is an integer
     * @param item var
     * @return if it's an int
     */
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

    /**
     * Given a value or variable, it will find the data type
     * @param val Value to check
     * @param vars Pre-defined variables to check
     * @return The data type
     */
    public static String findType(String val, BaseCompiler vars) {
        String[] items = val.replace(" ", "").split("\\u002b\\u002b|\\u002b|\u002d\u002d|\u002d|\u003d\u003d|\u003d|\\u002a|\u002f|\u0025");
        String item = items[0];
        String output = "var";
        if (vars instanceof BaseClass && ((BaseClass)vars).hasVar(item)) {
            output = ((BaseClass)vars).getType(item);
        } else if (vars instanceof BaseFunction && ((BaseFunction) vars).hasVar(item)) {
            output = ((BaseFunction) vars).getType(item);
        } else if (Util.isInteger(item)) {
            output = "int";
        } else if (Util.isDouble(item)) {
            output = "double";
        } else if (Util.isBoolean(item)) {
            output = "boolean";
        } else if (item.length() == 3 && item.startsWith("'")) {
            output = "char";
        } else if (item.startsWith("\"")) {
            output = "String";
        }
        else if (item.startsWith("NEW ")) {
            int i = item.indexOf("(");
            output = item.substring("NEW ".length(), i).replaceAll(" ", "");
        }
        return output;
    }

}
