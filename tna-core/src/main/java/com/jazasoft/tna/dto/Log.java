package com.jazasoft.tna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Data
public class Log {
  private Long timestamp;
  private String user;
  private String event;
  private String data;
  private String diff;
}
