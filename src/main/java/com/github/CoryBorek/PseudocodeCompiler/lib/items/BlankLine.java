package com.github.CoryBorek.PseudocodeCompiler.lib.items;

import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;

public class BlankLine extends SingleLineItem {

    /**
     * Blank lines
     * @param startingNum Start of the line
     * @param file File that is currently being converted
     * @param parent Parent of this line
     */
    public BlankLine(int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super("", startingNum, file, parent);
    }

    /**
     * Sets up the compilation
     */
    @Override
    public void setup() {
        setNewLine("");
    }
}
