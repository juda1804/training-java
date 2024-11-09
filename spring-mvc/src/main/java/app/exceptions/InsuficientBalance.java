package app.exceptions;

public class InsuficientBalance extends RuntimeException {
    public InsuficientBalance() {
        super("Not enough balance");
    }
}
