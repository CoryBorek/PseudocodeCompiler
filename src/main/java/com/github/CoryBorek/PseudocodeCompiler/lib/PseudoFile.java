package com.github.CoryBorek.PseudocodeCompiler.lib;

import com.github.CoryBorek.PseudocodeCompiler.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * General Pseudocode file
 */
public abstract class PseudoFile extends BaseCompiler {
    //Attributes
    private ArrayList<String> lines = new ArrayList<>();
    private ArrayList<String> indentations = new ArrayList<>();

    private File convertedFile;
    private String newName;

    private String fileType;

    /**
     * Constructor
     * @param file File to check
     * @param fileType Filetype
     */
    public PseudoFile(File file, String fileType) {
        convertedFile = file;
        newName = file.getName().replace(".pseudo", "");
        this.fileType = fileType;
    }

    /**
     * Runs a conversion
     */
    public void run() {
        //Message the user of the conversion
        System.out.println("Converting File: " + convertedFile.getName());


        try {
            //Grabs the lines from a file
            lines = Util.readLinesFromFile(convertedFile);
            for (int i = 0; i < lines.size(); i++) {

                //Replace tabs with spaces (to make it easier to read)
                lines.set(i, lines.get(i).replace("\t", "    ").replace("//", "§§"));

                //Store line and intent
                String line = lines.get(i);
                String indent = Util.getIndent(line);

                //Add indent to indentations
                indentations.add(indent);
                //Remove the indentations from the main line
                lines.set(i, line.replaceAll(Util.getIndent(line), ""));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Runs setup functino
        setup();
        //Compiles
        String out = compile();

        //Output compiled file
        System.out.println(out);
        try {

            //Save compiled document
            System.out.println("Saving Document.");
            FileOutputStream outputStream = new FileOutputStream(newName + fileType);
            outputStream.write(out.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        } catch (IOException e) {
            //Notify any errors
            System.err.println("Something wrong happened!");
            e.printStackTrace();
        }
    }

    /**
     * Returns any indentations
     * @return
     */
    public ArrayList<String> getIndentations() {
        return indentations;
    }

    /**
     * Returns the lines
     * @return
     */
    public ArrayList<String> getLines() {
        return lines;
    }

    /**
     * Returns the file name
     * @return
     */
    public String getFileName() {
        return newName;
    }

}
