package com.github.CoryBorek.PseudocodeCompiler.lib.items;

import com.github.CoryBorek.PseudocodeCompiler.Util;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseClass;
import com.github.CoryBorek.PseudocodeCompiler.lib.java.BaseFunction;

public class DataType extends SingleLineItem{

    //attributes
    private String name; // Name of the variable
    private String type; // Datatype of the variable
    private String value = null; //The value of the variable
    private String operation = ""; //What operation is being completed (if any)
    private boolean updating = false; //Is this assigning a new value?
    private boolean isConstant = false; //Is it a constant?
    private boolean isObject = false; // Is the variable an object?

    /**
     * Constructor
     * @param line Line of the DataType
     * @param startingNum Starting line index
     * @param file File of conversion
     * @param parent Parent object
     */
    public DataType(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(line, startingNum, file, parent);
    }

    /**
     * Sets up the conversion
     */
    @Override
    public void setup() {
        isObject = false;

        //Declaring a normal variable
        if (getLine().startsWith("CREATE ")) {
            //Initialization
            if (getLine().contains("=")) {
                String declaration = getLine().substring(0, getLine().indexOf("="));
                String instantiation = getLine().substring(getLine().indexOf("=") + 1);
                value = instantiation.trim();
                operation = "=";
                type = Util.findType(instantiation.replaceAll(" ", ""), getParent());
                name = declaration.replaceAll("CREATE | ", "");
            }
            //Object
            else if (getLine().contains(" AS ")) {
                String[] args = getLine().split("CREATE | AS | ");

                name = args[1];
                type = args[args.length-1];
                isObject = true;
            }
            //Initialized but not declared
            else {
                name = getLine().replaceAll("CREATE | ", "");
                type = "var";
            }
        }
        //Parameters for methods
        else if (getLine().startsWith("PARAM ")) {
            //Initialized and Declared
            if (getLine().contains("=")) {
                String declaration = getLine().substring(0, getLine().indexOf("="));
                String instantiation = getLine().substring(getLine().indexOf("=") + 1);
                value = instantiation.trim();
                operation = "=";
                type = Util.findType(instantiation.replaceAll(" ", ""), getParent());
                name = declaration.replaceAll("PARAM | ", "");
            }
            //Just declared (basically the only thing actually used)
            else {
                name = getLine().replaceAll("PARAM | ", "");
                type = "var";
            }
        }
        //Constants
        else if (getLine().startsWith("CONSTANT ")) {
            isConstant = true;
            //Initialized and declared
            if (getLine().contains("=")) {
                String declaration = getLine().substring(0, getLine().indexOf("="));
                String instantiation = getLine().substring(getLine().indexOf("=") + 1);
                value = instantiation.trim();
                operation = "=";
                type = Util.findType(instantiation.replaceAll(" ", ""), getParent());
                name = declaration.replaceAll("CONSTANT | ", "");
            }
            //Object
            else if (getLine().contains(" AS ")) {
                String[] args = getLine().split("CONSTANT |AS| ");

                name = args[0];
                type = args[1];
                isObject = true;
            }
            //Just initalized
            else {
                name = getLine().replaceAll("CONSTANT | ", "");
                type = "var";
            }
        }
        //Updating the object
        else {
            updating = true;
            //Add one or subtract one
            if ((getLine().contains("++") || getLine().contains("--")) && getLine().split("\\+\\+|--").length > 0) {
                //Splits by ++ or --
                String[] split = getLine().split("\\+\\+|--");
                //Gets the name of the object and sets a null value
                name = split[0].replaceAll("CREATE| ", "").trim();
                value = "";
                //Gets ++ or --
                operation = getLine().substring(getLine().indexOf(name) + name.length());
                //Gets the datatype from previous use cases
                if (getParent() instanceof BaseFunction) {
                    type = ((BaseFunction) getParent()).getType(name);
                }
                if (getParent() instanceof BaseClass) {
                    type = ((BaseClass) getParent()).getType(name);
                }

                //Sets the line
                setNewLine(name + operation + ";");
                return;
            }
            // Adding or subtracting
            else if ((getLine().contains("+=") || getLine().contains("-=") && getLine().split("\\+=|-=").length > 1)) {
                //Splits by the operation and sets the declaration and initialization
                String[] split = getLine().split("\\+=|-=");
                String declaration = split[0];
                String instantiation = split[1];
                //Sets the value
                value = instantiation.trim();

                //Grabs the data type
                type = Util.findType(instantiation.replaceAll(" ", ""), getParent());
                //Sets the name from the declaration
                name = declaration.replaceAll("CREATE | ", "");
                //Sets the operation
                operation = getLine().substring(getLine().indexOf(name) + name.length(),getLine().indexOf(value));
                //Updates the datatype from previous usecases
                if (getParent() instanceof BaseFunction) {
                    ((BaseFunction) getParent()).updateTypes(name, type);
                }
                if (getParent() instanceof BaseClass) {
                    ((BaseClass) getParent()).updateTypes(name, type);
                }
            }
            //Setting as a new value
            else if (getLine().contains("=") && getLine().split("=").length > 1) {
                //Splits by the = sign
                String[] split = getLine().split("=");
                String declaration = split[0];
                String instantiation = split[1];

                //Sets the operation
                operation = "=";
                //Objects
                if (instantiation.trim().startsWith("NEW")) {
                    value = instantiation.trim().replaceAll("NEW ", "new ");
                    type = instantiation.replaceAll("NEW | ", "").substring(0, instantiation.replaceAll("NEW | ", "").indexOf("(")).trim();
                    name = declaration.trim();
                }
                //Non-Objects
                else {
                        value = instantiation.trim();
                        type = Util.findType(instantiation.replaceAll(" ", ""), getParent());
                        name = declaration.trim();
                    }
                //Update previous use cases
                if (getParent() instanceof BaseFunction) {
                    ((BaseFunction) getParent()).updateTypes(name, type);
                }
                if (getParent() instanceof BaseClass) {
                    ((BaseClass) getParent()).updateTypes(name, type);
                }
            }
        }
        //Sets the line
        setNewLine();
    }

    /**
     * Set the dataType of an object
     * @param newType
     */
    public void setType(String newType) {
        type = newType;
        //Fix any changes
        if (getLine().contains("=")) {
            if (!(getLine().contains("CONSTANT") || getLine().contains("CREATE") || getLine().contains("PARAM"))) updating = true;
            String declaration = getLine().substring(0, getLine().indexOf("=")).replaceAll("CONSTANT|CREATE|PARAM| ", "");
            operation = "=";
            String instantiation = getLine().substring(getLine().indexOf("=") + 1);
            value = instantiation;
            name = declaration;
        }
        setNewLine();

    }

    /**
     * Updates the data type
     */
    public void updateType() {
        //If it's just a var (not normally allowed) try to fix the data type
        if (getType().equals("var") && getLine().contains("=")) {
            //Splits into declaration and instantiation
            String declaration = getLine().substring(0, getLine().indexOf("=")).replaceAll("CONSTANT|CREATE|PARAM| ", "");
            String instantiation = getLine().substring(getLine().indexOf("=") + 1);
            //Updates value, type, and name
            value = instantiation;
            type = Util.findType(instantiation.replaceAll(" ", ""), getParent());
            name = declaration;
            //Sets updating
            if (!(getLine().startsWith("CONSTANT") || getLine().startsWith("CREATE") || getLine().startsWith("PARAM"))) updating = true;
            //Update previous use cases
            if (getParent() instanceof BaseFunction) {
                ((BaseFunction) getParent()).updateTypes(name, type);
            }
            if (getParent() instanceof BaseClass) {
                ((BaseClass) getParent()).updateTypes(name, type);
            }
        }
        //Sets the line
        setNewLine();
    }

    /**
     * Sets the line
     */
    public void setNewLine() {
        //Output String
        String temp = "";
        //Adds constant value
        if (isConstant) temp += "final ";
        //Prints type
        if (!updating) temp += getType().trim() + " ";
        //Prints name
        temp += getName().trim();
        //Prints value and operation if used.
        if (value != null) temp += " " + operation.trim() + " " + value.trim();
        //The annoying semicolon
        temp += ";";
        //Sets the line
        super.setNewLine(temp);
    }

    /**
     * Returns the name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Returns if it is updating
     * @return
     */
    public boolean getUpdating() {
        return updating;
    }
}
