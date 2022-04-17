package com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.statements;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.SingleLineItem;

/**
 * Continue Line
 */
public class Continue extends SingleLineItem {

    /**
     * Constructor
     * @param line Line of Continue
     * @param startingNum Starting index
     * @param file File being converted
     * @param parent Parent Object
     */
    public Continue(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(line, startingNum, file, parent);
    }

    /**
     * Sets the line
     */
    @Override
    public void setup() {
        setNewLine("continue;");
    }
}
