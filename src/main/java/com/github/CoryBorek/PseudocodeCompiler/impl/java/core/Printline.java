package com.github.CoryBorek.PseudocodeCompiler.impl.java.core;

import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.SingleLineItem;

public class Printline extends SingleLineItem {
    public Printline(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(line, startingNum, file, parent);
    }

    @Override
    public void setup() {
        setNewLine("System.out.println(" + getLines().get(0).replace("PRINTLINE ", "") + ");");
    }
}
