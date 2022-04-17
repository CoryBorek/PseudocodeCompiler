package com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.statements;

import com.github.CoryBorek.PseudocodeCompiler.Util;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;

import java.util.List;

/**
 * While loop
 */
public class WhileLoop extends BaseFunction {

    /**
     * Constructor
     * @param lines Lines in loop
     * @param startingNum Starting index
     * @param file File being converted
     * @param parent Parent Object
     */
    public WhileLoop(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(lines, startingNum, file, parent);
    }

    /**
     * Compiles the loop
     * @return
     */
    @Override
    public String compile() {
        //Gets the info on the while
        String whileItem = getLines().get(0).replace("WHILE", "");
        whileItem = whileItem.substring(whileItem.indexOf("(")+1, whileItem.length() - 1);

        //Gets any arguments
        String[] args = whileItem.split("==|!=|<=|<|>=|>");
        String inner = whileItem;

        //String stuff
        if (Util.findType(args[0], this).equals("String")) inner = args[0] + ".equals(" + args[1] + ")";
        String output = getFile().getIndentations().get(getStartingNum());

        //Starts output
        output += "while (" + inner + ") {\n";

        //Compiles children
        for (BaseCompiler child : getChildren()) {
            output += child.compile();
        }

        //Finishes output
        output += getFile().getIndentations().get(getLines().size() + getStartingNum()) + "}\n";
        return output;
    }
}
