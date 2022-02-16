package com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.statements;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;

import java.util.List;

public class ElseStatement extends BaseFunction {

    public ElseStatement(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(lines, startingNum, file, parent);
    }

    @Override
    public String compile() {
        String out = getFile().getIndentations().get(getStartingNum()) + "else {\n";

        for (BaseCompiler child : getChildren()) {
            out += child.compile();
        }

        out += getFile().getIndentations().get(getStartingNum() + getLines().size()) + "}\n";
        return out;
    }
}
