package com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.statements;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;

import java.util.List;

/**
 * Else statement
 */
public class ElseStatement extends BaseFunction {

    /**
     * Constructor
     * @param lines Lines in Statement
     * @param startingNum Starting index
     * @param file File being converted
     * @param parent Parent Object
     */
    public ElseStatement(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(lines, startingNum, file, parent);
    }

    /**
     * Compiles the statement
     * @return
     */
    @Override
    public String compile() {
        //Starts output
        String out = getFile().getIndentations().get(getStartingNum()) + "else {\n";

        //Compiles children
        for (BaseCompiler child : getChildren()) {
            out += child.compile();
        }

        //Finishes output
        out += getFile().getIndentations().get(getStartingNum() + getLines().size()) + "}\n";
        return out;
    }
}
