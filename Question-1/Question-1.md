# Question 1: Authentication Tokens

Please implement the function [Solution.numberOfTokens](src/main/java/Solution.java) function to solve following problem.

When users are authenticated, they are given an authentication token. Unless a token is reset, it expires after a system-wide expiryLimit. If a reset request is received at or before the expiry time, the expiry time is reset to expiryLimit minutes from the time of reset.

 1. An unexpired token_id can be reset any number of times.
 2. A reset issued to a non-existent or an expired token_id is ignored.
 3. Once a token_id expires it cannot be reused.

Command syntax: [type, token_id, T]

* Create command:  Type 0 generates a token with id token_id at time T.  Its expiry is set to T + expiryLimit.
* Reset command: Type 1 resets the expiry to T + expiryLimit.

Start with an empty list of tokens. Perform a sequence of requests sorted ascending by their T parameter. Find the number of tokens that are active after all commands have been executed, at the maximum T of all requests.

## Example

expiryLimit = 4

commands = [[0,1,1], [0,2,2], [1,1,5],[1,2,7]]

The maximum time T = 7, so the analysis will end at T = 7.  Each time a token is created or reset, its new expiration time will be at time T + 4.
Working through the commands:

1. [0,1,1]: Create token_id = 1 at time T = 1 and set its expiry to T + expiryLimit = 5.
2. [0,2,2]: Create token_id = 2 with an expiry at T = 6
3. [1,1,5]: Reset token_id = 1 at T = 5. The time T is less than or equal to the expiry time fo token 1 so a new limit is set: 5 + 4 = 9.
4. [1,2,7]: Reset token_id = 2 at T = 7. The token 2 expires at time 6, so when the Reset token_id = 2 command comes in at T = 7, it is ignored.

Only token_id = 1 is active at time T = 7. Return 1.

## Function Description

Complete the numberOfTokens function in the editor below.

numberOfTokens has the following parameter(s):

* int expiryLimit: the expiry limit of each token
* int commands[n][3]: each commands[i], has 3 integers: [command, token_id, T].

return value:

* int: the number of tokens that still active at the end of the command stream

## Constraints

* The commands array is given sorted ascending by T (commands[i][2]).
* 1 ≤ expiryLimit < 10<sup>8</sup>
* 1 ≤ n < 10<sup>5</sup>
* 1 ≤ token_id < 10<sup>8</sup>
* 1 ≤ T < 10<sup>8</sup>

## Example Case 1

Input file for Example Case 1 is under resources folder: [`example_input.txt`](src/main/resources/example_input.txt)

```bash
3      →  expiryLimit = 3
2      →  commands[] size n = 2
3      →  commands[i][] size = 3 (always)
0 1 1  →  commands = [[0, 1, 1], [1, 1, 5]]
1 1 5

```

### Output for Example Case 1

```bash
0
```

### Explanation for Example Case 1

The token with id 1 is created at time 1. It expires at time 4. The reset command at time 5 is ignored since the token has expired. There are no tokens left.

## Example Case 2

Sample Input For Case 2

```bash
3
3
3
0 1 1
1 1 4
1 2 5
```

### Output for Example Case 2

```bash
1
```

### Explanation for Example Case 2

One token 1 is created at time 1. It is supposed to expire at time T = 4, but is reset at T = 4. Its next expiry is at T = 7. Since token 2 does not exist, the reset command 1 2 5 does not have any effect. There is one token at the end when T = 5.
