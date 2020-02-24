package com.jazasoft.tna.entity;

import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.security.PrivateKey;
import java.util.Date;
import java.util.Objects;

@NoArgsConstructor
@Data
@Entity
@EntityListeners({AuditingEntityListener.class})
public class OrderLog  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Temporal(value = TemporalType.TIME)
    private Date createdAt;

    @CreatedBy
    private String createdBy;

    /**
     * Events to log: [Order Created, Order Update, Activity Updated, Timeline Overridden]
     */
    private String event;

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
