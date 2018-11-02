import java.util.*;

import static java.lang.Math.min;

public class Main {
    static Board b;

    public static void go() {
        Agent.startTime = System.currentTimeMillis();
        Agent.maxTime = 1000;
        Agent.cutOff = false;
        int mx = 10;
        int[][] incpy = Board.cloneGrid(b.gamestate);
        for (int i = 1; i <= mx && !Agent.cutOff; i++) {

            Agent.maxLevel = i;
            Agent.minimax(true, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, new Board(incpy));

            if (!Agent.cutOff) {
                for (int j = 0; j < 8; j++) {
                    for (int k = 0; k < 8; k++) {
                        if (Agent.best.gamestate[j][k] == Agent.player && b.gamestate[j][k] == 0) {
                            System.out.println(j + " " + k);
                            return;
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Agent.player = 1;
        b = new Board(new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 2, 0, 0, 0},
                {0, 0, 0, 2, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0}
        });
        go();
    }
}


class Agent {

    public static int maxLevel = 10;
    public static long maxTime = 800;
    public static long startTime = 0;
    public static boolean cutOff = false;
    public static int player;

    public static Board best;


    public static int minimax(boolean maximizingPlayer, int level, int alpha, int beta, Board b) {
        if (System.currentTimeMillis() - startTime >= maxTime) {
            cutOff = true;
            return b.calculateValueDiff();
        } else if (level > maxLevel) {
            return b.calculateValueDiff();
        }
        ArrayList<Board> moves = b.getValidMoves(maximizingPlayer ? player : 3 - player);
        if (moves.size() == 0) {
            return maximizingPlayer ? 64 : -64;
        }
        if (maximizingPlayer) {

            int top = 0;

            for (int i = 0; i < moves.size(); i++) {
                int score = minimax(false, level + 1, alpha, beta, moves.get(i));

                if (score > alpha) {
                    alpha = score;
                    top = i;
                }

                if (alpha >= beta)
                    break;
            }
            if (level == 0) {
                best = moves.get(top);
            }
            return alpha;
        } else {
            for (Board i : moves) {
                int score = minimax(true, level + 1, alpha, beta, i);

                if (score < beta)
                    beta = score;

                if (alpha >= beta)
                    break;
            }
            return beta;
        }

    }

}

class Board {

    public int[][] gamestate;

    public int movedX, movedY;

    public Board(int[][] gamestate) {

        this.gamestate = gamestate;

    }

    public boolean makeMove(int X, int Y, int player) {

        if (gamestate[X][Y] != 0) // spot already occupied
            return false;

        boolean legalAtLeastOnce = false;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0)
                    continue;

                boolean piecesToFlip = false, passedOpponent = false;
                int k = 1;

                while (X + j * k >= 0 && X + j * k < 8
                        && Y + i * k >= 0 && Y + i * k < 8) { // Stay inside Board

                    if (gamestate[X + j * k][Y + i * k] == 0 ||
                            (gamestate[X + j * k][Y + i * k] == player && !passedOpponent)) {
                        break;
                    }
                    if (gamestate[X + j * k][Y + i * k] == player && passedOpponent) {
                        piecesToFlip = true;
                        break;
                    } else if (gamestate[X + j * k][Y + i * k] == player % 2 + 1) {
                        passedOpponent = true;
                        k++;
                    }
                }

                if (piecesToFlip) {

                    gamestate[X][Y] = player;

                    for (int h = 1; h <= k; h++) {
                        gamestate[X + j * h][Y + i * h] = player;
                    }

                    legalAtLeastOnce = true;
                }
            }
        }

        this.movedX = X;
        this.movedY = Y;

        return legalAtLeastOnce;
    }


    public int calculateValueDiff() {
        return calculateValue(1) - calculateValue(2);
    }

    public int calculateValue(int player) {
        int v = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (gamestate[j][i] == player) {
                    v++;
                }
            }
        }
        return v;
    }

    public ArrayList<Board> getValidMoves(int player) {
        ArrayList<Board> BoardList = new ArrayList<Board>();
        Board b = new Board(cloneGrid(gamestate));
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                if (b.makeMove(j, i, player)) {
                    BoardList.add(b);
                    b = new Board(cloneGrid(gamestate));
                }
            }
        }

        return BoardList;
    }

    public static int[][] cloneGrid(int[][] gamestate) {
        int[][] r = new int[8][];
        for (int i = 0; i < 8; i++) {
            r[i] = gamestate[i].clone();
        }
        return r;
    }
}
