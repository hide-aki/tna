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
    private Integer leadTimeNormal;

    private Integer leadTimeOptimal;

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

//    public void updateTSubActivity(TSubActivity tSubActivity, TSubActivity mTSubActivity){
//        mTSubActivity.setLeadTimeNormal(tSubActivity.getLeadTimeNormal());
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TActivity)) return false;
        TActivity tActivity = (TActivity) o;
        return id.equals(tActivity.id) &&
                leadTimeNormal.equals(tActivity.leadTimeNormal) &&
                leadTimeOptimal.equals(tActivity.leadTimeOptimal) &&
                timeFrom.equals(tActivity.timeFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, leadTimeNormal, leadTimeOptimal, timeFrom);
    }

    @Override
    public String toString() {
        return "TActivity{" +
                "id=" + id +
                ", leadTimeNormal=" + leadTimeNormal +
                ", leadTimeOptimal=" + leadTimeOptimal +
                ", timeFrom='" + timeFrom + '\'' +
                '}';
    }
}
