package mg.rinelfi.chat.observer;

import mg.rinelfi.chat.beans.Discussion;

public interface ContactRightClickListener {
    void onContactRightClick(ContactRightClickConsumer consumer);
    void triggerContactRightClick(long channel);
}
