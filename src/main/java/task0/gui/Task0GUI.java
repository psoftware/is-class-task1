package main.java.task0.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.event.ActionEvent;
import javafx.scene.text.TextAlignment;
import main.java.task0.Course;
import main.java.task0.db.CompositeDBManager;
import main.java.task0.db.DBManager;
import main.java.task0.db.LevelDBManager;

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
    private final Label levelDbAvailLabel = new Label("LevelDB Availability:");
    private final Label queryInfoLabel = new Label("Query Info");
    private final ChoiceBox levelDbAvailBox;
    
    public Task0GUI(){

        larghezzaFinestra = 650;
        lunghezzaFinestra = 600;
        font = "Arial";
        background = "ALICEBLUE";
        
        Label title = new Label("Task0");
        title.setFont(Font.font(font, FontWeight.BOLD, 20));

        levelDbAvailBox = new ChoiceBox(FXCollections.observableArrayList(new String[]{ "Available", "Not Available" }));
        levelDbAvailBox.getSelectionModel().selectFirst();

        form = new Form();
        table = new Table();

        VBox lowerVbox1 = new VBox(levelDbAvailLabel);
        VBox lowerVbox2 = new VBox(levelDbAvailBox);
        lowerHbox = new HBox(lowerVbox1, lowerVbox2);
        lowerHbox.setSpacing(15);
        lowerHbox.setAlignment(Pos.BOTTOM_CENTER);
        queryInfoLabel.setTextAlignment(TextAlignment.CENTER);
        
        outerVbox = new VBox(title, form.getForm(), table, queryInfoLabel, lowerHbox);
        outerVbox.setPrefSize(larghezzaFinestra, lunghezzaFinestra);
        outerVbox.setStyle("-fx-background: "+ background);
        outerVbox.setAlignment(Pos.TOP_CENTER);
        outerVbox.setSpacing(20);
        
        eventInit();
      
    }
    
   public void eventInit(){
       levelDbAvailBox.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> {
           boolean avalability = (new_value.intValue() == 0);
           LevelDBManager.getInstance().setAvailability(avalability);
       });

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
                                try {
                                    CompositeDBManager.getInstance().insertExam(course.getId(), newdate);
                                    SimpleDialog.showConfirmDialog("Exam added successfully");
                                    updateQueryInfoLabel();
                                } catch (Exception e) {
                                    showError(e);
                                }
                            });
                    table.update(CompositeDBManager.getInstance().findCourse(formId));
                } else if (action.equals("Add Grade")) {
                    table.setTableExamResults("Insert Mark", true,
                            reg -> {
                                int mark = SimpleDialog.MarkDialog.showDialog();
                                try {
                                    CompositeDBManager.getInstance().updateRegistration(reg, mark);
                                    table.update(CompositeDBManager.getInstance().findRegistrationProfessor(formId));
                                    SimpleDialog.showConfirmDialog("Mark added successfully");
                                    updateQueryInfoLabel();
                                } catch (Exception e) {
                                    showError(e);
                                }
                            });
                    table.update(CompositeDBManager.getInstance().findRegistrationProfessor(formId));
                }
            } else if (role.equals("Student")) {
                if (action.equals("Register to Exam")) {
                    table.setTableExams("Register",
                            exam -> {
                                try {
                                    CompositeDBManager.getInstance().insertRegistration(formId, exam, null);
                                    table.update(CompositeDBManager.getInstance().findExam(formId));
                                    updateQueryInfoLabel();
                                } catch (Exception e) {
                                    showError(e);
                                }
                            });
                    table.update(CompositeDBManager.getInstance().findExam(formId));
                } else if (action.equals("Deregister to Exam")) {
                    table.setTableExamResults("Deregister", false,
                            reg -> {
                                try {
                                    CompositeDBManager.getInstance().deleteRegistration(formId, reg.getExam());
                                    table.update(CompositeDBManager.getInstance().findRegistrationStudent(formId, true));
                                    updateQueryInfoLabel();
                                } catch (Exception e) {
                                    showError(e);
                                }
                            });
                    table.update(CompositeDBManager.getInstance().findRegistrationStudent(formId, true));
                } else if (action.equals("See Grades")) {
                    table.setTableExamResults("", false, null);
                    table.update(CompositeDBManager.getInstance().findRegistrationStudent(formId, false));
                }
            }
            updateQueryInfoLabel();
        } catch(Exception ex) {
            showError(ex);
        }
    };

    public void showError(Exception ex) {
        String errString;

        if(ex instanceof DBManager.TriggerSQLException)
            errString = ((DBManager.TriggerSQLException)ex).getTriggerMessage();
        else if(ex instanceof SQLException)
            errString = "SQLException: " + ((SQLException)ex).getMessage() +
                    "\nSQLState: " + ((SQLException)ex).getSQLState() +
                    "\nVendorError: " + ((SQLException)ex).getErrorCode();
        else if(ex instanceof LevelDBManager.LevelDBUnavailableException)
            errString = "LevelDB Exception: " + ex.getMessage();
        else
            errString = "Unexpected Exception: " + ex.getMessage();

        SimpleDialog.showErrorDialog(errString);

        //ex.printStackTrace();
    }

    public void updateQueryInfoLabel() throws SQLException, LevelDBManager.LevelDBUnavailableException {
        CompositeDBManager.QueryExecutor lastQueryExecutor = CompositeDBManager.getInstance().getLastExecutor();
        String prefix = "Last query executed by ";
        switch(lastQueryExecutor) {
            case MySQL:
                queryInfoLabel.setText(prefix + "MySQL database"); break;
            case LevelDB:
                queryInfoLabel.setText(prefix + "LevelDB database"); break;
            case Both:
                queryInfoLabel.setText(prefix + "both MySQL and LevelDB databases"); break;
        }
    }

    public VBox getOuterVbox(){return outerVbox;}
}
