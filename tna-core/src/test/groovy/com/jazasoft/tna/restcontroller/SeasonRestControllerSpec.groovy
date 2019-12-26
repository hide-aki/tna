package com.jazasoft.tna.restcontroller

import com.jazasoft.tna.ApiUrls
import com.jazasoft.tna.BaseISpec
import com.jazasoft.tna.entity.Season
import org.springframework.http.MediaType

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*


class SeasonRestControllerSpec extends BaseISpec {

    def "find all Seasons"() {
        expect:
        mvc.perform(get(ApiUrls.ROOT_URL_SEASONS + "?sort=id,asc").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.content', hasSize(2)))
                .andExpect(jsonPath('$.content[0].name', is("Spring 2019")))
    }

    def "find one Season"() {
        expect:
        mvc.perform(get(ApiUrls.ROOT_URL_SEASONS + ApiUrls.URL_SEASONS_SEASON, 2).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is("Spring 2020")))
    }


    def "create and delete Season"() {
        given:
        Season season = new Season(name: "Winter 2020");

        when: "Create new Season"
        def mvcResult = mvc.perform(post(ApiUrls.ROOT_URL_SEASONS)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(season))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(status().isCreated())
                .andReturn()

        String locationUri = mvcResult.getResponse().getHeader("Location")
        assert locationUri.contains(ApiUrls.ROOT_URL_SEASONS)
        int idx = locationUri.lastIndexOf('/');
        String id = locationUri.substring(idx + 1);

        then:
        1 == 1

        expect: "check if Season is created"
        mvc.perform(get(ApiUrls.ROOT_URL_SEASONS + ApiUrls.URL_SEASONS_SEASON, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is(season.name)))

        and: "delete season"
        this.mvc.perform(delete(ApiUrls.ROOT_URL_SEASONS + ApiUrls.URL_SEASONS_SEASON, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent())

        and: "check if season is deleted"
        this.mvc.perform(get(ApiUrls.ROOT_URL_BUYERS + ApiUrls.URL_BUYERS_BUYER, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound())
    }


    def "update Season"() {
        given:
        Season season = new Season(name: "Winter 2020");

        expect:
        mvc.perform(put(ApiUrls.ROOT_URL_SEASONS + ApiUrls.URL_SEASONS_SEASON, 2)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(season))
                .contentType(MediaType.APPLICATION_JSON_UTF8)

        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is(season.name)))

    }

    def "create bad request" () {

        given:
        Season season = new Season()

        expect:
        mvc.perform(post(ApiUrls.ROOT_URL_BUYERS)
                .header("Authorization","Bearer " + accessToken)
                .content(mapper.writeValueAsString(season))
                .contentType(MediaType.APPLICATION_JSON_UTF8)

        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath('$', hasSize(1)));

    }
}
