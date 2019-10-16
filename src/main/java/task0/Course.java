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
    
    public Course (int id, String name, int cfu){
        this.id = id;
        this.name = name;
        this.cfu = cfu;
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
}
