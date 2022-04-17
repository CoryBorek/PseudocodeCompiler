package com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.statements;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.SingleLineItem;

/**
 * Break
 */
public class Break extends SingleLineItem {

    /**
     * Constructor
     * @param line Line of Break
     * @param startingNum Starting index
     * @param file File being converted
     * @param parent Parent file
     */
    public Break(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(line, startingNum, file, parent);
    }

    /**
     * Sets the line as `break;`
     */
    @Override
    public void setup() {
        setNewLine("break;");
    }
}
