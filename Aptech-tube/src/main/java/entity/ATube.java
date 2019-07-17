package entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Calendar;
import java.util.Date;

@Entity
public class ATube {
    @Id
    private long id;
    @Index
    private String name;
    @Index
    private String description;
    @Index
    private Date createdAt;
    @Index
    private Date updatedAt;
    @Index
    private Date deletedAt;
    @Index
    private int status; //1 active, 0 deactive

    public ATube() {
        this.id =Calendar.getInstance().getTimeInMillis();
        this.createdAt= Calendar.getInstance().getTime();
        this.updatedAt=null;
        this.deletedAt= null;
        this.status= Status.active.getValue();
    }


    public enum Status{
        active(1),deactive(0),delete(-1);
        int value;
        Status(int value){
            this.value=value;
        }
        public int getValue(){
            return value;
        }
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(ATube.Status status) {
        this.status = status.getValue();
    }
}
