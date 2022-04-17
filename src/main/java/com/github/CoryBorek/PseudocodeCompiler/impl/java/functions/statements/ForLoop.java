package com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.statements;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;

import java.util.List;

/**
 * for loop
 */
public class ForLoop extends BaseFunction {

    /**
     * Constructor
     * @param lines Lines in loop
     * @param startingNum Starting index
     * @param file File being converted
     * @param parent Parent Object
     */
    public ForLoop(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(lines, startingNum, file, parent);
    }

    /**
     * Compiles the loop
     * @return
     */
    @Override
    public String compile() {
        //Get the for loop info
        String statement = getLines().get(0);
        String[] items = statement.substring("FOR".length()).split("TO|BY");
        String name = items[0].substring(0, items[0].indexOf("=")).replaceAll(" ", "");

        //Starts output
        String out = getFile().getIndentations().get(getStartingNum()) + "for (int " + items[0].trim() + "; "+ name.trim() + " < " + items[1].trim() + "; " + name.trim() + " += " + items[2].trim() + ") {\n";

        //Compiles children
        for (BaseCompiler child : getChildren()) {
            out += child.compile();
        }

        //Finishes output
        out += getFile().getIndentations().get(getLines().size() - 1 + getStartingNum()) + "}\n";
        return out;
    }
}
