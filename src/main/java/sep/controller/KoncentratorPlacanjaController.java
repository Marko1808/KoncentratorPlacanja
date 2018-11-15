package sep.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import sep.dto.MerchantDTO;

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
		return new ResponseEntity<>(merchant, HttpStatus.OK);
	}
}
