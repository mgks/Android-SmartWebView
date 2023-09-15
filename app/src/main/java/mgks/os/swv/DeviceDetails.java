package mgks.os.swv;

/*
 * Smart WebView 7.0 (May 2023)
 * Smart WebView is an Open Source project that integrates native features into webview to help create advanced hybrid applications. Available on GitHub (https://github.com/mgks/Android-SmartWebView).
 * Initially developed by Ghazi Khan (https://github.com/mgks) under MIT Open Source License.
 * This program is free to use for private and commercial purposes under MIT License (https://opensource.org/licenses/MIT).
 * Please mention project source or developer credits in your Application's License(s) Wiki.
 * Contribute to the project (https://github.com/mgks/Android-SmartWebView/discussions)
 * Sponsor the project (https://github.com/sponsors/mgks)
 * Giving right credits to developers encourages them to keep improving their projects :)
 */

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
