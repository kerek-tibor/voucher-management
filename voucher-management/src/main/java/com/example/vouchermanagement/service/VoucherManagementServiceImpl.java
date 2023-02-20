package com.example.vouchermanagement.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.example.vouchermanagement.data.VoucherEntity;
import com.example.vouchermanagement.data.VoucherRepository;
import com.example.vouchermanagement.service.exception.VoucherManagementServiceException;
import com.example.vouchermanagement.service.exception.VoucherManagementServiceValidationException;

@Service
public class VoucherManagementServiceImpl implements VoucherManagementService {
	
	private final Logger logger = LoggerFactory.getLogger(VoucherManagementServiceImpl.class);
	
	@Autowired
	private VoucherRepository voucherRepository;

	@Override
	public String redeemVoucher(String voucherCode) throws VoucherManagementServiceException {
		try {
			Optional<VoucherEntity> voucherEntity = voucherRepository.findById(voucherCode);
			if(voucherEntity.isPresent()) {
				VoucherEntity entity = voucherEntity.get();
				if(checkRedeemUntil(entity.getRedeemUntil()) && checkRedemptionCount(entity.getRedemptionLimit(), entity.getRedemptionCount()))
				{
					if (entity.getRedemptionCount() != null) {
						long redemptionCount = entity.getRedemptionCount().longValue();
						redemptionCount += 1;
						entity.setRedemptionCount(Long.valueOf(redemptionCount));
						voucherRepository.save(entity);
					} else {
						entity.setRedemptionCount(Long.valueOf(1));
						voucherRepository.save(entity);
					}
					return "Voucher redeemed";
				} else {
					return "Voucher not redeemable";
				}
			} else {
				return "Invalid voucher code";
			}
		} catch (Exception e) {
			logger.error("Unexpected error - voucherCode: " + voucherCode, e);
			throw new VoucherManagementServiceException(e);
		}
	}

	@Override
	public List<VoucherEntity> all() throws VoucherManagementServiceException {
		try {
			List<VoucherEntity> resultList = new ArrayList<>();
			for(VoucherEntity entity : voucherRepository.findAll()) {
				resultList.add(entity);
			}
			return resultList;
		} catch (Exception e) {
			logger.error("Unexpected error", e);
			throw new VoucherManagementServiceException(e);
		}
	}

	@Override
	public VoucherEntity findByVoucherCode(String voucherCode) throws VoucherManagementServiceException {
		try {
			return voucherRepository.findById(voucherCode).orElse(null);
		} catch (Exception e) {
			logger.error("Unexpected error - voucherCode: " + voucherCode, e);
			throw new VoucherManagementServiceException(e);
		}
	}

	@Override
	public VoucherEntity save(VoucherEntity voucherEntity) throws VoucherManagementServiceException {
		try {
			validateEntity(voucherEntity);
			return voucherRepository.save(voucherEntity);
		} catch (IllegalArgumentException e) {
			logger.error("Validation error -  " + voucherEntity.toString(), e);
			throw new VoucherManagementServiceValidationException(e);
		} catch (Exception e) {
			logger.error("Validation error -  " + voucherEntity.toString(), e);
			throw new VoucherManagementServiceException(e);
		}
	}
	
	private boolean checkRedeemUntil(LocalDateTime redeemUntil) {
		return redeemUntil == null || LocalDateTime.now().isBefore(redeemUntil); 
	}
	
	private boolean checkRedemptionCount(Long redemptionLimit, Long redemptionCount) {
		return redemptionLimit == null || redemptionCount == null || redemptionCount.longValue() < redemptionLimit.longValue();
	}
	
	private void validateEntity(VoucherEntity voucherEntity) {
		if(voucherEntity.getRedemptionLimit() != null) {
			Assert.isTrue(voucherEntity.getRedemptionLimit().longValue() > 0, "redemptionLimit must be greater than 0 or must be null");
		}
		if(voucherEntity.getRedeemUntil() != null) {
			Assert.isTrue(LocalDateTime.now().isBefore(voucherEntity.getRedeemUntil()), "redeemUntil must be in the future");
		}
	}

}
