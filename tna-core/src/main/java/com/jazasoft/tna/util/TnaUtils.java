package com.jazasoft.tna.util;

import com.jazasoft.tna.Constants;
import com.jazasoft.tna.entity.TActivity;
import com.jazasoft.util.Assert;
import com.jazasoft.util.Utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
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

    return graph.getStdLeadTime(lastActivity.getId());
  }

  public static int getLeadTime(int leadTime, int currLeadTime, int stdLeadTime) {
    return  (leadTime * currLeadTime)/stdLeadTime;
  }
}
