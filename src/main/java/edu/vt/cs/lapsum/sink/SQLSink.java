package edu.vt.cs.lapsum.sink;

import java.sql.*;
import edu.vt.cs.lapsum.models.*;
import com.google.gson.Gson;

public class SQLSink implements Sink{
    private Connection conn;
    private Gson gson;
    public SQLSink(String sqliteDbFile) throws SQLException {
        this.gson = new Gson();
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + sqliteDbFile);
        this.createTables(conn);
    }

    private void createTables(Connection conn){
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS uml_class (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                       "name TEXT, " +
                       "package_name TEXT, " +
                       "is_abstract BOOLEAN, " +
                       "is_interface BOOLEAN, " +
                       "annotations TEXT, " +
                       "files TEXT, " +
                       "dom_id TEXT, " +
                       "display_name TEXT, " +
                       "summary TEXT, " +
                       "comments TEXT)" );

            st.execute("CREATE TABLE IF NOT EXISTS uml_property (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                       "class_id INTEGER, " +
                       "name TEXT, " +
                       "data_type TEXT, " +
                       "visibility TEXT, " +
                       "is_static BOOLEAN, " +
                       "is_final BOOLEAN, " +
                       "source_line INTEGER, " +
                       "dom_id TEXT, " +
                       "annotations TEXT, " +
                       "comments TEXT, " +
                       "summary TEXT, " +
                       "FOREIGN KEY(class_id) REFERENCES uml_class(id))");
                       
            st.execute("CREATE TABLE IF NOT EXISTS uml_method (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                       "class_id INTEGER, " +
                       "name TEXT, " +
                       "dom_id TEXT, " +
                       "return_type TEXT, " +
                       "visibility TEXT, " +
                       "is_static BOOLEAN, " +
                       "is_abstract BOOLEAN, " +
                       "starting_line INTEGER, " +
                       "ending_line INTEGER, " +
                       "source TEXT, " +
                       "annotations TEXT, " +
                       "display_name TEXT, " +
                       "comments TEXT, " +
                       "summary TEXT, " +
                       "FOREIGN KEY(class_id) REFERENCES uml_class(id))");

            st.execute("CREATE TABLE IF NOT EXISTS uml_parameter (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                       "method_id INTEGER, " +
                       "name TEXT, " +
                       "dom_id TEXT, " +
                       "data_type TEXT, " +
                       "display_name TEXT, " +
                       "annotations TEXT, " +
                       "comments TEXT, " +
                       "summary TEXT, " +
                       "FOREIGN KEY(method_id) REFERENCES uml_method(id))");
        }catch(SQLException e){}
    }

    public long insertUMLClass(UMLClass umlClass) {
        String sql = "INSERT INTO uml_class (name, package_name, is_abstract, is_interface, annotations, files, dom_id, display_name, summary, comments) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, umlClass.getName());
            ps.setString(2, umlClass.getPackageName().orElse(""));
            ps.setBoolean(3, umlClass.isAbstract());
            ps.setBoolean(4, umlClass.isInterface());
            ps.setString(5, this.gson.toJson(umlClass.getAnnotations()));
            ps.setString(6, this.gson.toJson(umlClass.getFiles()));
            ps.setString(7, umlClass.getDomId().orElse(""));
            ps.setString(8, umlClass.getDisplayName().orElse(""));
            ps.setString(9, umlClass.getSummary().orElse(""));
            ps.setString(10, umlClass.getComments().orElse(""));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }catch(SQLException e){}
        return -1;
    }

    public void insertUMLProperty(long classId, UMLProperty prop){
        String sql = "INSERT INTO uml_property (class_id, name, data_type, visibility, is_static, is_final, source_line, dom_id, annotations, comments, summary) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, classId);
            ps.setString(2, prop.getName());
            ps.setString(3, prop.getDataType());
            ps.setString(4, prop.getVisibility());
            ps.setBoolean(5, prop.isStatic());
            ps.setBoolean(6, prop.isFinal());
            ps.setInt(7, prop.getSourceLine().orElse(-1));
            ps.setString(8, prop.getDomId().orElse(""));
            ps.setString(9, this.gson.toJson(prop.getAnnotations()));
            ps.setString(10, prop.getComments().orElse(""));
            ps.setString(11, prop.getSummary().orElse(""));

            ps.executeUpdate();
        }catch(SQLException e){}
    }

    public long insertUMLMethod(long classId, UMLMethod method){
        
        String sql = "INSERT INTO uml_method (class_id, name, return_type, visibility, is_static, is_abstract, starting_line, ending_line, source, dom_id, annotations, display_name, comments, summary) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, classId);
            ps.setString(2, method.getName());
            ps.setString(3, method.getReturnType());
            ps.setString(4, method.getVisibility());
            ps.setBoolean(5, method.isStatic());
            ps.setBoolean(6, method.isAbstract());
            ps.setInt(7, method.getStartingLine().orElse(-1));
            ps.setInt(8, method.getEndingLine().orElse(-1));
            ps.setString(9, method.getSource().orElse(""));
            ps.setString(10, method.getDomId().orElse(""));
            ps.setString(11, this.gson.toJson(method.getAnnotations()));
            ps.setString(12, method.getDisplayName().orElse(""));
            ps.setString(13, method.getComments().orElse(""));
            ps.setString(14, method.getSummary().orElse(""));

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }catch(SQLException e){}
        return -1;
    }

    public void insertUMLParameter(long methodId, UMLParameter param){
        String sql = "INSERT INTO uml_parameter (method_id, name, dom_id, data_type, display_name, annotations, comments, summary) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, methodId);
            ps.setString(2, param.getName());
            ps.setString(3, param.getDomId().orElse(""));
            ps.setString(4, param.getDataType());
            ps.setString(5, param.getDisplayName().orElse(""));
            ps.setString(6, this.gson.toJson(param.getAnnotations()));
            ps.setString(7, param.getComments().orElse(""));
            ps.setString(8, param.getSummary().orElse(""));

            ps.executeUpdate();
        }catch(SQLException e){}
    }
}
