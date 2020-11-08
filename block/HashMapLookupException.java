/* 
  Exception for reporting that a HashMap is applied to an item
  which is not in the set of keys for the HashMap
*/


public class HashMapLookupException extends Exception {

    
    public HashMapLookupException(String errorMessage) {
        super(errorMessage);
    }
}
