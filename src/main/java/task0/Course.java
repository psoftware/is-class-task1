/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.task0;

import java.util.Objects;
import javax.persistence.*;

/**
 *
 * @author cacomop
 */
@Entity
@Table(name = "course")
public class Course {
    private int id;
    private String name;
    private int cfu;
    private Professor professor;

    public Course() {

    }

    public Course (int id, String name, int cfu, Professor professor){
        this.id = id;
        this.name = name;
        this.cfu = cfu;
        this.professor = professor;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId () {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCfu () {
        return cfu;
    }

    public void setCfu(int cfu) {
        this.cfu = cfu;
    }

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name="professor", referencedColumnName = "id")
    public Professor getProfessor() { return professor; }
    public void setProfessor(Professor professor) { this.professor = professor; }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null || obj.getClass() != this.getClass())
            return false;

        Course course = (Course)obj;
        return Objects.equals(course.getId(), this.getId())
                && Objects.equals(course.getName(), this.getName())
                && Objects.equals(course.getCfu(), this.getCfu())
                && Objects.equals(course.getProfessor(), this.getProfessor());
    }
}
