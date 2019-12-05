package main.java.task0.db;

import org.jetbrains.annotations.Nullable;
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
            db = factory.open(new File("database.leveldb"), options);
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

    public String getRegistrationKey(Registration registration) {
        StringBuilder key = new StringBuilder("");
        // Format -> registration:courseId:date:profId:studentid:
        return key.append("registration:")
                .append(registration.getExam().getCourse().getId()).append(":")
                .append(registration.getExam().getId().getDate()).append(":")
                .append(registration.getExam().getCourse().getProfessor().getId()).append(":")
                .append(registration.getStudent().getId()).append(":").toString();
    }

    public void addRegistration(Registration registration) throws LevelDBUnavailableException {
        exceptionIfNotAvailable();

        String key = getRegistrationKey(registration).toString();
        //System.out.println(key);

        putKeyValue(key + "studentname", registration.getStudent().getName());
        putKeyValue(key + "studentsurname", registration.getStudent().getSurname());
        putKeyValue(key + "professorname", registration.getExam().getCourse().getProfessor().getName());
        putKeyValue(key + "professorsurname", registration.getExam().getCourse().getProfessor().getSurname());
        putKeyValue(key + "coursename", registration.getExam().getCourse().getName());
        putKeyValue(key + "coursecfu", registration.getExam().getCourse().getCfu());
        putKeyValue(key + "grade", (registration.getGrade() != null) ? registration.getGrade() : -1);
    }

    public ArrayList<Registration> findRegistrationProfessor(int professorId) throws LevelDBUnavailableException {
        return findRegistrations(reg -> reg.getExam().getCourse().getProfessor().getId() == professorId);
    }

    public ArrayList<Registration> findRegistrationStudent(int studentId, boolean toDo) throws LevelDBUnavailableException {
        if(!toDo)
            return findRegistrations(reg -> reg.getStudent().getId() == studentId && reg.getGrade() != null);

        // Get all registrations which course does not have registrations with grades
        ArrayList<Registration> tempRegs = findRegistrations(reg -> reg.getStudent().getId() == studentId);
        HashSet<Integer> completedCourses = new HashSet<>();
        for(Registration r : tempRegs)
            if(r.getGrade() != null)
                completedCourses.add(r.getExam().getCourse().getId());

        ArrayList<Registration> result = new ArrayList<>();
        for(Registration r : tempRegs)
            if(r.getGrade() == null  && !completedCourses.contains(r.getExam().getCourse().getId()))
                result.add(r);
        return result;
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
                        (courseId != course.getId() || !date.equals(exam.getId().getDate()) || studentId != student.getId())) {
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
                    case "grade" :
                        int grade = Integer.parseInt(storedValue);
                        registration.setGrade((grade != -1) ? grade : null);
                        break;
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

    public void updateRegistration(Registration reg, @Nullable Integer grade) throws LevelDBUnavailableException {
        exceptionIfNotAvailable();

        // this will simply override the registration
        // TODO: check if registration exists
        reg.setGrade(grade);
        addRegistration(reg);
    }

    public void deleteRegistration(int studentId, Exam exam) throws LevelDBUnavailableException {
        exceptionIfNotAvailable();

        Registration reg = new Registration(new Student(studentId, "",""), exam, -1);
        String key = getRegistrationKey(reg);
        db.delete(bytes(key));

        db.delete(bytes(key + "studentname"));
        db.delete(bytes(key + "studentsurname"));
        db.delete(bytes(key + "professorname"));
        db.delete(bytes(key + "professorsurname"));
        db.delete(bytes(key + "coursename"));
        db.delete(bytes(key + "coursecfu"));
        db.delete(bytes(key + "grade"));
    }

    public void insertRegistration(Student student, Exam exam, @Nullable Integer grade) throws LevelDBUnavailableException {
        exceptionIfNotAvailable();

        Registration reg = new Registration(student, exam, grade);
        String key = getRegistrationKey(reg);
        addRegistration(reg);
    }

    private void addDummyEntry(String prefix) {
        putKeyValue(prefix + ":valuename", "value");
    }

    public void clearAll() {
        // Important issue: this leveldb implementation does not have methods to clear all the database values.
        // Manual clearing does not work for the first key-value element, and THERE IS NO WAY TO DELETE IT.
        // So, to avoid storing old references of key-value, we put now a dummy key-value pair that will be always
        // the first
        addDummyEntry("aa-dummy");
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

    public static void test() throws LevelDBUnavailableException {
        LevelDBManager dbman = LevelDBManager.getInstance();

        System.out.println("-> Test 0: clearing db");
        // Clear db
        dbman.dumpAll();
        dbman.clearAll();
        dbman.dumpAll();

        // Test Add Registrations
        System.out.println("-> Test 1: adding registrations");
        Professor professor = new Professor(1, "Professore1", "Professore1Cognome");
        Course c = new Course(1, "Corso brutto", -10, professor);
        Exam e = new Exam(c, Date.valueOf("2019-7-7"));
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
            System.out.println(r.toString());

        System.out.println("-> Printing list for findRegistrationProfessor");
        regList = dbman.findRegistrationProfessor(1);
        for(Registration r : regList)
            System.out.println(r.toString());

        System.out.println("-> Printing list for findRegistrationStudent");
        regList = dbman.findRegistrationStudent(1, false);
        for(Registration r : regList)
            System.out.println(r.toString());

        System.out.println("\n-> Test 2: insertRegistration");
        student.setId(10);
        student.setName("");
        student.setSurname("");
        dbman.insertRegistration(student, e, null);
        for(Registration r : dbman.findRegistrations())
            System.out.println(r.toString());

        System.out.println("\n-> Test 3: updateRegistration");
        dbman.updateRegistration(reg, 27);
        for(Registration r : dbman.findRegistrations())
            System.out.println(r.toString());

        System.out.println("\n-> Test 4: deleteRegistration");
        dbman.deleteRegistration(10, e);
        for(Registration r : dbman.findRegistrations())
            System.out.println(r.toString());
    }

    public static void main(String[] args) throws SQLException, LevelDBUnavailableException {
        LevelDBManager dbman = LevelDBManager.getInstance();

        System.out.println("-> test started");
        test();
        System.out.println("-> test finished successfully");

        dbman.close();
    }

    public static class LevelDBUnavailableException extends Exception {
        public LevelDBUnavailableException(String name) {
            super(name);
        }
    }
}
