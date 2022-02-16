package com.github.CoryBorek.PseudocodeCompiler.impl.java.classes;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.BlankLine;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.DataType;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseClass;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.MainFunction;

import java.util.ArrayList;
import java.util.List;

public class NormalClass extends BaseClass {

    public NormalClass(List<String> lines, int startingNum, JavaPseudoFile file, String className) {
        super(lines, startingNum, file, className);
    }

    @Override
    public void setup() {

        int[] currentFunction = new int[2];
        String currentFunctionName = "";
        boolean inFunction = false;
        for (String line : getLines()) {
            System.out.println(line);
            if (line.startsWith("MAIN") && !inFunction) {
                currentFunction[0] = getLines().indexOf(line);
                currentFunctionName = "MAIN";
                inFunction = true;
            }
            else if (line.startsWith("END " + currentFunctionName)) {
                currentFunction[1] = getLines().indexOf(line);
                addFunctions(currentFunction, currentFunctionName);
                inFunction = false;
            }
            else if (line.startsWith("CREATE ") && !inFunction) {
                getChildren().add(new DataType(line, getStartingNum() + getLines().indexOf(line), getFile(), this));
            }
            else if (line.startsWith("CONSTANT ") && !inFunction) {
                getChildren().add(new DataType(line, getStartingNum() + getLines().indexOf(line), getFile(), this));
            }
            else if (line.contains("=") && hasVar(line.substring(0, line.indexOf("="))) && !inFunction) {
                getChildren().add(new DataType(line, getStartingNum() + getLines().indexOf(line), getFile(), this));
                continue;
            }

        }
    }

    private void addFunctions(int[] items, String name) {
        ArrayList<String> functionLines = new ArrayList<>();

        for (int i = items[0]; i < items[1]; i++) {
            functionLines.add(getLines().get(i));
        }
        if (name.equals("MAIN")){
            getChildren().add(new MainFunction(functionLines, items[0] + getStartingNum(), getFile(), this));
        }
        else {
            getChildren().add(new BlankLine(getStartingNum(), getFile(), this));
        }
    }
}
