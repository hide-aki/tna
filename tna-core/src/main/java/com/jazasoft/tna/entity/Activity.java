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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Department department;

    @OneToMany(mappedBy = "activity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<SubActivity> subActivityList = new HashSet<>();

    @Transient
    private Long departmentId;

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    private void AddSubActivity(SubActivity subActivity) {

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Activity)) return false;
        Activity activity = (Activity) o;
        return id.equals(activity.id) &&
                name.equals(activity.name) &&
                serialNo.equals(activity.serialNo) &&
                notify.equals(activity.notify) &&
                cLevel.equals(activity.cLevel) &&
                department.equals(activity.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, serialNo, notify, cLevel, department);
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", serialNo=" + serialNo +
                ", notify='" + notify + '\'' +
                ", cLevel=" + cLevel +
                ", department=" + department +
                '}';
    }
}
