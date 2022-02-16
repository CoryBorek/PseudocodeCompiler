package com.github.CoryBorek.PseudocodeCompiler.lib;

import java.util.ArrayList;

public abstract class BaseCompiler {
    private ArrayList<BaseCompiler> children = new ArrayList<>();

    public ArrayList<BaseCompiler> getChildren() {
        return children;
    }

    public abstract void setup();

    public abstract String compile();


}
