package com.github.CoryBorek.PseudocodeCompiler.impl;

import com.github.CoryBorek.PseudocodeCompiler.Util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

public class ClassCompiler {
    private ArrayList<String> lines = new ArrayList<>();
    private File convertedFile;
    private String newName;

    private String previousLine = "";
    private ArrayList<String> imports = new ArrayList<>();

    public ClassCompiler(File toConvert) {
        convertedFile = toConvert;
        newName = toConvert.getName().replace(".pseudo", ".java");
        try {
            lines = Util.readLinesFromFile(convertedFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        String out = "";

        System.out.println("Converting File: " + convertedFile.getName());
        for (String line : lines) {
            System.out.println(line);
            line = line.replace("\t", "    ");
            int firstItem = 0;
            char[] characters = line.toCharArray();
            String indent = "";
            for (int i = 0; i < line.length(); i++) {
                if (characters[i] != ' ') {
                    firstItem = i;
                    break;
                }
            }
            if (firstItem != 0) indent = line.substring(0, firstItem);
            line = line.substring(firstItem).replace("//", "§§");
            System.out.println(line);
            String[] comments = line.split("§§");

            if (!line.startsWith("§§")) {
                String temp = updateLine(comments[0], indent);
                out += temp;
                System.out.println(comments[0] + " -> " + temp);
                previousLine = comments[0];
            }
            if (comments.length > 1 || line.startsWith("§§")) {
                int temp = comments.length -1;
                if (comments.length == 0) temp = 0;
                out += indent + "//" + comments[temp] + "\n";
                //previousLine = "";
            }

        }

        String importStr = "";

        for (String item : imports) {
            importStr += item + "\n";
        }
        importStr += "\n";

        out = importStr + out;
        try {
            System.out.println("Saving Document.");
            FileOutputStream outputStream = new FileOutputStream(newName);
            outputStream.write(out.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        } catch (IOException e) {
            System.err.println("Something wrong happened!");
            e.printStackTrace();
        }

    }


    public String updateLine(String in, String indent) {
        if (in.startsWith("CLASS")) {
            String name = in.replace("CLASS ", "");
            return indent + "public class " + name;
        }
        else if (previousLine.startsWith("CLASS") && in.startsWith("BEGIN")) {
            return " {\n";
        }
        else if (in.startsWith("END CLASS")) {
            return indent + "}\n";
        }

        return "\n";
    }



}
