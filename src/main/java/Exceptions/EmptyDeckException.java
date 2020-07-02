package Exceptions;

public class EmptyDeckException extends Exception {
    public EmptyDeckException() {
        super("Deck is empty.");
    }
}
