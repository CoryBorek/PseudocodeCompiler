package com.github.CoryBorek.PseudocodeCompiler.impl;

import com.github.CoryBorek.PseudocodeCompiler.Util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


/**
 * ClassCompiler class. Converts a .pseudo file to a .java file.
 */
public class ClassCompiler {

    //Basic information about the program
    private ArrayList<String> lines = new ArrayList<>();
    private File convertedFile;
    private String newName;

    // Common variables for conversion
    private String previousLine = "";
    private ArrayList<String> imports = new ArrayList<>();

    /**
     * Constructor for ClassCompiler
     * @param toConvert File to convert
     */
    public ClassCompiler(File toConvert) {
        convertedFile = toConvert;
        newName = toConvert.getName().replace(".pseudo", ".java");
        try {
            lines = Util.readLinesFromFile(convertedFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs the conversion, line by line.
     */
    public void run() {

        //Output string
        String out = "";

        //Let the user know that the file is being converted
        System.out.println("Converting File: " + convertedFile.getName());

        //Runs this for every line in the program.
        for (String line : lines) {
            //Replace tabs with spaces, for easy conversion
            line = line.replace("\t", "    ");
            //Grab the location of the indent.
            int firstItem = 0;
            char[] characters = line.toCharArray();
            String indent = "";
            for (int i = 0; i < line.length(); i++) {
                if (characters[i] != ' ') {
                    firstItem = i;
                    break;
                }
            }
            //Use indent text as a variable
            if (firstItem != 0) indent = line.substring(0, firstItem);
            //Remove indent and replace comments with a temporary character for conversion.
            line = line.substring(firstItem).replace("//", "§§");

            //Split actual code from comments.
            String[] comments = line.split("§§");

            //If the line does not start with a comment, convert the line
            if (!line.startsWith("§§")) {
                String temp = updateLine(comments[0], indent);
                out += temp;
                System.out.println(comments[0] + " -> " + temp);
                previousLine = comments[0];
            }
            //If the line has a comment, copy over the comment.
            if (comments.length > 1 || line.startsWith("§§")) {
                int temp = comments.length -1;
                if (comments.length == 0) temp = 0;
                out += indent + "//" + comments[temp] + "\n";
                //previousLine = "";
            }

        }

        //Takes imports, which are added when needed, and adds them to the top of the java file.
        String importStr = "";
        for (String item : imports) {
            importStr += item + "\n";
        }
        if (imports.size() > 0) importStr += "\n";
        out = importStr + out;

        //Try to save the document to a .java file. If not, notify the user and print stack trace.
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

    /**
     * Updates a single line of code
     * @param in Inputted line of code
     * @param indent Indented tab.
     * @return Updated string
     */
    public String updateLine(String in, String indent) {
        //Class indentification
        if (in.startsWith("CLASS")) {
            String name = in.replace("CLASS ", "");
            return indent + "public class " + name;
        }
        //Opening of class
        else if (previousLine.startsWith("CLASS") && in.startsWith("BEGIN")) {
            return " {\n";
        }
        //End of something.
        else if (in.startsWith("END")) {
            return indent + "}\n";
        }
        else if (in.startsWith("MAIN")) {
            return indent + "public static void main(String[] args) {\n";
        }
        else if (in.startsWith("PRINTLINE")) {
            return indent + "System.out.println(" + in.replace("PRINTLINE ", "") + ");\n";
        }
        else if (in.startsWith("PRINT")) {
            return indent + "System.out.print(" + in.replace("PRINT ", "") + ");\n";
        }


        return "\n";
    }



}
