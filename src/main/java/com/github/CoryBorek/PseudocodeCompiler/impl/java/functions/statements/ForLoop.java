package com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.statements;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;

import java.util.List;

public class ForLoop extends BaseFunction {
    public ForLoop(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(lines, startingNum, file, parent);
    }

    @Override
    public String compile() {
        String statement = getLines().get(0);
        String[] items = statement.substring("FOR".length()).split("TO|BY");
        String name = items[0].substring(0, items[0].indexOf("=")).replaceAll(" ", "");

        String out = getFile().getIndentations().get(getStartingNum()) + "for (int " + items[0].trim() + "; "+ name.trim() + " < " + items[1].trim() + "; " + name.trim() + " += " + items[2].trim() + ") {\n";


        for (BaseCompiler child : getChildren()) {
            out += child.compile();
        }

        out += getFile().getIndentations().get(getLines().size() - 1 + getStartingNum()) + "}\n";
        return out;
    }
}
