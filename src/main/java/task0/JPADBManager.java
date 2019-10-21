package main.java.task0;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.security.spec.ECField;
import java.util.List;

public class JPADBManager {
    private EntityManagerFactory factory;
    private EntityManager entityManager;

    public void setup() {
        factory = Persistence.createEntityManagerFactory("Task0");

    }

    public void exit() {
        factory.close();
    }

    public List readRegistrationForStudent(int studentId) {
        List<Registration> resultList;
        try {
            EntityManager entityManager = factory.createEntityManager();
            Query query = entityManager.createQuery("SELECT r FROM Registration r WHERE r.student.id = :studentId");
            query.setParameter("studentId", studentId);
            resultList = query.getResultList();
            resultList.forEach(System.out::println);
        } catch (Exception ex) {
            throw ex;
        } finally {
            entityManager.close();
        }

        return resultList;
    }

    public void insertRegistration(Exam exam, Student student) {
        System.out.println("Creating a new Book");

        Registration registration = new Registration();
        registration.setExam(exam);
        registration.setStudent(student);

        try {
            entityManager = factory.createEntityManager();
            entityManager.getTransaction().begin();
            // Insert
            entityManager.persist(exam);
            // Update
            // entityManager.merge(exam);
            // Delete
            // entityManager.remove(exam);
            entityManager.getTransaction().commit();
            System.out.println("Registration Added");

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("A problem occurred inserting a registration!");
        } finally {
            entityManager.close();
        }
    }

    public static void main(String[] args) {
        JPADBManager manager = new JPADBManager();
        manager.setup();

        manager.readRegistrationForStudent(1);

        manager.exit();
        System.out.println("Finished");
    }
}
