package edu.vt.cs.lapsum.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import edu.vt.cs.lapsum.models.*;
import edu.vt.cs.lapsum.sink.Sink;

public class Parser {
    private JavaParser java_parser;
    private Sink sink;
    public Parser(Sink sink){
        this.sink = sink;
        ParserConfiguration config = new ParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        this.java_parser = new JavaParser(config);
        
    }
    // --------------------------------
    //  Parse with JavaParser & insert
    // --------------------------------
    public void parseAndInsert(Path javaFile) throws IOException, SQLException {
        // Parse the file
        ParseResult<CompilationUnit> parseResult = java_parser.parse(javaFile);

        if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
            CompilationUnit cu = parseResult.getResult().get();

            // Get the package name (if any)
            String packageName = cu.getPackageDeclaration()
                                   .map(pkg -> pkg.getName().asString())
                                   .orElse(null);

            // For each top-level type (class, interface, enum, record, etc.)
            cu.getTypes().forEach(type -> {
                if (type instanceof ClassOrInterfaceDeclaration) {
                    ClassOrInterfaceDeclaration decl = (ClassOrInterfaceDeclaration) type;
                    UMLClass umlClass = extractUMLClass(decl, packageName);

                    // Insert the UMLClass & get the DB id
                    long classId = sink.insertUMLClass(umlClass);

                    // Insert the fields
                    for (UMLProperty prop : umlClass.getProperties()) {
                        sink.insertUMLProperty(classId, prop);
                    }

                    // Insert the methods
                    for (UMLMethod method : umlClass.getMethods()) {
                        long methodId = sink.insertUMLMethod(classId, method);
                        for (UMLParameter param : method.getParameters()) {
                            sink.insertUMLParameter(methodId, param);
                        }
                    }
                }
                // handle Enums, Records, etc. if needed
            });
        } else {
            System.out.println("Parse failed for: " + javaFile);
        }
    }

    // --------------------------------
    //  Extract UML Data from a class
    // --------------------------------
    public UMLClass extractUMLClass(ClassOrInterfaceDeclaration decl, String packageName) {
        boolean isAbstract = decl.isAbstract();
        boolean isInterface = decl.isInterface();
        UMLClass umlClass = new UMLClass(decl.getNameAsString(), packageName, isAbstract, isInterface);

        // Annotations
        decl.getAnnotations().forEach(a -> umlClass.getAnnotations().add(a.getNameAsString()));

        // Fields -> UMLProperties
        decl.getFields().forEach(field -> {
            // Each FieldDeclaration can have multiple variables (ex: int x, y;)
            field.getVariables().forEach(var -> {
                String name = var.getNameAsString();
                String dataType = "Unknown";
                try {
                    // Try to resolve type (requires JavaParser symbol solver for advanced usage).
                    // For a simpler approach, we can just do:
                    dataType = field.getElementType().asString();
                } catch (Exception ignored) {}

                String visibility = field.isPublic() ? "public"
                                  : field.isPrivate() ? "private"
                                  : field.isProtected() ? "protected"
                                  : "default";
                
                boolean isStatic = field.isStatic();
                boolean isFinal = field.isFinal();

                // line number
                int lineNum = field.getBegin().map(pos -> pos.line).orElse(-1);

                UMLProperty prop = new UMLProperty(name, dataType, visibility, isStatic, isFinal, lineNum);
                umlClass.getProperties().add(prop);
            });
        });

        // Methods
        decl.getMethods().forEach(m -> {
            UMLMethod method = new UMLMethod();
            method.setName(m.getNameAsString());
            method.setVisibility(m.isPublic() ? "public"
                                : m.isPrivate() ? "private"
                                : m.isProtected() ? "protected"
                                : "default");
            method.setStatic(m.isStatic());
            method.setAbstract(m.isAbstract());

            // Return type
            try {
                // simpler approach:
                method.setReturnType(m.getType().asString());
            } catch (Exception ignored) {
                method.setReturnType("Unknown");
            }

            // Start/end line
            int startLine = m.getBegin().map(pos -> pos.line).orElse(-1);
            int endLine = m.getEnd().map(pos -> pos.line).orElse(-1);
            method.setStartLine(startLine);
            method.setEndLine(endLine);

            // Annotations
            m.getAnnotations().forEach(a -> method.getAnnotations().add(a.getNameAsString()));

            // Parameters
            m.getParameters().forEach(p -> {
                UMLParameter param = new UMLParameter();
                param.setName(p.getNameAsString());
                try {
                    param.setDataType(p.getType().asString());
                } catch (Exception ignored) {
                    param.setDataType("Unknown");
                }
                method.getParameters().add(param);
            });

            umlClass.getMethods().add(method);
        });
        return umlClass;
    }
}
