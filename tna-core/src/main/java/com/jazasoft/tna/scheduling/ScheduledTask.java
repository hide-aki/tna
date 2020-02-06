package com.jazasoft.tna.scheduling;

import com.jazasoft.mtdb.dto.License;
import com.jazasoft.mtdb.entity.Tenant;
import com.jazasoft.mtdb.service.LicenseServiceImpl;
import com.jazasoft.mtdb.service.TenantService;
import com.jazasoft.mtdb.tenant.MultiTenantConnectionProviderImpl;
import com.jazasoft.mtdb.util.MtdbUtils;
import com.jazasoft.tna.service.UserService;
import com.jazasoft.util.ProcessUtils;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ScheduledTask {
  private final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

  private final MultiTenantConnectionProvider multiTenantConnectionProvider;
  private final TenantService tenantService;
  private final UserService userService;
  private final LicenseServiceImpl licenseService;

  public ScheduledTask(MultiTenantConnectionProvider multiTenantConnectionProvider, TenantService tenantService, UserService userService, LicenseServiceImpl licenseService) {
    this.multiTenantConnectionProvider = multiTenantConnectionProvider;
    this.tenantService = tenantService;
    this.userService = userService;
    this.licenseService = licenseService;
  }

  /**
   * Run Database backup at 1.00 AM every day
   */
  @Scheduled(cron = "0 0 15 * * *")
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
      Map<String,Object> result = null;
      for (String db: databaseList) {
        process = ProcessUtils.createProcess(dir, "/bin/bash","db_backup.sh", host, port, username, password, db, backupDir + db + suffix);
        result = ProcessUtils.execute(process);
        if ((Integer)result.get(ProcessUtils.EXIT_CODE) != 0) {
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
    for (String tenantId: tenantIds) {
      MultiTenantConnectionProviderImpl mTenantConnectionProvider = (MultiTenantConnectionProviderImpl)multiTenantConnectionProvider;
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

}
