package net.sokontokoro_factory.lovelive.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Provider
public class ResponseLoggingFilter implements ContainerResponseFilter {

    private static final Logger logger = LogManager.getLogger(ResponseLoggingFilter.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private ResourceInfo resourceInfo;

    @Context
    private HttpServletRequest httpServletRequest;

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        StringBuffer log = new StringBuffer();
        log.append("\n***HTTP RESPONSE*************************************\n");
        // resource method and class
        log.append(resourceInfo.getResourceMethod() + "\n");
        // method and endpoint
        // status code
        log.append(responseContext.getStatus());
        log.append(" ");
        log.append(responseContext.getStatusInfo().getReasonPhrase() + "\n");

        // request header
        log.append("[request header]\n");
        Map<String, List<String>> requestHeader = requestContext.getHeaders();

        for(String key: requestHeader.keySet()){
            log.append(key);
            log.append("=");
            List<String> header = requestHeader.get(key);
            for(String element: header){
                log.append(element);
            }
            log.append("\n");
        }

        // response header
        log.append("[response header]\n");
        Map<String, List<Object>> responseHeader = responseContext.getHeaders();

        for(String key: responseHeader.keySet()){
            log.append(key);
            log.append("=");
            List<Object> header = responseHeader.get(key);
            for(Object element: header){
                log.append(element);
            }
            log.append("\n");
        }

        logger.info(log.toString());
    }
}