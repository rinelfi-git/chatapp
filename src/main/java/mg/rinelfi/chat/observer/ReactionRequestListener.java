package mg.rinelfi.chat.observer;

public interface ReactionRequestListener {
    void onReactionRequest(ReactionRequestConsumer consumer);
    void trigger(int reaction);
}
