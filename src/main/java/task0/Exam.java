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

        @Override
        public boolean equals(Object obj) {
            if(obj == null || obj.getClass() != this.getClass())
                return false;

            ExamID examobj = (ExamID)obj;
            return getDate().equals(examobj.date) && courseId == examobj.getCourseId();
        }

        //TODO: implement hashcode (is it really necessary?)
        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    private ExamID id;
    @EmbeddedId
    public ExamID getId() {
        return id;
    }
    public void setId(ExamID id) {
        this.id = id;
    }

    // ===== Chiave =====
    private Course course;

    @MapsId("courseId") // TODO: verificare correttezza
    @JoinColumn(name="course", referencedColumnName="id")
    @ManyToOne
    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) { this.course = course;}

    public Exam() {}
    public Exam(Course course, Date date) {
        this.id = new ExamID(date, course.getId());
    }
}
