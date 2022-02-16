package com.github.CoryBorek.PseudocodeCompiler.lib.items;

import com.github.CoryBorek.PseudocodeCompiler.Util;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;

import java.util.Collections;

public abstract class SingleLineItem extends CompilationItem {

    private String newLine = "";
    private String comment = "";
    private String line;


    public SingleLineItem(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(Collections.singletonList(line), startingNum, file, parent);
        this.line = line;
        if (Util.hasComment(getLines().get(0)) && !(this instanceof CommentLine)) {
            String[] commentsArr = getLines().get(0).split("§§");

            if (commentsArr.length > 1 || getLines().get(0).startsWith("§§")) {
                int temp = commentsArr.length - 1;
                CommentLine commentLine = new CommentLine(commentsArr[temp], getStartingNum(), file, this);
                comment = commentLine.compile();
                String lineOut = "";
                for (int i = 0; i < temp; i++) {
                    lineOut += commentsArr[i];
                }
                this.line = lineOut;
            }
        }
        setup();
    }

    public String getLine() {
        return line;
    }

    protected String getNewLine() {
        return newLine;
    }

    protected void setNewLine(String newLine) {
        this.newLine = newLine + comment + "\n";
    }

    @Override
    public String compile() {
        return getFile().getIndentations().get(getStartingNum()) + newLine;
    }
}
