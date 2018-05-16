package cn.focus.dc.focussearch.util;
/**
 * LCS算法，算字符串相似度
 * @author xuemingtang
 *
 */
public class LCS {
	private char[] x; // 输入字符串1
	private char[] y; // 输入字符串2 // 辅助数组
	private int[][] c; // 以x和y数组的长度组成的二维数组，用于存放比较结果
	private String[][] b; // 用于标记LCS的成员

	public LCS(String X, String Y) {
		// 初始化数组
		x = X.toCharArray();
		y = Y.toCharArray();

		this.c = new int[x.length + 1][y.length + 1];
		this.b = new String[x.length + 1][y.length + 1];
	}

	// 计算LCS长度
	public int LCS_length() {
		for (int i = 1; i <= x.length; i++) {
			c[i][0] = 0;
		}

		for (int i = 0; i <= y.length; i++) {
			c[0][i] = 0;
		}

		// 注意：因为x和y数组下标是从0开始的，所以x和y的下标都要减一，否则会越界
		for (int i = 1; i <= x.length; i++) {
			for (int j = 1; j <= y.length; j++) {
				if (x[i - 1] == y[j - 1]) {
					c[i][j] = c[i - 1][j - 1] + 1;
					b[i][j] = "↖";
				} else {
					if (c[i - 1][j] >= c[i][j - 1]) {
						c[i][j] = c[i - 1][j];
						b[i][j] = "↑";
					} else {
						c[i][j] = c[i][j - 1];
						b[i][j] = "←";
					}
				}
			}
		}
		return c[x.length][y.length];
	}

	public String print_LCS(int i, int j) {
		if (i == 0 || j == 0) {
			return "";
		}

		// 动态规划的体现
		if (b[i][j] == "↖") {
			return print_LCS(i - 1, j - 1) + x[i - 1];
		} else if (b[i][j] == "←") {
			return print_LCS(i, j - 1);
		} else
			return print_LCS(i - 1, j);
	}
}