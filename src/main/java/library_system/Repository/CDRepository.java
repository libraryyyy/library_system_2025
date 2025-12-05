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
    private static final String FILE_PATH = "src/main/resources/cds.json";

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    // دالة التحميل من الملف (مطابقة للـ BookRepository)
    public static void loadFromFile() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists() && file.length() > 0) {
                cds.clear(); // مهم جدًا عشان ما يتكررش الـ CDs
                List<CD> loaded = mapper.readValue(file, new TypeReference<List<CD>>() {});
                cds.addAll(loaded);
                System.out.println("Loaded CDs: " + cds.size());
            } else {
                System.out.println("No CDs file found or empty - starting with empty list.");
                cds.clear();
                saveToFile(); // إنشاء ملف فارغ من أول مرة
            }
        } catch (Exception e) {
            System.out.println("Error loading CDs: " + e.getMessage());
            e.printStackTrace();
            cds.clear();
            saveToFile(); // لو في مشكلة، نحفظ نسخة نظيفة
        }
    }

    // دالة الحفظ (مرتبة وجميلة)
    public static void saveToFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(FILE_PATH), cds);
            System.out.println("CDs saved successfully: " + cds.size());
        } catch (Exception e) {
            System.out.println("Error saving CDs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // إرجاع القائمة (آمنة)
    public static List<CD> getAll() {
        return new ArrayList<>(cds);
    }

    // إضافة CD وحفظ فوري
    public static void addCD(CD cd) {
        cds.add(cd);
        saveToFile();
        System.out.println("CD added: " + cd.getTitle() + " by " + cd.getArtist());
    }

    // مسح الكل
    public static void clear() {
        cds.clear();
        saveToFile();
        System.out.println("All CDs cleared.");
    }

    // البحث بالعنوان (جزئي) - زي الكتب
    public static List<CD> findByTitleContaining(String part) {
        return cds.stream()
                .filter(cd -> cd.getTitle().toLowerCase().contains(part.toLowerCase()))
                .toList();
    }

    // البحث بالفنان (جزئي)
    public static List<CD> findByArtistContaining(String part) {
        return cds.stream()
                .filter(cd -> cd.getArtist() != null &&
                        cd.getArtist().toLowerCase().contains(part.toLowerCase()))
                .toList();
    }

    // البحث العام (يستخدم في القائمة)
    public static List<CD> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        String lower = keyword.toLowerCase();
        return cds.stream()
                .filter(cd -> cd.getTitle().toLowerCase().contains(lower) ||
                        (cd.getArtist() != null && cd.getArtist().toLowerCase().contains(lower)))
                .toList();
    }

    // دالة مساعدة: إرجاع CD حسب الـ ID
    public static CD findById(String id) {
        return cds.stream()
                .filter(cd -> cd.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}