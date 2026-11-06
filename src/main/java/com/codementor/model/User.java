package com.codementor.model;
public class User {
 private final int id; private final String name,email;
 public User(int id,String name,String email){this.id=id;this.name=name;this.email=email;}
 public int getId(){return id;} public String getName(){return name;} public String getEmail(){return email;}
}
