package com.jazasoft.tna.util;

import java.util.ArrayList;
import java.util.List;

public class Node {
  private Long id;
  private String from; // "O"
  private int leadTime;
  private List<Node> adjList = new ArrayList<>();

  public Node(Long id, int leadTime, String from) {
    this.id = id;
    this.leadTime = leadTime;
    this.from = from;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public int getLeadTime() {
    return leadTime;
  }

  public void setLeadTime(int leadTime) {
    this.leadTime = leadTime;
  }

  public List<Node> getAdjList() {
    return adjList;
  }

  public void setAdjList(List<Node> adjList) {
    this.adjList = adjList;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }
}
