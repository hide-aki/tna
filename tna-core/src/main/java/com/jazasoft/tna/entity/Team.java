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
public class Team extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private String name;

    @Column(name = "description")
    private String desc;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @Column(nullable = false)
    private Department department;

//    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
//    private Set<User> userList = new HashSet<>();
//
}
