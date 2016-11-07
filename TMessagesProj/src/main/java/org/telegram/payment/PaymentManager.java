package org.telegram.payment;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.telegram.messenger.R;
import org.telegram.payment.billingModel.PaymentResponse;
import org.telegram.socialuser.BackgroundExecuter;

import java.math.BigDecimal;

/**
 * Created by craterzone3 on 13/6/16.
 */

public class PaymentManager extends Activity implements PaymentConfirmationListener{

    public static final String TAG = PaymentManager.class.getName().toString();

    /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     *
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     *    without communicating to PayPal's servers.
     */

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION ;


    // note that these credentials will differ between live & sandbox environments.

    //   /*"AZcFLUiVZaHz3darBCvBVrVT3o9yoCffywiqrBflBRy7V_1JSvHSJ9kV2Fo_bPNkkTXWynmbByW3JX5K";*//*//"AZcFLUiVZaHz3darBCvBVrVT3o9yoCffywiqrBflBRy7V_1JSvHSJ9kV2Fo_bPNkkTXWynmbByW3JX5K";//"ARafipSb7SknBnBKsQY1Na9CvKZPypeHORyOQ-f-1sEAfwt0NEymuOniy4__9oIJ1JYEleESrqSkESGB";*/"Acv-mIu5gkIOQStmK8CAco1p-h1geAElqFCOUEl0EQcdhSKsCmam5Z_IoZ0QiF0F4lu9YBZ2mT3KqQQJ";


    /**
     * live client id of IM for Telegram
     */


    private static final String CONFIG_CLIENT_ID ="ARafipSb7SknBnBKsQY1Na9CvKZPypeHORyOQ-f-1sEAfwt0NEymuOniy4__9oIJ1JYEleESrqSkESGB";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;
    private TextView buyContent;
    private TextView success;
    private Button payButton;
    private Button cancelButton;


    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("CRATERZONE PVT LTD").acceptCreditCards(true);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        acttionBarSetup();
        findViewById(R.id.backview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void acttionBarSetup(){
        // getActionBar().setTitle(" Premium");
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.custom_actionbar);

    }


    public static void createIntent(Activity activity) {
        ActivityCompat.startActivity(activity, new Intent(activity, PaymentManager.class),
                ActivityOptionsCompat.makeCustomAnimation(activity, android.R.anim.slide_in_left, android.R.anim.slide_out_right).toBundle());
    }


    public void onBuyPressed(View pressed) {
        /*
         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
         * Change PAYMENT_INTENT_SALE to
         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
         *     later via calls from your server.
         *
         * Also, to include additional payment details and an item list, see getStuffToBuy() below.
         */

        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

        /*
         * See getStuffToBuy(..) for examples of some available payment options.
         */
        Intent intent = new Intent(PaymentManager.this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    /**
     * @param paymentIntent
     * @return
     */


    private PayPalPayment getThingToBuy(String paymentIntent) {
        return new PayPalPayment(new BigDecimal("2.00"), "USD", "PREMIUM FEATURES",
                paymentIntent);
    }





    public void onHomePressed(View view){
        finish();
    }



    /*
     * This method shows use of optional payment details and item list.
     */
    /*private PayPalPayment getStuffToBuy(String paymentIntent) {
        //--- include an item list, payment amount details
        PayPalItem[] items =
                {
                        new PayPalItem("sample item #1", 2, new BigDecimal("87.50"), "USD",
                                "s
                                ku-12345678"),
                        new PayPalItem("free sample item #2", 1, new BigDecimal("0.00"),
                                "USD", "sku-zero-price"),
                        new PayPalItem("sample item #3 with a longer name", 6, new BigDecimal("37.99"),
                                "USD", "sku-33333")
                };
        BigDecimal subtotal = PayPalItem.getItemTotal(items);
        BigDecimasrivastavaaal shipping = new BigDecimal("7.21");
        BigDecimal tax = new BigDecimal("4.67");
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
        BigDecimal amount = subtotal.add(shipping).add(tax);
        PayPalPayment payment = new PayPalPayment(amount, "USD", "sample item", paymentIntent);
        payment.items(items).paymentDetails(paymentDetails);

        //--- set other optional fields like invoice_number, custom field, and soft_descriptor
        payment.custom("This is text that will be associated with the payment that the app can use.");

        return payment;
    }
*/
    /*protected void displayResultText(String result) {
     *//*   ((TextView)findViewById(R.id.txtResult)).setText("Result : " + result);
        Toast.makeText(
                getApplicationContext(),
                result, Toast.LENGTH_LONG)
                .show();*//*
    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Gson gson =  new Gson(); // new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                        PaymentResponse response =  gson.fromJson(confirm.toJSONObject().toString(), PaymentResponse.class);

                        UserPaymentInfo.getInstatance().setPaymentId(response.getResponse().getId());
                        UserPaymentInfo.getInstatance().setPaymentStatus(UserPaymentInfo.paidUser);
                        findViewById(R.id.buyItBtn).setVisibility(View.GONE);
                        findViewById(R.id.cancelBtn).setVisibility(View.VISIBLE);
                        sendAuthorizationToServer();
                        findViewById(R.id.content).setVisibility(View.GONE);
                        findViewById(R.id.done_content).setVisibility(View.VISIBLE);

                        //  cancelButton.setVisibility(View.VISIBLE);

                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));

                        //confirm.getProofOfPayment()

                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        //  displayResultText("PaymentConfirmation info received from PayPal");

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        //  sendAuthorizationToServer(auth);
                        //  displayResultText("Future Payment code received from PayPal");

                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("ProfileSharingExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("ProfileSharingExample", authorization_code);

                        // sendAuthorizationToServer(auth);
                        //  displayResultText("Profile Sharing code received from PayPal");

                    } catch (JSONException e) {
                        Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("ProfileSharingExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
        //   String s = UserPaymentInfo.getInstatance().getPaymentId();
    }




    private void sendAuthorizationToServer(/*PayPalAuthorization authorization*/) {
        /**
         * TODO: Send the authorization response to your server, where it can
         * exchange the authorization code for OAuth access and refresh tokens.
         *
         * Your server must then store these tokens, so that your server code
         * can execute payments for this user in the future.
         *
         * A more complete example that includes the required app-server to
         * PayPal-server integration is available from
         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
         */



        String id=UserPaymentInfo.getInstatance().getUserId();
        if(! id.equalsIgnoreCase("")) {
            BackgroundExecuter.getInstance().execute(new
                    ConfirmationRequester(UserPaymentInfo.getInstatance().getPaymentId(), id));
        }

    }

    /*
    public void onFuturePaymentPurchasePressed(View pressed) {
        // Get the Client Metadata ID from the SDK
        String metadataId = PayPalConfiguration.getClientMetadataId(this);

        Log.i("FuturePaymentExample", "Client Metadata ID: " + metadataId);

        // TODO: Send metadataId and transaction details to your server for processing with
        // PayPal...
        displayResultText("Client Metadata Id received from SDK");
    }*/


    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    @Override
    public void onPaymentConfirmationSuccess() {
        UserPaymentInfo.getInstatance().setPaymentStatus(UserPaymentInfo.paidUser);
    }

    @Override
    public void onPaymentConfirmationFailed() {
        UserPaymentInfo.getInstatance().setPaymentStatus(UserPaymentInfo.unPaidUser);
    }


}
