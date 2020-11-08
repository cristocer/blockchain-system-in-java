import java.util.HashMap;
import java.security.PublicKey;
import java.util.Set;


    /** defines maps between names of users (strings) and public keys 
        we define them for convenience and efficiency in both direction,
        and when only using operations in  this class consistency will be maintained 
    */

public class PublicKeyMap {


    /* the underlying hashmaps */
    
    private HashMap<String,PublicKey> user2PublicKey;
    private HashMap<PublicKey,String> publicKey2User;
    private HashMap<Address,PublicKey> address2PublicKey;    


    /* constrcuting the empty map */
    
    public PublicKeyMap(){
	this.user2PublicKey = new HashMap<String,PublicKey> ();
	this.publicKey2User = new HashMap<PublicKey,String>();
	this.address2PublicKey = new HashMap<Address,PublicKey>();
    }	

    /* constrcuting a map from existing data */

    public PublicKeyMap(HashMap<String,PublicKey> user2PublicKey,
			HashMap<PublicKey,String> publicKey2User,
			HashMap<Address,PublicKey> address2PublicKey) {       
	this.user2PublicKey = new HashMap<String,PublicKey> (user2PublicKey);
	this.publicKey2User = new HashMap<PublicKey,String>(publicKey2User);
	this.address2PublicKey = new HashMap<Address,PublicKey> (address2PublicKey);
    }

    /* the copy constructor */

    public PublicKeyMap(PublicKeyMap publicKeyMap) {       
	this.user2PublicKey = new HashMap<String,PublicKey> (publicKeyMap.getUser2PublicKey());
	this.publicKey2User = new HashMap<PublicKey,String>(publicKeyMap.publicKey2User());
	this.address2PublicKey = new HashMap<Address,PublicKey>(publicKeyMap.address2PublicKey());	
    }

    /* adding an entry to the map */

    public void addKey(String user,PublicKey publicKey){
	user2PublicKey.put(user,publicKey);
	publicKey2User.put(publicKey,user);
	address2PublicKey.put(new Address(KeyUtils.publicKey2Address(publicKey)),publicKey);
    }


    /* retrieve the  underlying hash maps */
    
    public HashMap<String,PublicKey> getUser2PublicKey(){
	return new HashMap<String,PublicKey>(user2PublicKey);
    }

    

    public HashMap<PublicKey,String> publicKey2User(){
	return new HashMap<PublicKey,String>(publicKey2User);
    }


    public HashMap<Address,PublicKey> address2PublicKey(){
	return address2PublicKey; //new HashMap<Address,PublicKey>(address2PublicKey);
    }    

    

    /*  look up a  user in the map from the public key */

    public String getUser(PublicKey pbk) throws HashMapLookupException{
	String result = this.publicKey2User.get(pbk);
	if (result == null)
	    {
		throw new HashMapLookupException ("No user in keymap for public key \"" + KeyUtils.publicKeyToString(pbk) + "\"");
	    } else {
	    return result;
	}
    }

    /* 
       look up the public key from the address
    */

    public PublicKey getPublicKey(Address address)
	throws HashMapLookupException { 
	PublicKey result = this.address2PublicKey.get(address);
	if (result == null)
	    {
		throw new HashMapLookupException ("No public Key in keymap for address \"" + address.toStringBase58() + "\"");
	    } else {
	    return result;
	}
    }

    /*
      obtain a string representation of the public key from the address
    */

    public String getPublicKeyString(Address address)
	throws HashMapLookupException { 			
	return KeyUtils.publicKeyToString(getPublicKey(address));
    };    

    /* 
       compute the public key from the address given as a byte array
    */
    

    public PublicKey getPublicKey(byte[] address)
	throws HashMapLookupException { 
	PublicKey result = this.address2PublicKey.get(new Address(address));
	if (result == null)
	    {
		throw new HashMapLookupException ("No public key in keymap for address  \"" + KeyUtils.addressByteArray2Base58(address) + "\"");
	    } else {
	    return result;
	}
    }
	
    /* 
       obtain the user from the address
    */

    public String getUser(Address address)
	throws HashMapLookupException {
	return getUser(getPublicKey(address));
    }


    /* 
       obtain the user from the address given as a byte array
    */
    

    public String getUser(byte[] address)
	throws HashMapLookupException { 
	return getUser(getPublicKey(address));
    }

    /*  look up a  public key in the map */    

    public PublicKey getPublicKey(String user)
	throws HashMapLookupException { 	
	PublicKey result = this.user2PublicKey.get(user);
	if (result == null)
	    {
		throw new HashMapLookupException ("No public key in key map for user \"" + user + "\"");
	    } else {
	    return result;
	}
    }


    
    /*  obtain from a user name the address */

    public Address getAddress(String user)
	throws HashMapLookupException { 
	return new Address(getPublicKey(user));
    };


    /*  obtain from a user name the public key in a string format */
    
    public String getPublicKeyString(String user)
	throws HashMapLookupException { 			
	return KeyUtils.publicKeyToString(getPublicKey(user));
    };

    /*  obtain from a user name the public key in Base58 format */    

    public String getPublicKeyStringBase58(String user)
	throws HashMapLookupException { 			
	return KeyUtils.publicKey2AddressBase58(getPublicKey(user));
    };
    


    /* get list of users */
    
    public Set<String> getUsers(){
	return user2PublicKey.keySet();
    }


}    
