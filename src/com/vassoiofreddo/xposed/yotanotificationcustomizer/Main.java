package com.vassoiofreddo.xposed.yotanotificationcustomizer;

import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findMethodBestMatch;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

import android.os.Environment;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Main implements IXposedHookLoadPackage {

	private final static String PKG = "com.yotadevices.notificationwidget";

	private static String[] packagesToAdd = null;
	private static final String FILE_TEMPLATE =
			"# Package names listed in this file (one per row) will not appear in always-on notification widget.\n"+
			"# Reboot is necessary for modification on this file to take effect.\n"+
			"# For instance the following line (uncomment to enable) will exclude google now notifications\n"+
			"#com.google.android.googlequicksearchbox"+
			"\n";

	@Override
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals(PKG))
			return;

		findAndHookConstructor("com.yotadevices.notifsutils.YotaCommonNotifsFilter", lpparam.classLoader,
				"android.content.Context", new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable {

						if(packagesToAdd==null){
							XposedBridge.log("reading file no-notification.properties");
							File sdcard = Environment.getExternalStorageDirectory();
							File file = new File(sdcard,"no-notification.properties");
							if(file.exists() && file.isFile()){
								XposedBridge.log("file found");
								Properties prop = new	Properties();
								try(FileReader fr = new FileReader(file)){
									prop.load(fr);
									packagesToAdd = new String[prop.size()];
									int i=0;
									for(Object key:prop.keySet()){
										packagesToAdd[i++] = (String)key;
										XposedBridge.log("["+i+"] adding to exclude list: "+key);
									}
								}
							} else {
								try(FileWriter bw = new FileWriter(file)){
									bw.write(FILE_TEMPLATE);
								}
								packagesToAdd = new String[0];
							}
						}

						findMethodBestMatch(param.thisObject.getClass(), "addPackages", packagesToAdd.getClass())
								.invoke(param.thisObject, new Object[] { packagesToAdd });
					}
				});

//		findAndHookMethod("com.yotadevices.notifsutils.YotaCommonNotifsFilter", lpparam.classLoader, "match",
//				StatusBarNotification.class, new XC_MethodHook() {
//					@Override
//					protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//						StatusBarNotification n = (StatusBarNotification) param.args[0];
//						XposedBridge.log("match package: " + n.getPackageName() + " result:" + param.getResult());
//					}
//				});

	}

}