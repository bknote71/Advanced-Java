package ftp.repo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
    private static final Map<String, String> repo = new ConcurrentHashMap<>();
    private static final UserRepository instance = new UserRepository();

    private UserRepository() { }

    public static UserRepository getInstance() {
        return instance;
    }

    static {
        repo.put("ftp", "ftp");
        repo.put("user", "user");
    }

    public String get(String key) {
        return this.repo.get(key);
    }

    public void save(String key, String value) {
        this.repo.put(key, value);
    }
}
