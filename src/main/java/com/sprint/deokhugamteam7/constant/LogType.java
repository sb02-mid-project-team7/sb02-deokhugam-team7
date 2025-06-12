package com.sprint.deokhugamteam7.constant;

public enum LogType {
  MYAPP("myapp."),
  ERROR("error-");

  private final String logDir;
  private static final String BASE_URL = "logs/";

  LogType(String logDir) {
    this.logDir = logDir;
  }

  public String getLogDir() {
    return BASE_URL + logDir;
  }
}
