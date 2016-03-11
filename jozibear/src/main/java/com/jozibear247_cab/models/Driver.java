/**
 * 
 */
package com.jozibear247_cab.models;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Hardik A Bhalodi
 * 
 */
@SuppressWarnings("serial")
public class Driver implements Parcelable {

	/**
	 * 
	 */

	private String firstName;
	private String lastName;

	private String phone;
	private String bio;
	private String picture;
	private double latitude;
	private double longitude;
	private double d_latitude;
	private double d_longitude;
	private double rating;
	private String lastTime;
	private String lastDistance;
	private Bill bill;

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getLastDistance() {
		return lastDistance;
	}

	public void setLastDistance(String lastDistance) {
		this.lastDistance = lastDistance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

	public double getD_latitude() {
		return d_latitude;
	}

	public void setD_latitude(double d_latitude) {
		this.d_latitude = d_latitude;
	}

	public double getD_longitude() {
		return d_longitude;
	}

	public void setD_longitude(double d_longitude) {
		this.d_longitude = d_longitude;
	}
}
