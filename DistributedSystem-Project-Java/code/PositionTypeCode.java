
/*
  WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

  This file was generated from .idl using "rtiddsgen".
  The rtiddsgen tool is part of the RTI Data Distribution Service distribution.
  For more information, type 'rtiddsgen -help' at a command shell
  or consult the RTI Data Distribution Service manual.
*/
    
import com.rti.dds.typecode.*;


public class PositionTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int i=0;
        StructMember sm[] = new StructMember[8];

        sm[i]=new StructMember("timestamp",false,(short)-1,false,(TypeCode)new TypeCode(TCKind.TK_STRING,255)); i++;
        sm[i]=new StructMember("route",false,(short)-1,false,(TypeCode)new TypeCode(TCKind.TK_STRING,255)); i++;
        sm[i]=new StructMember("vehicle",false,(short)-1,false,(TypeCode)new TypeCode(TCKind.TK_STRING,255)); i++;
        sm[i]=new StructMember("stopNumber",false,(short)-1,false,(TypeCode)TypeCode.TC_LONG); i++;
        sm[i]=new StructMember("numStops",false,(short)-1,false,(TypeCode)TypeCode.TC_LONG); i++;
        sm[i]=new StructMember("timeBetweenStops",false,(short)-1,false,(TypeCode)TypeCode.TC_FLOAT); i++;
        sm[i]=new StructMember("trafficConditions",false,(short)-1,false,(TypeCode)new TypeCode(TCKind.TK_STRING,255)); i++;
        sm[i]=new StructMember("fillInRatio",false,(short)-1,false,(TypeCode)TypeCode.TC_LONG); i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("Position",sm);
        return tc;
    }
}
