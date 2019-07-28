
/*******
 * 题目：
 * 骰子走轨迹
 *
 * 骰子起始状态为 下4，前0，上1，右2，左3，后5；
 *
 * 输入：
 * 1      测试组的数量;
 * 3 3    迷宫的大小 m n;
 * ..#    m*n 的迷宫，'#'表示墙壁不能走，'.'表示可以走的位置;
 * .#.
 * ...
 * sx sy tx ty
 *       //sx,sy 起始位置坐标   tx,ty 目标位置坐标
 * w0 w1 w2 w3 w4 w5
 *       //每个面的权重
 *
 * 路径上的权重为骰子底下的值（印在地上），求从初始位置到目标位置，最少权重；
 */


//解法：
//        5
//        1
//骰子：3  0  2    （前为 0 ，底为4）（对面数字相加为5）
//        4
//
//骰子有 24 种状态，----固定底 6 * 固定前 4（绕z轴旋转共四个方向）
//根据底部数字d 和前面数字 q 解码获得编号 0-23
//数组 getRight[x][y] 储存 底为x，前为y 对应的右边的数字  ----例如 d=4,q=0 则右边为 2    ----（getRight[4][0]=2）


public class Main {
    private static int m;
    private static int n;
    private static int[] weight;         //长度为为6  每个面对应的权重
    private static int[][] matrix;         //是否为障碍
    private static int[][] getRight;     //长度为36=6*6（实际应用24格对应骰子的24种状态  即x!=y && x+y!=5）
    private static int[][][] maxSum;     //
    private static int[] position;       //长度为4    ----[0][1] 记录 起始x 和 起始y    ----[2][3] 记录 目标x 和 目标y


    //1.initial    2.dfs   3.getOutput()/getResult()/printMaxsum()
    public static void main(String[] args) {
        initialDefault();
        dfs(position[0],position[1],4,0);
        printMaxsum();

        System.out.println();

        //7*7
        int[] inputWeight = new int[]{4,3,1,7,9,6};
        int[] inputPosition = new int[]{0,0,6,6};
        int[][] inputMatrix = new int[7][7];
        initial(7,7,inputWeight,inputPosition,inputMatrix);
        dfs(position[0],position[1],4,0);
        printMaxsum();
        System.out.println(getResult());
    }

    //---x为行数  ---y为列数  ---inputWeight是权重  ---inputPosition是起始和目标位置  ---inputMatrix是地图分布（格子是否可移动）
    private static void initial(int x,int y,int[] inputWeight,int[] inputPosition,int[][] inputMatrix){
        m=x;
        n=y;
        weight = new int[6];
        getRight = new int[6][6];
        maxSum = new int[m][n][36];
        position = new int[4];
        matrix = new int[m][n];

        weight = inputWeight;
        position = inputPosition;
        matrix = inputMatrix;

        //setting getRight[][] function
        int[][] around = new int[][]{
                {1,2,4,3},
                {0,3,5,2},
                {0,1,5,4},
                {0,4,5,1},
                {0,2,5,3},
                {1,3,4,2}
        };
        for(int i=0;i<6;i++){
            for(int j=0;j<3;j++){
                getRight[i][around[i][j]] = around[i][j+1];
            }
            getRight[i][around[i][3]] = around[i][0];
        }

        //setting default maxSum
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                for(int k=0;k<36;k++){
                    maxSum[i][j][k] = Integer.MAX_VALUE;
                }
            }
        }
        for(int k=0;k<36;k++){
            maxSum[position[0]][position[1]][k] = weight[4];
        }
    }

    //测试用的数据
    private static void initialDefault(){
        int[] inputWeight = new int[]{1,2,3,4,5};
        int[] inputPosition = new int[]{0,0,2,2};

        int[][] inputMatrix = new int[][]{
                {0,0,1},
                {0,1,0},
                {0,0,0}
        };
        initial(3,3,inputWeight,inputPosition,inputMatrix);
    }

    //模拟行走
    private static void dfs(int x,int y,int d,int q){
        if(x==position[2] && y==position[3]) return;

        //右
        if(y<n-1 && matrix[x][y+1]==0){
            int nextD = getRight[d][q];
            int nextQ = q;
            if(maxSum[x][y][d*6+q]+weight[nextD]<maxSum[x][y+1][nextD*6+nextQ]){
                maxSum[x][y+1][nextD*6+nextQ] = maxSum[x][y][d*6+q]+weight[nextD];
                dfs(x,y+1,nextD,nextQ);
            }
        }
        //左
        if(y>0 && matrix[x][y-1]==0){
            int nextD = 5-getRight[d][q];
            int nextQ = q;
            if(maxSum[x][y][d*6+q]+weight[nextD]<maxSum[x][y-1][nextD*6+nextQ]){
                maxSum[x][y-1][nextD*6+nextQ] = maxSum[x][y][d*6+q]+weight[nextD];
                dfs(x,y-1,nextD,nextQ);
            }
        }
        //上
        if(x>0 && matrix[x-1][y]==0){
            int nextD = 5-q;
            int nextQ = d;
            if(maxSum[x][y][d*6+q]+weight[nextD]<maxSum[x-1][y][nextD*6+nextQ]){
                maxSum[x-1][y][nextD*6+nextQ] = maxSum[x][y][d*6+q]+weight[nextD];
                dfs(x-1,y,nextD,nextQ);
            }
        }
        //下
        if(x<m-1 && matrix[x+1][y]==0){
            int nextD = q;
            int nextQ = 5-d;
            if(maxSum[x][y][d*6+q]+weight[nextD]<maxSum[x+1][y][nextD*6+nextQ]){
                maxSum[x+1][y][nextD*6+nextQ] = maxSum[x][y][d*6+q]+weight[nextD];
                dfs(x+1,y,nextD,nextQ);
            }
        }
    }

    //返回目标位置的最短路径消耗
    private static int getResult(){
        int[][] result = getOutput();
        return result[position[2]][position[3]];
    }

    //返回每个位置的24种状态中最小的路径消耗
    private static int[][] getOutput(){
        int[][] result = new int[m][n];
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                int min = Integer.MAX_VALUE;
                for(int k=0;k<36;k++){
                    min = Math.min(min,maxSum[i][j][k]);
                }
                result[i][j] = min;
            }
        }
        return result;
    }

    //打印
    private static void printMaxsum(){
        int[][] result = getOutput();
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                if(result[i][j]==Integer.MAX_VALUE)
                    System.out.printf("%5s","*");
                else System.out.printf("%5d",result[i][j]);
            }
            System.out.println();
        }
    }
}
