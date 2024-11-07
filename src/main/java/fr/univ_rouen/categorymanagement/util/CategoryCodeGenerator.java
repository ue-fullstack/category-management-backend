package fr.univ_rouen.categorymanagement.util;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CategoryCodeGenerator {
    private static final String PREFIX = "CAT";
    private static final int CODE_LENGTH = 6;
    private final AtomicInteger counter = new AtomicInteger(1);
    private final Random random = new Random();

    public String generateCode() {
        StringBuilder code = new StringBuilder(PREFIX);
        int number = counter.getAndIncrement();

        // Pad the number with zeros to ensure it's always 3 digits
        String paddedNumber = String.format("%03d", number);
        code.append(paddedNumber);

        // Add random characters to make the code more unique
        while (code.length() < CODE_LENGTH) {
            code.append((char) (random.nextInt(26) + 'A'));
        }

        return code.toString();
    }
}
