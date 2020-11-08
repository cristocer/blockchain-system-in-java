import java.security.PublicKey;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.util.ArrayList;

/** InputList
 *  defines a list of Inputs  of a  transaction
 *
 */

public class InputList{


    /** 
      * underlying list of inputs
      */
    
    private ArrayList<Input> inputList;

    /** 
      * add an entry given by txOutput,publicKey, and a signature
      */
    
    public void add(TxOutput txOutput,PublicKey publicKey,byte[] signature){
	inputList.add(new Input(txOutput,publicKey,signature));
    }

    /* 
        as before
        but providing the txOuput by its two components  txId and outputNumber
    */
        

    public void add(TxId txId, int outputNumber,PublicKey publicKey,byte[] signature){
	inputList.add(new Input(txId,outputNumber,publicKey,signature));
    }


    


    /** 
      * add an Input to the list
      */
    

    public void add(Input input){
	inputList.add(input);
    }


    /*
     * add an input determined by a txIdName, an outputnumber, 
     *    a transaction output list, a txLedger, and a public private key map
     *    this operation computes the signature by determining the user from txLedger,
     *    the message to be signed from the ouputList, signs it using the private key
     *    determined from the PublicPrivateKeyMap
     */

    public void add(String txIdName, int outputNumber, OutputList outputList,
		   TxLedger txLedger, PublicPrivateKeyMap keyMap)
	throws SignatureException, InvalidKeyException, HashMapLookupException {
	add(new Input(txIdName,outputNumber,outputList,txLedger.utxoList(),txLedger.txIdNameMap(),keyMap));
    }	

    /*
     * add an input determined by a txIdName, an outputnumber, 
     *    a transaction output list, a UTXOlis, a txIdName, and a public private key map
     *    this operation computes the signature by determining the user from txLedger,
     *    the message to be signed from the ouputList, signs it using the private key
     *    determined from the PublicPrivateKeyMap
     */

    public void add(String txIdName, int outputNumber, OutputList outputList,
		   UTXOlist utxoList,TxIdNameMap txIdNameMap, PublicPrivateKeyMap keyMap)
	throws SignatureException, InvalidKeyException, HashMapLookupException
    {
	add(new Input(txIdName,outputNumber,outputList,utxoList,txIdNameMap,keyMap));	
    }	


    /** 
      * constructor constructing the empty user amount list
      */
    
    public InputList(){
	inputList =  new ArrayList<Input>();
    }

    /** 
      * constructor constructing a list containing one entry
          consisting of a user, an amount, and a signature
      */    
    
    public InputList(TxOutput txOutput,PublicKey publicKey,byte[] signature){
	inputList = new ArrayList<Input>();
	add(txOutput,publicKey,signature);
    }

    /* 
        as before
        but providing the txOuput by its two components  txId and outputNumber
    */
        
    
    public InputList(TxId txId, int outputNumber,PublicKey publicKey, byte[] signature){
	inputList = new ArrayList<Input>();
	add(txId,outputNumber,publicKey,signature);
    }


    /** 
      * constructor constructing a list containing one entry
          given by an input
      */    
    


    public InputList(Input input){
	inputList = new ArrayList<Input>();
	add(input);
    }


    /** 
     * as before but having a list with two inputs
      */        
    
    public InputList(Input input1,Input input2){
	inputList = new ArrayList<Input>();
	add(input1);
	add(input2);	
    }    
    


    /*  
        as before but adding creating a list with two inputs
     */
    
    public InputList(TxId txId1, int outputNumber1,PublicKey publicKey1, byte[] signature1,
		     TxId txId2, int outputNumber2,PublicKey publicKey2, byte[] signature2    ){
	inputList = new ArrayList<Input>();
	add(txId1,outputNumber1,publicKey1,signature1);
	add(txId2,outputNumber2,publicKey2,signature2);	
    }	

    


    /** 
     * If we have a PublicPrivateKeyMap covering the sender
     * and an outputList
     *
     * then we can compute the signature by signing the transaction to be signed consisting
     *    of the public key and input amount and the output list
     *  using the private key of the user
     */    


    public InputList(TxId txId, int outputNumber, OutputList outputList,
		     UTXOlist utxoList,TxIdNameMap txIdNameMap, PublicPrivateKeyMap keyMap)
	throws SignatureException, InvalidKeyException, HashMapLookupException {
	inputList = new ArrayList<Input>();
	add(new Input(txId,outputNumber,outputList,utxoList,txIdNameMap,keyMap));
    }

    /*
       as before but using txLedger from which one obtains utxoList and txIdNameMap
    */

    public InputList(TxId txId, int outputNumber, OutputList outputList,
		     TxLedger txLedger, PublicPrivateKeyMap keyMap)
	throws SignatureException, InvalidKeyException, HashMapLookupException {
	inputList = new ArrayList<Input>();
	add(new Input(txId,outputNumber,outputList,txLedger,keyMap));
    }    


    /* 
         Variant of the above where we don't lookup the txId in txIdNameMap,
         but provide it directly.

         This allows to create transaction input lists with incorrect transaction ids
          and can be used for testing check funtions.
    */

    public InputList(TxId txId, int outputNumber, OutputList outputList,
		     PublicKey senderPublicKey, PublicPrivateKeyMap keyMap)
	throws SignatureException, InvalidKeyException, HashMapLookupException {
	inputList = new ArrayList<Input>();
	add(new Input(txId,outputNumber,outputList,senderPublicKey,keyMap));
    }    
    
    
    /*
        As before, but now we provide the name of the txId from which we obtain using
        txIdNameMap the txIdName
        The senderPUblicKey wee obtain from utxoList
     */

    public InputList(String txIdName, int outputNumber, OutputList outputList,
		     UTXOlist utxoList,TxIdNameMap txIdNameMap, PublicPrivateKeyMap keyMap)
	throws SignatureException, InvalidKeyException, HashMapLookupException {
	inputList = new ArrayList<Input>();
	add(new Input(txIdName,outputNumber,outputList,utxoList,txIdNameMap,keyMap));
    }


    /**  as before but for 2 users, using outputlist and keymap **/
    
    public InputList(String txIdName1, int outputNumber1, 
		     String txIdName2, int outputNumber2, OutputList outputList,
		     UTXOlist utxoList,TxIdNameMap txIdNameMap, PublicPrivateKeyMap keyMap)  		     
	throws SignatureException, InvalidKeyException, HashMapLookupException {  
	inputList = new ArrayList<Input>();
	add(new Input(txIdName1,outputNumber1,outputList,utxoList,txIdNameMap,keyMap));
	add(new Input(txIdName2,outputNumber2,outputList,utxoList,txIdNameMap,keyMap));	
    }    

    


    /*
       as before but using txLedger from which one obtains utxoList and txIdNameMap
    */



    public InputList(String txIdName, int outputNumber, OutputList outputList,
		     TxLedger txLedger,PublicPrivateKeyMap keyMap)
	throws SignatureException, InvalidKeyException, HashMapLookupException {
	inputList = new ArrayList<Input>();
	add(new Input(txIdName,outputNumber,outputList,txLedger.utxoList(),txLedger.txIdNameMap(),keyMap));
    }    

    /*
         As before but creating an inputList with two entries
    */
    
    public InputList(String txIdName1, int outputNumber1, 
		     String txIdName2, int outputNumber2, OutputList outputList,
		     TxLedger txLedger, PublicPrivateKeyMap keyMap)  		     
	throws SignatureException, InvalidKeyException, HashMapLookupException {  
	inputList = new ArrayList<Input>();
	add(new Input(txIdName1,outputNumber1,outputList,txLedger.utxoList(),txLedger.txIdNameMap(),keyMap));
	add(new Input(txIdName2,outputNumber2,outputList,txLedger.utxoList(),txLedger.txIdNameMap(),keyMap));	
    }    
    

    

    /** 
      * constructor constructing a list containing two entries
          each given by its components
      */        

    public InputList(TxOutput txOutput1,PublicKey publicKey1,byte[] signature1,
		     TxOutput txOutput2,PublicKey publicKey2,byte[] signature2){
	inputList = new ArrayList<Input>();
	add(txOutput1,publicKey1,signature1);
	add(txOutput2,publicKey2,signature2);	
    }

    /** 
      * obtain the underlying list
      */
    
    public ArrayList<Input> inputList(){
	return(inputList);
    };


    /* obtain the number of entries */
    
    public int size(){
	return(inputList().size());
    }

    /* 
       check whether the list is empty
       That means that we have a coinbase transaction
    */
    

    public boolean isEmpty(){
	return size() == 0;
    };


    /* 
       get one entry of the inputList by its index 
    */
    
    public Input get(int index)
    throws ArrayListIndexOutOfRangeException {
	if (index < this.size()){
	    return (inputList()).get(index);
	} else {
	    throw new ArrayListIndexOutOfRangeException ("inputList=" + this.toString() + ",index=" + index);
		}
    }

    /** 
      * compute the sum of amounts of the entries in an inputList
      */

    public int toSum(UTXOlist utxoList)
    throws HashMapLookupException {
	int result = 0;
	for (Input  entry : inputList()){
	    result += entry.amount(utxoList);
		};
	return result;
    };

    /** 
     * Create string of the entry in the form  <word1>transactionId<word2>outputnumber<word3>
     *
     */
    
    public String toStringBase58(String word1, String word2, String word3)
    {
	String result = "[";
	boolean notFirstEntry = false;
	for (Input input : inputList()){
	    if (notFirstEntry)
		result += ",";
	    notFirstEntry = true;
	    result +=  input.toStringBase58(word1,word2,word3);
	};
	return result + "]";
    };	


    /* 
       as toStringBase58(word1,word2,word3)
       but using default formatting
    */
    
    public String toStringBase58()
    { 
	return  toStringBase58("","[","]");
    }
    
    


    /** 
     * Create string of the entry in the form  <word1>transactionName<word2>outputnumber<word3>sendername<word4>amount<word5>
     *
     *  the sendername is looked up using pubKeyMap, the transactionName using txIdNameMap and the sender amount is looked upt
        using utxoList
     */
    
    public String toString(String word1, String word2, String word3, String word4, String word5,
			   UTXOlist utxoList, TxIdNameMap txIdNameMap, PublicKeyMap pubKeyMap)
	throws HashMapLookupException
    {
	String result = "[";
	boolean notFirstEntry = false;	
	for (Input input : inputList()){
	    if (notFirstEntry)
		result += ",";
	    notFirstEntry = true;	    
	    result += input.toString(word1, word2,word3,word4,word5,utxoList,txIdNameMap,pubKeyMap);
	};
	return result + "]";
    };
	       

    /* 
       as toString(word1,word2,word3,word4,word5,utxoList, txIdNameMap, pubKeyMap)
       but using default formatting
    */
    
    public String toString(UTXOlist utxoList, TxIdNameMap txIdNameMap, PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 
	return  toString("(input=","[","],sender=",",amount=",")",utxoList,txIdNameMap,pubKeyMap);	
    }

    


    /** 
     * Print the input in the form  <word1>transactionId<word2>outputnumber<word3>
     * using Base58 encoding
     *
     */
    
    public void printBase58(String word1, String word2, String word3)
    { 
	System.out.println(toStringBase58(word1,word2,word3));
    }

    /* 
       as printBase58(word1,word2,word3)
       but using default formatting
    */

    

    public void printBase58()
    { 
	System.out.println(toStringBase58());
    }
    
    


    /** 
     * Print input ino the form  <word1>transactionName<word2>outputnumber<word3>sendername<word4>amount<word5>
     *
     *  the sendername is looked up using pubKeyMap, the transactionName using txIdNameMap and the sender amount is looked upt
        using utxoList
     */
    
    public void print(String word1, String word2, String word3, String word4, String word5,
		      UTXOlist utxoList, TxIdNameMap txIdNameMap, PublicKeyMap pubKeyMap)
	throws HashMapLookupException {
	System.out.println(toString(word1,word2,word3,word4,word5,utxoList,txIdNameMap,pubKeyMap));
    }
    

    /* 
       as print(word1,word2,word3,word4,word5,utxoList, txIdNameMap, pubKeyMap)
       but using default formatting
    */
    
    public void print(UTXOlist utxoList, TxIdNameMap txIdNameMap, PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 
	System.out.println(toString(utxoList,txIdNameMap,pubKeyMap));
    }
    


    /*
        generic function for running test cases
    */

    public void testCase(String header,UTXOlist utxoList, TxIdNameMap txIdNameMap, PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 	
	System.out.println(header);
	printBase58();
	print(utxoList,txIdNameMap,pubKeyMap);	
	System.out.println("Sum of Amounts = " + toSum(utxoList));	
	System.out.println();	
    };


    /** 
     * Test cases
     */            
    public static void test()
	throws SignatureException, InvalidKeyException,HashMapLookupException,UTXOOutputAlreadyExistsException { 
	PublicPrivateKeyMap keyMap = SampleKeyMap.generate(new String[]{ "Alice", "Bob", "Carol", "David"});
	PublicKeyMap pubKeyMap = keyMap.toPublicKeyMap();	
	byte[] sampleMessage1 = KeyUtils.integer2ByteArray(1);
	byte[] sampleMessage2 = KeyUtils.integer2ByteArray(2);
	byte[] sampleMessage3 = KeyUtils.integer2ByteArray(3);
	byte[] signedMessage1 = keyMap.signMessage(sampleMessage1,"Alice");
	byte[] signedMessage2 = keyMap.signMessage(sampleMessage2,"Bob");	
	PublicKey pubKeyA =	pubKeyMap.getPublicKey("Alice");
	PublicKey pubKeyB =	pubKeyMap.getPublicKey("Bob");
	PublicKey pubKeyC =	pubKeyMap.getPublicKey("Carol");
	Address addressA =	pubKeyMap.getAddress("Alice");
	Address addressB =	pubKeyMap.getAddress("Bob");
	Address addressC =	pubKeyMap.getAddress("Carol");		
	TxId txId1  = new TxId(sampleMessage1);
	TxId txId2  = new TxId(sampleMessage2);
	TxId txId3  = new TxId(sampleMessage3);		
	InputList inputList1 = new InputList(txId1,0,pubKeyA,signedMessage1);
	InputList inputList2 = new InputList(txId2,2,pubKeyB,signedMessage2);
	InputList inputList3 = new InputList(txId1,0,pubKeyA,signedMessage1,txId2,2,pubKeyB,signedMessage2);
	UTXOlist utxoList = new UTXOlist();
	utxoList.add(txId1,0,addressA,10);
	utxoList.add(txId1,2,addressB,10);
	utxoList.add(txId2,2,addressC,5);
	TxIdNameMap txIdNameMap = new TxIdNameMap();
	txIdNameMap.add(txId1,"tx1");
	txIdNameMap.add(txId2,"tx2");
	txIdNameMap.add(txId3,"tx3");
        inputList1.testCase("Inputlist 1",utxoList,txIdNameMap,pubKeyMap);
        inputList2.testCase("Inputlist 2",utxoList,txIdNameMap,pubKeyMap);
        inputList3.testCase("Inputlist 3",utxoList,txIdNameMap,pubKeyMap);		

	OutputList outputList4 = new OutputList(addressA,10,addressB,10);
	Input input4a = new Input("tx1",2,outputList4,utxoList,txIdNameMap,keyMap);
	Input input4b = new Input("tx2",2,outputList4,utxoList,txIdNameMap,keyMap);
	InputList inputList4 = new InputList(input4a,input4b);

	inputList4.testCase("Inputlist 4",utxoList,txIdNameMap,pubKeyMap);		

	InputList inputList5 = new InputList(txId1,0,pubKeyA,signedMessage1,
					     txId2,2,pubKeyB,signedMessage2);
	inputList5.testCase("Test Alice 10 and Bob  20",utxoList,txIdNameMap,pubKeyMap);		
	
	
    }

    /** 
     * main function running test cases
     */            

    public static void main(String[] args)
	throws SignatureException, InvalidKeyException, HashMapLookupException,UTXOOutputAlreadyExistsException {
 	InputList.test();
    }    
};    
