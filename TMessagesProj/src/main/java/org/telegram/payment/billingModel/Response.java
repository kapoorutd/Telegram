package org.telegram.payment.billingModel;

/**
 * Created by craterzone3 on 15/6/16.
 */

public class Response {

    private String state;
    private String id;
    private String create_time;
    private String intent;


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }




}
