package sep.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import sep.dto.BitcoinDTO;
import sep.dto.MerchantDTO;
import sep.dto.PaymentUrlIdDTO;
import sep.dto.ResponseBitcoinDTO;
import sep.model.PayPalClient;

@RestController
@RequestMapping(value = "/zahtev")
public class KoncentratorPlacanjaController {
	
	PayPalClient payPalClient = new PayPalClient();
	
	private static final Logger logger = LoggerFactory.getLogger(KoncentratorPlacanjaController.class);
	
	@CrossOrigin
	@RequestMapping(
			value = "/posaljiZahtev",
			method = RequestMethod.POST
	)
	public ResponseEntity<?> posaljiZahtev(@RequestBody MerchantDTO merchant) {
		System.out.println("\n\t\tUšao u pošalji zahtev (koncentrator plaćanja).\n");
		//ovde treba koncentrator placanja da izgenerise merchantorderId, i to sve
		Random randomGenerator = new Random();
		merchant.setMerchant_order_id(randomGenerator.nextInt(1000));
		//zakomentarisano jer puca na datetime kod onog slanja
		/*Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		merchant.setMerchant_timestamp(timestamp);
		*/
		
		RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        PaymentUrlIdDTO paymentUrlIdDTO = new PaymentUrlIdDTO();

        try {

            System.out.println("\n\t\tProsleđujem zahtev banci prodavca.\n");
             //step 2
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<MerchantDTO> entity = new HttpEntity<>(merchant, headers);
            paymentUrlIdDTO = client.postForObject("https://localhost:1235/bank/proveriZahtev", entity,
                    PaymentUrlIdDTO.class);
            System.out.println("\n\t\tVratila mi banka url: " + paymentUrlIdDTO.getUrl() + "\n");
            
            logger.info("\n\t\tUspešno slanje zahteva.\n");
            return new ResponseEntity<>(paymentUrlIdDTO, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("\n\t\tNe može da pošalje zahtev (koncentrator plaćanja).\n");
            
            logger.info("\n\t\tNeuspešno slanje zahteva.\n");
            return null; 
        }
	}
	
	@CrossOrigin
	@RequestMapping(
			value = "/payPal",
			method = RequestMethod.POST
	)
	public ResponseEntity<?> payPal(@RequestBody MerchantDTO merchant) {
		System.out.println("\n\t\tDošao u PayPal.\n");
		logger.info("\n\t\tZapočeto plaćanje preko PayPal-a.\n");
		return payPalClient.createPayment(merchant.getAmount().toString());
	}
	
	@CrossOrigin
	@RequestMapping(
			value = "/zavrsiPlacanje",
			method = RequestMethod.POST
	)
	public ResponseEntity<?> completePayment(@RequestBody String request){
		logger.info("\n\t\tZavršeno plaćanje preko PayPal-a.\n");
		return payPalClient.completePayment(request);
	}
	
	
	@CrossOrigin
	@RequestMapping(
			value = "/bitcoin",
			method = RequestMethod.POST
	)
	public ResponseEntity<?> bitcoin(@RequestBody BitcoinDTO b) {
		System.out.println("\n\t\tDošao u Bitcoin.\n");
		
		Map<String, Object> mapa = new HashMap<String,Object>();
        mapa.put("order_id", UUID.randomUUID().toString());
        mapa.put("price_amount", b.getAmount());
        mapa.put("price_currency","USD");
        mapa.put("receive_currency","USD");
        mapa.put("title", b.getNaziv());
        mapa.put("description", "desc");
        mapa.put("callback_url", "https://api-sandbox.coingate.com/account/orders"); //TODO:promeniti
        mapa.put("success_url", "https://localhost:9081/uspesno.html");
        
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        
        headers.add("Authorization", "Token 8W2cFE2hUx55MHxxuisH9gigTzdP7pRjYmQsHH2V");
        HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String,Object>>(mapa, headers);
        
        ResponseBitcoinDTO response = client.postForObject("https://api-sandbox.coingate.com/v2/orders", entity, ResponseBitcoinDTO.class);
              
        HttpHeaders noviHeaders = new HttpHeaders();
        noviHeaders.add("Authorization", "Token 8W2cFE2hUx55MHxxuisH9gigTzdP7pRjYmQsHH2V");
        noviHeaders.add("Location", response.getPayment_url()); //payment url
        noviHeaders.add("id", response.getId().toString()); //id inicirane transakcije
        noviHeaders.add("uuid", response.getPayment_url().split("invoice/")[1]);
        noviHeaders.add("title", b.getNaziv()); //naziv casopisa koji se placa
        noviHeaders.add("created_at", response.getCreated_at());
        noviHeaders.add("status", response.getStatus()); //ovde ce uvek biti new
        noviHeaders.add("merchant_order_id", response.getOrder_id()); //uplatilac        
        System.out.println("\t\tnoviHeaders: " + noviHeaders.toString() + "\n\n");
        
        //parametri potrebni za kreiranje bitcoin transakcije
        String paymentUrl = response.getPayment_url();
        String idIniciraneTransakcije = response.getId().toString();
        String uuid = response.getPayment_url().split("invoice/")[1];
        String naziv = b.getNaziv();
        String vremeKreiranja = response.getCreated_at();
        String status = response.getStatus();
        String uplatilac = response.getOrder_id();
        String retVal = paymentUrl + ", " + idIniciraneTransakcije + ", " + uuid + ", " + naziv + ", " + vremeKreiranja + ", " + status + ", " + uplatilac;
        //System.out.println("\t\t[bitcoin transakcija] retVal:\n\t\t" + retVal + "\n\n");
        
        logger.info("\n\t\tUspešno završeno plaćanje preko bitcoin-a.\n");
        return new ResponseEntity<>(retVal, noviHeaders, HttpStatus.OK);
	}
	
}
