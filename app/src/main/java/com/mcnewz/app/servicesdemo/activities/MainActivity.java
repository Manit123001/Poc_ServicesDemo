package com.mcnewz.app.servicesdemo.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mcnewz.app.servicesdemo.services.MyIntentService;
import com.mcnewz.app.servicesdemo.services.MyStartedService;
import com.mcnewz.app.servicesdemo.R;

public class MainActivity extends AppCompatActivity {
    private TextView txvIntentServiceResult, txvStartedServiceResult;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txvIntentServiceResult = (TextView) findViewById(R.id.txvIntentServiceResult);
        txvStartedServiceResult = (TextView) findViewById(R.id.txvStartedServiceResult);

    }

    public void startStartedService(View view) {

        Intent intent = new Intent(MainActivity.this, MyStartedService.class);
        intent.putExtra("sleepTime", 10);
        startService(intent);
    }

    public void stopStartedService(View view) {

        Intent intent = new Intent(MainActivity.this, MyStartedService.class);
        stopService(intent);
    }

    public void startIntentService(View view) {
        ResultReceiver myResultReceiver = new MyesultReceiver(null);

        Intent intent = new Intent(this, MyIntentService.class);
        intent.putExtra("sleepTime", 10);
        intent.putExtra("receiver", myResultReceiver);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.service.to.activity");
        registerReceiver(myStartedServiceReceiver, intentFilter);
    }

    private BroadcastReceiver myStartedServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("startServiceResult");
            txvStartedServiceResult.setText(result);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myStartedServiceReceiver);
    }

    public void moveToSecondActivity(View view) {
        Intent intent = new Intent(MainActivity.this, MyBoundActivity.class);
        startActivity(intent);
    }

    public void moveToMessengerActivity(View view) {

        Intent intent = new Intent(this, MyMessengerActivity.class);
        startActivity(intent);
    }

    // To receive the data back from MyIntentService.java using ResultReceiver
    private class MyesultReceiver extends ResultReceiver {

        public MyesultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            Log.i("MyResultReceiver", Thread.currentThread().getName());

            if (resultCode == 18 && resultData != null) {
                final String result = resultData.getString("resultIntentService");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("MyHandler", Thread.currentThread().getName());
                        txvIntentServiceResult.setText(result);
                        Toast.makeText(MainActivity.this, "Hello: " + result, Toast.LENGTH_SHORT).show();

                    }
                });
            }


        }
    }
}
