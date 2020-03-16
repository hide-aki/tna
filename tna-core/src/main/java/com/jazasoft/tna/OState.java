package com.jazasoft.tna;

public enum OState {
  RUNNING("Running"),
  COMPLETED("Completed");

  private String value;
  OState(String value) {
    this.value = value;
  }

  public String getValue() { return value; }

  public static OState parse(String value) {
    OState oState = null;
    for (OState item : OState.values()) {
      if (item.getValue().equalsIgnoreCase(value.trim())) {
        oState = item;
        break;
      }
    }
    return oState;
  }
}
