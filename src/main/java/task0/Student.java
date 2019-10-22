/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.task0;

import java.util.function.Supplier;

/**
 *
 * @author adria
 */
public class Student {
    int id;
    String name;
    String surname;

    public Student() {}

    public Student(int id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

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

    public static class LazyStudent extends Student {
        private Supplier<Student> suppl;
        public LazyStudent(Supplier<Student> suppl) {
            super();
            this.suppl = suppl;
        }

        private void supplyIfNeeded() {
            // Supply only first time
            if(suppl != null) {
                Student stud = suppl.get();
                suppl = null;
                this.setId(stud.getId());
                this.setName(stud.getName());
                this.setSurname(stud.getSurname());
            }
        }

        @Override
        public int getId() {
            supplyIfNeeded();
            return id;
        }

        @Override
        public void setId(int id) {
            supplyIfNeeded();
            this.id = id;
        }

        @Override
        public String getName() {
            supplyIfNeeded();
            return name;
        }

        @Override
        public void setName(String name) {
            supplyIfNeeded();
            this.name = name;
        }

        @Override
        public String getSurname() {
            supplyIfNeeded();
            return surname;
        }

        @Override
        public void setSurname(String surname) {
            supplyIfNeeded();
            this.surname = surname;
        }
    }
}