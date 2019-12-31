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
