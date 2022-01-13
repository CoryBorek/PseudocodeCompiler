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

}
