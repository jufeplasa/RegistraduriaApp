import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Consulta {
    private   String URL = "jdbc:postgresql://xhgrid2:5432/votaciones";
    private   String USER = "postgres";
    private   String PASSWORD = "postgres";
    private  Connection connection = null;
    private  PreparedStatement pstmt = null;
    private  ResultSet rs = null;


    public Consulta(){
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconect(){
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void findInfoById(int documento) {
    String query = "SELECT c.nombre, m.consecutive AS mesa, p.direccion, mu.nombre AS municipio, d.nombre AS departamento " +
                "FROM Ciudadano c " +
                "JOIN mesa_votacion m ON c.mesa_id = m.id " +
                "JOIN puesto_votacion p ON m.puesto_id = p.id " +
                "JOIN municipio mu ON p.municipio_id = mu.id " +
                "JOIN departamento d ON mu.departamento_id = d.id " +
                "WHERE c.documento = ?::text";
    try {
        pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, documento);
        rs = pstmt.executeQuery();
        if (rs.next()) {
            String nombre = rs.getString("nombre");
            int mesa = rs.getInt("mesa");
            String direccion = rs.getString("direccion");
            String municipio = rs.getString("municipio");
            String departamento = rs.getString("departamento");
            System.out.println("Nombre: " + nombre + "\nMesa: " + mesa + "\nDireccion: " + direccion + "\nMunicipio: " + municipio + "\nDepartamento: " + departamento);
        } else {
            System.out.println("No se encontraron resultados para el documento: " + documento);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error en consulta");
    }
    }


}

