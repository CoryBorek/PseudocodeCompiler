package com.github.CoryBorek.PseudocodeCompiler.lib.items;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;

public class CommentLine extends SingleLineItem {

    //Beginning of a comment for this program
    public static String commmentStarter = "//";

    /**
     * Comment Lines
     * @param line current Line
     * @param startingNum value that the line is at
     * @param file Current file
     * @param parent Parent object
     */
    public CommentLine(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(line, startingNum, file, parent);
    }

    /**
     * Sets up conversion
     */
    @Override
    public void setup() {
        String newLine = commmentStarter + getLine().replace("§§", "");
        setNewLine(newLine);
    }

}
