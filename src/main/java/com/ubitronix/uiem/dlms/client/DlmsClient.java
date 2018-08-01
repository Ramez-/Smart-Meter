package com.ubitronix.uiem.dlms.client;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.AuthenticationMechanism;
import org.openmuc.jdlms.ConnectionBuilder;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.HexConverter;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.RawMessageData;
import org.openmuc.jdlms.RawMessageListener;
import org.openmuc.jdlms.SecuritySuite;
import org.openmuc.jdlms.SecuritySuite.SecuritySuiteBuilder;
import org.openmuc.jdlms.TcpConnectionBuilder.InetTransportProtocol;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.SnObjectInfo;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.openmuc.jdlms.settings.client.ReferencingMethod;

public class DlmsClient {
  private static final Log LOG = LogFactory.getLog(DlmsClient.class.getName());
  private static Long DEFAULT_CLOSE_TIMEOUT = 5000L;
  private static Integer DEFAULT_OPEN_NUMBER_OF_TRIES = 3;
  private static Long DEFAULT_OPEN_WAIT_TIMEOUT = 10000L;

  private ConnectionBuilder<?> connectionBuilder;
  private DlmsConnection dlmsConnection;
  private Map<ObisCode, SnObjectInfo> snObjectMapping;
  private Long closeTimeout; 
  private Integer port;
  private String ip;
  boolean open = false;
  
  public DlmsClient(String ip, Integer port, Integer clientAddress, String password, Integer logicalDeviceAddress,
      Integer physicalDeviceAddress, Map<ObisCode, SnObjectInfo> snObjectMapping) throws Exception {
    this(ip, port, clientAddress, password, logicalDeviceAddress, physicalDeviceAddress, snObjectMapping, DEFAULT_CLOSE_TIMEOUT);
  }

  public DlmsClient(String ip, Integer port, Integer clientAddress, String password, Integer logicalDeviceAddress,
      Integer physicalDeviceAddress, Map<ObisCode, SnObjectInfo> snObjectMapping,
      Long closeTimeout) throws Exception {
    InetAddress inetAddress = InetAddress.getByName(ip);
    TcpConnectionBuilder tcpConnectionBuilder = new TcpConnectionBuilder(inetAddress);
    this.connectionBuilder = tcpConnectionBuilder;
    this.snObjectMapping = snObjectMapping;
    this.closeTimeout = closeTimeout;
    this.port = port;
    this.ip = ip;
    tcpConnectionBuilder.setPort(port);
    tcpConnectionBuilder.setTranportProtocol(InetTransportProtocol.TCP);
    tcpConnectionBuilder.useHdlc();
    tcpConnectionBuilder.setRawMessageListener(new RawMessageListener() {
      public void messageCaptured(RawMessageData paramRawMessageData) {
        if (LOG.isTraceEnabled()) {
          LOG.trace("Received raw message with " + 
            "\n\t Source " + paramRawMessageData.getMessageSource() +
            "\n\t Message " + (paramRawMessageData.getMessage() != null ? HexConverter.toHexString(paramRawMessageData.getMessage()) : null) +
            "\n\t APDU " + paramRawMessageData.getApdu());
        }
      }
    });
   
    this.connectionBuilder.setClientId(clientAddress);
    this.connectionBuilder.setLogicalDeviceId(logicalDeviceAddress);
    this.connectionBuilder.setPhysicalDeviceAddress(physicalDeviceAddress);
    this.connectionBuilder.setReferencingMethod(ReferencingMethod.SHORT);
    this.connectionBuilder.setResponseTimeout(60000);
    if (this.snObjectMapping != null) {
      this.connectionBuilder.setSnObjectMapping(snObjectMapping);
    }

    if (password != null) {
       SecuritySuiteBuilder securityBuilder = SecuritySuite.builder();
       securityBuilder.setAuthenticationMechanism(AuthenticationMechanism.LOW);
       securityBuilder.setPassword(password.getBytes(StandardCharsets.US_ASCII));
       SecuritySuite securitySuite = securityBuilder.build();
       this.connectionBuilder.setSecuritySuite(securitySuite);
    }
  }
 
  public void openConnection() throws IOException, InterruptedException {
    this.openConnection(DEFAULT_OPEN_WAIT_TIMEOUT, DEFAULT_OPEN_NUMBER_OF_TRIES);
  }

  public void openConnection(long waitTimeout, int nrOfTries) throws IOException, InterruptedException  {
    if (!this.open) {
      int cnt = 1;
      boolean success = false;
  
      while (!success && cnt <= nrOfTries) {
        try {
          this.dlmsConnection = this.connectionBuilder.build();
          success = true;
          this.open = true;
        } catch (IOException e) {
          LOG.info("Unable to open connection to " + this.ip + " on port " + this.port + ", wait for " + waitTimeout/1000 + " second(s), try (" + cnt + "/" +  nrOfTries + ")");
          cnt++;
          Thread.sleep(waitTimeout);
        }
      }
      
      if (!success) {
        LOG.error("Unable to open connection to " + this.ip + " on port " + this.port + " within " + nrOfTries + " try/tries");
        throw new RuntimeException("Unable to open connection to " + this.ip + " on port " + this.port + " within " + nrOfTries + " try/tries");
      }
      
      if (this.snObjectMapping == null) {
        this.retrieveSnObjectMapping();
      }
    }
  }

  public void closeConnection(long closeTimeout) throws IOException {
    if (this.open) {
      this.dlmsConnection.close();
      
      try {
        Thread.sleep(closeTimeout);
      } catch (InterruptedException e) {
        // do nothing
      }
      finally {
        this.open = false;
      }
    }
  }

  public Map<ObisCode, SnObjectInfo> getSnObjectMapping() {
    return this.snObjectMapping;
  }

  public Map<ObisCode, SnObjectInfo> retrieveSnObjectMapping() throws IOException {
    this.snObjectMapping = SnObjectInfo.retrieveLnSnMappingFrom(dlmsConnection);
    
    return this.snObjectMapping; 
  }

  public void setSnObjectMapping(Map<ObisCode, SnObjectInfo> snObjectMapping) {
    this.snObjectMapping = snObjectMapping;
  }

  public void closeConnection() throws IOException {
    closeConnection(this.closeTimeout);
  }

  public List<GetResult> get(List <AttributeAddress> attributeAddressList) throws IOException {
    return this.dlmsConnection.get(attributeAddressList);
  }
  
  public List<AccessResultCode> set(List<SetParameter> setParameterList) throws IOException {
    return this.dlmsConnection.set(setParameterList);
  }
  
  public List<MethodResult> action(List<MethodParameter> methodParameterList) throws IOException {
    return this.dlmsConnection.action(methodParameterList);
  }
  
}
