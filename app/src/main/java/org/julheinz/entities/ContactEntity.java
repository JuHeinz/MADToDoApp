package org.julheinz.entities;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Represents a contact the user can add to a task.
 */
public class ContactEntity {
    private String name;
    private long id;
    private String email;
    private String phoneNumber;

    public ContactEntity(long id, String name, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return "ContactEntity{" + "name='" + name + '\'' + ", id='" + id + '\'' + ", email='" + email + '\'' + ", phoneNumber='" + phoneNumber + '\'' + '}';
    }

    /**
     * Two objects of this class are equal if they have the same id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactEntity that = (ContactEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
