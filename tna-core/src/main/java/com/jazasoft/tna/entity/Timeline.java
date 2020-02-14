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

@NamedEntityGraphs({
//        @NamedEntityGraph(
//                name = "activity.findAll",
//                attributeNodes = @NamedAttributeNode("department")
//        ),
    @NamedEntityGraph(
        name = "timeline.findOne",
        attributeNodes = {
            @NamedAttributeNode("buyer"),
            @NamedAttributeNode(value = "tActivityList", subgraph = "timeline.tActivityList")
        },
        subgraphs = {
            @NamedSubgraph(
                name = "timeline.tActivityList",
                attributeNodes = {
                    @NamedAttributeNode("activity"),
                    @NamedAttributeNode(value = "tSubActivityList", subgraph = "timeline.tActivityList.tSubActivityList")
                }

            ),
            @NamedSubgraph(
                name = "timeline.tActivityList.tSubActivityList",
                attributeNodes = {
                    @NamedAttributeNode("subActivity")
                }
            )
        }

    )
})
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

  private Boolean approved;

  private String approvedBy;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Buyer buyer;

  @OneToMany(mappedBy = "timeline", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonProperty("tActivityList")
  private Set<TActivity> tActivityList = new HashSet<>();

  @Transient
  private Long buyerId;

  public void addTActivity(TActivity tActivity) {
    this.tActivityList.add(tActivity);
    tActivity.setTimeline(this);
  }

  public void removeTActivity(TActivity tActivity) {
    this.tActivityList.remove(tActivity);
    tActivity.setTimeline(null);
  }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Timeline)) return false;
        Timeline timeline = (Timeline) o;
        return Objects.equals(id, timeline.id) &&
                Objects.equals(name, timeline.name) &&
                Objects.equals(buyerId, timeline.buyerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, buyerId);
    }

    @Override
    public String toString() {
        return "Timeline{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", approved=" + approved +
                ", approvedBy='" + approvedBy + '\'' +
                ", buyerId=" + buyerId +
                '}';
    }
}
