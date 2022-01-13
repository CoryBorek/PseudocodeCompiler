package com.github.CoryBorek.PseudocodeCompiler;

import com.github.CoryBorek.PseudocodeCompiler.impl.ClassCompiler;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main class, runs program
 */
public class Main {

    /**
     * This function runs the entire program at root.
     * @param args program arguments from command line.
     */
    public static void main(String[] args) {
        //Welcome user to the program
        System.out.println("Welcome to the Pseudocode Compiler...");
        System.out.println("Created by Cory Borek");

        //Get path of file from command line.
        Path main = Paths.get("./").resolve(args[0]);

        //Compile the file.
        ClassCompiler compiler = new ClassCompiler(main.toFile());
        compiler.run();

    }
}
