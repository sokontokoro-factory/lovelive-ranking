package net.sokontokoro_factory.lovelive.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Provider
public class RequestLoggingFilter implements ContainerRequestFilter {

    private static final Logger logger = LogManager.getLogger(RequestLoggingFilter.class);


    @Context
    private UriInfo uriInfo;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        StringBuffer log = new StringBuffer();
        log.append("\n***HTTP REQUEST*************************************\n");
        // resource method and class
        log.append(resourceInfo.getResourceMethod() + "\n");
        // method and endpoint
        log.append(requestContext.getMethod().toString() + ": " + uriInfo.getPath() + "\n");
        // qeuery params
        Map<String, List<String>> queryParameters = uriInfo.getQueryParameters();
        for(String key: queryParameters.keySet()){
            log.append(key);
            log.append("=");
            List<String> header = queryParameters.get(key);
            for(String element: header){
                log.append(element);
            }
            log.append("\n");
        }

        // header
        log.append("[header]\n");
        Map<String, List<String>> headers = requestContext.getHeaders();

        for(String key: headers.keySet()){
            log.append(key);
            log.append("=");
            List<String> header = headers.get(key);
            for(String element: header){
                log.append(element);
            }
            log.append("\n");
        }

        // parameter
        log.append("[params]\n");
        InputStream entity = requestContext.getEntityStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buf = new byte[1024];
        int size = 0;
        while((size = entity.read(buf, 0, buf.length)) != -1) {
            os.write(buf, 0, size);
        }

        byte[] bytes = os.toByteArray();
        requestContext.setEntityStream(new ByteArrayInputStream(bytes));
        log.append(new String(bytes)+ "\n");

        logger.info(log.toString());
    }
}