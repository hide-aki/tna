package com.jazasoft.tna.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Department department;

    @Transient
    private Long departmentId;
}
