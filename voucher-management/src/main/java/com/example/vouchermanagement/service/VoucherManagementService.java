package com.example.vouchermanagement.service;

import java.util.List;

import com.example.vouchermanagement.data.VoucherEntity;
import com.example.vouchermanagement.service.exception.VoucherManagementServiceException;
import com.example.vouchermanagement.service.exception.VoucherManagementServiceValidationException;

public interface VoucherManagementService {

	String redeemVoucher(String voucherCode) throws VoucherManagementServiceException;
	
	List<VoucherEntity> all() throws VoucherManagementServiceException;
	
	VoucherEntity findByVoucherCode(String voucherCode) throws VoucherManagementServiceException;
	
	VoucherEntity save(VoucherEntity voucherEntity) throws VoucherManagementServiceException, VoucherManagementServiceValidationException;
}
