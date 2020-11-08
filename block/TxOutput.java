/**  TxOutput consists of 
        a transaction id   txId
        an outputnumber, indicating one of the outputs from that transaction
*/


class TxOutput{

    /* the transaction id of the transaction providing the output */
    private TxId txId;

    /* the number of the output to be used as output */
    
    private int  outputNumber;

    public TxOutput(TxId txId,int outputNumber){
	this.txId = new TxId(txId);
	this.outputNumber = outputNumber;
    }


    /* copy constructor */
    
    public TxOutput(TxOutput txOutput){
	this.txId = new TxId(txOutput.txId());
	this.outputNumber = txOutput.outputNumber();
    }    

    /* return the transaction id */
    public TxId txId(){
	return txId;
    }

    /* return the outputnumber */    
    public int outputNumber(){
	return outputNumber;
    }


    /* looks up the transaction name in txIdNameMap
     */
    
    public String txIdName(TxIdNameMap txIdNameMap)
	throws HashMapLookupException { 	
	return txId().toString(txIdNameMap);
    }

    /* 
       as before but using txLedger which has as component txIdNameMap
     */
    

    public String txIdName(TxLedger txLedger)
	throws HashMapLookupException { 	
	return txIdName(txLedger.txIdNameMap());
    }

    /* computes the txid using Base58 representation
     */

    public String txIdBase58(){
	return txId().toStringBase58();
    }


    
    /** 
     * Get the sender's address from utxoList 
     */

    public Address senderAddress(UTXOlist utxoList)
    throws HashMapLookupException {
	return utxoList.utxo2Address(this);
    };


    public Address senderAddress(TxLedger txLedger)
    throws HashMapLookupException {
	return senderAddress(txLedger.utxoList());
    };
    


    /* get the Address of the sender  in Base58
    */

    public String senderAddressBase58(UTXOlist utxoList)
	throws HashMapLookupException { 
	return senderAddress(utxoList).toStringBase58();
    };

    /* get the name of the sender 
       by looking it up in  a PublicKeyMap
    */

    public String senderName(UTXOlist utxoList, PublicKeyMap keys)
	throws HashMapLookupException { 
	return keys.getUser(senderAddress(utxoList));
    };
    

    /* 
       as before but using txLedger which has as component txIdNameMap
     */

    public String senderName(TxLedger txLedger, PublicKeyMap keys)
	throws HashMapLookupException { 
	return keys.getUser(senderAddress(txLedger));
    };
    
    
    
    
    /* hashCode() creates a hashCode so that TxOutput could be used in a HashMap 
       we don't expect usually more than 10 output so multiplying it by 10 is a sensible approximation
       note that Hashmap allows nonequal elements to have the same hashcode; in that case it uses
       the equals() function to differentiate between them.
     */

    public int hashCode(){
	return txId().hashCode() * 10 + this.outputNumber();
    }
    

    

    /* equals function so tht TxId could be used in a Collections such as HashMap

       The following code is based on
        https://stackoverflow.com/questions/15576009/how-to-make-hashmap-work-with-arrays-as-key  
     */

    public boolean equals(Object o) {
            if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
            TxOutput txOutput = (TxOutput) o;
            return this.txId().equals(txOutput.txId()) && this.outputNumber == txOutput.outputNumber;
        }

    


    /** 
     * Create string of the entry in the form  <word1>transactionId<word2>outputnumber<word3>
     *
     */
    
    public String toStringBase58(String word1, String word2, String word3)
    { 
	return  (word1 +  txIdBase58() + word2 + outputNumber() + word3);
    }


    /* as toStringBase58(word1,word2,word3)
       but using default formatting
    */
    
    public String toStringBase58()
    { 
	return  toStringBase58("","[","]");
    }
    
    


    /** 
     * Create string of the entry in the form  <word1>transactionName<word2>outputnumber<word3>sendername<word4>
     *
     *  the sendername is looked up using pubKeyMap, the transactionName using txIdNameMap and the sender amount is looked upt
        using utxoList
     */
    
    public String toString(String word1, String word2, String word3, String word4, 
			   UTXOlist utxoList, TxIdNameMap txIdNameMap, PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 
	return  (word1 +  txIdName(txIdNameMap) + word2 + outputNumber() + 
		 word3 +  senderName(utxoList,pubKeyMap) + word4);
	
    }

    /*
      as before but using txLedger as argument from which one obtains
      utxoList and txIdNameMap
    */


    public String toString(String word1, String word2, String word3, String word4, 
			   TxLedger txLedger, PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 
	return  (word1 +  txIdName(txLedger) + word2 + outputNumber() + 
		 word3 +  senderName(txLedger,pubKeyMap) + word4);
	
    }
    
    

    /* 
       as toString(word1,word2,word3,word4,word5,utxoList, txIdNameMap, pubKeyMap)
       but using default formatting
    */
    
    public String toString(UTXOlist utxoList, TxIdNameMap txIdNameMap, PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 
	return  toString("(input=","[","],sender=",")",utxoList,txIdNameMap,pubKeyMap);	
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
    
    public void print(String word1, String word2, String word3, String word4, 
		      UTXOlist utxoList, TxIdNameMap txIdNameMap, PublicKeyMap pubKeyMap)
	throws HashMapLookupException {
	System.out.println(toString(word1,word2,word3,word4,utxoList,txIdNameMap,pubKeyMap));
    }
    

    /* 
       as print(word1,word2,word3,word4,word5,utxoList, txIdNameMap, pubKeyMap)
       but using default formatting
    */
    
    public void print(UTXOlist utxoList, TxIdNameMap txIdNameMap, PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 
	System.out.println(toString(utxoList,txIdNameMap,pubKeyMap));
    }
    
    
    
    


    
    
    
	    
}
