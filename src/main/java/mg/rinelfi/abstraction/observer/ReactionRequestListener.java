package mg.rinelfi.abstraction.observer;

public interface ReactionRequestListener {
    void onReactionRequest(ReactionRequestConsumer consumer);
    void trigger(int reaction);
}
