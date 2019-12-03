/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.task0;

import java.util.function.Supplier;
import java.util.Objects;
import javax.persistence.*;

/**
 *
 * @author adria
 */
@Entity
@Table(name = "student")
public class Student {
    private int id;
    private String name;
    private String surname;

    public Student() {}

    public Student(int id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null || obj.getClass() != this.getClass())
            return false;

        Student student = (Student)obj;
        return Objects.equals(student.getId(), this.getId())
                && Objects.equals(student.getName(), this.getName())
                && Objects.equals(student.getSurname(), this.getSurname());
    }
}