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
    private String operation = "";
    private boolean updating = false;
    private boolean isConstant = false;
    private boolean isObject = false;
    public DataType(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(line, startingNum, file, parent);
    }

    @Override
    public void setup() {
        isObject = false;
        if (getLine().startsWith("CREATE ")) {
            if (getLine().contains("=")) {
                String declaration = getLine().substring(0, getLine().indexOf("="));
                String instantiation = getLine().substring(getLine().indexOf("=") + 1);
                value = instantiation.trim();
                operation = "=";
                type = Util.findType(instantiation.replaceAll(" ", ""), getParent());
                name = declaration.replaceAll("CREATE | ", "");
            }
            else if (getLine().contains(" AS ")) {
                String[] args = getLine().split("CREATE | AS | ");

                name = args[1];
                type = args[args.length-1];
                isObject = true;
            }
                else {

                name = getLine().replaceAll("CREATE | ", "");
                type = "var";
            }
        }
        else if (getLine().startsWith("PARAM ")) {
            if (getLine().contains("=")) {
                String declaration = getLine().substring(0, getLine().indexOf("="));
                String instantiation = getLine().substring(getLine().indexOf("=") + 1);
                value = instantiation.trim();
                operation = "=";
                type = Util.findType(instantiation.replaceAll(" ", ""), getParent());
                name = declaration.replaceAll("PARAM | ", "");
            }
            else {
                name = getLine().replaceAll("PARAM | ", "");
                type = "var";
            }
        }
        else if (getLine().startsWith("CONSTANT ")) {
            isConstant = true;
            if (getLine().contains("=")) {
                String declaration = getLine().substring(0, getLine().indexOf("="));
                String instantiation = getLine().substring(getLine().indexOf("=") + 1);
                value = instantiation.trim();
                operation = "=";
                type = Util.findType(instantiation.replaceAll(" ", ""), getParent());
                name = declaration.replaceAll("CONSTANT | ", "");
            }  else if (getLine().contains(" AS ")) {
                String[] args = getLine().split("CONSTANT |AS| ");

                name = args[0];
                type = args[1];
                isObject = true;
            }
            else {

                name = getLine().replaceAll("CONSTANT | ", "");
                type = "var";
            }
        }
        else {
            updating = true;
            if ((getLine().contains("++") || getLine().contains("--")) && getLine().split("\\+\\+|--").length > 0) {
                String[] split = getLine().split("\\+\\+|--");
                name = split[0].replaceAll("CREATE| ", "").trim();
                value = "";
                operation = getLine().substring(getLine().indexOf(name) + name.length());
                if (getParent() instanceof BaseFunction) {
                    type = ((BaseFunction) getParent()).getType(name);
                }
                if (getParent() instanceof BaseClass) {
                    type = ((BaseClass) getParent()).getType(name);
                }
                setNewLine(name + operation + ";");
                return;
            }
            else if ((getLine().contains("+=") || getLine().contains("-=") && getLine().split("\\+=|-=").length > 1)) {
                String[] split = getLine().split("\\+=|-=");
                String declaration = split[0];
                String instantiation = split[1];
                value = instantiation.trim();

                type = Util.findType(instantiation.replaceAll(" ", ""), getParent());
                name = declaration.replaceAll("CREATE | ", "");
                operation = getLine().substring(getLine().indexOf(name) + name.length(),getLine().indexOf(value));
                if (getParent() instanceof BaseFunction) {
                    ((BaseFunction) getParent()).updateTypes(name, type);
                }
                if (getParent() instanceof BaseClass) {
                    ((BaseClass) getParent()).updateTypes(name, type);
                }
            }
            else if (getLine().contains("=") && getLine().split("=").length > 1) {
                System.out.println(" uses = ");
                String[] split = getLine().split("=");
                String declaration = split[0];
                String instantiation = split[1];

                operation = "=";
                System.out.println(instantiation);
                if (instantiation.trim().startsWith("NEW")) {
                    value = instantiation.trim().replaceAll("NEW ", "new ");
                    type = instantiation.replaceAll("NEW | ", "").substring(0, instantiation.replaceAll("NEW | ", "").indexOf("(")).trim();
                    name = declaration.trim();
                }
                else {
                        value = instantiation.trim();
                        type = Util.findType(instantiation.replaceAll(" ", ""), getParent());
                        name = declaration.trim();
                    }
                    if (getParent() instanceof BaseFunction) {
                        ((BaseFunction) getParent()).updateTypes(name, type);
                    }
                    if (getParent() instanceof BaseClass) {
                        ((BaseClass) getParent()).updateTypes(name, type);
                    }
            }
        }
        setNewLine();
    }

    public void setType(String newType) {
        type = newType;
        System.out.println("Line: " + getLine());
        if (getLine().contains("=")) {
            if (!(getLine().contains("CONSTANT") || getLine().contains("CREATE") || getLine().contains("PARAM"))) updating = true;
            String declaration = getLine().substring(0, getLine().indexOf("=")).replaceAll("CONSTANT|CREATE|PARAM| ", "");
            operation = "=";
            String instantiation = getLine().substring(getLine().indexOf("=") + 1);
            value = instantiation;
            name = declaration;
        }
        setNewLine();

    }

    public void updateType() {
        if (getType().equals("var") && getLine().contains("=")) {
            String declaration = getLine().substring(0, getLine().indexOf("=")).replaceAll("CONSTANT|CREATE|PARAM| ", "");
            String instantiation = getLine().substring(getLine().indexOf("=") + 1);
            value = instantiation;
            type = Util.findType(instantiation.replaceAll(" ", ""), getParent());
            name = declaration;
            System.out.println("Line: " + getLine());
            if (!(getLine().startsWith("CONSTANT") || getLine().startsWith("CREATE") || getLine().startsWith("PARAM"))) updating = true;
            if (getParent() instanceof BaseFunction) {
                ((BaseFunction) getParent()).updateTypes(name, type);
            }
            if (getParent() instanceof BaseClass) {
                ((BaseClass) getParent()).updateTypes(name, type);
            }
        }
        setNewLine();
    }

    public void setNewLine() {
        String temp = "";
        if (isConstant) temp += "final ";
        if (!updating) temp += getType().trim() + " ";
        temp += getName().trim();
        if (value != null) temp += " " + operation.trim() + " " + value.trim();
        temp += ";";
        super.setNewLine(temp);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean getUpdating() {
        return updating;
    }
}
