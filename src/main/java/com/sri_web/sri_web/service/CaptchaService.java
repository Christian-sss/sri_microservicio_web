package com.sri_web.sri_web.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;

@Service
public class CaptchaService {

    private static final String CAPTCHA_SESSION_KEY = "AUTH_CAPTCHA";
    private static final String CAPTCHA_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final int CAPTCHA_LENGTH = 5;

    private final SecureRandom random = new SecureRandom();

    public CaptchaChallenge generate(HttpSession session) {
        StringBuilder code = new StringBuilder(CAPTCHA_LENGTH);
        for (int index = 0; index < CAPTCHA_LENGTH; index++) {
            code.append(CAPTCHA_ALPHABET.charAt(random.nextInt(CAPTCHA_ALPHABET.length())));
        }

        String value = code.toString();
        session.setAttribute(CAPTCHA_SESSION_KEY, value);
        return new CaptchaChallenge(value, toSvgDataUri(value));
    }

    public boolean isValid(String captcha, HttpSession session) {
        Object expected = session.getAttribute(CAPTCHA_SESSION_KEY);
        session.removeAttribute(CAPTCHA_SESSION_KEY);
        if (expected == null || captcha == null) return false;
        return expected.toString().equals(captcha.trim().toUpperCase(Locale.ROOT));
    }

    private String toSvgDataUri(String code) {
        StringBuilder letters = new StringBuilder();
        for (int index = 0; index < code.length(); index++) {
            int x = 24 + index * 33;
            int y = 49 + random.nextInt(12);
            int rotate = -15 + random.nextInt(31);
            String fill = index % 2 == 0 ? "#17351d" : "#1f6f36";
            letters.append("<text x=\"").append(x).append("\" y=\"").append(y)
                    .append("\" transform=\"rotate(").append(rotate).append(" ").append(x).append(" ").append(y).append(")\"")
                    .append(" fill=\"").append(fill).append("\" font-size=\"32\" font-weight=\"900\"")
                    .append(" font-family=\"Georgia, Times New Roman, serif\">")
                    .append(code.charAt(index))
                    .append("</text>");
        }

        StringBuilder noise = new StringBuilder();
        for (int index = 0; index < 22; index++) {
            noise.append("<circle cx=\"").append(8 + random.nextInt(184))
                    .append("\" cy=\"").append(8 + random.nextInt(62))
                    .append("\" r=\"").append(1 + random.nextInt(3))
                    .append("\" fill=\"").append(index % 2 == 0 ? "#2f7d32" : "#168aad")
                    .append("\" opacity=\".22\"/>");
        }

        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="200" height="78" viewBox="0 0 200 78">
                    <defs>
                        <linearGradient id="bg" x1="0" y1="0" x2="1" y2="1">
                            <stop offset="0" stop-color="#f7fff2"/>
                            <stop offset=".46" stop-color="#ffffff"/>
                            <stop offset="1" stop-color="#cfecc4"/>
                        </linearGradient>
                        <pattern id="grid" width="18" height="18" patternUnits="userSpaceOnUse">
                            <path d="M18 0H0v18" fill="none" stroke="#2f7d32" stroke-opacity=".08" stroke-width="1"/>
                        </pattern>
                    </defs>
                    <rect width="200" height="78" rx="18" fill="url(#bg)"/>
                    <rect width="200" height="78" rx="18" fill="url(#grid)"/>
                    <path d="M-12 52 C35 16, 70 78, 118 36 S183 14, 214 48" fill="none" stroke="#2f7d32" stroke-opacity=".42" stroke-width="3" stroke-linecap="round"/>
                    <path d="M-10 24 C36 54, 84 8, 126 38 S173 68, 212 22" fill="none" stroke="#168aad" stroke-opacity=".30" stroke-width="3" stroke-linecap="round"/>
                    <path d="M11 64 L188 11" stroke="#17351d" stroke-opacity=".10" stroke-width="2"/>
                    %s
                    <g filter="none">%s</g>
                </svg>
                """.formatted(noise, letters);

        String encoded = Base64.getEncoder().encodeToString(svg.getBytes(StandardCharsets.UTF_8));
        return "data:image/svg+xml;base64," + encoded;
    }

    public record CaptchaChallenge(String code, String imageDataUri) {
    }
}
