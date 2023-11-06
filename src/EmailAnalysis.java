import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailAnalysis {
    private static final String LOG_FILE = "email_analysis.log";
    private static final String INPUT_FILE = "emails.txt";
    private static final String OUTPUT_FILE = "e-mail_analysis.txt";
    private static final String SUMMARY_FILE = "summary.txt";

    private static final String LOG_FORMAT = "[%1$tF %1$tT] [%2$s] %3$s%n";
    private static final String EMAIL_REGEX = "\\[(.*?)\\] \\[(.*?)\\] \\[(.*?)\\] \\[(.*?)\\]";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        Logger logger = setupLogger();

        try {
            Map<String, Integer> domainCounts = new HashMap<>();
            int totalEmails = 0;

            BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                Matcher matcher = Pattern.compile(EMAIL_REGEX).matcher(line);
                if (matcher.matches()) {
                    String sender = matcher.group(2);
                    String receiver = matcher.group(3);

                    String senderDomain = extractDomain(sender);
                    String receiverDomain = extractDomain(receiver);

                    incrementDomainCount(domainCounts, senderDomain);
                    incrementDomainCount(domainCounts, receiverDomain);

                    totalEmails++;
                }
            }
            reader.close();

            writeAnalysisResults(totalEmails, domainCounts);
            writeSummary(totalEmails, domainCounts);
            logger.info("Email analysis completed successfully.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred during email analysis.", e);
        }
    }

    private static Logger setupLogger() {
        Logger logger = Logger.getLogger(EmailAnalysis.class.getName());
        logger.setLevel(Level.ALL);

        try {
            FileHandler fileHandler = new FileHandler(LOG_FILE, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to set up logger.", e);
        }

        return logger;
    }

    private static String extractDomain(String email) {
        int atIndex = email.indexOf('@');
        return email.substring(atIndex + 1);
    }

    private static void incrementDomainCount(Map<String, Integer> domainCounts, String domain) {
        domainCounts.put(domain, domainCounts.getOrDefault(domain, 0) + 1);
    }

    private static void writeAnalysisResults(int totalEmails, Map<String, Integer> domainCounts) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE));

        writer.write("Email Analysis Results:");
        writer.newLine();
        writer.write("Total Emails: " + totalEmails);
        writer.newLine();
        writer.newLine();
        writer.write("Domain Statistics:");
        writer.newLine();

        for (Map.Entry<String, Integer> entry : domainCounts.entrySet()) {
            writer.write(entry.getKey() + ": " + entry.getValue());
            writer.newLine();
        }

        writer.close();
    }

    private static void writeSummary(int totalEmails, Map<String, Integer> domainCounts) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(SUMMARY_FILE));

        writer.write("Email Analysis Summary");
        writer.newLine();
        writer.newLine();
        writer.write("Date and Time: " + LocalDateTime.now().format(DATE_TIME_FORMATTER));
        writer.newLine();
        writer.write("Total Emails: " + totalEmails);
        writer.newLine();
        writer.newLine();
        writer.write("Domain Statistics:");
        writer.newLine();

        for (Map.Entry<String, Integer> entry : domainCounts.entrySet()) {
            writer.write(entry.getKey() + ": " + entry.getValue());
            writer.newLine();
        }

        writer.close();
    }
}