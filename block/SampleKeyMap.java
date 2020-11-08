import java.security.KeyPair;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import LabUtils.CryptoUtils;


    /* generates a random publicPrivateKeyMap  for a given list of names */

public class SampleKeyMap {


    /* 
       when DEBUG is set to true some additional debug messages are printed on the screen
       toggling DEBUG needs to be done by changing the code
    */

   private static boolean DEBUG =  false;

    /* method generating a publicPrivteKeyMap for a list of user names
     */
    
   public static PublicPrivateKeyMap generate(String[] names) {
	PublicPrivateKeyMap keymap = new  PublicPrivateKeyMap();
        byte[] initialKey = new byte[32];
	for (int i = 0; i < initialKey.length; i++){
            initialKey[i] = (byte)i;
        }
        SecureRandom prg = new SecureRandom(initialKey);
        int numSizeBits = 2048;
	for (String user : names){	
            byte[] key = new byte[32];
            prg.nextBytes(key);
	    if (DEBUG) 
		System.out.println("Generating key pair for user " + user);
            KeyPair rp = CryptoUtils.generateKeyPair(numSizeBits);
	    if (DEBUG)
		System.out.println("Public Key Base 58 = " + KeyUtils.publicKey2AddressBase58(rp.getPublic()));
	    keymap.addKey(user,rp.getPrivate(),rp.getPublic());
        }
	return keymap;
    }


    /** 
     * Test cases
     */            


    public static void test()
	throws HashMapLookupException { 			
	String[] names = new String[]{ "Alice", "Bob", "Carol", "David"};
	PublicPrivateKeyMap keys = generate(names);
	Set<String> users = keys.getUsers();
	for (String user  : users){
	    System.out.println("User = " + user + " public key = " + keys.getPublicKeyStringBase58(user));
	}
    }

    /** 
     * main function running test cases
     */            

    public static void main(String[] args)
	throws HashMapLookupException { 			
	test();
    }        


    
}
