

public class Database {

    public static void main(String[] args)
    {
        java.util.List<String> extraArgs = new java.util.ArrayList<String>();

        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args,"config.db",extraArgs))
        {
            if(!extraArgs.isEmpty())
            {
                System.err.println("too many arguments");
                for(String v:extraArgs){
                    System.out.println(v);
                }
            }
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("QueryService");
            QueryServiceI queryService = new QueryServiceI();
            com.zeroc.Ice.Object object = queryService;
            adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("SimpleQueryService"));
            adapter.activate();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Intercepted shutdown signal. Closing resources...");
                queryService.disconect();
            }));
            communicator.waitForShutdown();
        }
    }
}

