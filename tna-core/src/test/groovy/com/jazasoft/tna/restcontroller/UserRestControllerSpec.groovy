package com.jazasoft.tna.restcontroller

import com.jazasoft.tna.ApiUrls
import com.jazasoft.tna.BaseISpec
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity


class UserRestControllerSpec extends BaseISpec {

    def "find all user"() {
        expect:
        mvc.perform(get(ApiUrls.ROOT_URL_USERS + "?sort=id,asc").header(name: "Authorization", "Bearer" + accessToken))
                .andExpect(status().isok())
                .andExpect()






    }
}
