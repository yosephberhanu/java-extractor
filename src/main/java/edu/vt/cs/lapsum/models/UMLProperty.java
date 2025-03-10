package edu.vt.cs.lapsum.models;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UMLProperty {
    private String name;
    private String dataType;
    private String visibility;
    private boolean isStatic;
    private boolean isFinal;
    private int lineNumber; // optional

    public UMLProperty(String name, String dataType, String visibility, 
                       boolean isStatic, boolean isFinal, int lineNumber) {
        this.name = name;
        this.dataType = dataType;
        this.visibility = visibility;
        this.isStatic = isStatic;
        this.isFinal = isFinal;
        this.lineNumber = lineNumber;
    }
}