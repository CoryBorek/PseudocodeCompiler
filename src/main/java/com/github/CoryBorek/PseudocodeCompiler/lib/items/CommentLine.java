package com.github.CoryBorek.PseudocodeCompiler.lib.items;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;

public class CommentLine extends SingleLineItem {

    public static String commmentStarter = "//";

    public CommentLine(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(line, startingNum, file, parent);
    }

    @Override
    public void setup() {
        String newLine = commmentStarter + getLine().replace("§§", "");
        setNewLine(newLine);
    }

}
