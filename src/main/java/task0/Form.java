package task0;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class Form { //01
    
    private final Label labelID = new Label("ID Utente"); 
    private final Label labelRole = new Label("Role");
    private final Label labelAction = new Label("What do you want to do?");
    private final TextField id = new TextField();
    private final ChoiceBox role = new ChoiceBox();
    private final ChoiceBox action = new ChoiceBox();
    private final Button confirmButton = new Button("CONFIRM");
    private final HBox formBox;
    
    public Form(){     
        VBox v1 = new VBox(labelID, labelRole, labelAction);
        v1.setSpacing(15);
        VBox v2 = new VBox(id, role, action);
        v2.setSpacing(5);
        v1.setAlignment(Pos.TOP_CENTER);
        v2.setAlignment(Pos.TOP_CENTER);
        
        formBox = new HBox(v1, v2, confirmButton);
        formBox.setSpacing(45);
        formBox.setAlignment(Pos.TOP_CENTER);
        
    }
    
    public String getID(){return id.getText();}
    public ChoiceBox getRole(){return role;}
    public ChoiceBox getAction(){return action;}
    public Button getConfirmButton(){return confirmButton;}
    public HBox getForm(){return formBox;}
}
