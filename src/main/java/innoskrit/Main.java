package innoskrit;

import innoskrit.service.DatabaseManager;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        DatabaseManager dbms = new DatabaseManager();
        HashMap<String, Integer> initialData = new HashMap<>();
        initialData.put("key1", 2);
        initialData.put("key2", 10);

        dbms.simulateRecovery(initialData);
    }
}
