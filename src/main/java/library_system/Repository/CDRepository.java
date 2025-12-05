package library_system.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import library_system.domain.CD;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CDRepository {

    private static final List<CD> cds = new ArrayList<>();
    private static final String FILE_PATH = "src/main/resources/cds.json";  // أو "cds.json"

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    // دالة التحميل (نفس `BookRepository` بالضبط)
    public static void loadFromFile() {
        File file = new File(FILE_PATH);
        try {
            if (file.exists() && file.length() > 0) {
                cds.clear();
                List<CD> loaded = mapper.readValue(file, new TypeReference<List<CD>>() {});
                cds.addAll(loaded);
                System.out.println("Loaded CDs: " + cds.size());
            } else {
                System.out.println("No CDs file or empty - starting with empty list.");
                cds.clear();
                saveToFile();
            }
        } catch (Exception e) {
            System.out.println("Error loading CDs: " + e.getMessage());
            cds.clear();
            saveToFile();
        }
    }

    // دالة الحفظ (مرتبة)
    public static void saveToFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(FILE_PATH), cds);
            System.out.println("CDs saved successfully: " + cds.size());
        } catch (Exception e) {
            System.out.println("Error saving CDs: " + e.getMessage());
        }
    }

    // إرجاع القائمة
    public static List<CD> getAll() {
        return new ArrayList<>(cds);  // نسخة آمنة
    }

    // إضافة وحفظ فوري
    public static void addCD(CD cd) {
        cds.add(cd);
        saveToFile();
    }

    // مسح الكل وحفظ
    public static void clear() {
        cds.clear();
        saveToFile();
    }

    // البحث بالعنوان (تطابق تام)
    public static List<CD> findByTitle(String title) {
        return cds.stream()
                .filter(cd -> cd.getTitle().equalsIgnoreCase(title))
                .toList();
    }

    // البحث بالعنوان (جزئي)
    public static List<CD> findByTitleContaining(String part) {
        return cds.stream()
                .filter(cd -> cd.getTitle().toLowerCase().contains(part.toLowerCase()))
                .toList();
    }

    // بحث عام (زي اللي عندك search)
    public static List<CD> search(String part) {
        return findByTitleContaining(part);
    }
}