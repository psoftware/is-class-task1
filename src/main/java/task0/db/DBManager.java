/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.task0.db;

import org.jetbrains.annotations.Nullable;
import main.java.task0.*;

import javax.persistence.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author adria
 */
public class DBManager {
    private static DBManager INSTANCE = new DBManager();
    private static DBManager.Transactions TRANS_INSTANCE = getInstance().new Transactions();
    public static DBManager getInstance() {
        return INSTANCE;
    }

    public static DBManager.Transactions transactions() {
        return TRANS_INSTANCE;
    }

    private EntityManagerFactory factory;

    public class Transactions {
        // EntityManager is shared through Transaction method calls
        private EntityManager entityManager;
        public void startTransaction() {
            entityManager = factory.createEntityManager();
            entityManager.getTransaction().begin();
        }

        public void flushTransaction() {
            entityManager.flush();
        }

        public void commitTransaction() {
            entityManager.getTransaction().commit();
            entityManager.close();
        }

        public void rollbackTransaction() {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }

        public Student findStudent(int studentId) throws SQLException {
            try {
                Student student = entityManager.getReference(Student.class, studentId);
                return student;
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("A problem occurred finding a student!");
                throw ex;
            }
        }

        public void updateRegistration(Registration reg, int grade) {
            reg.setGrade(grade);

            try {
                entityManager.merge(reg);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("A problem occurred inserting a registration!");
                throw ex;
            }
        }

        public void deleteRegistration(int studentId, Exam exam) {
            try {
                // Delete
                Query query = entityManager.createQuery("DELETE FROM Registration r WHERE r.exam = :exam AND r.student.id = :studId");
                query.setParameter("exam", exam);
                query.setParameter("studId", studentId);
                query.executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("A problem occurred inserting a registration!");
                throw ex;
            }
        }

        public void insertRegistration(int studentId, Exam examDetached,  @Nullable Integer grade) {
            try {
                Student student = entityManager.getReference(Student.class, studentId);
                Exam exam = entityManager.getReference(Exam.class, examDetached.getId());
                Registration registration = new Registration(student, exam, grade);

                entityManager.persist(registration);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("A problem occurred inserting a registration!");
                throw ex;
            }
        }
    }

    public void setup() {
        factory = Persistence.createEntityManagerFactory("Task0");

    }

    public void exit() {
        factory.close();
    }

    public DBManager() {
        setup();
    }

    public static void main(String[] args) {
        DBManager manager = new DBManager();

        for(Course c : manager.findCourse(1))
            System.out.println("Course ("+c.getCfu()+")" + c.getName() + " of Professor " + c.getProfessor().getName() + " " + c.getProfessor().getSurname());

        for(Exam e : manager.findExam(1))
            System.out.println(e.getId().getDate() + " " + e.getCourse().getName());

        for(Registration r : manager.findRegistrationProfessor(1))
            System.out.println(r.getExam().getId().getDate().toString() + " " + r.getExam().getCourse().getName() + ": " + r.getGrade());

        for(Registration r : manager.findRegistrationStudent(1, true))
            System.out.println(r.getExam().getId().getDate().toString() + " " + r.getExam().getCourse().getName() + ": " + r.getGrade());
        for(Registration r : manager.findRegistrationStudent(1, false))
            System.out.println(r.getExam().getId().getDate().toString() + " " + r.getExam().getCourse().getName() + ": " + r.getGrade());

        manager.exit();
        System.out.println("Finished");
    }

    public List<Course> findCourse(int profID) {
        List<Course> resultList;
        EntityManager entityManager = null;
        try {
            entityManager = factory.createEntityManager();
            Query query = entityManager.createQuery("SELECT c FROM Course c WHERE c.professor.id = :profID");
            query.setParameter("profID", profID);
            resultList = query.getResultList();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if(entityManager != null)
                entityManager.close();
        }

        return resultList;
    }

    public List<Exam> findExam(int studId) {
        List<Exam> resultList;
        EntityManager entityManager = null;
        try {
            entityManager = factory.createEntityManager();
            Query query = entityManager.createQuery("SELECT e FROM Exam e WHERE (" +
                    "SELECT count(r) FROM Registration r " +
                    "WHERE r.student.id = :studId AND r.exam.id = e.id OR (r.exam.course = e.course AND r.grade IS NOT NULL) " +
                    ") = 0");
            query.setParameter("studId", studId);
            resultList = query.getResultList();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if(entityManager != null)
                entityManager.close();
        }

        return resultList;
    }

    public List<Registration> findRegistrations() throws SQLException {
        List<Registration> resultList;
        EntityManager entityManager = null;
        try {
            entityManager = factory.createEntityManager();
            Query query = entityManager.createQuery("SELECT r FROM Registration r");
            resultList = query.getResultList();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if(entityManager != null)
                entityManager.close();
        }

        return resultList;
    }

    public List<Registration> findRegistrationProfessor(int profId) {
        List<Registration> resultList;
        EntityManager entityManager = null;
        try {
            entityManager = factory.createEntityManager();
            Query query = entityManager.createQuery("SELECT r FROM Registration r WHERE r.exam.course.professor.id = :profID");
            query.setParameter("profID", profId);
            resultList = query.getResultList();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if(entityManager != null)
                entityManager.close();
        }

        return resultList;
    }

    public List<Registration> findRegistrationStudent(int studentId, boolean toDo) {
        List<Registration> resultList;
        EntityManager entityManager = null;
        try {
            entityManager = factory.createEntityManager();
            String sql = "SELECT r FROM Registration r WHERE r.student.id = :studentId AND r.grade IS ";
            if(toDo)
                sql += "NULL AND (" +
                        "SELECT count(r1) FROM Registration r1 " +
                            "WHERE r.student = r1.student AND r.exam.course = r1.exam.course and r1.grade IS NOT NULL) = 0";
            else
                sql += "NOT NULL";
            Query query = entityManager.createQuery(sql);
            query.setParameter("studentId", studentId);
            resultList = query.getResultList();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if(entityManager != null)
                entityManager.close();
        }

        return resultList;
    }

    public void insertExam(int courseID, LocalDate date) {
        EntityManager entityManager = null;
        try {
            entityManager = factory.createEntityManager();
            entityManager.getTransaction().begin();
            Course course = entityManager.getReference(Course.class, courseID);
            Exam exam = new Exam(course, Date.valueOf(date));
            entityManager.persist(exam);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("A problem occurred inserting an exam!");
            throw ex;
        } finally {
            if(entityManager != null)
                entityManager.close();
        }
    }
}
