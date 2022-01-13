package com.github.CoryBorek.PseudocodeCompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class Util {
    public static ArrayList<String> readLinesFromFile(File file) throws FileNotFoundException {
        ArrayList<String> out = new ArrayList<>();
        BufferedReader in = new BufferedReader(new FileReader(file));

        in.lines().forEach(line -> {
            out.add(line);
        });
        return out;
    }

}
