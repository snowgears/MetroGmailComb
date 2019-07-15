package com.tannerembry.metrics;

import com.google.api.services.gmail.Gmail;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class gets an initialized mail instance and then exports the different json files.
 */
public class JsonExporter {

    private static File exportDirectory;

    public static void main(String[] args) throws IOException {
        try {
            exportDirectory = new File(args[0]);
            if(!exportDirectory.exists())
                exportDirectory = new File(System.getProperty("user.dir"));
        } catch(Exception e){
            exportDirectory = new File(System.getProperty("user.dir"));
        }

        long startTime = System.currentTimeMillis();
        System.out.println("Scanning inbox for Metro receipts. This may take a few minutes...");

        // Build a new authorized API client service.
        Gmail service = MailInitializer.getGmailService();

        String query = "in:inbox (Your receipt from Capital Metro)";

        List<MetroReceipt> allMetroReceipts = MailInitializer.getAllMetroReceipts(service, "me", query);

        long endTime = System.currentTimeMillis();
        long totalSeconds = (endTime - startTime) / 1000;
        double totalMinutes = totalSeconds / 60;
        System.out.println("Compiled all Metro receipts (found "+ allMetroReceipts.size() +" in total). Job finished in "+(int)totalSeconds+" seconds.");


        exportReceiptData(allMetroReceipts);
    }

    private static void exportReceiptData(List<MetroReceipt> purchases) {
        try {
            File jsonFile = new File(exportDirectory, "metro_receipts.json");
            if (!jsonFile.exists())
                jsonFile.createNewFile();

            JSONObject json = new JSONObject();
            JSONArray objArray = new JSONArray();

            for(MetroReceipt receipt : purchases) {
                objArray.add(receipt.toJSONObject());
            }

            json.put("receipts", objArray);

            FileWriter file = new FileWriter(jsonFile);
            file.write(json.toJSONString());
            file.flush();
            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
