package com.internetbanking.common.email;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailTemplates {

    private static final String BANK_NAME = "NovaPay";
    private static final String SUPPORT_EMAIL = "anirudhajoshi003@gmail.com";
    private static final String PRIMARY_COLOR = "#1a3c5e";
    private static final String SUCCESS_COLOR = "#28a745";
    private static final String DANGER_COLOR = "#dc3545";
    private static final String WARNING_COLOR = "#ffc107";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    // ─────────────────────────────────────────────
    // BASE TEMPLATE
    // ─────────────────────────────────────────────

    private static String wrap(String title, String content) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                <title>%s</title>
            </head>
            <body style="margin:0;padding:0;background-color:#f4f6f9;font-family:Arial,sans-serif;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f6f9;padding:30px 0;">
                    <tr>
                        <td align="center">
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff;border-radius:8px;overflow:hidden;box-shadow:0 2px 10px rgba(0,0,0,0.1);">

                                <!-- HEADER -->
                                <tr>
                                    <td style="background-color:%s;padding:30px 40px;text-align:center;">
                                        <h1 style="color:#ffffff;margin:0;font-size:26px;letter-spacing:1px;">%s</h1>
                                        <p style="color:#a8c4e0;margin:5px 0 0;font-size:13px;">Secure. Simple. Reliable.</p>
                                    </td>
                                </tr>

                                <!-- CONTENT -->
                                <tr>
                                    <td style="padding:40px;">
                                        %s
                                    </td>
                                </tr>

                                <!-- FOOTER -->
                                <tr>
                                    <td style="background-color:#f4f6f9;padding:25px 40px;text-align:center;border-top:1px solid #e0e0e0;">
                                        <p style="margin:0;font-size:12px;color:#888;">This is an automated email from %s. Please do not reply.</p>
                                        <p style="margin:5px 0 0;font-size:12px;color:#888;">Need help? Contact us at <a href="mailto:%s" style="color:%s;">%s</a></p>
                                        <p style="margin:10px 0 0;font-size:11px;color:#aaa;">© 2026 %s. All rights reserved.</p>
                                    </td>
                                </tr>

                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(title, PRIMARY_COLOR, BANK_NAME, content, BANK_NAME, SUPPORT_EMAIL, PRIMARY_COLOR, SUPPORT_EMAIL, BANK_NAME);
    }

    private static String infoRow(String label, String value) {
        return """
            <tr>
                <td style="padding:10px 0;border-bottom:1px solid #f0f0f0;">
                    <span style="color:#888;font-size:13px;">%s</span>
                </td>
                <td style="padding:10px 0;border-bottom:1px solid #f0f0f0;text-align:right;">
                    <strong style="color:#333;font-size:13px;">%s</strong>
                </td>
            </tr>
            """.formatted(label, value);
    }

    private static String infoTable(String... rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin:20px 0;\">");
        for (String row : rows) sb.append(row);
        sb.append("</table>");
        return sb.toString();
    }

    private static String greeting(String firstName) {
        return "<p style=\"color:#333;font-size:15px;\">Dear <strong>%s</strong>,</p>".formatted(firstName);
    }

    private static String badge(String text, String color) {
        return "<span style=\"background-color:%s;color:#fff;padding:4px 12px;border-radius:20px;font-size:12px;font-weight:bold;\">%s</span>"
                .formatted(color, text);
    }

    private static String alertBox(String message, String color) {
        return """
            <div style="background-color:%s15;border-left:4px solid %s;padding:15px 20px;border-radius:4px;margin:20px 0;">
                <p style="margin:0;color:#333;font-size:14px;">%s</p>
            </div>
            """.formatted(color, color, message);
    }

    // ─────────────────────────────────────────────
    // 1. WELCOME / USER REGISTRATION
    // ─────────────────────────────────────────────

    public static String[] userRegistration(String firstName) {
        String subject = BANK_NAME + " — Welcome! Your account is ready";

        String content = greeting(firstName) + """
            <h2 style="color:%s;margin:20px 0 10px;">Welcome to %s! 🎉</h2>
            <p style="color:#555;font-size:14px;line-height:1.6;">
                Your profile has been successfully created. You now have access to all of our banking services.
            </p>
            %s
            <p style="color:#555;font-size:14px;line-height:1.6;">
                Here's what you can do next:
            </p>
            <ul style="color:#555;font-size:14px;line-height:2;">
                <li>Create a bank account (Savings or Current)</li>
                <li>Apply for a Debit or Credit card</li>
                <li>Transfer funds securely</li>
                <li>Track your transactions in real-time</li>
            </ul>
            <p style="color:#888;font-size:13px;margin-top:30px;">
                If you did not create this account, please contact us immediately at
                <a href="mailto:%s" style="color:%s;">%s</a>
            </p>
            """.formatted(
                PRIMARY_COLOR, BANK_NAME,
                alertBox("Your account is active and secured. Always keep your credentials confidential.", SUCCESS_COLOR),
                SUPPORT_EMAIL, PRIMARY_COLOR, SUPPORT_EMAIL
        );

        return new String[]{subject, wrap("Welcome to " + BANK_NAME, content)};
    }

    // ─────────────────────────────────────────────
    // 2. ACCOUNT CREATION
    // ─────────────────────────────────────────────

    public static String[] accountCreation(String firstName, String accountNumber,
                                            String accountType, String currency, String initialDeposit) {
        String subject = BANK_NAME + " — New " + accountType + " Account Created";

        String content = greeting(firstName) + """
            <h2 style="color:%s;margin:20px 0 10px;">Account Created Successfully</h2>
            <p style="color:#555;font-size:14px;line-height:1.6;">
                Your new bank account has been created and is ready to use.
            </p>
            %s
            <p style="color:#555;font-size:13px;margin-top:20px;">
                ⚠️ Please save your account number for future reference. You will need it for transfers and other transactions.
            </p>
            """.formatted(
                PRIMARY_COLOR,
                infoTable(
                    infoRow("Account Number", accountNumber),
                    infoRow("Account Type", accountType),
                    infoRow("Currency", currency),
                    infoRow("Status", "✅ Active"),
                    infoRow("Opening Balance", currency + " " + initialDeposit)
                )
        );

        return new String[]{subject, wrap("Account Created", content)};
    }

    // ─────────────────────────────────────────────
    // 3. ACCOUNT STATUS UPDATE
    // ─────────────────────────────────────────────

    public static String[] accountStatusUpdate(String firstName, String accountNumber,
                                                String oldStatus, String newStatus) {
        String color = newStatus.equalsIgnoreCase("ACTIVE") ? SUCCESS_COLOR :
                       newStatus.equalsIgnoreCase("SUSPENDED") ? WARNING_COLOR : DANGER_COLOR;

        String subject = BANK_NAME + " — Account Status Updated to " + newStatus;

        String content = greeting(firstName) + """
            <h2 style="color:%s;margin:20px 0 10px;">Account Status Changed</h2>
            <p style="color:#555;font-size:14px;line-height:1.6;">
                The status of your bank account has been updated.
            </p>
            %s
            %s
            """.formatted(
                PRIMARY_COLOR,
                infoTable(
                    infoRow("Account Number", accountNumber),
                    infoRow("Previous Status", oldStatus),
                    infoRow("New Status", newStatus),
                    infoRow("Updated On", LocalDateTime.now().format(DATETIME_FORMAT))
                ),
                alertBox(
                    newStatus.equalsIgnoreCase("SUSPENDED")
                        ? "Your account has been suspended. You will not be able to make transactions. Please contact support."
                        : newStatus.equalsIgnoreCase("CLOSED")
                        ? "Your account has been closed. If this was not requested by you, contact support immediately."
                        : "Your account is now active. You can resume all banking activities.",
                    color
                )
        );

        return new String[]{subject, wrap("Account Status Update", content)};
    }

    // ─────────────────────────────────────────────
    // 4. TRANSFER — SENDER
    // ─────────────────────────────────────────────

    public static String[] transferSent(String firstName, String fromAccount, String toAccount,
                                         BigDecimal amount, BigDecimal balanceAfter,
                                         String transactionRef, String description) {
        String subject = BANK_NAME + " — Money Sent ₹" + amount;

        String content = greeting(firstName) + """
            <h2 style="color:%s;margin:20px 0 10px;">Money Transferred Successfully</h2>
            <p style="color:#555;font-size:14px;line-height:1.6;">
                Your transfer has been processed. Here are the details:
            </p>
            %s
            %s
            <p style="color:#888;font-size:12px;margin-top:20px;">
                If you did not initiate this transfer, please contact us immediately.
            </p>
            """.formatted(
                PRIMARY_COLOR,
                infoTable(
                    infoRow("Transaction Ref", transactionRef),
                    infoRow("From Account", fromAccount),
                    infoRow("To Account", toAccount),
                    infoRow("Amount Debited", "₹" + amount),
                    infoRow("Remaining Balance", "₹" + balanceAfter),
                    infoRow("Description", description != null ? description : "—"),
                    infoRow("Date & Time", LocalDateTime.now().format(DATETIME_FORMAT)),
                    infoRow("Status", "✅ Success")
                ),
                alertBox("₹" + amount + " has been debited from your account " + fromAccount, DANGER_COLOR)
        );

        return new String[]{subject, wrap("Money Sent", content)};
    }

    // ─────────────────────────────────────────────
    // 5. TRANSFER — RECEIVER
    // ─────────────────────────────────────────────

    public static String[] transferReceived(String firstName, String fromAccount, String toAccount,
                                             BigDecimal amount, String transactionRef, String description) {
        String subject = BANK_NAME + " — Money Received ₹" + amount;

        String content = greeting(firstName) + """
            <h2 style="color:%s;margin:20px 0 10px;">Money Received 🎉</h2>
            <p style="color:#555;font-size:14px;line-height:1.6;">
                A transfer has been credited to your account.
            </p>
            %s
            %s
            """.formatted(
                PRIMARY_COLOR,
                infoTable(
                    infoRow("Transaction Ref", transactionRef),
                    infoRow("Credited To", toAccount),
                    infoRow("Received From", fromAccount),
                    infoRow("Amount Credited", "₹" + amount),
                    infoRow("Description", description != null ? description : "—"),
                    infoRow("Date & Time", LocalDateTime.now().format(DATETIME_FORMAT)),
                    infoRow("Status", "✅ Success")
                ),
                alertBox("₹" + amount + " has been credited to your account " + toAccount, SUCCESS_COLOR)
        );

        return new String[]{subject, wrap("Money Received", content)};
    }

    // ─────────────────────────────────────────────
    // 6. TRANSFER FAILED
    // ─────────────────────────────────────────────

    public static String[] transferFailed(String firstName, String fromAccount,
                                           BigDecimal amount, String reason) {
        String subject = BANK_NAME + " — Transfer Failed";

        String content = greeting(firstName) + """
            <h2 style="color:%s;margin:20px 0 10px;">Transfer Failed ❌</h2>
            <p style="color:#555;font-size:14px;line-height:1.6;">
                Unfortunately your transfer could not be processed.
            </p>
            %s
            %s
            <p style="color:#555;font-size:14px;">
                No amount has been deducted from your account. Please try again or contact support.
            </p>
            """.formatted(
                DANGER_COLOR,
                infoTable(
                    infoRow("From Account", fromAccount),
                    infoRow("Amount", "₹" + amount),
                    infoRow("Reason", reason),
                    infoRow("Date & Time", LocalDateTime.now().format(DATETIME_FORMAT)),
                    infoRow("Status", "❌ Failed")
                ),
                alertBox("Your transfer of ₹" + amount + " has failed. Reason: " + reason, DANGER_COLOR)
        );

        return new String[]{subject, wrap("Transfer Failed", content)};
    }

    // ─────────────────────────────────────────────
    // 7. CARD CREATION
    // ─────────────────────────────────────────────

    public static String[] cardCreation(String firstName, String maskedCardNumber,
                                         String cardType, LocalDate expiryDate,
                                         BigDecimal creditLimit) {
        String subject = BANK_NAME + " — New " + cardType + " Card Issued";

        String content = greeting(firstName) + """
            <h2 style="color:%s;margin:20px 0 10px;">Your New Card is Ready 💳</h2>
            <p style="color:#555;font-size:14px;line-height:1.6;">
                Your %s card has been successfully issued and is ready to use.
            </p>
            %s
            %s
            <p style="color:#555;font-size:13px;margin-top:20px;">
                🔒 <strong>Security Tips:</strong> Never share your card number, CVV, or PIN with anyone. 
                %s will never ask for these details.
            </p>
            """.formatted(
                PRIMARY_COLOR,
                cardType.toLowerCase(),
                infoTable(
                    infoRow("Card Number", maskedCardNumber),
                    infoRow("Card Type", cardType),
                    infoRow("Card Holder", firstName),
                    infoRow("Expiry Date", expiryDate.format(DateTimeFormatter.ofPattern("MM/yyyy"))),
                    infoRow("Status", "✅ Active"),
                    cardType.equalsIgnoreCase("CREDIT")
                        ? infoRow("Credit Limit", "₹" + creditLimit)
                        : ""
                ),
                alertBox("Your card is now active. You can use it for online and in-store purchases.", SUCCESS_COLOR),
                BANK_NAME
        );

        return new String[]{subject, wrap("Card Issued", content)};
    }

    // ─────────────────────────────────────────────
    // 8. CARD STATUS UPDATE
    // ─────────────────────────────────────────────

    public static String[] cardStatusUpdate(String firstName, String maskedCardNumber,
                                             String oldStatus, String newStatus) {
        String color = newStatus.equalsIgnoreCase("ACTIVE") ? SUCCESS_COLOR :
                       newStatus.equalsIgnoreCase("BLOCKED") ? WARNING_COLOR : DANGER_COLOR;

        String subject = BANK_NAME + " — Card " + newStatus;

        String content = greeting(firstName) + """
            <h2 style="color:%s;margin:20px 0 10px;">Card Status Updated</h2>
            <p style="color:#555;font-size:14px;line-height:1.6;">
                The status of your card has been updated.
            </p>
            %s
            %s
            """.formatted(
                PRIMARY_COLOR,
                infoTable(
                    infoRow("Card Number", maskedCardNumber),
                    infoRow("Previous Status", oldStatus),
                    infoRow("New Status", newStatus),
                    infoRow("Updated On", LocalDateTime.now().format(DATETIME_FORMAT))
                ),
                alertBox(
                    newStatus.equalsIgnoreCase("BLOCKED")
                        ? "Your card has been blocked. No transactions can be made. You can unblock it anytime from the app."
                        : newStatus.equalsIgnoreCase("CANCELLED")
                        ? "Your card has been permanently cancelled. Please apply for a new card if needed."
                        : "Your card is now active and ready to use.",
                    color
                )
        );

        return new String[]{subject, wrap("Card Status Update", content)};
    }

    // ─────────────────────────────────────────────
    // 9. LOGIN ALERT
    // ─────────────────────────────────────────────

    public static String[] loginAlert(String firstName, String ipAddress, String device) {
        String subject = BANK_NAME + " — New Login Detected";

        String content = greeting(firstName) + """
            <h2 style="color:%s;margin:20px 0 10px;">New Login to Your Account</h2>
            <p style="color:#555;font-size:14px;line-height:1.6;">
                We detected a new login to your %s account.
            </p>
            %s
            %s
            <p style="color:#555;font-size:14px;">
                If this was you, no action is needed. If you did not log in, 
                please change your password immediately and contact support.
            </p>
            """.formatted(
                PRIMARY_COLOR, BANK_NAME,
                infoTable(
                    infoRow("Date & Time", LocalDateTime.now().format(DATETIME_FORMAT)),
                    infoRow("IP Address", ipAddress != null ? ipAddress : "Unknown"),
                    infoRow("Device", device != null ? device : "Unknown")
                ),
                alertBox("If this wasn't you, secure your account immediately!", WARNING_COLOR)
        );

        return new String[]{subject, wrap("Login Alert", content)};
    }

    // ─────────────────────────────────────────────
    // 10. LOW BALANCE ALERT
    // ─────────────────────────────────────────────

    public static String[] lowBalanceAlert(String firstName, String accountNumber, BigDecimal balance) {
        String subject = BANK_NAME + " — Low Balance Alert";

        String content = greeting(firstName) + """
            <h2 style="color:%s;margin:20px 0 10px;">Low Balance Alert ⚠️</h2>
            <p style="color:#555;font-size:14px;line-height:1.6;">
                Your account balance has dropped below the minimum threshold.
            </p>
            %s
            %s
            """.formatted(
                WARNING_COLOR,
                infoTable(
                    infoRow("Account Number", accountNumber),
                    infoRow("Current Balance", "₹" + balance),
                    infoRow("Minimum Balance", "₹500.00"),
                    infoRow("Date & Time", LocalDateTime.now().format(DATETIME_FORMAT))
                ),
                alertBox("Please add funds to avoid service restrictions on your account.", WARNING_COLOR)
        );

        return new String[]{subject, wrap("Low Balance Alert", content)};
    }
}