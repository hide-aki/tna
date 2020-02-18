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
  private Date dueDate;

  public Task() {
  }

  public Task(OActivity oActivity) {
    this.type = "Activity";
    this.name = oActivity.getName();
    this.cLevel = oActivity.getTActivity().getCLevel();
    this.dueDate = oActivity.getDueDate();
  }

  public Task(OSubActivity oSubActivity) {
    this.type = "SubActivity";
    this.name = oSubActivity.getName();
    this.dueDate = oSubActivity.getDueDate();
  }
}
