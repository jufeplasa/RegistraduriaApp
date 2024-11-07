import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Server {
    private static final String URL = "jdbc:postgresql://xhgrid2:5432/votaciones";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static Scanner scanner;
    public static void main(String[] args) {
        scanner=new Scanner(System.in);
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT * FROM departamento";
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexion exitosa a la base de datos");
            // Aquí puedes añadir el código para interactuar con la base de datos
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                // Procesa los resultados
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                System.out.println("ID: " + id + ", Nombre: " + nombre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al conectar a la base de datos");
        }
        finally {
            // Cerrar ResultSet, Statement y Connection en el bloque finally
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
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
            if (option==exitOption) {
                doOptions(option);
            }
        }while (option==exitOption);  
    }

    public static void doOptions(int option){
        switch (option) {
            case 1:
                System.out.println("Implementacion de muchas consultas por un archivo");
                break;
            case 2:
                System.out.println("Devolver el puesto de votacion por una cedula");
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
