package com.fokuswissen.vuforia;

import org.springframework.stereotype.Component;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class VuforiaClient {

    private final String accessKey = "YOUR_SERVER_ACCESS_KEY";
    private final String secretKey = "YOUR_SERVER_SECRET_KEY";
    private final String targetEndpoint = "https://vws.vuforia.com/targets";

    public String uploadImageTarget(String name, String imageUrl) {
        try
        {
            // Schritt 1: Lade das Bild herunter
            byte[] imageBytes = new URL(imageUrl).openStream().readAllBytes();

            // Schritt 2: Erstelle JSON-Payload
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String payload = String.format("""
            {
                "name": "%s",
                "width": 320.0,
                "image": "%s",
                "active_flag": true
            }
            """, name, base64Image);

            // Schritt 3: Signiere Request
            byte[] secretBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec signingKey = new SecretKeySpec(secretBytes, "HmacSHA1");

            String dateStr = new Date().toString();
            String stringToSign = "POST\n" + getMD5(payload) + "\napplication/json\n" + dateStr + "\n/targets";

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            String signature = Base64.getEncoder().encodeToString(mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8)));

            // Schritt 4: Request senden
            HttpURLConnection conn = (HttpURLConnection) new URL(targetEndpoint).openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "VWS " + accessKey + ":" + signature);
            conn.setRequestProperty("Date", dateStr);
            conn.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            InputStream responseStream = conn.getInputStream();
            String response = new String(responseStream.readAllBytes());
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getMD5(String data) throws Exception {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        byte[] array = md.digest(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(array);
    }
}
