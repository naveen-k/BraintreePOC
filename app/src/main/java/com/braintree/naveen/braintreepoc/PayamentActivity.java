package com.braintree.naveen.braintreepoc;

import com.braintreepayments.api.BraintreePaymentActivity;
import com.braintreepayments.api.PaymentRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;

import android.content.Intent;
import android.widget.Toast;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;


import cz.msebera.android.httpclient.Header;

/**
 * A login screen that offers login via email/password.
 */
public class PayamentActivity extends AppCompatActivity {



    private static final String SERVER_BASE = "https://test-server-buat.herokuapp.com"; // Replace with your own server
    private static final int REQUEST_CODE = Menu.FIRST;
    private AsyncHttpClient client = new AsyncHttpClient();

    private String sClientToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payament);
        // Set up the login form.


        Button mTestTransection = (Button) findViewById(R.id.btn_start);
        mTestTransection.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartClick(view);
            }
        });
        getToken();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void getToken() {
        client.post(SERVER_BASE + "/client_token", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                findViewById(R.id.btn_start).setEnabled(false);
                Toast.makeText(PayamentActivity.this, "Token issue..!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                sClientToken = responseString;
                findViewById(R.id.btn_start).setEnabled(true);
                Toast.makeText(PayamentActivity.this, "Token !!!", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void onStartClick(View view) {
        PaymentRequest paymentRequest = new PaymentRequest()
                .clientToken(sClientToken)
                .amount("$10.00")
                .primaryDescription("Awesome payment")
                .secondaryDescription("Using the Client SDK")
                .submitButtonText("Pay");

        startActivityForResult(paymentRequest.getIntent(this), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == BraintreePaymentActivity.RESULT_OK) {
            PaymentMethodNonce paymentMethodNonce = data.getParcelableExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);

            RequestParams requestParams = new RequestParams();
            requestParams.put("payment_method_nonce", paymentMethodNonce.getNonce());
            requestParams.put("amount", "10.00");

            client.post(SERVER_BASE + "/checkouts", requestParams, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(PayamentActivity.this, responseString, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    //Toast.makeText(PayamentActivity.this, responseString, Toast.LENGTH_LONG).show();
                    gettransectionDetails(responseString);
                }
            });
        }
    }

    private void gettransectionDetails(String transectionID) {
        client.get(SERVER_BASE + "/checkouts/"+transectionID, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                findViewById(R.id.btn_start).setEnabled(false);
                Toast.makeText(PayamentActivity.this, responseString, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                sClientToken = responseString;
                findViewById(R.id.btn_start).setEnabled(true);
                System.out.println(responseString);
                Toast.makeText(PayamentActivity.this, responseString, Toast.LENGTH_LONG).show();
            }
        });
    }
}
