package innoskrit.service;

import innoskrit.model.Database;
import innoskrit.model.LogEntry;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DatabaseManager {
    private static final String LOG_FILE = "database.log";

    public void simulateRecovery(HashMap<String, Integer> initialData) {
        Database database = new Database(initialData);
        createLogFile();

        // initial data population
        int transactionId1 = 1;
        writeLogEntry(new LogEntry(LogEntry.Operation.UPDATE, "key1", transactionId1, -1, 2), LOG_FILE);
        database.update("key1", 2);
        writeLogEntry(new LogEntry(LogEntry.Operation.COMMIT, "", transactionId1, -1, -1), LOG_FILE);

        int transactionId2 = 2;
        writeLogEntry(new LogEntry(LogEntry.Operation.UPDATE, "key2", transactionId2, -1, 10), LOG_FILE);
        database.update("key2", 10);
        writeLogEntry(new LogEntry(LogEntry.Operation.COMMIT, "", transactionId2, -1, -1), LOG_FILE);


        // Simulate scenario 1: Update key1 from 2 to 5 and commit
        int transactionId3 = 3;
        writeLogEntry(new LogEntry(LogEntry.Operation.UPDATE, "key1", transactionId3, database.getValue("key1"), 5), LOG_FILE);
        database.update("key1", 5);
        writeLogEntry(new LogEntry(LogEntry.Operation.COMMIT, "", transactionId3, -1, -1), LOG_FILE);

        // Simulate scenario 2: Update key2 from 10 to 20 without commit
        int transactionId4 = 4;
        writeLogEntry(new LogEntry(LogEntry.Operation.UPDATE, "key2", transactionId4, database.getValue("key2"), 20), LOG_FILE);
        database.update("key2", 20);

        // Simulate system crash by deleting the database
        deleteDatabase(database);

        // Perform recovery
        System.out.println("---------- RECOVERY STARTED ----------");
        System.out.println("\n---------- REDO STARTED ----------\n");
        redoTransactions(database);
        System.out.println("\n---------- UNDO STARTED ----------\n");
        undoTransactions(database);

        // Verify the recovered database
        System.out.println("Recovered Database:");
        System.out.println("key1: " + database.getValue("key1")); // 2
        System.out.println("key2: " + database.getValue("key2")); // -1 (not present)
    }

    private void undoTransactions(Database database) {
        try (BufferedReader logReader = new BufferedReader(new FileReader(LOG_FILE))) {
            String logEntryString;
            Set<Integer> committedTransactions = new HashSet<>();

            // Identify committed transactions
            while ((logEntryString = logReader.readLine()) != null) {
                LogEntry logEntry = parseLogEntry(logEntryString);

                if (logEntry.getOperation() == LogEntry.Operation.COMMIT) {
                    committedTransactions.add(logEntry.getTransactionId());
                }
            }

            // Undo uncommitted transactions
            logReader.close();
            BufferedReader nlogReader = new BufferedReader(new FileReader(LOG_FILE)); // Reopen log file

            while ((logEntryString = nlogReader.readLine()) != null) {
                LogEntry logEntry = parseLogEntry(logEntryString);

                if (logEntry.getOperation() == LogEntry.Operation.UPDATE) {
                    if (!committedTransactions.contains(logEntry.getTransactionId())) {
                        System.out.println("Undoing transactionId: " + logEntry.getTransactionId() + " key: " + logEntry.getKey() + " value: " + logEntry.getOldValue());
                        database.update(logEntry.getKey(), logEntry.getOldValue());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void redoTransactions(Database database) {
        try (BufferedReader logReader = new BufferedReader(new FileReader(LOG_FILE))) {
            String logEntryString;
            while ((logEntryString = logReader.readLine()) != null) {
                LogEntry logEntry = parseLogEntry(logEntryString);

                String key = logEntry.getKey();
                int transactionId = logEntry.getTransactionId();
                int value = logEntry.getNewValue();
                if (logEntry.getOperation() == LogEntry.Operation.UPDATE) {
                    System.out.println("Redoing transactionId: " + transactionId + " key: " + key + " value: " + value);
                    database.update(key, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LogEntry parseLogEntry(String logEntryString) {
        String[] fields = logEntryString.split(",");
        LogEntry.Operation operation = LogEntry.Operation.valueOf(fields[0]);
        String key = fields[1];
        int transactionId = Integer.parseInt(fields[2]);
        int oldValue = Integer.parseInt(fields[3]);
        int newValue = Integer.parseInt(fields[4]);

        return new LogEntry(operation, key, transactionId, oldValue, newValue);
    }

    private void writeLogEntry(LogEntry logEntry, String logFilePath) {
        try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(logFilePath, true))) {
            String logEntryString = logEntry.getOperation() + "," + logEntry.getKey() + "," +
                    logEntry.getTransactionId() + "," + logEntry.getOldValue() + "," + logEntry.getNewValue();
            logWriter.write(logEntryString);
            logWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteDatabase(Database database) {
        // Delete the database file
        System.out.println("Simulating system crash, flushing entire database");
        database.flush();
    }

    private void createLogFile() {
        File file = new File(LOG_FILE);
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                FileOutputStream writer = new FileOutputStream(LOG_FILE);
                writer.write(("").getBytes());
                writer.close();
            }
        } catch (Exception e) {
            // do nothing
        }

    }
}
