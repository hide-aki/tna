package com.jazasoft.tna.entity;

import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Data
@Entity
@Table(name = "t_sub_activity")
public class TSubActivity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private Integer leadTimeNormal;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private  SubActivity subActivity;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "t_activity")
    private TActivity tActivity;
}
