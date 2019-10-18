package main.java.task0.gui;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.task0.Course;

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
        this.setTableExams();
       
    }
    
    public void setTableExams(){//02
        TableColumn course = new TableColumn("Course");
        TableColumn date = new TableColumn("Date");
        TableColumn action = new TableColumn("Action");
        course.setCellValueFactory(new PropertyValueFactory("course"));
        date.setCellValueFactory(new PropertyValueFactory("date"));
        //action.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        action.setCellFactory(TableCellButton.<Course>forTableColumn("Remove", (Course p) -> {
            //table.getItems().remove(p);
            return p;
        }));

        listaOsservabile = FXCollections.observableArrayList();
        setItems(listaOsservabile);
        getColumns().clear();
        getColumns().addAll(course, date, action);
    }
    
    public void setTableCourses(){//02
        TableColumn id = new TableColumn("ID");
        TableColumn name = new TableColumn("Name");
        TableColumn cfu = new TableColumn("CFU");
        id.setCellValueFactory(new PropertyValueFactory("id"));
        name.setCellValueFactory(new PropertyValueFactory("name"));
        cfu.setCellValueFactory(new PropertyValueFactory("cfu"));
        
        listaOsservabile = FXCollections.observableArrayList();
        setItems(listaOsservabile);
        getColumns().clear();
        getColumns().addAll(id, name, cfu);
    }
    
     public void setTableExamResults(){//02
        TableColumn student = new TableColumn("Student");
        TableColumn course = new TableColumn("Course");
        TableColumn date = new TableColumn("Date");
        TableColumn grade = new TableColumn("Grade");
        student.setCellValueFactory(new PropertyValueFactory("student"));
        course.setCellValueFactory(new PropertyValueFactory("course"));
        date.setCellValueFactory(new PropertyValueFactory("date"));
        grade.setCellValueFactory(new PropertyValueFactory("grade"));
        
        listaOsservabile = FXCollections.observableArrayList();
        setItems(listaOsservabile);
        getColumns().clear();
        getColumns().addAll(student, course, date, grade);
    }
    
    public void update(List lista){
        listaOsservabile.clear();
        listaOsservabile.addAll(lista);
    }
    
    public void clear(){listaOsservabile.clear();}
    
    public Course getSelectedCourse(){ return (Course) this.getSelectionModel().getSelectedItem(); }

    
}