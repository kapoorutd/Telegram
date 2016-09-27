package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.telegram.messenger.R;
import org.telegram.payment.CheckPremiumListener;
import org.telegram.payment.CheckPremiumUserRequester;
import org.telegram.payment.UserPaymentInfo;
import org.telegram.socialuser.BackgroundExecuter;

//import com.smaato.soma.AdDimension;
//import com.smaato.soma.BannerView;

/**
 * Created by craterzone3 on 20/5/16.
 */
public class AdvertiesmentCell extends FrameLayout implements CheckPremiumListener {

    private AdView mAdView;

    public AdvertiesmentCell(Context mContext) {
        super(mContext);
        View mView = View.inflate(mContext, R.layout.layout_ad, null);
        addView(mView);
        mAdView = (AdView) findViewById(R.id.adView);

        if( UserPaymentInfo.getInstatance().getPaymentStatus()==UserPaymentInfo.unPaidUser) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
        else if(!UserPaymentInfo.getInstatance().getUserId().equalsIgnoreCase("") && UserPaymentInfo.getInstatance().getPaymentStatus()==UserPaymentInfo.paidUser){
            mAdView.setVisibility(GONE);
        }



        if(UserPaymentInfo.getInstatance().getPaymentStatus() == 0){
            BackgroundExecuter.getInstance()
                    .execute(new CheckPremiumUserRequester
                            (UserPaymentInfo.getInstatance().getUserId(),this));

        }
    }

    @Override
    public void onPremiumUser() {
        mAdView.setVisibility(GONE);
        UserPaymentInfo.getInstatance().setPaymentStatus(UserPaymentInfo.paidUser);
    }

    @Override
    public void onGeneralUser() {
        UserPaymentInfo.getInstatance().setPaymentStatus(UserPaymentInfo.unPaidUser);
    }

}