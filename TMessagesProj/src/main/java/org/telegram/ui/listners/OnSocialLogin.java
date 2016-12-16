package org.telegram.ui.listners;

/**
 * Created by ram on 14/6/16.
 */
public interface OnSocialLogin {
    void onSocialLoginSuccess();
    void onSocialLoginError();
    void onSocialFailer(String userid);
}
