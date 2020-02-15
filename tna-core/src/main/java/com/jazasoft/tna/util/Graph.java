package com.jazasoft.tna.util;

import com.jazasoft.tna.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
  List<Node> nodeList;
  Map<Long, Node> nodeMap = new HashMap<>();

  public Graph(List<Node> nodeList) {
    this.nodeList = nodeList;
    for (Node node : nodeList) {
      nodeMap.put(node.getId(), node);
    }
  }

  public void addEdge(Long src, Long dest) {
    if (nodeMap.containsKey(src) && nodeMap.containsKey(dest)) {
      nodeMap.get(src).getAdjList().add(nodeMap.get(dest));
    }
  }

  public int getFinalLeadTime(Long start) {
    return DFSVisit(nodeMap.get(start));
  }

  private int DFSVisit(Node node) {
    if (Constants.FROM_ORDER_DATE.equals(node.getFrom())) return node.getLeadTime();

    int max = Integer.MIN_VALUE;
    for (Node adj : node.getAdjList()) {
      int leadTime = DFSVisit(adj);
      if (leadTime > max) {
        max = leadTime;
      }
    }
    return node.getLeadTime() + max;
  }
}
