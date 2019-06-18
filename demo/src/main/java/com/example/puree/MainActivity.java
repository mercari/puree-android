package com.example.puree;

import com.example.puree.logs.plugins.OutProtobufDisplay;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.mercari.puree.Puree;
import com.example.puree.logs.ClickLog;
import com.example.puree.logs.PvLog;
import com.example.puree.logs.plugins.OutBufferedDisplay;
import com.example.puree.logs.plugins.OutDisplay;
import com.example.event.Event;
import com.google.protobuf.MessageLite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TextView logDisplayTextView;
    private Button button1;
    private Button button2;
    private Button button3;

    private final OutDisplay.Callback outDisplayCallback = new OutDisplay.Callback() {
        @Override
        public void onEmit(JsonObject jsonLog) {
            prependOutput(jsonLog.toString());
        }
    };

    private final OutProtobufDisplay.Callback outProtobufDisplayCallback = new OutProtobufDisplay.Callback() {
        @Override
        public void onEmit(MessageLite protoLog) {
            prependOutput(protoLog.toString());
        }
    };

    private final OutBufferedDisplay.Callback outBufferedDisplayCallback = new OutBufferedDisplay.Callback() {
        @Override
        public void onEmit(JsonArray jsonLogs) {
            prependOutput(jsonLogs.toString());
        }
    };

    private void prependOutput(String text) {
        logDisplayTextView.setText(new StringBuilder()
                .append(text)
                .append(System.getProperty("line.separator"))
                .append(logDisplayTextView.getText())
                .toString());
    }

    private void clear() {
        Puree.discardBufferedLogs();
        logDisplayTextView.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        OutDisplay.register(outDisplayCallback);
        OutProtobufDisplay.register(outProtobufDisplayCallback);
        OutBufferedDisplay.register(outBufferedDisplayCallback);
        Puree.send(new PvLog(this));
        setupViews();
    }

    @Override
    protected void onDestroy() {
        OutDisplay.unregister();
        OutBufferedDisplay.unregister();
        super.onDestroy();
    }

    private void findViews() {
        logDisplayTextView = (TextView) findViewById(R.id.log_display);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
    }

    private void setupViews() {
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Puree.send(new ClickLog("MainActivity", "BUTTON 1"));
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Puree.send(new ClickLog("MainActivity", "BUTTON 2"));
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Event clickEvent = Event.newBuilder()
                        .setEventTime(Calendar.getInstance().getTime().toString())
                        .setUserId(123456)
                        .setEventType("click").build();
                Puree.send(clickEvent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_benchmark:
                startActivity(new Intent(this, BenchmarkActivity.class));
                return true;
            case R.id.action_dump:
                Puree.dump();
                return true;
            case R.id.action_clear:
                clear();
                return true;
            case R.id.action_settings:
                return true;
            default:
                // do nothing
        }
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
