package org.julheinz.entities;

public class ContactEntity {
    public ContactEntity(String name){
        this.name = name;
    }
    private String name;
    private String email;

    private String phoneNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ContactEntity{" + "name='" + name + '\'' + ", email='" + email + '\'' + ", phoneNumber='" + phoneNumber + '\'' + '}';
    }
}
