package com.jazasoft.tna.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.*;

@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "activity.findAll",
                attributeNodes = @NamedAttributeNode("department")
        ),
        @NamedEntityGraph(
                name = "activity.findOne",
                attributeNodes = {
                        @NamedAttributeNode("department")
//                        @NamedAttributeNode("subActivityList")
                }
        )
    }
)
@NoArgsConstructor
@Data
@Entity
public class Activity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer serialNo;

    /**
     * Departments which have to be notified if this activity is completed.
     */
    private String notify;

    /**
     * Whether this activity has to be reviewed at  Chief executive's level
     */
    @Column(nullable = false)
    @JsonProperty("cLevel")
    private Boolean cLevel;

    @Column(nullable = false)
    @JsonProperty("isDefault")
    private Boolean isDefault;

    @Column(nullable = false)
    private String delayReason;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Department department;

    @OneToMany(mappedBy = "activity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubActivity> subActivityList = new HashSet<>();

    @Transient
    private Long departmentId;

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }


    public void addSubActivity(SubActivity subActivity) {
        subActivityList.add(subActivity);
        subActivity.setActivity(this);
    }

    public void removeActivity(SubActivity subActivity) {
        subActivityList.remove(subActivity);
        subActivity.setActivity(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Activity)) return false;
        Activity activity = (Activity) o;
        return Objects.equals(id, activity.id) &&
                Objects.equals(name, activity.name) &&
                Objects.equals(serialNo, activity.serialNo) &&
                Objects.equals(notify, activity.notify) &&
                Objects.equals(cLevel, activity.cLevel) &&
                Objects.equals(isDefault, activity.isDefault) &&
                Objects.equals(delayReason, activity.delayReason) &&
                Objects.equals(department, activity.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, serialNo, notify, cLevel, isDefault, delayReason, department);
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", serialNo=" + serialNo +
                ", notify='" + notify + '\'' +
                ", cLevel=" + cLevel +
                ", isDefault=" + isDefault +
                ", delayReason='" + delayReason + '\'' +
                ", department=" + department +
                '}';
    }
}
