package com.jazasoft.tna;

import com.jazasoft.tna.util.Graph;
import com.jazasoft.tna.util.Node;
import com.jazasoft.tna.util.TnaUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;


public class UTest {

  @Test
  public void test() {
    System.out.println("hello");

    int currentLeadTime = (int) DAYS.between(LocalDate.of(2020, 2, 15), LocalDate.of(2020, 5, 2));
    System.out.println(currentLeadTime);
    System.out.println(TnaUtils.getLeadTime(35, currentLeadTime, 82));
    // A10 -> A9 -> A8 -> A7 -> A6 -> A5 -> A4 -> A3 -> A2 -> A1
    //           -> A7
//    List<Node> nodeList = Arrays.asList(
//        new Node(1L, 2, "O"),
//        new Node(2L, 5, "O"),
//        new Node(3L, 10, "O"),
//        new Node(4L, 20, "O"),
//        new Node(5L, 24, "O"),
//        new Node(6L, 67, "O"),
//        new Node(7L, 74, "O"),
//        new Node(8L, 80, "O"),
//        new Node(9L, 85, "O"),
//        new Node(10L, 95, "O"),
//        new Node(11L, 100, "O"),
//        new Node(12L, 115, "O")
//    );
    List<Node> nodeList = Arrays.asList(
        new Node(1L, 2, "O"),
        new Node(2L, 5, "O"),
        new Node(3L, 10, "O"),
        new Node(4L, 20, "O"),
        new Node(5L, 24, "O"),
        new Node(6L, 67, "O"),
        new Node(7L, 7, "A"), // A6 + 7
        new Node(8L, 15, "A"),  // A7 + 15
        new Node(9L, 10, "A"), // A7 + 10
        new Node(10L, 10, "A"), // A9, A8 + 10
        new Node(11L, 5, "A"), // A10 + 5
        new Node(12L, 15, "A") // A11 + 15
    );

    Graph graph = new Graph(nodeList);
    graph.addEdge(12L, 11L);
    graph.addEdge(11L, 10L);
    graph.addEdge(10L, 9L);
    graph.addEdge(10L, 8L);
    graph.addEdge(9L, 7L);
    graph.addEdge(8L, 7L);
    graph.addEdge(7L, 6L);

    System.out.println("STD Lead Time = " + graph.getFinalLeadTime(12L));

  }
}
