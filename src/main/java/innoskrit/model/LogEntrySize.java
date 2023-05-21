package innoskrit.model;

public class LogEntrySize {
    public static final int BYTES = 4 + 2 + 4 + 4; // Size of operation (int) + size of key (UTF String) + size of transactionId (int) + size of value (int)
}
