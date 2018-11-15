package sep.dto;

import java.math.BigDecimal;

public class MerchantDTO {
	
	private BigDecimal amount;
	
	private String merchant_id;
	
	private String merchant_password;
	
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
	
	
}
