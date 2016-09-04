package net.sokontokoro_factory.lovelive.filter;

import net.sokontokoro_factory.yoshinani.file.config.ConfigLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Arrays;

@Provider
public class CORSResponseFilter implements ContainerResponseFilter {
    @Context
    private HttpServletRequest httpServletRequest;

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        String clientOrigin = httpServletRequest.getHeader("Origin");

        if(!isAllowed(clientOrigin)){
            return;
        }
        MultivaluedMap<String, Object> headers = response.getHeaders();
        headers.add("Access-Control-Allow-Origin", clientOrigin);
        headers.add("Access-Control-Allow-Methods", "GET, POST, HEAD, OPTIONS, PUT, DELETE");
        headers.add("Access-Control-Allow-Credentials", true);
        headers.add("Access-Control-Allow-Headers", "Content-Type");
    }

    private boolean isAllowed(String origin){
        String[] allowOrigins = ConfigLoader.getProperties().getString("allow.cross.origins").split(";");
        return Arrays.asList(allowOrigins).contains(origin);
    }
}