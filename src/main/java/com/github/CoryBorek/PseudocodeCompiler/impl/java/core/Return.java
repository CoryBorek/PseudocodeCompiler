package com.github.CoryBorek.PseudocodeCompiler.impl.java.core;

import com.github.CoryBorek.PseudocodeCompiler.Util;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;
import com.github.CoryBorek.PseudocodeCompiler.lib.items.SingleLineItem;

public class Return extends SingleLineItem {
    public Return(String line, int startingNum, JavaPseudoFile file, BaseCompiler parent) {
        super(line, startingNum, file, parent);
    }

    private String type;
    private String value;


    private void setValue(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }
    private void setType(String type) {
        this.type = Util.findType(type, this.getParent());
    }

    public String getType() {
        return type;
    }
    @Override
    public void setup() {
        String item = getLines().get(0).replace("RETURN", "").trim();
        setType(item);
        setValue(item);
        setNewLine("return " + item + ";");
    }
}
