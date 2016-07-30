package net.sokontokoro_factory.lovelive;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("")
public class RestApplication extends ResourceConfig {
    public RestApplication() {
        // resource classes
        packages(false, this.getClass().getPackage().getName() + ".controller.resource");
        // filter classes
        packages(false, this.getClass().getPackage().getName() + ".filter");
    }
}