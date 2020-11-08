import java.util.Hashtable;

/*  
   TxIdNameMap
   is a map between transaction ids and readable names
   provided for convenience
*/


public class TxIdNameMap{

    /* the underlying functions between transaction ids and names 
     */

    private Hashtable<TxId, String> txid2Name;
    private Hashtable<String, TxId> name2TxId;

    /* 
       methods returning the underyling components
     */
    
    public Hashtable<TxId, String> txid2Name(){
	return txid2Name;
    }
    
    public Hashtable<String, TxId> name2TxId(){
	return name2TxId;
    }
    
    /* 
       create the empty txid map 
    */
    
    
    public TxIdNameMap(){
	txid2Name = new Hashtable<TxId, String> ();
	name2TxId = new Hashtable<String, TxId>();
    }

    /*
       check whether an entry can be added 
       i.e. both the transaction  id and the name are new
    */

    public boolean validNewEntry(TxId txid, String name){
	return !txid2Name.containsKey(txid) && !name2TxId.containsKey(name);
    }

    /*
      adds an entry given by a transaction id and a name
    */

    public void add (TxId txid, String name){
	txid2Name.put(txid,name);
	name2TxId.put(name,txid);
    }

    /*
      adds an entry given by a transaction
    */    

    public void add(Transaction trans)
	throws HashMapLookupException { 
	add(trans.txId(),trans.transactionName());
    }

    /*
      lookups up the transaction name from its ide 
     */
	 

    public String txid2Name(TxId txid)
	throws HashMapLookupException { 				
	String result = this.txid2Name.get(txid);
	if (result == null)
	    {
		throw new HashMapLookupException ("No name for transaction  \"" + txid.toStringBase58() + "\"");
	    } else {
	    return result;
	}
    }

    /*
      lookups up the name transaction id from its name
     */
    

    public TxId name2TxId(String txIdName)
	throws HashMapLookupException { 				
	TxId result = this.name2TxId.get(txIdName);
	if (result == null)
	    {
		throw new HashMapLookupException ("No transaction with name = \"" + txIdName + "\"");
	    } else {
	    return result;
	}
    }

    /* 
       computes a Base58 representation of txIdName
    */
    

    public String toStringBase58()
	throws HashMapLookupException
    {
	String result = "";
	for (TxId txId : txid2Name().keySet()){
	    result += "(txid:"+txId.toStringBase58()+ "|->" + txid2Name(txId) + "\n";
	};
	for (String txName : name2TxId().keySet()){
	    result += "(name:"+txName + "|->" + name2TxId(txName).toStringBase58() + "\n";
	};
	
	return result;
    }

    /* 
       computes a string version of the map
    */


    public String toString1()
	throws HashMapLookupException
    {
	String result = "";
	for (TxId txId : txid2Name().keySet()){
	    result += "(txid:"+txId.toString(this)+ "|->" + txid2Name(txId) + "\n";
	};
	for (String txName : name2TxId().keySet()){
	    result += "(name:"+txName + "|->" + name2TxId(txName).toString(this) + "\n";
	};
	
	return result;
    }
    
    
}

