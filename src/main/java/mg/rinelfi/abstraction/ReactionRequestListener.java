package mg.rinelfi.abstraction;

public interface ReactionRequestListener {
    void onReactionRequest(ReactionRequestConsumer consumer);
    void trigger(int reaction);
}
