package fi.invian.codingassignment.app;

import fi.invian.codingassignment.database.MessageDAO;
import fi.invian.codingassignment.database.UserCache;
import fi.invian.codingassignment.rest.utils.ValidationExceptionMapper;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class BackendApplication {
    public static void main(String[] args) throws Exception {
        URI baseUri = UriBuilder.fromUri("http://127.0.0.1/").port(8080).build();
        Server server = JettyHttpContainerFactory.createServer(baseUri, false);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/*");

        ServletContainer jersey = new ServletContainer(new ResourceConfig() {{
            packages("fi.invian.codingassignment.rest");
            register(new org.glassfish.hk2.utilities.binding.AbstractBinder() {
                @Override
                protected void configure() {
                    bind(UserCache.class).to(UserCache.class).in(Singleton.class);
                    bind(MessageDAO.class).to(MessageDAO.class).in(Singleton.class);
                }
            });
        }});
        ServletHolder holder = new ServletHolder(jersey);
        context.addServlet(holder, "/*");
        server.setHandler(context);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConnection.closeDataSource();
            try {
                server.stop();
            } catch (Exception e) {
                // ignored
            }
        }));
        server.start();
    }
}
