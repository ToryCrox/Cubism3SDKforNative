/**
 *
 *  You can modify and use this source freely
 *  only for the development of application related Live2D.
 *
 *  (c) Live2D Inc. All rights reserved.
 */
package com.live2d.demo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.SystemClock;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/*
 * 加速度センサの情報の管理。
 *
 */
public class AccelHelper {

	private float acceleration_x = 0;
	private float acceleration_y = 0;
	private float acceleration_z = 0;
	private float dst_acceleration_x = 0;
	private float dst_acceleration_y = 0;
	private float dst_acceleration_z = 0;

	private float last_dst_acceleration_x = 0;
	private float last_dst_acceleration_y = 0;
	private float last_dst_acceleration_z = 0;

	private long lastTimeMSec = -1;
	private float lastMove;

	private MySensorListener sensorListener;
	private SensorManager sensorManager;

	private float[] accelerometerValues = new float[3];
	private float[] geomagneticMatrix = new float[3];
	private boolean sensorReady;
	private final Context activity;
	private final Sensor accelerometer;
	private final Sensor magneticField;

	private float accel[] = new float[3];

	public AccelHelper(Context activity) {
		sensorListener = new MySensorListener();
		sensorManager = (SensorManager) activity.getSystemService(Activity.SENSOR_SERVICE);

		this.activity = activity;
		if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() > 0 && sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).size() > 0) {
			accelerometer = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			magneticField = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0);
		} else {
			accelerometer = null;
			magneticField = null;
		}
		//start();
	}

	/*
	 * デバイスを振ったときなどにどのくらい揺れたかを取得。 1を超えるとそれなりに揺れた状態。 resetShake()を使ってリセットできる。
	 * 
	 * @return
	 */
	public float getShake() {
		return lastMove;
	}

	/*
	 * シェイクイベントが連続で発生しないように揺れをリセットする。
	 */
	public void resetShake() {
		lastMove = 0;
	}

	/*
	 * 計測を開始する
	 */
	public void start() {
		try {
			if (accelerometer == null || magneticField == null)
				return;
			sensorManager.registerListener(sensorListener, magneticField, SensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 計測を停止する
	 */
	public void stop() {
		try {
			sensorManager.unregisterListener(sensorListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 画面の回転を取得。
	 * 
	 * @param act
	 * 
	 * @return
	 */
	private static int getDispRotation(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = windowManager.getDefaultDisplay();
		return DispRotateGetter.getInstance().getRotate(d);
	}

	/*
	 * 加速度が更新された時に呼ばれる
	 */
	public void setCurAccel(float a1, float a2, float a3) {
		dst_acceleration_x = a1;
		dst_acceleration_y = a2;
		dst_acceleration_z = a3;

		// 以下はシェイクイベント用の処理
		float move = fabs(dst_acceleration_x - last_dst_acceleration_x) + fabs(dst_acceleration_y - last_dst_acceleration_y) + fabs(dst_acceleration_z - last_dst_acceleration_z);
		lastMove = lastMove * 0.7f + move * 0.3f;

		last_dst_acceleration_x = dst_acceleration_x;
		last_dst_acceleration_y = dst_acceleration_y;
		last_dst_acceleration_z = dst_acceleration_z;
	}

	/*
	 * 更新
	 */
	public void update() {
		final float MAX_ACCEL_D = 0.04f;// setCurAccelの間隔が長い場合は、最大値を小さくする必要がある
		float dx = dst_acceleration_x - acceleration_x;
		float dy = dst_acceleration_y - acceleration_y;
		float dz = dst_acceleration_z - acceleration_z;

		if (dx > MAX_ACCEL_D)
			dx = MAX_ACCEL_D;
		if (dx < -MAX_ACCEL_D)
			dx = -MAX_ACCEL_D;

		if (dy > MAX_ACCEL_D)
			dy = MAX_ACCEL_D;
		if (dy < -MAX_ACCEL_D)
			dy = -MAX_ACCEL_D;

		if (dz > MAX_ACCEL_D)
			dz = MAX_ACCEL_D;
		if (dz < -MAX_ACCEL_D)
			dz = -MAX_ACCEL_D;

		acceleration_x += dx;
		acceleration_y += dy;
		acceleration_z += dz;

		long time = SystemClock.uptimeMillis();
		long diff = time - lastTimeMSec;

		lastTimeMSec = time;

		float scale = 0.2f * diff * 60 / (1000.0f); // 経過時間に応じて、重み付けをかえる
		final float MAX_SCALE_VALUE = 0.5f;
		if (scale > MAX_SCALE_VALUE)
			scale = MAX_SCALE_VALUE;

		accel[0] = (acceleration_x * scale) + (accel[0] * (1.0f - scale));
		accel[1] = (acceleration_y * scale) + (accel[1] * (1.0f - scale));
		accel[2] = (acceleration_z * scale) + (accel[2] * (1.0f - scale));
	}

	/*
	 * 絶対値計算
	 * 
	 * @param v
	 * 
	 * @return
	 */
	private float fabs(float v) {
		return v > 0 ? v : -v;
	}

	/*
	 * 横方向の回転を取得。 寝かせた状態で0。(表裏関係なく) 左に回転させると-1,右に回転させると1になる。
	 *
	 * @return
	 */
	public float getAccelX() {
		return accel[0];
	}

	/*
	 * 上下の回転を取得。 寝かせた状態で0。(表裏関係なく) デバイスが垂直に立っているときに-1、逆さまにすると1になる。
	 *
	 * @return
	 */
	public float getAccelY() {
		return accel[1];
	}

	/*
	 * 上下の回転を取得。 立たせた状態で0。 表向きに寝かせると-1、裏向きに寝かせると1になる
	 * 
	 * @return
	 */
	public float getAccelZ() {
		return accel[2];
	}

	/*
	 * 画面の回転を取得するクラス。
	 *
	 */
	private static class DispRotateGetter {
		private static IDispRotateGetter getInstance() {
			// 2.2からのAPIを使っているのでOSのバージョンによって処理を分ける
			if (Build.VERSION.SDK_INT >= 8) {
				// for 2.2 or higher
				return new DispRotateGetterV8();
			} else {
				// for 2.1 or lower
				return new DispRotateGetterV1();
			}
		}

		private interface IDispRotateGetter {
			int getRotate(Display d);
		}

		private static class DispRotateGetterV8 implements IDispRotateGetter {
			public int getRotate(Display d) {
				return d.getRotation();
			}
		}

		private static class DispRotateGetterV1 implements IDispRotateGetter {
			public int getRotate(Display d) {
				@SuppressWarnings("deprecation")
				int r = d.getOrientation();
				return (r == 0 ? Surface.ROTATION_0 : Surface.ROTATION_90);
			}
		}
	}

	/*
	 * イベントリスナー
	 */
	private class MySensorListener implements SensorEventListener {
		public void onAccuracyChanged(Sensor sensor, int i) {
		}

		public void onSensorChanged(SensorEvent e) {
			switch (e.sensor.getType()) {
				case Sensor.TYPE_ACCELEROMETER:
					accelerometerValues = e.values.clone();
					break;
				case Sensor.TYPE_MAGNETIC_FIELD:
					geomagneticMatrix = e.values.clone();
					sensorReady = true;
					break;
			}

			if (geomagneticMatrix != null && accelerometerValues != null && sensorReady) {
				sensorReady = false;

				float[] R = new float[16];
				float[] I = new float[16];

				SensorManager.getRotationMatrix(R, I, accelerometerValues, geomagneticMatrix);
				// 画面の回転状態を取得する
				int dr = getDispRotation(activity);
				float x = 0;
				float y = 0;
				float z = 0;
				if (dr == Surface.ROTATION_0) {
					// 回転無し
					// アンドロイド版はiPhoneと逆になるようなので - を設定
					x = -accelerometerValues[0] / SensorManager.GRAVITY_EARTH;
					y = -accelerometerValues[1] / SensorManager.GRAVITY_EARTH;
					z = -accelerometerValues[2] / SensorManager.GRAVITY_EARTH;
				} else if (dr == Surface.ROTATION_90) {
					x = accelerometerValues[1] / SensorManager.GRAVITY_EARTH;
					y = -accelerometerValues[0] / SensorManager.GRAVITY_EARTH;
					z = -accelerometerValues[2] / SensorManager.GRAVITY_EARTH;
				} else if (dr == Surface.ROTATION_180) {
					x = accelerometerValues[0] / SensorManager.GRAVITY_EARTH;
					y = accelerometerValues[1] / SensorManager.GRAVITY_EARTH;
					z = -accelerometerValues[2] / SensorManager.GRAVITY_EARTH;
				} else if (dr == Surface.ROTATION_270) {
					// タブレットで回転軸がずれているときは、正面から見てピッチ、ヨーを逆転
					x = -accelerometerValues[1] / SensorManager.GRAVITY_EARTH;
					y = accelerometerValues[0] / SensorManager.GRAVITY_EARTH;
					z = -accelerometerValues[2] / SensorManager.GRAVITY_EARTH;
				}

				// 更新
				setCurAccel(x, y, z);
			}
		}
	}
}
