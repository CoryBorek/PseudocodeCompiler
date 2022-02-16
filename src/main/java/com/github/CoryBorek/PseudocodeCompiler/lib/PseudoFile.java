package com.github.CoryBorek.PseudocodeCompiler.lib;

import com.github.CoryBorek.PseudocodeCompiler.Util;
import com.github.CoryBorek.PseudocodeCompiler.lib.BaseCompiler;
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

public abstract class PseudoFile extends BaseCompiler {
    private ArrayList<String> lines = new ArrayList<>();
    private ArrayList<String> indentations = new ArrayList<>();

    private File convertedFile;
    private String newName;

    private String fileType;
    public PseudoFile(File file, String fileType) {
        convertedFile = file;
        newName = file.getName().replace(".pseudo", "");
        this.fileType = fileType;
    }

    public void run() {
        System.out.println("Converting File: " + convertedFile.getName());

        try {
            lines = Util.readLinesFromFile(convertedFile);
            for (int i = 0; i < lines.size(); i++) {
                lines.set(i, lines.get(i).replace("\t", "    ").replace("//", "§§"));
                String line = lines.get(i);
                String indent = Util.getIndent(line);
                indentations.add(indent);
                lines.set(i, line.replaceAll(Util.getIndent(line), ""));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        setup();
        String out = compile();
        System.out.println(out);
        try {
            System.out.println("Saving Document.");
            FileOutputStream outputStream = new FileOutputStream(newName + fileType);
            outputStream.write(out.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        } catch (IOException e) {
            System.err.println("Something wrong happened!");
            e.printStackTrace();
        }
    }

    public ArrayList<String> getIndentations() {
        return indentations;
    }

    public ArrayList<String> getLines() {
        return lines;
    }

    public String getFileName() {
        return newName;
    }

}
