package com.jazasoft.tna.entity.view;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Entity;


@Entity
@Immutable
@Subselect("SELECT * FROM task_sub_activity_view")
public class TaskSubActivityView extends Task {

}
