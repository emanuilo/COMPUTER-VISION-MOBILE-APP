package rs.ac.bg.etf.diplomski;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Slf4jRequestLog;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rs.ac.bg.etf.diplomski.hibernate.DataAccessLayer;

public class MyServer {

	private static final Logger LOG = LoggerFactory.getLogger(MyServer.class.getName());

	public static void main(String[] args) throws Exception {
		Server server = null;
		try {
			DataAccessLayer.initialize();

			server = new Server(8080);  //http server - to je u sustini glassfish u netbeansu
			server.setRequestLog(new Slf4jRequestLog());
			
			ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
//			server.setHandler(servletContextHandler);

			ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/*");
			servletHolder.setInitParameter("jersey.config.server.provider.packages", "rs.ac.bg.etf.diplomski"); 

			//object that will handle the request for a given file
			ResourceHandler resource_handler = new ResourceHandler();

	        resource_handler.setDirectoriesListed(true);
	        resource_handler.setResourceBase(".");

	        HandlerList handlers = new HandlerList();
	        handlers.setHandlers(new Handler[] { resource_handler, servletContextHandler });
	        server.setHandler(handlers);
			
			
			server.start();
			LOG.debug("Server started");
			server.join();
		} catch (Exception e) {
			LOG.error("Unhandled exception during server startup", e);
			System.exit(1);
		} finally {
			if (server != null) {
				server.destroy();
			}
		}
	}
}
