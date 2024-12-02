import java.util.ArrayList;
import java.util.List;

public class Worker {

    private Demo.QueryServicePrx consult;
    private Demo.MasterPrx masterPrx;
    private com.zeroc.Ice.Communicator communicator;
    private List<String> proccessedDocuments;

    public Worker(String[] args){
        try{
            proccessedDocuments = new ArrayList<String>();
            communicator = com.zeroc.Ice.Util.initialize(args,"config.client");
            consult = Demo.QueryServicePrx.checkedCast(communicator.propertyToProxy("QueryService.Proxy"));
            masterPrx = Demo.MasterPrx.checkedCast(communicator.propertyToProxy("Master.Proxy"));
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

    public void doTask(int documento) {
        String result = consult.consult(documento);
        proccessedDocuments.add(result);
        System.out.println(result);
    }

    public void sendTask(){
        String[] stringArray = proccessedDocuments.toArray(new String[0]);
        masterPrx.receiveTask(stringArray);
        System.out.println("Resultados enviados");
        proccessedDocuments.clear();
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

    public void disconect(){
            try {
                communicator.destroy(); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}

