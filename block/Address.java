import java.security.PublicKey;
import java.util.Arrays;

public class Address{

    /* addresses are represented as an array of bytes */
    
    private byte[] address;

    /* constructs a address from a byte array  by simply copying it */
    
    public Address (byte[] address){
	this.address = address.clone();
    }


    /* copy constructor */
    
    public Address (Address address){
	this.address = address.getAddress().clone();
    }
    

    /* constructs a address from a public key by essentially running SHA256 on it

    */
    
    public Address (PublicKey pbk){
	this.address = Address.publicKey2ByteAddress(pbk);
    }


    /* returns the address */
    
    public byte[] getAddress(){
	return address;
    }


    /* returns the address in Base58 as used in bitcoin */
    
    public String toStringBase58(){
	return KeyUtils.publicKey2AddressBase58(address);
    };



    /* creates a hashCode so that TxId could be used in a HashMap 
     */

    public int hashCode(){
       return java.util.Arrays.hashCode(this.address);
    };


    /* equals function so that TxId could be used in a Collections such as HashMap

       The following code is based on
        https://stackoverflow.com/questions/15576009/how-to-make-hashmap-work-with-arrays-as-key  

     */
    

    public boolean equals(Object o) {
            if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
            Address addr = (Address) o;
            return java.util.Arrays.equals(getAddress(),addr.getAddress());
        }
    
    /* helper function to construct a byte[] address from a public key 

     */
    

    public static byte[] publicKey2ByteAddress (PublicKey pbk){
	return KeyUtils.publicKey2Address(pbk);
    }


    /**
       Test Cases

    */ 
   
	   
    public static void test()
	throws HashMapLookupException { 	
	String[] names = new String[]{ "Alice", "Bob", "Carol", "David"};
	PublicPrivateKeyMap keys = SampleKeyMap.generate(names);
	Address addressAlice = new Address(keys.getPublicKey("Alice"));
	Address addressBob = new Address(keys.getPublicKey("Bob"));
	System.out.println("addressAlice = " + addressAlice.toStringBase58());
	System.out.println("addressBob = " + addressBob.toStringBase58());	
	System.out.println("Compare addressAlice with addressAlice = "  + addressAlice.equals(addressAlice));
	System.out.println("Compare addressAlice with new addressAlice = "  + addressAlice.equals(new Address(addressAlice)));	
	System.out.println("Compare addressAlice with addressBob = "  + addressAlice.equals(addressBob));	

    }


    /*
      main function running test cases
    */
    
    public static void main(String[] args)
	throws HashMapLookupException { 	
	test();
    }

	


}
