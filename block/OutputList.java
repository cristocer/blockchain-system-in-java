import java.security.PublicKey;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.util.ArrayList;

/** OutputList
 *  defines a list of Outputs of a transaction
 *  
 */

public class OutputList{


    /** 
      * list of outputs for a transaction
      */
    
    private ArrayList<Output> outputList;


    /*
        add an entry givne directly by an element of Output
     */

    public void addEntry(Output output){
	outputList.add(output);
    }
    

    /** 
      * add an entry using the components sender and amount 
      */
    
    public void addEntry(Address sender,int amount){
	outputList.add(new Output(sender,amount));
    }

    

    /** 
      * constructor constructing the empty user aount list
      */
    
    public OutputList(){
	outputList =  new ArrayList<Output>();
    }

    /** 
      * constructor constructing a list containing one entry
          consisting of a user and an amount
      */    
    
    public OutputList(Address sender,int amount){
	outputList = new ArrayList<Output>();
	addEntry(sender,amount);
    }

    /* 
        As before but sender given by the name, address is looked up in keymap
     */

    public OutputList(String recipient,int amount,PublicKeyMap keymap)
	throws HashMapLookupException { 	
	outputList = new ArrayList<Output>();
	addEntry(new Output(recipient,amount,keymap));
    }

    /* 
         as before but using a publicPrivateKeyMap from which one obtains the
         underlying publicKeyMap
    */
    
    public OutputList(String recipient,int amount,PublicPrivateKeyMap keymap)
	throws HashMapLookupException { 
	outputList = new ArrayList<Output>();
	addEntry(new Output(recipient,amount,keymap));
    }

    /** 
      * constructor constructing a list containing two entries
          each consisting of a user and an amount
      */        

    public OutputList(Address sender1,int amount1,
		     Address sender2,int amount2){
	outputList = new ArrayList<Output>();
	addEntry(sender1,amount1);
	addEntry(sender2,amount2);
    }

    /* 
        As before but senders given by the names, addresses are looked up in keymap
     */

    public OutputList(String recipient1,int amount1,
		      String recipient2,int amount2,
		      PublicKeyMap keymap)
	throws HashMapLookupException { 	
	outputList = new ArrayList<Output>();
	addEntry(new Output(recipient1,amount1,keymap));
	addEntry(new Output(recipient2,amount2,keymap));	
    }


    /* 
         as before but using a publicPrivateKeyMap from which one obtains the
         underlying publicKeyMap
    */
    

    public OutputList(String recipient1,int amount1,
		      String recipient2,int amount2,
		      PublicPrivateKeyMap keymap)
	throws HashMapLookupException { 	
	outputList = new ArrayList<Output>();
	addEntry(new Output(recipient1,amount1,keymap));
	addEntry(new Output(recipient2,amount2,keymap));	
    }
    
	

    /** 
      * obtain the underlying list
      */
    
    public ArrayList<Output> outputList(){
	return(outputList);
    };

    /* 
      obtain an output by the index
    */
    
    public Output getOutput(int index)
	 throws ArrayListIndexOutOfRangeException
    {
	ArrayList<Output> theList = this.outputList();
	if (theList.size() < index) {
	    throw new ArrayListIndexOutOfRangeException("Aray list doesn't contain index " + index);
	};
	return theList.get(index);
    };


    /** 
      * compute the sum of entries in the list
      */

    public int toSum(){
	int result = 0;
	for (Output  entry : outputList()){
	    result += entry.getAmount();
		};
	return result;
    };


    /* 
      returns a string representing the Output using base58 encoding of the recipient's address  
       where each output is formatted    <word1>address<word2>amount<word3>
    */

    public String toStringBase58(String word1, String word2, String word3)
	throws HashMapLookupException
    {
	String result = "[";
	boolean notFirstEntry = false;
	for (Output output : outputList()){
	    if (notFirstEntry)
		result += ",";
	    notFirstEntry = true;
	    result += output.toStringBase58(word1,word2,word3);
	};
	return result + "]";
    };


    /* 
         returns a string representing the Output using base58 encoding using default formatting 
    */    
    
    public String toStringBase58()
	throws HashMapLookupException
    {
	return toStringBase58("(recipient=\"","\",amount=",")");
    };


    /* 
       returns a string representing the Output using pubKeyMap to look up the recipient 
       in format <word1>recipient<word2>amount<word3>
    */

    
    public String toString(String word1, String word2, String word3, PublicKeyMap pubKeyMap)
	throws HashMapLookupException  		
    {
	String result = "[";
	boolean notFirstEntry = false;	
	for (Output output : outputList()){
	    if (notFirstEntry)
		result += ",";
	    notFirstEntry = true;	    
	    result += output.toString(word1,word2,word3,pubKeyMap);
	};
	return result + "]";
    };

    /* 
       returns a string representing the Output using pubKeyMap to look up the recipient 
       in default formatting
    */

    
    public String toString(PublicKeyMap pubKeyMap)
    	throws HashMapLookupException
    {
	return toString("(Recipient=\"","\",value=",")",pubKeyMap);
    }


    /** 
     * Print the entry in the form word1  <user> word2 <amount> <word3>
         with recipient given in Base58
     */
    
    public void printBase58(String word1, String word2,String word3)
	throws HashMapLookupException
    { 	
	System.out.println(toStringBase58(word1,word2,word3));
    }

    /** 
        As printBase58(String word1, String word2,String word3)
         but using default formatting
     */        


    public void printBase58()
	throws HashMapLookupException
    { 	
	System.out.println(toStringBase58());
    }
    
    /**   method  to print all items in the User Mmaount Listo
     *    in the form 
     *      word1  <user> word2 <amount>  word3
     %
     *   we use the pubKeyMap in order to look up the names for each public key
     */

 
    
    public void print(String word1, String word2,String word3, PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 	
	System.out.println(toString(word1,word2,word3, pubKeyMap));
    }

    /** 
        As print(String word1, String word2,String word3, PublicKeyMap pubKeyMap)
         but using default formatting
     */        


    public void print(PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 	
	System.out.println(toString(pubKeyMap));
    }

    



    /** 
     * Creates the message to be signed, if the outpupt is the current OutputList
     *    and the sender and amount are the inputs
     *
     *  see the lecture where we discussed that the user signs his input and all outputs
     *
     */
    
    public byte[] getMessageToSign(TxOutput txOutput,PublicKey pbk)
	throws HashMapLookupException { 	
	SigData sigData = new SigData();
	sigData.addTxId(txOutput.txId());
	sigData.addInteger(txOutput.outputNumber());
	sigData.addPublicKey(pbk);
        for (Output output : outputList()) {
	    sigData.addAddress(output.getRecipient());
	    sigData.addInteger(output.getAmount());
        }
	return sigData.toArray();
    }


    /* 
       If we have PublicPrivateKeyMap containing the sender,
         we can create a signature for the message consisting of the output and
         input given by sender and amount 
    */


    public byte[] getSignature(TxOutput txOutput,PublicKey pbk,PublicPrivateKeyMap keyMap)
	throws SignatureException, InvalidKeyException, HashMapLookupException {
	return keyMap.signMessage(getMessageToSign(txOutput,pbk),keyMap.getUser(pbk));
    }


    /* 
       we can check that a signature is correct for input given by pubkeySender and amount]
       and the current output list 
    */

	
    public boolean checkSignature(TxOutput txOutput,PublicKey pbk,byte[] signature)
	throws HashMapLookupException { 
	return Crypto.verifySignature(pbk,getMessageToSign(txOutput,pbk),
				      signature);
    }
    
    
    /** 
     * Generic Test cases, providing a headline
     *    printing out the user amount list
     *    and printing out the sum of amounts in the user amount list.
     */            
    

    public void testCase(String header,PublicKeyMap pubKeyMap)
    	throws HashMapLookupException { 
	System.out.println(header);
	print(pubKeyMap);
	printBase58();	
	System.out.println("Sum of Amounts = " + toSum());	
	System.out.println();	
    };

    /** 
     * Test cases
     */            
    
    public static void test()
	throws SignatureException, InvalidKeyException,	HashMapLookupException { 
	PublicPrivateKeyMap keyMap = SampleKeyMap.generate(new String[]{ "Alice", "Bob", "Carol", "David"});
	PublicKeyMap pubKeyMap = keyMap.toPublicKeyMap();
	PublicKey pubKeyA =	pubKeyMap.getPublicKey("Alice");
	PublicKey pubKeyB =	pubKeyMap.getPublicKey("Bob");
	Address addressAlice = new Address(keyMap.getPublicKey("Alice"));
	Address addressBob = new Address(keyMap.getPublicKey("Bob"));
	OutputList outputList1 = new OutputList(addressAlice,10);
	OutputList outputList2 = new OutputList(addressBob,20);
	OutputList outputList3 = new OutputList(addressAlice,10,addressAlice,10);
	outputList1.testCase("Test Alice 10",pubKeyMap);

	outputList2.testCase("Test Bob 20",pubKeyMap);
	
	outputList3.testCase("Alice twice 10",pubKeyMap);

	OutputList outputList4 = new OutputList(addressAlice,10,
						addressBob,20);
	outputList4.testCase("Test Alice 10 and Bob  20",pubKeyMap);
	
	System.out.println("Same List but with words User and spends");	
	outputList4.print("(User "," spends ",")",pubKeyMap);		
	
    }
    

    /** 
     * main function running test cases
     */            
    
    public static void main(String[] args)
	throws SignatureException, InvalidKeyException,HashMapLookupException
    {  
	OutputList.test();
    }    

};    
