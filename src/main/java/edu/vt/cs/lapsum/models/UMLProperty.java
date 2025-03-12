package edu.vt.cs.lapsum.models;
import java.util.List;
import java.util.Optional;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UMLProperty extends UMLItem{
    private String dataType;
    private String visibility;
    private boolean isStatic;
    private boolean isFinal;
    private List<String> annotations;
    private Optional<Integer> sourceLine = Optional.empty();

}