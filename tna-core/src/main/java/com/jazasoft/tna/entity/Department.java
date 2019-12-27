package com.jazasoft.tna.entity;

import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Data
@Entity
@BatchSize(size = 10)
public class Department extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private String name;

    @Column(name = "description")
    private String desc;

//    @JsonIgnore
//    @OneToMany(mappedBy = "department")
//    private Set<Team> teamList = new HashSet<>();
}
