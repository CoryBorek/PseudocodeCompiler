package com.github.CoryBorek.PseudocodeCompiler;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main Class, runs program
 */
public class Main {

    /**
     * Main function
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        //Filetype to be outputted
        String type = args[0];
        //Runs for each file given
        for (int i = 1; i < args.length; i++) {


            //Compiles a file
            Path main = Paths.get("./").resolve(args[i]);
            CompileRunnable run = new CompileRunnable(type, main);
            Thread thread = new Thread(run);
            thread.start();


        }
    }
}


/**
 * Runs a compilation asynchronously
 */
class CompileRunnable implements Runnable {

    String type;
    Path path;

    public CompileRunnable(String type, Path path) {
        this.type = type;
        this.path = path;
    }

    @Override
    public void run() {

        //Compile the file.
        if (type.equalsIgnoreCase("java")) {
            JavaPseudoFile file = new JavaPseudoFile(path.toFile());
            file.run();
        }
    }
}