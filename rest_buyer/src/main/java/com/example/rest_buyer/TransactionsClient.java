package com.example.rest_buyer;

import com.example.consumingwebservice.wsdl.CreditCardDetails;
import com.example.consumingwebservice.wsdl.TransactionRequest;
import com.example.consumingwebservice.wsdl.TransactionResponse;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

public class TransactionsClient extends WebServiceGatewaySupport {

    private TransactionResponse processTransaction(CreditCardDetails cardDetails){
        TransactionRequest request = new TransactionRequest();
        request.setCreditCardDetails(cardDetails);
        TransactionResponse response = (TransactionResponse) getWebServiceTemplate().marshalSendAndReceive(
                "http://34.106.150.206:8083/ws/transactions",
                request,
                new SoapActionCallback("http://spring.io/guides/transactions/TransactionRequest")
        );
        return response;
    }

    public Jaxb2Marshaller getJaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.example.consumingwebservice.wsdl");
        return marshaller;
    }

    public TransactionResponse sendTransactionRequest(CreditCardDetails creditCardDetails){
        TransactionsClient client = new TransactionsClient();
        client.setDefaultUri("https://localhost:8083/ws");
        client.setMarshaller(client.getJaxb2Marshaller());
        client.setUnmarshaller(client.getJaxb2Marshaller());
        return client.processTransaction(creditCardDetails);
    }

    public static void main(String[] args){
        TransactionsClient client = new TransactionsClient();
        client.setDefaultUri("https://localhost:8083/ws");
        client.setMarshaller(client.getJaxb2Marshaller());
        client.setUnmarshaller(client.getJaxb2Marshaller());

        CreditCardDetails details = new CreditCardDetails();
        details.setName("Credit Card");
        details.setNumber("9999");
        details.setExpirationDate("1000");
        TransactionResponse response = client.processTransaction(details);
        System.out.println(response.getTransactionStatus().value());
    }


}
