package com.jazasoft.tna;

public interface ApiUrls {

  String ROOT_URL_USERS = "/api/users";
  String URL_USERS_USER = "/{userId}";
  String ROOT_URL_USERS_BATCH_SAVE = "/batchSave";

  String ROOT_URL_BUYERS = "/api/buyers";
  String URL_BUYERS_BUYER= "/{buyerId}";

  String ROOT_URL_SEASONS = "/api/seasons";
  String URL_SEASONS_SEASON= "/{seasonId}";

  String ROOT_URL_GARMENT_TYPES="/api/garmentTypes";
  String URL_GARMENT_TYPES_GARMENT_TYPE="/{garmentTypeId}";

  String ROOT_URL_DEPARTMENTS="/api/departments";
  String URL_DEPARTMENTS_DEPARTMENT="/{departmentId}";

  String ROOT_URL_TEAMS="/api/teams";
  String URL_TEAMS_TEAM="/{teamId}";

  String ROOT_URL_DELAYREASONS="/api/delayReasons";
  String URL_DELAYREASONS_DELAYREASON="/{delayReasonId}";

  String ROOT_URL_ACTIVITIES="/api/activities";
  String URL_ACTIVITIES_ACTIVITY="/{activityId}";

  String ROOT_URL_TIMELINES="/api/timelines";
  String URL_TIMELINES_TIMELINE="/{timelineId}";

  String ROOT_URL_SETTINGS = "/api/settings";
  String URL_SETTINGS_SETTING = "/{key}";

  String ROOT_URL_ORDERS = "api/orders";
  String URL_ORDERS_ORDER ="/{orderId}";
  String URL_ORDERS_ORDER_LOGS ="/logs";
  String URL_ORDERS_ORDER_LOGS_ACTIVITY_LOGS ="/activities/{activityId}/logs";
  String URL_ORDERS_ORDER_ACTIVITIES_ACTIVITY= "/activities/{activityId}";
  String URL_ORDERS_ORDER_SUBACTIVITIES_SUBACTIVITY= "/subActivities/{subActivityId}";

  String ROOT_URL_CALENDAR = "api/calendar";

  public static final String ROOT_URL_TEMPLATES = "/api/templates";
}
