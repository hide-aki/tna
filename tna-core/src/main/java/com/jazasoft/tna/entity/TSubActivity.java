package com.jazasoft.tna.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@NoArgsConstructor
@Data
@Entity
@Table(name = "t_sub_activity")
public class TSubActivity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @NotNull
    private Integer leadTime;

    @JsonIgnore
    @JsonProperty("tActivity")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "t_activity_id")
    private TActivity tActivity;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private  SubActivity subActivity;

    @Transient
    private Long subActivityId;

    @Transient
    @JsonProperty("tActivityId")
    private Long tActivityId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TSubActivity)) return false;
        TSubActivity that = (TSubActivity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(leadTime, that.leadTime) &&
                Objects.equals(subActivityId, that.subActivityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, leadTime, subActivityId);
    }

    @Override
    public String toString() {
        return "TSubActivity{" +
                "id=" + id +
                ", leadTime=" + leadTime +
                ", subActivityId=" + subActivityId +
                '}';
    }
}
