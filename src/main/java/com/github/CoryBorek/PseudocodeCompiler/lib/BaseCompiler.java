package com.github.CoryBorek.PseudocodeCompiler.lib;

import java.util.ArrayList;

/**
 * Core item that allows anything to be compiled
 */
public abstract class BaseCompiler {

    //children
    private ArrayList<BaseCompiler> children = new ArrayList<>();

    /**
     * Gives the children object
     * @return Children
     */
    public ArrayList<BaseCompiler> getChildren() {
        return children;
    }

    public abstract void setup();

    public abstract String compile();


}
