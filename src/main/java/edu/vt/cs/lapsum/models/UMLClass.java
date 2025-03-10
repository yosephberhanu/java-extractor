package edu.vt.cs.lapsum.models;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class UMLClass {
    private String name;
    private String packageName;
    private boolean isAbstract;
    private boolean isInterface;
    private List<String> annotations = new ArrayList<>();
    private List<String> files = new ArrayList<>();

    private List<UMLProperty> properties = new ArrayList<>();
    private List<UMLMethod> methods = new ArrayList<>();

    public UMLClass(String name, String packageName, boolean isAbstract, boolean isInterface) {
        this.name = name;
        this.packageName = packageName;
        this.isAbstract = isAbstract;
        this.isInterface = isInterface;
    }
}