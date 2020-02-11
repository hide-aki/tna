package com.jazasoft.tna.entity;

import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User extends Auditable {

    @Id
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private String fullName;

    /**
     * username of user for login
     */
    @NotEmpty
    @Column(nullable = false)
    private String username;

    /**
     * email of user for notification
     */
    private String email;

    @NotEmpty
    @Column(nullable = false)
    private String mobile;


    @NotEmpty
    @Column(nullable = false, columnDefinition = "TEXT")
    private String roles;

    private Long departmentId;

    private Long teamId;

    @Column(columnDefinition = "TEXT")
    private String buyerIds;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(fullName, user.fullName) &&
                Objects.equals(username, user.username) &&
                Objects.equals(email, user.email) &&
                Objects.equals(mobile, user.mobile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, username, email, mobile);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", roles='" + roles + '\'' +
                '}';
    }
}
