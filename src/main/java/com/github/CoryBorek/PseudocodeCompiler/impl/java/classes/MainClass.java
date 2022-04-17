package com.github.CoryBorek.PseudocodeCompiler.impl.java.classes;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseClass;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.MainFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * MAIN values
 */
public class MainClass extends BaseClass {

    /**
     * Constructor
     * @param lines Lines
     * @param startingNum Starting line index
     * @param file File being converted
     * @param className Class name
     */
    public MainClass(List<String> lines, int startingNum, JavaPseudoFile file, String className) {
        super(lines, startingNum, file, className);

    }

    /**
     * Sets up the conversion
     */
    @Override
    public void setup() {
        //Adds a main function
        getChildren().add(new MainFunction(getLines(), getStartingNum(), getFile(), this));

    }

}
