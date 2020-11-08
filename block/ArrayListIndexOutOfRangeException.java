/* Exception reporting that in an ArrayList a lookup for an index is out of range 
 */

public class ArrayListIndexOutOfRangeException extends Exception {

    
    public ArrayListIndexOutOfRangeException(String errorMessage) {
        super(errorMessage);
    }
}
