package mgks.os.swv;

/*
  Smart WebView v7
  https://github.com/mgks/Android-SmartWebView

  A modern, open-source WebView wrapper for building advanced hybrid Android apps.
  Native features, modular plugins, and full customisation—built for developers.

  - Documentation: https://docs.mgks.dev/smart-webview  
  - Plugins: https://docs.mgks.dev/smart-webview/plugins  
  - Discussions: https://github.com/mgks/Android-SmartWebView/discussions  
  - Sponsor the Project: https://github.com/sponsors/mgks  

  MIT License — https://opensource.org/licenses/MIT  

  Mentioning Smart WebView in your project helps others find it and keeps the dev loop alive.
*/

import android.os.Build;

public class MetaPull {
	String swv(){
		return "SWV.RELEASE : 7.1"
			+"\nSWV.BUILD : 7"
			+"\nSWV.SDK.MIN : 23"
			+"\nSWV.SDK.MAX : 35"
			+"\nSWV.BUILD.TYPE : release"
			+"\nSWV.BUILD.NAME : 7.1"
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
