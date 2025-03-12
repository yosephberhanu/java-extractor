package edu.vt.cs.lapsum.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
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

    public void parseAndInsert(Path javaFile, Path projectFolder) throws IOException{
        java_parser.parse(javaFile).getResult().ifPresent(cu -> {
            String packageName = cu.getPackageDeclaration()
                    .map(pkg -> pkg.getName().asString())
                    .orElse(null);
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cls -> {
                UMLClass umlClass = extractUMLClass(cls);
                if(packageName!=null){
                    umlClass.setPackageName(Optional.of(packageName));
                }
                umlClass.setFiles(List.of(projectFolder.relativize(javaFile).toString()));
                long classId = sink.insertUMLClass(umlClass);
                
                umlClass.getProperties().forEach(prop -> sink.insertUMLProperty(classId, prop));
                umlClass.getMethods().forEach(method -> sink.insertUMLMethod(classId, method));
            });
        });
    }

    public UMLClass extractUMLClass(ClassOrInterfaceDeclaration decl) {
        UMLClass umlClass = new UMLClass();
        umlClass.setName(decl.getNameAsString());
        umlClass.setAbstract(decl.isAbstract());
        umlClass.setInterface(decl.isInterface());
        umlClass.setComments(decl.getComment().map(c->c.asString()));
        decl.getAnnotations().stream()
                .map(AnnotationExpr::getNameAsString)
                .forEach(umlClass.getAnnotations()::add);
        decl.getFields().forEach(field -> {
            for (VariableDeclarator var : field.getVariables()) {
                UMLProperty prop = new UMLProperty();
                prop.setName(var.getNameAsString());
                prop.setDataType(var.getType().asString());
                prop.setVisibility(field.isPublic() ? "public" :
                        field.isProtected() ? "protected" :
                                field.isPrivate() ? "private" : "default");
                prop.setAnnotations(field.getAnnotations().stream()
                        .map(AnnotationExpr::getNameAsString).toList());
                prop.setStatic(field.isStatic());
                prop.setFinal(field.isFinal());
                prop.setComments(field.getComment().map(c->c.asString()));
                umlClass.getProperties().add(prop);
            }
        });

        decl.getMethods().forEach(m -> {
            UMLMethod method = new UMLMethod();
            method.setName(m.getNameAsString());
            method.setVisibility(m.isPublic() ? "public" :
                    m.isProtected() ? "protected" :
                            m.isPrivate() ? "private" : "default");
            method.setStatic(m.isStatic());
            method.setAnnotations(m.getAnnotations().stream()
                    .map(AnnotationExpr::getNameAsString).toList());
            method.setReturnType(m.getType().asString());

            method.setStartingLine(m.getBegin().map(pos -> pos.line));
            method.setEndingLine(m.getEnd().map(pos -> pos.line));
            method.setComments(m.getComment().map(c->c.asString()));
            method.setSource(m.getBody().map(b->b.toString()));
            
            m.getParameters().forEach(p -> {
                UMLParameter param = new UMLParameter();
                param.setName(p.getNameAsString());
                param.setDataType(p.getType().asString());
                param.setAnnotations(p.getAnnotations().stream()
                .map(AnnotationExpr::getNameAsString).toList());
                param.setComments(m.getComment().map(c->c.asString()));
                method.getParameters().add(param);
            });
            umlClass.getMethods().add(method);
        });

        return umlClass;
    }
}
