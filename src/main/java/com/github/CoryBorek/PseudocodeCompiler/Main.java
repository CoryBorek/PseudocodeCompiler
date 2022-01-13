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

        System.out.println("Welcome to the Pseudocode Compiler...");
        System.out.println("Created by Cory Borek");

        Path main = Paths.get("./").resolve(args[0]);

        ClassCompiler compiler = new ClassCompiler(main.toFile());
        compiler.run();

    }
}
