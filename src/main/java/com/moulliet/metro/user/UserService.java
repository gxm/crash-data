package com.moulliet.metro.user;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class UserService {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static final String uuid = "57f92412-4839-4de7-a2bb-61ec9fb01cf5";

    public ObjectNode login(String user, String password) {
        ObjectNode node = mapper.createObjectNode();
        if ("admin".equals(user) && "m3tr0".equals(password)) {
            node.put("type", "admin");
            node.put("key", uuid);
        } else {
            node.put("type", "user");
        }
        return node;
    }

    public boolean isValidKey(String key) {
        return uuid.equals(key);
    }
}
