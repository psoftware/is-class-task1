/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.task0;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 *
 * @author cacomop
 */
@Entity
@Table(name = "exam_result")
public class Registration {

    @Embeddable
    public static class RegistrationId implements Serializable {
        private int studentId;
        private Exam.ExamID exam;

        public RegistrationId() {}

        public int getStudentId() {
            return studentId;
        }

        public void setStudentId(int studentId) {
            this.studentId = studentId;
        }

        public Exam.ExamID getExamId() {
            return this.exam;
        }

        public void setExamId(Exam.ExamID examId) {
            this.exam = exam;
        }
    }


    @EmbeddedId
    private RegistrationId id;

    public Registration () {}

    /*public Registration (Student student, Exam exam, int grade) {
        this.exam = exam;
        this.grade = grade;
        this.student = student;
        this.id = new RegistrationId(student, exam);
    }*/


    // ===== Key fields =====
    private Student student;

    @MapsId("studentId") // TODO: verificare correttezza
    @JoinColumn(name="student", referencedColumnName="id")
    @ManyToOne
    public Student getStudent () {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
    }


    private Exam exam;

    @MapsId("examId") // TODO: verificare correttezza
    // il join tra Registration e Exam va fatto su due campi contemporaneamente:
    // exam_result.course = exam.course AND exam_result.date = exam.date
    @JoinColumns({
            @JoinColumn(name="course", referencedColumnName="course"),
            @JoinColumn(name="date", referencedColumnName="date")
    })
    @ManyToOne
    public Exam getExam() {
        return exam;
    }
    public void setExam(Exam exam) {
        this.exam = exam;
    }

    // ===== Additional fields =====
    private int grade;
    @Column(name = "grade")
    public int getGrade() {
        return grade;
    }
}
