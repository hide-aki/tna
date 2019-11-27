package com.jazasoft.tna;

import com.jazasoft.mtdb.AbstractApplication;
import com.jazasoft.mtdb.Scheduler;
import com.jazasoft.mtdb.dto.License;
import com.jazasoft.mtdb.entity.Tenant;
import com.jazasoft.mtdb.service.ILicenseService;
import com.jazasoft.mtdb.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by mdzahidraza on 21/07/17.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.jazasoft.mtdb", "com.jazasoft.tna"})
public class Application extends AbstractApplication {

  private final Logger logger = LoggerFactory.getLogger(Application.class);

  @Autowired
  private Scheduler scheduler;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  //    @Bean
  CommandLineRunner init(
      TenantService tenantService,
      ILicenseService licenseService
  ) {
    return (args) -> {
      scheduleTasks(tenantService, licenseService);
    };
  }


  @Override
  protected void addMessageSourceBaseNames(List<String> list) {
    super.addMessageSourceBaseNames(list);
  }

  @Bean
  RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.requestFactory(HttpComponentsClientHttpRequestFactory::new).build();
  }

  private void scheduleTasks(TenantService tenantService, ILicenseService licenseService) {
    logger.debug("scheduleTasks..");
    int todayMinsAfterMidnight = LocalTime.now().toSecondOfDay()/60;

    /***
     * Task2: Check License daily  at 1.00 AM. Also at start of application
     ***/

    Runnable checkLicense = () -> {
      logger.debug("checkLicense");
      List<String> tenants = tenantService.findAll().stream().map(Tenant::getTenantId).collect(Collectors.toList());
      logger.debug("No. of tenants = {}", tenants.size());
      for (String tenant : tenants) {
        License license = licenseService.refreshLicense(tenant);
        if (license != null) {
          logger.debug("Tenant = {}, Is License Active = {}", tenant, license.isActive());
        }
      }
    };
    scheduler.submit(checkLicense, 0L, TimeUnit.SECONDS);
    long scheduleAt = 1 * 60 + 0; // 1.00 AM
    long diff = scheduleAt - todayMinsAfterMidnight;
    long initialDelay = (diff > 0) ? diff : (24 * 60) - diff;
    scheduler.submit(checkLicense, initialDelay, 24 * 60L, TimeUnit.MINUTES);
//
//
//        /***
//         * Task2: Database backup
//         ***/
//
//        Runnable dbBackupTask = () -> {
//            LOGGER.info("Database backup started...");
//            List<String> databaseList = tenantService.findAll().stream().map(Company::getDbName).collect(Collectors.toList());
//
//            File dir = new File(Utils.getAppHome() + File.separator + "bin");
//            String backupDir = Utils.getAppHome() + File.separator + "data" + File.separator + "db-backup" + File.separator;
//            String suffix = "_" + new SimpleDateFormat("ddMMMYY").format(new Date()) + ".sql";
//
//            try {
//                //Datasource details
//                String host = (String) Utils.getAppProperty("spring.datasource.host");
//                String port = (String) Utils.getAppProperty("spring.datasource.port");
//                String username = (String) Utils.getAppProperty("spring.datasource.username");
//                String password = (String) Utils.getAppProperty("spring.datasource.password");
//
//                String masterdb = (String) Utils.getAppProperty("spring.datasource.masterdb");
//                databaseList.add(masterdb);
//
//                Process process = null;
//                Map<String,Object> result = null;
//                for (String db: databaseList) {
//                    process = ProcessUtils.createProcess(dir, "/bin/bash","db_backup.sh", host, port, username, password, db, backupDir + db + suffix);
//                    result = ProcessUtils.execute(process);
//                    if ((Integer)result.get(ProcessUtils.EXIT_CODE) != 0) {
//                        LOGGER.error("Database backup of db = {} failed. error = [{}\n{}]", db, result.get(ProcessUtils.CONSOLE_OUTPUT), result.get(ProcessUtils.CONSOLE_ERROR));
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            LOGGER.info("Database backup completed.");
//        };
//        scheduleAt = 3 * 60 + 0; // 3.00 AM
//        diff = scheduleAt - todayMinsAfterMidnight;
//        initialDelay = (diff > 0) ? diff : (24 * 60) - diff;
//        Scheduler.getInstance().submit(dbBackupTask, initialDelay, 24*60L, TimeUnit.MINUTES);
  }
}
