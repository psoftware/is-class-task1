package main.java.task0.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.event.ActionEvent;
import main.java.task0.Course;
import main.java.task0.db.DBManager;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
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
        lunghezzaFinestra = 550;
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
                        form.getAction().getItems().add("Register to Exam");
                        form.getAction().getItems().add("Deregister to Exam");
                        form.getAction().getItems().add("See Grades");
                    }
                    form.getAction().getSelectionModel().selectFirst();
                }
        });
        
      form.getConfirmButton().setOnAction((ActionEvent ev) -> {
              System.out.println("wasPressedConfirm");
              if(form.getRole().getValue() != null && form.getAction().getValue() != null) {
                  eventConfirm(form.getID(),
                          form.getRole().getValue().toString(),
                          form.getAction().getValue().toString());
              }
        });
    }
    
    public void eventConfirm(String id, String role, String action){
        int formId;
        try {
            formId = Integer.parseInt(form.getID());
        } catch (NumberFormatException e) {
            System.out.println("Inserted id is not a number");
            return;
        }

        try {
            if (role.equals("Professor")) {
                if (action.equals("Add Exam")) {
                    table.setTableCourses("Add Exam Date",
                            course -> {
                                LocalDate newdate = SimpleDialog.DateDialog.showDialog();
                                if(newdate == null)
                                    return;
                                try {
                                    DBManager.getInstance().insertExam(course.getId(), newdate);
                                    SimpleDialog.showConfirmDialog("Exam added successfully");
                                } catch (SQLException e) {
                                    showError(e);
                                }
                            });
                    table.update(DBManager.getInstance().findCourse(formId));
                } else if (action.equals("Add Grade")) {
                    table.setTableExamResults("Insert Mark", true,
                            reg -> {
                                Integer mark = SimpleDialog.MarkDialog.showDialog();
                                if(mark == null)
                                    return;
                                try {
                                    DBManager.getInstance().updateRegistration(reg, mark);
                                    table.update(DBManager.getInstance().findRegistrationProfessor(formId));
                                    SimpleDialog.showConfirmDialog("Mark added successfully");
                                } catch (SQLException e) {
                                    showError(e);
                                }
                            });
                    table.update(DBManager.getInstance().findRegistrationProfessor(formId));
                }
            } else if (role.equals("Student")) {
                if (action.equals("Register to Exam")) {
                    table.setTableExams("Register",
                            exam -> {
                                try {
                                    DBManager.getInstance().insertRegistration(formId, exam, null);
                                    table.update(DBManager.getInstance().findExam(formId));
                                } catch (SQLException e) {
                                    showError(e);
                                }
                            });
                    table.update(DBManager.getInstance().findExam(formId));
                } else if (action.equals("Deregister to Exam")) {
                    table.setTableExamResults("Deregister", false,
                            reg -> {
                                try {
                                    DBManager.getInstance().deleteRegistration(formId, reg.getExam());
                                    table.update(DBManager.getInstance().findRegistrationStudent(formId, true));
                                } catch (SQLException e) {
                                    showError(e);
                                }
                            });
                    table.update(DBManager.getInstance().findRegistrationStudent(formId, true));
                } else if (action.equals("See Grades")) {
                    table.setTableExamResults("", false, null);
                    table.update(DBManager.getInstance().findRegistrationStudent(formId, false));
                }
            }
        } catch(SQLException ex) {
            showError(ex);
        }
    };

    public void showError(SQLException ex) {
        String errString;

        if(ex instanceof DBManager.TriggerSQLException)
            errString = ((DBManager.TriggerSQLException)ex).getTriggerMessage();
        else
            errString = "SQLException: " + ex.getMessage() +
                    "\nSQLState: " + ex.getSQLState() +
                    "\nVendorError: " + ex.getErrorCode();
        SimpleDialog.showErrorDialog(errString);
    }

    public VBox getOuterVbox(){return outerVbox;}
}
