package com.jazasoft.tna.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@Data
@Entity
public class Timeline extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private String name;

    @NotEmpty
    @Column(nullable = false)
    private String tnaType;

//    @Column(nullable = false)
    private Boolean approved;

    private String approvedBy;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Buyer buyer;

    @OneToMany(mappedBy = "timeline", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("tActivityList")
    private Set<TActivity> tActivityList = new HashSet<>();

    @Transient
    private Long buyerId;

    public void addTActivity(TActivity tActivity){
        this.tActivityList.add(tActivity);
        tActivity.setTimeline(this);

        if (tActivity.getTSubActivityList() !=null){
            tActivity.getTSubActivityList().forEach(tActivity::addTSubActivity);
        }
    }

    public void removeTActivity(TActivity tActivity) {
        this.tActivityList.remove(tActivity);
        tActivity.setTimeline(null);
    }

//    public void updateTActivity(TActivity tActivity, TActivity mTActivity) {
//        mTActivity.setLeadTimeNormal(tActivity.getLeadTimeNormal());
//        mTActivity.setLeadTimeOptimal(tActivity.getLeadTimeOptimal());
//        mTActivity.setTimeFrom(tActivity.getTimeFrom());
//
//        Set<Long> existingIds = tActivity.getTSubActivityList().stream().
//                filter(tSubActivity -> tSubActivity.getId() != null).map(TSubActivity::getId).
//                collect(Collectors.toSet());
//        Set<TSubActivity> removeList = mTActivity.getTSubActivityList().stream().
//                filter(tSubActivity -> !existingIds.contains(tSubActivity.getId())).
//                collect(Collectors.toSet());
//        Set<TSubActivity> newList = tActivity.getTSubActivityList().stream().
//                filter(tSubActivity -> tSubActivity.getId() == null).
//                collect(Collectors.toSet());
//
//        removeList.forEach(mTActivity::removeTSubActivity);
//        newList.forEach(mTActivity::addTSubActivity);
//        existingIds.forEach(id -> {
//            TSubActivity tSubActivity = tActivity.getTSubActivityList().stream().filter(o -> o.getId() != null && o.getId().equals(id)).findAny().get();
//            TSubActivity mTSubActivity = mTActivity.getTSubActivityList().stream().filter(o -> o.getId() != null && o.getId().equals(id)).findAny().get();
//            mTActivity.updateTSubActivity(tSubActivity, mTSubActivity);
//        });
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Timeline)) return false;
        Timeline timeline = (Timeline) o;
        return Objects.equals(id, timeline.id) &&
                Objects.equals(name, timeline.name) &&
                Objects.equals(tnaType, timeline.tnaType) &&
                Objects.equals(buyerId, timeline.buyerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, tnaType, buyerId);
    }

    @Override
    public String toString() {
        return "Timeline{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tnaType='" + tnaType + '\'' +
                ", approved=" + approved +
                ", approvedBy='" + approvedBy + '\'' +
                ", buyerId=" + buyerId +
                '}';
    }
}
