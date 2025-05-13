package pe.edu.lamolina.amauta.controller.programacionhorarios.resumen;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.*;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("academico/programacion/resumen")
public class ResumenProgramacionController {

    @Autowired
    ResumenProgramacionService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

//   model.addAttribute("docente", ds.getDocente());
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        //    model.addAttribute("dptoAcad", ds.getDepartamentoAcademico());
        return "academico/programacion/resumenProgramacion";
    }



    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();

            List<AnexoBoletin> allAnexosActivosHijos = service.allActiveAnexos(filter, ciclo);

            List<Long> anexos = new ArrayList<>();
            for (AnexoBoletin departamento : allAnexosActivosHijos) {
                anexos.add(departamento.getId());
            }

            List<DepartamentoCursosProgramadosDTO> counts = new ArrayList<>();
            if (!anexos.isEmpty()) {
                counts = service.countGroupsByFilter( anexos, ds.getCicloAcademico(),null);

            }

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (AnexoBoletin dep : allAnexosActivosHijos) {

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("idDep", dep.getId());
                node.put("nombreDep", dep.getNombre());

                Optional<DepartamentoCursosProgramadosDTO> optCount = counts.stream()
                        .filter(x -> x.getIdDepartamento().equals(dep.getId()))
                        .findFirst();

                if (optCount.isPresent()) {

                    DepartamentoCursosProgramadosDTO countAnexos = optCount.get();

                    node.put("cantidadActivos", countAnexos.getActivos());
                    node.put("cantidadCursos",countAnexos.getCantidadCursos());
                    node.put("cantidadGrupos", countAnexos.getCantidadGrupos());
                    node.put("cantidadAnulados", countAnexos.getAnulados());
                    node.put("cantidadCancelados", countAnexos.getCancelados());
                    node.put("cantidadFusionados", countAnexos.getFusionados());
                    node.put("cantidadInactivos", countAnexos.getInactivos());
                    node.put("cantidadBloqueados", countAnexos.getBloqueados());
                    node.put("cantidadTotal", countAnexos.getTotalSecciones());
                    node.put("cursosMenosAlumnos",countAnexos.getCursosMenos6Alumnos());
                    node.put("cursosSinDocente", countAnexos.getCursosSinDocente());
                    node.put("cursosTotal", 124);
                } else {

                    node.put("cantidadActivos", 0);
                    node.put("cantidadCursos", 0);
                    node.put("cantidadGrupos", 0);
                    node.put("cantidadAnulados", 0);
                    node.put("cantidadCancelados", 0);
                    node.put("cantidadFusionados", 0);
                    node.put("cantidadInactivos", 0);
                    node.put("cantidadBloqueados", 0);
                    node.put("cantidadTotal", 0);
                    node.put("cursosMenosAlumnos", 0);
                    node.put("cursosSinDocente", 0);
                }
                array.add(node);
            }

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }

        return json;
    }

    @ResponseBody
    @RequestMapping("stats")
    public Map<String, Object> getEstadisticasProgramacion(HttpSession session) {


        Map<String, Object> respuesta = new HashMap<>();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();

            DynatableFilter filter = new DynatableFilter();

            // Traer todos los anexos activos
            List<AnexoBoletin> anexos = service.allActiveAnexos(filter, ciclo); // puedes pasar null si no necesitas filtro

            List<Long> ids = anexos.stream()
                    .map(AnexoBoletin::getId)
                    .collect(Collectors.toList());

            // Obtener conteos por cada anexo
            List<DepartamentoCursosProgramadosDTO> counts = service.countGroupsByFilter(ids, ciclo, null);

            // Acumular totales
            int activos = 0, bloqueados = 0, anulados = 0, cancelados = 0, fusionados = 0;
            int inactivos = 0, total = 0, cursosSinDocente = 0, cursosMenos6Alumnos = 0;

            for (DepartamentoCursosProgramadosDTO dto : counts) {
                activos += dto.getActivos();
                bloqueados += dto.getBloqueados();
                anulados += dto.getAnulados();
                cancelados += dto.getCancelados();
                fusionados += dto.getFusionados();
                inactivos += dto.getInactivos();
                total += dto.getTotalSecciones();
                cursosSinDocente += dto.getCursosSinDocente();
                cursosMenos6Alumnos += dto.getCursosMenos6Alumnos();
            }

            // Poner en respuesta
            respuesta.put("activos", activos);
            respuesta.put("bloqueados", bloqueados);
            respuesta.put("anulados", anulados);
            respuesta.put("cancelados", cancelados);
            respuesta.put("fusionados", fusionados);
            respuesta.put("inactivos", inactivos);
            respuesta.put("total", total);
            respuesta.put("cursosSinDocente", cursosSinDocente);
            respuesta.put("cursosMenos6Alumnos", cursosMenos6Alumnos);

        } catch (Exception e) {
            e.printStackTrace();
            respuesta.put("error", "Error al obtener estadísticas");
        }

        return respuesta;
    }

    @RequestMapping("{departamento}/departamento")
    public String departamento(@PathVariable("departamento") Long idDepartamento, Model model, HttpSession session, RedirectAttributes redirect) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        DepartamentoAcademico depAcademico = service.findDepartamento(idDepartamento);
        AnexoBoletin anexoBoletin = service.findAnexoBoletin(idDepartamento);

//        List<GrupoSeccion> allGruposSeccion = service.allGrupoSeccionByFilter(ds.getCicloAcademico(), new DepartamentoAcademico(idDepartamento), EstadoEnum.ACT);
//        ActaResumen resumen = service.findResumenByDepartamento(ds.getCicloAcademico(), depAcademico);
//
//        Boolean esOperadorEditor = verificadorService.isOperadorActaNotas(ds);
//        logger.debug("esOperadorEditor {}", esOperadorEditor);
//
//        Boolean isRevisorActaNotas = verificadorService.isRevisorActaNotasDepartamento(ds);
//        logger.debug("isRevisorActaNotas {}", isRevisorActaNotas);

//        model.addAttribute("resumen", resumen);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("departamentoAcademico", anexoBoletin);
//        model.addAttribute("gruposSecciones", allGruposSeccion);
//        model.addAttribute("esOperadorEditor", esOperadorEditor);
//        model.addAttribute("isRevisorActaNotas", isRevisorActaNotas);
        return "academico/programacion/detallesProgramacion";

    }

    @ResponseBody
    @RequestMapping("listGrupo")
    public DynatableResponse listGrupo(DynatableFilter filter, @RequestParam("departamento") Long idDepartamento, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
//            DepartamentoAcademico dpto = new DepartamentoAcademico(idDepartamento);
            AnexoBoletin anexo = new AnexoBoletin(idDepartamento);
            CicloAcademico ciclo = ds.getCicloAcademico();

//            List<Long> anexos = new ArrayList<>();
//            anexos.add(idDepartamento);
//
//            List<DepartamentoCursosProgramadosDTO> counts = new ArrayList<>();
//            counts = service.countGroupsByFilter( anexos, ds.getCicloAcademico(),null);


            List<GrupoSeccion> allGruposSeccion = service.allGrupoSeccionByFilterDyna(ciclo, anexo, filter);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (GrupoSeccion grupo : allGruposSeccion) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("idGrupo", grupo.getId());
                node.put("codigoGrupo", grupo.getCodigo());

                node.put("idCurso", grupo.getCurso().getId());
                node.put("nombreCurso", grupo.getCurso().getNombre());
                node.put("codigo", grupo.getCurso().getCodigo());
                node.put("estructura", grupo.getCurso().getTpc());

                node.put("estado", grupo.getEstado());
                node.put("estadoValue", "");
                if (!StringUtils.isEmpty(grupo.getEstado())) {
                    node.put("estadoValue", EstadoEnum.valueOf(grupo.getEstado()).getValue());
                }
                //    node.put("version", grupo.getVersion());

                node.put("version", grupo.getVersion());
                node.put("estadoPlan", "");
                node.put("estadoPlanValue", "");
                if (grupo.getEstadoPlanEnum() != null) {
                    node.put("estadoPlan", grupo.getEstadoPlanEnum().name());
                    node.put("estadoPlanValue", grupo.getEstadoPlanEnum().getValue());
                }
                node.put("estadoGrupo", "");
                node.put("estadoGrupoValue", "");
                if (grupo.getEstadoGrupoEnum() != null) {
                    node.put("estadoGrupo", grupo.getEstadoGrupoEnum().name());
                    node.put("estadoGrupoValue", grupo.getEstadoGrupoEnum().getValue());
                }

                node.put("fechaCierreActa", TypesUtil.getStringDate(grupo.getFechaCierreActa(), "dd/MM/yyyy"));
                node.put("estadoGrupoCerrado", grupo.isEstadoGrupoCerrado());
                node.put("estadoPlanAceptado", grupo.isEstadoAceptado());

                Long idSeccion = 0L;
                String secciones = "";
                String grupoHoras = "";
                List<DocenteSeccion> docentesSeccion = null;
                List<Docente> docentesPrincipal = new ArrayList();
                List<String> seccionesList = new ArrayList<>();
                List<String> grupoHorasList = new ArrayList<>();

                for (Seccion sec : grupo.getSecciones()) {
                    if (sec.isTipoSeccionPRA() || sec.isTipoSeccionTCUR() || sec.isTipoSeccionTEO()) {
                        idSeccion = sec.getId();
                        seccionesList.add(sec.getId() + "|" + sec.getCodigo2());

                        if (sec.getGrupoHoras() != null) {
                            grupoHorasList.add(sec.getGrupoHoras().getId() + "|" + sec.getGrupoHoras().getCodigo());
                        }

                        docentesSeccion = sec.getDocenteSeccion();
                        for (DocenteSeccion docentesSeccionEach : docentesSeccion) {
                            if (docentesSeccionEach.getEstadoEnum() == SeccionEstadoEnum.ACT &&
                                    docentesSeccionEach.esDocentePrincipal()) {
                                docentesPrincipal.add(docentesSeccionEach.getDocente());
                            }
                        }
                    }
                }

                node.put("seccion", idSeccion);
                node.put("secciones", String.join(",", seccionesList));
                node.put("grupoHoras", String.join(",", grupoHorasList));

                node.put("docenteNombre", "");
                node.put("emailDocente", "");
                //    node.put("idDocente", "");
                if (!docentesPrincipal.isEmpty()) {
                    Docente doc = docentesPrincipal.get(0);
                    node.put("docenteNombre", doc.getPersona().getApellidosNombres());
                    node.put("emailDocente", doc.getPersona().getEmailCompania());
                    //    node.put("idDocente", docentePrincipal.getId());
                }
                array.add(node);

            }
            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

}
