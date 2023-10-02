// Built OVERNIGHT by gabyah92(Instagram)
// Technical Trainer
package codeforces.leaderboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class CodeforcesLeaderboard extends javax.swing.JFrame {

    static private boolean generated;
    private JTextField contestIdField;
    private JTextField searchTokenField;
    private String excelSheetField = "";

    public CodeforcesLeaderboard() {
        setTitle("Codeforces Leaderboard App");
        setSize(600, 300);
        setIconImage(new ImageIcon("lib//logo.jpg").getImage());
        getContentPane().setBackground(Color.LIGHT_GRAY);
        setResizable(false);
        setLayout(null);

        JLabel TM = new JLabel("APP BY : gabyah92 || Pyramid ");
        TM.setFont(new Font("Arial", Font.BOLD, 12));
        TM.setFont(TM.getFont().deriveFont(Font.BOLD));
        TM.setBounds(395, 235, 250, 30);
        add(TM);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel contestIdLabel = new JLabel("Contest ID:");
        contestIdLabel.setFont(new Font("Arial", Font.BOLD, 18));
        contestIdLabel.setFont(contestIdLabel.getFont().deriveFont(Font.BOLD));
        contestIdLabel.setBounds(20, 20, 120, 30);
        add(contestIdLabel);

        contestIdField = new JTextField();
        contestIdField.setFont(new Font("Arial", Font.BOLD, 16));
        contestIdField.setFont(contestIdField.getFont().deriveFont(Font.BOLD));
        contestIdField.setBounds(210, 20, 360, 30);
        add(contestIdField);

        JLabel searchTokenLabel = new JLabel("Tokens: (Sep by ,)");
        searchTokenLabel.setFont(new Font("Arial", Font.BOLD, 18));
        searchTokenLabel.setBounds(20, 75, 190, 30);
        add(searchTokenLabel);

        searchTokenField = new JTextField();
        searchTokenField.setFont(new Font("Arial", Font.BOLD, 16));
        searchTokenField.setBounds(210, 75, 360, 30);
        add(searchTokenField);

        JLabel excelSheetLabel = new JLabel("Excel Sheet:");
        excelSheetLabel.setFont(new Font("Arial", Font.BOLD, 20));
        excelSheetLabel.setBounds(20, 130, 120, 40);
        add(excelSheetLabel);

        JButton browseButton = new JButton("Browse Excel(Rank,ID,Score)");
        browseButton.setFont(new Font("Arial", Font.BOLD, 20));
        browseButton.setBounds(210, 130, 360, 40);
        add(browseButton);

        JButton downloadButton = new JButton("Download Leaderboard");
        downloadButton.setFont(new Font("Arial", Font.BOLD, 20));
        downloadButton.setBounds(20, 190, 550, 50);
        add(downloadButton);

        browseButton.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {
                excelSheetField = fileChooser.getSelectedFile().getAbsolutePath();
            }
            System.out.print(excelSheetField);
        });

        downloadButton.addActionListener((ActionEvent e) -> {
            String searchToken = searchTokenField.getText();
            if (searchToken.replace(" ", "").equals("")) {
                JOptionPane.showMessageDialog(null, "Enter some Tokens!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Show the "Please wait" dialog
            JDialog pleaseWaitDialog = new JDialog();
            pleaseWaitDialog.setPreferredSize(new Dimension(200, 100));
            pleaseWaitDialog.getContentPane().setBackground(Color.gray);
            pleaseWaitDialog.setTitle("Retrieving Data");
            pleaseWaitDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            pleaseWaitDialog.setLocationRelativeTo(null);
            pleaseWaitDialog.setResizable(false);
            pleaseWaitDialog.setIconImage(new ImageIcon("lib//logo.jpg").getImage());

            pleaseWaitDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    pleaseWaitDialog.dispose(); // Close the dialog
                    CodeforcesLeaderboard.this.setEnabled(true);
                    CodeforcesLeaderboard.this.setVisible(true);
                }
            });

            JLabel label = new JLabel("Please wait...");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setSize(new Dimension(200, 100));
            label.setForeground(Color.white);
            label.setFont(new Font("Arial", Font.BOLD, 20));
            pleaseWaitDialog.add(label);
            // Start the other functions in a separate thread
            Thread thread = new Thread(() -> {
                String contestId = contestIdField.getText();
                List<Participant> curr_leaderboard = null;
                for (String contestID : contestId.replace(" ", "").split(",")) {
                    if (contestID.replace(" ", "").equals("")) {
                        JOptionPane.showMessageDialog(null, "contestId is Empty!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Download leaderboard and filter the results
                    try {
                        List<Participant> leaderboard = downloadLeaderboard(contestID, searchToken);
                        if (curr_leaderboard == null) {
                            curr_leaderboard = filterLeaderboard(leaderboard);
                        } else {
                            curr_leaderboard = mergeLeaderboards(filterLeaderboard(leaderboard), curr_leaderboard);
                        }
                    } catch (Exception p) {
                        JOptionPane.showMessageDialog(null, "InvalidSearchToken", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                // Load previous excel sheet if provided
                String excelSheetPath = excelSheetField;

                if (!excelSheetPath.equals("")) {
                    List<Participant> previousParticipants = loadExcelSheet(excelSheetPath);
                    curr_leaderboard = mergeLeaderboards(curr_leaderboard, previousParticipants);
                }

                // Sort and assign ranks
                sortLeaderboard(curr_leaderboard);
                assignRanks(curr_leaderboard);

                // Display the leaderboard in console 
                exportParticipantsToExcel((ArrayList<Participant>) curr_leaderboard);

                // Close the "Please wait" dialog
                pleaseWaitDialog.dispose();

                // Enable access to the previous window
                CodeforcesLeaderboard.this.setEnabled(true);
                CodeforcesLeaderboard.this.setVisible(true);

                generated = false;
            });
            thread.start();

            // Disable access to the previous window
            CodeforcesLeaderboard.this.setEnabled(false);

            // Show the "Please wait" dialog
            pleaseWaitDialog.pack();
            pleaseWaitDialog.setVisible(true);
        });

        //pack();
        //
        setLocationRelativeTo(null);
    }

    static void exportParticipantsToExcel(ArrayList<Participant> participants) {
        try {
            // Create a new Workbook
            XSSFWorkbook workbook = new XSSFWorkbook();

            // Create a new Sheet
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Current Codeforces Leaderboard");

            // Create bold font with size 18 for column headers
            org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldFont.setFontHeightInPoints((short) 20);

            org.apache.poi.ss.usermodel.Font boldFont2 = workbook.createFont();
            boldFont2.setBold(true);
            boldFont2.setFontHeightInPoints((short) 14);

            // Create bold centered cell style with 14 font size for normal cells
            CellStyle boldCenteredCellStyle = workbook.createCellStyle();
            boldCenteredCellStyle.setAlignment(HorizontalAlignment.CENTER);
            boldCenteredCellStyle.setFont(boldFont);
            boldCenteredCellStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE1.getIndex());
            boldCenteredCellStyle.setBorderBottom(BorderStyle.THICK);
            boldCenteredCellStyle.setBorderTop(BorderStyle.THICK);
            boldCenteredCellStyle.setBorderLeft(BorderStyle.THICK);
            boldCenteredCellStyle.setBorderRight(BorderStyle.THICK);
            boldCenteredCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Create bold cell style with 14 font size for normal cells
            CellStyle boldCellStyle = workbook.createCellStyle();
            boldCellStyle.setAlignment(HorizontalAlignment.CENTER);
            boldCellStyle.setFont(boldFont2);
            boldCellStyle.setFillForegroundColor(IndexedColors.TURQUOISE.getIndex());
            boldCellStyle.setBorderBottom(BorderStyle.THICK);
            boldCellStyle.setBorderTop(BorderStyle.THICK);
            boldCellStyle.setBorderLeft(BorderStyle.THICK);
            boldCellStyle.setBorderRight(BorderStyle.THICK);
            boldCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Add column headers
            Row headerRow = sheet.createRow(0);
            Cell rankHeaderCell = headerRow.createCell(0);
            rankHeaderCell.setCellValue("Rank");
            rankHeaderCell.setCellStyle(boldCenteredCellStyle);
            Cell codeforcesIdHeaderCell = headerRow.createCell(1);
            codeforcesIdHeaderCell.setCellValue("Codeforces_ID");
            codeforcesIdHeaderCell.setCellStyle(boldCenteredCellStyle);
            Cell scoreHeaderCell = headerRow.createCell(2);
            scoreHeaderCell.setCellValue("Score");
            scoreHeaderCell.setCellStyle(boldCenteredCellStyle);

            // Add participants' data
            for (int i = 0; i < participants.size(); i++) {
                Participant participant = participants.get(i);
                Row row = sheet.createRow(i + 1);
                Cell rankCell = row.createCell(0);
                rankCell.setCellValue(participant.getRank());
                rankCell.setCellStyle(boldCellStyle);
                Cell codeforcesIdCell = row.createCell(1);
                codeforcesIdCell.setCellValue(participant.getCodeforcesId());
                codeforcesIdCell.setCellStyle(boldCellStyle);
                Cell scoreCell = row.createCell(2);
                scoreCell.setCellValue(participant.getScore());
                scoreCell.setCellStyle(boldCellStyle);
            }

            File folder = new File("Leaderboards");
            if (!folder.exists()) {
                folder.mkdir();
            }

            // Resize columns to fit the content
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            // Create FileOutputStream to write to the file
            try ( FileOutputStream fileOut = new FileOutputStream("Leaderboards/CurrentCodeforcesLeaderboard.xlsx")) {
                // Write the workbook to the output stream  
                if (generated == true) {
                    workbook.write(fileOut);
                    System.out.println("Excel file created successfully!");
                    JOptionPane.showMessageDialog(null, "Generated! ", "Finished Generating Leaderboard!", JOptionPane.INFORMATION_MESSAGE);
                }
                // Close the workbook
                generated = false;
                workbook.close();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Something Went Wrong! ", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(null, "Something Went Wrong!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Participant> downloadLeaderboard(String contestId, String searchToken) throws Exception {
        String url = "https://codeforces.com/api/contest.standings?contestId=" + contestId + "&showUnofficial=false";
        JSONArray rows = null;
        try {
            URL websiteUrl = new URL(url);
            URLConnection connection = new URL(url).openConnection();
            HttpURLConnection o = (HttpURLConnection) websiteUrl.openConnection();
            o.setRequestMethod("GET");
            if (o.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND || o.getResponseCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
                JOptionPane.showMessageDialog(null, "ContestID Does Not Exist!", "Error", JOptionPane.ERROR_MESSAGE);
                return new ArrayList<>();
            }
            InputStream inputStream = connection.getInputStream();
            try ( BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                StringBuilder jsonContent = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    jsonContent.append(line);
                }
                JSONObject jsonObject = new JSONObject(jsonContent.toString());
                String status = jsonObject.getString("status");
                if (status.equals("OK")) {
                    rows = jsonObject.getJSONObject("result").getJSONArray("rows");
                }
            }
        } catch (HeadlessException | IOException | NumberFormatException | JSONException e) {
            JOptionPane.showMessageDialog(null, "No Internet OR Invalid Contest ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
        JSONArray standings = rows;
        List<Participant> handlePointsList = new ArrayList<>();
        try {
            int len;
            len = standings != null ? standings.length() : 0;
            String splitter[] = searchToken.replace(" ", "").split(",");
            if (splitter.length == 0 && splitter[0].equals("")) {
                throw new Exception("WTH");
            }
            if (standings != null) {
                for (int i = 0; i < len; i++) {
                    JSONObject row = standings.getJSONObject(i);
                    JSONObject party = row.getJSONObject("party");
                    JSONArray members = party.getJSONArray("members");
                    for (int j = 0; j < members.length(); j++) {
                        JSONObject member = members.getJSONObject(j);
                        String handle = member.getString("handle");
                        int points = row.getInt("points");
                        for (String chk : splitter) {
                            if (handle.toLowerCase().contains(chk.toLowerCase())) {
                                handlePointsList.add(new Participant(handle, points));
                                break;
                            }
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "InvalidContestNumber, No Participants!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (JSONException e) {
            JOptionPane.showMessageDialog(null, "Invalid Excel Sheet Format! There be Rank, Id and Score Columns!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        generated = true;
        return handlePointsList;
    }

    private List<Participant> filterLeaderboard(List<Participant> leaderboard) {
        List<Participant> arr = new ArrayList<>();
        HashSet<String> hs = new HashSet<>();
        for (Participant k : leaderboard) {
            if (!hs.contains(k.codeforcesId.toLowerCase())) {
                Participant y = k;
                if (y.score <= 9) {
                    y.score *= 1000;
                }
                arr.add(y);
                hs.add(k.codeforcesId.toLowerCase());
            }
        }
        return arr; // Placeholder
    }

    private List<Participant> loadExcelSheet(String excelSheetPath) {
        List<Participant> participants = new ArrayList<>();

        try {
            try ( FileInputStream excelFile = new FileInputStream(excelSheetPath);  Workbook workbook = WorkbookFactory.create(excelFile)) {
                // Assuming the data is in the first sheet (index 0)
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

                // Assuming 'codeforces_id' is in column A (index 0) and 'score' is in column B (index 1)
                Iterator<Row> rowIterator = sheet.iterator();
                int idInd = 1;
                int scoreInd = 2;
                int codeforcesIdColumnIndex = idInd;
                int scoreColumnIndex = scoreInd;

                if ((idInd == -1 || scoreInd == -1) || sheet.getRow(0).getCell(codeforcesIdColumnIndex) == null
                        || sheet.getRow(0).getCell(scoreColumnIndex) == null) {
                    JOptionPane.showMessageDialog(null, "Source Excel Sheet must have Codeforces_Id and Score Column!", "Error", JOptionPane.ERROR_MESSAGE);
                    return new ArrayList<>();
                }
                
                if (rowIterator.hasNext()) {
                    rowIterator.next();
                }
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();

                    Cell codeforcesIdCell = row.getCell(codeforcesIdColumnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell scoreCell = row.getCell(scoreColumnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                    if (codeforcesIdCell != null && scoreCell != null) {
                        // Assuming codeforces_id is a String and score is a numeric value
                        String codeforcesId = codeforcesIdCell.getStringCellValue();
                        int score = (int) scoreCell.getNumericCellValue();

                        Participant participant = new Participant(codeforcesId, score);
                        participants.add(participant);
                    }
                }

            }
        } catch (HeadlessException | IOException e) {
            JOptionPane.showMessageDialog(null, "Source Excel Sheet must have Codeforces_Id and Score Columns!", "Error", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }

        return participants;
    }

    private List<Participant> mergeLeaderboards(List<Participant> currentLeaderboard, List<Participant> previousLeaderboard) {
        // Code to merge the current and previous leaderboards
        // ... 
        List<Participant> mergedLeaderboard = new ArrayList<>();

        // Add all participants from current leaderboard to merged leaderboard
        mergedLeaderboard.addAll(currentLeaderboard);

        // Loop through participants in previous leaderboard
        for (Participant previousParticipant : previousLeaderboard) {
            boolean found = false;

            // Loop through participants in current leaderboard
            for (Participant currentParticipant : currentLeaderboard) {
                // If codeforces_Id matches, add scores and mark as found
                if (currentParticipant.getCodeforcesId().equals(previousParticipant.getCodeforcesId())) {
                    currentParticipant.setScore(currentParticipant.getScore() + previousParticipant.getScore());
                    found = true;
                    break;
                }
            }

            // If participant not found in current leaderboard, add to merged leaderboard with score as 0
            if (!found) {
                Participant newParticipant = new Participant(previousParticipant.getCodeforcesId(), previousParticipant.getScore());
                mergedLeaderboard.add(newParticipant);
            }
        }

        return mergedLeaderboard;
    }

    private void sortLeaderboard(List<Participant> leaderboard) {
        try {
            Collections.sort(leaderboard, (Participant p1, Participant p2) -> Integer.compare(p2.getScore(), p1.getScore()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid Input/Contest_ID Does Not Exist!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignRanks(List<Participant> leaderboard) {
        try {
            for (int i = 0; i < leaderboard.size(); i++) {
                leaderboard.get(i).setRank(i + 1);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid Input/Contest_ID Does Not Exist!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class Participant {

        private String codeforcesId;
        private int score;
        private int rank;

        public Participant(String codeforcesId, int score) {
            this.codeforcesId = codeforcesId;
            this.score = score;
            this.rank = 0;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getCodeforcesId() {
            return codeforcesId;
        }

        public int getScore() {
            return score;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }
    }

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeLater(() -> {
                new CodeforcesLeaderboard().setVisible(true);
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
