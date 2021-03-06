package sep.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

public class PayPalClient {
	
	private String clientId;
	private String clientSecret;
	
	public PayPalClient(){
		clientId = "Afs9F-qEpwias0bd95BE70PVIziIi8fVbklBVLHj_Ro2dX6Q5r2iWDeC4d9QEIDoNDm0RCJMPUyJDxAI";
		clientSecret = "EIYy3hrNkmp-W6u8ueBjRp_U3a0T2SDOo7SyCBayYEHoZezgKUHZpExoi_ife1Cku8W10GMetLihcfWb";
	}
	public ResponseEntity<?> createPayment(String sum){
	    Map<String, Object> response = new HashMap<String, Object>();
	    Amount amount = new Amount();
	    amount.setCurrency("USD");
	    amount.setTotal(sum);
	    Transaction transaction = new Transaction();
	    transaction.setAmount(amount);
	    List<Transaction> transactions = new ArrayList<Transaction>();
	    transactions.add(transaction);

	    Payer payer = new Payer();
	    payer.setPaymentMethod("paypal");

	    Payment payment = new Payment();
	    payment.setIntent("sale");
	    payment.setPayer(payer);
	    payment.setTransactions(transactions);

	    RedirectUrls redirectUrls = new RedirectUrls();
	    redirectUrls.setCancelUrl("https://localhost:9081/greska.html");
	    redirectUrls.setReturnUrl("https://localhost:9081/uspesno.html");
	    payment.setRedirectUrls(redirectUrls);
	    Payment createdPayment;
	    try {
	        String redirectUrl = "";
	        APIContext context = new APIContext(clientId, clientSecret, "sandbox");
	        createdPayment = payment.create(context);
	        if(createdPayment!=null){
	            List<Links> links = createdPayment.getLinks();
	            for (Links link:links) {
	                if(link.getRel().equals("approval_url")){
	                    redirectUrl = link.getHref();
	                    break;
	                }
	            }
	            response.put("status", "success");
	            response.put("redirect_url", redirectUrl);
	            return new ResponseEntity<>(redirectUrl,HttpStatus.OK);
	        }
	    } catch (PayPalRESTException e) {
	        System.out.println("Error happened during payment creation!");
	    }
	    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	
	public ResponseEntity<?> completePayment(String req){
	    Map<String, Object> response = new HashMap();
	    Payment payment = new Payment();
	   // payment.setId(req.getParameter("paymentId"));
	    String paymentId = req.split(",")[0];
	    payment.setId(paymentId);
	    PaymentExecution paymentExecution = new PaymentExecution();
	    String payerId = req.split(",")[2];
	    paymentExecution.setPayerId(payerId);
	  //  paymentExecution.setPayerId(req.getParameter("PayerID"));
	    try {
	        APIContext context = new APIContext(clientId, clientSecret, "sandbox");
	        Payment createdPayment = payment.execute(context, paymentExecution);
	        if(createdPayment!=null){
	            response.put("status", "success");
	            response.put("payment", createdPayment);
	        }
	    } catch (PayPalRESTException e) {
	        System.err.println(e.getDetails());
	    }
	    return new ResponseEntity<>(response,HttpStatus.OK);
	}

}
