

import com.zeroc.Ice.Current;


import Demo.File;

public class Client {
    public static  class FileI implements File{

        @Override
        public void Share(String document, Current current) {
            System.out.println(document);
        }
    }

    public static void main(String[] args){
        int status = 0;
        java.util.List<String> extraArgs = new java.util.ArrayList<String>();
        com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "config.sub", extraArgs);
        com.zeroc.Ice.ObjectPrx obj = communicator.propertyToProxy("TopicManager.Proxy");
        com.zeroc.IceStorm.TopicManagerPrx topicManager = com.zeroc.IceStorm.TopicManagerPrx.checkedCast(obj);
    
        com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("File.Subscriber");
    
        File file = new FileI();
        com.zeroc.Ice.ObjectPrx proxy = adapter.addWithUUID(file).ice_oneway();
        
        String subscriberID = proxy.ice_getIdentity().name; // Obtener el ID del suscriptor
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
