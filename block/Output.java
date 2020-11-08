import java.security.PublicKey;
import java.security.SignatureException;
import java.security.InvalidKeyException;


/** Output
     *  specifies a user and the amount to be transferred 
     *  will be used in a transaction as one arrow going in or out
     *    of one transaction
     */


public class Output{

    /** The amount to be transfered  */
    private int amount;

    /** The user */
    private Address recipient;

    /** 
     * Create new User Amount 
     */

    public Output(Address recipient,int amount){
	this.amount  = amount;
	this.recipient = recipient;
    }


    /* 
         create an output from the name of the user, looking up
         the address in the keymap
    */
    
    public Output(String recipient,int amount,PublicKeyMap keymap)
	throws HashMapLookupException { 
	this.amount  = amount;
	this.recipient = keymap.getAddress(recipient);
    }    

    /* 
         as before but using a publicPrivateKeyMap from which one obtains the
         underlying publicKeyMap
    */

    public Output(String recipient,int amount,PublicPrivateKeyMap keymap)
	throws HashMapLookupException { 
	this.amount  = amount;
	this.recipient = keymap.getAddress(recipient);
    }    
    

    

    /** 
     * Get the address of the recipient (a component of the output)
     */

    public Address getRecipient()
	throws HashMapLookupException { 	
	return recipient;
    };

    /* 
       look up the name of the recipient in the pubKeyMap 
    */

    public String getRecipientName(PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 
	return pubKeyMap.getUser(recipient);
    };
    
    

    /** 
     * Get the  amount 
     */    

    public int getAmount(){
	return amount;
    }

    /* 
       returns a string representing the Output using base58 encoding of the recipient's address  
       in format    <word1>address<word2>amount<word3>
    */

    public String toStringBase58(String word1, String word2, String word3)
	throws HashMapLookupException
    {
	return word1 + getRecipient().toStringBase58() + word2 + getAmount() + word3;
    };


    /* 
       returns a string representing the Output using base58 encoding using default formatting 
    */    
    
    public String toStringBase58()
	throws HashMapLookupException
    {
	return toStringBase58("(recipient=\"","\",amount=",")");
    };

    /* 
       returns a string representing the Output using pubKeyMap to look up the recipient 
       in format <word1>recipient<word2>amount<word3>
    */

    
    public String toString(String word1, String word2, String word3, PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 		
	return word1 + getRecipientName(pubKeyMap) + word2 + getAmount() + word3;
    };

    /* 
       returns a string representing the Output using pubKeyMap to look up the recipient 
       in default formatting
    */

    
    public String toString(PublicKeyMap pubKeyMap)
    	throws HashMapLookupException { 
	return toString("(Recipient=\"","\",value=",")",pubKeyMap);
    }


    /** 
     *  Print the entry in the form word1  <user> word2 <amount> <word3>
         with recipient given in Base58y
     */
    
    public void printBase58(String word1, String word2,String word3)
	throws HashMapLookupException { 	
	System.out.println(toStringBase58(word1,word2,word3));
    }

    /** 
        As printBase58(String word1, String word2,String word3)
         but using default formatting
     */        


    public void printBase58()
	throws HashMapLookupException { 	
	System.out.println(toStringBase58());
    }
    

 
    

    /** 
     * Print the entry in the form word1  <user> word2 <amount> <word3>
     *   we use the pubKeyMap in order to look up the names for each public key
     */
    
    public void print(String word1, String word2,String word3, PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 	
	System.out.println(toString(word1,word2,word3, pubKeyMap));
    }

    /** 
        As print(String word1, String word2,String word3, PublicKeyMap pubKeyMap)
         but using default formatting
     */        


    public void print(PublicKeyMap pubKeyMap)
	throws HashMapLookupException { 	
	System.out.println(toString(pubKeyMap));
    }


    /** 
     * Test cases
     */            

    public static void test()
	throws HashMapLookupException { 
	PublicPrivateKeyMap keyMap = SampleKeyMap.generate(new String[]{ "Alice", "Bob", "Carol", "David"});
	PublicKeyMap pubKeyMap = keyMap.toPublicKeyMap();
	PublicKey pubKeyA =	pubKeyMap.getPublicKey("Alice");
	PublicKey pubKeyB =	pubKeyMap.getPublicKey("Bob");
	Address addressA = new Address(pubKeyA);
	Address addressB = new Address(pubKeyB);
	System.out.println();
	System.out.println("Test Alice 10");
	(new Output(addressA,10)).print(pubKeyMap);
	(new Output(addressA,10)).printBase58();	
	System.out.println();
	System.out.println("Test Bob 20");
	(new Output(addressB,20)).print(pubKeyMap);
	(new Output(addressB,20)).printBase58();		
    };


    /** 
     * main function running test cases
     */            
    
    public static void main(String[] args)
	throws HashMapLookupException { 
	Output.test();
    }        

    
}
    

    
