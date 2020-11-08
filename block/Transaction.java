import java.security.PublicKey;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;

/**   Transaction
 *    consisting of a list of inputs and
 *    a list of outputs
 */    

public class Transaction {


    /* 
       This flag toggles whether to print certain keys/addresses
       as well in Base58 and not only as converted strings
     
       Needs to be changed by changing the code
    */
    
    private static boolean PRINTBASE58 = false;   

    /** The list of inputs  */
    private InputList inputs;

    /** The list of outputs  */
    private OutputList outputs;    

    /* a name given to the transaction
       used for giving readable names to transactions
    */
    
    private String transactionName;

    /**
     * Creates a new transaction 
     */ 
    public Transaction(InputList inputs, OutputList outputs,String transactionName){
	this.inputs = inputs;
	this.outputs= outputs;
	this.transactionName= transactionName;	
    }

    /* 
       retrieve the underlying transaction name
    */
    
    public String transactionName(){
	return this.transactionName;
    };

    /**
     * return the list of inputs
     */ 
    
    public InputList inputList(){
	return inputs;
    }


    /**
     * return the list of outputs
     */ 
    
    public OutputList outputList(){
	return outputs;
    }

    /* 
       checks whether we have a coinbase transations
       coinbase transactions are transactions with no inputs
    */
    

    public boolean isCoinbase(){
	return inputList().isEmpty();
    };


    /**
     * Task 1.1
     * Add a check that the 
     * sum of inputs is greater or equal than the sum of outputs
     *
     * however if a transaction is a coinase transaction (i.e. the
     *   list of inputs is empty
     *  then this check needs to return true  
     *
     *  otherwise there would be no valid coinbase transactions
     *   and therefore it would not be possible to create any valid transactions
     *
     *
     *  The argument txLedger is only used in order to be able to printout readable
     *   information about which input has the error in case of an error.
     *  The hasMapLookupException is as well triggered by printing out this message.
     *
     */

    public boolean checkTransactionAmountsValid (TxLedger txLedger)
    throws HashMapLookupException {
    	if (!this.isCoinbase()) {
    		if(this.inputList().toSum(txLedger.utxoList()) < this.outputList().toSum()) {
    			System.out.println("task1 fail");
    			return false;
    		}
    	}
    	return true;
    }


    /** 
     * Task 1.2
     * Check that the signatures in the inputs of a transaction are valid
     * Use txLedger and pubKeymap to print out the erroneous input in a readable format
     *
     * Again txLedger and pubKeyMap are used for printing out suitable error messages whereas
    */
    
    public boolean checkSignaturesValid(TxLedger txLedger, PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 
    	int size =  this.inputList().size();
		ArrayList<Input> inputs = this.inputList().inputList();
		OutputList outputs = this.outputList();
		for(int i=0;i<size;i++) {
			Input inputEach=inputs.get(i);
			byte[] signature = inputEach.signature();
			PublicKey puk=inputEach.publicKey();
			//1. inputs of the transaction
			//2. pub key of each input
			//3. signature introduced by each input only in this transaction
			//4. those are checked by the function in outputList checkSignature
			if(!outputs.checkSignature(inputEach.txOutput(),puk,signature )) {
				System.out.println("task2 fail");
				return false;
			}
		}
		return true;
    	
	
    }

    /* 
     * 
     * Task 1.3
     *  check all inputs in a transaction are unspent transaction outputs, i.e. in the underlying utxoList
     *  Use txLedger and pubKeymap to print out the erroneous input in a readable format
     *
     * Again txLedger and pubKeyMap are used for printing out suitable error messages whereas
     *  
     */

    //Check that each input in the transaction(txOutput for each input) is an entry in current utoxList.
    public boolean checkInputsAreInUTXO(TxLedger txLedger,PublicKeyMap pubKeyMap)
	throws HashMapLookupException
    {
    	int size =  this.inputList().size();
    	ArrayList<Input> inputs = this.inputList().inputList();
		for(int i=0;i<size;i++) {
			Input inputEach=inputs.get(i);
			PublicKey puk=inputEach.publicKey();
			if(!txLedger.utxoList().hasEntry(inputEach.txOutput())) {
				System.out.println("task3 fail");
				return false;
			}
		}
		return true;
    }

    /*
     *  Task 1.4
     *   check the inputs in the input list are different
     *   any transaction input can be used only once
     *   Use txLedger and pubKeymap to print out the erroneous input in a readable format
     *
     *   Again txLedger and pubKeyMap are used for printing out suitable error messages whereas
     *
     *   One way of programming this is to have an auxiliay set of transactiton outputs, which
     *      is initially empty. You can use HashSet to implement it.
     *   Then one goes through the transaction inputs one by one. If the transaction input is in the 
     *     auxiliary set, then it occurs twice, otherwise one adds it to the set, so when it occurs
     *     again it is noticed.
     */
    public boolean checkInputsDifferent(TxLedger txLedger,PublicKeyMap pubKeyMap)
	throws HashMapLookupException
    {	
    	//inputListFrequency is an empty ArrayList that memorizes the occurrences of each txOutput, to avoid duplications.
    	ArrayList<TxOutput> inputListFrequency=new ArrayList<TxOutput>();
    	int size =  this.inputList().size();
    	ArrayList<Input> inputs = this.inputList().inputList();
		for(int i=0;i<size;i++) {
			if(inputListFrequency.contains(
					inputs.get(i).txOutput())) {
				System.out.println("task4 fail");
				return false;
			}
			inputListFrequency.add(inputs.get(i).txOutput());			
		}
    	return true;
    }

    /*
     * Task 1.5
     *  Create a check function running all 4 checks above
     *
     * Again txLedger and pubKeyMap are used for printing out suitable error messages whereas     
     */

    public boolean check(TxLedger txLedger,PublicKeyMap pubKeyMap)
	throws HashMapLookupException
    {
    	if(this.checkInputsAreInUTXO(txLedger, pubKeyMap)&&this.checkInputsDifferent(txLedger, pubKeyMap)&&
    			this.checkSignaturesValid(txLedger, pubKeyMap)&&this.checkTransactionAmountsValid(txLedger)) {
    	return true;
    	}
    	return false;
    }
	    

    /* 
       get raw data from which the transaction id is computed
     */


    public SigData getRawData()
	throws HashMapLookupException { 	
	SigData sigData = new SigData();
	ArrayList<Input> inputs = inputList().inputList();
	for (int i = 0; i < inputs.size(); i++){
	    sigData.addTxId(inputs.get(i).txId());
	    sigData.addInteger(inputs.get(i).txOutput().outputNumber());
	    sigData.addTxId(inputs.get(i).txOutput().txId());
	};
	ArrayList<Output> outputs = outputList().outputList();	
	for (int i = 0; i < outputs.size(); i++){	
	    sigData.addAddress(outputs.get(i).getRecipient());
	    sigData.addInteger(outputs.get(i).getAmount());
        }
	return sigData;
    }


    /*
      compute the transaction id by hashing the raw data

     */
    
    public TxId txId ()
    	throws HashMapLookupException { 
	return new TxId(getRawData());
    }
    
    /** 
     * create String representation of the transaction using Base58 encoding
     *
     */
    
    public String toStringBase58()
	throws HashMapLookupException
    {
	return "Name=" + transactionName() + "\n"
	    + "Inputs:"    + inputList().toStringBase58() + "\n" 
	    + "Outputs:" + outputList().toStringBase58(); 
    };	


    /** 
     * Create string of the entry. 
     *
     *  the sendername is looked up using pubKeyMap, the transactionName using txIdNameMap and the sender amount is looked upt
        using utxoList
     */
    
    public String toString(UTXOlist utxoList, TxIdNameMap txIdNameMap, PublicKeyMap pubKeyMap)
	throws HashMapLookupException
    {
	return "Name=" + transactionName() + "\n"
	    + "Inputs:"    + inputList().toString(utxoList,txIdNameMap,pubKeyMap) + "\n" 
	    + "Outputs:" + outputList().toString(pubKeyMap);
	//	        outputList().print("User: "," spends ",")",pubKeyMap);	

    };


    /** 
     * print the transaction using Base58 encoding
     *
     */
    
    public void printBase58()
	throws HashMapLookupException	
    {
	System.out.println(toStringBase58());
    };	


    /** 
     * print the entry
     *
     *  the sendername is looked up using pubKeyMap, the transactionName using txIdNameMap and the sender amount is looked upt
        using utxoList
     */
    
    public void print(UTXOlist utxoList, TxIdNameMap txIdNameMap, PublicKeyMap pubKeyMap)
	throws HashMapLookupException
    {
	System.out.println(toString(utxoList,txIdNameMap,pubKeyMap));
    };


    /* 
       As before but using  txLedger which has as components utxoList and txIdNameMap
    */

    public void print(TxLedger txLedger, PublicKeyMap pubKeyMap)
	throws HashMapLookupException
    {
	print(txLedger.utxoList(),txLedger.txIdNameMap(),pubKeyMap);
    };
	       
    
	       


    /** 
     * Generic Test cases, providing a headline
     *    printing out the transaction
     *    and printing out whether it is valid
     */

    
    public void testCase(String header,TxLedger txLedger, PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 
	System.out.println(header);
	if (PRINTBASE58)
	    printBase58();
	print(txLedger,pubKeyMap);
	System.out.println("Is valid regarding sums = " + checkTransactionAmountsValid(txLedger));
	System.out.println("");
    }
	

    /** 
     * Test cases
     */            

    public static void test()
	throws SignatureException,InvalidKeyException, HashMapLookupException, UTXOOutputAlreadyExistsException,
	       UTXOOutputDoesntExistsException, ArrayListIndexOutOfRangeException {
	TxLedger txLedger = new TxLedger();
	PublicPrivateKeyMap keyMap = SampleKeyMap.generate(new String[]{ "Alice", "Bob", "Carol", "David"});
	PublicKeyMap pubKeyMap = keyMap.toPublicKeyMap();

		//Test cases done in TxLedger	
        
    }


    /** 
     * main function running test cases
     */            
    
    public static void main(String[] args)
	throws SignatureException,InvalidKeyException, HashMapLookupException, UTXOOutputAlreadyExistsException,
	       UTXOOutputDoesntExistsException, ArrayListIndexOutOfRangeException
    { 
	Transaction.test();
    }    
    
}

