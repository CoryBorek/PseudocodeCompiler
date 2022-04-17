package com.github.CoryBorek.PseudocodeCompiler.lib.items;

import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;

import java.util.ArrayList;
import java.util.List;

public abstract class CompilationItem extends BaseCompiler {

    //attrubutes
    private List<String> lines;
    private JavaPseudoFile file;
    private BaseCompiler parent;
    private int startingNum;

    /**
     * Constructor
     * @param lines Lines in the item
     * @param startingNum Starting value in file
     * @param file Parent file
     * @param parent Parent object
     */
    public CompilationItem(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        this.lines = lines;
        this.file = file;
        this.parent = parent;
        this.startingNum = startingNum;
    }

    /**
     * Constructor without parent
     * @param lines
     * @param startingNum
     * @param file
     */
    public CompilationItem(ArrayList<String> lines, int startingNum, JavaPseudoFile file) {
        this.lines = lines;
        this.file = file;
        this.parent = null;
        this.startingNum = startingNum;
    }

    /**
     * Gets the parent ouject
     * @return
     */
    public BaseCompiler getParent() {
        return parent;
    }

    /**
     * Gets the starting number
     * @return
     */
    public int getStartingNum() {
        return startingNum;
    }

    /**
     * Returns lines
     * @return
     */
    public List<String> getLines() {
        return lines;
    }

    /**
     * Gets the File
     * @return
     */
    public JavaPseudoFile getFile() {
        return file;
    }



}
