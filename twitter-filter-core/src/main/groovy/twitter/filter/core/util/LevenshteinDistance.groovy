package twitter.filter.core.util

/**
 * http://en.wikipedia.org/wiki/Levenshtein_distance
 *
 * @author bartek
 */
class LevenshteinDistance {
    static int computeDistance(String a, String b) {
        int m = a.length()
        int n = b.length()

        def d = new int[m+1][n+1]
        for (int i = 0; i <= m; i++) {
            d[i][0] = i
        }
        for (int j = 0; j <= n; j++) {
            d[0][j] = j
        }

        for (int j = 1; j <= n; j++) {
            for (int i = 1; i <= m; i++) {
                if (a[i-1] == b[j-1]) {
                    d[i][j] = d[i-1][j-1]
                } else {
                    d[i][j] = Math.min(d[i-1][j] + 1, Math.min(d[i][j-1] + 1, d[i-1][j-1] + 1))
                }
            }
        }

        d[m][n]
    }
}
