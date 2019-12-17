package com.jazasoft.tna.entity;

import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.security.PrivateKey;
import java.util.Date;

@NoArgsConstructor
@Data
@Entity
public class OrderLog  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(value = TemporalType.TIME)
    private Date createdAt;

    private String createdBy;

    private String diff;

    private String data;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Order order;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "o_activity")
    private OActivity oActivity;
}
