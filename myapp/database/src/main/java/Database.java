

public class Database {

    public static void main(String[] args)
    {
        java.util.List<String> extraArgs = new java.util.ArrayList<String>();

        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args,extraArgs))
        {
            communicator.getProperties().setProperty("Ice.Default.Package", "com.zeroc.demos.IceGrid.simple");
            if(!extraArgs.isEmpty())
            {
                System.err.println("too many arguments");
                for(String v:extraArgs){
                    System.out.println(v);
                }
            }
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("QueryService");
            // QueryServiceI queryService = new QueryServiceI();
            // com.zeroc.Ice.Object object = queryService;
            // adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("SimpleQueryService"));
            com.zeroc.Ice.Properties properties = communicator.getProperties();
            com.zeroc.Ice.Identity id = com.zeroc.Ice.Util.stringToIdentity(properties.getProperty("Identity"));
            adapter.add(new QueryServiceI(), id);
            adapter.activate();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Intercepted shutdown signal. Closing resources...");
                communicator.destroy();
            }));
            communicator.waitForShutdown();
        }
    }
}

