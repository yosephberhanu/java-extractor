package edu.vt.cs.lapsum.models;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class UMLMethod {
    private String name;
    private String returnType;
    private String visibility;
    private boolean isStatic;
    private boolean isAbstract;
    private int startLine;
    private int endLine;

    private List<UMLParameter> parameters = new ArrayList<>();
    private List<String> annotations = new ArrayList<>();

}