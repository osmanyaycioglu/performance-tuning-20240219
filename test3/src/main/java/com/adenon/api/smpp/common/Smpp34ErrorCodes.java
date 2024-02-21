package com.adenon.api.smpp.common;

public class Smpp34ErrorCodes {

    public static final int ERROR_CODE_ROK               = 0x00000000; // No Error
    public static final int ERROR_CODE_RINVMSGLEN        = 0x00000001; // Message Length is invalid
    public static final int ERROR_CODE_RINVCMDLEN        = 0x00000002; // Command Length is invalid
    public static final int ERROR_CODE_RINVCMDID         = 0x00000003; // Invalid Command ID
    public static final int ERROR_CODE_RINVBNDSTS        = 0x00000004; // Incorrect BIND Status for given command
    public static final int ERROR_CODE_RALYBND           = 0x00000005; // ESME Already in Bound State
    public static final int ERROR_CODE_RINVPRTFLG        = 0x00000006; // Invalid Priority Flag
    public static final int ERROR_CODE_RINVREGDLVFLG     = 0x00000007; // Invalid Registered Delivery Flag
    public static final int ERROR_CODE_RSYSERR           = 0x00000008; // System Error
    // Reserved 0x00000009 ; // Reserved
    public static final int ERROR_CODE_RINVSRCADR        = 0x0000000A; // Invalid Source Address
    public static final int ERROR_CODE_RINVDSTADR        = 0x0000000B; // Invalid Dest Addr
    public static final int ERROR_CODE_RINVMSGID         = 0x0000000C; // Message ID is invalid
    public static final int ERROR_CODE_RBINDFAIL         = 0x0000000D; // Bind Failed
    public static final int ERROR_CODE_RINVPASWD         = 0x0000000E; // Invalid Password
    public static final int ERROR_CODE_RINVSYSID         = 0x0000000F; // Invalid System ID
    // Reserved 0x00000010 ; // reserved
    public static final int ERROR_CODE_RCANCELFAIL       = 0x00000011; // Cancel SM Failed
    // Reserved 0x00000012 ; // Reserved
    public static final int ERROR_CODE_RREPLACEFAIL      = 0x00000013; // Replace SM Failed
    public static final int ERROR_CODE_RMSGQFUL          = 0x00000014; // Message Queue Full
    public static final int ERROR_CODE_RINVSERTYP        = 0x00000015; // Invalid Service Type
    // Reserved 0x00000016-0x00000032 Reserved
    public static final int ERROR_CODE_RINVNUMDESTS      = 0x00000033; // Invalid number of destinations
    public static final int ERROR_CODE_RINVDLNAME        = 0x00000034; // Invalid Distribution List name
    // Reserved 0x00000035-0x0000003F Reserved
    public static final int ERROR_CODE_RINVDESTFLAG      = 0x00000040; // Destination flag is invalid(submit_multi)
    // Reserved 0x00000041 Reserved
    public static final int ERROR_CODE_RINVSUBREP        = 0x00000042; // Invalid submit with replace request(i.e. submit_sm with
                                                                       // replace_if_present_flag
    // set)
    public static final int ERROR_CODE_RINVESMCLASS      = 0x00000043; // Invalid esm_class field data
    public static final int ERROR_CODE_RCNTSUBDL         = 0x00000044; // Cannot Submit to Distribution List
    public static final int ERROR_CODE_RSUBMITFAIL       = 0x00000045; // submit_sm or submit_multi failed
    // Reserved 0x00000046-0x00000047 Reserved
    public static final int ERROR_CODE_RINVSRCTON        = 0x00000048; // Invalid Source address TON
    public static final int ERROR_CODE_RINVSRCNPI        = 0x00000049; // Invalid Source address NPI
    public static final int ERROR_CODE_RINVDSTTON        = 0x00000050; // Invalid Destination address TON
    public static final int ERROR_CODE_RINVDSTNPI        = 0x00000051; // Invalid Destination address NPI
    // Reserved 0x00000052 Reserved
    public static final int ERROR_CODE_RINVSYSTYP        = 0x00000053; // Invalid system_type field
    public static final int ERROR_CODE_RINVREPFLAG       = 0x00000054; // Invalid replace_if_present flag
    public static final int ERROR_CODE_RINVNUMMSGS       = 0x00000055; // Invalid number of messages
    // Reserved 0x00000056-0x00000057 Reserved
    public static final int ERROR_CODE_RTHROTTLED        = 0x00000058; // Throttling error (ESME has exceeded allowed message limits)
    public static final int ERROR_CODE_RINVSCHED         = 0x00000061; // Invalid Scheduled Delivery Time
    public static final int ERROR_CODE_RINVEXPIRY        = 0x00000062; // Invalid message validity period(Expiry time)
    public static final int ERROR_CODE_RINVDFTMSGID      = 0x00000063; // Predefined Message Invalid or Not Found
    public static final int ERROR_CODE_RX_T_APPN         = 0x00000064; // ESME Receiver Temporary App Error Code
    public static final int ERROR_CODE_RX_P_APPN         = 0x00000065; // ESME Receiver Permanent App Error Code
    public static final int ERROR_CODE_RX_R_APPN         = 0x00000066; // ESME Receiver Reject Message Error Code
    public static final int ERROR_CODE_RQUERYFAIL        = 0x00000067; // query_sm request failed
    // Reserved 0x00000068-0x000000BF Reserved
    public static final int ERROR_CODE_RINVOPTPARSTREAM  = 0x000000C0; // Error in the optional part of the PDU Body.
    public static final int ERROR_CODE_ROPTPARNOTALLWD   = 0x000000C1; // Optional Parameter not allowed
    public static final int ERROR_CODE_RINVPARLEN        = 0x000000C2; // Invalid Parameter Length.
    public static final int ERROR_CODE_RMISSINGOPTPARAM  = 0x000000C3; // Expected Optional Parameter missing
    public static final int ERROR_CODE_RINVOPTPARAMVAL   = 0x000000C4; // Invalid Optional Parameter Value
    // Reserved 0x000000C5-0x000000FD Reserved
    public static final int ERROR_CODE_RDELIVERYFAILURE  = 0x000000FE; // Delivery Failure (used for data_sm_resp)
    public static final int ERROR_CODE_RUNKNOWNERR       = 0x000000FF; // Unknown Error
    // Reserved 0x00000100-0x000003FF Reserved for SMPP extension
    public static final int ERROR_CODE_RETRYSUBMIT       = 0x00000100; // Unknown Error
    // Reserved SMSC 0x00000400-0x000004FF Reserved for SMSC vendor specific errors
    // Reserved 0x00000500-0xFFFFFFFF Reserved
    public static final int ERROR_CODE_NO_ENOUGH_CREDITS = 0x00000500;

}
