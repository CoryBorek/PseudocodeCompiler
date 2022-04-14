package com.github.CoryBorek.PseudocodeCompiler.impl.java.core;

import com.github.CoryBorek.PseudocodeCompiler.Util;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.SingleLineItem;

public class Read extends SingleLineItem {
    public Read(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(line, startingNum, file, parent);
    }

    String var;
    String type;
    String start;
    @Override
    public void setup() {
        var = getLine().replaceAll("READ ", "").trim();
        start = "Scanner in" + var + " = new Scanner(System.in);\n";
        this.getFile().addImport("java.util.Scanner");
        type = Util.findType(var, getParent());
        setLine();
    }

    private void setLine() {
        switch (type) {
            case "int":
                setNewLine(start + getFile().getIndentations().get(getStartingNum()) + var + " = Integer.parseInt(in" + var + ".nextLine());");
                break;
            case "double":
                setNewLine(start + getFile().getIndentations().get(getStartingNum()) + var + " = Double.parseDouble(in" + var + ".nextLine());");
                break;
            case "boolean":
                setNewLine(start + getFile().getIndentations().get(getStartingNum()) + var + " = Boolean.parseBoolean(in" + var + ".nextLine());");
                break;
            default:
                setNewLine(start +getFile().getIndentations().get(getStartingNum()) + var + " = in" + var + ".nextLine();");
        }
    }

    public String getType() {
        return type;
    }

    public void updateType() {
        type = Util.findType(var, getParent());
        setLine();
    }
}
