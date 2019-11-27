package com.jazasoft.tna.restcontroller

import com.jazasoft.tna.ApiUrls
import com.jazasoft.tna.BaseISpec
import com.jazasoft.tna.entity.Buyer
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Ignore

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

/**
 * @author Md Zahid Raza
 */
//@Ignore
class BuyerRestControllerISpec extends BaseISpec {

    def "find all buyers"() {
        expect:
        mvc.perform(get(ApiUrls.ROOT_URL_BUYERS + "?sort=id,asc").header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(contentTypeJson))
                .andExpect(MockMvcResultMatchers.jsonPath('$.content', hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.content[0].name', is("Dressmann")))

    }

    def "find one buyer"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.get(ApiUrls.ROOT_URL_BUYERS + ApiUrls.URL_BUYERS_BUYER, 2).header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(contentTypeJson))
                .andExpect(MockMvcResultMatchers.jsonPath('$.name', is("GANT")))
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
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()

        String locationUri = mvcResult.getResponse().getHeader("Location")
        assert locationUri.contains(ApiUrls.ROOT_URL_BUYERS)
        int idx = locationUri.lastIndexOf('/');
        String id = locationUri.substring(idx + 1);

        then:
        1 == 1

        expect: "check if buyer is created"
        mvc.perform(MockMvcRequestBuilders.get(ApiUrls.ROOT_URL_BUYERS + ApiUrls.URL_BUYERS_BUYER, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(contentTypeJson))
                .andExpect(MockMvcResultMatchers.jsonPath('$.name', is(buyer.name)))

        and: "delete buyer"
        this.mvc.perform(MockMvcRequestBuilders.delete(ApiUrls.ROOT_URL_BUYERS + ApiUrls.URL_BUYERS_BUYER, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isNoContent())

        and: "check if buyer is deleted"
        this.mvc.perform(MockMvcRequestBuilders.get(ApiUrls.ROOT_URL_BUYERS + ApiUrls.URL_BUYERS_BUYER, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    def "update buyer"() {
        given:
        Buyer buyer = new Buyer(name: "Hackett");

        expect:
        mvc.perform(MockMvcRequestBuilders.put(ApiUrls.ROOT_URL_BUYERS + ApiUrls.URL_BUYERS_BUYER, 2)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(buyer))
                .contentType(MediaType.APPLICATION_JSON_UTF8)

        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(contentTypeJson))
                .andExpect(MockMvcResultMatchers.jsonPath('$.name', is(buyer.name)))

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
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath('$', hasSize(1)));

    }

}
