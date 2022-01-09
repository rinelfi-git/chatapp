package mg.rinelfi.chat.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public abstract class ViewModel {
    protected BooleanProperty visible;
    protected ViewModelFactory vmf;
    
    public ViewModel(ViewModelFactory vmf) {
        this.vmf = vmf;
        this.visible = new SimpleBooleanProperty();
    }
}
