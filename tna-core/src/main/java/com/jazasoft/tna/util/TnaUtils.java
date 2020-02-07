package com.jazasoft.tna.util;

import com.jazasoft.tna.entity.TActivity;

import java.util.Collection;

public class TnaUtils {

  public static int getStandardLeadTime(Collection<TActivity> tActivityList) {

    return 100;
  }

  public static int getLeadTime(int leadTime, int currLeadTime, int standardLeadTime) {
    return leadTime;
  }
}
