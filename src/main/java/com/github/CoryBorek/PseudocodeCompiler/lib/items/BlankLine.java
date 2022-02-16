package com.github.CoryBorek.PseudocodeCompiler.lib.items;

import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;

public class BlankLine extends SingleLineItem {

    public BlankLine(int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super("", startingNum, file, parent);
    }

    @Override
    public void setup() {
        setNewLine("");
    }
}
