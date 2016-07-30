package net.sokontokoro_factory.lovelive.controller;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("")
public class RestApplication extends ResourceConfig {
    public RestApplication() {
        packages(false, this.getClass().getPackage().getName() + ".resource");
        packages(false, )
    }
}