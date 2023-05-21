package innoskrit.model;

public class LogEntry {
    public enum Operation {
        UPDATE,
        COMMIT
    }

    private Operation operation;
    private String key;
    private int transactionId;
    private int oldValue;
    private int newValue;

    public LogEntry(Operation operation, String key, int transactionId, int oldValue, int newValue) {
        this.operation = operation;
        this.key = key;
        this.transactionId = transactionId;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Operation getOperation() {
        return operation;
    }

    public String getKey() {
        return key;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getOldValue() {
        return oldValue;
    }

    public int getNewValue() {
        return newValue;
    }
}