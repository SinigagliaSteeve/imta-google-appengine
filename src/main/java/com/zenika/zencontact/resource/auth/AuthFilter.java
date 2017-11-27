package com.zenika.zencontact.resource.auth;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by steeve on 27/11/2017.
 */
@WebFilter(urlPatterns = {"api/v0/users/*"})
public class AuthFilter implements Filter{

    private static final Logger LOG = Logger.getLogger(AuthFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse rep = (HttpServletResponse) servletResponse;

        String pathInfo = req.getPathInfo();
        if(pathInfo != null) {
            String[] pathParts = pathInfo.split("/");

            if(req.getMethod() == "DELETE"
                    && !AuthenticationService.getInstance().isAdmin()) {
                rep.setStatus(403);
                return;
            }

            if(AuthenticationService.getInstance().isAuthenticated()
                    && AuthenticationService.getInstance().getUsername() != null) {
                // User is already connected
                rep.setHeader("Username", AuthenticationService.getInstance().getUsername());
                rep.setHeader("Logout", AuthenticationService.getInstance().getLogoutURL("/#/clear"));
            } else {
                //Only authent users can edit
                rep.setHeader("Location", AuthenticationService.getInstance().getLoginURL("/#/edit/" + pathParts[1]));
                rep.setHeader("Logout", AuthenticationService.getInstance().getLogoutURL("/#/clear"));
                rep.setStatus(401);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}