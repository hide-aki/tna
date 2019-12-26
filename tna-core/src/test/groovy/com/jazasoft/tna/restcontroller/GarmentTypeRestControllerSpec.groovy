package com.jazasoft.tna.restcontroller

import com.jazasoft.tna.ApiUrls
import com.jazasoft.tna.BaseISpec
import com.jazasoft.tna.entity.GarmentType
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

class GarmentTypeRestControllerSpec extends BaseISpec {

    def "find all garmentType"() {
        expect:
        mvc.perform(get(ApiUrls.ROOT_URL_GARMENT_TYPES + "?sort=id,asc").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.content', hasSize(2)))
                .andExpect(jsonPath('$.content[0].name', is("Men's Shirt")))
    }

    def "findOne Garment Type"() {
        expect:
        mvc.perform(get(ApiUrls.ROOT_URL_GARMENT_TYPES + ApiUrls.URL_GARMENT_TYPES_GARMENT_TYPE, 2).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is("Women's Shirt")))

    }

    def "create and delete GarmentType"() {
        given:
        GarmentType garmentType = new GarmentType(name: "Shirt");

        when: "Create new Garment type"
        def mvcResult = mvc.perform(post(ApiUrls.ROOT_URL_GARMENT_TYPES)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(garmentType))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(status().isCreated())
                .andReturn()

        String locationUri = mvcResult.getResponse().getHeader("Location")
        assert locationUri.contains(ApiUrls.ROOT_URL_GARMENT_TYPES)
        int idx = locationUri.lastIndexOf('/');
        String id = locationUri.substring(idx + 1);

        then:
        1 == 1

        expect: "check if Garment type is created"
        mvc.perform(get(ApiUrls.ROOT_URL_GARMENT_TYPES + ApiUrls.URL_GARMENT_TYPES_GARMENT_TYPE, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is(garmentType.name)))

        and: "delete Garment type"
        this.mvc.perform(delete(ApiUrls.ROOT_URL_GARMENT_TYPES + ApiUrls.URL_GARMENT_TYPES_GARMENT_TYPE, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent())

        and: "check if garment type is deleted"
        this.mvc.perform(get(ApiUrls.ROOT_URL_GARMENT_TYPES + ApiUrls.URL_GARMENT_TYPES_GARMENT_TYPE, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound())
    }


    def "update garment type"() {
        given:
        GarmentType garmentType = new GarmentType(name: "Trouser");

        expect:
        mvc.perform(put(ApiUrls.ROOT_URL_GARMENT_TYPES + ApiUrls.URL_GARMENT_TYPES_GARMENT_TYPE, 2)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(garmentType))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )

                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is(garmentType.name)))
    }

    def "create bad request" () {

        given:
        GarmentType garmentType = new GarmentType()

        expect:
        mvc.perform(post(ApiUrls.ROOT_URL_GARMENT_TYPES)
                .header("Authorization","Bearer " + accessToken)
                .content(mapper.writeValueAsString(garmentType))
                .contentType(MediaType.APPLICATION_JSON_UTF8)

        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath('$', hasSize(1)));

    }
}
