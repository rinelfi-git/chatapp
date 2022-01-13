package mg.rinelfi.chat.observer;

import java.util.Map;

public interface ContactLeftClickConsumer {
    void consumeContactLeftClick(long id, String username,Map<String, String> tokens);
}
