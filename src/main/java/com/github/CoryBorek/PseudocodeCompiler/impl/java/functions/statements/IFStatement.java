package com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.statements;

import com.github.CoryBorek.PseudocodeCompiler.Util;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;

import java.util.List;

/**
 * If statement
 */
public class IFStatement extends BaseFunction {

    /**
     * Constructor
     * @param lines Lines being compiled
     * @param startingNum Starting index
     * @param file File being converted
     * @param parent Parent Object
     */
    public IFStatement(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(lines, startingNum, file, parent);
    }

    /**
     * Compiles the statement
     * @return
     */
    @Override
    public String compile() {

        //Gets the staetement
        String statement = getLines().get(0);
        String state = statement.substring(statement.indexOf("(") + 1, statement.lastIndexOf(")"));
        String[] items = state.split("==|!=|<=|<|>=|>");
        String check = state.substring(items[0].length(), state.indexOf(items[1]));
        String temp = state;

        //Does any string info
        boolean isString = Util.findType(items[0], this).equals("String");
        if (check.equals("==") && isString) temp = items[0].trim() + ".equals(" + items[1].trim() + ")";
        else if (check.equals("!=") && isString) temp = "!(" + items[0].trim() + ".equals(" + items[1].trim() + "))";

        //Starts output
        String out = getFile().getIndentations().get(getStartingNum()) +"if (" +  temp + ") {\n";

        //Compiles children
        for (BaseCompiler child : getChildren()) {
            out += child.compile();
        }

        //Finishes output
        out += getFile().getIndentations().get(getStartingNum() + getLines().size()) + "}\n";
        return out;
    }
}
