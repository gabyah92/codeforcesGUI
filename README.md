### CODEFORCES LEADERBOARD APP

Hello everyone!

I have created this project overnight to scrape results from Codeforces Rounds for my batch in **CMRIT** and through my consultancy **Pyramid**.

The first box contains an input for Codeforces round numbers. You can enter one or multiple round numbers separated by commas.

The second box is optional, but if another Excel sheet is included, it will merge all user handles within the first box and the second one, generating a combined leaderboard.

The Excel sheet will be generated in the same directory as the jar file.

Since there can be rounds where scores are different, like the codeTon Competition, where each question can have varied marks, I have used the following mechanism to remove that disparity where(solving 1 problem is 1 point, but solving a question in a different competition might be worth 300 marks).

- **1 problem solved in a Codeforces round is equal to 1000 marks.**
- **1 problem solved in a contest where scores can vary is taken as is.**

Please let me know if you have any questions or need further clarification.

Leave a star if you liked it. Follow me on Instagram : **[gabyah92](instagram.com/gabyah92)**

USAGE : 
- Download the latest release jar file **[here](https://github.com/gabyah92/codeforcesGUI/releases)**. 
- Download and install Java **[here](https://www.java.com/en/download)**.
- Run the jar file.
