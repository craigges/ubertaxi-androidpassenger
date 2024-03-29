/**
 * 
 */
package com.jozibear247_cab.models;

/**
 * @author Hardik A Bhalodi
 * 
 */
public class Bill {
	private String distance;
	private String time;
	private String basePrice, pricePerUnitDistance, pricePerUnitTime;
	private String distanceCost;
	private String timeCost;
	private String total;
	private String isPaid;
	private String unit;
	private String currency;

	private String primary_id;
	private String primary_amount;
	private String secoundry_id;
	private String secoundry_amount;
	private String payment_mode;
	private double discounted_amount;
	private double actual_total;

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(String basePrice) {
		this.basePrice = basePrice;
	}

	public String getPricePerUnitDistance() {
		return pricePerUnitDistance;
	}

	public void setPricePerUnitDistance(String pricePerUnitDistance) {
		this.pricePerUnitDistance = pricePerUnitDistance;
	}

	public String getPricePerUnitTime() {
		return pricePerUnitTime;
	}

	public void setPricePerUnitTime(String pricePerUnitTime) {
		this.pricePerUnitTime = pricePerUnitTime;
	}

	public String getDistanceCost() {
		return distanceCost;
	}

	public void setDistanceCost(String distanceCost) {
		this.distanceCost = distanceCost;
	}

	public String getTimeCost() {
		return timeCost;
	}

	public void setTimeCost(String timeCost) {
		this.timeCost = timeCost;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(String isPaid) {
		this.isPaid = isPaid;
	}

	public String getPrimary_id() {
		return primary_id;
	}

	public void setPrimary_id(String primary_id) {
		this.primary_id = primary_id;
	}

	public String getPrimary_amount() {
		return primary_amount;
	}

	public void setPrimary_amount(String primary_amount) {
		this.primary_amount = primary_amount;
	}

	public String getSecoundry_id() {
		return secoundry_id;
	}

	public void setSecoundry_id(String secoundry_id) {
		this.secoundry_id = secoundry_id;
	}

	public String getSecoundry_amount() {
		return secoundry_amount;
	}

	public void setSecoundry_amount(String secoundry_amount) {
		this.secoundry_amount = secoundry_amount;
	}

	public String getPayment_mode() {
		return payment_mode;
	}

	public void setPayment_mode(String payment_mode) {
		this.payment_mode = payment_mode;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public double getDiscounted_amount() {
		return discounted_amount;
	}

	public void setDiscounted_amount(double discounted_amount) {
		this.discounted_amount = discounted_amount;
	}

	public double getActual_total() {
		return actual_total;
	}

	public void setActual_total(double actual_total) {
		this.actual_total = actual_total;
	}

	

	

}
