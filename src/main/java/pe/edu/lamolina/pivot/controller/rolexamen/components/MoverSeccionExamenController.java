package pe.edu.lamolina.pivot.controller.rolexamen.components;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.pivot.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("rolexamen/moverseccionexamen")
public class MoverSeccionExamenController {

    @Autowired
    MoverSeccionExamenService moverSeccionExamenService;

    @Autowired
    RolExamenesLogger rolExamenesLogger;

    @ResponseBody
    @RequestMapping(value = "listTipoGrupoRolExamenes", method = RequestMethod.GET)
    public JsonResponse listTipoGrupoRolExamenes(HttpSession session, HttpServletRequest request) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            TipoGrupoRolExamenesEnum[] tiposGrupoRolExamenes = TipoGrupoRolExamenesEnum.values();

            JsonNodeFactory jc = JsonNodeFactory.instance;
            ArrayNode jTiposGrupoRolExamenes = new ArrayNode(jc);
            for (TipoGrupoRolExamenesEnum tipoGrupoRolExamenes : tiposGrupoRolExamenes) {
                if (tipoGrupoRolExamenes.equals(TipoGrupoRolExamenesEnum.GRU_ESP)) {
                    continue;
                }
                jTiposGrupoRolExamenes.add(JsonHelper.createJson(tipoGrupoRolExamenes, jc, false,
                        new String[]{
                            "*"
                        }
                ));
            }
            response.setData(jTiposGrupoRolExamenes);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "loadComponent", method = RequestMethod.GET)
    public JsonResponse loadComponent(HttpSession session, HttpServletRequest request,
            @RequestParam("seccion") Long seccionId,
            @RequestParam("tipoOrigen") String tipoOrigen,
            @RequestParam("rolExamenes") Long rolExamenesId) {
        
        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.TRUE);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            Seccion seccion = new Seccion(seccionId);
            RolExamenes rol = new RolExamenes(rolExamenesId);
            TipoGrupoRolExamenesEnum tipoGrupoRolExamenesEnumDefault = tipoGrupoRolExamenesEnumDefault = TipoGrupoRolExamenesEnum.GRU_REG;
            JsonNodeFactory jc = JsonNodeFactory.instance;
            ObjectNode data = new ObjectNode(jc);
            if (TipoGrupoRolExamenesEnum.CUR_MAS.name().equals(tipoOrigen)) {
                SeccionCursoMasivo seccionCursoMasivo = moverSeccionExamenService.findSeccionCursoMasivoBySeccionRolExamenes(seccion, rol);
                ObjectNode jSeccionRolExamenes = JsonHelper.createJson(seccionCursoMasivo, jc, false, new String[]{
                    "*",
                    "seccion.*",
                    "cursoMasivoExamen.*",
                    "cursoMasivoExamen.grupoHorasExamen.*",
                    "cursoMasivoExamen.curso.*"
                });
                jSeccionRolExamenes.set("grupoHorasExamen", JsonHelper.createJson(seccionCursoMasivo.getCursoMasivoExamen().getGrupoHorasExamen(), jc, false, new String[]{
                    "*",
                    "grupoHoras.*",
                    "horaInicio.*",
                    "horaFin.*"
                }));
                data.set("seccionRolExamenes", jSeccionRolExamenes);
                tipoGrupoRolExamenesEnumDefault = null;
            } else if (TipoGrupoRolExamenesEnum.GRU_ESP.name().equals(tipoOrigen)) {
                SeccionGrupoEspecial seccionGrupoEspecial = moverSeccionExamenService.findSeccionGrupoEspecialBySeccionRolExamenes(seccion, rol);
                ObjectNode jSeccionRolExamenes = JsonHelper.createJson(seccionGrupoEspecial, jc, false, new String[]{
                    "*",
                    "seccion.*",
                    "grupoHorasExamen.*",
                    "grupoHorasExamen.grupoHoras.*",
                    "grupoHorasExamen.horaInicio.*",
                    "grupoHorasExamen.horaFin.*"
                });
                data.set("seccionRolExamenes", jSeccionRolExamenes);
            } else if (TipoGrupoRolExamenesEnum.GRU_REG.name().equals(tipoOrigen)) {
                SeccionGrupoRegular seccionGrupoRegular = moverSeccionExamenService.findSeccionGrupoRegularBySeccionRolExamenes(seccion, rol);
                ObjectNode jSeccionRolExamenes = JsonHelper.createJson(seccionGrupoRegular, jc, false, new String[]{
                    "*",
                    "seccion.*",
                    "letraGrupoRegular.*",
                    "letraGrupoRegular.grupoHorasExamen.*"
                });
                jSeccionRolExamenes.set("grupoHorasExamen", JsonHelper.createJson(seccionGrupoRegular.getLetraGrupoRegular().getGrupoHorasExamen(), jc, false, new String[]{
                    "*",
                    "grupoHoras.*",
                    "horaInicio.*",
                    "horaFin.*"
                }));
                data.set("seccionRolExamenes", jSeccionRolExamenes);
            }
            if (tipoGrupoRolExamenesEnumDefault != null) {
                data.set("tipoGrupoRolExamenesEnumDefault", JsonHelper.createJson(tipoGrupoRolExamenesEnumDefault, jc, false, new String[]{
                    "*"
                }));
            }
            response.setData(data);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "cambiarTipoDestinoGrupo/{tipoDestino}", method = RequestMethod.POST)
    public JsonResponse cambiarTipoDestinoGrupo(HttpSession session, HttpServletRequest request,
            @PathVariable("tipoDestino") String tipoDestino,
            @RequestBody GrupoHorasExamen grupoHorasExamenOrigen) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.TRUE);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            JsonNodeFactory jc = JsonNodeFactory.instance;

            grupoHorasExamenOrigen = moverSeccionExamenService.findGrupoHorasExamen(grupoHorasExamenOrigen);

            ObjectNode data = new ObjectNode(jc);
            if (TipoGrupoRolExamenesEnum.CUR_MAS.name().equals(tipoDestino)) {
                List<CursoMasivoExamen> cursosMasivosExamen = moverSeccionExamenService.allActiveCursosMasivosByRolExamenes(grupoHorasExamenOrigen.getRolExamenes());
                ArrayNode jCursosMasivosExamen = new ArrayNode(jc);

                for (CursoMasivoExamen cursoMasivoExamen : cursosMasivosExamen) {
                    ObjectNode jCursoMasivoExamen = JsonHelper.createJson(cursoMasivoExamen, jc, false, new String[]{
                        "*",
                        "curso.*",
                        "grupoHorasExamen.*",
                        "grupoHorasExamen.grupoHoras.*",
                        "grupoHorasExamen.horaInicio.*",
                        "grupoHorasExamen.horaFin.*"
                    });
                    jCursosMasivosExamen.add(jCursoMasivoExamen);
                }
                data.set("jCursosMasivosExamen", jCursosMasivosExamen);
            } else if (TipoGrupoRolExamenesEnum.GRU_REG.name().equals(tipoDestino)) {
                List<LetraGrupoRegular> letrasGrupoRegular = moverSeccionExamenService.allLetrasGruposRegularesByRolExamenes(grupoHorasExamenOrigen.getRolExamenes());
                ArrayNode jLetrasGrupoRegular = new ArrayNode(jc);
                for (LetraGrupoRegular letraGrupoRegular : letrasGrupoRegular) {
                    ObjectNode jLetraGrupoRegular = JsonHelper.createJson(letraGrupoRegular, jc, false, new String[]{
                        "*",
                        "grupoHorasExamen.*",
                        "grupoHorasExamen.grupoHoras.*",
                        "grupoHorasExamen.horaInicio.*",
                        "grupoHorasExamen.horaFin.*"
                    });
                    jLetrasGrupoRegular.add(jLetraGrupoRegular);
                }
                data.set("jLetrasGrupoRegular", jLetrasGrupoRegular);
            }

            response.setData(data);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "cambioHorarioExamenSeccion", method = RequestMethod.POST)
    public JsonResponse cambioHorarioExamenSeccion(HttpSession session, HttpServletRequest request,
            @RequestBody CambioHorarioExamenSeccion cambioHorarioExamenSeccion) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.TRUE);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        ds.setFechaAccionAudit(new Date());
        try {
            JsonNodeFactory jc = JsonNodeFactory.instance;
            moverSeccionExamenService.cambioHorarioExamenSeccion(cambioHorarioExamenSeccion, ds);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            rolExamenesLogger.finalizeLog();
        }
        return response;
    }

}
