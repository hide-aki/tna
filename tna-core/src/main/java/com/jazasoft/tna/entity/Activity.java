package com.jazasoft.tna.entity;


import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Data
@Entity
public class Activity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer serialNo;

    /**
     * Departments which have to be notified if this activity is completed.
     */
    private String notify;

    /**
     * Whether this activity has to be reviewed at  Chief executive's level
     */
    @Column(nullable = false)
    private Boolean cLevel;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Department department;
}
