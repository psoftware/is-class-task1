package main.java.task0.gui;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import main.java.task0.Course;
import main.java.task0.Exam;
import main.java.task0.Registration;

public class Table  extends TableView {
    private final double lunghezzaTabella; //01
    private final double larghezzaTabella; //01
    private final String font; //01
    private ObservableList listaOsservabile;
    
    public Table(){
        lunghezzaTabella = 350;
        larghezzaTabella = 600;
        font = "Arial";
   
        this.setStyle(".table-column {\n" +
                      "  -fx-alignment: CENTER;\n" +
                      "};\n" +
                      "-fx-font: 12px "+ font);
        this.setMaxHeight(lunghezzaTabella);
        this.setMaxWidth(larghezzaTabella);
        this.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        this.setTableExams("", p->{});
       
    }

    /*
    public void setTable(List<Pair<String,String>> columnList) {
        listaOsservabile = FXCollections.observableArrayList();
        setItems(listaOsservabile);
        getColumns().clear();

        for(Pair<String,String> pair : columnList) {
            TableColumn column = new TableColumn(pair.getKey());
            column.setCellValueFactory(new PropertyValueFactory(pair.getValue()));
            getColumns().add(column);
        }
    }*/

    public static class MappedTableColumn<T>  {
        public static <T> Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>> build(
                Callback<T, String> stringFun) {
            return column -> new ReadOnlyObjectWrapper<String>(stringFun.call(column.getValue()));
        }

        public static <T> Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>> build(
                Callback<T, String> stringFun, String onNull) {
            return column -> {
                String lamdaRes = stringFun.call(column.getValue());
                return new ReadOnlyObjectWrapper<String>(lamdaRes!=null ? lamdaRes : onNull);
            };
        }
    }
    
    public void setTableExams(String actionName, @Nullable Consumer<Exam> callback){//02
        getColumns().clear();

        TableColumn course = new TableColumn("Course");
        TableColumn date = new TableColumn("Date");
        course.setCellValueFactory(MappedTableColumn.<Exam>build(p -> p.getCourse().getName()));
        date.setCellValueFactory((MappedTableColumn.<Exam>build(p -> p.getDate().toString())));
        getColumns().addAll(course, date);

        if(callback != null) {
            TableColumn action = new TableColumn("Action");
            action.setCellFactory(TableCellButton.<Exam>forTableColumn(actionName, (Exam p) -> {
                //table.getItems().remove(p);
                callback.accept(p);
                return p;
            }));
            getColumns().add(action);
        }

        listaOsservabile = FXCollections.observableArrayList();
        setItems(listaOsservabile);
    }
    
    public void setTableCourses(String actionName, @Nullable Consumer<Course> callback){//02
        getColumns().clear();

        TableColumn id = new TableColumn("ID");
        TableColumn name = new TableColumn("Name");
        TableColumn cfu = new TableColumn("CFU");
        id.setCellValueFactory(MappedTableColumn.<Course>build(p -> Integer.toString(p.getId())));
        name.setCellValueFactory(MappedTableColumn.<Course>build(Course::getName));
        cfu.setCellValueFactory(MappedTableColumn.<Course>build(p -> Integer.toString(p.getCfu())));
        getColumns().addAll(id, name, cfu);

        if(callback != null) {
            TableColumn action = new TableColumn("Action");
            action.setCellFactory(TableCellButton.<Course>forTableColumn(actionName, (Course p) -> {
                //table.getItems().remove(p);
                callback.accept(p);
                return p;
            }));
            getColumns().add(action);
        }
        
        listaOsservabile = FXCollections.observableArrayList();
        setItems(listaOsservabile);
    }
    
     public void setTableExamResults(String actionName, boolean showStudentId, @Nullable Consumer<Registration> callback){//02
         getColumns().clear();

        if(showStudentId) {
            TableColumn student = new TableColumn("Student");
            student.setCellValueFactory(MappedTableColumn.<Registration>build(p -> Integer.toString(p.getStudent().getId())));
            getColumns().add(student);
        }

        TableColumn course = new TableColumn("Course");
        TableColumn date = new TableColumn("Date");
        TableColumn grade = new TableColumn("Grade");

        course.setCellValueFactory(MappedTableColumn.<Registration>build(p -> p.getExam().getCourse().getName()));
        date.setCellValueFactory((MappedTableColumn.<Registration>build(p -> p.getExam().getDate().toString())));
        grade.setCellValueFactory(MappedTableColumn.<Registration>build(p -> (p.getGrade() != null) ? Integer.toString(p.getGrade()) : ""));
        getColumns().addAll(course, date, grade);

        if(callback != null) {
            TableColumn action = new TableColumn("Action");
            action.setCellFactory(TableCellButton.<Registration>forTableColumn(actionName, (Registration p) -> {
                //table.getItems().remove(p);
                callback.accept(p);
                return p;
            }));
            getColumns().add(action);
        }
        
        listaOsservabile = FXCollections.observableArrayList();
        setItems(listaOsservabile);
    }
    
    public void update(List lista){
        listaOsservabile.clear();
        listaOsservabile.addAll(lista);
    }
    
    public void clear(){listaOsservabile.clear();}
    
    public Course getSelectedCourse(){ return (Course) this.getSelectionModel().getSelectedItem(); }

    
}