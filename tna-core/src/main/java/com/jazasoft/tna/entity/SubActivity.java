package com.jazasoft.tna.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

@NoArgsConstructor
@Data
@Entity
public class SubActivity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private String name;

    @Column(name = "description")
    private String desc;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Activity activity;

    @Transient
    private Long subActivityId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubActivity)) return false;
        SubActivity that = (SubActivity) o;
        return id.equals(that.id) &&
                name.equals(that.name) &&
                desc.equals(that.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, desc);
    }

    @Override
    public String toString() {
        return "SubActivity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
