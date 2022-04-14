package com.github.CoryBorek.PseudocodeCompiler.impl.java;

import com.github.CoryBorek.PseudocodeCompiler.Util;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.core.Read;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.core.Return;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.functions.NormalFunction;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.PseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.BlankLine;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.CommentLine;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.DataType;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseClass;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.classes.MainClass;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.classes.NormalClass;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class JavaPseudoFile extends PseudoFile {
    private ArrayList<String> imports = new ArrayList<>();

    private File convertedFile;
    public JavaPseudoFile(File file) {
        super(file, ".java");
    }


    public ArrayList<String> getImports() {
        return imports;
    }


    public void addImport(String item) {
        boolean found = false;
        for (int i = 0; i < imports.size(); i++) {
            if (imports.get(i).equals(item)) found = true;
        }

        if (!found) getImports().add(item);
    }


    @Override
    public void setup() {
       int[] current = new int[2];
        String currentName = "";
        String currentType = "";
        boolean currentlyRunning = false;
        for (int i = 0; i < getLines().size(); i++) {
            String fullLine = getLines().get(i);

            String indent = Util.getIndent(fullLine);
            String line = fullLine.replaceAll(indent, "");

                if (line.startsWith("CLASS")) {
                    currentlyRunning = true;
                    current = new int[2];
                    current[0] = i;
                    currentName = line.replaceAll("CLASS ", "");
                    currentType = "CLASS";
                } else if (line.startsWith("MAIN") && !currentlyRunning) {
                    currentlyRunning = true;
                    current = new int[2];
                    current[0] = i;
                    currentName = "MAIN";
                    currentType = "MAIN";
                } else if (line.startsWith("END " + currentType)) {
                    current[1] = i;
                    addClassChild(current, currentName);
                    currentlyRunning = false;
                }
                else if (!currentlyRunning && line.replaceAll(" ", "").equals("")) {
                    getChildren().add(new BlankLine(i,this, this));
                }
                else if (!currentlyRunning && Util.hasComment(line)) {
                    String[] commentsArr = line.split("§§");

                    if (commentsArr.length > 1 || line.startsWith("§§")) {
                        int temp = commentsArr.length - 1;
                        getChildren().add(new CommentLine( commentsArr[temp], i,this, this));

                    }
                }

        }

    checkTypes();
    }


    private void addClassChild(int[] pos, String name) {
            //Create class objects
            ArrayList<String> classLines = new ArrayList<>();
            BaseClass baseClass;
            for (int j = pos[0]; j < pos[1] + 1; j++) {

                classLines.add(getLines().get(j));
            }
            if (name.equals("MAIN")) {
                baseClass = new MainClass(classLines, pos[0],this, getFileName());

            } else {
                baseClass = new NormalClass(classLines, pos[0],this, name);
            }
            assert baseClass != null;
            getChildren().add(baseClass);
    }

    private void checkTypes() {
        Scanner scan = new Scanner(System.in);

        ArrayList<BaseCompiler> children = this.getChildren();
        loopTypes(scan, children);
        loopTypes(scan, children);
    }

    private void loopTypes(Scanner in, ArrayList<BaseCompiler> children) {
        System.out.println(children);
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) instanceof DataType) {
                DataType child = (DataType) children.get(i);
                if (child.getType().equals("var") && !child.getUpdating()) {
                    System.out.println("What is the data type of variable: " + child.getName() + "?");
                    String type = in.nextLine();
                    if (child.getParent() instanceof BaseFunction)
                        ((BaseFunction) child.getParent()).updateTypes(child.getName(), type);
                    else if (child.getParent() instanceof BaseClass)
                        ((BaseClass) child.getParent()).updateTypes(child.getName(), type);
                }
            } else if (children.get(i) instanceof Return) {
                Return child = (Return) children.get(i);
                if (child.getParent() instanceof NormalFunction) {
                    NormalFunction p = (NormalFunction) child.getParent();
                    p.setReturnType(Util.findType(child.getValue(), p));
                }

            }
            else if (children.get(i) instanceof Read) {
                Read child = (Read) children.get(i);
                if (child.getType().equals("var")) {
                    child.updateType();
                }
            }
            else if (children.get(i).getChildren().size() == 0) continue;
            else loopTypes(in, children.get(i).getChildren());
        }

    }

    @Override
    public String compile() {
        String out = "";
        for (String importS : imports) {
            out += "import " + importS + ";\n";
        }
        for (BaseCompiler child : getChildren()) {
            out += child.compile();
        }
        return out;
    }
}
