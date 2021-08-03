import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;


public class Client extends Thread { 
    
    private static int numberOfTransactions;   		/* Number of transactions to process */
    private static int maxNbTransactions;      		/* Maximum number of transactions */
    private static Transactions [] transaction; 	        /* Transactions to be processed */
    private String clientOperation;    			/* sending or receiving */
       
	/** Constructor method of Client class
 	 * 
     * @return 
     * @param
     */
     Client(String operation)
     { 
       if (operation.equals("sending"))
       { 
           System.out.println("\n Initializing client sending application ...");
           numberOfTransactions = 0;
           maxNbTransactions = 100;
           transaction = new Transactions[maxNbTransactions];  
           clientOperation = operation; 
           System.out.println("\n Initializing the transactions ... ");
           readTransactions();
           System.out.println("\n Connecting client to network ...");
           String cip = Network.getClientIP();
           if (!(Network.connect(cip))) {
               System.out.println("\n Terminating client application, network unavailable");
               System.exit(0);
           }
       	}
       else
    	   if (operation.equals("receiving")) {
    		   System.out.println("\n Initializing client receiving application ...");
    		   clientOperation = operation; 
           }
     }
           
    /** 
     * Accessor method of Client class
     * 
     * @return numberOfTransactions
     * @param
     */
     public int getNumberOfTransactions()
     {
         return numberOfTransactions;
     }
            
    /** 
     * Mutator method of Client class
     * 
     * @return 
     * @param nbOfTrans
     */
     public void setNumberOfTransactions(int nbOfTrans)
     { 
         numberOfTransactions = nbOfTrans;
     }
         
    /** 
     * Accessor method of Client class
     * 
     * @return clientOperation
     * @param
     */
     public String getClientOperation()
     {
         return clientOperation;
     }
         
    /** 
     * Mutator method of Client class
	 * 
	 * @return 
	 * @param operation
	 */
	 public void setClientOperation(String operation)
	 { 
	     clientOperation = operation;
	 }
         
    /** 
     * Reading of the transactions from an input file
     */
     public void readTransactions()
     {
        Scanner inputStream = null;     	/* Transactions input file stream */
        int i = 0;                      		/* Index of transactions array */
        
        try
        {
        	inputStream = new Scanner(new FileInputStream("/home/anastassy/IdeaProjects/comp_346_assignment_2/src/transaction2.txt"));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File transaction.txt was not found");
            System.out.println("or could not be opened.");
            System.exit(0);
        }
        while (inputStream.hasNextLine())
        {
            try
            {   transaction[i] = new Transactions();
                transaction[i].setAccountNumber(inputStream.next());            /* Read account number */
                transaction[i].setOperationType(inputStream.next());            /* Read transaction type */
                transaction[i].setTransactionAmount(inputStream.nextDouble());  /* Read transaction amount */
                transaction[i].setTransactionStatus("pending");                 /* Set current transaction status */
                i++;
            }
             catch(InputMismatchException e)
            {
                System.out.println("Line " + i + "file transactions.txt invalid input");
                System.exit(0);
            }
            
        }
        setNumberOfTransactions(i);		/* Record the number of transactions processed */
        
        /* System.out.println("\n DEBUG : Client.readTransactions() - " + getNumberOfTransactions() + " transactions processed"); */
        
        inputStream.close();

     }
     
    /** 
     * Sending the transactions to the server 
     * 
     * @return 
     * @param
     */
     public void sendTransactions() {
         int i = 0;     /* index of transaction array */
         
         while (i < getNumberOfTransactions()) {

             // If the Network buffer is full, then yield CPU usage:
//             while(Network.getInBufferStatus().equals("full")) {
//                 Thread.yield();
//             } /* Alternatively, busy-wait until the network input buffer is available */
                                              	
            transaction[i].setTransactionStatus("sent");   /* Set current transaction status */
           
            System.out.println("\n DEBUG : Client.sendTransactions() - sending transaction on account " + transaction[i].getAccountNumber());
            
            Network.send(transaction[i]);                            /* Transmit current transaction */
            i++;          
         }
         
    }
         
 	/** 
  	 * Receiving the completed transactions from the server
     * 
     * @return 
     * @param transact
     */
     public void receiveTransactions(Transactions transact)
     {
         int i = 0;     /* Index of transaction array */
         
         while (i < getNumberOfTransactions()) {

             // Yield CPU usage if the Network buffer is empty
//             while(Network.getOutBufferStatus().equals("empty")) {
//                 Thread.yield();
//             }  	/* Alternatively, busy-wait until the network output buffer is available */

            Network.receive(transact);                               	/* Receive updated transaction from the network buffer */
            
            System.out.println("\n DEBUG : Client.receiveTransactions() - receiving updated transaction on account " + transact.getAccountNumber());
            
            System.out.println(transact);                               /* Display updated transaction */    
            i++;
         } 
    }
     
    /** 
     * Create a String representation based on the Client Object
     * 
     * @return String representation
     * @param 
     */
     public String toString() {
    	 return ("\n client IP " + Network.getClientIP() + " Connection status" + Network.getClientConnectionStatus() + "Number of transactions " + getNumberOfTransactions());
     }
    
       
    /** Code for the run method
     */
    public void run() {

        Transactions transact = new Transactions();
        long sendClientStartTime = System.currentTimeMillis(), sendClientEndTime, receiveClientStartTime = System.currentTimeMillis(), receiveClientEndTime;

        /* Implement the code for the run method */

        // Send transactions to the Network buffer and yield the CPU when the buffer is full
        if (this.clientOperation.equals("sending")) {
            this.sendTransactions();
            sendClientEndTime = System.currentTimeMillis();
            System.out.println("\n Terminating client sending thread - " + " Running time " + (sendClientEndTime - sendClientStartTime) + " milliseconds");
        }

        // Receive transactions from the Network buffer and yield the CPU usage in case the Network is empty
        if (this.clientOperation.equals("receiving")) {
            this.receiveTransactions(transact);
            Network.disconnect(Network.getClientIP());

            receiveClientEndTime = System.currentTimeMillis();
            System.out.println("\n Terminating client receiving thread - " + " Running time " + (receiveClientEndTime - receiveClientStartTime) + " milliseconds");

        }

                
    }
}
