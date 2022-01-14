package com.github.CoryBorek.PseudocodeCompiler.impl;

import com.github.CoryBorek.PseudocodeCompiler.Util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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
    private Map<String,String> vars = new HashMap<>();

    private String out;

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
        out = "";

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
                System.out.println(comments[0] + " -> " + temp.replace(indent, ""));
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
    private String updateLine(String in, String indent) {
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
        //Main function
        else if (in.startsWith("MAIN")) {
            return indent + "public static void main(String[] args) {\n";
        }
        //Prints and moves to next line
        else if (in.startsWith("PRINTLINE")) {
            return indent + "System.out.println(" + in.replace("PRINTLINE ", "") + ");\n";
        }
        //Prints, but stays on this line.
        else if (in.startsWith("PRINT")) {
            return indent + "System.out.print(" + in.replace("PRINT ", "") + ");\n";
        }
        //Creates a new variable
        else if (in.startsWith("CREATE")) {
            //Split assignment and declaration
            String[] split = in.split("=");
            //If declared, run this
            if (split.length > 1) {
                //Set a variable to all lines that have an equal sign past the first one, and combine them.
                String equals = "";
                for (int i = 1; i < split.length; i++) equals += split[i] + "=";
                if (equals.substring(equals.length() - 1).contains("=")) equals = equals.substring(0, equals.length() - 1);

                //If it's a boolean, output as boolean.
                if (Util.isBoolean(equals.trim())) {
                    vars.put(split[0].replace("CREATE ", "").replace(" ", ""), "boolean");
                    return indent + "boolean " + in.replace("CREATE ", "") + ";\n";
                }
                //If it's a Double, output as double.
                else if (Util.isDouble(equals.trim())) {
                    vars.put(split[0].replace("CREATE ", "").replace(" ", ""), "double");
                    return indent + "double " + in.replace("CREATE ", "") + ";\n";
                }
                //If it's an Integer, output as integer
                else if (Util.isInteger(equals.trim())) {
                    vars.put(split[0].replace("CREATE ", "").replace(" ", ""), "int");
                    return indent + "int " + in.replace("CREATE ", "") + ";\n";
                }
                //If it's a String, output as String
                else if (equals.trim().startsWith("\"")) {
                    System.out.println( "\\" + split[0].replace("CREATE ", "").replace(" ", "") + "\\");
                    vars.put(split[0].replace("CREATE ", "").replace(" ", ""), "String");
                    return indent + "String " + in.replace("CREATE ", "") + ";\n";
                }
                //If it's a Character, output as Character.
                else if (equals.trim().length() == 3 && equals.trim().startsWith("'")) {
                    vars.put(split[0].replace("CREATE ", "").replace(" ", ""), "char");
                    return indent + "char " + in.replace("CREATE ", "") + ";\n";
                }
                //If it's complex, and has multiple other options, do this
                else {
                    //Split output into multiple parts, ignoring operators.
                    String[] items = equals.replace(" ", "").split("\\u002b\\u002b|\\u002b|\u002d\u002d|\u002d|\u003d\u003d|\u003d|\\u002a|\u002f|\u0025");

                    //Variable for future checks
                    String output = "var";

                    //Check to see if the vars variable has an item, and marks it as the output
                    for (String item : items) {
                        if (vars.containsKey(item)) {
                            output = vars.get(item);
                            break;
                        }
                        else if (Util.isInteger(item)){
                            output = "int";
                            break;
                        }
                        else if (Util.isDouble(item)) {
                            output = "double";
                            break;
                        }
                        else if (Util.isBoolean(item)) {
                            output = "boolean";
                            break;
                        }
                        else if (item.length() == 3 && item.startsWith("'")) {
                            output = "char";
                            break;
                        }
                        else if (item.startsWith("\"")) {
                            output = "String";
                            break;
                        }
                    }
                    //If the output has a new type, set it for the variable, and put it in vars
                    if (!output.equals("var")) {
                        vars.put(split[0].replace("CREATE ", ""), output);
                        return indent + output + " " + in.replace("CREATE ", "") + ";\n";
                    }
                    //If it doesn't have a type at all, save it as a var for future changes.
                    vars.put(split[0].replace("CREATE ", ""), "var");
                    return indent + "var " + in.replace("CREATE ", "") + ";\n";
                }
            }
            else {
                vars.put(in.replace("CREATE ", ""), "var");
                return indent + "var " + in.replace("CREATE ", "") + ";\n";
            }
        }
        else if (in.contains("=")) {
            if (vars.containsKey(in.substring(0, in.indexOf("=")).replace(" ", ""))) {
                String[] split = in.split("=");
                updateVariables(split);
                return indent + in + ";\n";
            }
            }



        return "\n";
    }

    private void updateVariables(String[] split) {
        split[0] = split[0].replace(" ", "");
        if (vars.containsKey(split[0]) && vars.get(split[0]).equals("var")) {
            String equals = "";
            for (int i = 1; i < split.length; i++) equals += split[i] + "=";
            if (equals.substring(equals.length() - 1).contains("=")) equals = equals.substring(0, equals.length() - 1);
            if (Util.isBoolean(equals.trim())) {
                vars.remove(split[0]);
                vars.put(split[0], "boolean");
                System.out.println("Previous: var " + split[0] + " -> " + "boolean " + split[0]);
                out = out.replaceAll("var " + split[0], "boolean " + split[0]);
            } else if (Util.isDouble(equals.trim())) {
                vars.remove(split[0]);
                vars.put(split[0], "double");
                System.out.println("Previous: var " + split[0] + " -> " + "double " + split[0]);
                out = out.replaceAll("var " + split[0], "double " + split[0]);
            } else if (Util.isInteger(equals.trim())) {
                vars.remove(split[0]);
                vars.put(split[0], "int");
                System.out.println("Previous: var " + split[0] + " -> " + "int " + split[0]);
                out = out.replaceAll("var " + split[0], "int " + split[0]);
            } else if (equals.trim().startsWith("\"")) {
                vars.remove(split[0]);
                vars.put(split[0], "String");
                System.out.println("Previous: var " + split[0] + " -> " + "String " + split[0]);
                out = out.replaceAll("var " + split[0], "String " + split[0]);
            } else if (equals.trim().length() == 3 && equals.trim().startsWith("'")) {
                vars.remove(split[0]);
                vars.put(split[0], "char");
                System.out.println("Previous: var " + split[0] + " -> " + "char " + split[0]);
                out = out.replaceAll("var " + split[0], "char " + split[0]);
            }
            else {
                String[] items = equals.replace(" ", "").split("\43\43|\43|\45\45|\45|\47|\42|\61\61|\37");
                String output = "var";
                for (String item : items) {
                    if (vars.containsKey(item)) {
                        output = vars.get(item);
                        break;
                    }
                    else if (Util.isInteger(item)){
                        output = "int";
                        break;
                    }
                    else if (Util.isDouble(item)) {
                        output = "double";
                        break;
                    }
                    else if (Util.isBoolean(item)) {
                        output = "boolean";
                        break;
                    }
                    else if (item.length() == 3 && item.startsWith("'")) {
                        output = "char";
                        break;
                    }
                    else if (item.startsWith("\"")) {
                        output = "String";
                        break;
                    }
                }
                if (!output.equals("var")) {
                    vars.remove(split[0]);
                    vars.put(split[0], output);
                    System.out.println("Previous: var " + split[0] + " -> " + output + " " + split[0]);
                    out = out.replaceAll("var " + split[0], output + " " + split[0]);
                }
            }
        }

    }



}
