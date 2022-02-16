package com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.statements;

import com.github.CoryBorek.PseudocodeCompiler.Util;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;

import java.util.List;

public class ElseIfStatement extends BaseFunction {

    public ElseIfStatement(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(lines, startingNum, file, parent);
    }

    @Override
    public String compile() {
        String statement = getLines().get(0);
        String state = statement.substring(statement.indexOf("(") + 1, statement.lastIndexOf(")"));
        String[] items = state.split("==|!=|<=|<|>=|>");
        String check = state.substring(items[0].length(), state.indexOf(items[1]));
        String temp = state;
        boolean isString = Util.findType(items[0], this).equals("String");
        if (check.equals("==") && isString) temp = items[0].trim() + ".equals(" + items[1].trim() + ")";
        else if (check.equals("!=") && isString) temp = "!(" + items[0].trim() + ".equals(" + items[1].trim() + "))";

        String out = getFile().getIndentations().get(getStartingNum()) +"else if (" +  temp + ") {\n";

        for (BaseCompiler child : getChildren()) {
            out += child.compile();
        }

        out += getFile().getIndentations().get(getStartingNum() + getLines().size()) + "}\n";
        return out;
    }
}
