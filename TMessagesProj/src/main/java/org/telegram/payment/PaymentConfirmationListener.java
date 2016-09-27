package org.telegram.payment;

/**
 * Created by craterzone3 on 17/6/16.
 */
public interface PaymentConfirmationListener {

    public void onPaymentConfirmationSuccess();
    public void onPaymentConfirmationFailed();


}


