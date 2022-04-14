package com.github.CoryBorek.PseudocodeCompiler.lib.java;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.core.CallFunction;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.core.Read;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.statements.*;
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

    public BaseFunction(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(lines, startingNum, file, parent);
        setup();
    }


    @Override
    public void setup() {
        BaseCompiler item = this;

        int[] subFunction = new int[2];
        String subType = "";
        int deep = 0;
        for (int j = 1; j < getLines().size(); j++) {
            String line = getLines().get(j);
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
                continue;
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
                continue;
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
                    subType = "";
                }
                continue;
            }
            else if (line.startsWith("FOR")) {
                if (deep == 0) {
                    subFunction = new int[2];
                    subFunction[0] = startingNum;
                    subType = "for";
                }
                deep++;
            }
            else if (line.startsWith("END FOR")) {
                deep--;
                if (deep == 0) {
                    subFunction[1] = startingNum;
                    ArrayList<String> forLines = new ArrayList<>();
                    for (int i = subFunction[0]; i < subFunction[1]; i++) {
                        forLines.add(getFile().getLines().get(i));
                    }
                    getChildren().add(new ForLoop(forLines, subFunction[0], getFile(), this));
                    subType = "";
                }
            }
            else if (line.startsWith("DO")) {
                if (deep == 0) {
                    subFunction = new int[2];
                    subFunction[0] = startingNum;
                    subType = "do";
                }
                deep++;
            }
            else if (line.startsWith("WHILE")) {
                if (getLines().size() > j+1 && subType.equals("do") && getLines().get(j+1).startsWith("END WHILE")) {
                    deep--;
                    if (deep == 0) {
                        subFunction[1] = startingNum;
                        ArrayList<String> forLines = new ArrayList<>();
                        for (int i = subFunction[0]; i <= subFunction[1]; i++) {
                            forLines.add(getFile().getLines().get(i));
                        }
                        getChildren().add(new DoWhileLoop(forLines, subFunction[0], getFile(), this));
                        subType = "";
                    }
                }
                else if (subType.equals("")){
                    if (deep == 0) {
                        subFunction = new int[2];
                        subFunction[0] = startingNum;
                        subType ="while";
                    }
                    deep++;
                }

            }
            else if (line.startsWith("END WHILE")) {
                if (subType.equals("while")) {
                    deep--;
                    if (deep == 0) {
                        subFunction[1] = startingNum;
                        ArrayList<String> forLines = new ArrayList<>();
                        for (int i = subFunction[0]; i < subFunction[1]; i++) {
                            forLines.add(getFile().getLines().get(i));
                        }
                        getChildren().add(new WhileLoop(forLines, subFunction[0], getFile(), this));
                    }
                    subType = "";
                }

            }
            else if (deep == 0) {
                if (line.startsWith("§§")) {
                    getChildren().add(new CommentLine(line, startingNum, getFile(), this));
                    continue;
                } else if (line.replaceAll(" ", "").equals("")) {
                    getChildren().add(new BlankLine(startingNum, getFile(), item));
                    continue;
                } else if (line.split("=|\\+\\+|--|\\+=|-=").length > 0 && hasVar(line.split("=|\\+\\+|--|\\+=|-=")[0].replaceAll(" ", ""))) {
                    getChildren().add(new DataType(line, startingNum, getFile(), this));
                    continue;
                }
                else if (line.split(" ")[0].equals("CREATE")) {
                    if (line.contains(",")) {
                        String[] args = line.replaceAll("CREATE ", "").split(",");
                        for (String arg : args) {
                            arg = arg.trim();
                            getChildren().add(new DataType("CREATE " + arg, startingNum, getFile(), this));
                        }
                    } else
                        getChildren().add(new DataType(line, startingNum, getFile(), this));
                }
                else if (line.split(" ")[0].equals("CONSTANT")) {
                    getChildren().add(new DataType(line, startingNum, getFile(), this));
                }
                else if (line.split(" ")[0].equals("PRINTLINE")) {
                    getChildren().add(new Printline(line, startingNum, getFile(), item));
                }
                else if (line.split(" ")[0].equals("BREAK")) {
                    getChildren().add(new Break(line, startingNum, getFile(), item));
                }
                else if (line.split(" ")[0].equals("CONTINUE")) {
                    getChildren().add(new Continue(line, startingNum, getFile(), item));
                }
                else if (line.split(" ")[0].equals("PRINT")) {
                    getChildren().add(new Print(line, startingNum, getFile(), item));
                }
                else if (line.split(" ")[0].equals("READ")) {
                    getChildren().add(new Read(line, startingNum, getFile(), item));
                }
                else if (line.contains("(") && line.contains(")")) {
                    getChildren().add(new CallFunction(line, startingNum, getFile(), this));
                    continue;
                }

            }

        }


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
