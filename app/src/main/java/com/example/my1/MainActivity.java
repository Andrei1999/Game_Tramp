package com.example.my1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    public static boolean isLeftPressed = false; // нажата кнопка
    public static boolean isRightPressed = false;
    public static boolean isUpPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        // not rabit
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);



        GameView gameView = new GameView(this); // создаём gameView
        LinearLayout gameLayout = (LinearLayout) findViewById(R.id.gameLayout); // находим gameLayout
        gameLayout.addView(gameView); // и добавляем в него gameView

        Button leftButton = (Button) findViewById(R.id.leftButton); // находим кнопки
        Button rightButton = (Button) findViewById(R.id.rightButton);
        Button upButton = (Button) findViewById(R.id.upButton);

        leftButton.setOnTouchListener(this); // и добавляем этот класс как слушателя (при нажатии сработает onTouch)
        rightButton.setOnTouchListener(this);
        upButton.setOnTouchListener(this);



    }

    public boolean onTouch(View button, MotionEvent motion) {
        switch(button.getId()) { // определяем какая кнопка
            case R.id.leftButton:
                switch (motion.getAction()) { // определяем нажата или отпущена
                    case MotionEvent.ACTION_DOWN:
                        isLeftPressed = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isLeftPressed = false;
                        break;
                }
                break;
            case R.id.rightButton:
                switch (motion.getAction()) { // определяем нажата или отпущена
                    case MotionEvent.ACTION_DOWN:
                        isRightPressed = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isRightPressed = false;
                        break;
                }
                break;
            case R.id.upButton:
                switch (motion.getAction()) { // определяем нажата или отпущена
                    case MotionEvent.ACTION_DOWN:
                        isUpPressed = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isUpPressed = false;
                        break;
                }
                break;

        }
        return true;
    }

}

class GameView extends SurfaceView implements Runnable {

    public static int maxX = 500; // размер по горизонтали
    public static int maxY = 570; // размер по вертикали
    public static float unitW = 0; // пикселей в юните по горизонтали
    public static float unitH = 0; // пикселей в юните по вертикали

    private boolean firstTime = true;
    private boolean gameRunning = true;
    private Player player;
    private Thread gameThread = null;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Bitmap bitmapIdBG;
    private Bitmap bitmapBG;
    private Bitmap bitmapIdHeart;
    private Bitmap bitmapHeart;
    private Paint mScorePaint;


    private ArrayList<Trash> trashes = new ArrayList<>(); // тут будут харанится астероиды
    private ArrayList<Money> monies = new ArrayList<>(); // тут будут харанится астероиды
    private final int trashInterval = 50; // время через которое появляются астероиды (в итерациях)
    private final int moneyInterval = 70;
    private int currentTime = 0;

    private void checkCollision(){ // перебираем все астероиды и проверяем не касается ли один из них корабля
        for (Trash trash: trashes) {
            if(trash.isCollision(player.x, player.y, player.size)){
                player.XP -= 1;
                if (player.XP < 0){
                    gameRunning = false;
                }
                trashes.remove(trash);
                break;
            }

        }
        for (Money money: monies) {
            if(money.isCollision(player.x, player.y, player.size)){
                player.check += (money.randomId + 1) * 10;
                System.out.println(player.check);
                monies.remove(money);
                break;
            }

        }
    }

    private void checkIfNewAsteroid(){ // каждые 50 итераций добавляем новый астероид
        if(currentTime >= trashInterval){
            Trash trash = new Trash(getContext());
            trashes.add(trash);

            Money money = new Money(getContext());
            monies.add(money);

            money = new Money(getContext());
            monies.add(money);

            currentTime = 0;
        }else{
            currentTime ++;
        }

    }

    public GameView(Context context) {
        super(context);
        //инициализируем обьекты для рисования
        surfaceHolder = getHolder();
        paint = new Paint();


        // инициализируем поток
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (gameRunning) {
            update();
            draw();
            checkCollision();
            checkIfNewAsteroid();
            control();
        }
    }

    private void update() {
        if(!firstTime) {
            player.update();
            for (Trash trash : trashes) {
                trash.update();
            }

            for (Money money : monies) {
                money.update();
            }
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {  //проверяем валидный ли surface

            if(firstTime){ // инициализация при первом запуске
                firstTime = false;
                unitW = surfaceHolder.getSurfaceFrame().width()/maxX; // вычисляем число пикселей в юните
                unitH = surfaceHolder.getSurfaceFrame().height()/maxY;
                createBG();
                player = new Player(getContext()); // добавляем корабль
                createPaint();

            }

            canvas = surfaceHolder.lockCanvas(); // закрываем canvas
            drawBG(bitmapBG);
            //canvas.drawColor(Color.WHITE); // заполняем фон белым

            drawXPPlayer(player.XP);
            drawCheck(player.check);

            player.drow(paint, canvas); // рисуем персонажа
            for(Trash trash: trashes){ // рисуем мусор
                trash.drow(paint, canvas);
                if (trash.isDelete()) {
                    trashes.remove(trash);
                    break;
                }
            }
            //System.out.println(trashes);
            for(Money money: monies){ // рисуем мусор
                money.drow(paint, canvas);
                if (money.isDelete()) {
                    monies.remove(money);
                    break;

                }
            }


            surfaceHolder.unlockCanvasAndPost(canvas); // открываем canvas
        }
    }

    private void createBG(){
        bitmapIdBG = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        bitmapBG = Bitmap.createScaledBitmap(
                bitmapIdBG, 1080, 1450, false);
        bitmapIdBG.recycle();
    }

    private void drawBG(Bitmap bg) {
        canvas.drawBitmap(bg, 0, 0, paint);
    }

    private void drawXPPlayer(Integer n){
        bitmapIdHeart = BitmapFactory.decodeResource(getResources(), R.drawable.heart2);
        bitmapHeart = Bitmap.createScaledBitmap(
                bitmapIdHeart, 70, 70, false);
        bitmapIdHeart.recycle();
        for (int i = 0; i < n; i++){
            canvas.drawBitmap(bitmapHeart, 20 + i * 80, 20, paint);
        }

    }

    private void createPaint() {
        mScorePaint = new Paint();
        mScorePaint.setTextSize(60);
        mScorePaint.setStrokeWidth(1);
        mScorePaint.setStyle(Paint.Style.FILL);
        mScorePaint.setTextAlign(Paint.Align.RIGHT);
        mScorePaint.setColor(Color.WHITE);
    }

    private void drawCheck(Integer n) {
        canvas.drawText("Счёт: " + String.valueOf(n), 1000, 100, mScorePaint);
    }

    private void control() { // пауза на 25 миллисекунд
        try {
            gameThread.sleep(25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

class PlatformBody {
    protected float x; // координаты
    protected float y;
    protected float size; // размер
    protected float speed; // скорость
    protected int bitmapId; // id картинки
    protected Bitmap bitmap; // картинка

    void init(Context context) { // сжимаем картинку до нужных размеров
        Bitmap cBitmap = BitmapFactory.decodeResource(context.getResources(), bitmapId);
        bitmap = Bitmap.createScaledBitmap(
                cBitmap, (int)(size * GameView.unitW), (int)(size * GameView.unitH), false);
        cBitmap.recycle();
    }

    void update(){ // тут будут вычисляться новые координаты
    }

    void drow(Paint paint, Canvas canvas){ // рисуем картинку
        canvas.drawBitmap(bitmap, x*GameView.unitW, y*GameView.unitH, paint);
    }
}

class Player extends PlatformBody{
    private boolean isJump = false;
    private int jumpCount = 10;
    private int idCostume = 0;
    private Context c;
    private ArrayList<Integer> costumesIdLeft = new ArrayList<>();
    private ArrayList<Integer> costumesIdRight = new ArrayList<>();
    public int check = 0;
    public int XP = 3;


    public Player(Context context) {
        c = context;
        bitmapId = R.drawable.pygame_idle; // определяем начальные параметры
        size = 60;
        x= GameView.maxX / 2 - 20;
        y= GameView.maxY - size + 140;
        speed = (float) 10;
        init(context); // инициализируем корабль
        fillCostume();
    }

    @Override
    public void update() { // перемещаем корабль в зависимости от нажатой кнопки
        if(MainActivity.isLeftPressed && x >= 0){
            x -= speed;
            nextImg(c, costumesIdLeft.get(idCostume));
            idCostume += 1;
            idCostume %= 6;
        }
        else if(MainActivity.isRightPressed && x <= GameView.maxX - 20) {
            x += speed;
            nextImg(c, costumesIdRight.get(idCostume));
            idCostume += 1;
            idCostume %= 6;
        }
        else {
            nextImg(c, R.drawable.pygame_idle);
        }
        //System.out.println(y);
        if (!isJump) {
            if (MainActivity.isUpPressed) {
                isJump = true;
            }
        } else {
            if (jumpCount >= -10) {
                if (jumpCount < 0) {
                    y += Math.pow(jumpCount, 2) / 2;

                } else {
                    y -= Math.pow(jumpCount, 2) / 2;
                }
                jumpCount -= 1;
            } else {
                y = GameView.maxY - size + 140;
                isJump = false;
                jumpCount = 10;
            }
        }

    }



    private void nextImg(Context context, Integer id) {
        Bitmap cBitmap = BitmapFactory.decodeResource(context.getResources(), id);
        bitmap = Bitmap.createScaledBitmap(cBitmap, (int) (size * GameView.unitW),
                (int) (size * GameView.unitH), false);
        cBitmap.recycle();
    }

    private void fillCostume(){
        costumesIdLeft.add(R.drawable.pygame_left_1);
        costumesIdLeft.add(R.drawable.pygame_left_2);
        costumesIdLeft.add(R.drawable.pygame_left_3);
        costumesIdLeft.add(R.drawable.pygame_left_4);
        costumesIdLeft.add(R.drawable.pygame_left_5);
        costumesIdLeft.add(R.drawable.pygame_left_6);
        costumesIdRight.add(R.drawable.pygame_right_1);
        costumesIdRight.add(R.drawable.pygame_right_2);
        costumesIdRight.add(R.drawable.pygame_right_3);
        costumesIdRight.add(R.drawable.pygame_right_4);
        costumesIdRight.add(R.drawable.pygame_right_5);
        costumesIdRight.add(R.drawable.pygame_right_6);
    }

}

class Trash extends PlatformBody {
    private int radius = 30; // радиус
    private float minSpeed = (float) 5; // минимальная скорость
    private float maxSpeed = (float) 13; // максимальная скорость

    public Trash(Context context) {
        Random random = new Random();

        bitmapId = R.drawable.garbage;
        y=0;
        x = random.nextInt(GameView.maxX) - radius + 30;
        size = radius;
        speed = minSpeed + (maxSpeed - minSpeed) * random.nextFloat();

        init(context);
    }

    @Override
    public void update() {
        y += speed;
    }

    public boolean isCollision(float playerX, float playerY, float playerSize) {
        return !(((x+size) < playerX)||(x > (playerX+playerSize))||((y+size) < playerY)||(y > (playerY+playerSize)));
    }

    public boolean isDelete(){
        return y > 680;
    }
}

class Money extends PlatformBody {
    private int radius = 30; // радиус
    private float minSpeed = (float) 5; // минимальная скорость
    private float maxSpeed = (float) 13; // максимальная скорость
    private ArrayList<Integer> costumes = new ArrayList<>();
    public int randomId = 0;


    public Money(Context context) {
        Random random = new Random();
        loadedCostumes();
        randomId = (int) (Math.random() * 3);
        bitmapId = costumes.get(randomId);
        y=0;
        x = random.nextInt(GameView.maxX) - radius + 30;
        size = radius;
        speed = minSpeed + (maxSpeed - minSpeed) * random.nextFloat();

        init(context);
    }

    private void loadedCostumes() {
        costumes.add(R.drawable.oney1);
        costumes.add(R.drawable.money2);
        costumes.add(R.drawable.oney3);
    }



    @Override
    public void update() {
        y += speed;
    }

    public boolean isCollision(float playerX, float playerY, float playerSize) {
        return !(((x+size) < playerX)||(x > (playerX+playerSize))||((y+size) < playerY)||(y > (playerY+playerSize)));
    }

    public boolean isDelete(){
        return y > 680;
    }
}