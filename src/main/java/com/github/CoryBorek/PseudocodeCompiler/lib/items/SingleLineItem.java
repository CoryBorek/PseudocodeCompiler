package com.github.CoryBorek.PseudocodeCompiler.lib.items;

import com.github.CoryBorek.PseudocodeCompiler.Util;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;

import java.util.Collections;

public abstract class SingleLineItem extends CompilationItem {

    //attributes
    private String newLine = "";
    private String comment = "";
    private String line;


    /**
     * Constructor
     * @param line Current line
     * @param startingNum Index of where this line is
     * @param file The currently converting file
     * @param parent Parent object
     */
    public SingleLineItem(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        //compiles to Compilation item
        super(Collections.singletonList(line), startingNum, file, parent);
        //Sets the line item
        this.line = line;
        //Lines with comments
        if (Util.hasComment(getLines().get(0)) && !(this instanceof CommentLine)) {
            //Split line into array
            String[] commentsArr = getLines().get(0).split("§§");

            //Outputs a comment Line
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
        //runs setup
        setup();
    }

    /**
     * Returns the current line
     * @return Line
     */
    public String getLine() {
        return line;
    }

    /**
     * Returns the outputted line
     * @return Outputted Line
     */
    protected String getNewLine() {
        return newLine;
    }

    /**
     * Sets the new line
     * @param newLine Line to ste
     */
    protected void setNewLine(String newLine) {
        this.newLine = newLine + comment + "\n";
    }

    /**
     * Compiles a line
     * @return
     */
    @Override
    public String compile() {
        return getFile().getIndentations().get(getStartingNum()) + newLine;
    }
}
