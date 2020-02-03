package com.jazasoft.tna.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@Data
@Entity
@Table(name = "t_activity")
public class TActivity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer leadTime;

    @Column(nullable = false)
    private String timeFrom;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Timeline timeline;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Activity activity;

    @OneToMany(mappedBy = "tActivity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("tSubActivityList")
    private Set<TSubActivity> tSubActivityList = new HashSet<>();

    @Transient
    private Long activityId;

    @Transient
    private Long timelineId;

    public void addTSubActivity(TSubActivity tSubActivity) {
        this.tSubActivityList.add(tSubActivity);
        tSubActivity.setTActivity(this);
    }

    public void removeTSubActivity(TSubActivity tSubActivity){
        this.tSubActivityList.remove(tSubActivity);
        tSubActivity.setTActivity(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TActivity tActivity = (TActivity) o;
        return Objects.equals(id, tActivity.id) &&
                Objects.equals(leadTime, tActivity.leadTime) &&
                Objects.equals(timeFrom, tActivity.timeFrom) &&
                Objects.equals(timeline, tActivity.timeline) &&
                Objects.equals(activity, tActivity.activity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, leadTime, timeFrom, timeline, activity);
    }

    @Override
    public String toString() {
        return "TActivity{" +
                "id=" + id +
                ", leadTime=" + leadTime +
                ", timeFrom='" + timeFrom + '\'' +
                ", timeline=" + timeline +
                ", activity=" + activity +
                '}';
    }
}
