package com.github.CoryBorek.PseudocodeCompiler.impl;

import com.github.CoryBorek.PseudocodeCompiler.Util;

import java.beans.MethodDescriptor;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


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
    private Map<String,Map<String,String[]>> methods = new HashMap<>();
    private String currentMethod = "MAIN";

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
                if(!temp.replaceAll("\t| |\n", "").equals("")) {
                    System.out.println(indent + comments[0] + " -> " + temp.replace(indent, ""));
                    previousLine = comments[0];
                }
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

        Scanner in = new Scanner(System.in);

        System.out.println("Checking for unassigned variables and method data.");

        vars.keySet().forEach(key -> {
            if (vars.get(key).equals("var")) {
                System.out.println("Please input a data type for " + key + ":");
                String newType = in.nextLine();
                out.replaceAll("var " + key, newType + " " + key);
                vars.replace(key, newType);
            }
        });

        methods.keySet().forEach(key -> {
            methods.get(key).keySet().forEach(key2 -> {
                if (Integer.parseInt(key2) >= 0) {
                    if (methods.get(key).get(key2)[0].equalsIgnoreCase("var")) {
                        String[] var = methods.get(key).get(key2);
                        System.out.println("Please input a data type for " + var[1] + " in method " + key);
                        String newType = in.nextLine();
                        out = out.replaceAll(var[0] + " " + var[1], newType + " " + var[1]);
                        methods.get(key).replace(key2, new String[]{newType, var[1]});
                    }
                }
            });
            if (methods.get(key).get("-1")[0].equalsIgnoreCase("var")) {
                System.out.println("Please input a return type for method: " + methods.get(key));
                String newReturn = in.nextLine();
                out = out.replaceAll(methods.get(key).get("-2")[0] + " " + methods.get(key).get("-1")[0] + " " + key, methods.get(key).get("-2")[0] + " " + newReturn + " " + key);
                methods.get(key).get("-1")[0] = newReturn;
            }

            if (methods.get(key).get("-2")[0].equalsIgnoreCase("unknowntype")) {
                System.out.println("Is the method: " + key + " static?");
                String next = in.nextLine();
                if (next.equalsIgnoreCase("yes")) {
                    out = out.replaceAll("unknowntype " + methods.get(key).get("-1")[0] + " " + key, "static " + methods.get(key).get("-1")[0] + " " + key);
                    methods.get(key).get("-2")[0] = "static";
                }
                else {
                    out = out.replaceAll("unknowntype " + methods.get(key).get("-1")[0] + " " + key, methods.get(key).get("-1")[0] + " " + key);
                    methods.get(key).get("-2")[0] = "";
                }
            }
        });

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
            if (in.replace("END ", "").contains(currentMethod)) currentMethod = "MAIN";
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
                        output = findType(item);
                        if (!output.equals("var")) break;
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
        else if (in.startsWith("CONSTANT")) {
            //Split assignment and declaration
            String[] split = in.split("=");
            //If declared, run this
            if (split.length > 1) {
                //Set a variable to all lines that have an equal sign past the first one, and combine them.
                String equals = "";
                for (int i = 1; i < split.length; i++) equals += split[i] + "=";
                if (equals.substring(equals.length() - 1).contains("="))
                    equals = equals.substring(0, equals.length() - 1);

                //If it's a boolean, output as boolean.
                if (Util.isBoolean(equals.trim())) {
                    vars.put(split[0].replace("CONSTANT ", "").replace(" ", ""), "boolean");
                    return indent + "final boolean " + in.replace("CONSTANT ", "") + ";\n";
                }
                //If it's a Double, output as double.
                else if (Util.isDouble(equals.trim())) {
                    vars.put(split[0].replace("CONSTANT ", "").replace(" ", ""), "double");
                    return indent + "final double " + in.replace("CONSTANT ", "") + ";\n";
                }
                //If it's an Integer, output as integer
                else if (Util.isInteger(equals.trim())) {
                    vars.put(split[0].replace("CONSTANT ", "").replace(" ", ""), "int");
                    return indent + "final int " + in.replace("CONSTANT ", "") + ";\n";
                }
                //If it's a String, output as String
                else if (equals.trim().startsWith("\"")) {
                    vars.put(split[0].replace("CONSTANT ", "").replace(" ", ""), "String");
                    return indent + "final String " + in.replace("CONSTANT ", "") + ";\n";
                }
                //If it's a Character, output as Character.
                else if (equals.trim().length() == 3 && equals.trim().startsWith("'")) {
                    vars.put(split[0].replace("CONSTANT ", "").replace(" ", ""), "char");
                    return indent + "final char " + in.replace("CONSTANT ", "") + ";\n";
                }
                //If it's complex, and has multiple other options, do this
                else {
                    //Split output into multiple parts, ignoring operators.
                    String[] items = equals.replace(" ", "").split("\\u002b\\u002b|\\u002b|\u002d\u002d|\u002d|\u003d\u003d|\u003d|\\u002a|\u002f|\u0025");

                    //Variable for future checks
                    String output = "var";

                    //Check to see if the vars variable has an item, and marks it as the output
                    for (String item : items) {
                        output = findType(item);
                        if (!output.equals("var")) break;
                    }
                    //If the output has a new type, set it for the variable, and put it in vars
                    if (!output.equals("var")) {
                        vars.put(split[0].replace("CONSTANT ", ""), output);
                        return indent + "final " + output + " " + in.replace("CONSTANT ", "") + ";\n";
                    }
                    //If it doesn't have a type at all, save it as a var for future changes.
                    vars.put(split[0].replace("CONSTANT ", ""), "var");
                    return indent + "final var " + in.replace("CONSTANT ", "") + ";\n";
                }
            } else {
                vars.put(in.replace("CONSTANT ", ""), "var");
                return indent + "final var " + in.replace("CONSTANT ", "") + ";\n";
            }
        }
        else if (in.contains("=")) {
            //Variable Instantiation, add variable.
            if (vars.containsKey(in.substring(0, in.indexOf("=")).replace(" ", ""))) {
                String[] split = in.split("=");
                updateVariables(split);
                return indent + in + ";\n";
            }
        }
        //Methods
        else if (in.startsWith("METHOD")) {
            //Parameters
            if (in.contains("(parameters:")) {
                // Get indexes
                int index = in.indexOf("(parameters:");
                int index2 = in.indexOf(")");

                //Get parameters as array
                String params = in.substring(index + "(parameters:".length(), index2).replace(" ", "");
                String[] paramArr = params.split(",");

                //Map for output
                Map<String,String[]> paramMap = new HashMap<>();

                //Output for parameters
                String paramOut = "";

                //Get output for parameters
                for (int i = 0; i < paramArr.length; i++) {
                    String[] param2 = {"var", paramArr[i]};
                    paramMap.put(String.valueOf(i),param2);
                    paramOut += "var " + paramArr[i] + ",";
                }
                //Remove extra comma
                if (paramOut.length() > 0) paramOut = paramOut.substring(0, paramOut.length() - 1);
                //Return type
                paramMap.put("-1", new String[]{"void", "return"});
                //If static/final or not.
                paramMap.put("-2", new String[]{"unknowntype"});
                //Gets method name
                String methodName = in.substring("METHOD".length(), index).replace(" ", "");
                //Mark as current method
                currentMethod = methodName;

                //Add to method map
                methods.put(methodName, paramMap);

                //Create a new method
                return indent + "public unknowntype " + paramMap.get("-1")[0] + " " + methodName + "(" + paramOut + ")";
            }
        }
        //Begin method
        else if (previousLine.startsWith("METHOD") && in.startsWith("BEGIN")) {
            return " {\n";
        }
        //Returns
        else if (in.startsWith("RETURN")) {
            //Check if void or not
            if (in.length() > "RETURN".length()) {
                //Get return items
                String[] returnItems = in.substring("RETURN".length()).replaceAll(" ", "").split("\\u002b\\u002b|\\u002b|\u002d\u002d|\u002d|\u003d\u003d|\u003d|\\u002a|\u002f|\u0025");
                //Find return type.
                String type = "var";
                for (String item : returnItems) {
                    type = findType(item);
                    if (!type.equals("var")) break;
                }
                //Get new return type and add to map
                out = out.replaceAll(methods.get(currentMethod).get("-1")[0] + " " + currentMethod, type + " " + currentMethod);
                methods.get(currentMethod).get("-1")[0] = type;

                //Return the new return statement.
                return indent + "return " + in.substring("RETURN".length()) + ";";
            }
            else {
                //Set return type as void.
                out.replaceAll(methods.get(currentMethod).get("-1")[0] + " " + currentMethod, "void " + currentMethod);
                methods.get(currentMethod).get("-1")[0] = "void";

                //Return statement.
                return indent + "return;";
            }
        }
        else {
            //final return statement
            final String[] returnS = new String[]{"\n"};

            //Iterates over methods
            methods.keySet().forEach(key -> {
                //If current method is being called
                if (in.startsWith(key)) {
                    //Get parameters
                    String[] params;
                    //If parameters exist, set param array
                    if (!in.contains("()")) {
                        String paramIn = in.substring(in.indexOf("(")+1, in.indexOf(")"));

                        if (paramIn.contains(",")) params = paramIn.split(",");
                        else params = new String[]{paramIn};
                    }
                    //If no parameters, set blank array.
                    else params = new String[0];
                    //Create parameters
                    for (int i = 0; i < params.length; i++) {
                        //Get variable information
                        String[] var = methods.get(key).get(String.valueOf(i));
                        //Old variable
                        String variable = var[0] + " " + var[1];
                        //New variable
                        String newVar = vars.get(params[i]) + " " + var[1];
                        //Message variable change to console.
                        System.out.println(indent + key + ": " + variable + " -> " + newVar);
                        //Update variable
                        out = out.replaceAll(variable, newVar);
                        //Return information
                        returnS[0] = indent + in + ";\n";

                        //Replace data stored.
                        methods.get(key).replace(String.valueOf(i), new String[]{params[i], var[1]});
                    }

                    //Set functions using the MAIN method to be static, so they can run.
                    if (currentMethod.equals("MAIN")) {
                        String newType = "static";
                        System.out.println(indent + methods.get(key).get("-2")[0] + " " + methods.get(key).get("-1")[0] + " " + key + " -> " + newType + " " + methods.get(key).get("-1")[0] + " " + key);
                        out = out.replaceAll(methods.get(key).get("-2")[0] + " " + methods.get(key).get("-1")[0] + " " + key , newType + " " + methods.get(key).get("-1")[0] + " " + key);
                        methods.get(key).get("-2")[0] = newType;
                    }
                    //Else, make it a regular function.
                    else {
                        String newType = "";
                        System.out.println(indent + methods.get(key).get("-2")[0] + " " + methods.get(key).get("-1")[0] + " " + key + " -> " +  methods.get(key).get("-1")[0] + " " + key);
                        out = out.replaceAll(methods.get(key).get("-2")[0] + " " + methods.get(key).get("-1")[0] + " " + key,  methods.get(key).get("-1")[0] + " " + key);
                        methods.get(key).get("-2")[0] = newType;
                    }
                }
            });
            //Return
            return returnS[0];
        }



        return "\n";
    }

    /**
     * Gets the data type based on a part of setup
     * @param item Item to check
     * @return the data type
     */
    private String findType(String item) {
        String output = "var";
        if (vars.containsKey(item)) {
            output = vars.get(item);
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
        return output;
    }

    /**
     * Sets variable data types when initiated
     * @param split
     */
    private void updateVariables(String[] split) {
        //Splits variables
        split[0] = split[0].replace(" ", "");

        //Checks if variable exists, and is temporary type var.
        if (vars.containsKey(split[0]) && vars.get(split[0]).equals("var")) {
            String equals = "";
            for (int i = 1; i < split.length; i++) equals += split[i] + "=";
            if (equals.substring(equals.length() - 1).contains("=")) equals = equals.substring(0, equals.length() - 1);
            //Checks for boolean
            if (Util.isBoolean(equals.trim())) {
                vars.remove(split[0]);
                vars.put(split[0], "boolean");
                System.out.println("Previous: var " + split[0] + " -> " + "boolean " + split[0]);
                out = out.replaceAll("var " + split[0], "boolean " + split[0]);
            }
            //If it's a double
            else if (Util.isDouble(equals.trim())) {
                vars.remove(split[0]);
                vars.put(split[0], "double");
                System.out.println("Previous: var " + split[0] + " -> " + "double " + split[0]);
                out = out.replaceAll("var " + split[0], "double " + split[0]);
            }
            //If it's an int
            else if (Util.isInteger(equals.trim())) {
                vars.remove(split[0]);
                vars.put(split[0], "int");
                System.out.println("Previous: var " + split[0] + " -> " + "int " + split[0]);
                out = out.replaceAll("var " + split[0], "int " + split[0]);
            }
            //If it's a string
            else if (equals.trim().startsWith("\"")) {
                vars.remove(split[0]);
                vars.put(split[0], "String");
                System.out.println("Previous: var " + split[0] + " -> " + "String " + split[0]);
                out = out.replaceAll("var " + split[0], "String " + split[0]);
            }
            //If it's a char
            else if (equals.trim().length() == 3 && equals.trim().startsWith("'")) {
                vars.remove(split[0]);
                vars.put(split[0], "char");
                System.out.println("Previous: var " + split[0] + " -> " + "char " + split[0]);
                out = out.replaceAll("var " + split[0], "char " + split[0]);
            }
            //Else is's complex, and you need to check all items.
            else {
                String[] items = equals.replace(" ", "").split("\43\43|\43|\45\45|\45|\47|\42|\61\61|\37");
                String output = "var";
                for (String item : items) {
                    output = findType(item);
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
