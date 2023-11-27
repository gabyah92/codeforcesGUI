### CODEFORCES LEADERBOARD APP
![Image](https://user-images.githubusercontent.com/22296232/268352019-66b656df-4fc4-4194-81ac-c66a55941903.png)
Hello everyone!

I have created this project overnight to scrape results from Codeforces Rounds for my batch in **CMRIT** and through my consultancy **Pyramid**.

The first input box contains an input for Codeforces round numbers. You can enter one or multiple round numbers separated by commas.

The second input box accepts tokens, which can be used to scrape results by multiple tokens. Useful when scraping results for a set of people having the same token name in their handle.

The third box is optional, but if another Excel sheet is included, it will merge all user handles within the first box and the third one, generating a combined leaderboard.

The Excel sheet will be generated in the same directory as the jar file.

Since there can be rounds where scores are different, like the codeTon Competition, where each question can have varied marks, I have used the following mechanism to remove that disparity where(solving 1 problem is 1 point, but solving a question in a different competition might be worth 300 marks).

- **1 problem solved in a Codeforces round is equal to 1000 marks.**
- **1 problem solved in a contest where scores can vary is taken as is.**

Please let me know if you have any questions or need further clarification.

Leave a star if you liked it. Follow me on Instagram : **[gabyah92](instagram.com/gabyah92)**

USAGE : 
- Download and install Java **[here](https://www.oracle.com/java/technologies/downloads/#jdk21-windows)**.
- Download the latest release zip file and extract **[here](https://github.com/gabyah92/codeforcesGUI/releases)**. 
- Run the Codeforces_Leaderboard.jar file in extracted folder.
