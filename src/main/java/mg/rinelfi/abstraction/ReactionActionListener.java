package mg.rinelfi.abstraction;

public interface ReactionActionListener {
    void onReact(ReactionActionConsumer consumer);
    void trigger(int reaction);
}
