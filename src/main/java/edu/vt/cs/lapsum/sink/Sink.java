package edu.vt.cs.lapsum.sink;

import edu.vt.cs.lapsum.models.*;

public interface Sink {
    public long insertUMLClass(UMLClass umlClass);

    public void insertUMLProperty(long classId, UMLProperty prop);

    public long insertUMLMethod(long classId, UMLMethod method);

    public void insertUMLParameter(long methodId, UMLParameter param);    
}
