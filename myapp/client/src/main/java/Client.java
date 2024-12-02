

import com.zeroc.Ice.Current;
import java.util.ArrayList;

import Demo.File;

public class Client {
    private static String subscriberID;
    private static Worker worker;
    private static final String MESSAGE_LIMIT = "END_OF_DOCUMENTS";
    /*
     * START STATIC CLASS FileI
     */
    public static  class FileI implements File{
        private ArrayList<String> avaibleDocs = new ArrayList<String>();
        private boolean avaibleId;
        /*
         * Metodo de suscripcion al publicador
         */
        @Override
        public void Share(String document, Current current) {
            if(document.length()==36){
                avaibleId= document.equals(subscriberID);
            }else{
                if (document.equals(MESSAGE_LIMIT)) {
                    processBatch();
                }
                else if (avaibleId) {
                    avaibleDocs.add(document);
                    System.out.println("Documento recibido: " + document); 
                }
            }
        }

        /*
         * Llama a la clase consulta realizar las consultas de los documentos disponibles.
         */
        private void processBatch() {
            if (!avaibleDocs.isEmpty()) {
                // Simular consulta en la base de datos
                System.out.println("Procesando lote de documentos...");
                for (String doc : avaibleDocs) {
                    int document= Integer.parseInt(doc);
                    worker.doTask(document);
                }
                worker.sendTask();
                avaibleDocs.clear();
            }
        }
    }
    /*
     * END STATIC CLASS FileI
     */

    public static void main(String[] args){

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
        } catch (com.zeroc.IceStorm.BadQoS|com.zeroc.IceStorm.AlreadySubscribed|com.zeroc.IceStorm.NoSuchTopic | com.zeroc.IceStorm.InvalidSubscriber e) {
            System.out.println("Error al manejar el topico o suscriptor: " + e.getMessage());
        }

        final com.zeroc.IceStorm.TopicPrx topicF = topic;
        final com.zeroc.Ice.ObjectPrx subscriberF = proxy;
        
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            try
            {
                topicF.unsubscribe(subscriberF);
                worker.disconect();
            }
            finally
            {
                communicator.destroy();
            }
        }));
        communicator.waitForShutdown();
    
        topic.unsubscribe(proxy);
    }

}
