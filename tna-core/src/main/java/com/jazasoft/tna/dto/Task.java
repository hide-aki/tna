package com.jazasoft.tna.dto;

import com.jazasoft.tna.entity.OActivity;
import com.jazasoft.tna.entity.OSubActivity;
import lombok.Data;

import java.util.Date;

@Data
public class Task {
  private String type; // Activity | SubActivity
  private String name;
  private Boolean cLevel;
  private Date completedDate;
  private Date dueDate;

  public Task() {
  }

  public Task(OActivity oActivity) {
    this.type = "Activity";
    this.name = oActivity.getName();
    this.cLevel = oActivity.getTActivity().getCLevel();
    this.dueDate = oActivity.getDueDate();
    this.completedDate = oActivity.getCompletedDate();
  }

  public Task(OSubActivity oSubActivity) {
    this.type = "SubActivity";
    this.name = oSubActivity.getName();
    this.dueDate = oSubActivity.getDueDate();
    this.completedDate = oSubActivity.getCompletedDate();
  }
}
