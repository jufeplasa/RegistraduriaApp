import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Server {
    private static final String URL = "jdbc:postgresql://xhgrid2:5432/votaciones";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static Scanner scanner;
    private static Connection connection = null;
    private static PreparedStatement pstmt = null;
    private static ResultSet rs = null;
    public static void main(String[] args) {
        scanner=new Scanner(System.in);
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexion exitosa a la base de datos");
            // Aquí puedes añadir el código para interactuar con la base de datos
            menu();
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al conectar a la base de datos");
        }
        finally {
            // Cerrar ResultSet, Statement y Connection en el bloque finally
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (connection != null) connection.close();
                System.out.println("Conexión cerrada correctamente");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void menu(){
        int option;
        int exitOption=3;
        do {
            showMenu();
            option=scanner.nextInt();
            scanner.nextLine();
            if (option!=exitOption) {
                doOptions(option);
            }
        }while (option!=exitOption);  
    }



    public static void doOptions(int option){
        switch (option) {
            case 1:
                System.out.println("Implementacion de muchas consultas por un archivo");
                break;
            case 2:
                System.out.println("Ingresa un numero de cedula ");
                int documento=scanner.nextInt();
                findInfoById(documento);
                break;
            default :
                System.out.println("Opcion incorrecta");
            break;
        }
    }

    /*
     * Mostrar menu
     */
    public static void showMenu(){
        System.out.println("Selecciona una opcion");
        System.out.println("1: Ingrese la ruta de un archivo");
        System.out.println("2: Consulte su puesto de votacion con su cedula");
        System.out.println("3: Salir de la aplicacion");
    }



    public static void findInfoById(int documento) {
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
                System.out.println("Nombre: " + nombre + "\nMesa: " + mesa + "\nDirección: " + direccion + "\nMunicipio: " + municipio + "\nDepartamento: " + departamento);
            } else {
                System.out.println("No se encontraron resultados para el documento: " + documento);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error en consulta");
        }
    }



    /*
     * Metodo que calcula los factores
     */
    public static String findfactors(int n  ){
        String factores="[";
        for (long i = 2; i <= n; i++) {
            while (n % i == 0) {
                factores+=i+" ";
                n /= i;
            }
        }
        factores+="]";
        return factores.toString();
    }
}
