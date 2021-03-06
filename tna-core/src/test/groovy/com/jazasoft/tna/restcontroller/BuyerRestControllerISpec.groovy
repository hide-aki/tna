package com.jazasoft.tna.restcontroller

import com.jazasoft.tna.ApiUrls
import com.jazasoft.tna.BaseISpec
import com.jazasoft.tna.entity.Buyer
import org.springframework.http.MediaType

import spock.lang.Ignore

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*

/**
 * @author Md Zahid Raza
 */
//@Ignore
class BuyerRestControllerISpec extends BaseISpec {

    def "find all buyers"() {
        expect:
        mvc.perform(get(ApiUrls.ROOT_URL_BUYERS + "?sort=id,asc").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.content', hasSize(2)))
                .andExpect(jsonPath('$.content[0].name', is("Dressmann")))
    }

    def "find one buyer"() {
        expect:
        mvc.perform(get(ApiUrls.ROOT_URL_BUYERS + ApiUrls.URL_BUYERS_BUYER, 2).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is("GANT")))
    }

    def "create and delete buyer"() {
        given:
        Buyer buyer = new Buyer(name: "Test Buyer");

        when: "Create new buyer"
        def mvcResult = mvc.perform(post(ApiUrls.ROOT_URL_BUYERS)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(buyer))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(status().isCreated())
                .andReturn()

        String locationUri = mvcResult.getResponse().getHeader("Location")
        assert locationUri.contains(ApiUrls.ROOT_URL_BUYERS)
        int idx = locationUri.lastIndexOf('/');
        String id = locationUri.substring(idx + 1);

        then:
        1 == 1

        expect: "check if buyer is created"
        mvc.perform(get(ApiUrls.ROOT_URL_BUYERS + ApiUrls.URL_BUYERS_BUYER, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is(buyer.name)))

        and: "delete buyer"
        this.mvc.perform(delete(ApiUrls.ROOT_URL_BUYERS + ApiUrls.URL_BUYERS_BUYER, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent())

        and: "check if buyer is deleted"
        this.mvc.perform(get(ApiUrls.ROOT_URL_BUYERS + ApiUrls.URL_BUYERS_BUYER, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound())
    }

    def "update buyer"() {
        given:
        Buyer buyer = new Buyer(name: "Hackett");

        expect:
        mvc.perform(put(ApiUrls.ROOT_URL_BUYERS + ApiUrls.URL_BUYERS_BUYER, 2)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(buyer))
                .contentType(MediaType.APPLICATION_JSON_UTF8)

        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is(buyer.name)))

    }

    def "create bad request" () {

        given:
        Buyer buyer = new Buyer()

        expect:
        mvc.perform(post(ApiUrls.ROOT_URL_BUYERS)
                .header("Authorization","Bearer " + accessToken)
                .content(mapper.writeValueAsString(buyer))
                .contentType(MediaType.APPLICATION_JSON_UTF8)

        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath('$', hasSize(1)));

    }

}
