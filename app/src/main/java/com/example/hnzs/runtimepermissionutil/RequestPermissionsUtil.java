package com.example.hnzs.runtimepermissionutil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Android6.0以上动态添加权限
 * @author yh
 * @date 2016-9-22
 */
public class RequestPermissionsUtil {
	/**
	 * 存储监听对象和其对应的code值
	 */
	private static Map<Integer, PermissionsResultListener> permissionsResultListenerMap = new HashMap<Integer, PermissionsResultListener>();
	private static String SharedPreferencesFile = "RequestPermissionsUtil";

	/**
	 * 动态添加权限
	 * @param fragment fragment上下文
	 * @param permissions 权限名数组，如：new String[]{ Manifest.permission.CAMERA}，添加摄像头权限
	 * @param listener 当用户许可或者禁止的回调监听
	 */
	public static void needPermission(Fragment fragment, String[] permissions,
									  PermissionsResultListener listener) {
		requestPermissions(fragment, (int) SystemClock.uptimeMillis(),
				permissions, listener);
	}
	/**
	 * 动态添加权限
	 * @param activity activity上下文
	 * @param permissions 权限名数组，如：new String[]{ Manifest.permission.CAMERA}，添加摄像头权限
	 * @param listener 当用户许可或者禁止的回调监听
	 */
	public static void needPermission(Activity activity, String[] permissions,
									  PermissionsResultListener listener) {
		requestPermissions(activity, (int) SystemClock.uptimeMillis(),
				permissions, listener);
	}

	private static void requestPermissions(Object object, int requestCode,
										   String[] permissions, PermissionsResultListener listener) {
		//判断手机api是否小于等于22
		if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.LOLLIPOP_MR1){
			for(int i=0;i<permissions.length;i++){
				listener.succeed(permissions[i]);
			}
			return;
		}
		//将不需要动态添加的权限和用户已经否决的权限去除
		List<String> deniedPermissions = findDeniedPermissions(object,
				listener, permissions);

		if (deniedPermissions.size() > 0&&permissionsResultListenerMap!=null) {
			permissionsResultListenerMap.put(requestCode, listener);
			if (object instanceof Activity) {
				((Activity) object).requestPermissions(deniedPermissions
						.toArray(new String[deniedPermissions.size()]),
						requestCode);
			} else if (object instanceof Fragment) {
				((Fragment) object).requestPermissions(deniedPermissions
						.toArray(new String[deniedPermissions.size()]),
						requestCode);
			} else {
				throw new IllegalArgumentException(object.getClass().getName()
						+ " is not supported Context");
			}
		}
	}

	
	private static List<String> findDeniedPermissions(Object object,
			PermissionsResultListener listener, String... permission) {
		
		List<String> denyPermissions = new ArrayList<String>();
		for (String value : permission) {
			if (ContextCompat.checkSelfPermission((Context) object, value) != PackageManager.PERMISSION_GRANTED) {
				denyPermissions.add(value);
			} else {
				listener.succeed(value);
			}
		}
		return denyPermissions;
	}
	/**
	 * 在onRequestPermissionsResult方法中调用该函数。
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	public static void setPermissionsResult(Context context,int requestCode,
			String[] permissions, int[] grantResults) {
		if(permissionsResultListenerMap==null){
			return;
		}
		PermissionsResultListener li = permissionsResultListenerMap
				.get(requestCode);
		SharedPreferences sp = context.getSharedPreferences(SharedPreferencesFile,
				Context.MODE_PRIVATE);
		if (li != null) {
			for (int i = 0; i < grantResults.length; i++) {
				if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
					li.succeed(permissions[i]);
				} else {
					int num = sp.getInt(permissions[i], -1);
					if (num != -1) {
						sp.edit().putInt(permissions[i], ++num).commit();
						li.fail(permissions[i], num);
					} else{
						
						li.fail(permissions[i], 1);
						sp.edit().putInt(permissions[i], 1).commit();
					}

				}
			}
		}
		permissionsResultListenerMap.remove(requestCode);
		sp = null;
	}

	/**
	 * 用户许可或者禁止权限添加的回调
	 * @author yh
	 *
	 */
	public interface PermissionsResultListener {
		/**
		 * 当用户允许该权限的添加时，回调该方法。注意：当你一次申请多条权限时，该方法会回调用户用意次。比如：在needPermission方法中
		 * 申请了3条权限，用户同意了两条，则该方法会回调两次。
		 * @param permissions 用户同已添加的权限名称。
		 */
		public void succeed(String permissions);
		/**
		 * 当用户禁止该权限添加时，回调该方法。
		 * @param permissions 用户禁止添加的权限名称。
		 * @param num 你向用户申请该权限的次数。注意：在原生的系统下，当你第一次向用户申请该权限被禁止后，第二次再申请时，shouldShowRequestPermissionRationale（）方法会返回true。但是在部分定制系统中该方法会永远返回false。
		 */
		public void fail(String permissions, int num);
	}
}
