package com.github.CoryBorek.PseudocodeCompiler.impl.java.functions;

import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;
import java.util.List;

/**
 * Main functino
 */
public class MainFunction extends BaseFunction {

    /**
     * Constructor
     * @param lines Lines being converted
     * @param startingNum Starting index
     * @param file File being converted
     * @param parent Parent Object
     */
    public MainFunction(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(lines, startingNum, file, parent);
    }

    /**
     * Compiles the Function
     * @return
     */
    @Override
    public String compile() {

        //This is always the same
        String out = "";
        out += getFile().getIndentations().get(getStartingNum()) +
                "public static void main(String[] args) {\n";

        //Compiles children
        for (BaseCompiler child : getChildren()) {
            out += child.compile();
        }

        //Finishes compilation
        out += getFile().getIndentations().get(getStartingNum() + getLines().size() - 1) + "}\n";
        return out;
    }
}
