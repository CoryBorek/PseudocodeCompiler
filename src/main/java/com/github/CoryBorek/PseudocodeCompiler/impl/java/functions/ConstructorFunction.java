package com.github.CoryBorek.PseudocodeCompiler.impl.java.functions;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.DataType;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Constructor
 */
public class ConstructorFunction extends BaseFunction {

    //Attributes
    private String name;
    private String returnType = "";
    private ArrayList<String> vars;

    /**
     * Constructor
     * @param lines Lines of function
     * @param startingNum Starting index
     * @param file File being converted
     * @param parent Parent Object
     */
    public ConstructorFunction(List<String> lines, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(lines, startingNum, file, parent);
    }

    /**
     * Gets the name of the Constructor (Class name)
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the variables
     * @return
     */
    public ArrayList<String> getVars() {
        return vars;
    }

    /**
     * Sets up the Constructor
     */
    @Override
    public void setup() {
        //Variables
        this.vars = new ArrayList<>();
        //Gets the declaration
        String declaration = getLines().get(0);
        //Gets the name
        this.name = declaration.substring(0,declaration.indexOf("(")).replaceAll("CONSTRUCTOR ", "").trim();
        //Finds parameters and sets them
        int hasParams = declaration.indexOf("parameters:");
        if (hasParams != -1) {
            //Gets all parameters in a single string
            String params = declaration.substring(hasParams + "parameters:".length(), declaration.lastIndexOf(")"));
            //Splits the parameters
            String[] args = params.split(",");
            //Loops for each parameter
            for (int i = 0; i < args.length; i++) {
                //Gets the argument
                String arg = args[i].trim();
                //Creates the parameter as a DataType
                DataType parameter = new DataType("PARAM " + arg, getStartingNum(), getFile(), this);
                //Stores it for later
                vars.add(parameter.getName());
                //Adds the parameter
                getChildren().add(parameter);
            }
        }
        //Runs the parent setup
        super.setup();
    }

    /**
     * Updates Variables
     */
    public void updateVariables() {
        for (BaseCompiler child : getChildren()) {
            if (child instanceof DataType) {
                ((DataType) child).updateType();
            }
        }
    }


    /**
     * Compiles the Constructor
     * @return
     */
    @Override
    public String compile() {
        //Starts the output
        String out = getFile().getIndentations().get(getStartingNum()) + "public ";
        out += name + "(";
        //Prints the parameters
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
        //Removes the parameters as children
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
        //Finishes the declaration
        out += ") {\n";

        //Compiles children
        for (BaseCompiler child : getChildren()) {
            out += child.compile();
        }
        //Ends the constructor
        out += getFile().getIndentations().get(getLines().size() - 1 + getStartingNum()) + "}\n";
        return out;
    }
}
