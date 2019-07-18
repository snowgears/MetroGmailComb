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

import static java.util.Calendar.*;

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

        Collections.sort(allMetroReceipts);


        exportReceiptData(allMetroReceipts);
        exportBusDailyData(allMetroReceipts);
        exportBusAllData(allMetroReceipts);
    }

    private static void exportReceiptData(List<MetroReceipt> receipts) {
        try {
            File jsonFile = new File(exportDirectory, "server/json/metro_receipts.json");
            if (!jsonFile.exists())
                jsonFile.createNewFile();

            JSONObject json = new JSONObject();
            JSONArray objArray = new JSONArray();

            for(MetroReceipt receipt : receipts) {
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

    private static void exportBusDailyData(List<MetroReceipt> receipts) {
        try {
            File jsonFile = new File(exportDirectory, "server/json/metro_rides_daily.json");
            if (!jsonFile.exists())
                jsonFile.createNewFile();

            JSONObject json = new JSONObject();
            JSONArray dayColumn = new JSONArray();
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
            dayColumn.addAll(new ArrayList<String>(Arrays.asList(days)));
            JSONArray objArray = new JSONArray();

            HashMap<Integer, Integer> dayMap = new HashMap<Integer, Integer>();

            Calendar cal;
            for(MetroReceipt receipt : receipts) {
                cal = Calendar.getInstance(TimeZone.getTimeZone("CST"));
                cal.setTime(receipt.getDate());

                int day = cal.get(Calendar.DAY_OF_WEEK);

                //only count it if its a weekday
                if(day != SUNDAY && day != SATURDAY) {
                    if(dayMap.get(day) == null)
                        dayMap.put(day, 1);
                    else
                        dayMap.put(day, dayMap.get(day)+1);
                }
            }

            for(int i=MONDAY; i<SATURDAY; i++){
                if(dayMap.get(i) == null)
                    objArray.add(0);
                else
                    objArray.add(dayMap.get(i));
            }

            json.put("x", dayColumn);
            json.put("rides", objArray);

            FileWriter file = new FileWriter(jsonFile);
            file.write(json.toJSONString());
            file.flush();
            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void exportBusAllData(List<MetroReceipt> receipts) {
        try {
            File jsonFile = new File(exportDirectory, "server/json/metro_rides_all.json");
            if (!jsonFile.exists())
                jsonFile.createNewFile();

            //one column called "To Work"
            //another called "From Work"

            //have to convert all dates into the seconds of the day on that date for y values
            //will then convert them back when displaying the values

            JSONObject json = new JSONObject();
            JSONArray datesColumn = new JSONArray();
            //datesColumn.add("x");
            JSONArray commuteTimeInColumn = new JSONArray();
            JSONArray commuteTimeOutColumn = new JSONArray();

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

            SimpleDateFormat sdf;
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            sdf.setTimeZone(TimeZone.getTimeZone("CST"));

            HashMap<String, Integer> dateCommuteInMap = new HashMap<String, Integer>();
            HashMap<String, Integer> dateCommuteOutMap = new HashMap<String, Integer>();

            String dateString = "";
            String currentDateString = "";
            int currentDateStringCount = 0;
            Calendar cal;
            for(MetroReceipt receipt : receipts) {
                cal = Calendar.getInstance(TimeZone.getTimeZone("CST"));
                cal.setTime(receipt.getDate());

                dateString = dateFormat.format(cal.getTime());

                if(dateString.equals("07-17-2019")){
                    String s = "";
                }

                if(datesColumn.size() == 0){
                    datesColumn.add(dateString);
                }
                else if(! datesColumn.get(datesColumn.size()-1).equals(dateString)){
                    datesColumn.add(dateString);
                }

                if(!currentDateString.equals(dateString)){
                    currentDateString = dateString;
                    currentDateStringCount = 0;
                }
                currentDateStringCount++;

                int minutesInDay = cal.get(HOUR_OF_DAY) * 60;
                minutesInDay += cal.get(MINUTE);

                //first ride of the day
                if(currentDateStringCount == 1){
                    //commuteTimeInColumn.add(minutesInDay);
                    dateCommuteInMap.put(dateString, minutesInDay);
                    //commuteTimeInColumn.add(cal.getTime().getTime());
                    //commuteTimeInColumn.add(sdf.format(cal.getTime()));
                }
                //second ride of the day
                if(currentDateStringCount == 2){
                    //commuteTimeOutColumn.add(minutesInDay);
                    dateCommuteOutMap.put(dateString, minutesInDay);
                    //commuteTimeOutColumn.add(cal.getTime().getTime());
                    //commuteTimeOutColumn.add(sdf.format(cal.getTime()));
                }
            }

            for(MetroReceipt receipt : receipts) {
                cal = Calendar.getInstance(TimeZone.getTimeZone("CST"));
                cal.setTime(receipt.getDate());

                dateString = dateFormat.format(cal.getTime());

                if(dateCommuteInMap.get(dateString) != null){
                    commuteTimeInColumn.add(dateCommuteInMap.get(dateString));
                }
                else{
                    commuteTimeInColumn.add(null);
                }

                if(dateCommuteOutMap.get(dateString) != null){
                    commuteTimeOutColumn.add(dateCommuteOutMap.get(dateString));
                }
                else{
                    commuteTimeOutColumn.add(null);
                }
            }

            json.put("x", datesColumn);
            json.put("rideIn", commuteTimeInColumn);
            json.put("rideOut", commuteTimeOutColumn);

            FileWriter file = new FileWriter(jsonFile);
            file.write(json.toJSONString());
            file.flush();
            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
