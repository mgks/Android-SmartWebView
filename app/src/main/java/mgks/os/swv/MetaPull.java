package mgks.os.swv;

/*
  Smart WebView 7.0

  MIT License (https://opensource.org/licenses/MIT)

  Smart WebView is an Open Source project that integrates native features into
  WebView to help create advanced hybrid applications (https://github.com/mgks/Android-SmartWebView).

  Explore plugins and enhanced capabilities: (https://mgks.dev/app/smart-webview-documentation#plugins)
  Join the discussion: (https://github.com/mgks/Android-SmartWebView/discussions)
  Support Smart WebView: (https://github.com/sponsors/mgks)

  Your support and acknowledgment of the project's source are greatly appreciated.
  Giving credit to developers encourages them to create better projects.
*/

import android.os.Build;

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
