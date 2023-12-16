package org.julheinz.entities;

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
    private boolean isDone;
    private LocalDateTime doneDate;

    public TaskEntity(String title, String description, LocalDateTime createdDate, LocalDateTime dueDate, Boolean isFav) {

        this.title = title;
        this.description = description;
        this.createdDate = createdDate;
        this.dueDate = dueDate;
        this.isDone = false;
        this.doneDate = null;
        this.isFav = isFav;
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
        this.title = title;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
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
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    private boolean isFav;

    @NonNull
    @Override
    public String toString() {
        return "TaskEntity{" + "taskName='" + title + '\'' + ", taskDescription='" + description + '\'' + ", createdDate=" + createdDate + ", dueDate=" + dueDate + ", isDone=" + isDone + ", doneDate=" + doneDate + ", isFav=" + isFav + '}';
    }
}

