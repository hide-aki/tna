package com.jazasoft.tna.entity;

import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
@Table(name = "t_activity")
public class TActivity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer leadTimeNormal;

    private Integer leadTimeOptimal;

    @Column(nullable = false)
    private String timeFrom;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Timeline timeLine;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Activity activity;
}
