package sep.controller;

import java.sql.Timestamp;
import java.util.Random;

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

import sep.dto.MerchantDTO;
import sep.dto.PaymentUrlIdDTO;

@RestController
@RequestMapping(value = "/zahtev")
public class KoncentratorPlacanjaController {
	
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
}
