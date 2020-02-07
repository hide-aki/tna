package com.jazasoft.tna.entity;

import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.security.PrivateKey;
import java.util.Date;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLog orderLog = (OrderLog) o;
        return Objects.equals(id, orderLog.id) &&
                Objects.equals(createdAt, orderLog.createdAt) &&
                Objects.equals(createdBy, orderLog.createdBy) &&
                Objects.equals(diff, orderLog.diff) &&
                Objects.equals(data, orderLog.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, createdBy, diff, data);
    }

    @Override
    public String toString() {
        return "OrderLog{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", createdBy='" + createdBy + '\'' +
                ", diff='" + diff + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
