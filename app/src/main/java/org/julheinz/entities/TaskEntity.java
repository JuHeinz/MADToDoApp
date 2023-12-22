package org.julheinz.entities;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.time.LocalDateTime;

//Entity annotation for ROOM to make a table for this class
@Entity
public class TaskEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;

    private String description;

    private LocalDateTime createdDate;

    private LocalDateTime dueDate;
    private boolean done;

    private boolean fav;
    private LocalDateTime doneDate;

    public TaskEntity() {
        this.createdDate = LocalDateTime.now();
        this.dueDate = LocalDateTime.now();
        this.doneDate = null;
    }

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
        Log.i("TaskEntity", "Changed title: " + title);
        this.title = title;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
        Log.i("TaskEntity", "Changed done: " + this.done);
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(LocalDateTime doneDate) {
        this.doneDate = doneDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    @NonNull
    @Override
    public String toString() {
        return "TaskEntity{" + "id=" + id + ", title='" + title + '\'' + ", description='" + description + '\'' + ", createdDate=" + createdDate + ", dueDate=" + dueDate + ", done=" + done + ", fav=" + fav + ", doneDate=" + doneDate + '}';
    }
}

