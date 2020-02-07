package com.jazasoft.tna.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.Objects;

@NoArgsConstructor
@Data
@Entity
@Table(name = "o_sub_activity")
@BatchSize(size = 20)
public class OSubActivity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String name;

    @Column(nullable = false)
    private Integer leadTime;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date completedDate;

    private String remarks;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "o_activity_id")
    private  OActivity oActivity;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "t_sub_activity_id")
    private TSubActivity tSubActivity;

    @Transient
    @JsonProperty("oActivityId")
    private Long oActivityId;

    @Transient
    @JsonProperty("tSubActivityId")
    private Long tSubActivityId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OSubActivity)) return false;
        OSubActivity that = (OSubActivity) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(oActivityId, that.oActivityId) &&
            Objects.equals(tSubActivityId, that.tSubActivityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, oActivityId, tSubActivityId);
    }

    @Override
    public String toString() {
        return "OSubActivity{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", leadTime=" + leadTime +
            ", completedDate=" + completedDate +
            ", remarks='" + remarks + '\'' +
            ", oActivityId=" + oActivityId +
            ", tSubActivityId=" + tSubActivityId +
            '}';
    }
}
