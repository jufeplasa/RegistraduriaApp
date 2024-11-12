
import java.util.Scanner;

public class Server {
    private static Consulta consulta;
    private static Scanner scanner;
    public static void main(String[] args) {
        scanner=new Scanner(System.in);
        try {
            consulta = new Consulta();
            System.out.println("Conexion exitosa a la base de datos");
            menu();
        }
        finally {
            consulta.disconect();
            System.out.println("Desconexion base de datos");
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
                consulta.findInfoById(documento);
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
