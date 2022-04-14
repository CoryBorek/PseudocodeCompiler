package com.github.CoryBorek.PseudocodeCompiler;

import com.github.CoryBorek.PseudocodeCompiler.impl.java.JavaPseudoFile;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        String type = args[0];
        for (int i = 1; i < args.length; i++) {
            Path main = Paths.get("./").resolve(args[i]);

            //Compile the file.
            if (type.equalsIgnoreCase("java")) {
                JavaPseudoFile file = new JavaPseudoFile(main.toFile());
                file.run();
            }


        }
    }
}
