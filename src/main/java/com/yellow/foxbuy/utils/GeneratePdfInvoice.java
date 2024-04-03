package com.yellow.foxbuy.utils;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.FileNotFoundException;

public class GeneratePdfInvoice {
    private final String invoiceNumber;
    private final String fullName;
    private final String address;
    private final String date;

 //   UserRepository userRepository;

    public GeneratePdfInvoice(String invoiceNumber, String fullName, String address, String date) throws FileNotFoundException {
        this.invoiceNumber = invoiceNumber;
        this.fullName = fullName;
        this.address = address;
        this.date = date;
    }

    public void generateAndClose() throws FileNotFoundException {
        String path = "invoice.pdf";
        PdfWriter pdfWriter = new PdfWriter(path);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);

        Document document = new Document(pdfDocument);

        document.add(new Paragraph("Invoice for VIP USERS"));
        document.add(new Paragraph("Invoice Number: " + invoiceNumber));
        document.add(new Paragraph("Date: " + date));
        document.add(new Paragraph(" "));

        Table userInfoTable = new Table(2);
        userInfoTable.addCell("Full Name:");
        userInfoTable.addCell(fullName);
        userInfoTable.addCell("Address:");
        userInfoTable.addCell(address);
        document.add(userInfoTable);

        document.close();

    }

}
