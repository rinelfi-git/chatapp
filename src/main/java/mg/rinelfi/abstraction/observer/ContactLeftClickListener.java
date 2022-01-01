package mg.rinelfi.abstraction.observer;

import mg.rinelfi.beans.Discussion;

public interface ContactLeftClickListener {
    void onContactLeftClick(ContactLeftClickConsumer consumer);
    void triggerContaLeftClick(Discussion data);
}
