import com.zeroc.Ice.Current;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Demo.File;

public class Client {
    private static String subscriberID;
    private static Worker worker;
    private static final String MESSAGE_LIMIT = "END_OF_DOCUMENTS";
    private static final int THREAD_POOL_SIZE = 5; // Definir el tamaño del thread pool
    private static ExecutorService executorService; // ExecutorService para manejar los threads

    /*
     * START STATIC CLASS FileI
     */
    public static class FileI implements File {
        private ArrayList<String> avaibleDocs = new ArrayList<String>();
        private boolean avaibleId;

        /*
         * Metodo de suscripcion al publicador
         */
        @Override
        public void Share(String document, Current current) {
            if (document.length() == 36) {
                avaibleId = document.equals(subscriberID);
            } else {
                if (document.equals(MESSAGE_LIMIT)) {
                    processBatch();
                } else if (avaibleId) {
                    avaibleDocs.add(document);
                    System.out.println("Documento recibido: " + document);
                }
            }
        }

        /*
         * Llama a la clase consulta para realizar las consultas de los documentos disponibles.
         */
        private void processBatch() {
            if (!avaibleDocs.isEmpty()) {
                System.out.println("Procesando lote de documentos...");
                CountDownLatch latch = new CountDownLatch(avaibleDocs.size());
                for (String doc : avaibleDocs) {
                    try {
                        final int document = Integer.parseInt(doc);
                        executorService.submit(() -> {
                            try {
                                worker.doTask(document);  // Ejecutar la tarea en el worker
                            } finally {
                                latch.countDown();  // Reducir el conteo cuando termine la tarea
                                worker.updateConsult();
                            }
                        });
                    } catch (NumberFormatException e) {
                        System.err.println("Documento inválido: " + doc);
                    }
                }
                try {
                    latch.await();  // Esperar a que todas las tareas finalicen
                    worker.sendTask();  // Enviar el resultado después de que todas las tareas terminen
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("El proceso fue interrumpido.");
                }
                avaibleDocs.clear();
            }
        }

    }
    /*
     * END STATIC CLASS FileI
     */

    public static void main(String[] args) {
        // Inicializar el ThreadPool
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        worker = new Worker(args);
        java.util.List<String> extraArgs = new java.util.ArrayList<String>();
        com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "config.sub", extraArgs);
        com.zeroc.Ice.ObjectPrx obj = communicator.propertyToProxy("TopicManager.Proxy");
        com.zeroc.IceStorm.TopicManagerPrx topicManager = com.zeroc.IceStorm.TopicManagerPrx.checkedCast(obj);

        com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("File.Subscriber");

        File file = new FileI();
        com.zeroc.Ice.ObjectPrx proxy = adapter.addWithUUID(file).ice_oneway();

        subscriberID = proxy.ice_getIdentity().name; // Obtener el ID del suscriptor
        System.out.println("Subscriber ID: " + subscriberID); // Mostrar el ID del
        adapter.activate();

        com.zeroc.IceStorm.TopicPrx topic = null;
        try {
            topic = topicManager.retrieve("files");
            java.util.Map<String, String> qos = null;
            topic.subscribeAndGetPublisher(qos, proxy);
        } catch (com.zeroc.IceStorm.BadQoS | com.zeroc.IceStorm.AlreadySubscribed | com.zeroc.IceStorm.NoSuchTopic | com.zeroc.IceStorm.InvalidSubscriber e) {
            System.out.println("Error al manejar el topico o suscriptor: " + e.getMessage());
        }

        final com.zeroc.IceStorm.TopicPrx topicF = topic;
        final com.zeroc.Ice.ObjectPrx subscriberF = proxy;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                topicF.unsubscribe(subscriberF);
                worker.disconect();
            } finally {
                communicator.destroy();
                // Apagar el ThreadPool al cerrar
                executorService.shutdown();
            }
        }));

        communicator.waitForShutdown();

        topic.unsubscribe(proxy);
    }
}
