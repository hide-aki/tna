package com.jazasoft.tna.util;

import com.jazasoft.tna.Constants;
import com.jazasoft.tna.entity.TActivity;
import com.jazasoft.util.Assert;
import com.jazasoft.util.DateUtils;
import com.jazasoft.util.Utils;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class TnaUtils {

  public static int getStdLeadTime(Collection<TActivity> tActivityList) {

    List<Node> nodeList = tActivityList.stream().map(tActivity -> new Node(tActivity.getId(), tActivity.getLeadTime(), tActivity.getTimeFrom())).collect(Collectors.toList());

    Graph graph = new Graph(nodeList);

    // Create Edges
    for (TActivity tActivity: tActivityList) {
      if (!Constants.FROM_ORDER_DATE.equals(tActivity.getTimeFrom())) {
        List<Long> ids = Utils.getListFromCsv(tActivity.getTimeFrom()).stream().map(Long::parseLong).collect(Collectors.toList());
        for (Long id: ids) {
          graph.addEdge(tActivity.getId(), id);
        }
      }
    }

    //Find Starting Activity Id
    TActivity lastActivity = tActivityList.stream().max(Comparator.comparing(TActivity::getSerialNo)).orElse(null);
    Assert.notNull(lastActivity, "Unable to Detect last activity");

    return graph.getFinalLeadTime(lastActivity.getId());
  }

  public static int getLeadTime(int leadTime, int currLeadTime, int stdLeadTime) {
    return (int)Math.round ( (double) (leadTime * currLeadTime)/stdLeadTime);
  }

  public static long daysBetween(Date first, Date second) {
    return DateUtils.toLocalDate(first).until(DateUtils.toLocalDate(second), ChronoUnit.DAYS);
  }

  public static String removeKeyFromSearch(String search, String key) {
    if (search.contains(key)) {
      String[] splits = search.split(";");
      List<String> list = new ArrayList<>();
      for (int i = 0; i < splits.length; i++) {
        if (!splits[i].contains(key)) {
          list.add(splits[i]);
        }
      }
      StringBuilder builder = new StringBuilder();
      for (String s : list) {
        builder.append(s).append(";");
      }
      if (builder.length() > 0) {
        builder.setLength(builder.length() - 1);
      }
      search = builder.toString();

      splits = search.split(",");
      list = new ArrayList<>();
      for (int i = 0; i < splits.length; i++) {
        if (!splits[i].contains(key)) {
          list.add(splits[i]);
        }
      }
      builder = new StringBuilder();
      for (String s : list) {
        builder.append(s).append(",");
      }
      if (builder.length() > 0) {
        builder.setLength(builder.length() - 1);
      }
      search = builder.toString();
    }
    return search;
  }
}
