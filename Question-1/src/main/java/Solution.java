import java.util.List;


class Solution {

    /*
     * Complete the 'numberOfTokens' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     *  1. INTEGER expiryLimit
     *  2. 2D_INTEGER_ARRAY commands
     */

    public static int numberOfTokens(int expiryLimit, List<List<Integer>> commands) {
        Map<Integer, Long> tokenExpiryMap = new HashMap<>();

        long maxTime = 0;

        for (List<Integer> command : commands) {
            int commandType = command.get(0);
            int tokenId = command.get(1);
            long currentTime = command.get(2);

            maxTime = Math.max(maxTime, currentTime);

            if (commandType == 0) {
                long expiryTime = currentTime + expiryLimit;
                tokenExpiryMap.put(tokenId, expiryTime);
            } else if (commandType == 1) {
                if (tokenExpiryMap.containsKey(tokenId)) {
                    long currentExpiry = tokenExpiryMap.get(tokenId);
                    if (currentTime <= currentExpiry) {
                        long newExpiryTime = currentTime + expiryLimit;
                        tokenExpiryMap.put(tokenId, newExpiryTime);
                    }
                }
            }
        }

        int activeTokens = 0;
        for (long expiryTime : tokenExpiryMap.values()) {
            if (expiryTime >= maxTime) {
                activeTokens++;
            }
        }

        return activeTokens;
    }

}
