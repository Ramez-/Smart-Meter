package com.ubitronix.uiem.dlms.server;

import static org.openmuc.jdlms.AttributeAccessMode.READ_AND_WRITE;
import static org.openmuc.jdlms.datatypes.DataObject.newOctetStringData;

import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemMethod;
import org.openmuc.jdlms.CosemSnInterfaceObject;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;

@CosemClass(id = 99, version = 12)
public class SnHelloClass extends CosemSnInterfaceObject {

    public static final ObisCode INSTANCE_ID = new ObisCode("1.11.123.55.1.13");

    @CosemAttribute(id = 2, type = Type.OCTET_STRING, accessMode = READ_AND_WRITE)
    private DataObject d1;

    public SnHelloClass() {
        super(0xA0, INSTANCE_ID.toString());

        this.d1 = DataObject.newOctetStringData("Hello".getBytes());
    }

    public void setD1(DataObject d1) {
        this.d1 = d1;
    }

    @CosemMethod(id = 3)
    public DataObject hello() {
        return newOctetStringData("Hello".getBytes());
    }
}
