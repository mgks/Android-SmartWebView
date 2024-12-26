package mgks.os.swv;

import android.os.Build;

import com.google.firebase.BuildConfig;

public class MetaPull {
	String swv(){
		return "SWV.RELEASE : 7.0"
			+"\nSWV.BUILD : 7"
			+"\nSWV.SDK.MIN : 23"
			+"\nSWV.SDK.MAX : 35"
			+"\nSWV.BUILD.TYPE : release"
			+"\nSWV.BUILD.NAME : 7.0"
			+"\nSWV.PACKAGE.NAME : mgks.os.swv";
	}

	String device(){
		return "VERSION.RELEASE : "+Build.VERSION.RELEASE
			+"\nVERSION.SDK.NUMBER : "+Build.VERSION.SDK_INT
			+"\nMANUFACTURER : "+Build.MANUFACTURER
			+"\nMODEL : "+Build.MODEL;

			// consider these if really necessary
			/*
			//+"\nVERSION.INCREMENTAL : "+Build.VERSION.INCREMENTAL
			//+"\nBOARD : "+Build.BOARD
			//+"\nBOOTLOADER : "+Build.BOOTLOADER
			//+"\nDISPLAY : "+Build.DISPLAY
			//+"\nFINGERPRINT : "+Build.FINGERPRINT
			//+"\nHARDWARE : "+Build.HARDWARE
			//+"\nBRAND : "+Build.BRAND
			//+"\nHOST : "+Build.HOST
			//+"\nID : "+Build.ID
			//+"\nPRODUCT : "+Build.PRODUCT
			//+"\nTAGS : "+Build.TAGS
			//+"\nTIME : "+Build.TIME
			//+"\nTYPE : "+Build.TYPE
			//+"\nUNKNOWN : "+Build.UNKNOWN
			//+"\nUSER : "+ Build.USER;
			*/
	}
}
