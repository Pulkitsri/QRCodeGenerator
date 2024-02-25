package com.pulkit.QRCodeGenerator.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.pulkit.QRCodeGenerator.Model.UserInfo;


@Controller
@RequestMapping("/qr")
public class QRController {
    
    @ModelAttribute("qr")
    public UserInfo UserInfo(){
        return new UserInfo();
    }

    @GetMapping
    public String homePage(Model model){
        model.addAttribute("qr", UserInfo());
        return "index";
    }

    @PostMapping
    public String generateQRCode(@ModelAttribute("qr") UserInfo userInfo, Model model){
        try {
            BufferedImage bufferedImage =  generateQRCodeImage(userInfo);
            //Adding logo to QR code
            BufferedImage bufferedImageWithOverlay = getQRCodeWithOverlay(bufferedImage);
            File output  = new File( "C:\\Users\\Dell\\Documents\\Spring Boot Projects\\QRCodeGenerator\\QRCodeGenerator\\src\\main\\resources\\"+userInfo.getFirstName()+".jpg");
            ImageIO.write(bufferedImageWithOverlay, "png", output);
            model.addAttribute("qr", userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }   
        
        return "redirect:/qr?success";
    }

    //Method to generate QR code
    public static BufferedImage generateQRCodeImage(UserInfo userInfo) throws WriterException {
        StringBuilder sb = new StringBuilder();
        sb.append("First Name:").append(userInfo.getFirstName()).append("\n")
        .append("Last Name:").append(userInfo.getLastName()).append("\n")
        .append("City:").append(userInfo.getCity()).append("\n")
        .append("State:").append(userInfo.getState()).append("\n")
        .append("ZipCode:").append(userInfo.getZipCode()).append("\n")
        .append("message").append(userInfo.getMessage());
        // Create new configuration that specifies the error correction
        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix  = qrCodeWriter.encode(sb.toString(), BarcodeFormat.QR_CODE, 1500 , 1500,hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    //Method to add logo to QR code
    public BufferedImage getQRCodeWithOverlay(BufferedImage qrcode) throws IOException{
        BufferedImage overlay = ImageIO.read(new File("C:\\Users\\Dell\\Documents\\Spring Boot Projects\\QRCodeGenerator\\QRCodeGenerator\\src\\main\\resources\\Logo.png"));
        //Calculate the delta height and width
        Integer deltaHeight = qrcode.getHeight() - overlay.getHeight();
        Integer deltaWidth  = qrcode.getWidth()  - overlay.getWidth();
        System.out.println("Logo Height = "+overlay.getHeight());
        System.out.println("Logo Width = "+overlay.getWidth());
        System.out.println("QR Height = "+qrcode.getHeight());
        System.out.println("QR Width = "+qrcode.getWidth());
        System.out.println("deltaHeight = "+deltaHeight);
        System.out.println("deltaWidth = "+deltaWidth);
        //Draw the new image
        BufferedImage combined = new BufferedImage(qrcode.getWidth(), qrcode.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D)combined.getGraphics();
        g2.drawImage(qrcode, 0, 0, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2.drawImage(overlay, Math.round(deltaHeight/2), Math.round(deltaWidth/2), null);
        return combined;
    }

    
}
