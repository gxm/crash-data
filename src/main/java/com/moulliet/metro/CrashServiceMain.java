package com.moulliet.metro;

import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrashServiceMain {
    private static final Logger logger = LoggerFactory.getLogger(CrashServiceMain.class);
    private static Server server;

    public static void main(String[] args) throws Exception {
        logger.debug("starting up CrashServiceMain ");
        startResources(8080);
        server.join();
    }

    public static void startResources(int port) throws Exception {
        logger.info("starting Crash Service on port " + port);
        ServletHolder servletHolder = new ServletHolder(ServletContainer.class);
        servletHolder.setInitParameter(PackagesResourceConfig.PROPERTY_PACKAGES, "com.moulliet.metro");
        server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(server, "/");
        context.addServlet(servletHolder, "/*");

        server.start();
        logger.info("started Crash Service on port " + port);
    }

    public static void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            logger.warn("unable to stop", e);
        }
    }
}
