package com.github.CoryBorek.PseudocodeCompiler.impl.java.core;

import com.github.CoryBorek.PseudocodeCompiler.Util;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.SingleLineItem;

/**
 * Returns a value from a function
 */
public class Return extends SingleLineItem {

    //Attributes
    private String type;
    private String value;

    /**
     * Constructor
     * @param line Line of Return
     * @param startingNum Starting index
     * @param file File being converted
     * @param parent Parent Object
     */
    public Return(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(line, startingNum, file, parent);
    }

    /**
     * Sets the value being returned
     * @param value
     */
    private void setValue(String value) {
        this.value = value;
    }


    /**
     * Gets the value being returned
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the return type
     * @param type
     */
    private void setType(String type) {
        this.type = Util.findType(type, this.getParent());
    }

    /**
     * Gets the return type
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Sets up conversion
     */
    @Override
    public void setup() {
        String item = getLines().get(0).replace("RETURN", "").trim();
        setType(item);
        setValue(item);
        setNewLine("return " + item + ";");
    }
}
