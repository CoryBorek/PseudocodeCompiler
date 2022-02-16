package com.github.CoryBorek.PseudocodeCompiler.lib.java;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.statements.ElseIfStatement;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.statements.ElseStatement;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.statements.IFStatement;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.BlankLine;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.CommentLine;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.core.Printline;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.core.Print;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.CompilationItem;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.DataType;

import java.util.*;

public abstract class BaseFunction extends CompilationItem {

    private int deep = 0;
    public BaseFunction(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(lines, startingNum, file, parent);
        setup();
    }

    public int getDeep() {
        return deep;
    }

    @Override
    public void setup() {
        BaseCompiler item = this;

        int[] subFunction = new int[2];
        String subType = "";
        for (int j = 1; j < getLines().size(); j++) {
            String line = getLines().get(j);
            System.out.println(deep);
            int startingNum = getLines().indexOf(line) + getStartingNum();
            if (line.startsWith("IF")) {

                if (deep == 0) {
                    subFunction = new int[2];
                    subFunction[0] = startingNum;
                    subType = "if";
                }
                deep++;
            } else if (line.startsWith("ELSE IF")) {

                deep--;
                if (deep == 0) {
                    subFunction[1] = startingNum;
                    ArrayList<String> ifLines = new ArrayList<>();
                    for (int i = subFunction[0]; i < subFunction[1]; i++) {
                        ifLines.add(getFile().getLines().get(i));
                    }
                    if (subType.equals("if"))
                        getChildren().add(new IFStatement(ifLines, subFunction[0], getFile(), this));
                    else if (subType.equals("elseIf"))
                        getChildren().add(new ElseIfStatement(ifLines, subFunction[0], getFile(), this));
                    subFunction = new int[2];
                    subFunction[0] = startingNum;
                    subType = "elseIf";
                }
                deep++;
            } else if (line.startsWith("ELSE")) {
                deep--;
                if (deep == 0) {
                    subFunction[1] = startingNum;
                    ArrayList<String> ifLines = new ArrayList<>();
                    for (int i = subFunction[0]; i < subFunction[1]; i++) {
                        ifLines.add(getFile().getLines().get(i));
                    }
                    if (subType.equals("if"))
                        getChildren().add(new IFStatement(ifLines, subFunction[0], getFile(), this));
                    else if (subType.equals("elseIf"))
                        getChildren().add(new ElseIfStatement(ifLines, subFunction[0], getFile(), this));
                    subFunction = new int[2];
                    subFunction[0] = startingNum;
                    subType = "else";
                }
                deep++;

            } else if (line.startsWith("END IF")) {
                deep--;
                if (deep == 0) {
                    subFunction[1] = startingNum;
                    ArrayList<String> ifLines = new ArrayList<>();
                    for (int i = subFunction[0]; i < subFunction[1]; i++) {
                        ifLines.add(getFile().getLines().get(i));
                    }
                    if (subType.equals("if"))
                        getChildren().add(new IFStatement(ifLines, subFunction[0], getFile(), this));
                    else if (subType.equals("elseIf"))
                        getChildren().add(new ElseIfStatement(ifLines, subFunction[0], getFile(), this));
                    else if (subType.equals("else"))
                        getChildren().add(new ElseStatement(ifLines, subFunction[0], getFile(), this));
                }
            }
            if (deep == 0) {
                if (line.startsWith("§§")) {
                    getChildren().add(new CommentLine(line, startingNum, getFile(), this));
                    continue;
                } else if (line.replaceAll(" ", "").equals("")) {
                    getChildren().add(new BlankLine(startingNum, getFile(), item));
                    continue;
                } else if (line.contains("=") && hasVar(line.substring(0, line.indexOf("=")).replaceAll(" ", ""))) {
                    getChildren().add(new DataType(line, startingNum, getFile(), this));
                    continue;
                }
                switch (line.split(" ")[0]) {
                    case "CREATE":
                        getChildren().add(new DataType(line, startingNum, getFile(), this));
                        break;
                    case "CONSTANT":
                        getChildren().add(new DataType(line, startingNum, getFile(), this));
                        break;
                    case "PRINTLINE":
                        getChildren().add(new Printline(line, startingNum, getFile(), item));
                        break;
                    case "PRINT":
                        getChildren().add(new Print(line, startingNum, getFile(), item));
                        break;
                }
            }
        }


    }

    protected void checks() {

    }
    public void updateTypes(String name, String newType) {
        boolean check = false;
        for (BaseCompiler child : getChildren())
            if (child instanceof DataType && ((DataType) child).getName().equals(name)) {
                ((DataType) child).setType(newType);
                check = true;
            }
        if (!check) {
            BaseCompiler parent = getParent();
            if (parent instanceof BaseClass) {
               ((BaseClass) parent).updateTypes(name, newType);
            }
            else if (parent instanceof BaseFunction) {
                ((BaseFunction) parent).updateTypes(name, newType);
            }
        }
    }

    public boolean hasVar(String name) {
        for (BaseCompiler child : getChildren())
            if (child instanceof DataType && ((DataType) child).getName().equals(name)) {
                return true;
            }
        BaseCompiler parent = getParent();
        if (parent instanceof BaseClass) {
            return ((BaseClass) parent).hasVar(name);
        }
        else if (parent instanceof BaseFunction) {
            return ((BaseFunction) parent).hasVar(name);
        }
        return false;
    }

    public String getType(String name) {
        for (BaseCompiler child : getChildren())
            if (child instanceof DataType && ((DataType) child).getName().equals(name)) {
                return ((DataType) child).getType();
            }
        BaseCompiler parent = getParent();
        if (parent instanceof BaseClass) {
            return ((BaseClass) parent).getType(name);
        }
        else if (parent instanceof BaseFunction) {
            return ((BaseFunction) parent).getType(name);
        }
        return null;
    }
}
