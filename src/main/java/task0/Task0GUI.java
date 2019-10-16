package main.java.task0;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

class Task0GUI {
    private final double larghezzaFinestra;
    private final double lunghezzaFinestra;
    private final String font;
    private final String background;
    private final Form form;
    private final VBox outerVbox;
    private final Table table;
    
    public Task0GUI(){
        larghezzaFinestra = 650;
        lunghezzaFinestra = 700;
        font = "Arial";
        background = "ALICEBLUE";
        
        Label title = new Label("Task0");
        title.setFont(Font.font(font, FontWeight.BOLD, 20));
        
        form = new Form();
        table = new Table();
        
        outerVbox = new VBox(title, form.getForm(), table);
        outerVbox.setPrefSize(larghezzaFinestra, lunghezzaFinestra);
        outerVbox.setStyle("-fx-background: "+ background);
        outerVbox.setAlignment(Pos.TOP_CENTER);
        outerVbox.setSpacing(20);
        
        eventInit();
      
    }
    
   public void eventInit(){
        form.getRole().showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
            if(isNowShowing)
                if( !(form.getID().isEmpty()) ){
                    form.getRole().getItems().clear();
                    form.getRole().getItems().add("Professor");
                    form.getRole().getItems().add("Student"); 
                }else{
                    form.getRole().getItems().clear();
                    form.getAction().getItems().clear();
                }
        });
        form.getRole().getSelectionModel().
                selectedIndexProperty().addListener(
        new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, 
                             Number oldV, Number newV){
                    if(newV.intValue() == 0){
                        form.getAction().getItems().clear();
                        form.getAction().getItems().add("Add Exam");
                        form.getAction().getItems().add("Add Grade"); 
                    }else{
                        form.getAction().getItems().clear();
                        form.getAction().getItems().add("Register/Deregister to Exam");
                        form.getAction().getItems().add("See Grades"); 
                    }
                }
        });
        
      form.getConfirmButton().pressedProperty().addListener((e) -> {
          eventConfirm(form.getID(),
                  form.getRole().getValue().toString(),
                  form.getAction().getValue().toString());
        });
    }

    public void updateTable(List list) {
        table.update(list);
    }
    
    public void eventConfirm(String id, String role, String action){
        if(role.equals("Professor")) {
            if(action.equals("Add Exam"))
                User.getInstance().listCourses(this, Integer.parseInt(form.getID()));
            else if(action.equals("Add Grade"))
                User.getInstance().listExams(this);
        } else if(role.equals("Student")) {
            if(action.equals("Register/Deregister to Exam"))
                ;
            else if(action.equals("See Grades"))
                ;
        }
    };
    public VBox getOuterVbox(){return outerVbox;}

}
