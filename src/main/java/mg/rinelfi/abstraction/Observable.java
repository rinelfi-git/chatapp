package mg.rinelfi.abstraction;

public interface Observable {
    void addObserver(Observer observer);
    void update(Object data);
}
