/* 
   Exception reporting that
    a utxoutput entry doesn't exist  in utxo
*/


public class UTXOOutputDoesntExistsException extends Exception {

    
    public UTXOOutputDoesntExistsException(String errorMessage) {
        super(errorMessage);
    }
}
