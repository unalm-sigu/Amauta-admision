package pe.edu.lamolina.amauta.controller.academico.profesor.view;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.file.pdf.AbstractOnlyPdfView;
import pe.edu.lamolina.amauta.controller.academico.profesor.HistoricoCargaAcademicoBean;

@Component
public class ReporteHistoricoCargaAcademicoView extends AbstractOnlyPdfView {

    private final String TITULO = "UNIVERSIDAD AGRARIA LA MOLINA";
    private final String SUB_TITULO = "DIRECCIÓN DE ESTUDIOS Y REGISTROS ACADEMICOS";
    private final String SUB_TITULO_2 = "Historico de Carga Académica de créditos por profesor";

    private String DPTO = "";

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<HistoricoCargaAcademicoBean> historicos = (List<HistoricoCargaAcademicoBean>) model.get("historicos");
        List<String> docentes = historicos.stream().map(x -> x.getNombreDocente()).distinct().collect(Collectors.toList());
        List<String> departamentos = historicos.stream().map(x -> x.getDepartamento()).distinct().collect(Collectors.toList());
        List<String> ciclos = historicos.stream().map(x -> x.getCiclo()).distinct().collect(Collectors.toList());

        // IE workaround: write into byte array first.
        ByteArrayOutputStream baos = createTemporaryOutputStream();

        // Apply preferences and build metadata.
        Document document = new Document(PageSize.A4.rotate());

        File tempFile = new File("TablaConFiltros.pdf");
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        prepareWriter(model, writer, request);
        buildPdfMetadata(model, document, request);

        // Build PDF document.
//        writer.setInitialLeading(100);
        document.open();
        this.headerPDF(document, writer);
        this.buildPdfDocument(model, document, writer, request, response);
//        this.bodyPDF(model, document);

        System.out.println("PDF creado con éxito con los filtros aplicados.");

        String filename = "historico-carga-academica";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".pdf\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
        document.close();

        // Flush to HTTP response.
        writeToResponse(response, baos);

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(tempFile);
            } else {
                System.out.println("Abrir archivos no está soportado en este sistema.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {
        document.addAuthor("La Molina");
        document.addCreationDate();
        document.addCreator("Amauta");
        document.addTitle("Reporte Historico Carga Académica");
        document.addSubject("");
        document.setPageSize(PageSize.A4.rotate());
    }

    protected void headerPDF(Document document, PdfWriter writer) throws DocumentException, BadElementException, IOException {

        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font fontSubtitulo = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

        Paragraph parrafoTitulo = new Paragraph();
        parrafoTitulo.setFont(fontTitulo);
        parrafoTitulo.add(TITULO);
        document.add(parrafoTitulo);

        Paragraph parrafoSubTitulo = new Paragraph();
        parrafoSubTitulo.setFont(fontSubtitulo);
        parrafoSubTitulo.add(SUB_TITULO);
        document.add(parrafoSubTitulo);

        Paragraph texto = new Paragraph();
        parrafoSubTitulo.setFont(fontSubtitulo);
        texto.setAlignment(Paragraph.ALIGN_CENTER);
        texto.add(SUB_TITULO_2);

        document.add(new Paragraph(""));

        // Crear una celda y agregarle el texto
        PdfPCell celda = new PdfPCell();
        celda.addElement(texto);
        celda.setPadding(5);
        celda.setBorderWidth(2);
        celda.setFixedHeight(30);
        celda.setBorderColor(BaseColor.BLACK);
        celda.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        celda.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

        // Crear una tabla con una sola celda y agregar la celda a la tabla
        PdfPTable tabla = new PdfPTable(1); // Tabla de una sola columna
//        tabla.setWidthPercentage(50);       // Ancho de la tabla al 50% de la página
        tabla.setSpacingBefore(20);         // Espacio antes de la tabla
        tabla.setSpacingAfter(20);          // Espacio después de la tabla
        tabla.addCell(celda);               // Agregar la celda a la tabla
        tabla.setHorizontalAlignment(Element.ALIGN_LEFT);
        tabla.setWidthPercentage(70);

        // Agregar la tabla al documento
        document.add(tabla);

    }

    protected void bodyPDF(Map<String, Object> model, Document document) throws Exception {
        List<HistoricoCargaAcademicoBean> historicos = (List<HistoricoCargaAcademicoBean>) model.get("historicos");
        List<String> docentes = historicos.stream().map(x -> x.getNombreDocente()).distinct().collect(Collectors.toList());
        List<String> departamentos = historicos.stream().map(x -> x.getDepartamento()).distinct().collect(Collectors.toList());
        List<String> ciclos = historicos.stream().map(x -> x.getCiclo()).distinct().collect(Collectors.toList());

        departamentos.forEach(dpto -> {

            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            Paragraph textoDPTO = new Paragraph();
            textoDPTO.setFont(fontTitulo);
            textoDPTO.add("DPTO ACADEMICO " + dpto.toUpperCase());

            try {
                document.add(textoDPTO);
                int docenteCol = 1;
                int numColumnas = docenteCol + ciclos.size();

//          Crear una tabla con 3 columnas para Nombre, Ciclo Académico y Calificación
                PdfPTable tabla = new PdfPTable(numColumnas);
                tabla.setWidthPercentage(100);  // Ancho de la tabla al 100% de la página

                float[] anchosColumnas = new float[numColumnas];
                anchosColumnas[0] = 100; // Ancho mayor para la primera columna
                for (int i = 1; i < numColumnas; i++) {
                    anchosColumnas[i] = 8; // Anchos iguales para las demás columnas
                }
                tabla.setWidths(anchosColumnas);

                // Encabezados de columna
                PdfPCell celdaDocente = new PdfPCell(new Paragraph("NOMBRE DEL PROFESOR"));
                celdaDocente.setRowspan(2); // Fusionar la celda en las 3 columnas
                celdaDocente.setHorizontalAlignment(Element.ALIGN_CENTER); // Centrar el texto dentro de la celda
//        celdaDocente.setPadding(20);

                tabla.addCell(celdaDocente);

                ciclos.stream().forEach(ciclo -> {
                    PdfPCell cellCiclo = new PdfPCell(new Paragraph(ciclo));
                    tabla.addCell(cellCiclo);

                });

                ciclos.stream().forEach(ciclo -> {
                    PdfPCell textCred = new PdfPCell(new Paragraph("Pre"));
                    tabla.addCell(textCred);

                });

                docentes.stream().forEach(docente -> {
                    historicos.stream().forEach(reporte -> {
                        if (reporte.getDepartamento().equalsIgnoreCase(dpto)
                                && reporte.getNombreDocente().equalsIgnoreCase(docente)) {
                            tabla.addCell(docente);

                            ciclos.stream().forEach(ciclo -> {
                                if (reporte.getDepartamento().equalsIgnoreCase(dpto)
                                        && reporte.getNombreDocente().equalsIgnoreCase(docente)
                                        && reporte.getCiclo().equalsIgnoreCase(ciclo)) {
                                    tabla.addCell(reporte.getCreditosPre());
                                }

                            });
                        }
                        int countCiclo = ciclos.size();
                        int count = 0;

                    });

//                    ciclos.stream().forEach(x -> {
//                        tabla.addCell("50.00");
//                    });
                });

//                this.DPTO = dpto;
//                this.buildPdfDocument(model, document, writer, request, response);
//                this.DPTO = "";
            } catch (DocumentException ex) {
                Logger.getLogger(ReporteHistoricoCargaAcademicoView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ReporteHistoricoCargaAcademicoView.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

        ///***********************
//        int docenteCol = 1;
//        int numColumnas = docenteCol + ciclos.size();
//
////          Crear una tabla con 3 columnas para Nombre, Ciclo Académico y Calificación
//        PdfPTable tabla = new PdfPTable(numColumnas);
//        tabla.setWidthPercentage(100);  // Ancho de la tabla al 100% de la página
//
//        float[] anchosColumnas = new float[numColumnas];
//        anchosColumnas[0] = 100; // Ancho mayor para la primera columna
//        for (int i = 1; i < numColumnas; i++) {
//            anchosColumnas[i] = 8; // Anchos iguales para las demás columnas
//        }
//        tabla.setWidths(anchosColumnas);
//
//        // Encabezados de columna
//        PdfPCell celdaDocente = new PdfPCell(new Paragraph("NOMBRE DEL PROFESOR"));
//        celdaDocente.setRowspan(2); // Fusionar la celda en las 3 columnas
//        celdaDocente.setHorizontalAlignment(Element.ALIGN_CENTER); // Centrar el texto dentro de la celda
////        celdaDocente.setPadding(20);
//
//        tabla.addCell(celdaDocente);
//
//        ciclos.stream().forEach(ciclo -> {
//            PdfPCell cellCiclo = new PdfPCell(new Paragraph(ciclo));
//            tabla.addCell(cellCiclo);
//
//        });
//        ciclos.stream().forEach(ciclo -> {
//            PdfPCell textCred = new PdfPCell(new Paragraph("Pre"));
//            tabla.addCell(textCred);
//
//        });
//
////         Agregar los datos filtrados a la tabla
//        docentes.stream().forEach(docente -> {
//            tabla.addCell(docente);
//            int countCiclo = ciclos.size();
//            int count = 0;
//            ciclos.stream().forEach(x -> {
//                tabla.addCell("50.00");
//            });
//
//        });
//        document.add(tabla);
    }

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        List<HistoricoCargaAcademicoBean> historicos = (List<HistoricoCargaAcademicoBean>) model.get("historicos");
//        List<String> docentes = historicos.stream().map(x -> x.getNombreDocente()).distinct().collect(Collectors.toList());
////
//        List<String> departamentos = historicos.stream().map(x -> x.getDepartamento()).distinct().collect(Collectors.toList());
//        List<String> ciclos = historicos.stream().map(x -> x.getCiclo()).distinct().collect(Collectors.toList());
//
//        departamentos.stream().forEach(dpto -> {
//            try {
//                PdfPTable tabla = this.tablaBody(ciclos);
//                docentes.stream().forEach(docente -> {
//                    Optional<HistoricoCargaAcademicoBean> histo = historicos.stream().
//                            filter(a -> a.getDepartamento().equalsIgnoreCase(dpto) && a.getNombreDocente().equalsIgnoreCase(docente)).
//                            distinct().findFirst();
//                    tabla.addCell(histo.isPresent() ? histo.get().getCodDocente():"NO DICTO");
//                    for (int i = 0; i < ciclos.size(); i++) {
////
//                        tabla.addCell("4");
////
//                    }
//                });
//
//            } catch (DocumentException ex) {
//                Logger.getLogger(ReporteHistoricoCargaAcademicoView.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        });

////        xxxxxxxxxxxxxxxxxxxxxxx  Crear una tabla con 3 columnas para Nombre, Ciclo Académico y Calificación
//        departamentos.forEach(dpto -> {
//            this.DPTO = dpto;
//
//            try {
//                Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
//
//                Paragraph textoDPTO = new Paragraph();
//                textoDPTO.setFont(fontTitulo);
//                textoDPTO.add("DPTO ACADEMICO " + dpto.toUpperCase());
//
//                document.add(textoDPTO);
//
//                // Encabezados de columna
//                PdfPCell celdaDocente = new PdfPCell(new Paragraph("NOMBRE DEL PROFESOR"));
//                celdaDocente.setRowspan(2); // Fusionar la celda en las 3 columnas
//                celdaDocente.setHorizontalAlignment(Element.ALIGN_CENTER); // Centrar el texto dentro de la celda
////        celdaDocente.setPadding(20);
//
//                PdfPTable tabla = new PdfPTable(numColumnas);
//                tabla.setWidthPercentage(100);  // Ancho de la tabla al 100% de la página
//
//                float[] anchosColumnas = new float[numColumnas];
//                anchosColumnas[0] = 100; // Ancho mayor para la primera columna
//                for (int i = 1; i < numColumnas; i++) {
//                    anchosColumnas[i] = 8; // Anchos iguales para las demás columnas
//                }
//                tabla.setWidths(anchosColumnas);
//
//                tabla.addCell(celdaDocente);
//
//                ciclos.stream().forEach(ciclo -> {
//                    PdfPCell cellCiclo = new PdfPCell(new Paragraph(ciclo));
//                    tabla.addCell(cellCiclo);
//
//                });
//
//                ciclos.stream().forEach(ciclo -> {
//                    PdfPCell textCred = new PdfPCell(new Paragraph("Pre"));
//                    tabla.addCell(textCred);
//
//                });
//
//                docentes.stream().forEach(docente -> {
//
////                    historicos.stream().forEach(reporte -> {
////                        int ciclosCount = ciclos.size();
////
////                        if (reporte.getDepartamento().equalsIgnoreCase(dpto)
////                                && reporte.getNombreDocente().equalsIgnoreCase(docente)) {
////
////                            tabla.addCell(docente);
//                    for (int i = 0; i < ciclos.size(); i++) {
//
//                        tabla.addCell("4");
//
//                    }
//
////                            ciclos.stream().forEach(ciclo -> {
////                                if (reporte.getDepartamento().equalsIgnoreCase(dpto)
////                                        && reporte.getNombreDocente().equalsIgnoreCase(docente)
////                                        && reporte.getCiclo().equalsIgnoreCase(ciclo)) {
////                                    tabla.addCell(reporte.getCreditosPre());
////                                }
////
////                            });
////                        }
////                        int countCiclo = ciclos.size();
////                        int count = 0;
////
////                    });
////                    ciclos.stream().forEach(x -> {
////                        tabla.addCell("50.00");
////                    });
//                });
//                document.add(tabla);
//
////                this.DPTO = dpto;
////                this.buildPdfDocument(model, document, writer, request, response);
////                this.DPTO = "";
//            } catch (Exception ex) {
//                Logger.getLogger(ReporteHistoricoCargaAcademicoView.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//        });
//        document.add(tabla);
//        List<HistoricoCargaAcademicoBean> historicos = (List<HistoricoCargaAcademicoBean>) model.get("historicos");
//        List<String> docentes = historicos.stream().map(x -> x.getNombreDocente()).distinct().collect(Collectors.toList());
//        List<String> ciclos = historicos.stream().map(x -> x.getCiclo()).distinct().collect(Collectors.toList());
//
//        int docenteCol = 1;
//        int numColumnas = docenteCol + ciclos.size();
//
////          Crear una tabla con 3 columnas para Nombre, Ciclo Académico y Calificación
//        PdfPTable tabla = new PdfPTable(numColumnas);
//        tabla.setWidthPercentage(100);  // Ancho de la tabla al 100% de la página
//
//        float[] anchosColumnas = new float[numColumnas];
//        anchosColumnas[0] = 100; // Ancho mayor para la primera columna
//        for (int i = 1; i < numColumnas; i++) {
//            anchosColumnas[i] = 8; // Anchos iguales para las demás columnas
//        }
//        tabla.setWidths(anchosColumnas);
//
//        // Encabezados de columna
//        PdfPCell celdaDocente = new PdfPCell(new Paragraph("NOMBRE DEL PROFESOR"));
//        celdaDocente.setRowspan(2); // Fusionar la celda en las 3 columnas
//        celdaDocente.setHorizontalAlignment(Element.ALIGN_CENTER); // Centrar el texto dentro de la celda
////        celdaDocente.setPadding(20);
//
//        tabla.addCell(celdaDocente);
//
//        ciclos.stream().forEach(ciclo -> {
//            PdfPCell cellCiclo = new PdfPCell(new Paragraph(ciclo));
//            tabla.addCell(cellCiclo);
//
//        });
//        ciclos.stream().forEach(ciclo -> {
//            PdfPCell textCred = new PdfPCell(new Paragraph("Pre"));
//            tabla.addCell(textCred);
//
//        });
//
////         Agregar los datos filtrados a la tabla
//        docentes.stream().forEach(docente -> {
//            tabla.addCell(docente);
//            int countCiclo = ciclos.size();
//            int count = 0;
//            ciclos.stream().forEach(x -> {
//                tabla.addCell("50.00");
//            });
//            for (HistoricoCargaAcademicoBean reporte : historicos) {
//
//                if (this.DPTO.equalsIgnoreCase(reporte.getDepartamento()) && docente.equalsIgnoreCase(reporte.getNombreDocente())) {
//                    tabla.addCell(reporte.getCreditosPre());
////                    count++;
////                    if (countCiclo == count ) {
////                        tabla.addCell("0.00"); // Puedes cambiar "N/A" a cualquier texto o dejar vacío
////                    }
//                }
//
//            }
////            if (countCiclo > count && count == 1) {
////                tabla.addCell("0.00"); // Puedes cambiar "N/A" a cualquier texto o dejar vacío
////            }
////
////            // Si no hay registros en 'historicos' para el docente, agregar dos celdas vacías
////            if (countCiclo > count && count == 0) {
////                tabla.addCell("0.00");
////                tabla.addCell("0.00");
////            }
//        });
//         Agregar la tabla al documento
//        document.add(tabla);
    }

    PdfPTable tablaBody(List<String> ciclos) throws DocumentException {

        int docenteCol = 1;
        int numColumnas = docenteCol + ciclos.size();

        PdfPCell celdaDocente = new PdfPCell(new Paragraph("NOMBRE DEL PROFESOR"));
        celdaDocente.setRowspan(2); // Fusionar la celda en las 3 columnas
        celdaDocente.setHorizontalAlignment(Element.ALIGN_CENTER); // Centrar el texto dentro de la celda
//        celdaDocente.setPadding(20);

        PdfPTable tabla = new PdfPTable(numColumnas);
        tabla.setWidthPercentage(100);  // Ancho de la tabla al 100% de la página

        float[] anchosColumnas = new float[numColumnas];
        anchosColumnas[0] = 100; // Ancho mayor para la primera columna
        for (int i = 1; i < numColumnas; i++) {
            anchosColumnas[i] = 8; // Anchos iguales para las demás columnas
        }
        tabla.setWidths(anchosColumnas);

        tabla.addCell(celdaDocente);

        ciclos.stream().forEach(ciclo -> {
            PdfPCell cellCiclo = new PdfPCell(new Paragraph(ciclo));
            tabla.addCell(cellCiclo);

        });

        ciclos.stream().forEach(ciclo -> {
            PdfPCell textCred = new PdfPCell(new Paragraph("Pre"));
            tabla.addCell(textCred);

        });
        return tabla;
    }

}
