package com.yellow.foxbuy.utils;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Component
public class GeneratePdfUtil {

   LocalDate today;
   String formattedDate;

   UserRepository userRepository;


   public GeneratePdfUtil(UserRepository userRepository) {
        this.today = LocalDate.now();
        this.userRepository = userRepository;
    }

    public void generateAndCloseInvoice(String username) throws IOException {
        String path = "invoice_vip_" + generateInvoiceNumber() + ".pdf"; // Add invoice number to filename
        PdfWriter pdfWriter = new PdfWriter(path);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);

        Document document = new Document(pdfDocument);

        // Title
        Paragraph title = new Paragraph("Invoice").setFontSize(24).setTextAlignment(TextAlignment.LEFT);
        document.add(title);

        // Invoice details table
        Table detailsTable = new Table(2);
        detailsTable.setWidthPercent(50);
        detailsTable.addCell(createCell("Invoice Number", generateInvoiceNumber()));
        detailsTable.addCell(createCell("Invoice Date", formattedDate));
        document.add(detailsTable);

        // User information table
        Table userInfoTable = new Table(2);
        userInfoTable.setWidthPercent(50);

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            userInfoTable.addCell(createCell("Full Name", user.getFullName()));
            userInfoTable.addCell(createCell("Address", user.getAddress()));
            document.add(userInfoTable);
        } else {
            // Handle the case where no user is found with the given username
            System.out.println("User with username '" + username + "' not found.");
        }

        // VIP benefits
        document.add(new Paragraph("VIP User Benefits:")
                .setFontSize(14)
                .setBold());

        document.add(new Paragraph("This invoice confirms your payment for VIP user status in the FoxBuy application. As a VIP user, you enjoy the following benefits:")
                .setMarginBottom(10));

        // Close the document
        document.close();
    }

    // Helper method to generate invoice number (placeholder implementation)
    private String generateInvoiceNumber() {
        // Placeholder implementation, replace with actual logic
        return "12345";
    }

    // Helper method to create a cell with content
    private Paragraph createCell(String label, String value) {
        return new Paragraph(label + ": " + value)
                .setFontSize(12)
                .setBold()
                .setMarginBottom(10);
    }
}
