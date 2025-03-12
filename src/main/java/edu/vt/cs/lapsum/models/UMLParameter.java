package edu.vt.cs.lapsum.models;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UMLParameter extends UMLItem{
    private String dataType;
    private List<String> annotations;

}