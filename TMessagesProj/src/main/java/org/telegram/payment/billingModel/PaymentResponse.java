package org.telegram.payment.billingModel;

/**
 * Created by craterzone3 on 14/6/16.
 */

public class PaymentResponse {

    private Response response;

    private ClientResponse client ;




    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public ClientResponse getClient() {
        return client;
    }

    public void setClient(ClientResponse client) {
        this.client = client;
    }


}
