import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zeroc.Ice.Current;

public class QueryServiceI implements Demo.QueryService{

    private   String URL = "jdbc:postgresql://xhgrid2:5432/votaciones";
    private   String USER = "postgres";
    private   String PASSWORD = "postgres";
    private  Connection connection = null;
    private  PreparedStatement pstmt = null;
    private  ResultSet rs = null;

    public QueryServiceI(){
        try {
            System.out.println("Conectando a la Base de datos ");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexion creada");
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
            System.out.println("Conexion CERRADA");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String consult(int document, Current current) {
        System.out.println("Inicio de query");
        String message = "";
        String query = "SELECT c.nombre, m.consecutive AS mesa, p.direccion, mu.nombre AS municipio, d.nombre AS departamento " +
        "FROM Ciudadano c " +
        "JOIN mesa_votacion m ON c.mesa_id = m.id " +
        "JOIN puesto_votacion p ON m.puesto_id = p.id " +
        "JOIN municipio mu ON p.municipio_id = mu.id " +
        "JOIN departamento d ON mu.departamento_id = d.id " +
        "WHERE c.documento = ?::text";
        try {
            pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, document);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String nombre = rs.getString("nombre");
                int mesa = rs.getInt("mesa");
                String direccion = rs.getString("direccion");
                String municipio = rs.getString("municipio");
                String departamento = rs.getString("departamento");
                message="Nombre: " + nombre + "\nMesa: " + mesa + "\nDireccion: " + direccion + "\nMunicipio: " + municipio + "\nDepartamento: " + departamento;
            } else {
                System.out.println("No se encontraron resultados para el documento: " + document);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error en consulta");
        }
        System.out.println("Retorno de query");
        return message;  
    }
    
}
