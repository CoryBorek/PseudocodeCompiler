package com.github.CoryBorek.PseudocodeCompiler.impl.java.functions;

import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;
import java.util.List;

public class MainFunction extends BaseFunction {
    public MainFunction(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(lines, startingNum, file, parent);
    }
    @Override
    public String compile() {
        String out = "";
        out += getFile().getIndentations().get(getStartingNum()) +
                "public static void main(String[] args) {\n";

        for (BaseCompiler child : getChildren()) {
            out += child.compile();
        }

        out += getFile().getIndentations().get(getStartingNum() + getLines().size() - 1) + "}\n";
        return out;
    }
}
