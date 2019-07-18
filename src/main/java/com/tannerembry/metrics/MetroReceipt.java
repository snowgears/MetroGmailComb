package com.tannerembry.metrics;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class MetroReceipt implements Comparable<MetroReceipt>{

    private Date date;
    private double cost;

    private MetroReceipt(Date date, double cost){
        this.date = date;
        this.cost = cost;
    }

    public Date getDate() {
        return date;
    }

    public double getCost(){
        return cost;
    }

    public static MetroReceipt parse(Message message) throws IOException {
        MetroReceipt receipt = null;

        //this message part is what contains all paypal information
        message.getPayload().getParts().get(0);

        receipt = parseFromMessage(message);

        //System.out.println(receipt.toString());

        return receipt;
    }

    private static MetroReceipt parseFromMessage(Message message) {
        Date date = new Date(message.getInternalDate());
        double cost = 0;

        //this message part is what contains all paypal information
        MessagePart part = message.getPayload().getParts().get(0);

        //need to decode the actual body of the message
        byte[] decoded = Base64.decodeBase64(part.getBody().toString());
        String purchase = "";
        try {
            purchase = new String(decoded, "UTF8");
        } catch (UnsupportedEncodingException e){
            return null;
        }

        Document doc = Jsoup.parse(purchase);

        String costString;

        //Elements content = doc.getAllElements();
        Elements content = doc.getElementsByTag("td");
        for (Element link : content) {
            //String linkHref = link.attr("href");
            //String linkText = link.text();
            if(link.hasText() && link.text().contains("Capital Metro Thank you for your purchase from Capital Metro")) {
                //System.out.println(link.text());
                //System.out.println();

                //int dateIndex = link.text().indexOf("Purchase Date: ")+15;
                //System.out.println(link.text().substring(dateIndex, dateIndex+));

                int costIndex = link.text().indexOf("Total Cost: ")+16;

                costString = link.text().substring(costIndex, costIndex+4);
                cost = Double.parseDouble(costString);
            }
        }

        return new MetroReceipt(date, cost);
    }

    @Override
    public String toString(){
        return ("PURCHASE: "+
                "\n\t Date: "+date.toString()+
                "\n\t Cost: "+cost);
    }

    public JSONObject toJSONObject(){
        JSONObject obj = new JSONObject();
        obj.put("date", date.toString());
        return obj;
    }

    public int compareTo(MetroReceipt receipt) {
        return this.getDate().compareTo(receipt.getDate());
    }
}
