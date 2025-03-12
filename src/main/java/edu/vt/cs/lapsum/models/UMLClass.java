package edu.vt.cs.lapsum.models;

import lombok.*;
import java.util.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class UMLClass extends UMLItem {
    private Optional<String> packageName = Optional.empty();
    private boolean isAbstract;
    private boolean isInterface;
    private List<String> annotations = new ArrayList<>();
    private List<String> files = new ArrayList<>();

    private List<UMLProperty> properties = new ArrayList<>();
    private List<UMLMethod> methods = new ArrayList<>();
}