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
 * @author adria
 */
@Entity
@Table(name = "professor")
public class Professor {
    private int id;
    private String name;
    private String surname;

    public Professor() {

    }

    public Professor(int id, String name, String surname) {
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

        Professor professor = (Professor)obj;
        return Objects.equals(professor.getId(), this.getId())
                && Objects.equals(professor.getName(), this.getName())
                && Objects.equals(professor.getSurname(), this.getSurname());
    }
}
