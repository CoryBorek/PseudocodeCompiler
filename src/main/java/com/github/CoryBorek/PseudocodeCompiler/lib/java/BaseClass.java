package com.github.CoryBorek.PseudocodeCompiler.lib.java;

import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.CompilationItem;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.DataType;

import java.util.List;

public abstract class BaseClass extends CompilationItem {

    private String className;


    boolean isPublic = false;

    public BaseClass(List<String> lines, int startingNum, JavaPseudoFile file, String className) {
        super(lines, startingNum, file, file);
        this.className = className;
        if (getFile().getFileName().equals(className)) isPublic = true;
        setup();
    }

    @Override
    public String compile() {
        String out = getFile().getIndentations().get(getStartingNum());
        if (isPublic) out += "public ";
        out += "class " + className + " {\n";

        for (BaseCompiler child : getChildren()) {
            out += child.compile();
        }
        out += getFile().getIndentations().get(getStartingNum() + getLines().size() - 1) + "}\n";
        return out;
    }

    public void updateTypes(String name, String newType) {
        for (BaseCompiler child : getChildren()) {
            if (child instanceof DataType && ((DataType) child).getName().equals(name)) {
                ((DataType) child).setType(newType);
            }
        }
    }

    public boolean hasVar(String name) {
        for (BaseCompiler child : getChildren()) {
            if (child instanceof DataType && ((DataType) child).getName().equals(name)) {
                return true;
            }
        }
        return false;
    }


    public String getType(String name) {
        for (BaseCompiler child : getChildren()) {
            if (child instanceof DataType && ((DataType) child).getName().equals(name)) {
                return ((DataType) child).getType();
            }
        }
        return null;
    }


}
