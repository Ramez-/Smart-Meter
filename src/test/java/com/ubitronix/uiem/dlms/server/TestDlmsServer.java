package com.ubitronix.uiem.dlms.server;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SnObjectInfo;

import com.ubitronix.uiem.dlms.client.DlmsClient;
import com.ubitronix.uiem.dlms.client.TestClient;

public class TestDlmsServer {
  private static final Log LOG = LogFactory.getLog(TestClient.class.getName());

  private static Integer PORT = 5200;
  private static Long DEVICE_ID = 32l;
  private static Integer LOGICAL_DEVICE_ID = 1;
  private static String LOGICA_DEVICE_NAME = "SSE111";
  private static String MANUFACTURER_ID = "SSE";
  private static String PASSWORD = "Password";
  private static Integer CLIENT_ID = 16;
  private static Integer PHYSICAL_DEVICE_ADDRESS = 6713;

  private static Map<ObisCode, SnObjectInfo> SN_OBJECT_MAPPING = new LinkedHashMap<ObisCode, SnObjectInfo>();
  
  static {
    SnObjectInfo value = new SnObjectInfo(0xA0, 99, 12, 3, 5 << 3); // shift to the left, since first three bits are zero
    SN_OBJECT_MAPPING.put(SnHelloClass.INSTANCE_ID, value);
  }

  public void testOpen() throws Exception {
    LOG.info("Running testOpen");
    DlmsServer server = new DlmsServer(LOGICAL_DEVICE_ID, LOGICA_DEVICE_NAME, MANUFACTURER_ID, DEVICE_ID, PORT, PASSWORD);
    
    try {
      server.open();
    }
    finally {
      server.close();
    }
  }

  public void testGet() throws Exception {
    LOG.info("Running testGet");
    DlmsServer server = new DlmsServer(LOGICAL_DEVICE_ID, LOGICA_DEVICE_NAME, MANUFACTURER_ID, DEVICE_ID, PORT, PASSWORD);
    
    try {
      server.open();

      DlmsClient client = new DlmsClient(InetAddress.getLocalHost().getHostAddress(), PORT, CLIENT_ID, PASSWORD, LOGICAL_DEVICE_ID, PHYSICAL_DEVICE_ADDRESS, SN_OBJECT_MAPPING);
      try {
        client.openConnection();
        
        AttributeAddress attributeAddress = new AttributeAddress(99, SnHelloClass.INSTANCE_ID, 2);
        GetResult getResult = client.get(Arrays.asList(attributeAddress)).get(0);

        if (getResult.getResultCode() == AccessResultCode.SUCCESS) {
          byte[] val = getResult.getResultData().getValue();
          LOG.info(new String(val));
        } else {
          LOG.info(getResult.getResultCode());
        }
      }
      finally {
        if (client != null) {
          client.closeConnection();
        }
      }
    }
    finally {
      server.close();
    }
  }

  public void testAction() throws Exception {
    LOG.info("Running testAction");
    DlmsServer server = new DlmsServer(LOGICAL_DEVICE_ID, LOGICA_DEVICE_NAME, MANUFACTURER_ID, DEVICE_ID, PORT, PASSWORD);
    
    try {
      server.open();
      
      DlmsClient client = new DlmsClient(InetAddress.getLocalHost().getHostAddress(), PORT, CLIENT_ID, PASSWORD, LOGICAL_DEVICE_ID, PHYSICAL_DEVICE_ADDRESS, SN_OBJECT_MAPPING);
      try {
        client.openConnection();
        
        MethodParameter methodParameter = new MethodParameter(99, SnHelloClass.INSTANCE_ID, 3);
        MethodResult methodResult = client.action(Arrays.asList(methodParameter)).get(0);

        if (methodResult.getResultCode() == MethodResultCode.SUCCESS) {
          byte[] val = methodResult.getResultData().getValue();
          LOG.info(new String(val));
        } else {
          LOG.info(methodResult.getResultCode());
        }
      }
      finally {
        if (client != null) {
          client.closeConnection();
        }
      }
    }
    finally {
      server.close();
    }
  }
}
