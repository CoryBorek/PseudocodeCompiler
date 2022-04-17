package com.github.CoryBorek.PseudocodeCompiler.impl.java.core;

import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.SingleLineItem;

/**
 * Print statements
 */
public class Print extends SingleLineItem {

    /**
     * Constructor
     * @param line Line of PRINT
     * @param startingNum Starting index
     * @param file File being converted
     * @param parent Parent object
     */
    public Print(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(line, startingNum, file, parent);
    }

    /**
     * Sets the new line
     */
    @Override
    public void setup() {
        setNewLine("System.out.print(" + getLines().get(0).replace("PRINT ", "") + ");");
    }
}
