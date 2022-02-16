package com.github.CoryBorek.PseudocodeCompiler.impl.java.classes;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseClass;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.MainFunction;

import java.util.ArrayList;
import java.util.List;

public class MainClass extends BaseClass {
    ArrayList<String> out = new ArrayList<>();
    public MainClass(List<String> lines, int startingNum, JavaPseudoFile file, String className) {
        super(lines, startingNum, file, className);

    }

    @Override
    public void setup() {
        getChildren().add(new MainFunction(getLines(), getStartingNum(), getFile(), this));
    }
}
