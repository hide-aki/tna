package com.jazasoft.tna

import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.jazasoft.mtdb.IConfigKeys
import com.jazasoft.mtdb.util.MtdbUtils
import com.jazasoft.tna.dto.OauthResponse
import com.jazasoft.util.YamlUtils
import groovy.sql.Sql
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.FileSystemResourceAccessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.*
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author Md Zahid Raza
 */
@Ignore
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
class BaseISpec extends Specification {

    @Autowired
    protected MockMvc mvc

    @Autowired
    RestTemplate restTemplate

    @Shared
    ObjectMapper mapper = new ObjectMapper()

    @Shared
            contentTypeJson = "application/json;charset=UTF-8"

    @Shared
            tenant = "test"
//  @Shared tenant = "marketing"

    @Shared
    Sql sql

    @Shared
    def accessToken


    def setupSpec() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        println "Opening Sql Connection..."
        def inputStream = YamlUtils.class.getClassLoader().getResourceAsStream("application-test.yml")
        def props = (Map<String, String>) YamlUtils.getInstance().getProperty(inputStream, "test.tenant.datasource")
        def url = props.get("url")
        def user = props.get("username")
        def password = props.get("password")
        def driverClass = props.get("driver-class-name");
        sql = Sql.newInstance(url, user, password, driverClass)

        // Initialize database
        def dataSource = new DriverManagerDataSource(url, user, password)
        dataSource.setDriverClassName(driverClass);
        def liquibase = new Liquibase("src/test/resources/db/changelog-tenant-h2.xml", new FileSystemResourceAccessor(), new JdbcConnection(dataSource.getConnection()));
//    def liquibase = new Liquibase("src/test/resources/db/changelog-tenant-db.xml", new FileSystemResourceAccessor(), new JdbcConnection(dataSource.getConnection()));

        liquibase.update("")

    }

    def cleanupSpec() {
        println "Closing Sql Connection..."
        sql.close()
    }

    def setup() {
        def username = "su_dev"
        def password = "Jaza@7292"

        String appId = (String) MtdbUtils.getAppId();
        String authorization = MtdbUtils.getBasicAuthorization();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorization);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity("", headers);

        def baseUrl = MtdbUtils.getConfProperty("iam.endpoint");
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/oauth/token")
                .queryParam("grant_type", "password")
                .queryParam("username", username)
                .queryParam("password", password)
                .queryParam("appId", appId);
        String url = builder.build().toUri().toString();
        ResponseEntity<OauthResponse> response = null;

        try {
            response = this.restTemplate.exchange(url, HttpMethod.POST, httpEntity, OauthResponse.class, new Object[0]);
        } catch (Exception e) {
            throw new RuntimeException("Unable to login with user: " + username + ". error = " + e.getMessage());
        }

        OauthResponse resp = response.getBody();

        accessToken = resp.access_token
    }

    protected void printResponse(MvcResult mvcResult) throws Exception {
        println "Response body: " + mvcResult.getResponse().getContentAsString()
    }
}
