/**
 * 
 */
package com.jozibear247_cab.models;

/**
 * @author Hardik A Bhalodi
 * 
 */
public class VehicalType {
	private int id;
	private String name;
	private String icon;
	private int isDefault;
	private String pricePerUnitTime;
	private String pricePerUnitDistance;
	private String basePrice;
	private String currency;
	private String unit;
	public boolean isSelected;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}

	public String getPricePerUnitTime() {
		return pricePerUnitTime;
	}

	public void setPricePerUnitTime(String pricePerUnitTime) {
		this.pricePerUnitTime = pricePerUnitTime;
	}

	public String getPricePerUnitDistance() {
		return pricePerUnitDistance;
	}

	public void setPricePerUnitDistance(String pricePerUnitDistance) {
		this.pricePerUnitDistance = pricePerUnitDistance;
	}

	public String getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(String basePrice) {
		this.basePrice = basePrice;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}
