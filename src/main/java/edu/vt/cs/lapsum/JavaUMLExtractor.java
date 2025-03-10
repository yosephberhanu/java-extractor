package edu.vt.cs.lapsum;
import edu.vt.cs.lapsum.parser.Parser;
import edu.vt.cs.lapsum.sink.SQLSink;

import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
public class JavaUMLExtractor {

    // For demonstration, main method:
    public static void main(String[] args) throws IOException, SQLException {
        if (args.length < 2) {
            System.err.println("Usage: ./gradlew run --args=\"<project-folder>  <sqlite-db-file>\"");
            System.exit(1);
        }
        String projectFolder = args[0];
        String sqliteDbFile = args[1];

        // 1) Connect to the SQLite DB (and create tables if needed).
        SQLSink sink = new SQLSink(sqliteDbFile);

        // 2) Recursively parse all .java files in projectFolder
        List<Path> allJavaFiles = findJavaFiles(Paths.get(projectFolder));

        Parser parser = new Parser(sink);
        
        for (Path javaFile : allJavaFiles) {
            try {
                parser.parseAndInsert(javaFile);
            } catch (Exception e) {
                System.err.println("Warning: Could not parse file: " + javaFile + " due to " + e);
            }
        }
        System.out.println("Extraction complete.");
    }

    // --------------------------------
    //  Find .java files recursively
    // --------------------------------
    private static List<Path> findJavaFiles(Path root) throws IOException {
        List<Path> result = new ArrayList<>();
        Files.walk(root)
             .filter(p -> p.toString().endsWith(".java"))
             .forEach(result::add);
        return result;
    }    
    
}