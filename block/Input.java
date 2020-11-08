import java.security.PublicKey;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.util.Arrays;


/** Input
     *  specifies one input to a transction
     %  given by a public key, and the amount to be transferred 
     *    and a signature
     */


public class Input{

    /** The amount to be transfered  */
    private TxOutput txOutput;

    /** public key of sender; 
	needed since the output contains only the double hashed public key, not the public key 
    */
    
    private PublicKey publicKey;

    /** The signature signed by the private key corresponding to the public key  */

    private byte[] signature;
    

    /** 
     * Create input from txOutput, publicKey, and signature; 
     */

    public Input(TxOutput txOutput,PublicKey publicKey, byte[] signature){
	this.txOutput  = new TxOutput(txOutput);
	this.publicKey = publicKey;	
	this.signature = Arrays.copyOf(signature,signature.length);
    }

    /** 
        As before but instead of txOutput provide its two components, txId and outputNumber 
    */

    public Input(TxId txId, int outputNumber,PublicKey publicKey, byte[] signature){
	this.txOutput  = new TxOutput(txId,outputNumber);
	this.publicKey = publicKey;	
	this.signature = Arrays.copyOf(signature,signature.length);
    }
    


    /** 
     * If we have a PublicPrivateKeyMap covering the sender
     * and an outputList
     *
     * then we can compute the signature by signing the transaction to be signed consisting
     *    of the public key and input amount and the output list
     *  using the private key of the user
     */    

    public Input(String txIdName, int outputNumber, OutputList outputList,
		 UTXOlist utxoList,TxIdNameMap txIdNameMap, PublicPrivateKeyMap keyMap)
    	throws SignatureException, InvalidKeyException, HashMapLookupException { 
	TxId txId = txIdNameMap.name2TxId(txIdName);
	TxOutput txOutput = new TxOutput(txId, outputNumber);
	Address senderAddress = utxoList.utxo2Address(txOutput);
	PublicKey senderPublicKey = keyMap.getPublicKey(senderAddress);
	byte[] signatureTmp = outputList.getSignature(txOutput,senderPublicKey,keyMap);
	this.txOutput = txOutput;
	this.publicKey= senderPublicKey;
	this.signature =  Arrays.copyOf(signatureTmp,signatureTmp.length);
    }


    /* 
         Variant of the above where we don't lookup the txId in txIdNameMap,
         but provide it directly.

         This allows to create transaction inputs with incorrect transaction ids
          and can be used for testing check funtions.
    */

    
    public Input(TxId txId, int outputNumber, OutputList outputList,
		 UTXOlist utxoList,TxIdNameMap txIdNameMap, PublicPrivateKeyMap keyMap)
    	throws SignatureException, InvalidKeyException, HashMapLookupException { 
	TxOutput txOutput = new TxOutput(txId, outputNumber);
	Address senderAddress = utxoList.utxo2Address(txOutput);
	PublicKey senderPublicKey = keyMap.getPublicKey(senderAddress);
	byte[] signatureTmp = outputList.getSignature(txOutput,senderPublicKey,keyMap);
	this.txOutput = txOutput;
	this.publicKey= senderPublicKey;
	this.signature =  Arrays.copyOf(signatureTmp,signatureTmp.length);
    }


    /* 
         As before but using txLedger, from which one obtains utxoList and txIdNameMap

    */

    

    public Input(TxId txId, int outputNumber, OutputList outputList,
		 TxLedger txLedger, PublicPrivateKeyMap keyMap)
    	throws SignatureException, InvalidKeyException, HashMapLookupException { 
	TxOutput txOutput = new TxOutput(txId, outputNumber);
	Address senderAddress = txLedger.utxoList().utxo2Address(txOutput);
	PublicKey senderPublicKey = keyMap.getPublicKey(senderAddress);
	byte[] signatureTmp = outputList.getSignature(txOutput,senderPublicKey,keyMap);
	this.txOutput = txOutput;
	this.publicKey= senderPublicKey;
	this.signature =  Arrays.copyOf(signatureTmp,signatureTmp.length);
    }

    /* 
         Variant of the above where we provide the senders public key directly
         and don't look it up in txLedger
 
         Used in order to test check functions.
         
    */


    public Input(TxId txId, int outputNumber, OutputList outputList,
		 PublicKey senderPublicKey, PublicPrivateKeyMap keyMap)
    	throws SignatureException, InvalidKeyException, HashMapLookupException { 
	TxOutput txOutput = new TxOutput(txId, outputNumber);
	byte[] signatureTmp = outputList.getSignature(txOutput,senderPublicKey,keyMap);
	this.txOutput = txOutput;
	this.publicKey= senderPublicKey;
	this.signature =  Arrays.copyOf(signatureTmp,signatureTmp.length);
    }
    
    
    
    /*
        Returns the field txOutput
    */
    


    public TxOutput txOutput(){
	return txOutput;
    }

    /*
        Returns the txId contained in the field txOutput
    */
    

    public TxId txId(){
	return txOutput().txId();
    }


    /*
        Returns the outputNumber contained in the txOutput component
    */
    
    public int outputNumber(){
	return txOutput().outputNumber();
    }

    /*
        looks up in txIdNameMap the name corresponding to the txId in the input
    */    

    
    public String txIdName(TxIdNameMap txIdNameMap)
	throws HashMapLookupException { 	
	return txId().toString(txIdNameMap);
    }

    /*
        returns the txId and computes its Base58 representation
    */    
    

    public String txIdBase58(){
	return txId().toStringBase58();
    }
    
    /* 
       returns the public Key field
    */
    

    public PublicKey publicKey(){
	return publicKey;
    };

    /* returns the underlying signature, an element
       of byte[]

    */

    public byte[] signature(){
	return signature;
    };

    

    
    /** 
     * Get the sender's address from the utxoList 
       Note the input only has an unspent transaction output
       By looking at it in utxoList one obtains the sender 
     */

    public Address senderAddress(UTXOlist utxoList)
    throws HashMapLookupException {
	return txOutput().senderAddress(utxoList);
    };



    /* 
       get the Address of the sender  in Base58
       by looking it up in utxoList
    */

    public String senderAddressBase58(UTXOlist utxoList)
	throws HashMapLookupException { 
	return senderAddress(utxoList).toStringBase58();
    };

    /* get the name of the sender 
       by looking it up first in utxoList, then in  a keys to obtain the name
    */

    public String senderName(UTXOlist utxoList, PublicKeyMap keys)
	throws HashMapLookupException { 
	return keys.getUser(senderAddress(utxoList));
    };
    
    /** 
     * Get the  amount which one obtains by looking it up in the utxoList
     */    

    public int amount(UTXOlist utxoList)
    throws HashMapLookupException {
	return utxoList.utxo2Amount(txOutput());
    }

    /*
      as before but using a txLedger, which has utxoList as a component
      
     */

    public int amount(TxLedger txLedger)
    throws HashMapLookupException {
	return txLedger.utxoList().utxo2Amount(txOutput());
    }

    

    /** 
     * Create string of the entry in the form  <word1>transactionId<word2>outputnumber<word3>
     *    addresses are encoded in Base58
     */
    
    public String toStringBase58(String word1, String word2, String word3)
    { 
	return  txOutput().toStringBase58(word1,word2,word3);
    }


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
	throws HashMapLookupException { 
	return  txOutput.toString(word1,word2,word3,word4,utxoList,txIdNameMap,pubKeyMap) +
	        amount(utxoList) + word5;
	
    }


    /* 
       as before but using argument txLedger which has as components
       utxoList and txIdNameMap
    */
    

    public String toString(String word1, String word2, String word3, String word4, String word5,
			   TxLedger txLedger, PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 
	return  txOutput.toString(word1,word2,word3,word4,txLedger,pubKeyMap) +
	    amount(txLedger) + word5;
	
    }
    
    

    /* 

       as toString(word1,word2,word3,word4,word5,utxoList, txIdNameMap, pubKeyMap)
       but using default formatting
    */
    
    public String toString(UTXOlist utxoList, TxIdNameMap txIdNameMap, PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 
	return  toString("(input=","[","],sender=",",amount=",")",utxoList,txIdNameMap,pubKeyMap);	
    }

    /*
       as before but using txLedger from which one obtains utxoList and txIdNameMap
    */

    public String toString(TxLedger txLedger, PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 
	return  toString("(input=","[","],sender=",",amount=",")",txLedger,pubKeyMap);	
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

    /* as printBase58(word1,word2,word3)
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
       as before but using txLedger from which one obtains utxoList and txIdNameMap
    */
    
    public void print(String word1, String word2, String word3, String word4, String word5,
		      TxLedger txLedger, PublicKeyMap pubKeyMap)
	throws HashMapLookupException {
	System.out.println(toString(word1,word2,word3,word4,word5,txLedger,pubKeyMap));
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
       as before but using txLedger from which one obtains utxoList and txIdNameMap
    */
    

    public void print(TxLedger txLedger, TxIdNameMap txIdNameMap, PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 
	System.out.println(toString(txLedger,pubKeyMap));
    }
    
    
    
    /** 
     * Test cases
     */            

    public static void test()
	throws SignatureException, InvalidKeyException, HashMapLookupException, UTXOOutputAlreadyExistsException {  
	PublicPrivateKeyMap keyMap = SampleKeyMap.generate(new String[]{ "Alice", "Bob", "Carol", "David"});
	PublicKeyMap pubKeyMap = keyMap.toPublicKeyMap();
	byte[] sampleMessage1 = KeyUtils.integer2ByteArray(1);
	byte[] sampleMessage2 = KeyUtils.integer2ByteArray(2);
	byte[] sampleMessage3 = KeyUtils.integer2ByteArray(3);
	byte[] signedMessage1 = keyMap.signMessage(sampleMessage1,"Alice");
	byte[] signedMessage2 = keyMap.signMessage(sampleMessage2,"Bob");	
	System.out.println();
	PublicKey pubKeyA =	pubKeyMap.getPublicKey("Alice");
	PublicKey pubKeyB =	pubKeyMap.getPublicKey("Bob");
	PublicKey pubKeyC =	pubKeyMap.getPublicKey("Carol");	
	Address addressA =	pubKeyMap.getAddress("Alice");
	Address addressB =	pubKeyMap.getAddress("Bob");
	Address addressC =	pubKeyMap.getAddress("Carol");		
	TxId txId1  = new TxId(sampleMessage1);
	TxId txId2  = new TxId(sampleMessage2);
	TxId txId3  = new TxId(sampleMessage3);	
	Input input1 = new Input(txId1,0,pubKeyA,signedMessage1);
	Input input2 = new Input(txId2,2,pubKeyB,signedMessage2);
	System.out.print("input1 in Base 58:");
	input1.printBase58();
	System.out.print("input2 in Base 58:");
	input2.printBase58();
	UTXOlist utxoList = new UTXOlist();
	utxoList.add(txId1,0,addressA,10);
	utxoList.add(txId1,2,addressB,10);
	utxoList.add(txId2,2,addressC,5);
	TxIdNameMap txIdNameMap = new TxIdNameMap();
	txIdNameMap.add(txId1,"tx1");
	txIdNameMap.add(txId2,"tx2");
	txIdNameMap.add(txId3,"tx3");
	input1.print(utxoList,txIdNameMap,pubKeyMap);
	input2.print(utxoList,txIdNameMap,pubKeyMap);
	OutputList outputList3 = new OutputList(addressA,10,addressB,10);	
	Input input3 = new Input("tx1",2,outputList3,utxoList,txIdNameMap,keyMap);
	input3.print(utxoList,txIdNameMap,pubKeyMap);
    };

    /** 
     * main function running test cases
     */            

    public static void main(String[] args)
	throws SignatureException, InvalidKeyException, HashMapLookupException,UTXOOutputAlreadyExistsException {
	Input.test();
    }        
    
}
    

    
