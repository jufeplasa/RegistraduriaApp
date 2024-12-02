import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Worker {

    private Demo.QueryServicePrx consult;
    private Demo.MasterPrx masterPrx;
    private com.zeroc.Ice.Communicator communicator;
    private List<String> proccessedDocuments;
    private ExecutorService executorService;  // El ThreadPoolExecutor

    public Worker(String[] args) {
        try {
            proccessedDocuments = new ArrayList<String>();
            communicator = com.zeroc.Ice.Util.initialize(args, "config.client");
            consult = Demo.QueryServicePrx.checkedCast(communicator.propertyToProxy("QueryService.Proxy"));
            masterPrx = Demo.MasterPrx.checkedCast(communicator.propertyToProxy("Master.Proxy"));
            if (consult == null) {
                throw new Error("Invalid proxy");
            }
            
            // Inicia el ThreadPoolExecutor con 10 hilos.
            executorService = Executors.newFixedThreadPool(10);
        } catch (com.zeroc.Ice.LocalException ex) {
            System.err.println("Error initializing Ice communicator or proxy: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.err.println("Unexpected error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void doTask(int documento) {
        // Ejecutar la consulta en paralelo usando el thread pool.
        executorService.submit(() -> {
            String result = consult.consult(documento);
            synchronized (proccessedDocuments) {
                proccessedDocuments.add(result);
            }
            System.out.println(result);
        });
    }

    public void sendTask() {
        String[] stringArray = proccessedDocuments.toArray(new String[0]);
        masterPrx.receiveTask(stringArray);
        System.out.println("Resultados enviados");
        proccessedDocuments.clear();
    }

    public static String findfactors(int n) {
        String factores = "[";
        for (long i = 2; i <= n; i++) {
            while (n % i == 0) {
                factores += i + " ";
                n /= i;
            }
        }
        factores += "]";
        return factores.toString();
    }

    public void disconnect() {
        try {
            // Esperar que todas las tareas terminen y luego cerrar el ThreadPool
            executorService.shutdown();
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            communicator.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
