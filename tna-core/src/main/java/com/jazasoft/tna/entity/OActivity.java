package com.jazasoft.tna.entity;


import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@NoArgsConstructor
@Data
@Entity
@Table(name = "o_activity")
public class OActivity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer leadTime;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date completedDate;

    @NotEmpty
    private String delayReason;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Order order;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "t_activity")
    private TActivity tActivity;
}
