package com.jazasoft.tna.entity;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

/**
 * Created by mdzahidraza on 01/07/17.
 */
public class MyRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        MyRevisionEntity entity = (MyRevisionEntity)revisionEntity;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }
        String username = null;
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken)authentication;
            if (jwtAuthenticationToken.getToken() != null && jwtAuthenticationToken.getToken().getClaims() != null) {
                username = (String) jwtAuthenticationToken.getToken().getClaims().get("user_name");
                if (username == null) {
                    username = (String) jwtAuthenticationToken.getToken().getClaims().get("username");
                }
            }
        }
        if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            username = ((User) authentication.getPrincipal()).getUsername();
        }
        if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }
        entity.setUsername(username);
    }
}
