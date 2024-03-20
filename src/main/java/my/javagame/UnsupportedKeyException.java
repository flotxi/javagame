package my.javagame;

public class UnsupportedKeyException extends RuntimeException {

    public UnsupportedKeyException(){
        super();
    }

    public UnsupportedKeyException(String message){
        super(message);
    }
}
