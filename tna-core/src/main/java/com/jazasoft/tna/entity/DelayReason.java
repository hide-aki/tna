package com.jazasoft.tna.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

@NoArgsConstructor
@Entity
public class DelayReason extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Activity activity;

    @Transient
    private Long activityId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    @Override
    public String toString() {
        return "DelayReason{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
