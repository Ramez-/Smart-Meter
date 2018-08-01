package com.ubitronix.uiem.execution.adapter.dlms.cosem.util;

import org.openmuc.jdlms.ObisCode;

// TODO need to be extracted to the database
public class ObisCodeMapper {
  public static ObisCode getClockTimeObisCode() {
    return new ObisCode("0.0.1.0.0.255"); 
  }
}
