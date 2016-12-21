package org.telegram.payment;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


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
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.R;
import org.telegram.payment.billingModel.PaymentResponse;
import org.telegram.socialuser.BackgroundExecuter;
import org.telegram.socialuser.model.CreditModel;
import org.telegram.socialuser.runable.AddKarmaRequester;
import org.telegram.socialuser.runable.DeductKarmaRequester;
import org.telegram.ui.SocialFriendActivity;
import org.telegram.ui.listners.AdsRemovalListener;
import org.telegram.ui.listners.OnKarmaAddedListener;

import java.math.BigDecimal;

/**
 * Created by craterzone3 on 13/6/16.
 */

public class PaymentManager extends Activity implements PaymentConfirmationListener ,AdsRemovalListener,View.OnClickListener,OnKarmaAddedListener {

    public static final String TAG = PaymentManager.class.getName().toString();

    /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     * <p>
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     * <p>
     * <p>
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;


    // note that these credentials will differ between live & sandbox environments.

    //   /*"AZcFLUiVZaHz3darBCvBVrVT3o9yoCffywiqrBflBRy7V_1JSvHSJ9kV2Fo_bPNkkTXWynmbByW3JX5K";*//*//"AZcFLUiVZaHz3darBCvBVrVT3o9yoCffywiqrBflBRy7V_1JSvHSJ9kV2Fo_bPNkkTXWynmbByW3JX5K";//"ARafipSb7SknBnBKsQY1Na9CvKZPypeHORyOQ-f-1sEAfwt0NEymuOniy4__9oIJ1JYEleESrqSkESGB";*/"Acv-mIu5gkIOQStmK8CAco1p-h1geAElqFCOUEl0EQcdhSKsCmam5Z_IoZ0QiF0F4lu9YBZ2mT3KqQQJ";


    /**
     * live client id of IM for Telegram
     */

    private static final String CONFIG_CLIENT_ID = "ARafipSb7SknBnBKsQY1Na9CvKZPypeHORyOQ-f-1sEAfwt0NEymuOniy4__9oIJ1JYEleESrqSkESGB";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;
    private TextView buyContent;
    private TextView success;
    private Button payButton;
    private Button cancelButton;
    private android.widget.RadioButton rbPaypal;
    private Button getNow;
    private static boolean isForAdsFree;

    final private String APP_ID = "app185a7e71e1714831a49ec7";
    final private String ZONE_ID = "vzdf92c3785a1945eea7";


    private boolean isAddLoad=false;
    private boolean isLoading=false;
    private boolean isWatch=false;


    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("CRATERZONE PVT LTD").acceptCreditCards(true);


    private RadioGroup rgOption;
    private int paymentValue;
    private RadioButton adBtn;
    private ProgressBar progress;
    private Button show_button;

    private View view;
    private static View dialogView;
    private Dialog dialog;
    AlertDialog.Builder builder;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isForAdsFree) {
        onBuyPressed(2);
        }

        else {
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
   //      findViewById(R.id.ll_credit).setOnClickListener(this);
         findViewById(R.id.pay1).setOnClickListener(this);
         findViewById(R.id.pay2).setOnClickListener(this);
         findViewById(R.id.pay3).setOnClickListener(this);
         tv=(TextView)findViewById(R.id.buy_content);

        }
        builder = new AlertDialog.Builder(PaymentManager.this);

        tv.setText(getResources().getString(R.string.karmapoint)+" "+getKarmaBal());


    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void acttionBarSetup() {
        // getActionBar().setTitle(" Premium");
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.custom_actionbar);

    }




  /*  public void intializeAds()*//*{
        *//*
        LayoutInflater inflater = (LayoutInflater)PaymentManager.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       view = inflater.inflate(R.layout.layout_dialog_watch, null);
       show_button=(Button)view.findViewById(R.id.watch_ads);
        progress = (ProgressBar) view.findViewById( R.id.progress );*//*

        *//** Construct optional app options object to be sent with configure *//*
        AdColonyAppOptions app_options = new AdColonyAppOptions()
                .setUserID( "unique_user_id" );
        app_options.setOriginStore("Blackberry");

        *//**
         * Configure AdColony in your launching Activity's onCreate() method so that cached ads can
         * be available as soon as possible.
         *//*
        AdColony.configure( this, app_options, APP_ID, ZONE_ID );

        *//** Optional user metadata sent with the ad options in each request *//*
        AdColonyUserMetadata metadata = new AdColonyUserMetadata()
                .setUserAge( 26 )
                .setUserEducation( AdColonyUserMetadata.USER_EDUCATION_BACHELORS_DEGREE )
                .setUserGender( AdColonyUserMetadata.USER_MALE );

        *//** Ad specific options to be sent with request *//*
        ad_options = new AdColonyAdOptions()
                .enableConfirmationDialog( true )
                .enableResultsDialog( true )
                .setUserMetadata( metadata );




        listner1 = new AdColonyRewardListener() {
            @Override
            public void onReward(AdColonyReward adColonyReward) {

            }
        };


     *//*   *//**//** Create and set a reward listener *//**//*
        AdColony.setRewardListener( new AdColonyRewardListener()
        {
            @Override
            public void onReward( AdColonyReward reward )
            {
                *//**//** Query reward object for info here *//**//*
                Log.d( TAG, "onReward" );
            }
        } );*//*

        *//**
         * Set up listener for interstitial ad callbacks. You only need to implement the callbacks
         * that you care about. The only required callback is onRequestFilled, as this is the only
         * way to get an ad object.
         *//*
        listener = new AdColonyInterstitialListener()
        {
            *//** Ad passed back in request filled callback, ad can now be shown *//*
            @Override
            public void onRequestFilled( AdColonyInterstitial ad )
            {
                PaymentManager.this.ad = ad;
                isAddLoad=true;
                isLoading=false;
                *//* show_button.setEnabled( true );
                show_button.setSelected(true);
                progress.setVisibility( View.GONE );*//*
                Log.d( TAG, "onRequestFilled" );
            }

            *//** Ad request was not filled *//*
            @Override
            public void onRequestNotFilled( AdColonyZone zone )
            {
                isLoading=false;
                isAddLoad=false;
                //   progress.setVisibility( View.GONE );
                Log.d( TAG, "onRequestNotFilled");
            }

            *//** Ad opened, reset UI to reflect state change *//*
            @Override
            public void onOpened( AdColonyInterstitial ad )
            {
                isLoading=false;
                isAddLoad=false;
                *//*show_button.setEnabled( false );
                progress.setVisibility( View.VISIBLE );
                Log.d( TAG, "onOpened" );*//*
            }

            *//** Request a new ad if ad is expiring *//*
            @Override
            public void onExpiring( AdColonyInterstitial ad )
            {

                isLoading=false;
                isAddLoad=false;

                *//*show_button.setEnabled( false );
                progress.setVisibility( View.VISIBLE );
                AdColony.requestInterstitial( ZONE_ID, this, ad_options );
                Log.d( TAG, "onExpiring" );*//*
            }
        };

        *//** Set up button to show an ad when clicked *//*

       *//* show_button.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View view )
            {
              if(show_button.isSelected()){
                ad.show();}else{
                  Toast.makeText(PaymentManager.this,"Currently ads not available!",Toast.LENGTH_SHORT).show();
              }
            }
        } );*//*

    }*/


    /*public void showAds()*//*{
        *//** Construct optional app options object to be sent with configure *//*
        AdColonyAppOptions app_options = new AdColonyAppOptions()
                .setUserID( "unique_user_id" );

        *//**
         * Configure AdColony in your launching Activity's onCreate() method so that cached ads can
         * be available as soon as possible.
         *//*
        AdColony.configure( this, app_options, APP_ID, ZONE_ID );


        *//** Optional user metadata sent with the ad options in each request *//*
        AdColonyUserMetadata metadata = new AdColonyUserMetadata()
                .setUserAge( 26 )
                .setUserEducation( AdColonyUserMetadata.USER_EDUCATION_BACHELORS_DEGREE )
                .setUserGender( AdColonyUserMetadata.USER_MALE );

        *//** Ad specific options to be sent with request *//*
        ad_options = new AdColonyAdOptions()
                .enableConfirmationDialog( true )
                .enableResultsDialog( true )
                .setUserMetadata( metadata );



  /////////////////////////////////////////////////////
        *//** Create and set a reward listener *//*
        AdColony.setRewardListener( new AdColonyRewardListener()
        {
            @Override
            public void onReward( AdColonyReward reward )
            {
                *//** Query reward object for info here *//*
                Log.d( TAG, "onReward" );
            }
        } );

        *//**
         * Set up listener for interstitial ad callbacks. You only need to implement the callbacks
         * that you care about. The only required callback is onRequestFilled, as this is the only
         * way to get an ad object.
         *//*
        listener = new AdColonyInterstitialListener()
        {
            *//** Ad passed back in request filled callback, ad can now be shown *//*
            @Override
            public void onRequestFilled( AdColonyInterstitial ad )
            {
                PaymentManager.this.ad = ad;
             //   ad.show();

              //  show_button.setEnabled( true );
                //progress.setVisibility( View.INVISIBLE );
                Log.d( TAG, "onRequestFilled" );
            }

            *//** Ad request was not filled *//*
            @Override
            public void onRequestNotFilled( AdColonyZone zone )
            {
               // progress.setVisibility( View.INVISIBLE );
                Log.d( TAG, "onRequestNotFilled");
            }

            *//** Ad opened, reset UI to reflect state change *//*
            @Override
            public void onOpened( AdColonyInterstitial ad )
            {
               // show_button.setEnabled( false );
                //progress.setVisibility( View.VISIBLE );
                Log.d( TAG, "onOpened" );
            }

            *//** Request a new ad if ad is expiring *//*
            @Override
            public void onExpiring( AdColonyInterstitial ad )
            {
            //    show_button.setEnabled( false );
              //  progress.setVisibility( View.VISIBLE );
         //       AdColony.requestInterstitial( ZONE_ID, this, ad_options );
                Log.d( TAG, "onExpiring" );
            }
        };
        //ad.show();
        *//** Set up button to show an ad when clicked *//*
  *//*      show_button = (Button) findViewById( R.id.showbutton );
        show_button.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View view )
            {
                ad.show();
            }
        } );

  *//*
    }*/



    @Override
    protected void onResume() {
        super.onResume();

/*        if (ad == null || ad.isExpired())
        {
            *//**
             * Optionally update location info in the ad options for each request:
             * LocationManager location_manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
             * Location location = location_manager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
             * ad_options.setUserMetadata( ad_options.getUserMetadata().setUserLocation( location ) );
             *//*
//            progress.setVisibility( View.VISIBLE );

            AdColony.requestInterstitial( ZONE_ID, listener, ad_options );
            AdColony.setRewardListener(listner1);

        }
        if(isWatch){
            isWatch=false;
            SharedPreferences p = ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE);
            String mob = p.getString("mob", "9888888").replace(" ","");
            String cc  = p.getString("cCode", "US").replace(" ","");
            BackgroundExecuter.getInstance().execute(new AddKarmaRequester(new CreditModel("VIDEO_ADS", mob, cc), PaymentManager.this));

        }*/

        /**
         * It's somewhat arbitrary when your ad request should be made. Here we are simply making
         * a request if there is no valid ad available onResume, but really this can be done at any
         * reasonable time before you plan on showing an ad.
         */
   /*     if (ad == null || ad.isExpired())
        {
            *//**
             * Optionally update location info in the ad options for each request:
             * LocationManager location_manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
             * Location location = location_manager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
             * ad_options.setUserMetadata( ad_options.getUserMetadata().setUserLocation( location ) );
             *//*
         //   progress.setVisibility( View.VISIBLE );
            AdColony.requestInterstitial( ZONE_ID, listener, ad_options );


        }*/

       /* onBuyPressed(2);*/

        /*if(isForAdsFree){
        findViewById(R.id.op_paypal).setVisibility(View.GONE);
        findViewById(R.id.op_credit).setVisibility(View.GONE);
    }*/
    }

    public static void createIntent(Activity activity, boolean adsFree) {
        ActivityCompat.startActivity(activity, new Intent(activity, PaymentManager.class),null);

                isForAdsFree=adsFree;
             //   ActivityOptionsCompat.makeCustomAnimation(activity, android.R.anim.slide_in_left, android.R.anim.slide_out_right).toBundle());

    }




    public void onBuyPressed(int paymentValue) {
        /*
         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
         * Change PAYMENT_INTENT_SALE to
         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
         *     later via calls from your server.
         *
         * Also, to include additional payment details and an item list, see getStuffToBuy() below.
         */

        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE, String.valueOf(paymentValue));

        /*
         * See getStuffToBuy(..) for examples of some available payment options.
         */
        Intent intent = new Intent(PaymentManager.this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    public static  void payForAdsFreeScreen(){

    }

    /**
     * @param paymentIntent
     * @return
     */


    private PayPalPayment getThingToBuy(String paymentIntent, String paymentValue) {
        return new PayPalPayment(new BigDecimal(paymentValue), "USD", "PREMIUM FEATURES",
                paymentIntent);

    }

    public void onHomePressed(View view) {
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
                        Gson gson = new Gson(); // new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                        PaymentResponse response = gson.fromJson(confirm.toJSONObject().toString(), PaymentResponse.class);
                        UserPaymentInfo.getInstatance().setPaymentId(response.getResponse().getId());


                        UserPaymentInfo.getInstatance().setPaymentStatus(UserPaymentInfo.paidUser);


                        sendAuthorizationToServer(String.valueOf(paymentValue));

                        findViewById(R.id.content).setVisibility(View.GONE);

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


    private void sendAuthorizationToServer(String amount) {
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

        String id = UserPaymentInfo.getInstatance().getUserId();

        SharedPreferences p = ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE);
        // TODO change the amount selected by user.
        String cc = p.getString("cCode", "zz");
        String mob = p.getString("mob", "00000000");

        if (!id.equalsIgnoreCase("")) {
            BackgroundExecuter.getInstance().execute(new
                    ConfirmationRequester(UserPaymentInfo.getInstatance().getPaymentId(), id, amount, mob, cc));
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


    public String getKarmaBal(){
        SharedPreferences p = ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE);

        return p.getString("karmaBal","0");
    }



    @Override
    public void onAdsRemovePressed() {
        onBuyPressed(2);
    }






  /*  private void showads()*/ /*{

        HeyzapAds.start("67f2aa69c99998d435594817c610d439",PaymentManager.this,HeyzapAds.DISABLE_AUTOMATIC_FETCH);

        //HeyzapAds.startTestActivity(PaymentManager.this);

        InterstitialAd.fetch();

        InterstitialAd.setOnStatusListener(new HeyzapAds.OnStatusListener() {
            @Override
            public void onShow(String tag) {
                // Ad is now showing
            }
            @Override
            public void onClick(String tag) {
                // Ad was clicked on. You can expect the user to leave your application temporarily.
            }
            @Override
            public void onHide(String tag) {
                // Ad was closed. The user has returned to your application.
            }
            @Override
            public void onFailedToShow(String tag) {
                // Display was called but there was no ad to show
            }
            @Override
            public void onAvailable(String tag) {
                Log.d("PaymentManager","onAvailable");
                // An ad has been successfully fetched
                InterstitialAd.display(PaymentManager.this);
            }
            @Override
            public void onFailedToFetch(String tag) {
                Log.d("PaymentManager","onFailedToFetch");
                // No ad was able to be fetched
            }
            @Override
            public void onAudioStarted() {
                // The ad about to be shown will require audio. Any background audio should be muted.
            }
            @Override
            public void onAudioFinished() {
                // The ad being shown no longer requires audio. Any background audio can be resumed.
            }
        });

        IncentivizedAd.setOnIncentiveResultListener(new HeyzapAds.OnIncentiveResultListener() {
            @Override
            public void onComplete(String tag) {
                Log.d("PaymentManager","onComplete");
                // Give the player their reward
            }
            @Override
            public void onIncomplete(String tag) {
                Log.d("PaymentManager","onInComplete");
                // Don't give the player their reward, and tell them why
            }
        });
    }*/








    public void openDialog(boolean isAddLoa){

    LayoutInflater inflater = (LayoutInflater)PaymentManager.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_dialog_watch, null);
        builder.setView(view);
        show_button=(Button)view.findViewById(R.id.watch_ads);
        if(isAddLoad){
            show_button.setSelected(true);
        }
        else{show_button.setSelected(false);}
/*


        show_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ad!=null && show_button.isSelected() && isAddLoad){
                    ad.show();
                    isWatch=true;
                }else{
                    Toast.makeText(PaymentManager.this,"Currently ads not available!",Toast.LENGTH_SHORT).show();
                }
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();

*/

        
       /* view.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/
      /*  Button button = (Button)view.findViewById(R.id.btn_yes);
        button.setSelected(true);
        TextView textView = (TextView)view.findViewById(R.id.txt_dia) ;
        textView.setText("REMOVE ANNOYING ADS?\nPay once $2, Use forever!");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PaymentManager.createIntent(PaymentManager.this,false);
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
            }
        });*/
        /*  AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage("REMOVE ANNOYING ADS?\nPay once , Use forever!");
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
     *//*   final String arg1 = usePhone;*//*
        builder.setPositiveButton( "BUY $2.00", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    onBuyPressed(2);
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());*/
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch(id){

            case R.id.ll_credit:

                openDialog(isAddLoad);

                break;
            case R.id.pay1:
                onBuyPressed(10);
                break;

            case R.id.pay2:
                onBuyPressed(5);
                break;

            case R.id.pay3:
              onBuyPressed(1);
                break;

        }
    }

    @Override
    public void onAddKarmaSuccess(int credits) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(getResources().getString(R.string.karmapoint)+" "+getKarmaBal());
            }
        });

    }

    @Override
    public void onAddKarmaFailure() {

    }


}
