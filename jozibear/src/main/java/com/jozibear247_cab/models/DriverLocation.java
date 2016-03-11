/**
 * 
 */
package com.jozibear247_cab.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author Hardik A Bhalodi
 * 
 */
public class DriverLocation {
	private LatLng latLng;
	private String distance;
	private String unit;

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

}
