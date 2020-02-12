package com.jazasoft.tna.restcontroller

import com.jazasoft.tna.ApiUrls
import com.jazasoft.tna.BaseISpec
import com.jazasoft.tna.entity.Activity
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

class ActivityRestControllerSpec extends BaseISpec {

    def "find all Activity"() {
        expect:
        mvc.perform(get(ApiUrls.ROOT_URL_ACTIVITIES + "?sort=id,asc").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
//                .andExpect(jsonPath('$.content', hasSize(2)))
//                .andExpect(jsonPath('$.content[0].name', is("Activity1")))
    }

    def "findOne activity"() {
        expect:
        mvc.perform(get(ApiUrls.ROOT_URL_ACTIVITIES + ApiUrls.URL_ACTIVITIES_ACTIVITY, 2).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is("Activity2")))

    }

    def "create and delete activity"() {
        given:
        Activity activity = new Activity(name: "Activity3", departmentId: 1,serialNo: 1, cLevel: 1, isDefault: 1);


        when: "Create new "
        def mvcResult = mvc.perform(post(ApiUrls.ROOT_URL_ACTIVITIES)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(activity))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(status().isCreated())
                .andReturn()

        String locationUri = mvcResult.getResponse().getHeader("Location")
        assert locationUri.contains(ApiUrls.ROOT_URL_ACTIVITIES)
        int idx = locationUri.lastIndexOf('/');
        String id = locationUri.substring(idx + 1);

        then:
        1 == 1

        expect: "check if Activity is created"
        mvc.perform(get(ApiUrls.ROOT_URL_ACTIVITIES + ApiUrls.URL_ACTIVITIES_ACTIVITY, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is(activity.name)))

        and: "delete Activity"
        this.mvc.perform(delete(ApiUrls.ROOT_URL_ACTIVITIES + ApiUrls.URL_ACTIVITIES_ACTIVITY, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent())

        and: "check if Activity is deleted"
        this.mvc.perform(get(ApiUrls.ROOT_URL_ACTIVITIES + ApiUrls.URL_ACTIVITIES_ACTIVITY, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound())
    }


    def "update Activity"() {
        given:
        Activity activity = new Activity(name: Activity4, departmentId: 1, serialNo: 1, cLevel: 1, isDefault: 1)

        expect:
        mvc.perform(put(ApiUrls.ROOT_URL_ACTIVITIES + ApiUrls.URL_ACTIVITIES_ACTIVITY, 2)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(activity))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )

                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.name', is(activity.name)))
    }

    def "create bad request"() {

        given:
        Activity activity = new Activity();

        expect:
        mvc.perform(post(ApiUrls.ROOT_URL_ACTIVITIES)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(activity))
                .contentType(MediaType.APPLICATION_JSON_UTF8)

        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath('$', hasSize(1)));

    }
}
