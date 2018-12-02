package sep.dto;


public class BitcoinDTO {

	private String amount;
	private String naziv;
	
	public BitcoinDTO() {
		super();
	}

	public BitcoinDTO(String amount, String naziv) {
		super();
		this.amount = amount;
		this.naziv = naziv;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}
	
}
