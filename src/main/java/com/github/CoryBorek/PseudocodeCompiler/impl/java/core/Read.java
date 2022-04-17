package com.github.CoryBorek.PseudocodeCompiler.impl.java.core;

import com.github.CoryBorek.PseudocodeCompiler.Util;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.SingleLineItem;

/**
 * Reads a variable from console
 */
public class Read extends SingleLineItem {

    //Attributes
    private String var;
    private String type;
    private String start;

    /**
     * Constructor
     * @param line Line of READ
     * @param startingNum Starting index
     * @param file File being converted
     * @param parent Parent Object
     */
    public Read(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(line, startingNum, file, parent);
    }


    /**
     * Sets up the function
     */
    @Override
    public void setup() {
        //Name of the variable
        var = getLine().replaceAll("READ ", "").trim();
        //Creates a Scanner
        start = "Scanner in" + var + " = new Scanner(System.in);\n";
        //Adds an import
        this.getFile().addImport("java.util.Scanner");
        //Sets the type of the variable
        type = Util.findType(var, getParent());
        //Sets the line
        setLine();
    }

    private void setLine() {
        switch (type) {
            //Integer
            case "int":
                setNewLine(start + getFile().getIndentations().get(getStartingNum()) + var + " = Integer.parseInt(in" + var + ".nextLine());");
                break;
            //Double
            case "double":
                setNewLine(start + getFile().getIndentations().get(getStartingNum()) + var + " = Double.parseDouble(in" + var + ".nextLine());");
                break;
            //Boolean
            case "boolean":
                setNewLine(start + getFile().getIndentations().get(getStartingNum()) + var + " = Boolean.parseBoolean(in" + var + ".nextLine());");
                break;
            //Anything else (most likely a string)
            default:
                setNewLine(start +getFile().getIndentations().get(getStartingNum()) + var + " = in" + var + ".nextLine();");
        }
    }

    /**
     * Gets the DataType
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Updates the DataType
     */
    public void updateType() {
        type = Util.findType(var, getParent());
        setLine();
    }
}
