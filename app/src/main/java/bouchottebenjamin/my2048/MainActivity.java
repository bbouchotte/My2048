package bouchottebenjamin.my2048;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.net.rtp.RtpStream;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import static android.view.Gravity.CENTER;
import static bouchottebenjamin.my2048.R.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView[][] box = new TextView[4][4];
    private Integer[][] boxid = new Integer[4][4];
    private Game2048 game = new Game2048(MainActivity.this);
    private static final int TRYMOVE_LEFT = 0;
    private static final int TRYMOVE_UP = 1;
    private static final int TRYMOVE_RIGHT = 2;
    private static final int TRYMOVE_DOWN = 3;

    private static Integer[] colId = new Integer[21];
    private static Integer[] color = new Integer[21];

    private TextView scoreTV;
    private TextView lastTPV;
    private RatingBar bestTRB;

    private ImageButton buttonU;
    private ImageButton buttonL;
    private ImageButton buttonR;
    private ImageButton buttonD;

    private RelativeLayout controlLO;
    private LinearLayout rightLO;

    protected TableLayout boardLO;

    private TextView testPort;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(id.toolbar);
        setSupportActionBar(toolbar);

        scoreTV = (TextView) findViewById(id.scoreTV);
        lastTPV = (TextView) findViewById(id.lastTPV);
        bestTRB = (RatingBar) findViewById(id.bestTRB);

        buttonU = (ImageButton) findViewById(id.buttonU);
        buttonL = (ImageButton) findViewById(id.buttonL);
        buttonR = (ImageButton) findViewById(id.buttonR);
        buttonD = (ImageButton) findViewById(id.buttonD);

        buttonU.setOnClickListener(MainActivity.this);
        buttonL.setOnClickListener(MainActivity.this);
        buttonR.setOnClickListener(MainActivity.this);
        buttonD.setOnClickListener(MainActivity.this);

        controlLO = (RelativeLayout) findViewById(R.id.controlLO);
        rightLO = (LinearLayout) findViewById(R.id.rightLO);

        // Test de portabilité
        testPort = (TextView) findViewById(R.id.testPort);
        String testPortText = "Taille <";
        testPortText += getResources().getString(string.dimClass);
        testPortText += "> \n Densité <";
        testPortText += getResources().getString(string.densClass);
        testPortText += "> \n TextSize = ";
        testPortText += testPort.getTextSize();
        testPortText += " px";

        testPort.setText(testPortText);

        ////        Crée des lignes et des cases dans boardLO        ////
        boardLO = (TableLayout) findViewById(id.boardLO);

        for (int i=0; i<4; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(tableRowParams);
            row.setId(i);
            for (int j=0; j<4; j++) {
                TextView tile = new TextView(this);
                LinearLayout.LayoutParams tileLOParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                tileLOParams.setMargins(5, 5, 5, 5);
                tile.setLayoutParams(tileLOParams);
                TableRow.LayoutParams rowParams = (TableRow.LayoutParams) new TableRow.LayoutParams(50,LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                row.addView(tile, rowParams);
                // 31
                tile.setText("lc=" + Integer.toString(i) + Integer.toString(j));
                //
//                tile.setText("131072");

                Integer tileId = 100 + j + i * 4;
                tile.setId(tileId);
                //29
                boxid[i][j] = tileId;

                tile.setBackgroundColor(Color.WHITE);
                tile.setGravity(CENTER);
                tile.setTypeface(null, Typeface.BOLD);
                tile.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

                // 30
                box[i][j] = tile;
            }
            boardLO.addView(row);
        }
//        game.initTest();
        game.init();
        initColor();
        update();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonU:
                tryMove(TRYMOVE_UP);
                break;
            case R.id.buttonL:
                tryMove(TRYMOVE_LEFT);
                break;
            case R.id.buttonR:
                tryMove(TRYMOVE_RIGHT);
                break;
            case R.id.buttonD:
                tryMove(TRYMOVE_DOWN);
                break;
        }
    }

    private void update() {
        // Récupère les tuiles et les affiches dans les tv
        if (game.hasJustWon()) {
            Toast.makeText(getApplicationContext(), R.string.won, Toast.LENGTH_LONG).show();
        }
        if (game.isOver()) {
            Toast.makeText(getApplicationContext(), R.string.gameOver, Toast.LENGTH_LONG).show();
            lastTPV.setText(string.gameOver);
            controlLO.setVisibility(View.INVISIBLE);
        } else {
            controlLO.setVisibility(View.VISIBLE);
        }
        for (int l=0; l<4; l++) {
            for (int c = 0; c < 4; c++) {
                TextView tv = (TextView) findViewById(100 + c + l * 4);
                Game2048.Tile tile = game.getTile(l, c);

                String tileContent = tile.toString();
/*
                tileContent = tileContent.concat("\n2^");
                tileContent.concat(String.valueOf(game.getTile(l, c).getRank()));
*/
                tv.setBackgroundColor(color[tile.getRank()]);

                tv.setText(tileContent);

                if (tile.isNew()) {
                    tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colNT));
//                    tv.setTextColor(getResources().getColor());
                } else if (tile.getRank() <= 3) {
                    tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colDT));
                } else {
                    tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colBT));
                }
            }
        }
        scoreTV.setText("" + game.getScore());
        lastTPV.setText("" + game.getLastP());
//        Log.i("bestr", "update - bestR: " + String.valueOf(game.getBestR()));
        bestTRB.setRating(game.getBestR());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        ////        Adapte la taille de boardLO pour en faire un carré      ////
        LinearLayout globalLO = (LinearLayout) findViewById(id.globalLO);
        RelativeLayout scoreLO = (RelativeLayout) findViewById(id.scoreLO);
        boardLO = (TableLayout) findViewById(id.boardLO);
        RelativeLayout controlLO = (RelativeLayout) findViewById(id.controlLO);

        LinearLayout.LayoutParams boardLOParams = (LinearLayout.LayoutParams) boardLO.getLayoutParams();
      //  float boardLOWeight = boardLOParams.weight;

        float l = boardLO.getWidth();
        float h = boardLO.getHeight();
        float H = globalLO.getHeight();
        float D = globalLO.getWidth();
        float x = globalLO.getPaddingLeft();
        float w = 0;
        float wScore;
        float wControl;
        boolean portrait = globalLO.getOrientation() == LinearLayout.VERTICAL;

        if (portrait) {
            w = l * 100 / H;
            wScore = (float) (0.25 * (100 - w));
            wControl = (float) (0.75 * (100 - w));
        } else {
            w = 100f * h / ( D - 2 * x);
            wScore = 20;
            wControl = 80;
            LinearLayout.LayoutParams rightLOParams = (LinearLayout.LayoutParams) rightLO.getLayoutParams();
            rightLOParams.weight = 100f - w;
            rightLO.setLayoutParams(rightLOParams);
           // Log.i("zzzz", "paysage w: " + String.valueOf(w) + " - rightLOParams.weight: " + rightLOParams.weight);
        }
Log.i("zzzz", "wScore: " + String.valueOf(wScore) + " - wControl: " + String.valueOf(wControl));
        if (h > l) {
            Log.i("zzzz", "h > l");
                boardLOParams.weight = w;
                boardLO.setLayoutParams(boardLOParams);

                LinearLayout.LayoutParams scoreLOParams = (LinearLayout.LayoutParams) scoreLO.getLayoutParams();
                scoreLOParams.weight = wScore;
                scoreLO.setLayoutParams(scoreLOParams);

                LinearLayout.LayoutParams controlLOParams = (LinearLayout.LayoutParams) controlLO.getLayoutParams();
                controlLOParams.weight = wControl;
                controlLO.setLayoutParams(controlLOParams);

        } else {

            int padding = (int) ((D - h) / 2);
            Log.i("zzzz", "W: " + String.valueOf(D) + " h: " + String.valueOf(h) + " w: " + String.valueOf(boardLO.getWidth()) + " padding:  " + String.valueOf(padding));
            globalLO.setPadding(padding, 0, padding, 0);
            //CoordinatorLayout.LayoutParams globalLOParams = (CoordinatorLayout.LayoutParams) globalLO.getLayoutParams();
            //globalLOParams.width = h;
            Log.i("zzzz", "W: " + String.valueOf(D) + " h: " + String.valueOf(h) + " w: " + String.valueOf(boardLO.getWidth()) + " padding:  " + String.valueOf(padding));
            boardLOParams.width = (int) h;
            boardLO.setLayoutParams(boardLOParams);
        }
        //boardLO.setPadding(5, 5, 5, 5);

        ////        Adapte la taille des lignes et des tuiles       ////

        for (int i=0; i<4; i++) {
            TableRow row = (TableRow) findViewById(i);
            LinearLayout.LayoutParams rowParams = (LinearLayout.LayoutParams) row.getLayoutParams();
            rowParams.height = 0;
            rowParams.weight = (float) (w * 0.1);
            rowParams.width = (int) l - 10;
            row.setLayoutParams(rowParams);
            for(int j=0; j<4; j++) {
                TextView tile = (TextView) findViewById(100 + j + i * 4);
                LinearLayout.LayoutParams tileParams = (LinearLayout.LayoutParams) tile.getLayoutParams();
                tileParams.setMargins(5, 5, 5, 5);
                tile.setLayoutParams(tileParams);
            }
            super.onWindowFocusChanged(hasFocus);
        }

        ////        Adapte la taille des flèches        ////
        float controlLOHeight = controlLO.getHeight();
        float arrowLength = controlLOHeight * 5 / 13;
        float arrowWidth = arrowLength * 3 / 5;

        RelativeLayout.LayoutParams arrowParams = (RelativeLayout.LayoutParams) buttonU.getLayoutParams();
        arrowParams.height = (int) arrowLength;
        arrowParams.width = (int) arrowWidth;
        buttonU.setLayoutParams(arrowParams);

        arrowParams = (RelativeLayout.LayoutParams) buttonD.getLayoutParams();
        arrowParams.height = (int) arrowLength;
        arrowParams.width = (int) arrowWidth;
        buttonD.setLayoutParams(arrowParams);

        arrowParams = (RelativeLayout.LayoutParams) buttonL.getLayoutParams();
        arrowParams.height = (int) arrowWidth;
        arrowParams.width = (int) arrowLength;
        buttonL.setLayoutParams(arrowParams);

        arrowParams = (RelativeLayout.LayoutParams) buttonR.getLayoutParams();
        arrowParams.height = (int) arrowWidth;
        arrowParams.width = (int) arrowLength;
        buttonR.setLayoutParams(arrowParams);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void initColor(){
         colId[0] = R.color.col00;
         colId[1] = R.color.col01;
         colId[2] = R.color.col02;
         colId[3] = R.color.col03;
         colId[4] = R.color.col04;
         colId[5] = R.color.col05;
         colId[6] = R.color.col06;
         colId[7] = R.color.col07;
         colId[8] = R.color.col08;
         colId[9] = R.color.col09;
         colId[10] = R.color.col10;
         colId[11] = R.color.col11;
         colId[12] = R.color.col12;
         colId[13] = R.color.col13;
         colId[14] = R.color.col14;
         colId[15] = R.color.col15;
         colId[16] = R.color.col16;
         colId[17] = R.color.col17;
         colId[18] = R.color.colNT;
         colId[19] = R.color.colDT;
         colId[20] = R.color.colBT;

        for (int i=0; i<18; i++){
//            color[i] = getResources().getColor(colId[i]);
            color[i] = ContextCompat.getColor(getApplicationContext(), colId[i]);
        }
    }

    private void tryMove(int dir) {
//        Toast.makeText(getApplicationContext(), "code direction: " + dir, Toast.LENGTH_SHORT).show();
        boolean croiss = false, vert = false;
        if (dir == TRYMOVE_UP || dir == TRYMOVE_RIGHT) {
            croiss = true;
        }
        if (dir == TRYMOVE_UP || dir == TRYMOVE_DOWN) {
            vert = true;
        }
        if (game.move(croiss, vert)) {
            update();
 //           Toast.makeText(getApplicationContext(), "Mouvement valide", Toast.LENGTH_SHORT).show();
        } else {
  //          Toast.makeText(getApplicationContext(), "Mouvement invalide", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == R.id.action_new) {
            game.init();
            update();
//            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle b = new Bundle();
        Game2048.SaveBundle sb = game.getSaveBundle();
        b.putString("board", sb.board);
        b.putString("lastP", sb.lastP);
        b.putInt("score", sb.score);
        outState.putBundle("sb", b);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Bundle b = savedInstanceState.getBundle("sb");
        Game2048.SaveBundle sb = new Game2048.SaveBundle();
        sb.board = b.getString("board");
        sb.lastP = b.getString("lastP");
        sb.score = b.getInt("score");
        game.restoreFromSaveBundle(sb);
        update();
    }
}
