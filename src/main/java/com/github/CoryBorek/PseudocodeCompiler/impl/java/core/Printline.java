package com.github.CoryBorek.PseudocodeCompiler.impl.java.core;

import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.SingleLineItem;

/**
 * Printlines
 */
public class Printline extends SingleLineItem {

    /**
     * Constructor
     * @param line Line of Printline
     * @param startingNum Starting index
     * @param file File being converted
     * @param parent Parent Object
     */
    public Printline(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(line, startingNum, file, parent);
    }

    /**
     * Sets up the printline
     */
    @Override
    public void setup() {
        setNewLine("System.out.println(" + getLines().get(0).replace("PRINTLINE ", "") + ");");
    }
}
