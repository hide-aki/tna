package com.jazasoft.tna.entity;

import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
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

}
