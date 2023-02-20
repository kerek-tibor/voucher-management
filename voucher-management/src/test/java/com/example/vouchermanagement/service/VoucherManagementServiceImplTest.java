package com.example.vouchermanagement.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;

import com.example.vouchermanagement.data.VoucherEntity;
import com.example.vouchermanagement.data.VoucherRepository;
import com.example.vouchermanagement.service.exception.VoucherManagementServiceException;
import com.example.vouchermanagement.service.exception.VoucherManagementServiceValidationException;

@ExtendWith(MockitoExtension.class)
class VoucherManagementServiceImplTest {
	
	@Mock
	private VoucherRepository voucherRepository;
	
	@InjectMocks
	private VoucherManagementServiceImpl voucherManagementService;
	
	@Test
	void test_findByVoucherCode_found() throws VoucherManagementServiceException {
		VoucherEntity entity = new VoucherEntity();
		entity.setVoucherCode("junitTestVoucher");
		
		Mockito.when(voucherRepository.findById(entity.getVoucherCode())).thenReturn(Optional.of(entity));
		
		VoucherEntity foundEntity = voucherManagementService.findByVoucherCode("junitTestVoucher");
		
		Assert.notNull(foundEntity, "Entity is null");
		Assert.isTrue("junitTestVoucher".equals(foundEntity.getVoucherCode()), "voucherCode mismatch");
	}
	
	@Test
	void test_findByVoucherCode_notFound() throws VoucherManagementServiceException {
		Mockito.when(voucherRepository.findById("junitTestVoucherEmpty")).thenReturn(Optional.empty());
		
		VoucherEntity entity = voucherManagementService.findByVoucherCode("junitTestVoucherEmpty");
		
		Assert.isNull(entity, "Entity should be null");
	}
	
	@Test
	void test_findByVoucherCode_error() {
		Mockito.when(voucherRepository.findById(Mockito.anyString())).thenThrow(new ServiceException("test exception"));
		
		VoucherManagementServiceException exception = assertThrows(VoucherManagementServiceException.class, ()->{voucherManagementService.findByVoucherCode("junitTestVoucherError");});
		
		Assert.notNull(exception, "VoucherManagementServiceException was not thrown");
		Assert.isTrue("test exception".equals(getRootCause(exception).getMessage()), "Exception cause mismatch");
	}
	
	@Test
	void test_all_found() throws VoucherManagementServiceException {
		VoucherEntity entity1 = new VoucherEntity();
		entity1.setVoucherCode("junitTestVoucher1");
		VoucherEntity entity2 = new VoucherEntity();
		entity2.setVoucherCode("junitTestVoucher2");
		Iterable<VoucherEntity> iterable = Arrays.asList(entity1, entity2);
		Mockito.when(voucherRepository.findAll()).thenReturn(iterable);
		
		List<VoucherEntity> foundList = voucherManagementService.all();
		
		Assert.notEmpty(foundList, "Returned list is null or empty");
		Assert.isTrue("junitTestVoucher1".equals(foundList.get(0).getVoucherCode()), "voucherCode mismatch");
		Assert.isTrue("junitTestVoucher2".equals(foundList.get(1).getVoucherCode()), "voucherCode mismatch");
	}
	
	@Test
	void test_all_notFound() throws VoucherManagementServiceException {
		Mockito.when(voucherRepository.findAll()).thenReturn(Collections.emptyList());
		
		List<VoucherEntity> foundList = voucherManagementService.all();
		
		Assert.isTrue(foundList.isEmpty(), "Found list should not contain any elements");
	}
	
	@Test
	void test_all_error() {
		Mockito.when(voucherRepository.findAll()).thenThrow(new ServiceException("test exception"));
		
		VoucherManagementServiceException exception = assertThrows(VoucherManagementServiceException.class, ()->{voucherManagementService.all();});
		
		Assert.notNull(exception, "VoucherManagementServiceException was not thrown");
		Assert.isTrue("test exception".equals(getRootCause(exception).getMessage()), "Exception cause mismatch");
	}
	
	@Test
	void test_save_validation() {
		VoucherEntity input1 = new VoucherEntity();
		input1.setRedemptionLimit(Long.valueOf(0));
		
		VoucherManagementServiceException exception = assertThrows(VoucherManagementServiceValidationException.class, ()->{voucherManagementService.save(input1);});
		
		Assert.notNull(exception, "VoucherManagementServiceValidationException was not thrown");
		Assert.isTrue("redemptionLimit must be greater than 0 or must be null".equals(getRootCause(exception).getMessage()), "Exception cause mismatch");
		
		VoucherEntity input2 = new VoucherEntity();
		input2.setRedemptionLimit(Long.valueOf(1));
		input2.setRedeemUntil(LocalDateTime.MIN);
		
		exception = assertThrows(VoucherManagementServiceValidationException.class, ()->{voucherManagementService.save(input2);});
		
		Assert.notNull(exception, "VoucherManagementServiceValidationException was not thrown");
		Assert.isTrue("redeemUntil must be in the future".equals(getRootCause(exception).getMessage()), "Exception cause mismatch");
	}
	
	@Test
	void test_save() throws VoucherManagementServiceException {
		VoucherEntity input = new VoucherEntity();
		input.setRedeemUntil(LocalDateTime.MAX);
		
		voucherManagementService.save(input);
		
		Mockito.verify(voucherRepository, Mockito.times(1)).save(input);
	}
	
	@Test
	void test_save_error() {
		Mockito.when(voucherRepository.save(Mockito.any())).thenThrow(new ServiceException("test exception"));
		
		VoucherEntity input = new VoucherEntity();
		VoucherManagementServiceException exception = assertThrows(VoucherManagementServiceException.class, ()->{voucherManagementService.save(input);});
		
		Assert.notNull(exception, "VoucherManagementServiceException was not thrown");
		Assert.isTrue("test exception".equals(getRootCause(exception).getMessage()), "Exception cause mismatch");
	}
	
	@Test
	void test_redeemVoucher_checkRedeemUntil_fail() throws VoucherManagementServiceException {
		VoucherEntity entity = new VoucherEntity();
		entity.setRedeemUntil(LocalDateTime.MIN);
		Mockito.when(voucherRepository.findById(Mockito.anyString())).thenReturn(Optional.of(entity));
		
		String result = voucherManagementService.redeemVoucher("something");
		
		Assert.isTrue("Voucher not redeemable".equals(result), "Voucher should not be redeemable");
	}
	
	@Test
	void test_redeemVoucher_checkRedemptionCount_fail() throws VoucherManagementServiceException {
		VoucherEntity entity = new VoucherEntity();
		entity.setRedeemUntil(LocalDateTime.MAX);
		entity.setRedemptionLimit(Long.valueOf(1));
		entity.setRedemptionCount(Long.valueOf(1));
		Mockito.when(voucherRepository.findById(Mockito.anyString())).thenReturn(Optional.of(entity));
		
		String result = voucherManagementService.redeemVoucher("something");
		
		Assert.isTrue("Voucher not redeemable".equals(result), "Voucher should not be redeemable");
	}
	
	@Test
	void test_redeemVoucher_invalid_voucherCode() throws VoucherManagementServiceException {
		Mockito.when(voucherRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
		
		String result = voucherManagementService.redeemVoucher("something");
		
		Assert.isTrue("Invalid voucher code".equals(result), "Wrong result message");
	}
	
	@Test
	void test_redeemVoucher_null_redemptionCount() throws VoucherManagementServiceException {
		VoucherEntity entity = new VoucherEntity();
		entity.setRedemptionLimit(Long.valueOf(1));
		Mockito.when(voucherRepository.findById(Mockito.anyString())).thenReturn(Optional.of(entity));
		
		String result = voucherManagementService.redeemVoucher("something");
		
		Assert.isTrue("Voucher redeemed".equals(result), "Null value for returned voucher entity should still work");
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void test_redeemVoucher() throws VoucherManagementServiceException {
		VoucherEntity entity1 = new VoucherEntity();
		entity1.setRedemptionLimit(Long.valueOf(1));
		entity1.setRedemptionCount(Long.valueOf(0));
		VoucherEntity entity2 = new VoucherEntity();
		entity2.setRedemptionCount(Long.valueOf(13));
		Mockito.when(voucherRepository.findById(Mockito.anyString())).thenReturn(Optional.of(entity1), Optional.of(entity2));
		
		String result = voucherManagementService.redeemVoucher("something");
		
		Assert.isTrue("Voucher redeemed".equals(result), "Voucher should be redeemable");
		
		result = voucherManagementService.redeemVoucher("something");
		
		Assert.isTrue("Voucher redeemed".equals(result), "Voucher should be redeemable");
	}
	
	@Test
	void test_redeemVoucher_error() {
		Mockito.when(voucherRepository.findById(Mockito.anyString())).thenThrow(new ServiceException("test exception"));
		
		VoucherManagementServiceException exception = assertThrows(VoucherManagementServiceException.class, ()->{voucherManagementService.redeemVoucher("something");});
		
		Assert.notNull(exception, "VoucherManagementServiceException was not thrown");
		Assert.isTrue("test exception".equals(getRootCause(exception).getMessage()), "Exception cause mismatch");
	}
	
	private static Throwable getRootCause(Throwable throwable) {
	    if (throwable.getCause() != null)
	        return getRootCause(throwable.getCause());

	    return throwable;
	}
}
