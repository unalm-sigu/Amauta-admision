package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.aula.SeccionDTO;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte.view.ReporteCrucesExcelView;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte.view.ReporteSeccionesByFilterExcelView;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.zelper.pdf.pdfHtml.PDFFormatoEnum;
import pe.edu.lamolina.pivot.zelper.pdf.pdfHtml.PdfHtmlView;

@Controller
@RequestMapping("academico/gposeccion/reporte")
public class GpoReporteController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GpoReporteService service;

    @Autowired
    PdfHtmlView pdfHtmlView;

    @Autowired
    BoletinPDF boletinPDF;

    @Autowired
    ReporteCrucesExcelView reporteCrucesExcelView;

    @Autowired
    ReporteSeccionesByFilterExcelView reporteSeccionesByFilterExcelView;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                } catch (ParseException e) {
                    setValue(null);
                }
            }
        });

        dataBinder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new BigDecimal(value.replaceAll(",", "")));
                } catch (Exception e) {
                    setValue(null);
                }
            }
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = service.findCiclo(ds.getCicloAcademico());
        model.addAttribute("cicloJson", createCicloJson(ciclo).toString());
        model.addAttribute("resumenJson", createResumenJson(service.resumenByCiclo(ciclo)));
        return "academico/gposeccion/reporte/reporte";
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        ObjectNode nodeJson = JsonHelper.createJson(ciclo, JsonNodeFactory.instance, true, new String[]{
            "id", "codigo", "descripcion", "descripcion2", "tipo",
            "modalidadEstudio.codigo",
            "modalidadEstudio.nombre"
        });
        return nodeJson;
    }

    @RequestMapping("reporteVeranoPagoPorDocente")
    public ModelAndView reporteVeranoPagoPorDocente(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        Date date = new Date();

        StringBuilder sb = new StringBuilder();
        sb.append(TypesUtil.getStringDate(date, " dd 'de' MMMM 'de' yyyy ", "es"));

        List<DepartamentoAcademico> departamentoAcademicos = service.allDepartamentoAcademico(cicloAcademico);

        model.addAttribute("departamentoAcademicos", departamentoAcademicos);
        model.addAttribute("fecha", sb.toString());
        model.addAttribute("formatoEnum", PDFFormatoEnum.REPORTE_VERANO_PAGO_DOCENTE);
        model.addAttribute("cicloAcademico", cicloAcademico);
        model.addAttribute("nombrePdf", "pago_profesor_nivelacion");

        return new ModelAndView(pdfHtmlView);
    }

    @RequestMapping("reporteVeranoDocenteDepartamento")
    public ModelAndView reporteVeranoDocenteDepartamento(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        Date date = new Date();

        StringBuilder sb = new StringBuilder();
        sb.append(TypesUtil.getStringDate(date, " dd 'de' MMMM 'de' yyyy ", "es"));

        List<DepartamentoAcademico> departamentoAcademicos = service.allDepartamentoAcademico(cicloAcademico);

        model.addAttribute("departamentoAcademicos", departamentoAcademicos);
        model.addAttribute("fecha", sb.toString());
        model.addAttribute("formatoEnum", PDFFormatoEnum.REPORTE_VERANO_DOCENTE_DEPARTAMENTO);
        model.addAttribute("cicloAcademico", cicloAcademico);
        model.addAttribute("nombrePdf", "pago_profesor_nivelacion");

        return new ModelAndView(pdfHtmlView);
    }

    @RequestMapping("reporteVeranoPagoCurso")
    public ModelAndView reporteVeranoPagoCurso(TramiteDocumentoAcademico tramiteDocumentoAcademicoForm, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        Date date = new Date();

        StringBuilder sb = new StringBuilder();
        sb.append(TypesUtil.getStringDate(date, " dd 'de' MMMM 'de' yyyy ", "es"));

        List<DepartamentoAcademico> departamentoAcademicos = service.allDepartamentoAcademico(cicloAcademico);

        model.addAttribute("departamentoAcademicos", departamentoAcademicos);
        model.addAttribute("fecha", sb.toString());
        model.addAttribute("formatoEnum", PDFFormatoEnum.REPORTE_VERANO_CURSO);
        model.addAttribute("cicloAcademico", cicloAcademico);
        model.addAttribute("nombrePdf", "pago_profesor_nivelacion");

        return new ModelAndView(pdfHtmlView);
    }

    @RequestMapping("reporteVeranoPagoPorDocenteFacultad")
    public ModelAndView reporteVeranoPagoPorDocenteFacultad(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        Date date = new Date();

        StringBuilder sb = new StringBuilder();
        sb.append(TypesUtil.getStringDate(date, " dd 'de' MMMM 'de' yyyy ", "es"));

        List<Facultad> falcultades = service.allDepartamentoAcademicoXfacultad(cicloAcademico);

        model.addAttribute("falcultades", falcultades);
        model.addAttribute("fecha", sb.toString());
        model.addAttribute("formatoEnum", PDFFormatoEnum.REPORTE_VERANO_PAGO_DOCENTE_FACULTAD);
        model.addAttribute("cicloAcademico", cicloAcademico);
        model.addAttribute("nombrePdf", "pago_profesor_nivelacion");

        return new ModelAndView(pdfHtmlView);
    }

    @RequestMapping("boletinPDF")
    public ModelAndView boletinPDF(Model model, HttpSession session, HttpServletResponse response) throws Exception {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();

        List<AnexoBoletin> anexosSuper = service.getAnexosForBoletin(ciclo);

        model.addAttribute("ciclo", ciclo);
        model.addAttribute("anexosSuper", anexosSuper);

        return new ModelAndView(boletinPDF);
    }

    private ObjectNode createResumenJson(GpoSeccionResumen resumen) {
        ObjectNode nodeJson = JsonHelper.createJson(resumen, JsonNodeFactory.instance, true, new String[]{"*"});
        return nodeJson;
    }

    @RequestMapping("reporteCrucesSecciones")
    public ModelAndView reporteCrucesSecciones(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        InputStream formato = this.getClass().getResourceAsStream("/templates/excel/formatoSeccionesConCruce.xlsx");

        List<Seccion> secciones = service.allSeccionesConCruce(cicloAcademico);

        model.addAttribute("formato", formato);
        model.addAttribute("secciones", secciones);
        model.addAttribute("ciclo", cicloAcademico);
        return new ModelAndView(reporteCrucesExcelView);
    }

    @RequestMapping("reporteSeccionesSinAulas")
    public ModelAndView reporteSeccionesSinAulas(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        InputStream formato = this.getClass().getResourceAsStream("/templates/excel/formatoSeccionesSinAula.xlsx");

        SeccionDTO seccionDTO = new SeccionDTO();
        seccionDTO.setTituloReporte("Secciones Sin Aula");
        seccionDTO.setModalidadesEstudioEnum(Arrays.asList(ModalidadEstudioEnum.PRE));
        seccionDTO.setConAula(false);
        seccionDTO.setConHorario(true);
        List<Seccion> secciones = service.allSeccionesSinAula(cicloAcademico, seccionDTO);

        model.addAttribute("seccionDTO", seccionDTO);
        model.addAttribute("formato", formato);
        model.addAttribute("secciones", secciones);
        model.addAttribute("ciclo", cicloAcademico);
        return new ModelAndView(reporteSeccionesByFilterExcelView);
    }

    @RequestMapping("reporteSeccionesConAulas")
    public ModelAndView reporteSeccionesConAulas(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        InputStream formato = this.getClass().getResourceAsStream("/templates/excel/formatoSeccionesSinAula.xlsx");

        SeccionDTO seccionDTO = new SeccionDTO();
        seccionDTO.setTituloReporte("Secciones Con Aula");
        seccionDTO.setModalidadesEstudioEnum(Arrays.asList(ModalidadEstudioEnum.EPG, ModalidadEstudioEnum.PRE));
        seccionDTO.setConAula(true);
        seccionDTO.setConHorario(true);
        List<Seccion> secciones = service.allSeccionesConAula(cicloAcademico, seccionDTO);

        model.addAttribute("seccionDTO", seccionDTO);
        model.addAttribute("formato", formato);
        model.addAttribute("secciones", secciones);
        model.addAttribute("ciclo", cicloAcademico);
        return new ModelAndView(reporteSeccionesByFilterExcelView);
    }

}
