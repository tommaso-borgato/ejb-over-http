```text
/subsystem=undertow/server=default-server/host=default-host/setting=http-invoker:write-attribute(name=path,value=wildfly-services)
```

```shell
./bin/add-user.sh -u 'ejb-over-http-user' -p 'ejb-over-http-password' -g 'admin'
./bin/standalone.sh --server-config=standalone-ha.xml -Djboss.node.name=wildfly1
```

```text
15:33:01,781 WARN  [org.wildfly.extension.elytron] (MSC service thread 1-7) WFLYELY00023: KeyStore file '/home/tborgato/Documents/EAP7-2062/server-29.0.0.Beta1/server/standalone/configuration/application.keystore' does not exist. Used blank.
```

```shell
keytool -genkeypair -alias localhost -keyalg RSA -keysize 1024 -validity 365 -keystore application.keystore -dname "CN=localhost" -storepass password
```