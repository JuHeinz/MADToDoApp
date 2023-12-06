package org.julheinz.entities;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


public class TaskEntity implements Serializable {

    protected static long idCount = 0;
    private long id = ++idCount;
    private String taskName;

    private String description;

    private LocalDateTime createdDate;

    private LocalDateTime dueDate;
    private boolean isDone;
    private Date doneDate;


    public TaskEntity(String taskName, String description, LocalDateTime createdDate, LocalDateTime dueDate, Boolean isFav) {

        this.taskName = taskName;
        this.description = description;
        this.createdDate = createdDate;
        this.dueDate = dueDate;
        this.isDone = false;
        this.doneDate = null;
        this.isFav = isFav;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
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

    public Date getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Date doneDate) {
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
        return "TaskEntity{" +
                "taskName='" + taskName + '\'' +
                ", taskDescription='" + description + '\'' +
                ", createdDate=" + createdDate +
                ", dueDate=" + dueDate +
                ", isDone=" + isDone +
                ", doneDate=" + doneDate +
                ", isFav=" + isFav +
                '}';
    }
}

