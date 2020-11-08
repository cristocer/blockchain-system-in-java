/* 
   Exception reporting that
    when trying to add a new entry to UTXOlist, it alreay exists
*/

public class UTXOOutputAlreadyExistsException extends Exception {

    
    public UTXOOutputAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
