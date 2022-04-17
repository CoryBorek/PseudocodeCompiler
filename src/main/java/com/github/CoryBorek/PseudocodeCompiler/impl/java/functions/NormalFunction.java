package com.github.CoryBorek.PseudocodeCompiler.impl.java.functions;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.core.Return;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.DataType;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Normal Function
 */
public class NormalFunction extends BaseFunction {

    //Attributes
    private boolean isStatic = false;
    private String name;
    private String returnType = "void";
    private ArrayList<String> vars;

    /**
     * Constructor
     * @param lines Lines being converted
     * @param startingNum Starting index
     * @param file File being converted
     * @param parent Parent Object
     */
    public NormalFunction(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(lines, startingNum, file, parent);
    }

    /**
     * Gets the name of the function
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Makes the function static or not
     * @param isStatic If it's static
     */
    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    /**
     * Gets the static info
     * @return If it's static
     */
    public boolean getStatic() {
        return isStatic;
    }

    /**
     * Gets the variables
     * @return
     */
    public ArrayList<String> getVars() {
        return vars;
    }

    /**
     * Starts the function
     */
    @Override
    public void setup() {
        //Varaibles
        this.vars = new ArrayList<>();

        //Declaration of the function
        String declaration = getLines().get(0);

        //Name of the function
        this.name = declaration.substring(0,declaration.indexOf("(")).replaceAll("METHOD ", "").trim();

        //Parameters
        int hasParams = declaration.indexOf("parameters:");
        if (hasParams != -1) {
            //Stores parameters in an array
            String params = declaration.substring(hasParams + "parameters:".length(), declaration.lastIndexOf(")"));
            String[] args = params.split(",");
            for (int i = 0; i < args.length; i++) {
                String arg = args[i].trim();
                //Stores each paramter as a datatype
                DataType parameter = new DataType("PARAM " + arg, getStartingNum(), getFile(), this);
                vars.add(parameter.getName());
                getChildren().add(parameter);
            }
        }
        //Runs parent setup
        super.setup();

        //Sets up return info
        for (int i = 0; i < getLines().size(); i++) {
         if (getLines().get(i).startsWith("RETURN")) {
             Return item = new Return(getLines().get(i), getStartingNum() + i, getFile(), this);
             setReturnType(item.getType());
             getChildren().add(item);
         }
        }
    }

    /**
     * Updates variables
     */
    public void updateVariables() {
        for (BaseCompiler child : getChildren()) {
            if (child instanceof DataType) {
                ((DataType) child).updateType();
            }
        }
    }

    /**
     * Sets the return type
     * @param type
     */
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

    /**
     * Compiles the function
     * @return
     */
    @Override
    public String compile() {
        //Starts compilation
        String out = getFile().getIndentations().get(getStartingNum()) + "public ";

        //Static?
        if (isStatic) out += "static ";

        //Return type
        out += returnType + " " + name + "(";

        //Adds variables
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
        //Removes varaibles as children
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

        //Compiles children
        for (BaseCompiler child : getChildren()) {
            out += child.compile();
        }

        //Finishes compilation
        out += getFile().getIndentations().get(getLines().size() - 1 + getStartingNum()) + "}\n";
        return out;
    }
}
