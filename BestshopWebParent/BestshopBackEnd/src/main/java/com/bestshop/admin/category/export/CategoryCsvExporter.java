package com.bestshop.admin.category.export;

import com.bestshop.admin.AbstractExporter;
import com.bestshop.common.entity.Category;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class CategoryCsvExporter extends AbstractExporter {

    public void export(List<Category> categoryList, HttpServletResponse response, String moduleName) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv", moduleName);

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"Category ID", "Category Name"};
        String[] fieldMapping = {"id", "name"};

        csvWriter.writeHeader(csvHeader);

        for (Category category: categoryList){
            category.setName(category.getName().replace("--", "  "));
            csvWriter.write(category, fieldMapping);
        }
        csvWriter.close();
    }

}
