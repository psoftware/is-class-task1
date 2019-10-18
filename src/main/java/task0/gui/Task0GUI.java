package main.java.task0.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.event.ActionEvent;
import main.java.task0.Course;
import main.java.task0.User;

import java.sql.Date;
import java.util.List;

public class Task0GUI {
    private final double larghezzaFinestra;
    private final double lunghezzaFinestra;
    private final String font;
    private final String background;
    private final Form form;
    private final VBox outerVbox;
    private final Table table;
    private final HBox lowerHbox;
    
    public Task0GUI(){
        larghezzaFinestra = 650;
        lunghezzaFinestra = 700;
        font = "Arial";
        background = "ALICEBLUE";
        
        Label title = new Label("Task0");
        title.setFont(Font.font(font, FontWeight.BOLD, 20));
        
        form = new Form();
        table = new Table();
        lowerHbox = new HBox();
        
        outerVbox = new VBox(title, form.getForm(), table, lowerHbox);
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
        
      form.getConfirmButton().setOnAction((ActionEvent ev) -> {
              System.out.println("wasPressedConfirm");
              eventConfirm(form.getID(),
                      form.getRole().getValue().toString(),
                      form.getAction().getValue().toString());
        });
    }
    
    public void eventConfirm(String id, String role, String action){
        if(role.equals("Professor")) {
            if(action.equals("Add Exam")) {
                System.out.println("Add Exam");
                User.getInstance().listCourses(this, Integer.parseInt(form.getID()));
                //update gui, aggiungere bottoni (Datepicker, Conferma)
                lowerHbox.getChildren().clear();
                DatePicker examDate = new DatePicker();
                Button confirmAddButton = new Button("Add Exam");
                lowerHbox.getChildren().add(examDate);
                lowerHbox.getChildren().add(confirmAddButton);
                confirmAddButton.setOnAction((ActionEvent ev) -> {
                        System.out.println("wasPressed");
                        Course course = table.getSelectedCourse();
                        Date date = Date.valueOf(examDate.getValue());
                        System.out.println(date);
                        User.getInstance().addExam(course.getId(), date);
                });
            }
            else if (action.equals("Add Grade")) {
                User.getInstance().listExams(this);
                //update gui, aggiungere bottoni (Textfield(voto), conferma)
            }
        } else if(role.equals("Student")) {
            if(action.equals("Register/Deregister to Exam"))
                ;
            else if(action.equals("See Grades"))
                ;
        }
    };

    public VBox getOuterVbox(){return outerVbox;}

    //gestione delle tabella
    public void updateTable(List list) {
        table.update(list);
    }
    public void setTableExams() {table.setTableExams();}
    public void setTableCourses() {table.setTableCourses();}
}
