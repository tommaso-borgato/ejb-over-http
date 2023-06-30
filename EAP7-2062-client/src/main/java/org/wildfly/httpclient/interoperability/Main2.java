package org.wildfly.httpclient.interoperability;

import org.jboss.as.test.clustering.ejb.EJBDirectory;
import org.org.jboss.as.test.clustering.ejb.RemoteEJBDirectory;
import org.wildfly.common.function.ExceptionSupplier;
import org.wildfly.httpclient.interoperability.test.Incrementor;
import org.wildfly.httpclient.interoperability.test.Result;
import org.wildfly.httpclient.interoperability.test.StatelessIncrementorBean;

import javax.naming.Context;
import java.util.Properties;

public class Main2 {
	public static void main(String[] args) throws Exception {
		Properties env = new Properties();
		env.setProperty(Context.INITIAL_CONTEXT_FACTORY, org.wildfly.naming.client.WildFlyInitialContextFactory.class.getName());
		ExceptionSupplier<EJBDirectory, Exception> directoryProvider = () -> new RemoteEJBDirectory(
				"EAP7-2062-ejb",
				getProperties(false, "127.0.0.1", 8080, "remoteejbuser", "rem@teejbpasswd1")
		);
		try (EJBDirectory directory = directoryProvider.get()) {
			Incrementor slsb = directory.lookupStateless(StatelessIncrementorBean.class.getSimpleName(), Incrementor.class);
			Result<Integer> result = slsb.increment();
			System.out.println(
					String.format(
							"Called MultipleSFSBWithoutFailover: SFSB value = %s, backend node = %s",
							result.getValue().intValue(),
							result.getNode())
			);

			for (int i = 0; i < 100; ++i) {
				result = slsb.increment();
				System.out.println(
						String.format("Called SFSBInTransactionWithoutFailover: SFSB value = %s, backend node = %s",
								result.getValue().intValue(),
								result.getNode())
				);
			}
		}
	}

	private static Properties getProperties(boolean isSecure, String host, Integer port, String user, String password) {
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, org.wildfly.naming.client.WildFlyInitialContextFactory.class.getName());
		String providerUrl = String.format("%s://%s:%d/wildfly-services", isSecure? "https":"http", host, port);
		System.out.println(String.format("providerUrl: %s", providerUrl));
		props.put(Context.PROVIDER_URL, providerUrl);
		props.put(Context.SECURITY_PRINCIPAL, user);
		props.put(Context.SECURITY_CREDENTIALS, password);
		return props ;
	}
}
