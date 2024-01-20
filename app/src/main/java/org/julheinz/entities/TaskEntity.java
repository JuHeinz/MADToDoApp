package org.julheinz.entities;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

//Entity annotation for ROOM to make a table for this class
@Entity
public class TaskEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    @SerializedName("name")
    private String title;

    private String description;

    @SerializedName("expiry")
    private long dueDate;
    private boolean done;
    @SerializedName("favourite")
    private boolean fav;

    public TaskEntity() {;
        this.dueDate = 784681200;
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

    @NonNull
    @Override
    public String toString() {
        return "TaskEntity{" + "id=" + id + ", title='" + title + '\'' + ", description='" + description + '\'' + ", dueDate=" + dueDate + ", done=" + done + ", fav=" + fav + '}';
    }

    // Override equals so that two TaskEntities with the same id are seen as equal, not exact same instance. Needed because TaskEntities get serialized
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
}

