package com.github.CoryBorek.PseudocodeCompiler.impl.java.core;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.SingleLineItem;

import java.util.ArrayList;

/**
 * Calling a function
 */
public class CallFunction extends SingleLineItem {
    //Attributes
    private ArrayList<String> data;
    private String name;

    /**
     * Constructor
     * @param line Line of CallFunction
     * @param startingNum Index in file
     * @param file File being converted
     * @param parent Parent object
     */
    public CallFunction(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(line, startingNum, file, parent);
    }

    /**
     * Gets the attributes being called
     * @return
     */
    public ArrayList<String> getData() {
        return data;
    }

    /**
     * Gets the name of the function
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets up the conversion
     */
    @Override
    public void setup() {
        //Name of the function
        name = getLine().substring(0, getLine().indexOf("(")).replaceAll(" ", "");
        //Sets data (attributes)
        data = new ArrayList<>();
        if (!getLine().contains("()")) {
            String dataIn = getLine().substring(getLine().indexOf("(") + 1, getLine().lastIndexOf(")"));
            String[] dataArr = dataIn.split(",");
            for (String item : dataArr) {
                this.data.add(item.trim());
            }
        }
        //Sets the line
        setNewLine(getLine() + ";\n");

    }

}
