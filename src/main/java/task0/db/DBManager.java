/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.task0.db;

import com.sun.istack.internal.Nullable;
import main.java.task0.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
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
    public static DBManager getInstance() {
        return INSTANCE;
    }

    private EntityManagerFactory factory;
    private EntityManager entityManager;

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
        try {
            entityManager = factory.createEntityManager();
            Query query = entityManager.createQuery("SELECT c FROM Course c WHERE c.professor.id = :profID");
            query.setParameter("profID", profID);
            resultList = query.getResultList();
        } catch (Exception ex) {
            throw ex;
        } finally {
            entityManager.close();
        }

        return resultList;
    }

    public List<Exam> findExam(int studId) {
        List<Exam> resultList;
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
            entityManager.close();
        }

        return resultList;
    }

    public List<Registration> findRegistrationProfessor(int profId) {
        List<Registration> resultList;
        try {
            entityManager = factory.createEntityManager();
            Query query = entityManager.createQuery("SELECT r FROM Registration r WHERE r.exam.course.professor.id = :profID");
            query.setParameter("profID", profId);
            resultList = query.getResultList();
        } catch (Exception ex) {
            throw ex;
        } finally {
            entityManager.close();
        }

        return resultList;
    }

    public List<Registration> findRegistrationStudent(int studentId, boolean toDo) {
        List<Registration> resultList;
        try {
            entityManager = factory.createEntityManager();
            Query query = entityManager.createQuery("SELECT r FROM Registration r WHERE r.student.id = :studentId AND r.grade IS "
                    + ((toDo) ? "NULL" : "NOT NULL"));
            query.setParameter("studentId", studentId);
            resultList = query.getResultList();
        } catch (Exception ex) {
            throw ex;
        } finally {
            entityManager.close();
        }

        return resultList;
    }

    public void insertExam(int courseID, LocalDate date) {
        try {
            entityManager = factory.createEntityManager();
            Course course = entityManager.getReference(Course.class, courseID);
            Exam exam = new Exam(course, Date.valueOf(date));
            entityManager.getTransaction().begin();
            entityManager.persist(exam);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("A problem occurred inserting an exam!");
            throw ex;
        } finally {
            entityManager.close();
        }
    }

    public void updateRegistration(Registration reg, int grade) {
        reg.setGrade(grade);

        try {
            entityManager = factory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.merge(reg);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("A problem occurred inserting a registration!");
            throw ex;
        } finally {
            entityManager.close();
        }
    }

    public void deleteRegistration(int studentId, Exam exam) {
        try {
            entityManager = factory.createEntityManager();
            //Student student = entityManager.getReference(Student.class, studentId);
            entityManager.getTransaction().begin();
            // Delete
            Query query = entityManager.createQuery("DELETE FROM Registration r WHERE r.exam = :exam AND r.student.id = :studId");
            query.setParameter("exam", exam);
            query.setParameter("studId", studentId);
            query.executeUpdate();
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("A problem occurred inserting a registration!");
            throw ex;
        } finally {
            entityManager.close();
        }
    }

    // L'ho fatta io e non l'ho copiata dal codice commentato
    public void insertRegistration(int studentId, Exam examDetached,  @Nullable Integer grade) {
        try {
            entityManager = factory.createEntityManager();

            Student student = entityManager.getReference(Student.class, studentId);
            Exam exam = entityManager.getReference(Exam.class, examDetached.getId());
            Registration registration = new Registration(student, exam, grade);

            entityManager.getTransaction().begin();
            entityManager.persist(registration);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("A problem occurred inserting a registration!");
            throw ex;
        } finally {
            entityManager.close();
        }
    }
}
