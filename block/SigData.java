import java.security.PublicKey;
import java.util.ArrayList;
import java.security.MessageDigest;


/* Class for defining messages to be signed by private keys */

public class SigData{

    /* 
       when DEBUG is set to true some additional debug messages are printed on the screen
       toggling DEBUG needs to be done by changing the code
    */
    
    private boolean DEBUG = false;

    /* messages are essentially byte arrays */
    
    private ArrayList<Byte> sigData;

    /* the empty message */
    
    public SigData(){
	sigData = new ArrayList<Byte>();
    }

    /* adds a byte array */
    
    public void addByteArray(byte[] bytes){
	for (int i = 0; i < bytes.length;i++)
	    sigData.add(bytes[i]);
    }

    /* 
       adds a transaction id to a message (as part of the input)
    */

    public void addTxId(TxId txid){
	addByteArray(txid.txId());
    }

    /* adds an integer to the current message */
    
    public void addInteger(int number){
	addByteArray(KeyUtils.integer2ByteArray(number));
    }

    /* adds a public key to the current message */    

    public void addPublicKey(PublicKey pubkey){
	if (DEBUG)
	    System.out.println("pubkey=" + KeyUtils.publicKey2ByteArray(pubkey));
	addByteArray(pubkey.getEncoded());
    }

    /* adds an address to the current message */    

    public void addAddress(Address address){
	addByteArray(address.getAddress());
    }


    /* returns the underlying byte array which will then be signed */
    
    public byte[] toArray(){
	byte[] sigD = new byte[sigData.size()];
        int i = 0;
        for (Byte sb : sigData)
	sigD[i++] = sb; 
        return sigD;
    }

    /* hash the current message using SHA256. The result of the hashing is a byte array  */

    public byte[] toSHA256Hash(){
	return KeyUtils.byteArray2SHA256hash(toArray());

    }

    /* txids are obtained by twice applying SHA256 */
    
    public byte[] toDoubleSHA256Hash(){
	return KeyUtils.byteArray2SHA256hash(KeyUtils.byteArray2SHA256hash(toArray()));

    }
}
    
	
