// import java.io.BufferedWriter;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;

import java.sql.SQLException;

public class Consulta {

    private Demo.QueryServicePrx consult;
    private com.zeroc.Ice.Communicator communicator;

    public Consulta(String[] args){
        try{
            communicator = com.zeroc.Ice.Util.initialize(args,"config.server");
            consult = Demo.QueryServicePrx.checkedCast(communicator.propertyToProxy("QueryService.Proxy"));
            if(consult == null)
            {
                throw new Error("Invalid proxy");
            }
        } catch (com.zeroc.Ice.LocalException ex) {
            System.err.println("Error initializing Ice communicator or proxy: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.err.println("Unexpected error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void findInfoById(int documento) {
        String result = consult.consult(documento);
        System.out.println(result);
    }

    public void disconect(){
            try {
                communicator.destroy(); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    // private   String URL = "jdbc:postgresql://xhgrid2:5432/votaciones";
    // private   String USER = "postgres";
    // private   String PASSWORD = "postgres";
    // private  Connection connection = null;
    // private  PreparedStatement pstmt = null;
    // private  ResultSet rs = null;


    // public Consulta(){
    //     try {
    //         connection = DriverManager.getConnection(URL, USER, PASSWORD);
    //     }
    //     catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    // }

    // public void disconect(){
    //     try {
    //         if (rs != null) rs.close();
    //         if (pstmt != null) pstmt.close();
    //         if (connection != null) connection.close();
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    // }

    // public void findInfoById(int documento) {
    // String query = "SELECT c.nombre, m.consecutive AS mesa, p.direccion, mu.nombre AS municipio, d.nombre AS departamento " +
    //             "FROM Ciudadano c " +
    //             "JOIN mesa_votacion m ON c.mesa_id = m.id " +
    //             "JOIN puesto_votacion p ON m.puesto_id = p.id " +
    //             "JOIN municipio mu ON p.municipio_id = mu.id " +
    //             "JOIN departamento d ON mu.departamento_id = d.id " +
    //             "WHERE c.documento = ?::text";
    // try {
    //     pstmt = connection.prepareStatement(query);
    //     pstmt.setInt(1, documento);
    //     rs = pstmt.executeQuery();
    //     if (rs.next()) {
    //         String nombre = rs.getString("nombre");
    //         int mesa = rs.getInt("mesa");
    //         String direccion = rs.getString("direccion");
    //         String municipio = rs.getString("municipio");
    //         String departamento = rs.getString("departamento");
    //         System.out.println("Nombre: " + nombre + "\nMesa: " + mesa + "\nDireccion: " + direccion + "\nMunicipio: " + municipio + "\nDepartamento: " + departamento);
    //     } else {
    //         System.out.println("No se encontraron resultados para el documento: " + documento);
    //     }
    // } catch (SQLException e) {
    //     e.printStackTrace();
    //     System.out.println("Error en consulta");
    // }
    // }

    // public void writeDocument(int limite) {
    //     String query = "SELECT documento FROM Ciudadano LIMIT ?;";
    //     BufferedWriter writer = null;
    //     try {
    //         pstmt = connection.prepareStatement(query);
    //         pstmt.setInt(1, limite);
    //         rs = pstmt.executeQuery();
    //         String nombreArchivo = limite + ".txt";
    //         writer = new BufferedWriter(new FileWriter(nombreArchivo));
            
    //         if (rs.next()) {
    //             do {
    //                 String documento = rs.getString("documento");
    //                 writer.write(documento);
    //                 writer.newLine(); // Nueva línea para cada resultado
    //             } while (rs.next());
    //             System.out.println("Resultados escritos en el archivo: " + nombreArchivo);
    //         } else {
    //             System.out.println("Sin resultados");
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         System.out.println("Error en consulta");
    //     }finally {
    //         // Cerrar recursos
    //         try {
    //             if (rs != null) rs.close();
    //             if (pstmt != null) pstmt.close();
    //             if (writer != null) writer.close();
    //         } catch (IOException | SQLException e) {
    //             e.printStackTrace();
    //         }
    //     }
    // }


}

