package org.wildfly.httpclient.interoperability;

import org.jboss.as.test.clustering.ejb.EJBDirectory;
import org.org.jboss.as.test.clustering.ejb.RemoteEJBDirectory;
import org.wildfly.common.function.ExceptionSupplier;
import org.wildfly.httpclient.interoperability.test.Incrementor;
import org.wildfly.httpclient.interoperability.test.Result;
import org.wildfly.httpclient.interoperability.test.StatelessIncrementorBean;

import javax.naming.Context;
import java.util.Properties;

/**
 * 10:24:57,493 INFO [org.jboss.weld.deployer] (MSC service thread 1-1) WFLYWELD0003: Processing weld deployment clusterbench-ee10-web-granular.war
 * 10:24:57,514 INFO [org.jboss.weld.deployer] (MSC service thread 1-3) WFLYWELD0003: Processing weld deployment clusterbench-ee10-web-passivating.war
 * 10:24:57,527 INFO [org.jboss.weld.deployer] (MSC service thread 1-4) WFLYWELD0003: Processing weld deployment clusterbench-ee10-web.war
 * 10:24:57,583 INFO [org.jboss.weld.deployer] (MSC service thread 1-5) WFLYWELD0003: Processing weld deployment clusterbench-ee10-ejb.jar
 * 10:24:57,611 INFO [org.jboss.as.ejb3.deployment] (MSC service thread 1-5) WFLYEJB0473: JNDI bindings for session bean named 'RemoteStatelessSBImpl' in deployment unit 'subdeployment "clusterbench-ee10-ejb.jar" of deployment "clusterbench-ee10.ear"' are as follows:
 *
 * java:global/clusterbench-ee10/clusterbench-ee10-ejb/RemoteStatelessSBImpl!org.jboss.test.clusterbench.ejb.stateless.RemoteStatelessSB
 * java:app/clusterbench-ee10-ejb/RemoteStatelessSBImpl!org.jboss.test.clusterbench.ejb.stateless.RemoteStatelessSB
 * java:module/RemoteStatelessSBImpl!org.jboss.test.clusterbench.ejb.stateless.RemoteStatelessSB
 * java:jboss/exported/clusterbench-ee10/clusterbench-ee10-ejb/RemoteStatelessSBImpl!org.jboss.test.clusterbench.ejb.stateless.RemoteStatelessSB
 * ejb:clusterbench-ee10/clusterbench-ee10-ejb/RemoteStatelessSBImpl!org.jboss.test.clusterbench.ejb.stateless.RemoteStatelessSB
 * java:global/clusterbench-ee10/clusterbench-ee10-ejb/RemoteStatelessSBImpl
 * java:app/clusterbench-ee10-ejb/RemoteStatelessSBImpl
 * java:module/RemoteStatelessSBImpl
 *
 * 10:24:57,611 INFO [org.jboss.as.ejb3.deployment] (MSC service thread 1-5) WFLYEJB0473: JNDI bindings for session bean named 'RemoteStatefulSBImpl' in deployment unit 'subdeployment "clusterbench-ee10-ejb.jar" of deployment "clusterbench-ee10.ear"' are as follows:
 *
 * java:global/clusterbench-ee10/clusterbench-ee10-ejb/RemoteStatefulSBImpl!org.jboss.test.clusterbench.ejb.stateful.RemoteStatefulSB
 * java:app/clusterbench-ee10-ejb/RemoteStatefulSBImpl!org.jboss.test.clusterbench.ejb.stateful.RemoteStatefulSB
 * java:module/RemoteStatefulSBImpl!org.jboss.test.clusterbench.ejb.stateful.RemoteStatefulSB
 * java:jboss/exported/clusterbench-ee10/clusterbench-ee10-ejb/RemoteStatefulSBImpl!org.jboss.test.clusterbench.ejb.stateful.RemoteStatefulSB
 * ejb:clusterbench-ee10/clusterbench-ee10-ejb/RemoteStatefulSBImpl!org.jboss.test.clusterbench.ejb.stateful.RemoteStatefulSB?stateful
 * java:global/clusterbench-ee10/clusterbench-ee10-ejb/RemoteStatefulSBImpl
 * java:app/clusterbench-ee10-ejb/RemoteStatefulSBImpl
 * java:module/RemoteStatefulSBImpl
 */
public class MainOC {
	public static void main(String[] args) throws Exception {
		Properties env = new Properties();
		env.setProperty(Context.INITIAL_CONTEXT_FACTORY, org.wildfly.naming.client.WildFlyInitialContextFactory.class.getName());
		ExceptionSupplier<EJBDirectory, Exception> directoryProvider = () -> new RemoteEJBDirectory(
				"EAP7-2062-ejb",
				getProperties(false, "127.0.0.1", 8080, "admin", "pass.1234")
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
		String providerUrl = "http://wildfly-ejb-route-eap7-2062.apps.tborgato-yhvd.eapqe.psi.redhat.com//wildfly-services";
		System.out.println(String.format("providerUrl: %s", providerUrl));
		props.put(Context.PROVIDER_URL, providerUrl);
		props.put(Context.SECURITY_PRINCIPAL, user);
		props.put(Context.SECURITY_CREDENTIALS, password);
		return props ;
	}
}
