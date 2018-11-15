package sep.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.joda.time.DateTime;

public class MerchantDTO {
	
	private BigDecimal amount;
	
	private String merchant_id;
	
	private String merchant_password;
	
	private Integer merchant_order_id;
	
	private Timestamp merchant_timestamp;
	
	private String successUrl;
	
	private String errorUrl;
	
	private String failedUrl;
	
	
	public MerchantDTO() {
		
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getMerchant_id() {
		return merchant_id;
	}

	public void setMerchant_id(String merchant_id) {
		this.merchant_id = merchant_id;
	}

	public String getMerchant_password() {
		return merchant_password;
	}

	public void setMerchant_password(String merchant_password) {
		this.merchant_password = merchant_password;
	}

	public Integer getMerchant_order_id() {
		return merchant_order_id;
	}

	public void setMerchant_order_id(Integer merchant_order_id) {
		this.merchant_order_id = merchant_order_id;
	}

	public Timestamp getMerchant_timestamp() {
		return merchant_timestamp;
	}

	public void setMerchant_timestamp(Timestamp merchant_timestamp) {
		this.merchant_timestamp = merchant_timestamp;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}

	public String getErrorUrl() {
		return errorUrl;
	}

	public void setErrorUrl(String errorUrl) {
		this.errorUrl = errorUrl;
	}

	public String getFailedUrl() {
		return failedUrl;
	}

	public void setFailedUrl(String failedUrl) {
		this.failedUrl = failedUrl;
	}
	
	
}
