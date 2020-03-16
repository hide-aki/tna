package com.jazasoft.tna.scheduling;

import com.jazasoft.mtdb.entity.Tenant;
import com.jazasoft.mtdb.service.EmailServiceImpl;
import com.jazasoft.mtdb.service.LicenseServiceImpl;
import com.jazasoft.mtdb.service.TenantService;
import com.jazasoft.mtdb.tenant.MultiTenantConnectionProviderImpl;
import com.jazasoft.mtdb.util.MtdbUtils;
import com.jazasoft.tna.Constants;
import com.jazasoft.tna.entity.OActivity;
import com.jazasoft.tna.entity.Order;
import com.jazasoft.tna.entity.User;
import com.jazasoft.tna.OState;
import com.jazasoft.tna.repository.UserRepository;
import com.jazasoft.tna.service.OrderService;
import com.jazasoft.tna.service.UserService;
import com.jazasoft.tna.util.MapBuilder;
import com.jazasoft.util.DateUtils;
import com.jazasoft.util.ProcessUtils;
import com.jazasoft.util.Utils;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class ScheduledTask {
  private final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);
  private final MultiTenantConnectionProvider multiTenantConnectionProvider;
  private final TenantService tenantService;
  private final UserService userService;
  private final LicenseServiceImpl licenseService;
  private final EmailServiceImpl emailService;
  private final OrderService orderService;
  private final UserRepository userRepository;
  private final ExecutorService threadPool = Executors.newFixedThreadPool(10);


  public ScheduledTask(MultiTenantConnectionProvider multiTenantConnectionProvider, TenantService tenantService, UserService userService, LicenseServiceImpl licenseService, EmailServiceImpl emailService, OrderService orderService, UserRepository userRepository) {
    this.multiTenantConnectionProvider = multiTenantConnectionProvider;
    this.tenantService = tenantService;
    this.userService = userService;
    this.licenseService = licenseService;
    this.emailService = emailService;
    this.orderService = orderService;
    this.userRepository = userRepository;
  }

  /**
   * Run Database backup at 1.00 AM every day
   */
  @Scheduled(cron = "0 0 1 * * *")
  public void backupDatabase() {
    logger.info("Database backup started...");
    List<String> databaseList = tenantService.findAll().stream().map(Tenant::getTenantId).map(tenantId -> "tna_" + tenantId).collect(Collectors.toList());

    File dir = new File(MtdbUtils.getAppHome() + File.separator + "bin");
    String backupDir = MtdbUtils.getAppHome() + File.separator + "data" + File.separator + "db-backup" + File.separator;
    String suffix = "_" + new SimpleDateFormat("ddMMMYY").format(new Date()) + ".sql";

    try {

      // Create Backup Directory if not created
      if (!new File(backupDir).exists()) {
        boolean created = new File(backupDir).createNewFile();
        if (!created) {
          logger.error("Failed to create backup directory...");
        }
      }

      String host = System.getenv("PG_HOST");
      String port = System.getenv("PG_PORT");
      String username = System.getenv("PG_USERNAME");
      String password = System.getenv("PG_PASSWORD");
      String masterdb = System.getenv("DB_NAME");

      if (host == null || port == null) {
        host = (String) MtdbUtils.getAppProperty("spring.datasource.host");
        port = (String) MtdbUtils.getAppProperty("spring.datasource.port");
        username = (String) MtdbUtils.getAppProperty("spring.datasource.username");
        password = (String) MtdbUtils.getAppProperty("spring.datasource.password");
        masterdb = (String) MtdbUtils.getAppProperty("spring.datasource.masterdb");
      }

      databaseList.add(masterdb);

      Process process = null;
      Map<String, Object> result = null;
      for (String db : databaseList) {
        process = ProcessUtils.createProcess(dir, "/bin/bash", "db_backup.sh", host, port, username, password, db, backupDir + db + suffix);
        result = ProcessUtils.execute(process);
        if ((Integer) result.get(ProcessUtils.EXIT_CODE) != 0) {
          logger.error("Database backup of db = {} failed. error = [{}\n{}]", db, result.get(ProcessUtils.CONSOLE_OUTPUT), result.get(ProcessUtils.CONSOLE_ERROR));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    logger.info("Database backup completed.");
  }

  /**
   * Every 1 hour minutes
   */
  @Scheduled(initialDelay = 10000, fixedRate = 3600000)
  public void syncUsers() {
    List<String> tenantIds = tenantService.findAll().stream().map(Tenant::getTenantId).collect(Collectors.toList());
    for (String tenantId : tenantIds) {
      MultiTenantConnectionProviderImpl mTenantConnectionProvider = (MultiTenantConnectionProviderImpl) multiTenantConnectionProvider;
      mTenantConnectionProvider.setDefaultTenant(tenantId);
      userService.syncUsers(tenantId);
      mTenantConnectionProvider.setDefaultTenant(null);
    }
  }

//  /**
//   * Run License Check at 3.00 AM every day
//   */
//  @Scheduled(cron = "0 0 3 * * *")
//  public void checkLicense() {
//    logger.debug("checkLicense");
//    List<String> tenants = tenantService.findAll().stream().map(Tenant::getTenantId).collect(Collectors.toList());
//    logger.debug("No. of tenants = {}", tenants.size());
//    for (String tenant : tenants) {
//      License license = licenseService.refreshLicense(tenant);
//      if (license != null) {
//        logger.debug("Tenant = {}, Is License Active = {}", tenant, license.isActive());
//      }
//    }
//  }

//  /**
//   * Update User Every hour
//   */
//  @Scheduled(cron = "0 0 * * * *")
//  public void updateUserCache() {
//    logger.debug("updateUserCache");
////    userCache.updateCache();
//  }

  @Scheduled(cron = "0 0 8 * * *")
  private void delayedActivity() throws ParseException {
    logger.info("Schedular Started for sending delayed oactivity notification");
    List<String> tenantIds = tenantService.findAll().stream().map(Tenant::getTenantId).collect(Collectors.toList());
    for (String tenantId : tenantIds) {
      MultiTenantConnectionProviderImpl mTenantConnectionProvider = (MultiTenantConnectionProviderImpl) multiTenantConnectionProvider;
      mTenantConnectionProvider.setDefaultTenant(tenantId);

      Map<Long, Map<String, Object>> oActivityUserMap = new HashMap<>();

      SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
      Date today = dateFormat.parse(dateFormat.format(new Date()));

      List<Order> mOrderList = orderService.findAll();

      for (Order order : mOrderList) {
        for (OActivity oActivity : order.getOActivityList()) {

          if (oActivity != null && today.compareTo(oActivity.getDueDate()) > 0 && oActivity.getCompletedDate() == null) {
            Long buyerId = oActivity.getOrder().getBuyer().getId();
            Long departmentId = oActivity.getTActivity().getDepartment().getId();
            List<User> users = userRepository.findAll(Specification.where(byDepartmentId(departmentId).and(byRole(Constants.ROLE_USER).or(byRole(Constants.ROLE_MERCHANDISING)))));

            users = users.stream().filter(user -> {
              if (user.getBuyerIds() == null) return false;
              Set<Long> buyerIds = Utils.getListFromCsv(user.getBuyerIds()).stream().map(Long::parseLong).collect(Collectors.toSet());
              return buyerIds.contains(buyerId);
            }).collect(Collectors.toList());

            List<String> toUser = users.stream().filter(user -> user.getEmail() != null).map(User::getEmail).collect(Collectors.toList());
            String[] to = new String[toUser.size()];
            toUser.toArray(to);
            String subject = "" + oActivity.getName() + " is delayed ";
            String body = "Due Date of '" + oActivity.getName() + "' was " + oActivity.getDueDate();
            logger.debug("to ={}" + to);

            oActivityUserMap.put(oActivity.getId(), new MapBuilder<String, Object>().put("to", to).put("subject", subject).put("body", body).build());
          }
        }
      }
      Runnable task = () -> {
        oActivityUserMap.values().forEach(map -> {
          String[] to = (String[]) map.get("to");
          String subject = (String) map.get("subject");
          String body = (String) map.get("body");
          emailService.sendSimpleEmail(to, subject, body);
        });
      };
      threadPool.execute(task);

      mTenantConnectionProvider.setDefaultTenant(null);
    }
    logger.info("Schedular Ended after notify for delayed oActivity");
  }

//check the order is delayed or not
  @Scheduled(cron = "0 0 22 * * *" )
  private void delayedOrder() {
    logger.info("scheduler started... ");

    List<String> tenantIds = tenantService.findAll().stream().map(Tenant::getTenantId).collect(Collectors.toList());
    for (String tenantId : tenantIds) {
      MultiTenantConnectionProviderImpl mTenantConnectionProvider = (MultiTenantConnectionProviderImpl) multiTenantConnectionProvider;
      mTenantConnectionProvider.setDefaultTenant(tenantId);

      Date today = new Date();
      List<Order> mOrderList = orderService.findAll(Specification.where(byRunningState(OState.RUNNING)));
      for (Order order : mOrderList) {
        for (OActivity oActivity : order.getOActivityList()) {
          logger.trace("Activity = {}, today = {}, dueDate = {}, completedDate = {}", oActivity.getName(), today, oActivity.getDueDate(), oActivity.getCompletedDate());
          if (LocalDate.now().isAfter(DateUtils.toLocalDate(oActivity.getDueDate()))) {
            boolean isDelayed = oActivity.getCompletedDate() == null || DateUtils.toLocalDate(oActivity.getCompletedDate()).isAfter(DateUtils.toLocalDate(oActivity.getDueDate()));
            long delayDays = oActivity.getCompletedDate() == null ? daysBetween(oActivity.getDueDate(), today) : daysBetween(oActivity.getDueDate(), oActivity.getCompletedDate());
            if (isDelayed && delayDays <= Constants.WEEKLY_MEETING) {
              oActivity.setDelayed(true);
            }
          }
        }
        boolean isDelayed = order.getOActivityList().stream().anyMatch(oActivity -> Boolean.TRUE.equals(oActivity.getDelayed()));
        boolean isCompleted = order.getOActivityList().stream().allMatch(oActivity -> oActivity.getCompletedDate() != null);
        if (!order.getDelayed().equals(isDelayed) || (OState.parse(order.getState()) != OState.COMPLETED && isCompleted)) {
          order.setDelayed(isDelayed);
          if (isCompleted) {
            order.setState(OState.COMPLETED.getValue());
          }
          orderService.update(order, "default");
        }
      }
      logger.info("scheduler ended. ");
      mTenantConnectionProvider.setDefaultTenant(null);
    }
  }

  private Specification<User> byDepartmentId(Long departmentId) {
    return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("departmentId"), departmentId));
  }
  private Specification<User> byRole(String role) {
    return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("roles"), "%" + role + "%"));
  }
  private Specification<Order> byRunningState(OState OState) {
    return (((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get("state"), OState.getValue())));
  }

  private static long daysBetween(Date first, Date second) {
    return DateUtils.toLocalDate(first).until(DateUtils.toLocalDate(second), ChronoUnit.DAYS);
  }
}