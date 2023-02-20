package com.example.vouchermanagement.data;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class VoucherEntity {

	@Id
	private String voucherCode;
	
	private Long redemptionLimit;
	
	private Long redemptionCount;
	
	private LocalDateTime redeemUntil;
	
	private String description;
	
	public String getVoucherCode() {
		return voucherCode;
	}
	
	public void setVoucherCode(String voucherCode) {
		this.voucherCode = voucherCode;
	}
	
	public Long getRedemptionLimit() {
		return redemptionLimit;
	}
	
	public void setRedemptionLimit(Long redemptionLimit) {
		this.redemptionLimit = redemptionLimit;
	}
	
	public Long getRedemptionCount() {
		return redemptionCount;
	}

	public void setRedemptionCount(Long redemptionCount) {
		this.redemptionCount = redemptionCount;
	}

	public LocalDateTime getRedeemUntil() {
		return redeemUntil;
	}

	public void setRedeemUntil(LocalDateTime redeemUntil) {
		this.redeemUntil = redeemUntil;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "VoucherEntity [voucherCode=" + voucherCode + ", redemptionLimit=" + redemptionLimit
				+ ", redemptionCount=" + redemptionCount + ", redeemUntil=" + redeemUntil + ", description="
				+ description + "]";
	}
}
