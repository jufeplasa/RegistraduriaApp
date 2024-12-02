
public class Consulta {

    private Demo.QueryServicePrx consult;
    private com.zeroc.Ice.Communicator communicator;

    public Consulta(String[] args){
        try{
            communicator = com.zeroc.Ice.Util.initialize(args,"config.client");
            consult = Demo.QueryServicePrx.checkedCast(communicator.propertyToProxy("QueryService.Proxy"));
            if(consult == null)
            {
                throw new Error("Invalid proxy");
            }
        } catch (com.zeroc.Ice.LocalException ex) {
            System.err.println("Error initializing Ice communicator or proxy: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.err.println("Unexpected error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void findInfoById(int documento) {
        String result = consult.consult(documento);
        System.out.println(result);
    }

    public void disconect(){
            try {
                communicator.destroy(); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}

