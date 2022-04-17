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
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Pseudo File being converted to Java
 */
public class JavaPseudoFile extends PseudoFile {

    //Attributes
    private ArrayList<String> imports = new ArrayList<>();

    /**
     * Constructor
     * @param file File being converted
     */
    public JavaPseudoFile(File file) {
        super(file, ".java");
    }

    /**
     * Gets the imports
     * @return imports
     */
    public ArrayList<String> getImports() {
        return imports;
    }


    /**
     * Adds an import
     * @param item Imported item
     */
    public void addImport(String item) {
        boolean found = false;
        for (int i = 0; i < imports.size(); i++) {
            if (imports.get(i).equals(item)) found = true;
        }

        if (!found) getImports().add(item);
    }


    /**
     * Sets up the compilation
     */
    @Override
    public void setup() {
       int[] current = new int[2];
        String currentName = "";
        String currentType = "";
        boolean currentlyRunning = false;
        for (int i = 0; i < getLines().size(); i++) {

            //Current line
            String fullLine = getLines().get(i);

            //Indentation for line
            String indent = Util.getIndent(fullLine);
            //Rest of line
            String line = fullLine.replaceAll(indent, "");
                //Classes
                if (line.startsWith("CLASS")) {
                    currentlyRunning = true;
                    current = new int[2];
                    current[0] = i;
                    currentName = line.replaceAll("CLASS ", "");
                    currentType = "CLASS";
                }
                //MAIN
                else if (line.startsWith("MAIN") && !currentlyRunning) {
                    currentlyRunning = true;
                    current = new int[2];
                    current[0] = i;
                    currentName = "MAIN";
                    currentType = "MAIN";
                }
                //Adds a class as a child
                else if (line.startsWith("END " + currentType)) {
                    current[1] = i;
                    addClassChild(current, currentName);
                    currentlyRunning = false;
                }
                //Blank lines
                else if (!currentlyRunning && line.replaceAll(" ", "").equals("")) {
                    getChildren().add(new BlankLine(i,this, this));
                }
                //Comments
                else if (!currentlyRunning && Util.hasComment(line)) {
                    String[] commentsArr = line.split("§§");

                    if (commentsArr.length > 1 || line.startsWith("§§")) {
                        int temp = commentsArr.length - 1;
                        getChildren().add(new CommentLine( commentsArr[temp], i,this, this));

                    }
                }

        }
    //Checks data types
    checkTypes();
    }


    /**
     * Adds a clas as a child
     * @param pos
     * @param name
     */
    private void addClassChild(int[] pos, String name) {
            //Create class objects
            ArrayList<String> classLines = new ArrayList<>();
            BaseClass baseClass;
            //Adds lines
            for (int j = pos[0]; j < pos[1] + 1; j++) {
                classLines.add(getLines().get(j));
            }

            //Main class
            if (name.equals("MAIN")) {
                baseClass = new MainClass(classLines, pos[0],this, getFileName());

            }
            //Regular class
            else {
                baseClass = new NormalClass(classLines, pos[0],this, name);
            }
            //Adds the class as a child
            assert baseClass != null;
            getChildren().add(baseClass);
    }

    /**
     * Double checks data types
     */
    private void checkTypes() {
        //Uses to ask for data types not found
        Scanner scan = new Scanner(System.in);

        //children
        ArrayList<BaseCompiler> children = this.getChildren();

        //Loops over types
        loopTypes(scan, children);
        loopTypes(scan, children);
    }

    /**
     * Loops over unfound types to figure them out, This is also a recursive function
     * @param in Input Scanner
     * @param children Children of this file
     */
    private void loopTypes(Scanner in, ArrayList<BaseCompiler> children) {

        for (int i = 0; i < children.size(); i++) {
            //Data Types
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
            }
            //Returns (for functions)
            else if (children.get(i) instanceof Return) {
                Return child = (Return) children.get(i);
                if (child.getParent() instanceof NormalFunction) {
                    NormalFunction p = (NormalFunction) child.getParent();
                    p.setReturnType(Util.findType(child.getValue(), p));
                }

            }
            //Sets the variables being read
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

    /**
     * Compiles the file
     * @return
     */
    @Override
    public String compile() {
        String out = "";

        //Imports
        for (String importS : imports) {
            out += "import " + importS + ";\n";
        }

        //Compiles children
        for (BaseCompiler child : getChildren()) {
            out += child.compile();
        }

        //Finishes the file
        return out;
    }
}
