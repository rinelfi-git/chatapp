package mg.rinelfi.abstraction.observer;

public interface ReactionActionListener {
    void onReact(ReactionActionConsumer consumer);
    void resetOnReact();
    void trigger(int reaction);
}
