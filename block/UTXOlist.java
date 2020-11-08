import java.util.Hashtable;
import java.util.HashSet;
import java.util.ArrayList;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.InvalidKeyException;


/* 
   class representing a list of unspent transaction outputs
     which is what corresponds in the transaction model to the ledger in the ledger model

   It maintains the  list of transaction outputs, together with operations determining
   for each transaction output the address it was sent (utxo2Address) to and the amount  (utxo2Amount).
   
   In additoin it maintains a map which determines for addresses a set of  unspent transaction outputs towards this 
     address.

   
   The list of transaction  output is actually the domain of the maps utxo2Address and utxo2Amount,
   so it is given indirectly as the keySets for these two maps.

*/

class UTXOlist {

    /* 
       This flag toggles whether to print certain keys/addresses
       as well in Base58 and not only as converted strings
     
       Needs to be changed by changing the code
    */
    
    private static boolean PRINTBASE58 = false;

    /* 


       hash tables mapping unspent transaction outputs to address and the amount
       and  as well determing for addresses  the set of unspent transaction outputs towards that user.
       
       The actual list of unspent transaction outputs is the keyset of utxo2Address and of utxo2Amount
       both maps should have the same keyset.
       The list of unspent transaction outputs is determined indirectly as this keyset.

    */
    
    
    private Hashtable<TxOutput,Address>  utxo2Address;

    private Hashtable<TxOutput,Integer>  utxo2Amount;

    private Hashtable<Address,HashSet<TxOutput>>  address2UTXO;

    /* 
       when DEBUG is set to true some additional debug messages are printed on the screen
       toggling DEBUG needs to be done by changing the code, there are currently no methods in place for toggling it.
    */

    
    private boolean DEBUG = false;


    /* creates an empty UTXO */
    
    public UTXOlist(){
	utxo2Address = new Hashtable<TxOutput,Address>();
	utxo2Amount =  new Hashtable<TxOutput,Integer>();
	address2UTXO = new Hashtable<Address,HashSet<TxOutput>>();
    };

    /* add an entry giving by its components */

    public void add(TxOutput txOutput,Address recipient,int amount)
     throws UTXOOutputAlreadyExistsException{
        if (utxo2Address.containsKey(txOutput) ||
	    utxo2Amount.containsKey(txOutput)){
	    throw new UTXOOutputAlreadyExistsException("TxOutput " + txOutput.toString() + " already exists");
	};
	utxo2Address.put(txOutput,recipient);
	utxo2Amount.put(txOutput,amount); 
	if (!address2UTXO.containsKey(recipient)){
	    address2UTXO.put(recipient,new HashSet<TxOutput>());
	};
	address2UTXO.get(recipient).add(txOutput);
	if (DEBUG)
	    {
		System.out.println("Adding entry ro recipient " + recipient.toStringBase58());
		System.out.println("address2UTXO.get(recipient).contains(txOutput) = " + address2UTXO.get(recipient).contains(txOutput));
	    }
    }

    /*
       As before but txOuptut is given by its two components
     */
    
    public void add(TxId txId, int outputNumber,Address recipient,int amount)
	throws UTXOOutputAlreadyExistsException
    {
	add(new TxOutput(txId,outputNumber),recipient,amount);
    }
    
    /* 
       remove an item  from the utxoList
     */
    public void remove(TxOutput txOutput)
	throws HashMapLookupException, UTXOOutputDoesntExistsException
    {
	Address recipientOld = utxo2Address(txOutput);
	if (DEBUG) {
	    System.out.println("Removing txOutput = " + txOutput.toString());
	    System.out.println("recipients address = " + recipientOld.toStringBase58() );
	};
	if (!utxo2Address.containsKey(txOutput)){
	    throw new UTXOOutputDoesntExistsException ("txoutput " + txOutput.toString() + " not in utxo2Address");
	};
	utxo2Address.remove(txOutput);
	if (!utxo2Amount.containsKey(txOutput)){
	    throw new UTXOOutputDoesntExistsException ("txoutput " + txOutput.toString() + " not in utxo2Amount");
	};
	utxo2Amount.remove(txOutput);
	if (!address2UTXO.containsKey(recipientOld)){
	    address2UTXO.put(recipientOld,new HashSet<TxOutput>());};
	HashSet<TxOutput> txOutputs = address2UTXO.get(recipientOld);
	if (txOutputs == null){
	    throw new UTXOOutputDoesntExistsException("UTXO doesn't contain transctions for address \"" +
						      recipientOld.toStringBase58() + "\"");
	};
	if (!txOutputs.contains(txOutput)){
		throw new UTXOOutputDoesntExistsException ("txoutput for address \"" + recipientOld.toStringBase58()
							  + "\" doesnt contain txOuutput \""+ txOutput.toString() + "\"");
	    };
	txOutputs.remove(recipientOld);
	if (txOutputs.isEmpty()){
	    address2UTXO.remove(txOutput);
	};
    }


    /*
       As before but txOuptut is given by its two components
     */

    public void remove(TxId txId, int outputNumber)
	throws HashMapLookupException, UTXOOutputDoesntExistsException {	
	remove(new TxOutput(txId,outputNumber));
    }


    /*
      check whether txOutput is an entry in the utxoList
    */

    public boolean hasEntry(TxOutput txOutput){
	return utxo2Address.containsKey(txOutput) && utxo2Amount.containsKey(txOutput);
    };

    /*
       As before but txOuptut is given by its two components
     */
    
    public boolean hasEntry(TxId txId, int outputNumber){
	return hasEntry(new TxOutput(txId,outputNumber));
    };    
       
		
    /* 
       lookup the underlying address
     */
    
    public Address utxo2Address(TxOutput txOutput)
    throws HashMapLookupException {
	if (!utxo2Address.containsKey(txOutput)){
	    throw new HashMapLookupException ("txOutput  \"" + txOutput.toString() +  "\" not in UTXO");
	};
	return utxo2Address.get(txOutput);
    }

    /*
      As before but txOuptut is given by its two components
    */

    public Address utxo2Address(TxId txId, int outputNumber){
	return utxo2Address.get(new TxOutput(txId,outputNumber));
    }
    

    /* 
       look up the amount in  a transaction output
     */
    
    public int utxo2Amount(TxOutput txOutput)
    throws HashMapLookupException {	
	if (!utxo2Amount.containsKey(txOutput)){
	    throw new HashMapLookupException ("txOutput  \"" + txOutput.toString() +   "\" not in UTXO");
	};
	return utxo2Amount.get(txOutput);
    }

    /*
      As before but txOuptut is given by its two components
    */
    

    public int utxo2Amount(TxId txId, int outputNumber)
    throws HashMapLookupException {		
	return utxo2Amount(new TxOutput(txId,outputNumber));
    }    


    /* returns the underlying function 
       address2UTXO
    */
    
    public HashSet<TxOutput> address2UTXO(Address address){
	if (address2UTXO.containsKey(address)){
	    return address2UTXO.get(address);
	} else{
	    return new HashSet<TxOutput>();
	}
    }


    /* Processes a  transaction by removing the inputs from the utxoList
       and add for each output one entry
    */
    
    public void processTransaction(Transaction transact)
	throws HashMapLookupException, UTXOOutputAlreadyExistsException, UTXOOutputDoesntExistsException { 	
	ArrayList<Input> inputs = transact.inputList().inputList();
	for (Input entry :  inputs){
	    if (DEBUG)
		    System.out.println("Removing + txOutput = " + entry.toStringBase58() +  ", publicKey = " +
			       KeyUtils.publicKeyToString(entry.publicKey()));
	    remove(entry.txOutput());
	    
	}
	ArrayList<Output> outputs = transact.outputList().outputList();
	TxId txId = transact.txId();
	for (int i = 0; i < outputs.size(); i++){
	    if (DEBUG)
		System.out.println("Adding  + txId = " + txId.toStringBase58() + ", index = " + i + ", recipient = "  +
			       outputs.get(i).getRecipient().toStringBase58() + ", amount = " +
			       outputs.get(i).getAmount());
	    add(txId,i,outputs.get(i).getRecipient(),outputs.get(i).getAmount());
	};

	}
	   


    /** 
     * create String representation using Base58 encoding
     *
     */
    
    public String toStringBase58()
	throws HashMapLookupException
    {
	String result = "[";
	boolean notFirstEntry = false;	
	for (TxOutput txOutput : utxo2Address.keySet()){
	    if (notFirstEntry)
		result += ",\n";
	    notFirstEntry = true;	    
	    result += txOutput.toStringBase58("(txoutput=","[","],address=") + utxo2Address(txOutput).toStringBase58()
		+ ",amount=" + utxo2Amount(txOutput) + ")";
	};
	return result + "]";
    };	


    /** 
     * Create string of the entry. 
     *
     *  the sendername is looked up using pubKeyMap, the transactionName using txIdNameMap and the sender amount is looked upt
        using utxoList
     */

    public String toString(TxIdNameMap txIdNameMap, PublicKeyMap pubKeyMap)
	
	throws HashMapLookupException
    {
	String result = "[";
	boolean notFirstEntry = false;	
	for (TxOutput txOutput : utxo2Address.keySet()){
	    if (notFirstEntry)
		result += ",\n";
	    notFirstEntry = true;	    
	    result += txOutput.toString("(txoutput=","[","],recipient=",",amount=",this,txIdNameMap,pubKeyMap)
		+ "" + utxo2Amount(txOutput) + ")";
	};
	return result + "]";
    };	
    
    
    /** 
     * print item using Base58 encoding
     *
     */
    
    public void printBase58()
	throws HashMapLookupException	
    {
	System.out.println(toStringBase58());
    };


    /* print item
       by looking up the names in txIdNameMap and pubKeyMap
    */
    
    public void print(TxIdNameMap txIdNameMap, PublicKeyMap pubKeyMap)
	
	throws HashMapLookupException
    {
	System.out.println(toString(txIdNameMap,pubKeyMap));
    };
    

    /** 
     * Generic Test cases, providing a headline
     *    printing out the transaction
     *    and printing out whether it is valid
     */

    
    public void testCase(String header,Transaction tr, TxIdNameMap txIdNameMap, PublicKeyMap pubKeyMap)
	throws HashMapLookupException, UTXOOutputAlreadyExistsException,UTXOOutputDoesntExistsException { 
	System.out.println(header);
	if (PRINTBASE58)
	    printBase58();
	tr.print(this,txIdNameMap,pubKeyMap);
	processTransaction(tr);
	txIdNameMap.add(tr);
	if (PRINTBASE58)
	    {
		System.out.println("utxolist(Base58)=" + toStringBase58());
		System.out.println();
	    };
	System.out.println("After processesing utxolist=\n" + toString(txIdNameMap,pubKeyMap));
	System.out.println();
	
    }
    

    
    /** 
     * Test cases
     */            

    public static void test()
	throws SignatureException,InvalidKeyException, HashMapLookupException, UTXOOutputAlreadyExistsException,
	       UTXOOutputDoesntExistsException, ArrayListIndexOutOfRangeException
    { 
	PublicPrivateKeyMap keyMap = SampleKeyMap.generate(new String[]{ "Alice", "Bob", "Carol", "David"});
	PublicKeyMap pubKeyMap = keyMap.toPublicKeyMap();		

	UTXOlist utxoList = new UTXOlist();
	TxIdNameMap txIdNameMap = new TxIdNameMap();

	/*
	  Space for creating test cases similar to the ones in TxLedger

	 */
	

        
    }


    /** 
     * main function running test cases
     */            
    
    public static void main(String[] args)
	throws SignatureException,InvalidKeyException, HashMapLookupException, UTXOOutputAlreadyExistsException,
	       UTXOOutputDoesntExistsException, ArrayListIndexOutOfRangeException
    { 
	test();
    }    
    

}
