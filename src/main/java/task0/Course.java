/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.task0;

/**
 *
 * @author cacomop
 */
public class Course {
    private int id;
    private String name;
    private int cfu;
    private int professor;
    
    public Course (int id, String name, int cfu, int professor){
        this.id = id;
        this.name = name;
        this.cfu = cfu;
        this.professor = professor;
    }

    public int getId () {
        return id;
    }
    
    public String getName () {
        return name;
    }
    
    public int getCfu () {
        return cfu;
    }

    public int getProfessor() { return professor; }
}
