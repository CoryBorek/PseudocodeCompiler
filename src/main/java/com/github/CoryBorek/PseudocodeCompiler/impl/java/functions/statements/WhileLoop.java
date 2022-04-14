package com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.statements;

import com.github.CoryBorek.PseudocodeCompiler.Util;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;

import java.util.List;

public class WhileLoop extends BaseFunction {

    public WhileLoop(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(lines, startingNum, file, parent);
    }

    @Override
    public String compile() {
        String whileItem = getLines().get(0).replace("WHILE", "");
        whileItem = whileItem.substring(whileItem.indexOf("(")+1, whileItem.length() - 1);

        String[] args = whileItem.split("==|!=|<=|<|>=|>");
        String inner = whileItem;

        if (Util.findType(args[0], this).equals("String")) inner = args[0] + ".equals(" + args[1] + ")";
        String output = getFile().getIndentations().get(getStartingNum());

        output += "while (" + inner + ") {\n";

        for (BaseCompiler child : getChildren()) {
            output += child.compile();
        }

        output += getFile().getIndentations().get(getLines().size() + getStartingNum()) + "}\n";
        return output;
    }
}
