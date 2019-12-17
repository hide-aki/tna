package com.jazasoft.tna.entity;

import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Data
@Entity
public class Timeline extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private String name;

    @NotEmpty
    @Column(nullable = false)
    private String tnaType;

//    @Column(nullable = false)
    private Boolean approved;

    private String approvedBy;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Buyer buyer;
}
