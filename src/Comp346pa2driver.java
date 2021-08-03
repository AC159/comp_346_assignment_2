
public class Comp346pa2driver {

    /** 
     * main class
     * @param args the command line arguments
     */
    public static void main(String[] args) {

    	Network objNetwork = new Network();            /* Activate the network */
        objNetwork.start();

        Server objServer1 = new Server("THREAD1");                                 /* Start the server */
        objServer1.start();
        Server objServer2 = new Server("THREAD2");                                 /* Start the server */
        objServer2.start();

        Client objClient1 = new Client("sending");          /* Start the sending client thread */
        objClient1.start();
        Client objClient2 = new Client("receiving");        /* Start the receiving client thread */
        objClient2.start();

    }


}
