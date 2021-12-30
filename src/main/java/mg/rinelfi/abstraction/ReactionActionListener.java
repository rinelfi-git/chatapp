package mg.rinelfi.abstraction;

public interface ReactionActionListener {
    void onReact(ReactionActionConsumer consumer);
    void resetOnReact();
    void trigger(int reaction);
}
