package com.jazasoft.tna.audit;

import com.jazasoft.tna.entity.OActivity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;

import java.util.Date;

@NoArgsConstructor
@Data
@TypeName("Activity")
public class Activity {
  @Id
  private Long id;
  private Integer leadTime;
  private Integer finalLeadTime;
  private Date completedDate;
  private String delayReason;
  private String remarks;

  public Activity(OActivity oActivity) {
    this.id = oActivity.getId();
    this.leadTime = oActivity.getLeadTime();
    this.finalLeadTime = oActivity.getFinalLeadTime();
    this.completedDate = oActivity.getCompletedDate();
    this.delayReason = oActivity.getDelayReason();
    this.remarks = oActivity.getRemarks();
  }
}
