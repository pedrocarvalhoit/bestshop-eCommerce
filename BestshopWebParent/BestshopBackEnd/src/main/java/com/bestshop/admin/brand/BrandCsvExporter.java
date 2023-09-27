package com.bestshop.admin.brand;

import com.bestshop.admin.AbstractExporter;
import com.bestshop.common.entity.Brand;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class BrandCsvExporter extends AbstractExporter {

    public void export(List<Brand> brandList, HttpServletResponse response, String moduleName) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv", moduleName);

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"Brand ID", "Brand Name"};
        String[] fieldMapping = {"id", "name"};

        csvWriter.writeHeader(csvHeader);

        for (Brand brand : brandList){
            csvWriter.write(brand, fieldMapping);
        }
        csvWriter.close();
    }
}
