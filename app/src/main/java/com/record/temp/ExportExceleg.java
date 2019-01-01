package com.record.temp;

import android.database.Cursor;
import android.os.Environment;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExportExceleg {
    private void exportToExcel(Cursor cursor) {
        String fileName = "TodoList.xls";
        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/javatechig.todo");
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        File file = new File(directory, "TodoList.xls");
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        try {
            WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
            WritableSheet sheet = workbook.createSheet("MyShoppingList", 0);
            try {
                sheet.addCell(new Label(0, 0, "Subject"));
                sheet.addCell(new Label(1, 0, "Description"));
                if (cursor.moveToFirst()) {
                    do {
                        int i = cursor.getPosition() + 1;
                        String title = "a" + i;
                        String desc = "b" + i;
                        sheet.addCell(new Label(0, i, title));
                        sheet.addCell(new Label(1, i, desc));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e2) {
                e2.printStackTrace();
            }
            workbook.write();
            try {
                workbook.close();
            } catch (WriteException e22) {
                e22.printStackTrace();
            }
        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }
}
