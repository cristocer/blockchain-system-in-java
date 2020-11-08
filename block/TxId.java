import java.security.PublicKey;
import java.util.Arrays;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import core.Base58;

/* 
   TxId is a class of transaction ids which are given as
   array of bytes 
*/


public class TxId{

    /* a TxId is represented by an array of bytes */

    private byte[] txId;

    /* creates a TxId from a byte array */
    
    public TxId(byte[] txId){
	this.txId = Arrays.copyOf(txId,txId.length);
    }

    /* copy constructor */
    
    public TxId(TxId txId){
	this.txId = Arrays.copyOf(txId.txId(),txId.txId().length);	
    }    


    /* creates a TxId from serialised transaction data given as an element of SigData 
       by applying twice SHA256 
    */

    public TxId(SigData rawTxData){
	txId = rawTxData.toDoubleSHA256Hash();
    }


    /* returns the underlying byte array */
    
    public byte[] txId(){
	return txId;
    }
    
    
    /* creates a hashCode so that TxId could be used in a HashMap 
     */

    
    public int hashCode(){
	return java.util.Arrays.hashCode(txId());
    };

    /* equals function so tht TxId could be used in a Collections such as HashMap

       The following code is based on
        https://stackoverflow.com/questions/15576009/how-to-make-hashmap-work-with-arrays-as-key  

     */

    public boolean equals(Object o) {
            if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
            TxId txId = (TxId) o;
            return java.util.Arrays.equals(txId(),txId.txId());
        }


    /* returns the TxId in base58 encoding as usedin Bitcoin  
    */
    
    public String toStringBase58(){
	return Base58.encode(txId());
    }



    /* 
       converts a txId into a redable string
       by looking up its name in txIdNameMap
     */
    
    public String toString(TxIdNameMap txIdNameMap)
	throws HashMapLookupException
    {
	return txIdNameMap.txid2Name(this);
    }

    /** 
     * Test cases
     */            

    public static void test()
	throws SignatureException, InvalidKeyException, HashMapLookupException { 
	PublicPrivateKeyMap keyMap = SampleKeyMap.generate(new String[]{ "Alice", "Bob", "Carol", "David"});
	PublicKeyMap pubKeyMap = keyMap.toPublicKeyMap();	
	byte[] sampleMessage1 = KeyUtils.integer2ByteArray(1);
	byte[] sampleMessage2 = KeyUtils.integer2ByteArray(2);
	byte[] sampleMessage3 = KeyUtils.integer2ByteArray(3);
	byte[] signedMessage1 = keyMap.signMessage(sampleMessage1,"Alice");
	byte[] signedMessage2 = keyMap.signMessage(sampleMessage2,"Bob");
	byte[] signedMessage3 = keyMap.signMessage(sampleMessage3,"Carol");
	PublicKey pubKeyA =	pubKeyMap.getPublicKey("Alice");
	PublicKey pubKeyB =	pubKeyMap.getPublicKey("Bob");
	PublicKey pubKeyC =	pubKeyMap.getPublicKey("Carol");
	PublicKey pubKeyD =	pubKeyMap.getPublicKey("David");
	TxId txId1  = new TxId(sampleMessage1);
	TxId txId2  = new TxId(sampleMessage2);
	TxIdNameMap  txIdNameMap = new TxIdNameMap();
	txIdNameMap.add(txId1,"transaction 1");
	txIdNameMap.add(txId2,"transaction 2");	
	System.out.println("txId1 = " + txId1.toStringBase58());
	System.out.println("txId1Name = " + txId1.toString(txIdNameMap));
	System.out.println("txId2 = " + txId2.toStringBase58());
	System.out.println("txId2Name = " + txId2.toString(txIdNameMap));	
	System.out.println("txId1.equals(txId2) = " + txId1.equals(txId2));
	System.out.println("txId1.equals(txId1) = " + txId1.equals(txId1));	
    }


    /** 
     * main function running test cases
     */            

    public static void main(String[] args)
	throws SignatureException, InvalidKeyException, HashMapLookupException {  
	test();
    }

}
