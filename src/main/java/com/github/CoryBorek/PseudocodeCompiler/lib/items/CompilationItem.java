package com.github.CoryBorek.PseudocodeCompiler.lib.items;

import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;

import java.util.ArrayList;
import java.util.List;

public abstract class CompilationItem extends BaseCompiler {

    private List<String> lines;
    private JavaPseudoFile file;
    private BaseCompiler parent;
    private int startingNum;


    public CompilationItem(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        this.lines = lines;
        this.file = file;
        this.parent = parent;
        this.startingNum = startingNum;
    }
    public CompilationItem(ArrayList<String> lines, int startingNum, JavaPseudoFile file) {
        this.lines = lines;
        this.file = file;
        this.parent = null;
    }

    public BaseCompiler getParent() {
        return parent;
    }

    public int getStartingNum() {
        return startingNum;
    }
    public List<String> getLines() {
        return lines;
    }

    public JavaPseudoFile getFile() {
        return file;
    }



}
