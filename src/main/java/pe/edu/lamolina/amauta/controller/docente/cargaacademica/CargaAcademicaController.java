package pe.edu.lamolina.amauta.controller.docente.cargaacademica;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.amauta.controller.reporte.view.ReporteAlumnosExcel;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;

@Slf4j
@Controller
@RequestMapping("docente/cargaacademica")
public class CargaAcademicaController {

    @Autowired
    CargaAcademicaService service;

    @Autowired
    ReporteAlumnosExcel reporteActasView;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        model.addAttribute("docente", ds.getDocente());
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());
        return "docente/cargaacademica";
    }

    @ResponseBody
    @RequestMapping("list")
    public ObjectNode list(HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        ArrayNode arrayPregrado = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode arrayPosgrado = new ArrayNode(JsonNodeFactory.instance);
        ObjectNode data = new ObjectNode(JsonNodeFactory.instance);

        CicloAcademico ciclo = ds.getCicloAcademico();
        log.debug("ciclo {}", ciclo.getId());
        log.debug("ciclo {}", ciclo.getTipo());
        log.debug("ciclo {}", ciclo.getCodigo());
        log.debug("ciclo {}", ciclo.getEstado());
        log.debug("ciclo {}", ciclo.getModalidadEstudio().getCodigoEnum());
        
        List<GrupoSeccion> gruposSeccion = service.allGpoSecciones(ds.getDocente(), ciclo);

        BigDecimal creditosPregrado = BigDecimal.ZERO;
        BigDecimal creditosPosgrado = BigDecimal.ZERO;

        for (GrupoSeccion grupoSeccion : gruposSeccion) {
            AnexoBoletin anexoSup = grupoSeccion.getAnexoBoletin().getAnexoSuperior();

            List<Seccion> secciones = grupoSeccion.getSecciones();
            for (Seccion seccion : secciones) {
                List<DocenteSeccion> profesSeccion = seccion.getDocenteSeccion();
                for (DocenteSeccion profeSecc : profesSeccion) {
                    if (profeSecc.getCreditosCarga() == null) {
                        continue;
                    }
                    if (anexoSup.isAnexoCursosPostgrado()) {
                        creditosPosgrado = creditosPosgrado.add(profeSecc.getCreditosCarga());
                    } else {
                        creditosPregrado = creditosPregrado.add(profeSecc.getCreditosCarga());
                    }
                }
            }

            ObjectNode node = JaneHelper
                    .from(grupoSeccion)
                    .only("id, estadoEnum, estadoGrupoEnum, cursoDirigido")
                    .join("cicloAcademico", "tipoEnum")
                    .join("curso", "codigo, nombre, tpc")
                    .join("curso.modalidadEstudio", "id, nombre, estado, codigo")
                    .join("planCalificacion", "id")
                    .json();

            List<Seccion> seccionesByGpoSecc = grupoSeccion.getSecciones();
            ArrayNode seccionesArray = new ArrayNode(JsonNodeFactory.instance);
            for (Seccion seccion : seccionesByGpoSecc) {
                ObjectNode nodeSeccion = JaneHelper
                        .from(seccion)
                        .only("id,tipoSeccionEnum,codigo2,matriculados,verInformacion,horarioTexto,linkZoom,modoDictado")
                        .join("aula", "codigo,nombre,usuarioZoom,passZoom")
                        .join("aula.aulaSuperior", "id, codigo, nombre")
                        .join("grupoHoras", "codigo")
                        .json();

                List<DocenteSeccion> docentesSecc = seccion.getDocenteSeccion();
                ArrayNode docSeccArray = JaneHelper
                        .from(docentesSecc)
                        .only("id,estado,porcentajeCarga,fechaInicio,fechaFin,creditosCarga")
                        .array();

                nodeSeccion.set("docenteSeccion", docSeccArray);
                seccionesArray.add(nodeSeccion);
            }

            node.set("secciones", seccionesArray);

            if (anexoSup.isAnexoCursosPostgrado()) {
                arrayPosgrado.add(node);
            } else {
                arrayPregrado.add(node);
            }
        }

        data.set("pregrado", arrayPregrado);
        data.set("posgrado", arrayPosgrado);
        data.put("creditosPregrado", creditosPregrado);
        data.put("creditosPosgrado", creditosPosgrado);
        data.put("isCongCicloPre", ciclo.getEstadoEnum()==CicloAcademicoEstadoEnum.CFG);
        
        return data;
    }

    @RequestMapping("reporteAlumno")
    public ModelAndView reporteDeActasExcel(Model model,
            @RequestParam("seccion") Long idSeccion,
            HttpSession session
    ) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        Seccion seccion = service.findSeccion(idSeccion);
        Curso curso = seccion.getGrupoSeccion().getCurso();
        List<MatriculaSeccion> matriculados = service.allMatriculadosBySecciion(seccion);

        model.addAttribute("docente", ds.getDocente());
        model.addAttribute("seccion", seccion);
        model.addAttribute("ciclo", ds.getCicloAcademico());
        model.addAttribute("curso", curso);
        model.addAttribute("matriculados", matriculados);

        return new ModelAndView(reporteActasView);
    }
}
