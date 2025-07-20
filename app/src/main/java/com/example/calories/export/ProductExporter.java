package com.example.calories.export;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.calories.utils.Utility.clipData;
import static com.example.calories.utils.Utility.emailSend;

import com.example.calories.data.models.ProductItem;

public class ProductExporter {

    private final Context context;

    public ProductExporter(Context context) {
        this.context = context;
    }

    public void export(ArrayList<ProductItem> productList) {
        if (productList == null || productList.isEmpty()) {
            Toast.makeText(context, "אין פריטים לייצוא", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<ProductItem> sortedList = sortProductList(productList);
        String exportText = generateExportText(sortedList);

        clipData(exportText, context);
        emailSend(exportText, context);

        Toast.makeText(context, "Copied successfully " + sortedList.size() + " items", Toast.LENGTH_SHORT).show();
    }

    private ArrayList<ProductItem> sortProductList(ArrayList<ProductItem> productList) {
        ArrayList<ProductItem> tempList = new ArrayList<>(productList); // שכפול כדי לא לשנות מקורית
        ArrayList<ProductItem> result = new ArrayList<>();

        for (int i = 0; i < tempList.size(); i++) {
            ProductItem itemI = tempList.get(i);
            if (itemI.getName() == null) continue;

            for (int j = 0; j < tempList.size(); j++) {
                ProductItem itemJ = tempList.get(j);
                if (itemJ.getName() == null || itemJ.getUnit() == null) continue;

                if (itemJ.getName().equals(itemI.getName()) && itemJ.getUnit().equals("יחידה")) {
                    result.add(copyItem(itemJ));
                    tempList.set(j, new ProductItem()); // לנטרל כפילויות
                }
            }

            for (int j = 0; j < tempList.size(); j++) {
                ProductItem itemJ = tempList.get(j);
                if (itemJ.getName() == null || itemJ.getUnit() == null) continue;

                if (itemJ.getName().equals(itemI.getName()) &&
                        !itemJ.getUnit().equals("100 גרם") &&
                        !itemJ.getUnit().equals("100 מל")) {
                    result.add(copyItem(itemJ));
                    tempList.set(j, new ProductItem());
                }
            }

            for (int j = 0; j < tempList.size(); j++) {
                ProductItem itemJ = tempList.get(j);
                if (itemJ.getName() == null || itemJ.getUnit() == null) continue;

                if (itemJ.getName().equals(itemI.getName()) &&
                        (itemJ.getUnit().equals("100 גרם") || itemJ.getUnit().equals("100 מל"))) {
                    result.add(copyItem(itemJ));
                    tempList.set(j, new ProductItem());
                }
            }
        }

        return result;
    }

    private ProductItem copyItem(ProductItem item) {
        return new ProductItem(
                item.getUnitTypeValue(),
                item.getItemState(),
                item.getName(),
                item.getUnit(),
                item.getCalorieText(),
                item.getUnitImageResId(),
                item.getBarcode()
        );
    }

    private String generateExportText(ArrayList<ProductItem> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("_myProductList_ ").append(list.size()).append(" items");

        for (ProductItem item : list) {
            String barcode = item.getBarcode();
            if (barcode == null || barcode.equals("0") || barcode.isEmpty()) {
                barcode = "";
            }

            sb.append("\nSystemProductArr.add(new ProductItem(")
                    .append(0).append(", ")
                    .append(0).append(", \"")
                    .append(item.getName()).append("\", \"")
                    .append(item.getUnit()).append("\", \"")
                    .append(item.getCalorieText()).append("\", ")
                    .append(0).append(", \"")
                    .append(barcode).append("\"));");
        }

        return sb.toString();
    }
}
