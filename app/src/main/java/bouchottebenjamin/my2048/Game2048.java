package bouchottebenjamin.my2048;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Benj on 29/03/2016.
 */
public class Game2048 {

    private int score = 0;
    private int bestR = 0;      // meilleur (rating)
    private int nbv = 0;
    private Random rand = new Random();
    private String lastP = "";       // chaine de carac à gauche pour le calcul
    private boolean goal;

    public static class Tile {
        /**
         * état de la tuile
         *      -1: ajoutée aléatoirement à la fin du tour
         *      0: case vide ou tuile déjà présente à la fin du tour
         *      1: Tuile nouvellement fusionnée
         */
        private Integer flag;

        /**
         * Entier, la puissance
         */
        private Integer r;

        /**
         * valeurs croissantes des puissances de 0 puis 2puiss1 à 2puiss17
         */
        private static Integer pow2[] = new Integer[18];

        public Tile() {
            this.flag = 0;
            this.r = 0;
            // Valeurs des puissances
            pow2[0] =  0;
            for (int i=1; i<18; i++) {
                pow2[i] = (int) Math.pow(2, i);
            }
        }

        public Integer getFlag() {
            return flag;
        }

        public Integer getRank() {
            return r;
        }

        public Integer value(){
            return pow2[this.r];
        }

        public void setFlag(Integer flag) {
            this.flag = flag;
        }

        public void setRank(Integer r) {
            this.r = r;
        }

        public void set(int rk, int fg) {
            this.r = rk;
            this.flag = fg;
        }

        public boolean isNew() {
            return (flag == -1);
        }

        public boolean isFusion() {
            return (flag == 1);
        }

        @Override
        public String toString() {
            if(r == 0) {
                return "";
            } else {
                return String.valueOf(pow2[this.r]);
            }
        }

        public String log() {   // Créé la chaine log pour la sauvegarde
            String flag = "+ ^";
            String rank = " 123456789ABCDEFGH";

            String  log = Character.toString(flag.charAt(this.flag + 1));
            log = log.concat(String.valueOf(rank.charAt(this.getRank())));

            return log;
        }

        public void setFromLog(String log) {
            String rank = " 123456789ABCDEFGH";
            String flag = "+ ^";
            this.setFlag(flag.indexOf(log.charAt(0) - 1));
            this.setRank(rank.indexOf(log.charAt(1)));
        }
    }

    public static class SaveBundle {
        public String board = "";
        public String lastP = "";
        public int score = 0;
        //TODO question 110????
    }

    private Context context;
    private Tile[][] board = new Tile[4][4];

    public Game2048(Context context) {
        this.context = context;
        for (int l=0; l<4; l++) {
            for (int c=0; c<4; c++) {
                board[l][c] = new Tile();
            }
        }

        //64 pour essais
//        this.score = 3932000;
//        this.lastP = "2 + 4 + 128";
//        this.bestR = 5;
    }

    public Tile getTile(int l, int c) {
        return board[l][c];
    }

    // 86
    private Tile getTile(int lc, int i, boolean croiss, boolean vert) {
        int l;
        int c;
        if (vert) {
            c = lc;
            if (croiss) {
                l = i;
            } else {
                l = 3 - i;
            }
        } else {
            l = lc;
            if (croiss) {
                c = 3 - i;
            } else {
                c = i;
            }
        }
        return board[l][c];
    }

    private void addTile() {
        // Q77
        int min = 1;
        int max = this.nbv;
        int alea = rand.nextInt(max - min + 1) + min;
        int emptyTileNumber = 0;
        outerloop:
        for (int l = 0; l < 4; l++) {
            for (int c = 0; c < 4; c++) {
                if (board[l][c].getFlag() == 0 && board[l][c].getRank() == 0) {
                    emptyTileNumber++;
                    if (emptyTileNumber == alea) {       // Une case vide a été choisie

                        // génère aléatoirement un 2 (90% de chance) ou un 4 (10%)
                        min = 1;
                        max = 99;
                        alea = rand.nextInt(max - min + 1) + min;
                        int rank = alea > 9 ? 1 : 2;
                        board[l][c].setRank(rank);

                        // Q77
//                        board[l][c].setRank(1);
                        board[l][c].setFlag(-1);
                        this.nbv--;
                        this.bestR = rank > this.bestR ? rank : this.bestR;
 //                       Log.i("zzzz", "addTile - bestR: " + this.bestR + " rank: " + String.valueOf(rank) + " nbv:" + String.valueOf(nbv) + " -- l: " + String.valueOf(l) + " -- c: " + String.valueOf(c));
                        break outerloop;
                    }
                }
            }
        }
    }

    //40
    public void init() {
        this.goal = false;
        for (int l = 0; l < 4; l++) {
            for (int c = 0; c < 4; c++) {
                // Q40
                //board[l][c].r = 17 - (l * 4 + c);       // affiche 2  à 131072 pour tests
                board[l][c].setRank(0);
                board[l][c].setFlag(0);
            }
        }
        this.nbv = 16;
        addTile();
        addTile();

        // Q54
//        board[3][3].setFlag(-1);
//        Toast.makeText(context, "init", Toast.LENGTH_SHORT).show();
    }

    public void initTest() {
        for (int l = 0; l < 4; l++) {
            for (int c = 0; c < 4; c++) {
                board[l][c].setRank(1);
                //board[l][c].setFlag(-1);
            }
        }
        board[0][1].setRank(3);
        board[0][2].setRank(0);
        board[0][2].setFlag(0);
        board[1][0].setRank(0);
        board[1][0].setFlag(0);
        board[1][3].setRank(0);
        board[1][3].setFlag(0);
        board[3][2].setRank(2);
        board[3][3].setRank(2);

        this.nbv = 16;
//        addTile();
//        addTile();
    }

    public int getScore() {
        return score;
    }

    public String getLastP() {
        return lastP;
    }

    public int getBestR() {
        return bestR;
    }

    public boolean move(boolean croiss, boolean vert) {
        String info = "";
        boolean modif = false;
        this.nbv = 0;
        for (int lc=0; lc<4; lc++) {
            final ArrayList<Integer> pile = new ArrayList<>(4);   // Q90
            int rank;
            int firstEmpty = 0;
            // ajout des rangs dans la pile par ligne ou colonne sans 0
            boolean emptyFound = false;
            for (int i=0, ip=0; i<4; i++, ip++) {
                rank = this.getTile(lc, i, croiss, vert).getRank();
                if (rank > 0) {
                    pile.add(ip,rank);
                    if (emptyFound) modif = true;
                } else {
                    ip--;
                    emptyFound = true;
                }
            }
            // Fusion des tuiles du même rang
            for (int ip=0, i=0; ip < pile.size(); ip++, i++) {
                Tile tile = this.getTile(lc, i, croiss, vert);
//                Log.i("compacting", "lc:" + String.valueOf(lc) + ".pile(" + String.valueOf(ip) + "): " + String.valueOf(pile.get(ip)));
                if (ip < pile.size()-1 && pile.get(ip) == pile.get(ip+1)){
//                    Log.i("compacting", "pile(" + String.valueOf(ip) + "): " + String.valueOf(pile.get(ip)) + " pile(" + String.valueOf(ip+1) + "): " + String.valueOf(pile.get(ip + 1)));
                    tile.setRank(1 + pile.get(ip));
                    tile.setFlag(1);
                    ip++;
                    modif = true;
                    // Q100
                    int points = tile.pow2[1 + pile.get(ip)];
                    this.score += points;
                    if (1 + pile.get(ip) > bestR) bestR = 1 + pile.get(ip);
                    String strPoints = String.valueOf(points);
                    info += info == "" ? strPoints : "+" + strPoints;
                } else {
                    tile.setRank(pile.get(ip));
                    tile.setFlag(0);
                }
                firstEmpty = i + 1;
            }
            // Met toutes les lignes ou colonnes restantes à vide
            for (int i = firstEmpty; i<4; i++) {
                Tile tile = this.getTile(lc, i, croiss, vert);
                tile.setRank(0);
                tile.setFlag(0);
                this.nbv ++;
            }
        }
        if (modif) {
            this.lastP = info;
            if (this.nbv > 0) addTile();
            this.nbv --;
        }
        return modif;
    }

    public boolean hasJustWon() {
        boolean goalPrev = this.goal;
        this.goal =  this.bestR >= 11;
        return this.goal && !goalPrev;
    }

    public boolean isOver() {
        if (nbv > 0) return false;
        boolean over = false;
        for (int l=0; l<3; l++) {
            for (int c=0; c<3; c++) {
                if (this.getTile(l, c).getRank() == this.getTile(l, c + 1).getRank()) return false;
                if (this.getTile(l, c).getRank() == this.getTile(l + 1, c).getRank()) return false;
            }
        }
        return  true;
    }

    private String logBoard() {
        String log = "";
        for (int l = 0; l < 4; l++) {
            for (int c = 0; c < 4; c++) {
                log = log.concat(getTile(l, c).log());
            }
        }
        return log;
    }

    public SaveBundle getSaveBundle() {
        SaveBundle sb = new SaveBundle();
        sb.board = this.logBoard();
        sb.lastP = this.lastP;
        sb.score = this.score;
        return sb;
    }

    public void restoreFromSaveBundle(SaveBundle sb) {
        this.nbv = 0;
        this.bestR = 0;
        this.goal = false;

        this.lastP = sb.lastP;
        this.score = sb.score;

        int index = 0;
        for (int l = 0; l < 4; l++) {
            for (int c = 0; c < 4; c++) {
                String log = sb.board.substring(index, index + 2);
                Tile tile = this.getTile(l, c);
                tile.setFromLog(log);
                if (tile.getRank() == 0)
                    this.nbv++;
                if (tile.getRank() > this.bestR)
                    this.bestR = tile.getRank();
                index += 2;
            }
        }
        this.goal = this.hasJustWon();
    }

}

