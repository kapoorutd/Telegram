package org.telegram.calling;

import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.telegram.messenger.R;

public class PlaceCallActivity extends BaseActivity implements SinchService.StartFailedListener{

    private Button mCallButton;
    private EditText mCallName;
    private Button mLoginButton;
    private EditText mLoginName;
    private ProgressDialog mSpinner;
 //   private Button logIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mCallName = (EditText) findViewById(R.id.callName);
        mCallButton = (Button) findViewById(R.id.callButton);
        mCallButton.setEnabled(false);
        mCallButton.setOnClickListener(buttonClickListener);

        if(getIntent().getExtras()!=null){
            mCallName.setText(getIntent().getStringExtra("mob"));
        }

      }

    @Override
    protected void onServiceConnected() {

         // logIn.setVisibility(View.GONE);
         getSinchServiceInterface().setStartListener(this);
         TextView userName = (TextView) findViewById(R.id.loggedInName);
         userName.setText(getSinchServiceInterface().getUserName());
         mCallButton.setEnabled(true);
    }

// TO Do if you want to stop the service then enable this
    private void stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        finish();
    }

    private void callButtonClicked() {
        loginClicked();
        String phoneNumber = mCallName.getText().toString();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a number to call", Toast.LENGTH_LONG).show();
            return;
        }

        Call call = getSinchServiceInterface().callPhoneNumber(phoneNumber);
        String callId = call.getCallId();

        Intent callScreen = new Intent(this, CallScreenActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        startActivity(callScreen);
    }


    private void loginClicked() {
      //ToDO // get unique  user name to register a user:
        String userName = "Ashish Srivastava";
      try {
    if (!getSinchServiceInterface().isStarted()) {
        getSinchServiceInterface().startClient(userName);
        showSpinner();
    }
    }     catch (Exception e){

     }
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }

    private OnClickListener buttonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.callButton:
                    callButtonClicked();
                    break;

               /* case R.id.stopButton:
                    stopButtonClicked();
                    break;*/

            }
        }
    };
    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    public void onStarted() {
       // findViewById(R.id.ll_caller).setVisibility(View.VISIBLE);
     //   logIn.setVisibility(View.GONE);
        if (mSpinner != null) {
            mSpinner.dismiss();
        }


    }



    @Override
    protected void onServiceDisconnected() {
        super.onServiceDisconnected();

    }
}
