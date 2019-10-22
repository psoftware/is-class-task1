package main.java.task0.db;

import main.java.task0.*;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;
import java.sql.Date;

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

    private void putKeyValue(String key, String value) {
        db.put(bytes(key), bytes(value));
    }

    private void putKeyValue(String key, int value) {
        db.put(bytes(key), bytes(Integer.toString(value)));
    }

    public void addStudent(Student student) {
        String studentPrefix = "student:" + student.getId() +":";
        putKeyValue(studentPrefix + "name", student.getName());
        putKeyValue(studentPrefix + "surname", student.getSurname());
    }

    public void addRegistration(Registration registration) {
        StringBuilder key = new StringBuilder("");
        // Format -> registration:studentid:courseid:date:
        key.append("registration:")
                .append(registration.getStudent().getId()).append(":")
                .append(registration.getExam().getCourse().getId()).append(":")
                .append(registration.getExam().getDate()).append(":");

        putKeyValue(key.toString() + "grade", registration.getGrade());
    }

    public void addExam(Exam exam) {
        StringBuilder key = new StringBuilder("");
        // Format -> exam:courseid:date:
        key.append("exam:")
                .append(exam.getCourse().getId()).append(":")
                .append(exam.getDate()).append(":");

        putKeyValue(key.toString(), "");
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

    public void clearAll() {
        // TODO: not working, the first entry remains always
        db.forEach(entry -> db.delete(entry.getKey()));
    }

    public void dumpAll() {
        System.out.println("Dumping all database values:");
        DBIterator keyIterator = db.iterator();
        while(keyIterator.hasNext()) {
            String key = asString(keyIterator.peekNext().getKey());
            String storedValue = asString(db.get(bytes(key)));
            System.out.println(key + " = " + storedValue);
            keyIterator.next();
        }
        System.out.println("Dump end.");
    }

    public void close() {
        try {
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LevelDBManager dbman = LevelDBManager.getInstance();

        // Clear db
        dbman.clearAll();
        dbman.dumpAll();

        // Test Add Student
        Student stud = new Student(1, "ciao", "arrivederci");
        dbman.addStudent(stud);
        stud = new Student(2, "ciao2", "arrivederci2");
        dbman.addStudent(stud);
        stud = new Student(3, "ciao3", "arrivederci3");
        dbman.addStudent(stud);

        // Test Add Exam
        Professor professor = new Professor(1, "Tizio", "CognomeTizio");
        Course c = new Course(1, "Corso brutto", -10, professor);
        Exam e = new Exam(c, Date.valueOf("2019-7-7"));
        dbman.addExam(e);

        // Test Add Exam
        Student student = new Student(1,"Antonio", "Le Caldare");
        Registration reg = new Registration(student, e, 10);
        dbman.addRegistration(reg);

        dbman.dumpAll();

        stud = dbman.getStudent(2);
        System.out.println(stud.getName() + " " + stud.getSurname());

        dbman.close();
    }
}
