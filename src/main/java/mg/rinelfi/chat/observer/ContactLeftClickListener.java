package mg.rinelfi.chat.observer;

import java.util.Map;

public interface ContactLeftClickListener {
    void onContactLeftClick(ContactLeftClickConsumer consumer);
    void triggerContaLeftClick(long id, String username, Map<String, String> tokens);
}
