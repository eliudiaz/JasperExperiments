/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.cracken.code;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static net.sf.dynamicreports.report.builder.DynamicReports.*;

import net.sf.dynamicreports.report.builder.column.PercentageColumnBuilder;

import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;

/**
 *
 * @author eliud
 */
public class SimpleTestExp1 {

    public SimpleTestExp1() {
        build();
    }

    private void build() {
        StyleBuilder boldStyle = stl.style().bold();
        StyleBuilder boldCenteredStyle = stl.style(boldStyle).setHorizontalAlignment(HorizontalAlignment.CENTER);
        StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle)
                .setBorder(stl.pen1Point())
                .setBackgroundColor(Color.LIGHT_GRAY);

        //                                                           title,     field name     data type
        TextColumnBuilder<String> brandColumn = col.column("Brand", "brand", type.stringType()).setStyle(boldStyle);
        TextColumnBuilder<String> itemColumn = col.column("Item", "item", type.stringType()).setStyle(boldStyle);

        TextColumnBuilder<Integer> quantityColumn = col.column("Quantity", "quantity", type.integerType());
        TextColumnBuilder<BigDecimal> unitPriceColumn = col.column("Unit price", "unitprice", type.bigDecimalType());
        //price = unitPrice * quantity
        TextColumnBuilder<BigDecimal> priceColumn = unitPriceColumn.multiply(quantityColumn).setTitle("Price");
        PercentageColumnBuilder pricePercColumn = col.percentageColumn("Price %", priceColumn);
        TextColumnBuilder<Integer> rowNumberColumn = col.reportRowNumberColumn("No.")
                //sets the fixed width of a column, width = 2 * character width
                .setFixedColumns(2)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            report()//create new report design
                    .setColumnTitleStyle(columnTitleStyle)
                    .setSubtotalStyle(boldStyle)
                    .highlightDetailEvenRows()
                    .columns(//add columns
                            rowNumberColumn, brandColumn, itemColumn, quantityColumn, unitPriceColumn, priceColumn, pricePercColumn)
                    .groupBy(brandColumn)                    
                    .groupBy(itemColumn)
                    //			  .subtotalsAtSummary(sbt.count(rowNumberColumn),
                    //			  	sbt.sum(unitPriceColumn), sbt.sum(priceColumn))
                    .subtotalsAtFirstGroupFooter(sbt.count(rowNumberColumn),
                            sbt.sum(unitPriceColumn), sbt.sum(priceColumn))
                    //			  .title(cmp.text("Getting started").setStyle(boldCenteredStyle))//shows report title
                    //			  .pageFooter(cmp.pageXofY().setStyle(boldCenteredStyle))//shows number of page at page footer
                    .setDataSource(createDataSource())//set datasource
                    .show()
                    .toCsv(out);//create and show report
            String data;
            System.out.println(data = new String(out.toByteArray()));
            List<Map<?, ?>> objects = readObjectsFromCsv(data);
//            System.out.println(new Gson().toJson(objects));
            writeAsJson(objects);
        } catch (DRException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(SimpleTestExp1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private JRDataSource createDataSource() {
        DRDataSource dataSource = new DRDataSource("brand", "item", "quantity", "unitprice");
        dataSource.add("A", "Notebook", 1, new BigDecimal(500));
        dataSource.add("A", "DVD", 5, new BigDecimal(30));
        dataSource.add("A", "DVD", 1, new BigDecimal(28));
        dataSource.add("C", "DVD", 5, new BigDecimal(32));
        dataSource.add("C", "Book", 3, new BigDecimal(11));
        dataSource.add("C", "Book", 1, new BigDecimal(15));
        dataSource.add("C", "Book", 5, new BigDecimal(10));
        dataSource.add("D", "Book", 8, new BigDecimal(9));
        return dataSource;
    }

    public static List<Map<?, ?>> readObjectsFromCsv(String file) throws IOException {
        CsvSchema bootstrap = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<Map<?, ?>> mappingIterator = csvMapper.reader(Map.class).with(bootstrap).readValues(file);

        return mappingIterator.readAll();
    }

    public static void writeAsJson(List<Map<?, ?>> data) throws IOException {
        StringWriter sw = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(sw, data);
        System.out.println(sw.toString());
    }

    public static void main(String[] args) {
        new SimpleTestExp1();
    }
}
