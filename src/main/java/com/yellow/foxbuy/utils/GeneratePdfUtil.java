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
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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

    public void generateAndSendInvoiceByEmail(String username, UserRepository userRepository) throws IOException {
        String path = "invoice_vip_" + invoiceNumber + ".pdf";

        try {
            PdfWriter pdfWriter = new PdfWriter(path);
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
                    .setBackgroundColor(ColorConstants.BLACK);

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
            String imagePath = "C:\\Users\\Thinkpad\\Sarka\\FoxBuy\\badius-foxbuy-yellow\\src\\main\\resources\\fox.png";
            ImageData imageData = ImageDataFactory.create(imagePath);
            Image image = new Image(imageData);

            float width = 80f;
            float height = 80f;
            image.scaleAbsolute(width,height);  // Resize the image
            image.setFixedPosition(50, 700);
            document.add(image);

            // Empty line
            Paragraph emptyLine = new Paragraph("\n");

            document.add(emptyLine);
            document.add(emptyLine);
            document.add(emptyLine);

            // Company address
            Paragraph companyInfo = new Paragraph()
                    .add("FOX BUY YELLOW team company CZ s.r.o.\nAddress: Václavské námestí 837/11, Nové Mesto, 110 00 Praha\nICO: 07513666")
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginBottom(20);
            document.add(companyInfo);

            document.add(emptyLine);
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Paragraph userInfo = new Paragraph()
                        .add("Billing to: \nCustomer: " + user.getFullName() + "\nAddress: " + user.getAddress() + "\nEmail: " + user.getEmail())
                        .setTextAlignment(TextAlignment.LEFT)
                        .setMarginBottom(20);
                document.add(userInfo);

                document.add(emptyLine);

                // User information table
                Table userTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                        .setWidth(UnitValue.createPercentValue(100))
                        .setTextAlignment(TextAlignment.LEFT)
                        .setMarginBottom(20)
                        .setBorder(Border.NO_BORDER);

                document.add(userTable);

                // VIP benefits
                Paragraph vipBenefits = new Paragraph()
                        .add("Payment for VIP User account")
                        .setFontSize(14)
                        .setBold()
                        .setMarginBottom(20)
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY);
                document.add(vipBenefits);

                document.add(emptyLine);
                // Total payment information
                Paragraph totalPayment = new Paragraph()
                        .add("Total Payment: $20")
                        .setFontSize(20)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setMarginBottom(20);
                document.add(totalPayment);

                // Close the document
                document.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while generating the PDF invoice.", e);
        }
    }

    // Helper method to generate invoice number (placeholder implementation)
    private String generateInvoiceNumber() {
        String invoiceIdentifier = String.format("%03d", invoiceCounter);
        invoiceCounter++; // Increment the counter for the next invoice
        return "INV-" + formattedDate + "-" + invoiceIdentifier;
    }
}

