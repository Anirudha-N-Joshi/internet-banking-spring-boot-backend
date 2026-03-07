package com.internetbanking.transaction.service;

import com.internetbanking.transaction.dto.TransactionResponseDTO;
import com.internetbanking.transaction.entity.Transaction;
import com.internetbanking.transaction.repository.TransactionRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionReadPlatformServiceImpl implements TransactionReadPlatformService {

    private final TransactionRepository transactionRepository;

    public TransactionReadPlatformServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<TransactionResponseDTO> getAccountTransactions(Long accountId) {
        return transactionRepository
                .findAllByAccountId(accountId)
                .stream()
                .map(tx -> mapToResponseDTO(tx, accountId))
                .toList();
    }

    @Override
    public List<TransactionResponseDTO> getOutgoingTransactions(Long accountId) {
        return transactionRepository
                .findByFromAccount_IdOrderByCreatedAtDesc(accountId)
                .stream()
                .map(tx -> mapToResponseDTO(tx, accountId))
                .toList();
    }

    @Override
    public List<TransactionResponseDTO> getIncomingTransactions(Long accountId) {
        return transactionRepository
                .findByToAccount_IdOrderByCreatedAtDesc(accountId)
                .stream()
                .map(tx -> mapToResponseDTO(tx, accountId))
                .toList();
    }

    @Override
    public byte[] generateStatement(Long accountId, LocalDate from, LocalDate to) {

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository.findByAccountIdAndDateRange(accountId, start, end);

        return generatePdf(accountId, transactions, from, to);
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction, Long accountId) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setTransactionRef(transaction.getTransactionRef());
        dto.setFromAccountNumber(transaction.getFromAccount().getAccountNumber());
        dto.setToAccountNumber(transaction.getToAccount().getAccountNumber());
        dto.setAmount(transaction.getAmount());
        dto.setBalanceBefore(transaction.getBalanceBefore());
        dto.setBalanceAfter(transaction.getBalanceAfter());
        dto.setStatus(transaction.getStatus().name());
        dto.setDescription(transaction.getDescription());
        dto.setFailureReason(transaction.getFailureReason());
        dto.setCreatedAt(transaction.getCreatedAt());

        if (transaction.getToAccount().getId().equals(accountId)) {
            dto.setTransactionType("CREDIT");
        } else {
            dto.setTransactionType("DEBIT");
        }
        return dto;
    }

    private byte[] generatePdf(Long accountId, List<Transaction> transactions,
                               LocalDate from, LocalDate to) {

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            content.setFont(PDType1Font.HELVETICA, 12);
            content.beginText();
            content.setLeading(14.5f);
            content.newLineAtOffset(50, 750);

            content.showText("Account Statement");
            content.newLine();

            content.showText("Account Id: " + accountId);
            content.newLine();

            content.showText("From: " + from + " To: " + to);
            content.newLine();
            content.newLine();

            for (Transaction tx : transactions) {
                String line = tx.getCreatedAt() + " | "
                        + tx.getAmount() + " | "
                        + tx.getDescription() + " | "
                        + tx.getBalanceAfter() + " | "
                        + tx.getStatus();

                content.showText(line);
                content.newLine();
                content.newLine();
            }

            content.endText();
            content.close();

            document.save(out);

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}