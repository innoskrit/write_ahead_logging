package innoskrit.model;

import java.util.HashMap;
import java.util.Map;

public class Database {
    private Map<String, Integer> data;

    public Database(HashMap<String, Integer> initialData) {
        this.data = initialData;
    }

    public void update(String key, int value) {
        System.out.println("Updating key: " + key + " value: " + value);
        data.put(key, value);
    }

    public int getValue(String key) {
        return data.getOrDefault(key, -1);
    }

    public void flush() {
        this.data = new HashMap<>();
    }
}