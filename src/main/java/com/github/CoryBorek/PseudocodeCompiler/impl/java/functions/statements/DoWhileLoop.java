package com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.statements;

import com.github.CoryBorek.PseudocodeCompiler.Util;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;

import java.util.List;

/**
 * Do While Loop
 */
public class DoWhileLoop extends BaseFunction {

    /**
     * Constructor
     * @param lines Lines in loop
     * @param startingNum Starting index
     * @param file File being converted
     * @param parent Parent Object
     */
    public DoWhileLoop(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(lines, startingNum, file, parent);
    }

    /**
     * Compiles the DoWhile
     * @return
     */
    @Override
    public String compile() {
        //Starts the output
        String out = getFile().getIndentations().get(getStartingNum()) + "do {\n";

        //Compiles children
        for (BaseCompiler child : getChildren()) {
            out += child.compile();
        }

        //Gets the info from While
        String whileItem = getLines().get(getLines().size() - 1).replace("WHILE", "");
        whileItem = whileItem.substring(whileItem.indexOf("(")+1, whileItem.length() -1);

        //Adds the arguments
        String[] args = whileItem.split("==|!=|<=|>=|<|>");
        String inner = whileItem;

        //Changes String to .equals
        if (Util.findType(args[0], this).equals("String")) inner = args[0] + ".equals(" + args[1] + ")";
        //Finishes
        out += getFile().getIndentations().get(getLines().size() - 1 + getStartingNum()) + "} while (" + inner + ");\n";
        return out;
    }
}
