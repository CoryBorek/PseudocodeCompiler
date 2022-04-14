package com.github.CoryBorek.PseudocodeCompiler.lib.java;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.core.CallFunction;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.ConstructorFunction;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.MainFunction;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.NormalFunction;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.CompilationItem;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.DataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseClass extends CompilationItem {

    private String className;
    boolean isPublic = false;

    public BaseClass(List<String> lines, int startingNum, JavaPseudoFile file, String className) {
        super(lines, startingNum, file, file);
        this.className = className;
        if (getFile().getFileName().equals(className)) isPublic = true;
        setup();
        updateMethodAttributes();
    }

    private void updateMethodAttributes() {
        Map<String,ArrayList<String>> varTypes = new HashMap<>();
        Map<String,Boolean> varStatic = new HashMap<>();
        for (BaseCompiler child : getChildren()) {

            if (child instanceof BaseFunction) {
                for (BaseCompiler grandchild : child.getChildren()) {
                    if (grandchild instanceof CallFunction) {
                        ArrayList<String> data = ((CallFunction) grandchild).getData();
                        String name = ((CallFunction) grandchild).getName();
                        ArrayList<String> types = new ArrayList<>();
                        for (String datum : data) {
                            types.add(((BaseFunction) child).getType(datum));
                        }
                        if (child instanceof MainFunction) varStatic.put(name, true);
                        else if (child instanceof NormalFunction) {
                            varStatic.put(name, ((NormalFunction) child).getStatic());
                        }
                        varTypes.put(name, types);
                    }
                }
            }

        }
        for (BaseCompiler child : getChildren()) {
            if (child instanceof NormalFunction) {
                NormalFunction f = ((NormalFunction) child);
                if (varTypes.containsKey(f.getName())) {
                    ArrayList<String> values = varTypes.get(f.getName());
                    for (int i = 0; i < values.size(); i++) {
                        if (f.getVars().size() == values.size() && values.size() > 0) {
                            ((NormalFunction) child).updateTypes(f.getVars().get(i), values.get(i));
                        }
                    }

                }
                if (varStatic.containsKey(f.getName())) {
                    ((NormalFunction) child).setStatic(varStatic.get(f.getName()));
                }
                ((NormalFunction) child).updateVariables();
            }
            else if (child instanceof ConstructorFunction) {
                ConstructorFunction f = ((ConstructorFunction) child);
                if (varTypes.containsKey(f.getName())) {
                    ArrayList<String> values = varTypes.get(f.getName());
                    for (int i = 0; i < values.size(); i++) {
                        if (f.getVars().size() == values.size() && values.size() > 0) {
                            ((ConstructorFunction) child).updateTypes(f.getVars().get(i), values.get(i));
                        }
                    }

                }
                ((ConstructorFunction) child).updateVariables();
            }
        }
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
