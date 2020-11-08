import java.nio.ByteBuffer;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.util.Base64;
import core.Base58;
import io.nayuki.bitcoin.crypto.Ripemd160;



/* this class defines some convenient operations for dealing with keys (public and private keys, byte arrays) */

public class KeyUtils{


    /* this prints out a public key in a readable format using a Base64 encoder */

    public static String publicKeyToString(PublicKey pbk) {
	    return Base64.getEncoder().encodeToString(pbk.getEncoded());
    }


    /* Messages to be signed are formed from the amounts (which are integers)
         and public keys 

      The following operation converts an integer into a array of bytes which can
      be used for creating messages
    */

    
    public static byte[] integer2ByteArray (int i){
	ByteBuffer b = ByteBuffer.allocate(Integer.SIZE / 8); // Temporary memory
	b.putInt(i);
	return b.array();
    }


    /* operation for signing a message with a a private key */
    
    public static byte[] signMessage(byte[] message, PrivateKey privateKey)
	throws SignatureException, InvalidKeyException{
	return Crypto.sign(privateKey, message);
    }


    /* hashes a byte array  using SHA256
       since SHA-256 does exist, we catch the NoSuchAlgorithmException */

    
    public static byte[] byteArray2SHA256hash (byte[] byteArray){
        try {
	    MessageDigest md;
            md = MessageDigest.getInstance("SHA-256");
            md.update(byteArray);
            return md.digest();
        } catch (NoSuchAlgorithmException x) {
            x.printStackTrace(System.err);
	    return null;
        }
    }

    /* the following converts a public key into a bitcoin  address.
       based on the code in 
        https://www.novixys.com/blog/generate-bitcoin-addresses-java/

       Note that we are in this version using RSA public/private keys
        rather than ECDSA keys as used in Bitcoin 
    */
    
    public static byte[] publicKey2Address (byte[] byteArray){
	/* 
           code taken from 
           https://www.novixys.com/blog/generate-bitcoin-addresses-java/ 
 
           but referring to the library at 
           io.nayuki.Bitcoin-Cryptography-Library.Ripemd160
           see reference there.

	*/
	
        try {
	    MessageDigest sha = MessageDigest.getInstance("SHA-256");
	    byte[] s1 = sha.digest(byteArray);  // bcPub.getBytes("UTF-8"));

	    // the standard way for computing RIPEMD160 would be using BouncyCastleProvider
	    // which needs to be installed as a java library.
	    // Then one could use the following code:
	    //
	    // MessageDigest rmd = MessageDigest.getInstance("RipeMD160", "BC");
	    // byte[] r1 = rmd.digest(s1);
	    //
	    // we use instead the library from Nayuki, to avoid having to install that library:
	    //

	    byte[] r1 = Ripemd160.getHash(s1);
	    byte[] r2 = new byte[r1.length + 1];
	    r2[0] = 0;
	    for (int i = 0 ; i < r1.length ; i++) r2[i+1] = r1[i];
	    byte[] s2 = sha.digest(r2);
	    byte[] s3 = sha.digest(s2);
	    int r2Length = r2.length;
	    byte[] a1 = new byte[r2Length + 5];
	    for (int i = 0 ; i < r2Length ; i++) a1[i] = r2[i];
	    for (int i = 0 ; i < 5 ; i++) a1[r2Length + i] = s3[i];
	    return a1;
	} catch (NoSuchAlgorithmException x) {
            x.printStackTrace(System.err);
	    return null;
	}
    }


    /*
       computes the underlying byte[] address of a public Key
     */
    
    public static byte[] publicKey2Address (PublicKey pbk){
	return publicKey2Address(publicKey2ByteArray(pbk));
    }

    /*
       takes a byte array (an element of byte[]) and converts it into a string using
       Base58
     */


    public static String addressByteArray2Base58 (byte[] addressAsByteArray){
	return Base58.encode(addressAsByteArray);
    }


    /*
       as before but taking an element of Address instead of an element of byte []
    */
    
    public static String address2Base58 (Address address){
	return address.toStringBase58();
    }
    
    /* 
        computes Base58 representation of a public key
     */

    public static String publicKey2AddressBase58 (byte[] byteArray){
	return addressByteArray2Base58(publicKey2Address(byteArray));
    };


    /* 
      computes the unerlying byte array  of a public key
    */
    
    public static byte[] publicKey2ByteArray (PublicKey pbk){
	if (pbk == null){
	    return new byte[]{};
	} else{
	return pbk.getEncoded();
	}
    };

    /*
      converts a public Key given by a byte array into Base58 
    */
    

    public static String publicKey2AddressBase58 (PublicKey pbk){
	return publicKey2AddressBase58(publicKey2ByteArray(pbk));
    };


    /*
       Test cases
    */
    

    public static void test()
	throws HashMapLookupException { 			
	String[] names = new String[]{ "Alice", "Bob", "Carol", "David"};
	PublicPrivateKeyMap keys = SampleKeyMap.generate(names);
	System.out.println("Note that our addresses are not defined as bitcoin addresses since we don't want to install BouncyCastle");
	for (String user  : names){
	    
	    System.out.println("User = " + user + " public key = " +
			       publicKey2AddressBase58(keys.getPublicKey(user)));
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
