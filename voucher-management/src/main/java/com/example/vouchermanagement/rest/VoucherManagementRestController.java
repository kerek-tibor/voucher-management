package com.example.vouchermanagement.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vouchermanagement.data.VoucherEntity;
import com.example.vouchermanagement.service.VoucherManagementService;
import com.example.vouchermanagement.service.exception.VoucherManagementServiceException;
import com.example.vouchermanagement.service.exception.VoucherManagementServiceValidationException;

@RestController
@RequestMapping("/vouchers")
public class VoucherManagementRestController {
	
	@Autowired
	private VoucherManagementService voucherManagmeentService;
	
	@PutMapping(value = "/redeem/{voucherCode}")
	public ResponseEntity<String> redeemVoucher(@PathVariable String voucherCode) {
		try {
			return ResponseEntity.ok(voucherManagmeentService.redeemVoucher(voucherCode));
		} catch (VoucherManagementServiceException e) {
			return ResponseEntity.internalServerError().header("Error", getRootCause(e).getMessage()).build();
		}
	}
	
	@GetMapping("/manage")
	public ResponseEntity<List<VoucherEntity>> all() {
		try {
			return ResponseEntity.ok(voucherManagmeentService.all());
		} catch (VoucherManagementServiceException e) {
			return ResponseEntity.internalServerError().header("Error", getRootCause(e).getMessage()).build();
		}
	}
	
	@GetMapping("/manage/{voucherCode}")
	public ResponseEntity<VoucherEntity> one(@PathVariable String voucherCode) {
		try {
			return ResponseEntity.ok(voucherManagmeentService.findByVoucherCode(voucherCode));
		} catch (VoucherManagementServiceException e) {
			return ResponseEntity.internalServerError().header("Error", getRootCause(e).getMessage()).build();
		}
	}
	
	@PostMapping("/manage")
	public ResponseEntity<VoucherEntity> newVoucherEntity(@RequestBody VoucherEntity voucherEntity) {
		try {
			return ResponseEntity.ok(voucherManagmeentService.save(voucherEntity));
		} catch(VoucherManagementServiceValidationException e) {
			return ResponseEntity.badRequest().header("Error", getRootCause(e).getMessage()).build();
		} catch (VoucherManagementServiceException e) {
			return ResponseEntity.internalServerError().header("Error", getRootCause(e).getMessage()).build();
		}
	}
	
	private static Throwable getRootCause(Throwable throwable) {
	    if (throwable.getCause() != null)
	        return getRootCause(throwable.getCause());

	    return throwable;
	}

}
