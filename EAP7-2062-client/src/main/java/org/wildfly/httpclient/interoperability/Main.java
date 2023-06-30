package org.wildfly.httpclient.interoperability;

import org.jboss.ejb.client.EJBClient;
import org.jboss.ejb.client.StatelessEJBLocator;
import org.jboss.ejb.client.URIAffinity;
import org.wildfly.httpclient.interoperability.test.Incrementor;
import org.wildfly.httpclient.interoperability.test.Result;
import org.wildfly.naming.client.WildFlyInitialContextFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.PrivilegedActionException;
import java.util.Properties;

public class Main {

	public static void main(String[] args)
			throws NamingException, PrivilegedActionException, InterruptedException {
		InitialContext ctx = new InitialContext(getCtxProperties());
		String lookupName = "ejb:/EAP7-2062-ejb/StatelessIncrementorBean!org.wildfly.httpclient.interoperability.test.Incrementor";
		Incrementor bean = (Incrementor)ctx.lookup(lookupName);
		System.out.println(bean.increment());
		ctx.close();
	}
	public static Properties getCtxProperties() {
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
		props.put(Context.PROVIDER_URL, "http://127.0.0.1:8080/wildfly-services");
		props.put(Context.SECURITY_PRINCIPAL, "ejb-over-http-user");
		props.put(Context.SECURITY_CREDENTIALS, "ejb-over-http-password");
		return props;
	}

	/*private static final String APP = "";
	private static final String MODULE = "EAP7-2062-ejb";
	private static final String BEAN = "StatelessIncrementorBean";

	private static final String JBOSS_NODE_NAME = "wildfly1";

	public static void main(String[] args) throws URISyntaxException {
		final StatelessEJBLocator<Incrementor> statelessEJBLocator = new StatelessEJBLocator<>(Incrementor.class, APP, MODULE, BEAN, "");
		final Incrementor proxy = (Incrementor) EJBClient.createProxy(statelessEJBLocator);
		EJBClient.setStrongAffinity(proxy, URIAffinity.forUri(new URI("http://127.0.0.1:8080/wildfly-services")));

		int count = 0;
		Result<Integer> retVal = null;

		// first message, handshake initialization, decide marshallers/unmarshallers
		retVal = proxy.increment();
		System.out.println(JBOSS_NODE_NAME + ": " + retVal.getNode());

		// second message, handshake completion, agree on marshallers/unmarshallers
		retVal = proxy.increment();
		System.out.println(retVal.getNode());

		// third message message, stable marshallers/unmarshallers
		retVal = proxy.increment();
		System.out.println(retVal.getNode());
	}*/
}
