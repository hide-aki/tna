package com.jazasoft.tna.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "t_activity")
public class TActivity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer leadTime;

    private Integer prevLeadTime;

    @Column(nullable = false)
    private String timeFrom;

    @NotEmpty
    private String name;

    // Copy of Parent: Activity
    private Integer serialNo;

    // Copy of Parent: Activity
    private Boolean overridable;

    // Copy of Parent: Activity
    private String delayReasons;

    private Boolean cLevel;

    // Copy of Parent: Activity
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Department department;

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

    @Transient
    private Long departmentId;

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
                Objects.equals(name, tActivity.name) &&
                Objects.equals(serialNo, tActivity.serialNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, serialNo);
    }

    @Override
    public String toString() {
        return "TActivity{" +
                "id=" + id +
                ", leadTime=" + leadTime +
                ", timeFrom='" + timeFrom + '\'' +
                ", name='" + name + '\'' +
                ", timeline=" + timeline +
                '}';
    }
}
