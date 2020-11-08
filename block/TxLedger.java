import java.util.Hashtable;
import java.util.HashSet;
import java.util.ArrayList;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.InvalidKeyException;

/**
 *  TxLedeger consists of a UTXOlist listening unspent transaction outputs
    and a TxIdNameMap     which gives readable names to transaction ids.
    
    TxLedger is the ledger maintaining cryptocurrency.
    It has  a method processTransaction   for processing a transaction
       (which changes the UTXO by deleting the now spent transaction outputs and adding the new unspent transaction outputs
        and adds the name of the transaction to the TxIdNameMap

     It has as well a method for checking a transaction which calls the corresponding check method for the transaction in question

     Print functions print the underlying UTXO list.
*/


class TxLedger {


    /* 
       This flag toggles whether to print certain keys/addresses
       as well in Base58 and not only as converted strings
     
       Needs to be changed by changing the code
    */

    private static boolean PRINTBASE58 = false;   // whether to print as well in Base58 (except for explicitly requested or selected ones)        
    /*
     * the underlying UTXO list and TxIdNameMap
     */

    
    private UTXOlist utxoList;
    private TxIdNameMap txIdNameMap;


    /* operations returning the underlying UTXO list and TxIdNameMap */
    
    public UTXOlist utxoList(){
	return utxoList;
    }

    public TxIdNameMap txIdNameMap(){
	return txIdNameMap;
    }


    /* 
     * constructor for forming the initial empty TxLedger
     */

    public TxLedger(){
	utxoList = new UTXOlist();
	txIdNameMap = new TxIdNameMap();
    };


    /* 
     * determine whether a txToutput or Input of a transaction is listed
     */

    
    public boolean hasEntry(TxOutput txOutput){
	return utxoList().hasEntry(txOutput);
    };

    public boolean hasEntry(Input input){
	return hasEntry(input.txOutput());
    };
    
    
    /*
     * lookup the transaction id by the name of the transaction
     */
    
    public TxId name2Txid(String txIdName)
	throws HashMapLookupException {
	return txIdNameMap().name2TxId(txIdName);
    }


    /*
     * Process a transaction by processing the underlying the transaction throught the underlying utxoLIst
     *  and adding the name of the transaction to the txIdNameMap
     */
    
    public void processTransaction(Transaction transact)
	throws HashMapLookupException, UTXOOutputAlreadyExistsException, UTXOOutputDoesntExistsException
    {
	utxoList().processTransaction(transact);
	txIdNameMap().add(transact);
    }

    /* 
     * Base58 representation of the underlying utxolist
     */

    public String toStringBase58()
	throws HashMapLookupException
    {
	return utxoList().toStringBase58();
    }



    /* 
     * human readable represenation of the utxolist, looking up usernames using pubKeyMap
     */
    


    public String toString(PublicKeyMap pubKeyMap)
	
	throws HashMapLookupException
    {
	return utxoList().toString(txIdNameMap,pubKeyMap);
    }


    /* 
     * Print Base58 representation of the underlying utxolist
     */
    
    public void printBase58()
	throws HashMapLookupException	
    {
	System.out.println(toStringBase58());
    };



    /* 
     * print human readable represenation of the utxolist, looking up usernames using pubKeyMap
     */
    
    
    public void print(PublicKeyMap pubKeyMap)
	
	throws HashMapLookupException
    {
	System.out.println(toString(pubKeyMap));
    };

    /*
     * check a  transaction by running the  corresponding operation in the transaction.
     */

    public boolean checkTransaction(Transaction tr,PublicKeyMap pubKeyMap)
	throws HashMapLookupException
    {
	return tr.check(this,pubKeyMap);
    }


    /* 
     * generic test function  
     *
     * prints the transaction checks it processses it
     * and shows the state of the txLedger after processing it.
     */


    public void testCase(String header,Transaction tr, PublicKeyMap pubKeyMap)
	throws HashMapLookupException, UTXOOutputAlreadyExistsException,UTXOOutputDoesntExistsException { 
	System.out.println(header);
	if (PRINTBASE58)
	    tr.printBase58();
	//tr.print(utxoList(),txIdNameMap(),pubKeyMap);
	System.out.println("Transaction is valid = " + checkTransaction(tr,pubKeyMap));
	if(checkTransaction(tr,pubKeyMap)) {
		processTransaction(tr);
	}
	if (PRINTBASE58)
	    {
		System.out.println("legdger(Base58)=" + toStringBase58());
		System.out.println();
	    };
	System.out.println("After processesing ledger=\n" + toString(pubKeyMap));
	System.out.println();
	
    }


    /*
     * Simple Example 
     *  Demonstrating how to create a coinbase transaction and using it to transfer money  to two people
     *
     * 
     */

    public static void test2()
	throws SignatureException,InvalidKeyException, HashMapLookupException, UTXOOutputAlreadyExistsException,
	       UTXOOutputDoesntExistsException, ArrayListIndexOutOfRangeException {
	PublicPrivateKeyMap keyMap = SampleKeyMap.generate(new String[]{ "Alice1", "Alice2", "Bob1", "Bob2", "Carol1", "Carol2",
									 "Carol3", "David1", "David2"});	
	PublicKeyMap pubKeyMap = keyMap.toPublicKeyMap();


	byte[] sampleMessage1 = KeyUtils.integer2ByteArray(1);
	byte[] sampleMessage2 = KeyUtils.integer2ByteArray(2);
	byte[] sampleMessage3 = KeyUtils.integer2ByteArray(3);
	byte[] signedMessage1 = keyMap.signMessage(sampleMessage1,"Alice1");
	byte[] signedMessage2 = keyMap.signMessage(sampleMessage2,"Bob1");	
	
	PublicKey pubKeyA =	pubKeyMap.getPublicKey("Alice1");
	PublicKey pubKeyB =	pubKeyMap.getPublicKey("Bob1");
	PublicKey pubKeyC =	pubKeyMap.getPublicKey("Carol1");	
	

	TxLedger txLedger = new TxLedger();
        /* First coinbase transaction */
	
	InputList inputList1 = new InputList();
	OutputList outputList1 = new OutputList("Carol1",10,"David1",5,pubKeyMap);
	String transactionName1 = "coinbase(Carol1-10,David1-5)";
	Transaction tr1 = new Transaction(inputList1,outputList1,transactionName1);
	txLedger.testCase("Transaction " + transactionName1,tr1,pubKeyMap);


	/* After this transaction  the TxLedger will show
         *
         *  [(txoutput=coinbase(Carol1-10,David1-5)[1],recipient=David1,amount=5),
	 *   (txoutput=coinbase(Carol1-10,David1-5)[0],recipient=Carol1,amount=10)]
         *
         *  Meaning that there is a transction  with string  name "coinbase(Carol1-10,David1-5)"
         *    its output number 0   (indicated by coinbase(Carol1-10,David1-5)[0]  )
         *    has recipient Carol1 and the amount sent to Carol1 is 10
         *    
         *   its output number 1   (indicated by coinbase(Carol1-10,David1-5)[1]  )
         *    has recipient David1 and the amount sent to David1 is 5
         *
	 */


	/*  
         * Now we take a transaction which uses transction  with name "coinbase(Carol1-10,David1-5)", output number 0
         *  (so this is transaction  output   coinbase(Carol1-10,David1-5)[0]
         *  and gives 5 to Alice1 and 5 to Bob1
         */
	

	OutputList outputList2 = new OutputList("Alice1",5,"Bob1",5,pubKeyMap);
	InputList inputList2 = new InputList("coinbase(Carol1-10,David1-5)",0,outputList2,
					     txLedger,keyMap);
	String transactionName2 = "Carol1-10->Alice1-5,Bob1-5";
	Transaction tr2 = new Transaction(inputList2,outputList2,transactionName2);
	txLedger.testCase("Transaction " + transactionName2,tr2,pubKeyMap);


	/*
         *  After running this transaction we have the  state
         *
         *  [(txoutput=Carol1-10->Alice1-5,Bob1-5[0],recipient=Alice1,amount=5),
         *   (txoutput=coinbase(Carol1-10,David1-5)[1],recipient=David1,amount=5),
         *   (txoutput=Carol1-10->Alice1-5,Bob1-5[1],recipient=Bob1,amount=5)]
         *
         *  so we have transaction outputs 
         *      Carol1-10->Alice1-5,Bob1-5[0]    giving to Alice1 5 units
         *      Carol1-10,David1-5)[1]           giving to David1 5 units
         *      Carol1-10->Alice1-5,Bob1-5[1]    giving to Bob1 5 units
	 *
         */
    };
	


    /** 
     * Test cases
     */            

    public static void test()
	throws SignatureException,InvalidKeyException, HashMapLookupException, UTXOOutputAlreadyExistsException,
	       UTXOOutputDoesntExistsException, ArrayListIndexOutOfRangeException {
	PublicPrivateKeyMap keyMap = SampleKeyMap.generate(new String[]{ "Alice1", "Alice2", "Bob1", "Bob2", "Carol1", "Carol2",
									 "Carol3", "David1", "David2"});	
	PublicKeyMap pubKeyMap = keyMap.toPublicKeyMap();


	byte[] sampleMessage1 = KeyUtils.integer2ByteArray(1);
	byte[] sampleMessage2 = KeyUtils.integer2ByteArray(2);
	byte[] sampleMessage3 = KeyUtils.integer2ByteArray(3);
	byte[] signedMessage1 = keyMap.signMessage(sampleMessage1,"Alice1");
	byte[] signedMessage2 = keyMap.signMessage(sampleMessage2,"Bob1");	
	
	PublicKey pubKeyA =	pubKeyMap.getPublicKey("Alice1");
	PublicKey pubKeyB =	pubKeyMap.getPublicKey("Bob1");
	PublicKey pubKeyC =	pubKeyMap.getPublicKey("Carol1");	
	

	TxLedger txLedger = new TxLedger();

	/** 
         Task 2: Create the following test cases (each test case should be run using the testCase method above):.
         Testcase 1: a transaction with no inputs and outputs;
         Testcase 2: a coinbase transaction giving Alice and Bub 10 units each.
         Testcase 3: a transaction with input the 1st output of the coinbase transaction,
                     giving Bob and Carol 5 units each.
         Testcase 4: a transaction combining the two outputs to Bob (in total 15) and giving the
	             result to Carol
	**/
    //Testcase1
	OutputList outputList1 = new OutputList();
	InputList inputList1 = new InputList();
	String transactionName1 = " ";
	Transaction tr1 = new Transaction(inputList1,outputList1,transactionName1);
	txLedger.testCase("Transaction " + transactionName1,tr1,pubKeyMap);
	
	//Testcase2
	InputList inputList2 = new InputList();
	OutputList outputList2 = new OutputList("Alice1",10,"Bob1",10,pubKeyMap);
	String transactionName2 = "coinbase(Alice1-10,Bob1-10)";
	Transaction tr2 = new Transaction(inputList2,outputList2,transactionName2);
	txLedger.testCase("Transaction " + transactionName2,tr2,pubKeyMap);
	
	//Testcase3
	OutputList outputList3 = new OutputList("Carol1",5,"Bob2",5,pubKeyMap);
	InputList inputList3 = new InputList(transactionName2,0,outputList3,txLedger,keyMap);
	String transactionName3 = "Alice1-10->Carol1-5,Bob2-5";
	Transaction tr3 = new Transaction(inputList3,outputList3,transactionName3);
	txLedger.testCase("Transaction " + transactionName3,tr3,pubKeyMap);
	
	//Testcase4
	OutputList outputList4 = new OutputList("Carol2",15,pubKeyMap);
	Input inputList4a = new Input(transactionName2,1,outputList4,txLedger.utxoList(),txLedger.txIdNameMap,keyMap);
	Input inputList4b = new Input(transactionName3,1,outputList4,txLedger.utxoList(),txLedger.txIdNameMap,keyMap);
	InputList inputList4=new InputList(inputList4a,inputList4b);
	String transactionName4 = "Bob1-10,Bob2-5->Carol2-15";
	Transaction tr4 = new Transaction(inputList4,outputList4,transactionName4);
	txLedger.testCase("Transaction " + transactionName4,tr4,pubKeyMap);

	/** Task 3: Create for each check defined in Task 1 one test case, which 
                 will fail that particular check (and passes all checks coming before it)**/
	//task1fail the output list has too big of values.
	OutputList outputList5 = new OutputList("Carol1",105,"Bob2",5,pubKeyMap);
	InputList inputList5 = new InputList(transactionName4,0,outputList5,
					     txLedger,keyMap);
	String transactionName5 = "Carol2-15->Carol1-105,Bob2-5";
	Transaction tr5 = new Transaction(inputList5,outputList5,transactionName5);
	txLedger.testCase("Transaction " + transactionName5,tr5,pubKeyMap);
	
	//task2fail the output which is used to generate signatures is different when generating input from when creating transactions.
	OutputList outputList6 = new OutputList("Carol1",5,"Bob2",5,pubKeyMap);
	InputList inputList6 = new InputList(transactionName4,0,outputList6,
					     txLedger,keyMap);
	String transactionName6 = "Carol2-15->Carol1-5,Bob2-5";
	Transaction tr6 = new Transaction(inputList6,outputList5,transactionName6);
	txLedger.testCase("Transaction " + transactionName6,tr6,pubKeyMap);
	
	//task3fail check if the txoutput 0 from transaction 2(finished before transaction4) is in current utoxlist (last transaction in utoxList is transaction4).
	OutputList outputList7 = new OutputList("Carol1",5,"Bob2",5,pubKeyMap);
	TxId txId = txLedger.txIdNameMap.name2TxId(transactionName2);
	Input input7 = new Input(txId,0,outputList7,pubKeyA,keyMap);		    	
	String transactionName7 = "Alice1-10->Carol1-5,Bob2-5";
	InputList inputList7 = new InputList(input7);
	Transaction tr7 = new Transaction(inputList7,outputList7,transactionName7);
	txLedger.testCase("Transaction " + transactionName7,tr7,pubKeyMap);

	//task4fail The inputs are duplicated.
	OutputList outputList8 = new OutputList("Carol1",5,pubKeyMap);
	Input inputList8a = new Input(transactionName4,0,outputList8,txLedger.utxoList(),txLedger.txIdNameMap,keyMap);
	Input inputList8b = new Input(transactionName4,0,outputList8,txLedger.utxoList(),txLedger.txIdNameMap,keyMap);
	InputList inputList8=new InputList(inputList8a,inputList8b);
	String transactionName8 = "Carol2-15->Carol1-5";
	Transaction tr8 = new Transaction(inputList8,outputList8,transactionName8);
	txLedger.testCase("Transaction " + transactionName8,tr8,pubKeyMap);
	
    }


    /** 
     * main function running test2 and test
     */            
    
    public static void main(String[] args)
	throws SignatureException,InvalidKeyException, HashMapLookupException, UTXOOutputAlreadyExistsException,
	       UTXOOutputDoesntExistsException, ArrayListIndexOutOfRangeException
    {

        System.out.println("Executing test2()");
	test2();
	System.out.println();
        System.out.println("Executing test()");	
	test();
    }    
    
    
    

}
    
