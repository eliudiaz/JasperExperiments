/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.cracken.code;

import java.math.BigDecimal;
import static net.sf.dynamicreports.report.builder.DynamicReports.cht;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.FontBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;

/**
 *
 * @author eliud
 */
public class PieChartReport {

    public PieChartReport() {
        build();
    }

    private void build() {
        FontBuilder boldFont = stl.fontArialBold().setFontSize(12);

        TextColumnBuilder<String> itemColumn = col.column("Item", "item", type.stringType());
        TextColumnBuilder<Integer> quantityColumn = col.column("Quantity", "quantity", type.integerType());
        TextColumnBuilder<BigDecimal> unitPriceColumn = col.column("Unit price", "unitprice", type.bigDecimalType());

        try {
            report()
                    .setTemplate(Templates.reportTemplate)
                    .columns(itemColumn, quantityColumn, unitPriceColumn)
                    .title(Templates.createTitleComponent("PieChart"))
                    .summary(
                            cht.pieChart()
                            .setTitle("Pie chart")
                            .setTitleFont(boldFont)
                            .setKey(itemColumn)
                            .series(
                                    cht.serie(unitPriceColumn)))
                    .pageFooter(Templates.footerComponent)
                    .setDataSource(createDataSource())
                    .show();
        } catch (DRException e) {
            e.printStackTrace();
        }
    }

    private JRDataSource createDataSource() {
        DRDataSource dataSource = new DRDataSource("item", "quantity", "unitprice");
        dataSource.add("Tablet", 350, new BigDecimal(300));
        dataSource.add("Laptop", 300, new BigDecimal(500));
        dataSource.add("Smartphone", 450, new BigDecimal(250));
        return dataSource;
    }

    public static void main(String[] args) {
        new PieChartReport();
    }
}
