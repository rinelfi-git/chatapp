package mg.rinelfi.chat.observer;

import java.util.List;

public interface ContactLeftClickListener {
    void onContactLeftClick(ContactLeftClickConsumer consumer);
    void triggerContaLeftClick(String username, List<String> tokens);
}
