package com.jazasoft.tna.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.Objects;

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

    @NotEmpty
    private String subActivityName;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "o_activity_id")
    private  OActivity oActivity;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "t_sub_activity_id")
    private TSubActivity tSubActivity;


    //Transient Fields
    @Transient
    @JsonProperty("oActivityId")
    private Long oActivityId;

    @Transient
    @JsonProperty("tSubActivityId")
    private Long TSubActivityId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OSubActivity)) return false;
        OSubActivity that = (OSubActivity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(leadTime, that.leadTime) &&
                Objects.equals(completedDate, that.completedDate) &&
                Objects.equals(remarks, that.remarks) &&
                Objects.equals(subActivityName, that.subActivityName) &&
                Objects.equals(oActivity, that.oActivity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, leadTime, completedDate, remarks, subActivityName, oActivity);
    }

    @Override
    public String toString() {
        return "OSubActivity{" +
                "id=" + id +
                ", leadTime=" + leadTime +
                ", completedDate=" + completedDate +
                ", remarks='" + remarks + '\'' +
                ", subActivityName='" + subActivityName + '\'' +
                ", oActivity=" + oActivity +
                '}';
    }
}
