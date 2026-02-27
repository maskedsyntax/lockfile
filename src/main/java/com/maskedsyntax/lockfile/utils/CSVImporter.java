package com.maskedsyntax.lockfile.utils;

import com.maskedsyntax.lockfile.model.Entry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVImporter {

    public static List<Entry> importFromCSV(File file) throws Exception {
        List<Entry> entries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String headerLine = br.readLine();
            if (headerLine == null) return entries;

            String[] headers = parseCSVLine(headerLine);
            Map<String, Integer> columnMap = mapHeaders(headers);

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = parseCSVLine(line);
                if (values.length == 0) continue;

                Entry entry = new Entry();
                entry.setTitle(getValue(values, columnMap, "title", "name"));
                entry.setUsername(getValue(values, columnMap, "username", "login_username"));
                entry.setPassword(getValue(values, columnMap, "password", "login_password"));
                entry.setUrl(getValue(values, columnMap, "url", "login_uri", "website"));
                entry.setNotes(getValue(values, columnMap, "notes", "note"));
                entry.setTotpSecret(getValue(values, columnMap, "totp", "login_totp"));

                if (entry.getTitle() != null || entry.getUsername() != null) {
                    entries.add(entry);
                }
            }
        }
        return entries;
    }

    private static String[] parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        values.add(sb.toString().trim());
        return values.toArray(new String[0]);
    }

    private static Map<String, Integer> mapHeaders(String[] headers) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            map.put(headers[i].toLowerCase(), i);
        }
        return map;
    }

    private static String getValue(String[] values, Map<String, Integer> columnMap, String... keys) {
        for (String key : keys) {
            Integer index = columnMap.get(key.toLowerCase());
            if (index != null && index < values.length) {
                String val = values[index];
                if (val.startsWith("\"") && val.endsWith("\"")) {
                    val = val.substring(1, val.length() - 1);
                }
                return val;
            }
        }
        return null;
    }
}
