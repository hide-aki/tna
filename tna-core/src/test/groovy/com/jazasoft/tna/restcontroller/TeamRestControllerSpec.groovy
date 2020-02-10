package com.jazasoft.tna.restcontroller

import com.jazasoft.tna.ApiUrls
import com.jazasoft.tna.BaseISpec
import com.jazasoft.tna.entity.Team
import org.springframework.http.MediaType
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class TeamRestControllerSpec extends BaseISpec {

    def "find all team"() {
        expect:
        mvc.perform(get(ApiUrls.ROOT_URL_TEAMS + "?sort=id,asc").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.content', hasSize(2)))
                .andExpect(jsonPath('$.content[0].name', is("Team1")))
    }

    def "findOne team"() {
        expect:
        mvc.perform(get(ApiUrls.ROOT_URL_TEAMS + ApiUrls.URL_TEAMS_TEAM, 2).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is("Team2")))

    }

    def "create and delete team"() {
        given:
        Team team = new Team(name: "Team3", departmentId: 1);



        when: "Create new Team"
        def mvcResult = mvc.perform(post(ApiUrls.ROOT_URL_TEAMS)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(team))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(status().isCreated())
                .andReturn()

        String locationUri = mvcResult.getResponse().getHeader("Location")
        assert locationUri.contains(ApiUrls.ROOT_URL_TEAMS)
        int idx = locationUri.lastIndexOf('/');
        String id = locationUri.substring(idx + 1);

        then:
        1 == 1

        expect: "check if Team is created"
        mvc.perform(get(ApiUrls.ROOT_URL_TEAMS + ApiUrls.URL_TEAMS_TEAM, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is(team.name)))

        and: "delete Team"
        this.mvc.perform(delete(ApiUrls.ROOT_URL_TEAMS + ApiUrls.URL_TEAMS_TEAM, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent())

        and: "check if Team is deleted"
        this.mvc.perform(get(ApiUrls.ROOT_URL_TEAMS + ApiUrls.URL_TEAMS_TEAM, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound())
    }


    def "update Team"() {
        given:
        Team team = new Team(name: "team4",departmentId: 1);

        expect:
        mvc.perform(put(ApiUrls.ROOT_URL_TEAMS + ApiUrls.URL_TEAMS_TEAM, 2)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(team))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )

                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is(team.name)))
    }

    def "create bad request"() {

        given:
        Team team = new Team()

        expect:
        mvc.perform(post(ApiUrls.ROOT_URL_TEAMS)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(team))
                .contentType(MediaType.APPLICATION_JSON_UTF8)

        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath('$', hasSize(1)));

    }
}
