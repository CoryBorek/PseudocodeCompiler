package com.github.CoryBorek.PseudocodeCompiler.impl.java.core;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.SingleLineItem;

import java.util.ArrayList;

public class CallFunction extends SingleLineItem {
    private ArrayList<String> data;
    private String name;
    public CallFunction(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(line, startingNum, file, parent);
    }

    public ArrayList<String> getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setup() {
        name = getLine().substring(0, getLine().indexOf("(")).replaceAll(" ", "");
        data = new ArrayList<>();
    if (!getLine().contains("()")) {
        String dataIn = getLine().substring(getLine().indexOf("(") + 1, getLine().lastIndexOf(")"));
        String[] dataArr = dataIn.split(",");
        for (String item : dataArr) {
            this.data.add(item.trim());
        }
    }
        setNewLine(getLine() + ";\n");

    }

}
