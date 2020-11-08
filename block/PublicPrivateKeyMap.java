import java.util.HashMap;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.util.Set;

/* this class maintains
    a map from users to private key
    and a map from users to public key 
     the latter given by a publicKeymap
*/


public class PublicPrivateKeyMap {
    private HashMap<String,PrivateKey> user2PrivateKey;
    private PublicKeyMap  publicKeyMap;


    /* construct the empty public private key map
     */
    
    public PublicPrivateKeyMap(){
	this.user2PrivateKey = new HashMap<String,PrivateKey> ();
	this.publicKeyMap = new PublicKeyMap();
    }	


    /* construct a public private key map from the underlying components
     */

    public PublicPrivateKeyMap(HashMap<String,PrivateKey> user2PrivateKey,
			       HashMap<String,PublicKey> user2PublicKey,
			       HashMap<PublicKey,String> publicKey2User,
			       HashMap<Address,PublicKey> address2PublicKey) {       
	this.user2PrivateKey = new HashMap<String,PrivateKey> (user2PrivateKey);
	this.publicKeyMap = new PublicKeyMap(user2PublicKey,publicKey2User,address2PublicKey);
    }

    /* add a key to the public private key
       by providing the user name, public and private key
    */
    

    public void addKey(String user,PrivateKey privateKey, PublicKey publicKey){
	user2PrivateKey.put(user,privateKey);
	publicKeyMap.addKey(user,publicKey);
    }

    /* 
       return the underlying publicKeyMmap
    */

    public PublicKeyMap toPublicKeyMap(){
	return new PublicKeyMap(publicKeyMap);
    }

    /* 
       the following 3 methods retrieve the underlying hashmaps
    */
    
    public HashMap<String,PrivateKey> getUser2PrivateKey(){
	return new HashMap<String,PrivateKey>(user2PrivateKey);
    }

    public HashMap<String,PublicKey> getUser2PublicKey(){
	return publicKeyMap.getUser2PublicKey();
    }


    public HashMap<PublicKey,String> publicKey2User(){
	return publicKeyMap.publicKey2User();	
    }    

    /* 
       lookup the user name in the publicKeyMap
     */
    
    public String getUser(PublicKey pbk)
	throws HashMapLookupException { 
	return publicKeyMap.getUser(pbk);
    }

    /* 
       lookup the user name from an address in the publicKeyMap
     */
    


    public String getUser(Address address)
	throws HashMapLookupException { 
	return publicKeyMap.getUser(address);
    }


    public String getUser(byte[] address)
	throws HashMapLookupException { 
	return publicKeyMap.getUser(address);
    }


    /* 
         lookup the public key of a user in the publicKeyMap
     */

    public PublicKey getPublicKey(String user)
	throws HashMapLookupException { 		
	return publicKeyMap.getPublicKey(user);
    }


    /* 
         lookup the public key corresonding to an address in the publicKeyMap
     */
    

    public PublicKey getPublicKey(Address address)
	throws HashMapLookupException { 	
	return publicKeyMap.getPublicKey(address); 
    }

    /* 
         lookup the public key corresonding to an address in the publicKeyMap in string format
     */
    


    public String getPublicKeyString(Address address)
	throws HashMapLookupException { 			
	return KeyUtils.publicKeyToString(getPublicKey(address));
    };        
    


    /* lookup the address of a a user in the publicKeyMap
     */
    
    public Address getAddress(String user) 
      throws HashMapLookupException { 	
	return publicKeyMap.getAddress(user);
    }

    /* 
       get the public Key as a string from a user name
     */
    
    public String getPublicKeyString(String user)
	throws HashMapLookupException { 			
	return publicKeyMap.getPublicKeyString(user);
    };


    /* 
       get the public Key in Base58 encoding from a user name
     */
    

    public String getPublicKeyStringBase58(String user)
	throws HashMapLookupException { 		
	return KeyUtils.publicKey2AddressBase58(getPublicKey(user));
    };
    
    /* 
      obtain the private key from a user namme
    */
    

    public PrivateKey getPrivateKey(String user)
	throws HashMapLookupException { 	
	PrivateKey result =  user2PrivateKey.get(user);
	if (result == null)
	    {
		throw new HashMapLookupException ("No private Key in keymap for user \"" + user + "\"");
	    } else {
	    return result;
	}
    }


    /* 
      obtain the private key from a public key
    */
    
    

    public PrivateKey getPrivateKey(PublicKey pbk)
          throws HashMapLookupException { 
	return getPrivateKey(getUser(pbk));
	
    }

    /* 
      obtain the private key from an address
    */
    

    public PrivateKey getPrivateKey(Address address)
	throws HashMapLookupException { 
	return getPrivateKey(getUser(address));
	
    }    



    /* 
      obtain the underlying set of users
    */
    
    public Set<String> getUsers(){
	return publicKeyMap.getUsers();
    }

    /*
      sign a message  by the private key of a user
    */

    public byte[] signMessage(byte[] message,String user)
	throws SignatureException, InvalidKeyException, HashMapLookupException { 	
	return Crypto.sign(getPrivateKey(user),message);
    }

    /** 
     * Test cases
     */            

    public static void test()
	throws HashMapLookupException { 
	PublicPrivateKeyMap keyMap = SampleKeyMap.generate(new String[]{ "Alice", "Bob", "Carol", "David"});
	PublicKeyMap pubKeyMap = keyMap.toPublicKeyMap();
	PublicKey pubKeyA =	pubKeyMap.getPublicKey("Alice");
	System.out.println("pubKeyA=" + KeyUtils.publicKey2AddressBase58(Address.publicKey2ByteAddress(pubKeyA)));
	PublicKey pubKeyB =	pubKeyMap.getPublicKey("Bob");
	System.out.println("pubKeyB=" + KeyUtils.publicKey2AddressBase58(Address.publicKey2ByteAddress(pubKeyB)));
	System.out.println("Alice to pubkey and back = " + keyMap.getUser(pubKeyA));
	Address addressA = new Address(pubKeyA);
	Address addressB = new Address(pubKeyB);
	System.out.println("Get public Key Alice in base 58 =  " + keyMap.getPublicKeyString("Alice"));
	try{
	    System.out.println("Get public key for Unknown base 58 =  " + keyMap.getPublicKeyString("Unknown"));
	} catch (HashMapLookupException e){
	    System.out.println("HashMapLookup Exception = " + e.getMessage());
	    };
	System.out.println("Alice to address and back = " + keyMap.getUser(addressA));	
    	System.out.println("Alice to byte[] address and back = " + keyMap.getUser(Address.publicKey2ByteAddress(pubKeyA)));
	System.out.println("Number of keys in address2PublicKey = " + keyMap.toPublicKeyMap().address2PublicKey().keySet().size());
	System.out.println("Keys:");
	for (Address key : keyMap.toPublicKeyMap().address2PublicKey().keySet()){
	    System.out.println(KeyUtils.publicKey2AddressBase58(key.getAddress()));
	};
	System.out.println("Hashes of keys");	
	for (Address key : keyMap.toPublicKeyMap().address2PublicKey().keySet()){
	    System.out.println(key.hashCode());
	};
	System.out.println("Comparison of publickey of Alice with other hashes:");		
	for (Address key : keyMap.toPublicKeyMap().address2PublicKey().keySet()){
	    System.out.println("Comparison for key with hascode \"" + key.hashCode() + "\"");
	    System.out.println("comparison = " + key.equals(addressA));
	    if (key.equals(addressA)){
		System.out.println("PublicKey is \"" +  keyMap.getPublicKeyString(addressA) + "\"");
	    }

	};	
	
	System.out.println("Address Alice = " + KeyUtils.publicKey2AddressBase58(addressA.getAddress()));
	System.out.println("Hash of Alice Address = " + addressA.hashCode());
	System.out.println("keys contain alice address = "
			   + keyMap.toPublicKeyMap().address2PublicKey().keySet().contains(addressA.getAddress()));
	PublicKey aliceAddressBack = keyMap.toPublicKeyMap().address2PublicKey().get(addressA);
	System.out.println("Vers 1 Public Key Alice from address = " +
			   KeyUtils.publicKey2AddressBase58(aliceAddressBack));
	System.out.println("Public Key Alice from address = " +
			   KeyUtils.publicKey2AddressBase58(keyMap.getPublicKey(addressA)));
    }



    /** 
     * main function running test cases
     */            
    
    public static void main(String[] args)
	throws HashMapLookupException { 
	test();
    }        
    

}    
