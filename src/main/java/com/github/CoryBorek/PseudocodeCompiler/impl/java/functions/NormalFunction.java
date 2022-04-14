package com.github.CoryBorek.PseudocodeCompiler.impl.java.functions;

import com.github.CoryBorek.PseudocodeCompiler.Util;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.core.Return;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.DataType;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseClass;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

public class NormalFunction extends BaseFunction {



    private boolean isStatic = false;
    private String name;
    private String returnType = "void";
    private ArrayList<String> vars;
    public NormalFunction(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(lines, startingNum, file, parent);
    }

    public String getName() {
        return name;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public boolean getStatic() {
        return isStatic;
    }

    public ArrayList<String> getVars() {
        return vars;
    }

    @Override
    public void setup() {
        this.vars = new ArrayList<>();
        String declaration = getLines().get(0);
        this.name = declaration.substring(0,declaration.indexOf("(")).replaceAll("METHOD ", "").trim();
        int hasParams = declaration.indexOf("parameters:");
        if (hasParams != -1) {
            String params = declaration.substring(hasParams + "parameters:".length(), declaration.lastIndexOf(")"));
            String[] args = params.split(",");
            for (int i = 0; i < args.length; i++) {
                String arg = args[i].trim();
                DataType parameter = new DataType("PARAM " + arg, getStartingNum(), getFile(), this);
                vars.add(parameter.getName());
                getChildren().add(parameter);
            }
        }
        super.setup();

        for (int i = 0; i < getLines().size(); i++) {
         if (getLines().get(i).startsWith("RETURN")) {
             Return item = new Return(getLines().get(i), getStartingNum() + i, getFile(), this);
             setReturnType(item.getType());
             getChildren().add(item);
         }
        }
    }

    public void updateVariables() {
        for (BaseCompiler child : getChildren()) {
            if (child instanceof DataType) {
                ((DataType) child).updateType();
            }
        }
    }

    public void setReturnType(String type) {
        returnType = type;
    }

    /**
    private void checkStatic() {
        for (BaseCompiler sibling: getParent().getChildren()) {
            if (sibling instanceof MainFunction) {
                ((MainFunction) sibling).getChildren().
            }
        }

    }*/

    @Override
    public String compile() {
        String out = getFile().getIndentations().get(getStartingNum()) + "public ";
        if (isStatic) out += "static ";
        out += returnType + " " + name + "(";
        ArrayList<DataType> types = new ArrayList<>();
        for (String var : vars) {
            for (BaseCompiler child : getChildren()) {
                if (child instanceof DataType) {
                    DataType type = ((DataType) child);
                    if (type.getName().equals(var)) {
                        out += type.getType() + " " + type.getName() + ", ";
                        types.add(type);
                    }
                }
            }
        }
        ArrayList<BaseCompiler> remove = new ArrayList<>();
        for (DataType type : types) {
            for (BaseCompiler child : getChildren()) {
                if (child.equals(type)){
                    System.out.println("Removing: " + type.getName());
                    remove.add(type);
                }

            }
        }
        for (BaseCompiler i : remove)
        {
            getChildren().remove(i);
        }
        if (out.endsWith(", ")) out = out.substring(0, out.length() - 2);
        out += ") {\n";

        for (BaseCompiler child : getChildren()) {
            out += child.compile();
        }

        out += getFile().getIndentations().get(getLines().size() - 1 + getStartingNum()) + "}\n";
        return out;
    }
}
