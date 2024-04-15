package com.yellow.foxbuy.utils;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.yellow.foxbuy.models.User;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class GeneratePdfUtil {

    private final String formattedDate;
    private final String invoiceNumber;
    private static int invoiceCounter = 0;


    public GeneratePdfUtil() {
        LocalDate today = LocalDate.now();
        this.formattedDate = today.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.invoiceNumber = generateInvoiceNumber();
    }

    public String generateInvoice(User user) throws IOException {

        String directoryPath = "resources" + File.separator + "generated-PDF";
        String fileName = "invoice_vip_" + invoiceNumber + ".pdf";
        String filePath = directoryPath + File.separator + fileName;
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            directory.mkdirs(); //
        }

       try {
            PdfWriter pdfWriter = new PdfWriter(filePath);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument, PageSize.A4);

            // Define font and style)
            PdfFont font = PdfFontFactory.createFont();
            Style style = new Style()
                    .setFont(font)
                    .setFontSize(40)
                    .setFontColor(ColorConstants.BLACK)
                    .setBold();

            Style style1 = new Style()
                    .setFont(font)
                    .setFontSize(20)
                    .setFontColor(ColorConstants.WHITE)
                    .setBackgroundColor(ColorConstants.DARK_GRAY);

            // Title
            Paragraph title = new Paragraph()
                    .add(new Text("I  N  V  O  I  C  E ").addStyle(style))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20)
                    .setMarginRight(10);
            document.add(title);

            Paragraph number = new Paragraph()
                    .add(new Text("number: " + invoiceNumber).addStyle(style1))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20)
                    .setMarginRight(10);
            document.add(number);

            //Image
            String imagePath = "src/main/resources/fox.png";
            ImageData imageData = ImageDataFactory.create(imagePath);
            Image image = new Image(imageData);

            float width = 80f;
            float height = 80f;
            image.scaleAbsolute(width, height);  // Resize the image
            image.setFixedPosition(50, 700);
            document.add(image);

            // Empty line
            Paragraph emptyLine = new Paragraph("\n");

            document.add(emptyLine);
            document.add(emptyLine);
            document.add(emptyLine);

            // Company address
            Paragraph companyInfo = new Paragraph()
                    .add("Company: \nFOX BUY YELLOW team company CZ s.r.o.\nAddress: Vaclavske namesti 837/11, Nove Mesto, 110 00 Praha\nICO: 07513666")
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(14).setMarginBottom(20);
            document.add(companyInfo);

            document.add(emptyLine);

            Paragraph userInfo = new Paragraph()
                    .add("Billing to: \nCustomer: " + user.getFullName() + "\nAddress: " + user.getAddress() + "\nEmail: " + user.getEmail())
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(14)
                    .setMarginBottom(20);
            document.add(userInfo);

            document.add(emptyLine);


            // VIP benefits
            Paragraph vipBenefits = new Paragraph()
                    .add("Payment for VIP User account for one year.................................................20 USD")
                    .setFontSize(14)
                    .setBold()
                    .setMarginBottom(20)
                    .setMarginRight(10)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY);
            document.add(vipBenefits);

            document.add(emptyLine);
            document.add(emptyLine);

            // Total payment information
            Paragraph totalPayment = new Paragraph()
                    .add("Total Payment: $20")
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20)
                    .setMarginRight(10);
            document.add(totalPayment);

            // Close the document
            document.close();

        } catch (IOException e) {
            throw new RuntimeException("An error occurred while generating the PDF invoice.", e);
        }
        return invoiceNumber;
    }


    // Helper method to generate invoice number (placeholder implementation)
    private String generateInvoiceNumber() {
        String invoiceIdentifier = String.format("%03d", invoiceCounter);
        invoiceCounter++; // Increment the counter for the next invoice
        return "INV-" + formattedDate + "-" + invoiceIdentifier;
    }
}