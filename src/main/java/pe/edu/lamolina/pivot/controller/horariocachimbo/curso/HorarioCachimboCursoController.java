package pe.edu.lamolina.pivot.controller.horariocachimbo.curso;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCachimbos;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.horario.SeccionCursoCachimbos;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/horariocachimbo/curso")
public class HorarioCachimboCursoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HorarioCachimboCursoService service;

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
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        ModalidadEstudio modalidadEstudio = new ModalidadEstudio(1);
        List<CarreraCursoCachimbo> carreras = service.allCarrera(modalidadEstudio, cicloAcademico);
        model.addAttribute("cicloAcademico", cicloAcademico);
        model.addAttribute("carreras", carreras);
        return "academico/horariocachimbo/curso/horarioCachimboCurso";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            logger.debug("cicloAcademico {} {}", cicloAcademico.getId(), cicloAcademico.getDescripcion());
            List<CursoCachimbos> cursoCachimbos = service.allCursoCachimbos(filter, cicloAcademico);
            Map<Long, Map<Long, HorarioCachimbos>> carsoHorarioCachimbosMap = service.allSeccionHorarioCachimbos(cursoCachimbos, cicloAcademico);
            service.fillGrupoSeccion(cursoCachimbos, cicloAcademico);
            List<SeccionCursoCachimbos> seccionCursoCachimbos = service.allCursoCachimbos(cursoCachimbos);
            Map<Long, List<SeccionCursoCachimbos>> seccionCursoCachimbosMapByCurso = TypesUtil.convertListToMapList("cursoCachimbos.id", seccionCursoCachimbos);

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jsonFactory);

            for (CursoCachimbos cursoCachimbo : cursoCachimbos) {

                ObjectNode node = new ObjectNode(jsonFactory);

                Curso curso = cursoCachimbo.getCurso();
                Carrera carrera = cursoCachimbo.getCarrera();
                Facultad facultad = carrera.getFacultad();
                DepartamentoAcademico departamento = curso.getDepartamentoAcademico();

                node.put("id", cursoCachimbo.getId());
                node.put("codigo", curso.getCodigo());
                node.put("nombre", curso.getNombre());
                node.put("carrera", carrera.getNombre());
                node.put("facultad", facultad.getNombre());
                node.put("departamentoAcademico", departamento.getNombre());
                node.put("curso", curso.getNombre());
                node.put("tpc", curso.getTpc());
                node.put("oferta", cursoCachimbo.getOferta());
                node.put("demanda", cursoCachimbo.getDemanda());
                node.put("ofertaTotal", cursoCachimbo.getOfertaTotal());
                node.put("demandaTotal", cursoCachimbo.getDemandaTotal());
//                node.put("errorDemanda", cursoCachimbo.getDemanda() > cursoCachimbo.getOferta());

                node.put("showfacultad", !facultad.getCodigo().equals(carrera.getCodigo()));

                Map<Long, HorarioCachimbos> horarios = carsoHorarioCachimbosMap.get(curso.getId());
                node.put("horarios", horarios != null ? horarios.size() : 0);

                List<SeccionCursoCachimbos> seccionCursoCachimbosByCurso = seccionCursoCachimbosMapByCurso.get(cursoCachimbo.getId());
                if (seccionCursoCachimbosByCurso == null || seccionCursoCachimbosByCurso.isEmpty()) {
                    seccionCursoCachimbosByCurso = new ArrayList();
                }
                Map<Long, SeccionCursoCachimbos> seccionCursoCachimbosMapBySeccion = TypesUtil.convertListToMap("seccion.id", seccionCursoCachimbosByCurso);

                List<GrupoSeccion> gruposSeccion = curso.getGrupoSeccion();

                ArrayNode gruposSeccionArray = new ArrayNode(jsonFactory);

                for (GrupoSeccion grupoSeccion : gruposSeccion) {
                    ObjectNode grupoSeccionNode = new ObjectNode(jsonFactory);
                    ArrayNode seccionesArray = new ArrayNode(jsonFactory);

                    for (Seccion seccion : grupoSeccion.getSecciones()) {

                        SeccionCursoCachimbos scc = null;
                        if (seccionCursoCachimbosMapBySeccion.size() > 0) {
                            scc = seccionCursoCachimbosMapBySeccion.get(seccion.getId());
                        }
                        ObjectNode seccionNode = new ObjectNode(jsonFactory);
                        seccionNode.put("id", seccion.getId());
                        seccionNode.put("codigo2", seccion.getCodigo2());
                        seccionNode.put("codigo", seccion.getCodigo());
                        seccionNode.put("tipo", seccion.getTipoSeccion());
                        seccionNode.put("suscritos", seccion.getReservados());
                        seccionNode.put("vacantes", seccion.getVacantesDisponibles());
                        seccionNode.put("seleccionado", scc != null);
                        seccionNode.put("anexo", grupoSeccion.getAnexoBoletin().getNombre());
                        seccionesArray.add(seccionNode);
                    }

                    int cant = (grupoSeccion.getSecciones().size() > 12) ? 12 : grupoSeccion.getSecciones().size();
                    grupoSeccionNode.put("cantidadSecciones", cant);
                    grupoSeccionNode.put("secciones", seccionesArray);
                    gruposSeccionArray.add(grupoSeccionNode);
                }

                node.put("grupos", gruposSeccionArray);

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
    @RequestMapping("addcurso")
    public JsonResponse addCurso(
            @RequestParam("curso.id") ArrayList<Long> cursos,
            @RequestParam("carrera.id") Long carrera, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            logger.debug("curso {} ", cursos.size());
            Usuario user = ds.getUsuario();
            for (Long curso : cursos) {
                logger.debug("curso long1 {} ", curso);
                CursoCachimbos cursoCachimbos = new CursoCachimbos();
                cursoCachimbos.setCicloAcademico(cicloAcademico);
                cursoCachimbos.setCarrera(new Carrera(carrera));
                cursoCachimbos.setIdUserCreacion(user.getId());
                cursoCachimbos.setCurso(new Curso(curso));
                cursoCachimbos.setClaves(0);
                cursoCachimbos.setHorarios(0);
                service.addCurso(cursoCachimbos);
            }
            response.setMessage("Curso agregado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(CursoCachimbos cursoCachimbos) {
        JsonResponse response = new JsonResponse();
        try {
            service.delete(cursoCachimbos);
            response.setMessage("Curso eliminado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("searchcurso")
    public JsonResponse searchCurso(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            List<Curso> cursos = service.allCursoByName(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            for (Curso curso : cursos) {
                ObjectNode json = new ObjectNode(jsonFactory);
                json.put("id", curso.getId());
                json.put("curso", curso.getNombre());
                json.put("codigo", curso.getCodigo());
                json.put("tpc", curso.getTpc());
                json.put("creditos", curso.getCreditos());
                json.put("departamento", (String) ObjectUtil.getParentTree(curso, "departamentoAcademico.nombre"));
                json.put("facultad", (String) ObjectUtil.getParentTree(curso, "departamentoAcademico.facultad.nombre"));
                json.put("especialidad", (String) ObjectUtil.getParentTree(curso, "carrera.nombre"));
                json.put("tipoEspecialidad", (String) ObjectUtil.getParentTree(curso, "carrera.tipoEnum.value"));
                jsonList.add(json);
            }
            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("searchcarrera")
    public JsonResponse searchCarrera(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            ModalidadEstudio modalidadEstudio = new ModalidadEstudio(1);
            List<Carrera> carreras = service.allCarreraByName(nombre, modalidadEstudio);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Carrera carrera : carreras) {
                ObjectNode json = new ObjectNode(jsonFactory);
                json.put("id", carrera.getId());
                json.put("nombre", carrera.getNombre());
                json.put("codigo", carrera.getCodigo());
                json.put("facultad", carrera.getFacultad().getNombre());
                jsonList.add(json);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("updateseccioncursocachimbo")
    public JsonResponse updateSeccionCursoCachimbo(CarreraCursoCachimbo carreraCursoCachimbo, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Usuario usuario = ds.getUsuario();
            service.updateSeccionCursoCachimbo(carreraCursoCachimbo, usuario);
            response.setMessage("Curso actualizado satisfactoriamente");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
