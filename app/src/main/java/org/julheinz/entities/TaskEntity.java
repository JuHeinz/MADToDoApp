package org.julheinz.entities;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import org.julheinz.data.HashSetConverter;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;


@Entity
public class TaskEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    @SerializedName("name")
    private String title;

    private String description;

    @SerializedName("expiry")
    private long dueDate = System.currentTimeMillis();
    private boolean done;
    @SerializedName("favourite")
    private boolean fav;

    @TypeConverters(HashSetConverter.class)
    private HashSet<String> contacts = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public HashSet<String> getContacts() {
        if (this.contacts == null) {
            this.contacts = new HashSet<>();
        }
        return this.contacts;
    }

    public void setContacts(HashSet<String> contacts) {
        if (contacts == null) {
            return;
        }
        this.contacts = contacts;
    }


    public String getFullDueDateFormatted() {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(getDueDate()), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, kk:mm");
        return formatter.format(dateTime);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskEntity that = (TaskEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "TaskEntity{" + "id=" + id + ", title='" + title + '\'' + ", description='" + description + '\'' + ", dueDate=" + dueDate + "(" + getFullDueDateFormatted() + ")" + ", done=" + done + ", fav=" + fav + ". contacts:" + contacts;
    }
}

