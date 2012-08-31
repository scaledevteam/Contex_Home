package ps.age.util;

import android.content.*;
import android.location.*;
import android.os.*;

public class GpsHandler implements LocationListener {

	LocationManager locManager;
	Context context;
	LocListener listener;
	public static final int GPS_MSG = 12114626;

	public GpsHandler(Context context) {
		this.context = context;
		locManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				this);
	}

	public void showGpsOptions() {
		Intent gpsOptionsIntent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		gpsOptionsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(gpsOptionsIntent);
	}

	public double[] getLastKnownLocation() {
		if (isGpsEnabled()) {
			android.location.Location loc = locManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			
			double[] array = new double[] { loc.getLatitude(),
					loc.getLongitude() };
			return array;
		}
		return null;
	}

	public void setLocationListener(LocListener listener) {
		this.listener = listener;
	}

	/*
	 * checks if the GPS is enabled
	 */
	public boolean isGpsEnabled() {
		return locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/*
	 * Starts Listining for GpsStatus and Location updates
	 */
	/*
	 * Stops Listening for GpsStatus and Location updates
	 */
	public void stopListening() {
		locManager.removeUpdates(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onLocationChanged(android.location.
	 * Location)
	 */
	@Override
	public void onLocationChanged(Location loc) {
		listener.updateLocation(loc.getLongitude(), loc.getLatitude());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String,
	 * int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
	}

}
