package net.sokontokoro_factory.lovelive;

import net.sokontokoro_factory.lovelive.controller.errorhandling.NoResourceExceptionMapper;
import net.sokontokoro_factory.lovelive.exception.InvalidArgumentException;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("")
public class RestApplication extends ResourceConfig {
    public RestApplication() {
        // resource classes
        packages(false, this.getClass().getPackage().getName() + ".controller.resource")
                .register(NoResourceExceptionMapper.class)
                .register(InvalidArgumentException.class);

        // filter classes
        packages(false, this.getClass().getPackage().getName() + ".filter");
    }
}