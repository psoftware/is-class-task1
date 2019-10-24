package main.java.task0.db;

import main.java.task0.*;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;

public class LevelDBManager {
    private static LevelDBManager INSTANCE = new LevelDBManager();
    public static LevelDBManager getInstance() {
        return INSTANCE;
    }

    private DB db;
    private boolean isAvailable;

    public LevelDBManager()  {
        try {
            Options options = new Options();
            db = factory.open(new File("../database.leveldb"), options);
            setAvailability(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailability(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    private void exceptionIfNotAvailable() throws LevelDBUnavailableException {
        if(!isAvailable)
            throw new LevelDBUnavailableException("LevelDB is unavailable (simulated)");
    }

    private void putKeyValue(String key, String value) {
        db.put(bytes(key), bytes(value));
    }

    private void putKeyValue(String key, int value) {
        db.put(bytes(key), bytes(Integer.toString(value)));
    }

    public void addStudent(Student student) throws LevelDBUnavailableException {
        exceptionIfNotAvailable();

        String studentPrefix = "student:" + student.getId() +":";
        putKeyValue(studentPrefix + "name", student.getName());
        putKeyValue(studentPrefix + "surname", student.getSurname());
    }

    public void addRegistration(Registration registration) throws LevelDBUnavailableException {
        exceptionIfNotAvailable();

        StringBuilder key = new StringBuilder("");
        // Format -> registration:studentid:courseid:date:
        // Format -> registration:courseId:date:profId:studentid:
        key.append("registration:")
                .append(registration.getExam().getCourse().getId()).append(":")
                .append(registration.getExam().getDate()).append(":")
                .append(registration.getExam().getCourse().getProfessor().getId()).append(":")
                .append(registration.getStudent().getId()).append(":");
        System.out.println(key);

        putKeyValue(key.toString() + "studentname", registration.getStudent().getName());
        putKeyValue(key.toString() + "studentsurname", registration.getStudent().getSurname());
        putKeyValue(key.toString() + "professorname", registration.getExam().getCourse().getProfessor().getName());
        putKeyValue(key.toString() + "professorsurname", registration.getExam().getCourse().getProfessor().getSurname());
        putKeyValue(key.toString() + "coursename", registration.getExam().getCourse().getName());
        putKeyValue(key.toString() + "coursecfu", registration.getExam().getCourse().getCfu());
        putKeyValue(key.toString() + "grade", registration.getGrade());
    }

    public ArrayList<Registration> findRegistrationProfessor(int professorId) throws LevelDBUnavailableException {
        return findRegistrations(reg -> reg.getExam().getCourse().getProfessor().getId() == professorId);
    }

    public ArrayList<Registration> findRegistrationStudent(int studentId) throws LevelDBUnavailableException {
        return findRegistrations(reg -> reg.getStudent().getId() == studentId);
    }

    // add registration to registration list only if filters returns true
    private void conditionalAdd(ArrayList<Registration> registrations, Registration registration,
                               Function<Registration, Boolean> filter) {
        if(filter.apply(registration))
            registrations.add(registration);
    }

    public ArrayList<Registration> findRegistrations() throws LevelDBUnavailableException {
        return findRegistrations(reg -> true);
    }

    public ArrayList<Registration> findRegistrations(Function<Registration, Boolean> filter) throws LevelDBUnavailableException {
        exceptionIfNotAvailable();

        // Result list
        ArrayList<Registration> registrationList = new ArrayList<>();

        // New registration instance
        Student student = null;
        Professor professor = null;
        Course course = null;
        Exam exam = null;
        Registration registration = null;

        DBIterator keyIterator = db.iterator();
        keyIterator.seek(bytes("registration:"));

        try {
            while (keyIterator.hasNext()) {
                // Key parsing
                // registration:courseId:date:profId:studentid:
                String key = asString(keyIterator.next().getKey());
                String[] keySplit = key.split(":"); // split the key

                int keySplitId = 0;
                String prefix = keySplit[keySplitId++];
                if(!prefix.equals("registration")) {
                    // we must save the latest registration object! (if there is one)
                    if(registration != null)
                        conditionalAdd(registrationList, registration, filter);
                    return registrationList;
                }

                if(keySplit.length != 6)
                    throw new IllegalStateException("Student key string format is incorrect");

                int courseId = Integer.parseInt(keySplit[keySplitId++]);
                Date date = Date.valueOf(keySplit[keySplitId++]);
                int profId = Integer.parseInt(keySplit[keySplitId++]);
                int studentId = Integer.parseInt(keySplit[keySplitId++]);
                String attributeName = keySplit[keySplitId++];

                String storedValue = asString(db.get(bytes(key)));

                // if key changed (or it's the first registration)...
                // (remember that db entries are ordered by key)
                if(registration == null ||
                        (courseId != course.getId() || !date.equals(exam.getDate()) || studentId != student.getId())) {
                    // we must save the last registration instance (if it is not the first registration)
                    if(registration != null)
                        conditionalAdd(registrationList, registration, filter);

                    // we must create another instance of registration (and all the linked objects...)
                    student = new Student(studentId, null, null);
                    professor = new Professor(profId, null, null);
                    course = new Course(courseId,null, -1, professor);
                    exam = new Exam(course, date);
                    registration = new Registration(student, exam, -1);
                }

                switch(attributeName) {
                    case "studentname": student.setName(storedValue); break;
                    case "studentsurname": student.setSurname(storedValue); break;
                    case "professorname": professor.setName(storedValue); break;
                    case "professorsurname": professor.setSurname(storedValue); break;
                    case "coursename": course.setName(storedValue); break;
                    case "coursecfu": course.setCfu(Integer.parseInt(storedValue)); break;
                    case "grade" : registration.setGrade(Integer.parseInt(storedValue)); break;
                }
            }
        } finally {
            try {
                keyIterator.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // If we are here is because last entry of the database had "registration" prefix, so
        // we must save the latest registration object! (if there is one)
        if(registration != null)
            conditionalAdd(registrationList, registration, filter);

        return registrationList;
    }

    public void addExam(Exam exam) throws LevelDBUnavailableException {
        exceptionIfNotAvailable();

        StringBuilder key = new StringBuilder("");
        // Format -> exam:courseid:date:
        key.append("exam:")
                .append(exam.getCourse().getId()).append(":")
                .append(exam.getDate()).append(":");

        putKeyValue(key.toString(), "");
    }

    public Student getStudent(int studentId) throws LevelDBUnavailableException {
        exceptionIfNotAvailable();

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

    private static Consumer<Registration> printRegistration = r -> {
        Student student = r.getStudent();
        Exam exam = r.getExam();
        Course course = exam.getCourse();
        Professor prof = course.getProfessor();

        System.out.println("Student: " + student.getId() + " " + student.getName() + " " + student.getSurname() + " "
                + "Professor: " + prof.getId() + " " + prof.getName() + " " + prof.getSurname() + " "
                + "Exam: " + exam.getDate() + " "
                + "Course: " + course.getId() + " "+ course.getName() + " " + course.getCfu());
    };

    public static boolean listDeepEqual(ArrayList<Registration> list1, ArrayList<Registration> list2) {
        //System.out.println("Deepequal debug:");
        if(list1.size() != list2.size())
            return false;

        HashSet<Integer> pickedFormList2 = new HashSet<>();

        // THIS ALGORITHM is O(n^2) and could be optimized with hashtables.
        // However we didn't implement any hashcode method
        for(int i=0; i<list1.size(); i++) {
            //System.out.println("Element " + i +":");
            //printRegistration.accept(list1.get(i));
            //System.out.println("v");
            boolean found = false;
            for(int j=0; j<list2.size(); j++) {
                //printRegistration.accept(list2.get(j));
                if (!pickedFormList2.contains(j) && list1.get(i).equals(list2.get(j))) {
                    found = true;
                    pickedFormList2.add(j);
                    break;
                }
            }

            if(!found)
                return false;
        }

        return true;
    }

    public static void importFromMysql() throws SQLException, LevelDBUnavailableException {
        LevelDBManager dbman = LevelDBManager.getInstance();
        dbman.clearAll();

        ArrayList<Registration> mysqlRegistrations = DBManager.getInstance().findRegistrations();
        for(Registration r : mysqlRegistrations)
            dbman.addRegistration(r);
        dbman.dumpAll();

        // assert
        if(!isConsistent())
            throw new IllegalStateException("LevelDB registration list does not correspond to MySQL one");
    }

    public static boolean isConsistent() throws SQLException, LevelDBUnavailableException {
        return listDeepEqual(DBManager.getInstance().findRegistrations(), LevelDBManager.getInstance().findRegistrations());
    }

    public static void test() throws LevelDBUnavailableException {
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
        Professor professor = new Professor(1, "Professore1", "Professore1Cognome");
        Course c = new Course(1, "Corso brutto", -10, professor);
        Exam e = new Exam(c, Date.valueOf("2019-7-7"));
        dbman.addExam(e);

        // Test Add Exam
        Student student = new Student(1,"Antonio", "Le Caldare");
        Registration reg = new Registration(student, e, 10);
        dbman.addRegistration(reg);

        student = new Student(2,"Antonio2", "Le Caldare2");
        reg = new Registration(student, e, 11);
        dbman.addRegistration(reg);

        student = new Student(3,"Antonio3", "Le Caldare3");
        professor = new Professor(2, "Professore2", "Professore2Cognome");
        c = new Course(1, "Corso brutto del Prof2", -10, professor);
        e = new Exam(c, Date.valueOf("2018-8-8"));
        reg = new Registration(student, e, 33);
        dbman.addRegistration(reg);

        dbman.dumpAll();

        System.out.println("-> Printing list for findRegistrations");
        List<Registration> regList = dbman.findRegistrations();
        for(Registration r : regList)
            printRegistration.accept(r);

        System.out.println("-> Printing list for findRegistrationProfessor");
        regList = dbman.findRegistrationProfessor(1);
        for(Registration r : regList)
            printRegistration.accept(r);

        System.out.println("-> Printing list for findRegistrationStudent");
        regList = dbman.findRegistrationStudent(1);
        for(Registration r : regList)
            printRegistration.accept(r);

        //stud = dbman.getStudent(2);
        //System.out.println(stud.getName() + " " + stud.getSurname());
    }

    public static void main(String[] args) throws SQLException, LevelDBUnavailableException {
        LevelDBManager dbman = LevelDBManager.getInstance();

        System.out.println("-> test started");
        test();
        System.out.println("-> test finished successfully");

        System.out.println("-> importFromMysql started");
        importFromMysql();
        System.out.println("-> importFromMysql finished successfully");

        dbman.close();
    }

    public static class LevelDBUnavailableException extends Exception {
        public LevelDBUnavailableException(String name) {
            super(name);
        }
    }
}
