package com.comp4911.backend.api.filters;

import com.comp4911.backend.access.CredentialManager;
import com.comp4911.backend.lib.Constants;
import com.comp4911.backend.models.CredentialEntity;
import com.comp4911.backend.models.EmployeeEntity;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.security.enterprise.credential.Credential;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

@Priority(3)
@Provider
public class BaseAuthFilter implements ContainerRequestFilter {

    private final String[] excludePath = { "/auth/login" };

    @Context
    private UriInfo uriInfo;

    @Inject
    private CredentialManager credentialManager;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        //ignore the login path. ei) any user can access the login path
        for (String path : excludePath) {
            if (uriInfo.getPath().equals(path)) {
                return;
            }
        }

        EmployeeEntity requester = (EmployeeEntity)(containerRequestContext.getProperty("requester"));
        if (requester == null) {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        CredentialEntity ce = requester.getCredentialEntity();
        if (ce == null) {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        now.setTime(now.getTime() + TimeUnit.MINUTES.toMillis(Constants.LOGIN_TIMEOUT_IN_MINUTES));
        ce.setTimestamp(now);
        credentialManager.merge(ce);
    }
}
