package com.jazasoft.tna.entity;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by mdzahidraza on 01/07/17.
 */
@Entity
@Table(name = "rev_info")
@RevisionEntity(MyRevisionListener.class)
public class MyRevisionEntity extends DefaultRevisionEntity {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "MyRevisionEntity{" +
                "username='" + username + '\'' +
                '}';
    }
}
