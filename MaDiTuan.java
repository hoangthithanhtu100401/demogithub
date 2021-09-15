import java.util.Scanner;

public class MaDiTuan {
    private static int N;
    private static boolean[][] DaDiQua = new boolean[100][100];
    private static int[][] DuongDi = new int[100][100];
    private static int[] X = { -2, -2, -1, -1, 1, 1, 2, 2 };
    private static int[] Y = { -1, 1, -2, 2, -2, 2, -1, 1 };
    private static int BuocDi = 0;
    // private static int x_first , y_first;

    public static void Print() {
        System.out.println("Cach khac: ");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(DuongDi[i][j] + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void Try(int x, int y) {
        ++BuocDi;
        DaDiQua[x][y] = true;
        DuongDi[x][y] = BuocDi;
        // System.out.println("+++++++++");
        for (int i = 0; i < X.length; i++) {
            if (BuocDi == N * N) {
                // System.out.println(BuocDi);
                Print();
            }

            // Neu chua di het
            int x_new = x + X[i];
            int y_new = y + Y[i];

            if (x_new >= 0 && x_new < N && y_new >= 0 && y_new < N && DaDiQua[x_new][y_new] == false) {
                // System.out.println(x_new + " . " + y_new);
                Try(x_new, y_new);

            }

        }
        --BuocDi;
        DaDiQua[x][y] = false;
        DuongDi[x][y] = 0;
        // break;
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Nhap do dai cua ban co  N*N");
        N = input.nextInt();

        System.out.println("Nhap toa do x ban dau: ");
        int x = input.nextInt();
        // x_first = x;

        System.out.println("Nhap toa do y ban dau: ");
        int y = input.nextInt();
        // y_first = y;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                DaDiQua[i][j] = false;
            }
        }

        Try(x, y);

    }

}