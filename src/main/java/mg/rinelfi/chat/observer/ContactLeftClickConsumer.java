package mg.rinelfi.chat.observer;

import java.util.List;

public interface ContactLeftClickConsumer {
    void consumeContactLeftClick(String username, List<String> tokens);
}
