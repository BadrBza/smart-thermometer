package stas.thermometer.infrastructures;

import stas.thermometer.domains.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.*;

public class IniConfigurationReader implements ConfigurationReader {
    private static final String SECTION_START = "[";
    private static final String SECTION_END = "]";
    private static final int KEY_VALUE_PARTS = 2;
    private final Map<String, Map<String, String>> sections = new HashMap<>();

    public IniConfigurationReader(InputStream configPath) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(configPath)) {
            loadFromConfig(scanner);
        }
    }

    private void loadFromConfig(Scanner scanner) {
        String currentSectionName = null;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.startsWith(SECTION_START) && line.endsWith(SECTION_END)) {
                currentSectionName = line.substring(1, line.length() - 1);
            } else if (currentSectionName != null && line.contains("=")) {
                processKeyValueLine(line, currentSectionName);
            }
        }
    }

    private void processKeyValueLine(String line, String sectionName) {
        String[] parts = line.split("=", KEY_VALUE_PARTS);
        if (parts.length == KEY_VALUE_PARTS) {
            sections.computeIfAbsent(sectionName, k -> new HashMap<>())
                    .put(parts[0].trim(), parts[1].trim());
        }
    }

    @Override
    public Thermometer toThermometer() {
        String name = get("general", "name");
        String temperatureFormat = get("format", "temperature");
        String datetimeFormat = get("format", "datetime");
        List<Jalon> profileData = getJalons("profile");
        return new Thermometer(name, temperatureFormat, datetimeFormat, profileData);
    }

    @Override
    public String get(String section, String key) {
        return sections.getOrDefault(section, Collections.emptyMap()).get(key);
    }

    @Override
    public List<Jalon> getJalons(String sectionName) {
        Map<String, String> profileSection = sections.getOrDefault(sectionName, Collections.emptyMap());
        List<Jalon> jalons = new ArrayList<>();
        for (Map.Entry<String, String> entry : profileSection.entrySet()) {
            LocalTime time = LocalTime.of(extractHourFromKey(entry.getKey()), 0);
            double value = Double.parseDouble(entry.getValue());
            jalons.add(new Jalon(time, value));
        }
        jalons.sort(Comparator.comparing(Jalon::time));
        return jalons;
    }

    private int extractHourFromKey(String key) {
        return Integer.parseInt(key.substring(1));
    }
}
