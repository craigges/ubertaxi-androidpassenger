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

	private String make;
	private String regno;
	private String color;
	private String picture_car;

	public Driver() {

	}

	public Driver(Parcel in) {
		this.firstName = in.readString();
		this.lastName = in.readString();
		this.phone = in.readString();
		this.bio = in.readString();
		this.picture = in.readString();
		this.latitude = in.readDouble();
		this.longitude = in.readDouble();
		this.d_latitude = in.readDouble();
		this.d_longitude = in.readDouble();
		this.rating = in.readDouble();
		this.lastTime = in.readString();
		this.lastDistance = in.readString();

		this.make = in.readString();
		this.regno = in.readString();
		this.color = in.readString();
		this.picture_car = in.readString();

		this.bill = (Bill) in.readValue(Bill.class.getClassLoader());
	}

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

	public void setMake(String make) {
		this.make = make;
	}

	public void setRegno(String regno) {
		this.regno = regno;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setPicture_car(String picture_car) {
		this.picture_car = picture_car;
	}

	public String getPicture_car() {
		return picture_car;
	}

	public String getMake() {
		return make;
	}

	public String getRegno() {
		return regno;
	}

	public String getColor() {
		return color;
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
		dest.writeString(this.firstName);
		dest.writeString(this.lastName);
		dest.writeString(this.phone);
		dest.writeString(this.bio);
		dest.writeString(this.picture);
		dest.writeDouble(this.latitude);
		dest.writeDouble(this.longitude);
		dest.writeDouble(this.d_latitude);
		dest.writeDouble(this.d_longitude);
		dest.writeDouble(this.rating);
		dest.writeString(this.lastTime);
		dest.writeString(this.lastDistance);
		dest.writeString(this.make);
		dest.writeString(this.regno);
		dest.writeString(this.color);
		dest.writeSerializable(this.picture_car);
		dest.writeValue(this.bill);

	}

	public static final Parcelable.Creator<Driver> CREATOR = new Parcelable.Creator() {

		@Override
		public Object createFromParcel(Parcel source) {
			return new Driver(source);
		}

		@Override
		public Object[] newArray(int size) {
			return new Driver[size];
		}

	};
}
