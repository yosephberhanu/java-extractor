package edu.vt.cs.lapsum.models;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class UMLMethod extends UMLItem{
    private String returnType;
    private String visibility;
    private boolean isStatic;
    private boolean isAbstract;
    private Optional<Integer> startingLine = Optional.empty();
    private Optional<Integer> endingLine = Optional.empty();

    private List<UMLParameter> parameters = new ArrayList<>();
    private List<String> annotations = new ArrayList<>();
    private Optional<String> source;
}