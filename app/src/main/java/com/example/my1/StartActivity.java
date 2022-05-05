package com.example.my1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
public class StartActivity extends Activity implements OnClickListener {

    private static final int STOPSPLASH = 0;
    private static final long SPLASHTIME = 10000 ; //Время показа Splash картинки 10 секунд
    private ImageView splash;

    private Handler splashHandler = new Handler() { //создаем новый хэндлер
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STOPSPLASH:
                    //убираем Splash картинку - меняем видимость
                    splash.setVisibility(View.GONE);

                    break;
            }
            super.handleMessage(msg);
        }
    };


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // если хотим, чтобы приложение постоянно имело портретную ориентацию
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // если хотим, чтобы приложение было полноэкранным
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // и без заголовка
        requestWindowFeature(Window.FEATURE_NO_TITLE);



        setContentView(R.layout.menu);
        //setContentView(new GameView(this,null));

        Button startButton = (Button)findViewById(R.id.button1);
        startButton.setOnClickListener(this);

        Button exitButton = (Button)findViewById(R.id.button2);
        exitButton.setOnClickListener(this);

        Button infoButton = (Button)findViewById(R.id.button3);
        infoButton.setOnClickListener(this);

        Button helpButton = (Button)findViewById(R.id.button4);
        helpButton.setOnClickListener(this);

        splash = (ImageView) findViewById(R.id.splashscreen); //получаем индентификатор ImageView с Splash картинкой
        Message msg = new Message();
        msg.what = STOPSPLASH;
        splashHandler.sendMessageDelayed(msg, SPLASHTIME);




    }

    /** Обработка нажатия кнопок */
    public void onClick(View v) {
        switch (v.getId()) {
            //переход на сюрфейс
            case R.id.button1: {
                Intent intent = new Intent();
                intent.setClass(this, MainActivity.class);
                startActivity(intent);

            }break;

            //выход
            case R.id.button2: {
                finish();
            }break;

            case R.id.button3:{
                Intent intent = new Intent();
                intent.setClass(this, InfoActivity.class);
                startActivity(intent);
            }break;

            case R.id.button4:{
                Intent intent = new Intent();
                intent.setClass(this, HelpActivity.class);
                startActivity(intent);
            }break;

            default:
                break;
        }
    }
}

