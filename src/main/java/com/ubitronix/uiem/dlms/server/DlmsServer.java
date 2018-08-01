package com.ubitronix.uiem.dlms.server;

import java.io.IOException;

import org.openmuc.jdlms.AuthenticationMechanism;
import org.openmuc.jdlms.DlmsServer.TcpServerBuilder;
import org.openmuc.jdlms.LogicalDevice;
import org.openmuc.jdlms.SecuritySuite;
import org.openmuc.jdlms.SecuritySuite.SecuritySuiteBuilder;
import org.openmuc.jdlms.ServerConnectionInfo;
import org.openmuc.jdlms.ServerConnectionListener;
import org.openmuc.jdlms.sessionlayer.server.ServerSessionLayerFactories;
import org.openmuc.jdlms.settings.client.ReferencingMethod;

/*
 * Sample DlmsServer with a simple SNObject returning hello
 */
public class DlmsServer {
  private long deviceId;
  private String manufacturerId;
  private String password;
  private int port;
  private String logicalDeviceName;
  private int logicalDeviceId;
  private org.openmuc.jdlms.DlmsServer dlmsServer;

  public DlmsServer(int logicalDeviceId, String logicalDeviceName, String manufacturerId, long deviceId, int port, String password) {
    this.logicalDeviceId = logicalDeviceId;
    this.logicalDeviceName = logicalDeviceName;
    this.manufacturerId = manufacturerId;
    this.deviceId = deviceId;
    this.port = port;
    this.password = password;
  }
  
  public void open() throws IOException {
    TcpServerBuilder builder = org.openmuc.jdlms.DlmsServer.tcpServerBuilder(this.port);
    LogicalDevice logicalDevice = new LogicalDevice(this.logicalDeviceId, this.logicalDeviceName, this.manufacturerId, this.deviceId);

    if (password != null) {
      SecuritySuiteBuilder securityBuilder = SecuritySuite.builder();
      securityBuilder.setAuthenticationMechanism(AuthenticationMechanism.LOW);
      securityBuilder.setPassword(password.getBytes());
      SecuritySuite securitySuite = securityBuilder.build();

      logicalDevice.addRestriction(16, securitySuite);
    }

    // Client needs to set snObjectMapping and must not retrieve snObjectMapping    
    // Map<ObisCode, SnObjectInfo> snObjectMapping = new HashMap<ObisCode, SnObjectInfo>();
    // snObjectMapping.put(SnHelloClass.INSTANCE_ID, new SnObjectInfo(0xA0, 99, 12, 3, 5 << 3)); // shift to the left, since first three bits are zero
    logicalDevice.registerCosemObject(new SnHelloClass());
    
    builder.setRefernceingMethod(ReferencingMethod.SHORT);
    builder.registerLogicalDevice(logicalDevice);
    builder.setSessionLayerFactory(ServerSessionLayerFactories.newHdlcSessionLayerFactory());
    builder.setConnectionListener(new ServerConnectionListener() {
      public void connectionChanged(ServerConnectionInfo connectionInfo) {
        System.out.println("Client connecting from " + connectionInfo.getClientInetAddress() + ", connection status is " + connectionInfo.getConnectionStatus());
      }
    });
    
    this.dlmsServer = builder.build();
  }
  
  public void close() throws IOException {
    this.dlmsServer.close();
  }
}
