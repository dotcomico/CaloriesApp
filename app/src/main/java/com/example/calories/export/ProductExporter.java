package com.example.calories.export;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.calories.utils.Utility.clipData;
import static com.example.calories.utils.Utility.emailSend;

import com.example.calories.data.models.Product;
import static com.example.calories.utils.AppConstants.*;
public class ProductExporter {

    private final Context context;

    public ProductExporter(Context context) {
        this.context = context;
    }

    public void export(ArrayList<Product> productList) {
        if (productList == null || productList.isEmpty()) {
            Toast.makeText(context, "אין פריטים לייצוא", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Product> sortedList = sortProductList(productList);
        String exportText = generateExportText(sortedList);

        clipData(exportText, context);
        emailSend(exportText, context);

        Toast.makeText(context, "Copied successfully " + sortedList.size() + " items", Toast.LENGTH_SHORT).show();
    }

    private ArrayList<Product> sortProductList(ArrayList<Product> productList) {
        ArrayList<Product> tempList = new ArrayList<>(productList); // שכפול כדי לא לשנות מקורית
        ArrayList<Product> result = new ArrayList<>();

        for (int i = 0; i < tempList.size(); i++) {
            Product itemI = tempList.get(i);
            if (itemI.getName() == null) continue;

            //סדר כך שאם יש מוצרים עם אותו שם אז הכנס קודם את זה בעל המדד: יחידה
            for (int j = 0; j < tempList.size(); j++) {
                Product itemJ = tempList.get(j);
                if (itemJ.getName() == null || itemJ.getUnit() == null) continue;

                if (itemJ.getName().equals(itemI.getName()) && itemJ.getUnit().equals(UNIT_UNIT)) {
                    result.add(copyItem(itemJ));
                    tempList.set(j, new Product()); // לנטרל כפילויות
                }
            }

            //סדר כך שאם יש מוצרים עם אותו שם אז הכנס קודם את זה בעל המדד: 100 גרם או 100 מל
            for (int j = 0; j < tempList.size(); j++) {
                Product itemJ = tempList.get(j);
                if (itemJ.getName() == null || itemJ.getUnit() == null) continue;

                if (itemJ.getName().equals(itemI.getName()) &&
                        !itemJ.getUnit().equals(UNIT_100_GRAM) &&
                        !itemJ.getUnit().equals(UNIT_100_ML)) {
                    result.add(copyItem(itemJ));
                    tempList.set(j, new Product());
                }
            }

            //סדר כך שאם יש מוצרים עם אותו שם אז הכנס קודם את זה בעל המדד: 100 גרם או 100 מל
            for (int j = 0; j < tempList.size(); j++) {
                Product itemJ = tempList.get(j);
                if (itemJ.getName() == null || itemJ.getUnit() == null) continue;

                if (itemJ.getName().equals(itemI.getName()) &&
                        (itemJ.getUnit().equals(UNIT_100_GRAM) || itemJ.getUnit().equals(UNIT_100_ML))) {
                    result.add(copyItem(itemJ));
                    tempList.set(j, new Product());
                }
            }
        }

        return result;
    }

    private Product copyItem(Product item) {
        return new Product(
                item.getItemState(),
                item.getName(),
                item.getUnit(),
                item.getCalorieText(),
                item.getBarcode()
        );
    }

    private String generateExportText(ArrayList<Product> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("_myProductList_ ").append(list.size()).append(" items");

        for (Product item : list) {
            String barcode = item.getBarcode();
            if (barcode == null || barcode.equals("0") || barcode.isEmpty()) {
                barcode = "";
            }

            sb.append("\nSystemProductArr.add(new Product(")
                    .append(PRODUCT_STATE_SYSTEM).append(", \"")
                    .append(item.getName()).append("\", \"")
                    .append(item.getUnit()).append("\", \"")
                    .append(item.getCalorieText()).append("\", ")
                    .append("\"")
                    .append(barcode).append("\"));");
        }

        return sb.toString();
    }
}
