package com.example.myhobitapplication.models;

public class UserTest {
        private int id;
        private String name;
        private String surname;

        public UserTest(int id, String name, String surname) {
            this.id = id;
            this.name = name;
            this.surname = surname;
        }

        // Getteri
        public int getId() { return id; }
        public String getName() { return name; }
        public String getSurname() { return surname; }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
