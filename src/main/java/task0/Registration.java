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
        private int student;
        private Exam.ExamID exam;

        public RegistrationId() {}

        public int getStudent() {
            return student;
        }
        public void setStudent(int student) {
            this.student = student;
        }

        public Exam.ExamID getExam() {
            return this.exam;
        }
        public void setExam(Exam.ExamID exam) {
            this.exam = exam;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null || obj.getClass() != this.getClass())
                return false;

            RegistrationId regobj = (RegistrationId)obj;
            return student == regobj.getStudent() && exam.equals(regobj.getExam());
        }

        //TODO: implement hashcode (is it really necessary?)
        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }




    public Registration () {}

    /*public Registration (Student student, Exam exam, int grade) {
        this.exam = exam;
        this.grade = grade;
        this.student = student;
        this.id = new RegistrationId(student, exam);
    }*/

    private RegistrationId id;
    @EmbeddedId
    public RegistrationId getId() {
        return id;
    }
    public void setId(RegistrationId id) {
        this.id = id;
    }

    // ===== Key fields =====
    private Student student;

    @MapsId("student") // TODO: verificare correttezza
    @JoinColumn(name="student", referencedColumnName="id")
    @ManyToOne
    public Student getStudent () {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
    }


    private Exam exam;

    @MapsId("exam") // TODO: verificare correttezza
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
    private Integer grade;
    @Column(name = "grade")
    public Integer getGrade() {
        return grade;
    }
    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}
