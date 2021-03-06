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

/**
 * Compiles a function
 */
public abstract class BaseFunction extends CompilationItem {

    /**
     * Constructor
     * @param lines All lines in the function
     * @param startingNum Starting value for file
     * @param file File being converted
     * @param parent Parent Object
     */
    public BaseFunction(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(lines, startingNum, file, parent);
        //Runs the setup value
        setup();
    }


    /**
     * Sets up the function
     */
    @Override
    public void setup() {
        //Sets this as a BaseCompiler
        BaseCompiler item = this;
        //Stores subfunctions
        int[] subFunction = new int[2];
        //Current type
        String subType = "";
        //How many functions are we in?
        int deep = 0;
        for (int j = 1; j < getLines().size(); j++) {
            String line = getLines().get(j);
            int startingNum = getLines().indexOf(line) + getStartingNum();
            //Sets the current as an if statment
            if (line.startsWith("IF")) {

                if (deep == 0) {
                    subFunction = new int[2];
                    subFunction[0] = startingNum;
                    subType = "if";
                }
                deep++;
            }
            //Else if statements
            else if (line.startsWith("ELSE IF")) {

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
            }
            //Else statements
            else if (line.startsWith("ELSE")) {
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
            }
            //Ends the if statement and adds it as a child
            else if (line.startsWith("END IF")) {
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
            //For loops
            else if (line.startsWith("FOR")) {
                if (deep == 0) {
                    subFunction = new int[2];
                    subFunction[0] = startingNum;
                    subType = "for";
                }
                deep++;
            }
            //Ends the for loop and adds the child
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
            //Do (for whiles)
            else if (line.startsWith("DO")) {
                if (deep == 0) {
                    subFunction = new int[2];
                    subFunction[0] = startingNum;
                    subType = "do";
                }
                deep++;
            }
            //While loop
            else if (line.startsWith("WHILE")) {
                //Ends do while and adds the child
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
                //Regular while
                else if (subType.equals("")){
                    if (deep == 0) {
                        subFunction = new int[2];
                        subFunction[0] = startingNum;
                        subType ="while";
                    }
                    deep++;
                }

            }
            //Ends the while loop and adds the child
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
            //Normal lines
            else if (deep == 0) {
                //Commments
                if (line.startsWith("????")) {
                    getChildren().add(new CommentLine(line, startingNum, getFile(), this));
                    continue;
                }
                //Blank Lines
                else if (line.replaceAll(" ", "").equals("")) {
                    getChildren().add(new BlankLine(startingNum, getFile(), item));
                    continue;
                }
                //Data types
                else if (line.split("=|\\+\\+|--|\\+=|-=").length > 0 && hasVar(line.split("=|\\+\\+|--|\\+=|-=")[0].replaceAll(" ", ""))) {
                    getChildren().add(new DataType(line, startingNum, getFile(), this));
                    continue;
                }
                //Declaring data types
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
                //Declaring constants
                else if (line.split(" ")[0].equals("CONSTANT")) {
                    getChildren().add(new DataType(line, startingNum, getFile(), this));
                }
                //Print lines
                else if (line.split(" ")[0].equals("PRINTLINE")) {
                    getChildren().add(new Printline(line, startingNum, getFile(), item));
                }
                //Breaks
                else if (line.split(" ")[0].equals("BREAK")) {
                    getChildren().add(new Break(line, startingNum, getFile(), item));
                }
                //Continues
                else if (line.split(" ")[0].equals("CONTINUE")) {
                    getChildren().add(new Continue(line, startingNum, getFile(), item));
                }
                //Prints
                else if (line.split(" ")[0].equals("PRINT")) {
                    getChildren().add(new Print(line, startingNum, getFile(), item));
                }
                //Read values
                else if (line.split(" ")[0].equals("READ")) {
                    getChildren().add(new Read(line, startingNum, getFile(), item));
                }
                //functions
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
