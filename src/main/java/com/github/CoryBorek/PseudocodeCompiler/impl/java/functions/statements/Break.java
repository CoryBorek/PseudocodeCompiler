package com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.statements;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.SingleLineItem;

public class Break extends SingleLineItem {
    public Break(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(line, startingNum, file, parent);
    }

    @Override
    public void setup() {
        setNewLine("break;");
    }
}
