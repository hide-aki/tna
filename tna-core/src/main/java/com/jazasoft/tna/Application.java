package com.jazasoft.tna;

import com.jazasoft.mtdb.AbstractApplication;
import com.jazasoft.mtdb.Scheduler;
import com.jazasoft.mtdb.dto.License;
import com.jazasoft.mtdb.entity.Tenant;
import com.jazasoft.mtdb.service.ILicenseService;
import com.jazasoft.mtdb.service.TenantService;
import com.jazasoft.mtdb.util.RestUtils;
import com.jazasoft.util.PropUtils;
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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by mdzahidraza on 21/07/17.
 */
@Controller
@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = {"com.jazasoft.mtdb", "com.jazasoft.tna"})
public class Application extends AbstractApplication {

  private final Logger logger = LoggerFactory.getLogger(Application.class);

  @Autowired
  private Scheduler scheduler;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }


  @RequestMapping(value = {"/", "/index.html"})
  public String home() {
    return "index.html";
  }

  @RequestMapping(value = "/service-worker.js", method = RequestMethod.GET)
  public String serviceWorker() {
    return "service-worker.js";
  }

  @RequestMapping(value = {"/{path:[^\\.]*}", "/**/{path:^(?!oauth).*}/{path:[^\\.]*}"}, method = RequestMethod.GET)
  public String redirect() {
    return "forward:/";
  }

  @GetMapping("/buildInfo")
  @ResponseBody
  public Map<String, String> buildInfo() {

    InputStream is = PropUtils.class.getClassLoader().getResourceAsStream("build.properties");
    Properties props = new Properties();
    try {
      props.load(is);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Map<String, String> buildInfo = new HashMap<>();

    buildInfo.put("version", props.getProperty("build.version", ""));
    buildInfo.put("number", props.getProperty("build.number", ""));
    buildInfo.put("date", props.getProperty("build.date", ""));
    return buildInfo;
  }

  //    @Bean
  CommandLineRunner init(
      TenantService tenantService,
      ILicenseService licenseService
  ) {
    return (args) -> {
      checkLicenseAtStart(tenantService, licenseService);
    };
  }


  @Override
  protected void addMessageSourceBaseNames(List<String> list) {
    super.addMessageSourceBaseNames(list);
  }


//  @Bean
//  public Javers javers() {
//    return JaversBuilder.javers()
//        .withListCompareAlgorithm(ListCompareAlgorithm.AS_SET)
//        .build();
//  }

  @Bean
  RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.requestFactory(HttpComponentsClientHttpRequestFactory::new).build();
  }

  @Bean
  public RestUtils restUtils(RestTemplate restTemplate) {
    return new RestUtils(restTemplate);
  }


  private void checkLicenseAtStart(TenantService tenantService, ILicenseService licenseService) {
    logger.debug("checkLicense");
    List<String> tenants = tenantService.findAll().stream().map(Tenant::getTenantId).collect(Collectors.toList());
    logger.debug("No. of tenants = {}", tenants.size());
    for (String tenant : tenants) {
      License license = licenseService.refreshLicense(tenant);
      if (license != null) {
        logger.debug("Tenant = {}, Is License Active = {}", tenant, license.isActive());
      }
    }
  }
}
