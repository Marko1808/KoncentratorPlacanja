package sep.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import sep.dto.BitcoinDTO;
import sep.dto.MerchantDTO;
import sep.dto.PaymentUrlIdDTO;
import sep.dto.ResponseBitcoinDTO;
import sep.model.PayPalClient;

@Controller
@RequestMapping(value = "/zahtev")
public class KoncentratorPlacanjaController {
	
	PayPalClient payPalClient = new PayPalClient();
	
	@CrossOrigin
	@RequestMapping(
			value = "/posaljiZahtev",
			method = RequestMethod.POST
	)
	public ResponseEntity<?> posaljiZahtev(@RequestBody MerchantDTO merchant) {
		System.out.println("Stao sam tu");
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

            System.out.println("Prosledjujem zahtev banci prodavca");
             //step 2
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<MerchantDTO> entity = new HttpEntity<>(merchant, headers);
            paymentUrlIdDTO = client.postForObject("http://localhost:1235/bank/proveriZahtev", entity,
                    PaymentUrlIdDTO.class);
            System.out.println("Vratila mi banka url: " + paymentUrlIdDTO.getUrl());
            return new ResponseEntity<>(paymentUrlIdDTO, HttpStatus.OK);

        } catch (Exception e) {
            System.out.println("Ne moze da posalje");
            return null; 
        }
		
	}
	
	@CrossOrigin
	@RequestMapping(
			value = "/payPal",
			method = RequestMethod.POST
	)
	public Map<String, Object> payPal(@RequestBody MerchantDTO merchant) {
		System.out.println("Dosao u PayPal");
		return payPalClient.createPayment(merchant.getAmount().toString());
		
	}
	
	@CrossOrigin
	@RequestMapping(
			value = "/zavrsiPlacanje",
			method = RequestMethod.POST
	)
	public Map<String, Object> completePayment(@RequestBody String request){
	    return payPalClient.completePayment(request);
	}
	
	
	@CrossOrigin
	@RequestMapping(
			value = "/bitcoin",
			method = RequestMethod.POST
	)
	public void bitcoin(@RequestBody BitcoinDTO b, HttpServletResponse r) {
		System.out.println("Dosao u Bitcoin...");
		
		Map<String, Object> mapa = new HashMap<String,Object>();
        mapa.put("order_id",UUID.randomUUID().toString());
        mapa.put("price_amount",b.getAmount());
        mapa.put("price_currency","USD");
        mapa.put("receive_currency","USD");
        mapa.put("title",b.getNaziv());
        mapa.put("description","desc");
        mapa.put("callback_url","https://api-sandbox.coingate.com/account/orders"); //TODO:promeniti
        mapa.put("success_url", "http://localhost:1236/responseSuccessBitcoin.html");
        
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        
        headers.add("Authorization", "Token 8W2cFE2hUx55MHxxuisH9gigTzdP7pRjYmQsHH2V");
        HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String,Object>>(mapa, headers);
        
        ResponseBitcoinDTO response = client.postForObject("https://api-sandbox.coingate.com/v2/orders", entity, ResponseBitcoinDTO.class);
              
        HttpHeaders noviHeaders = new HttpHeaders();
        noviHeaders.add("Authorization", "Token 8W2cFE2hUx55MHxxuisH9gigTzdP7pRjYmQsHH2V");
        noviHeaders.add("Location", response.getPayment_url());
        
        HttpEntity<ResponseBitcoinDTO> entity1 = new HttpEntity<ResponseBitcoinDTO>(response, noviHeaders);
        //String odg = client.postForObject(entity1.getHeaders().getLocation(), entity1, String.class); //ne moze da pogodi nas localhost...
        
        System.out.println("\n\n\t\tredirekcija: \n\n\n" + response.getPayment_url());
		r.setStatus(302);
		r.setHeader("Location", response.getPayment_url());
        r.setHeader("Access-Control-Allow-Origin", "*");
	}
	
}
