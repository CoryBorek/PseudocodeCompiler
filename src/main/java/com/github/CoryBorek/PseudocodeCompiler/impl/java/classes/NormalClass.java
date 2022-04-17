package com.github.CoryBorek.PseudocodeCompiler.impl.java.classes;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.ConstructorFunction;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.NormalFunction;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.BlankLine;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.DataType;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseClass;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.MainFunction;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Standard classes
 */
public class NormalClass extends BaseClass {

    /**
     * Constructor
     * @param lines Lines being used
     * @param startingNum Starting index in file
     * @param file File being converted
     * @param className Name of class
     */
    public NormalClass(List<String> lines, int startingNum, JavaPseudoFile file, String className) {
        super(lines, startingNum, file, className);
    }

    /**
     * Sets up conversion
     */
    @Override
    public void setup() {

        int[] currentFunction = new int[2];
        String currentFunctionName = "";
        boolean inFunction = false;
        for (String line : getLines()) {
            //Main method
            if (line.startsWith("MAIN") && !inFunction) {
                currentFunction[0] = getLines().indexOf(line);
                currentFunctionName = "MAIN";
                inFunction = true;
            }
            //Regular method
            else if (line.startsWith("METHOD") && !inFunction) {
                currentFunction[0] = getLines().indexOf(line);
                currentFunctionName = line.substring("METHOD".length(), line.indexOf("(")).trim();
                inFunction = true;
            }
            //Object constructor
            else if (line.startsWith("CONSTRUCTOR") && !inFunction) {
                currentFunction[0] = getLines().indexOf(line);
                currentFunctionName = "CONSTRUCTOR";
                inFunction = true;
            }
            //Ends the method
            else if (line.startsWith("END " + currentFunctionName)) {
                currentFunction[1] = getLines().indexOf(line);
                addFunctions(currentFunction, currentFunctionName);
                inFunction = false;
            }
            //Creates a variable
            else if (line.startsWith("CREATE ") && !inFunction) {
                getChildren().add(new DataType(line, getStartingNum() + getLines().indexOf(line), getFile(), this));
            }
            //Creates a constant
            else if (line.startsWith("CONSTANT ") && !inFunction) {
                getChildren().add(new DataType(line, getStartingNum() + getLines().indexOf(line), getFile(), this));
            }
            //Updates a variable
            else if (line.contains("=") && hasVar(line.substring(0, line.indexOf("="))) && !inFunction) {
                getChildren().add(new DataType(line, getStartingNum() + getLines().indexOf(line), getFile(), this));
                //continue;
            }

        }
    }

    /**
     * Adds functions
     * @param items
     * @param name
     */
    private void addFunctions(int[] items, String name) {
        ArrayList<String> functionLines = new ArrayList<>();

        for (int i = items[0]; i < items[1]; i++) {
            functionLines.add(getLines().get(i));
        }
        //Main function
        if (name.equals("MAIN")){
            getChildren().add(new MainFunction(functionLines, items[0] + getStartingNum(), getFile(), this));
        }
        //Constructor
        else if (name.equals("CONSTRUCTOR")) {
            getChildren().add(new ConstructorFunction(functionLines, items[0] + getStartingNum(), getFile(), this));
        }
        //Regular functions
        else {
            getChildren().add(new NormalFunction(functionLines, items[0] + getStartingNum(), getFile(), this));
        }
    }
}
