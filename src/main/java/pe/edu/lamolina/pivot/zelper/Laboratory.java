package pe.edu.lamolina.pivot.zelper;

import org.thymeleaf.context.Context;
import pe.edu.lamolina.model.enums.DocumentoPdfEnum;
import pe.edu.lamolina.pivot.zelper.pdf.PdfContent;
import pe.edu.lamolina.pivot.zelper.pdf.PdfGenerator;
import pe.edu.lamolina.pivot.zelper.pdf.PdfGeneratorImp;
import pe.edu.lamolina.pivot.zelper.pdf.pdfHtml.PDFFormatoEnum;

public class Laboratory {

    
    public static void main666(String[] args) {
    
        PdfGenerator pdfGeneratorImp = new PdfGeneratorImp();
            Context ctx = new Context();
    
         PdfContent pdfContent = new PdfContent();
        pdfContent.setContext(ctx);
        pdfContent.setDocumentPdfEnum(DocumentoPdfEnum.RESULTADO_ENCUESTA);
        String filePdf = pdfGeneratorImp.generateDocument(pdfContent, "tmp");
        System.out.println(filePdf);
        
        
//        Font fontBold = new Font("Arial", Font.BOLD, 8);
//        Font fontNormal = new Font("Arial", Font.PLAIN, 6);
//        try {
//            DefaultCategoryDataset data = new DefaultCategoryDataset();
//            data.setValue(1, "CATEGORÍA", "Disposición y cumplimiento");
//            data.setValue(1, "CATEGORÍA", "Conduccion del aprendizaje");
//            data.setValue(3, "CATEGORÍA", "Motivacion");
//            data.setValue(2, "CATEGORÍA", "Evaluacion");
//            data.setValue(4, "CATEGORÍA", "Uso de material educativo");
//
//            JFreeChart chart = ChartFactory.createBarChart3D(
//                    "ENCUESTA ESTUDIANTIL\n(ESCALA 1-5)",
//                    "CATEGORÍA",
//                    "PUNTAJE",
//                    data,
//                    PlotOrientation.VERTICAL,
//                    false, true, false);
//
//            CategoryPlot plot = chart.getCategoryPlot();
//
//            plot.setRenderer((CategoryItemRenderer) new CustomRenderer(
//                    new Paint[]{Color.red, Color.blue, Color.green,
//                        Color.yellow, Color.orange}));
//
//            plot.setRangeGridlinePaint(new Color(0));
//            plot.setBackgroundPaint(Color.white);
//            plot.getDomainAxis().setTickLabelFont(fontNormal);
//            plot.getDomainAxis().setLabelFont(fontBold);
//            plot.getRangeAxis().setTickLabelFont(fontNormal);
//            plot.getRangeAxis().setLabelFont(fontBold);
//
//            chart.getTitle().setFont(fontBold);
//
//            BarRenderer br = (BarRenderer) plot.getRenderer();
//            br.setMaximumBarWidth(.05); // set maximum width to 35% of chart
//
//            NumberAxis range = (NumberAxis) plot.getRangeAxis();
//            range.setTickUnit(new NumberTickUnit(0.5));
//            plot.getRangeAxis().setRange(0, 5);
//
//            BarRenderer3D bre = (BarRenderer3D) plot.getRenderer();
//            bre.setSeriesPaint(0, Color.blue);
//            bre.setSeriesPaint(1, Color.green);
//            bre.setSeriesPaint(2, Color.red);
//            bre.setSeriesPaint(3, Color.cyan);
//            bre.setSeriesPaint(4, Color.pink);
//            bre.setWallPaint(Color.white);
//
//            Stroke dashedStroke = plot.getRangeGridlineStroke();
//            
//            plot.setRangeGridlineStroke(new BasicStroke(0.3f));
//
//           
//            ObjectUtil.printAttr(plot.getRangeAxis().getAttributedLabel());
//            int width = 640;
//            int height = 350;
//            Document document = new Document(new Rectangle(width, height));
//            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("pdf.pdf"));
//            document.open();
//            PdfContentByte Add_Chart_Content = writer.getDirectContent();
//            PdfTemplate template_Chart_Holder = Add_Chart_Content.createTemplate(width, height);
//            Graphics2D Graphics_Chart = template_Chart_Holder.createGraphics(width, height, new DefaultFontMapper());
//            Rectangle2D Chart_Region = new Rectangle2D.Double(0, 0, 500, 300);
//            chart.draw(Graphics_Chart, Chart_Region);
//            Graphics_Chart.dispose();
//            Add_Chart_Content.addTemplate(template_Chart_Holder, 0, 0);
//            document.close();
//        } catch (Exception i) {
//            System.out.println(i);
//        }

    }
}
