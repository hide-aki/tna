package com.jazasoft.tna

import com.fasterxml.jackson.databind.ObjectMapper
import com.jazasoft.util.YamlUtils
import groovy.sql.Sql
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification

class BaseUSpec extends Specification {

  @Shared Sql sql

  @Shared ObjectMapper mapper = new ObjectMapper()

  @Shared RestTemplate restTemplate = new RestTemplate()

  def setupSpec() {
    def inputStream = YamlUtils.class.getClassLoader().getResourceAsStream("application-test.yml")
    def props =  (Map<String, String>) YamlUtils.getInstance().getProperty(inputStream, "test.tenant.datasource")
    def url = props.get("url")
    def user = props.get("username")
    def password = props.get("password")
    def driverClass = props.get("driver-class-name");
    sql = Sql.newInstance(url, user, password, driverClass)

//    def dataSource = new DriverManagerDataSource(url, user, password)
//    dataSource.setDriverClassName(driverClass);
//
//    def liquibase = new Liquibase("src/test/resources/db/changelog-unit-test.xml", new FileSystemResourceAccessor(), new JdbcConnection(dataSource.getConnection()));
//    liquibase.update("")
  }

  def cleanupSpec() {
    sql.close()
  }

}
