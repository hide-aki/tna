package com.jazasoft.tna.entity;

import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Data
@Entity
@Table(name = "o_sub_activity")
public class OSubActivity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer leadTime;

    @Temporal(value = TemporalType.TIME)
    private Date completedDate;

    private String remarks;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "o_activity")
    private  OActivity oActivity;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "t_sub_activity")
    private TSubActivity tSubActivity;
}
