package edu.vt.cs.lapsum.sink;

import java.sql.*;
import edu.vt.cs.lapsum.models.*;

public class SQLSink implements Sink{
    private Connection conn;
    public SQLSink(String sqliteDbFile) throws SQLException {
        super();
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + sqliteDbFile);
        this.createTables(conn);
    }
    // --------------------------------
    //  SQLite Setup & Insertion
    // --------------------------------
    private void createTables(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS uml_class (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                       "name TEXT, " +
                       "package_name TEXT, " +
                       "is_abstract BOOLEAN, " +
                       "is_interface BOOLEAN)");

            st.execute("CREATE TABLE IF NOT EXISTS uml_property (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                       "class_id INTEGER, " +
                       "name TEXT, " +
                       "data_type TEXT, " +
                       "visibility TEXT, " +
                       "is_static BOOLEAN, " +
                       "is_final BOOLEAN, " +
                       "line_number INTEGER, " +
                       "FOREIGN KEY(class_id) REFERENCES uml_class(id))");

            st.execute("CREATE TABLE IF NOT EXISTS uml_method (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                       "class_id INTEGER, " +
                       "name TEXT, " +
                       "return_type TEXT, " +
                       "visibility TEXT, " +
                       "is_static BOOLEAN, " +
                       "is_abstract BOOLEAN, " +
                       "start_line INTEGER, " +
                       "end_line INTEGER, " +
                       "FOREIGN KEY(class_id) REFERENCES uml_class(id))");

            st.execute("CREATE TABLE IF NOT EXISTS uml_parameter (" +
                       "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                       "method_id INTEGER, " +
                       "name TEXT, " +
                       "data_type TEXT, " +
                       "FOREIGN KEY(method_id) REFERENCES uml_method(id))");
        }
    }

    public long insertUMLClass(UMLClass umlClass) {
        String sql = "INSERT INTO uml_class (name, package_name, is_abstract, is_interface) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, umlClass.getName());
            ps.setString(2, umlClass.getPackageName());
            ps.setBoolean(3, umlClass.isAbstract());
            ps.setBoolean(4, umlClass.isInterface());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void insertUMLProperty(long classId, UMLProperty prop) {
        String sql = "INSERT INTO uml_property (class_id, name, data_type, visibility, is_static, is_final, line_number) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, classId);
            ps.setString(2, prop.getName());
            ps.setString(3, prop.getDataType());
            ps.setString(4, prop.getVisibility());
            ps.setBoolean(5, prop.isStatic());
            ps.setBoolean(6, prop.isFinal());
            ps.setInt(7, prop.getLineNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long insertUMLMethod(long classId, UMLMethod method) {
        String sql = "INSERT INTO uml_method (class_id, name, return_type, visibility, is_static, is_abstract, start_line, end_line) " +
                     "VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, classId);
            ps.setString(2, method.getName());
            ps.setString(3, method.getReturnType());
            ps.setString(4, method.getVisibility());
            ps.setBoolean(5, method.isStatic());
            ps.setBoolean(6, method.isAbstract());
            ps.setInt(7, method.getStartLine());
            ps.setInt(8, method.getEndLine());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void insertUMLParameter(long methodId, UMLParameter param) {
        String sql = "INSERT INTO uml_parameter (method_id, name, data_type) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, methodId);
            ps.setString(2, param.getName());
            ps.setString(3, param.getDataType());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
