package com.jazasoft.tna.restcontroller

import com.jazasoft.tna.ApiUrls
import com.jazasoft.tna.BaseISpec
import com.jazasoft.tna.entity.User
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


class UserRestControllerSpec extends BaseISpec {

    def "find all users"() {
        expect:
        mvc.perform(get(ApiUrls.ROOT_URL_USERS + "?sort=id,asc").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.content[0].fullName', is("aditya")))
                .andExpect(jsonPath('$.content[0].email', is("adityababu55@gmail.com")))
                .andExpect(jsonPath('$.content[0].username', is("m_user")))
                .andExpect(jsonPath('$.content[0].roles', is("hod")))
                .andExpect(jsonPath('$.content[0].mobile', is("8744952966")))
                .andExpect(jsonPath('$.content[0].teamId', is(1)))
                .andExpect(jsonPath('$.content', hasSize(2)))
    }

    def "findOne User"() {
        expect:
        mvc.perform(get(ApiUrls.ROOT_URL_USERS + ApiUrls.URL_USERS_USER, 1).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(jsonPath('$.fullName', is("aditya")))
                .andExpect(jsonPath('$.email', is("adityababu55@gmail.com")))
    }

    def "create and delete user"() {
        given:
        User user = new User(
                id: 3,
                fullName: "Test User 3",
                username: "test_user3",
                email: "test_user3@gmail.com",
                mobile: "1234567890",
                roles: "operator",
        );

        when: "Create new user"
        def mvcResult = mvc.perform(post(ApiUrls.ROOT_URL_USERS)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(status().isCreated())
                .andReturn()

        String locationUri = mvcResult.getResponse().getHeader("Location")
        assert locationUri.contains(ApiUrls.ROOT_URL_USERS)
        int idx = locationUri.lastIndexOf('/');
        String id = locationUri.substring(idx + 1);

        then:
        1 == 1

        expect: "check if user is created"
        mvc.perform(get(ApiUrls.ROOT_URL_USERS + ApiUrls.URL_USERS_USER, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(MockMvcResultMatchers.jsonPath('$.fullName', is(user.fullName)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.username', is(user.username)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.email', is(user.email)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.mobile', is(user.mobile)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.roles', is(user.roles)))

        and: "delete user"
        this.mvc.perform(delete(ApiUrls.ROOT_URL_USERS + ApiUrls.URL_USERS_USER, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent())

        and: "check if user is deleted"
        this.mvc.perform(get(ApiUrls.ROOT_URL_USERS + ApiUrls.URL_USERS_USER, id).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound())
    }

    def "update user"() {
        given:
        User user = new User(
                id: 1,
                fullName: "Test User 3",
                username: "test_user3",
                email: "test_user3@gmail.com",
                mobile: "1234567890",
                roles: "operator",
        );

        expect:
        mvc.perform(put(ApiUrls.ROOT_URL_USERS + ApiUrls.URL_USERS_USER, 1)
                .header("Authorization", "Bearer " + accessToken)
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON_UTF8)

        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentTypeJson))
                .andExpect(MockMvcResultMatchers.jsonPath('$.fullName', is(user.fullName)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.username', is(user.username)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.email', is(user.email)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.mobile', is(user.mobile)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.roles', is(user.roles)))
    }
}
