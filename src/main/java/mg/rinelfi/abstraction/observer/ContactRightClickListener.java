package mg.rinelfi.abstraction.observer;

import mg.rinelfi.beans.Discussion;

public interface ContactRightClickListener {
    void onContactRightClick(ContactRightClickConsumer consumer);
    void triggerContactRightClick(Discussion data);
}
