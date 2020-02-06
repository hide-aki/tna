package com.jazasoft.tna.dto;

import lombok.Data;

@Data
public class Api {
  private String name;
  private String tenantId;
  private String endpoint;
  private String apiKey;
  private Long lastSync;
}
