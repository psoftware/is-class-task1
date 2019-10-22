package main.java.task0.db;

import main.java.task0.Student;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;

public class LevelDBManager {
    private static LevelDBManager INSTANCE = new LevelDBManager();
    public static LevelDBManager getInstance() {
        return INSTANCE;
    }

    private DB db;

    public LevelDBManager()  {
        try {
            Options options = new Options();
            db = factory.open(new File("../database.leveldb"), options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addStudent(Student student) {
        String studentPrefix = "student:" + student.getId() +":";
        db.put(bytes(studentPrefix + "name"), bytes(student.getName()));
        db.put(bytes(studentPrefix + "surname"), bytes(student.getSurname()));
    }

    public Student getStudent(int studentId) {
        Student student = new Student();
        student.setId(studentId);

        DBIterator keyIterator = db.iterator();
        keyIterator.seek(bytes("student:" + studentId)); // moves the iterator to the keys starting with "employee"

        try {
            while (keyIterator.hasNext()) {

                String key = asString(keyIterator.peekNext().getKey()); // key arrangement : employee:$employee_id:$attribute_name = $value
                String[] keySplit = key.split(":"); // split the key

                int parsedId = Integer.parseInt(keySplit[1]);
                if(parsedId != studentId || !keySplit[0].equals("student"))
                    break;

                String lastAttribute = keySplit[keySplit.length - 1];
                String storedValue = asString(db.get(bytes(key)));

                if(lastAttribute.equals("name"))
                    student.setName(storedValue);
                else if(lastAttribute.equals("surname"))
                    student.setSurname(storedValue);

                keyIterator.next();
            }
        } finally {
            try {
                keyIterator.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return student;
    }

    public static void main(String[] args) {
        LevelDBManager dbman = LevelDBManager.getInstance();

        Student stud = new Student(1, "ciao", "arrivederci");
        dbman.addStudent(stud);
        stud = new Student(2, "ciao2", "arrivederci2");
        dbman.addStudent(stud);
        stud = new Student(3, "ciao3", "arrivederci3");
        dbman.addStudent(stud);

        stud = dbman.getStudent(2);
        System.out.println(stud.getName() + " " + stud.getSurname());
    }
}
