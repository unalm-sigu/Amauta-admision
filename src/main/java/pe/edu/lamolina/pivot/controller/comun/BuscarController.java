package pe.edu.lamolina.pivot.controller.comun;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Ubicacion;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("comun/buscar")
public class BuscarController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BuscarService buscarService;

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

    @ResponseBody
    @RequestMapping("cursosSCA")
    public DynatableResponse cursosSCA(DynatableFilter filter,
            @RequestParam("nombre") String nombre,
            @RequestParam("planCalificacion") Long idPlanCalifica,
            HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = ds.getCicloAcademico();
            List<Curso> cursos = buscarService.allCursosSCA(nombre, new PlanCalificacion(idPlanCalifica), ciclo);

            for (Curso curso : cursos) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", curso.getId());
                node.put("codigo", curso.getCodigo());
                node.put("nombre", curso.getNombre());
                node.put("departamentoAcademico", curso.getDepartamentoAcademico() != null ? curso.getDepartamentoAcademico().getNombre() : "");
                node.put("sistemaCalificacion", (String) ObjectUtil.getParentTree(curso, "planCalificacion.codigo"));
                node.put("tpc", curso.getTipoCurso());
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
    @RequestMapping("allDistritos")
    public JsonResponse allDistritos(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<Ubicacion> ubicaciones = buscarService.allDistritosByName(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Ubicacion ubicacion : ubicaciones) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", ubicacion.getId());
                Ubicacion provincia = ubicacion.getUbicacionSuperior();
                Ubicacion departamento = provincia.getUbicacionSuperior();

                json.put("distrito", ubicacion.getNombre());
                json.put("provincia", provincia.getNombre());
                json.put("departamento", departamento.getNombre());
                json.put("nombre", ubicacion.getDistrito());
                json.put("distrito", ubicacion.getDistrito());

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
    @RequestMapping("allDepartamentoAcademico")
    public JsonResponse allDepartamentoAcademico(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<DepartamentoAcademico> departamentos = buscarService.allDepartamentosByName(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (DepartamentoAcademico departamento : departamentos) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", departamento.getId());
                json.put("nombre", departamento.getNombre());
                json.put("facultad", departamento.getFacultad().getNombre());

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
    @RequestMapping("allCoordinadores")
    public JsonResponse allCoordinadoresByDpto(@RequestParam("dpto") Long dpto, @RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<Docente> coordinadores = buscarService.allCoordinadoresByIdDptoName(dpto, nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Docente coordinador : coordinadores) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", coordinador.getId());
                json.put("nombre", coordinador.getPersona().getNombreCompleto());

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
    @RequestMapping("allPaises")
    public JsonResponse allPaises(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<Pais> paises = buscarService.allPaisesByName(nombre);
            for (Pais pais : paises) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", pais.getId());
                json.put("nombre", pais.getNombre());
                json.put("codigo", pais.getCodigo());

                jsonList.add(json);
            }
            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("allEmpresa")
    public JsonResponse allEmpresa(Long idPais,
            @RequestParam("nombre") String nombre,
            HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<Empresa> empresas = buscarService.allEmpresaByName(new Pais(idPais), nombre);
            for (Empresa empresa : empresas) {
                ObjectNode json = new ObjectNode(jsonFactory);
                json.put("id", empresa.getId());
                json.put("razonSocial", empresa.getRazonSocial());
                jsonList.add(json);
            }
            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("allUniversidad")
    public JsonResponse allUniversidad(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<Universidad> universidades = buscarService.allUniversidadByName(nombre);
            for (Universidad universidad : universidades) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", universidad.getId());
                json.put("nombre", universidad.getNombre());
                json.put("codigo", universidad.getSiglas() == null ? "" : universidad.getSiglas());

                jsonList.add(json);
            }
            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
