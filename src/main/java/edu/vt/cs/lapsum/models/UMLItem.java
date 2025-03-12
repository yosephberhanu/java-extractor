package edu.vt.cs.lapsum.models;

import java.util.Optional;

import lombok.Data;

@Data
public abstract class UMLItem{
    private Long id;
    private Optional<String> domId = Optional.empty();
    private String name;
    private Optional<String> comments = Optional.empty();
    private Optional<String> displayName = Optional.empty();
    private Optional<String> summary = Optional.empty();
    private Optional<String> style = Optional.empty();
    private Optional<String> generatedContent = Optional.empty();
}
