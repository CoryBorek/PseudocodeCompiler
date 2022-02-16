package com.github.CoryBorek.PseudocodeCompiler.lib.items;

import com.github.CoryBorek.PseudocodeCompiler.Util;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseClass;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;

public class DataType extends SingleLineItem{

    private String name;
    private String type;
    private String value = null;
    private boolean updating = false;
    private boolean isConstant = false;
    public DataType(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(line, startingNum, file, parent);
    }

    @Override
    public void setup() {
        if (getLine().startsWith("CREATE ")) {
            if (getLine().contains("=")) {
                String declaration = getLine().substring(0, getLine().indexOf("="));
                String instantiation = getLine().substring(getLine().indexOf("=") + 1);
                value = instantiation;
                type = Util.findType(instantiation.replaceAll(" ", ""), getParent());
                name = declaration.replaceAll("CREATE | ", "");
            } else {
                name = getLine().replaceAll("CREATE | ", "");
                type = "var";
            }
        }
        else if (getLine().startsWith("CONSTANT ")) {
            isConstant = true;
            if (getLine().contains("=")) {
                String declaration = getLine().substring(0, getLine().indexOf("="));
                String instantiation = getLine().substring(getLine().indexOf("=") + 1);
                value = instantiation;
                type = Util.findType(instantiation.replaceAll(" ", ""), getParent());
                name = declaration.replaceAll("CONSTANT | ", "");
            } else {
                name = getLine().replaceAll("CONSTANT | ", "");
                type = "var";
            }
        }
        else {
            updating = true;
            String declaration = getLine().substring(0, getLine().indexOf("="));
            String instantiation = getLine().substring(getLine().indexOf("=") + 1);
            value = instantiation;
            type = Util.findType(instantiation.replaceAll(" ", ""), getParent());
            name = declaration.replaceAll("CREATE | ", "");
            if (getParent() instanceof BaseFunction) {
                ((BaseFunction) getParent()).updateTypes(name, type);
            }
            if (getParent() instanceof BaseClass) {
                ((BaseClass) getParent()).updateTypes(name, type);
            }
        }
        setNewLine();
    }

    public void setType(String newType) {
        type = newType;
        setNewLine();

    }

    public void setNewLine() {
        String temp = "";
        if (isConstant) temp += "final ";
        if (!updating) temp += getType() + " ";
        temp += getName();
        if (value != null) temp += " = " + value;
        temp += ";";
        super.setNewLine(temp);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
