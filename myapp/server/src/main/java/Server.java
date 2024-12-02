
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import Demo.*;



public class Server {
    private static Consulta consulta;
    private static Scanner scanner;
    private static FilePrx file;
    private static com.zeroc.IceStorm.TopicManagerPrx manager;
    private static com.zeroc.Ice.ObjectPrx publisher;
    private static com.zeroc.IceStorm.TopicPrx topic;
    private static com.zeroc.Ice.Communicator communicator;


    public static void main(String[] args) {


        java.util.List<String> extraArgs = new java.util.ArrayList<String>();
        try
        {
            communicator = com.zeroc.Ice.Util.initialize(args, "config.pub", extraArgs);
            communicator.getProperties().setProperty("Ice.Default.Package", "com.zeroc.demos.IceStorm.clock");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> communicator.destroy()));

            run(communicator, extraArgs.toArray(new String[extraArgs.size()]));
            scanner=new Scanner(System.in);
            consulta = new Consulta(args);
            System.out.println("Conexion exitosa a la base de datos");
            menu();
            
        }
        finally {
            scanner.close(); 
            communicator.destroy(); 
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

    private static void run(com.zeroc.Ice.Communicator communicator, String[] args){
        String topicName = "files";
        manager = com.zeroc.IceStorm.TopicManagerPrx.checkedCast(
            communicator.propertyToProxy("TopicManager.Proxy"));
            topic = null;
            try
            {
                topic = manager.retrieve(topicName);
            }
            catch(com.zeroc.IceStorm.NoSuchTopic e)
            {
                try
                {
                    topic = manager.create(topicName);
                }
                catch(com.zeroc.IceStorm.TopicExists ex)
                {
                    System.err.println("temporary failure, try again.");
                }
            }
        publisher = topic.getPublisher();
        file = FilePrx.uncheckedCast(publisher);
    }

    public static void doOptions(int option){
        switch (option) {
            case 1:
                System.out.println("Implementacion de muchas consultas por un archivo");
                int text=scanner.nextInt();
                long startTime = System.currentTimeMillis();
                leerArchivo("C:\\Users\\jufep\\OneDrive\\Escritorio\\11vo semestre\\ingesoft4\\proyecto\\Documentos\\"+text+".txt");
                long endTime = System.currentTimeMillis();
                long elapsedTime = endTime - startTime;
                System.out.println("Tiempo de ejecución: " + elapsedTime + " ms");
                break;
            case 2:
                System.out.println("Ingresa un numero de documentos ");
                int documento=scanner.nextInt();
                consulta.findInfoById(documento);
                break;
            default :
                System.out.println("Opcion incorrecta");
            break;
        }
    }


    public static List<String> getSubs(){
        String command = "icestormadmin -e \"subscribers files\" --Ice.Config=config.admin";
        ArrayList<String> subscribers = new ArrayList<>();

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean isSubscriberSection = false;

            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("files:")) {
                    isSubscriberSection = true;
                } else if (isSubscriberSection && !line.trim().isEmpty()) {
                    subscribers.add(line.trim());
                }
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        // Mostrar los suscriptores y el tamaño del ArrayList
        System.out.println("Subscribers: " + subscribers);
        return subscribers;
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

    public static void leerArchivo(String ruta) {
        List<String> subscribers = getSubs(); 
        BufferedReader reader = null;
        try { 
            reader = new BufferedReader(new FileReader(ruta));
            String linea;
            long numeroDeLineas = Files.lines(Paths.get(ruta)).count();
            System.out.println("Numero total de lineas: " + numeroDeLineas);

            // Dividir las líneas entre los suscriptores
            int numSubscribers = subscribers.size();
            int linesPerSubscriber = (int) Math.ceil((double) numeroDeLineas / numSubscribers);

            int currentSubscriber = 0;
            int currentLine = 0;
            System.out.println(subscribers.get(currentSubscriber));
            file.Share(subscribers.get(currentSubscriber));
            while ((linea = reader.readLine()) != null) {
                System.out.println(linea);
                file.Share(linea);
                currentLine++;
                if (currentLine >= linesPerSubscriber) {
                    System.out.println(currentLine);
                    currentLine = 0;
                    currentSubscriber++;
                    if (currentSubscriber >= numSubscribers) {
                        currentSubscriber = 0;
                    }
                    System.out.println(subscribers.get(currentSubscriber));
                    file.Share(subscribers.get(currentSubscriber));
                }
            }
            System.out.println("END_OF_DOCUMENTS");
            file.Share("END_OF_DOCUMENTS");
        } catch (IOException e) {
            // Manejo de errores
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }finally {
            // Cerrar el archivo después de usarlo
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                System.out.println("Error al cerrar el archivo: " + e.getMessage());
            }
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
    /*
    * Metodo alternativo que funciona para ejecutar la búsqueda desde el mismo nodo de procesamiento.
    */
    public static void leerArchivo2(String ruta) {
        BufferedReader reader = null;
        try { 
            reader = new BufferedReader(new FileReader(ruta));
            String linea;
            while ((linea = reader.readLine()) != null) {
                int documento = Integer.parseInt(linea);
                consulta.findInfoById(documento);
            }
        } catch (IOException e) {
            // Manejo de errores
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                System.out.println("Error al cerrar el archivo: " + e.getMessage());
            }
        }
    }

}