package mgks.os.swv;

import android.os.Build;

public class DeviceDetails {
	String pull(){
		return "VERSION.RELEASE : "+Build.VERSION.RELEASE
		+"\nVERSION.INCREMENTAL : "+Build.VERSION.INCREMENTAL
		+"\nVERSION.SDK.NUMBER : "+Build.VERSION.SDK_INT
		+"\nBOARD : "+Build.BOARD
		+"\nBOOTLOADER : "+Build.BOOTLOADER
		+"\nBRAND : "+Build.BRAND
		+"\nDISPLAY : "+Build.DISPLAY
		+"\nFINGERPRINT : "+Build.FINGERPRINT
		+"\nHARDWARE : "+Build.HARDWARE
		+"\nHOST : "+Build.HOST
		+"\nID : "+Build.ID
		+"\nMANUFACTURER : "+Build.MANUFACTURER
		+"\nMODEL : "+Build.MODEL
		+"\nPRODUCT : "+Build.PRODUCT
		+"\nTAGS : "+Build.TAGS
		+"\nTIME : "+Build.TIME
		+"\nTYPE : "+Build.TYPE
		+"\nUNKNOWN : "+Build.UNKNOWN
		+"\nUSER : "+ Build.USER;
	}
}
