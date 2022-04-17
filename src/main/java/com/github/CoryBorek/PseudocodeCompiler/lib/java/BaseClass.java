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

/**
 * Base for all classes
 */
public abstract class BaseClass extends CompilationItem {

    //Attributes
    private String className;
    boolean isPublic = false;

    /**
     * Constructor
     * @param lines Lines in a class
     * @param startingNum The starting index value
     * @param file The File being converted
     * @param className Name of the class
     */
    public BaseClass(List<String> lines, int startingNum, JavaPseudoFile file, String className) {
        super(lines, startingNum, file, file);
        this.className = className;
        if (getFile().getFileName().equals(className)) isPublic = true;
        setup();
        updateMethodAttributes();
    }

    /**
     * Updates method attributes
     */
    private void updateMethodAttributes() {
        //Stores types and if it's static
        Map<String,ArrayList<String>> varTypes = new HashMap<>();
        Map<String,Boolean> varStatic = new HashMap<>();
        //Grabs all children
        for (BaseCompiler child : getChildren()) {

            //If it's a function
            if (child instanceof BaseFunction) {
                for (BaseCompiler grandchild : child.getChildren()) {
                    //Store the types
                    if (grandchild instanceof CallFunction) {
                        ArrayList<String> data = ((CallFunction) grandchild).getData();
                        String name = ((CallFunction) grandchild).getName();
                        ArrayList<String> types = new ArrayList<>();
                        for (String datum : data) {
                            types.add(((BaseFunction) child).getType(datum));
                        }
                        //If its a main function, make it static
                        if (child instanceof MainFunction) varStatic.put(name, true);
                        //Normal function
                        else if (child instanceof NormalFunction) {
                            varStatic.put(name, ((NormalFunction) child).getStatic());
                        }
                        //Stores the types
                        varTypes.put(name, types);
                    }
                }
            }

        }
        //updates data types
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

    /**
     * Compiles a class
     * @return
     */
    @Override
    public String compile() {
        //Gets the indentation of the first line
        String out = getFile().getIndentations().get(getStartingNum());
        //Outputs if it's public
        if (isPublic) out += "public ";
        //Add the class info
        out += "class " + className + " {\n";

        //Compile children
        for (BaseCompiler child : getChildren()) {
            out += child.compile();
        }
        //End the class
        out += getFile().getIndentations().get(getStartingNum() + getLines().size() - 1) + "}\n";
        //Return the class file info
        return out;
    }

    /**
     * Updates data types
     * @param name Name of datatype
     * @param newType New DataType
     */
    public void updateTypes(String name, String newType) {
        for (BaseCompiler child : getChildren()) {
            if (child instanceof DataType && ((DataType) child).getName().equals(name)) {
                ((DataType) child).setType(newType);
            }
        }
    }

    /**
     * Checks if an object has a datatype
     * @param name
     * @return
     */
    public boolean hasVar(String name) {
        for (BaseCompiler child : getChildren()) {
            if (child instanceof DataType && ((DataType) child).getName().equals(name)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Gets the data type
     * @param name
     * @return
     */
    public String getType(String name) {
        for (BaseCompiler child : getChildren()) {
            if (child instanceof DataType && ((DataType) child).getName().equals(name)) {
                return ((DataType) child).getType();
            }
        }
        return null;
    }


}
