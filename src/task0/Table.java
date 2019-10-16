package task0;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
import javafx.scene.control.cell.PropertyValueFactory;

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
        course.setCellValueFactory(new PropertyValueFactory("course"));
        date.setCellValueFactory(new PropertyValueFactory("date"));;
        
        listaOsservabile = FXCollections.observableArrayList();
        setItems(listaOsservabile);
        getColumns().clear();
        getColumns().addAll(course, date);
    }
    
    public void setTableCourses(){//02
        TableColumn id = new TableColumn("ID");
        TableColumn name = new TableColumn("Name");
        TableColumn cfu = new TableColumn("CFU");
        TableColumn professor = new TableColumn("Professor");
        id.setCellValueFactory(new PropertyValueFactory("id"));
        name.setCellValueFactory(new PropertyValueFactory("name"));
        cfu.setCellValueFactory(new PropertyValueFactory("cfu"));
        professor.setCellValueFactory(new PropertyValueFactory("professor"));
        
        listaOsservabile = FXCollections.observableArrayList();
        setItems(listaOsservabile);
        getColumns().clear();
        getColumns().addAll(id, name, cfu, professor);
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
    
    public void setSelectedRow(int i){this.getSelectionModel().select(i);}
    
    public int getSelectedRow(){ return this.getSelectionModel().getSelectedIndex();}
    
}