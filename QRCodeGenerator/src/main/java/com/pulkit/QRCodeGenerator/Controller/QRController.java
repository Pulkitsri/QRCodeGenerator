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
import java.io.FileInputStream;

import javax.imageio.ImageIO;
import javax.swing.Spring;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
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
            File output  = new File( "C:\\Users\\Dell\\Documents\\Spring Boot Projects\\QRCodeGenerator\\QRCodeGenerator\\src\\main\\resources\\"+userInfo.getFirstName()+".jpg");
            ImageIO.write(bufferedImage, "jpg", output);
            model.addAttribute("qr", userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }   
        
        return "redirect:/qr?success";
    }

    public static BufferedImage generateQRCodeImage(UserInfo userInfo) throws WriterException {
        StringBuilder sb = new StringBuilder();
        sb.append("First Name:").append(userInfo.getFirstName()).append("\n")
        .append("Last Name:").append(userInfo.getLastName()).append("\n")
        .append("City:").append(userInfo.getCity()).append("\n")
        .append("State:").append(userInfo.getState()).append("\n")
        .append("ZipCode:").append(userInfo.getZipCode()).append("\n")
        .append("message").append(userInfo.getMessage());

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix  = qrCodeWriter.encode(sb.toString(), BarcodeFormat.QR_CODE, 200 , 200);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
