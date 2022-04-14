package com.github.CoryBorek.PseudocodeCompiler;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        String type = args[0];
        for (int i = 1; i < args.length; i++) {


            Path main = Paths.get("./").resolve(args[i]);
            CompileRunnable run = new CompileRunnable(type, main);
            Thread thread = new Thread(run);
            thread.start();


        }
    }
}


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