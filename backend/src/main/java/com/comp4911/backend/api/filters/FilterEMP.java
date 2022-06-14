package com.comp4911.backend.api.filters;

import com.comp4911.backend.access.CredentialManager;
import com.comp4911.backend.api.filters.annotations.AuthEMP;
import com.comp4911.backend.lib.Role;
import com.comp4911.backend.models.CredentialEntity;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Priority(2)
@Provider
@AuthEMP
public class FilterEMP implements ContainerRequestFilter {

    @Inject
    private CredentialManager credentialManager;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        Object token = containerRequestContext.getProperty("token");
        if (token == null) {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        CredentialEntity requester = credentialManager.findByToken(token.toString());
        if (requester != null && requester.getEmployeeEntity().getRole().equals(Role.EMP.getValue())) {
            containerRequestContext.setProperty("requester", requester.getEmployeeEntity());
        }
    }
}
