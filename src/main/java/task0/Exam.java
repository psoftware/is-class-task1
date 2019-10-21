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
@Table(name = "exam")
public class Exam {

    @Embeddable
    public static class ExamID implements Serializable {
        private Date date;
        private int courseId;

        public ExamID() {}
        public ExamID(Date date, int courseId) {
            this.date = date;
            this.courseId = courseId;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public int getCourseId() {
            return courseId;
        }

        public void setCourseId(int courseId) {
            this.courseId = courseId;
        }

        //TODO: implement hash and equals (is it really necessary?)
        @Override
        public boolean equals(Object obj) {
            if(obj == null || obj.getClass() != this.getClass())
                return false;

            ExamID examobj = (ExamID)obj;
            return getDate().equals(examobj.date) && courseId == examobj.getCourseId();
        }
    }

    @EmbeddedId private ExamID id;

    public Exam() {}
    /*public Exam(Course course, Date date) {
        this.id = new ExamID(date, course);
        this.id.setDate(date);
        this.id.setCourse(course);
    }*/

    // ===== Chiave =====
    private Course course;

    @MapsId("courseId") // TODO: verificare correttezza
    @JoinColumn(name="course", referencedColumnName="id")
    @ManyToOne(targetEntity = Course.class)
    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) { this.course = course;}


    // ===== Date =====
    private Date date;

    @Column(name = "date")
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) { this.date = date;} // TODO: perchè non è usato?
}
