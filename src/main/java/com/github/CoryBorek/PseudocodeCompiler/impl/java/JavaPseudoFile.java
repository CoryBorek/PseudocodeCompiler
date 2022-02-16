package com.github.CoryBorek.PseudocodeCompiler.impl.java;

import com.github.CoryBorek.PseudocodeCompiler.Util;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.PseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.BlankLine;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.CommentLine;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseClass;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.classes.MainClass;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.classes.NormalClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class JavaPseudoFile extends PseudoFile {
    private ArrayList<String> imports = new ArrayList<>();

    private File convertedFile;
    public JavaPseudoFile(File file) {
        super(file, ".java");
    }


    public ArrayList<String> getImports() {
        return imports;
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
            //System.out.println(line);

                System.out.println(currentName + line);
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
                    System.out.println(line);
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

    @Override
    public String compile() {
        String out = "";
        for (String importS : imports) {
            out += importS + "\n";
        }
        for (BaseCompiler child : getChildren()) {
            out += child.compile();
        }
        return out;
    }
}
