package com.jazasoft.tna.restcontroller

import com.jazasoft.tna.ApiUrls
import com.jazasoft.tna.BaseISpec
import com.jazasoft.tna.entity.Department
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

class DepartmentRestControllerSpec extends BaseISpec {

    def "find all department"() {
        expect:
        mvc.perform(get(ApiUrls.ROOT_URL_DEPARTMENTS + "?sort=id,asc").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.content', hasSize(2)))
                .andExpect(jsonPath('$.content[0].name', is("CAD")))
    }

    def "findOne Department"() {
        expect:
        mvc.perform(get(ApiUrls.ROOT_URL_DEPARTMENTS + ApiUrls.URL_DEPARTMENTS_DEPARTMENT, 2).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is("Cutting")))

    }

    def "create and delete department"() {
        given:
        Department department = new Department(name: "Shirt");

        when: "Create new Garment type"
        def mvcResult = mvc.perform(post(ApiUrls.ROOT_URL_DEPARTMENTS)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(department))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(status().isCreated())
                .andReturn()

        String locationUri = mvcResult.getResponse().getHeader("Location")
        assert locationUri.contains(ApiUrls.ROOT_URL_DEPARTMENTS)
        int idx = locationUri.lastIndexOf('/');
        String id = locationUri.substring(idx + 1);

        then:
        1 == 1

        expect: "check if Department is created"
        mvc.perform(get(ApiUrls.ROOT_URL_DEPARTMENTS + ApiUrls.URL_DEPARTMENTS_DEPARTMENT, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is(department.name)))

        and: "delete season"
        this.mvc.perform(delete(ApiUrls.ROOT_URL_DEPARTMENTS + ApiUrls.URL_DEPARTMENTS_DEPARTMENT, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent())

        and: "check if season is deleted"
        this.mvc.perform(get(ApiUrls.ROOT_URL_DEPARTMENTS + ApiUrls.URL_DEPARTMENTS_DEPARTMENT, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound())
    }


    def "update department"() {
        given:
        Department department = new Department(name: "Trouser");

        expect:
        mvc.perform(put(ApiUrls.ROOT_URL_DEPARTMENTS + ApiUrls.URL_DEPARTMENTS_DEPARTMENT, 2)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(department))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )

                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is(department.name)))
    }

    def "create bad request"() {

        given:
        Department department = new Department()

        expect:
        mvc.perform(post(ApiUrls.ROOT_URL_DEPARTMENTS)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(department))
                .contentType(MediaType.APPLICATION_JSON_UTF8)

        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath('$', hasSize(1)));

    }
}
