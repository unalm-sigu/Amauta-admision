package pe.edu.lamolina.amauta.controller.migraciones.histomigra;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.croacia.HistoGradMy;
import pe.edu.lamolina.model.croacia.HistoMy;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("migraciones/histomigra")
public class HistoMigraController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HistoMigraService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        return "redirect:/academico/alumno";
    }

    @RequestMapping("{idAlumno}/alumno")
    public String alumno(
            @PathVariable Long idAlumno,
            @RequestParam("origen") String origen,
            Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        Alumno alumno = service.findAlumno(new Alumno(idAlumno));
        model.addAttribute("alumno", createAlumnoJson(alumno));
        model.addAttribute("origen", getOrigen(origen));
        return "migraciones/histomigra/histomigra";
    }

    private String getOrigen(String origen) {
        if (StringUtils.isEmpty(origen)) {
            return "/academico/alumno";
        }
        byte[] decoded = Base64.getMimeDecoder().decode(origen);
        String output = new String(decoded);
        return output;
    }

    @ResponseBody
    @RequestMapping("list/{idAlumno}")
    public DynatableResponse list(@PathVariable Long idAlumno, HttpSession session) {
        DynatableResponse json = new DynatableResponse();

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<HistoMy> historias = service.allHistoByAlumno(new Alumno(idAlumno));
            List<HistoGradMy> historiasGrad = service.allHistoGradByAlumno(new Alumno(idAlumno));

            ArrayNode array = createHistoJson(new Alumno(idAlumno), historias, historiasGrad);

            json.setData(array);
            json.setTotal(historias.size());
            json.setFiltered(historias.size());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("migrarCurso")
    public JsonResponse migrarCurso(@RequestBody HistoGradMy histo, HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.TRUE);
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.migrarCurso(histo, ds);

            Alumno alumno = service.findAlumnoByCodigo(histo.getMatricula());
            List<HistoMy> historias = service.allHistoByAlumno(alumno);
            List<HistoGradMy> historiasGrad = service.allHistoGradByAlumno(alumno);
            ObjectNode data = createHistoJsonByHistograd(alumno, historias, historiasGrad, histo);

            response.setData(data);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private boolean isMovOK(HistoMy histo, AlumnoCicloCurso acc) {
        if (acc.getId() == null) {
            return false;
        }
        String mov = histo.getHistoPK().getMov();
        Boolean registroOk = Arrays.asList("1", "3").contains(mov);
        if (registroOk && acc.getEstadoEnum() == MAT) {
            return true;
        }
        if (!registroOk && acc.getEstadoEnum() != MAT) {
            return true;
        }
        return false;
    }

    private boolean isMovOK(HistoGradMy histo, AlumnoCicloCurso acc) {
        if (acc.getId() == null) {
            return false;
        }
        String mov = histo.getMov();
        Boolean registroOk = Arrays.asList("1", "3").contains(mov);
        if (registroOk && acc.getEstadoEnum() == MAT) {
            return true;
        }
        if (!registroOk && acc.getEstadoEnum() != MAT) {
            return true;
        }
        return false;
    }

    private boolean isCreditosOK(HistoMy histo, AlumnoCicloCurso acc) {
        if (acc.getId() == null) {
            return false;
        }
        return histo.getCurCredit() == acc.getCreditos().intValue();
    }

    private boolean isCreditosOK(HistoGradMy histo, AlumnoCicloCurso acc) {
        if (acc.getId() == null) {
            return false;
        }
        return histo.getCurCredit() == acc.getCreditos().intValue();
    }

    private boolean isNotasOK(HistoMy histo, AlumnoCicloCurso acc) {
        if (acc.getId() == null) {
            return false;
        }
        return histo.getNota().compareTo(acc.getNota()) == 0;
    }

    private boolean isNotasOK(HistoGradMy histo, AlumnoCicloCurso acc) {
        if (acc.getId() == null) {
            return false;
        }
        return histo.getNota().compareTo(acc.getNota()) == 0;
    }

    private AlumnoCicloCurso getAlumnoCursoByHisto(HistoMy histo, List<AlumnoCicloCurso> alumnoCursosTodos, Curso curso) {
        if (curso == null) {
            return null;
        }

        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCurso = TypesUtil.convertListToMapList("curso.id", alumnoCursosTodos);
        List<AlumnoCicloCurso> aluCicloCursoByCurso = TypesUtil.getListNotNull(mapAlumnoCurso.get(curso.getId()));
        if (aluCicloCursoByCurso.isEmpty()) {
            return null;
        }

        String ciclo = histo.getHistoPK().getCiclo();
        String cicloMy = ciclo.substring(0, 4) + (ciclo.endsWith("N") ? "15" : (ciclo.substring(4, 5) + "0"));

        Map<String, List<AlumnoCicloCurso>> mapAlumnoCiclo = TypesUtil.convertListToMapList("alumnoCiclo.cicloAcademico.codigo", aluCicloCursoByCurso);
        List<AlumnoCicloCurso> aluCicloCursoByCiclo = TypesUtil.getListNotNull(mapAlumnoCiclo.get(cicloMy));
        if (aluCicloCursoByCiclo.isEmpty()) {
            return null;
        }

        String mov = histo.getHistoPK().getMov();
        Boolean registroOk = Arrays.asList("1", "3").contains(mov);
        for (AlumnoCicloCurso acc : aluCicloCursoByCiclo) {
            if (acc.getEstadoEnum() == MAT && registroOk) {
                return acc;
            }
            if (acc.getEstadoEnum() != MAT && !registroOk) {
                return acc;
            }
        }
        return aluCicloCursoByCiclo.get(0);

    }

    private AlumnoCicloCurso getAlumnoCursoByHisto(HistoGradMy histo, List<AlumnoCicloCurso> alumnoCursosTodos, Curso curso) {
        if (curso == null) {
            return null;
        }

        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCurso = TypesUtil.convertListToMapList("curso.id", alumnoCursosTodos);
        List<AlumnoCicloCurso> aluCicloCursoByCurso = TypesUtil.getListNotNull(mapAlumnoCurso.get(curso.getId()));
        if (aluCicloCursoByCurso.isEmpty()) {
            return null;
        }

        String ciclo = histo.getCiclo();
        String cicloMy = ciclo.substring(0, 4) + (ciclo.endsWith("N") ? "15" : (ciclo.substring(4, 5) + "0"));

        Map<String, List<AlumnoCicloCurso>> mapAlumnoCiclo = TypesUtil.convertListToMapList("alumnoCiclo.cicloAcademico.codigo", aluCicloCursoByCurso);
        List<AlumnoCicloCurso> aluCicloCursoByCiclo = TypesUtil.getListNotNull(mapAlumnoCiclo.get(cicloMy));
        if (aluCicloCursoByCiclo.isEmpty()) {
            return null;
        }

        String mov = histo.getMov();
        Boolean registroOk = Arrays.asList("1", "3").contains(mov);
        for (AlumnoCicloCurso acc : aluCicloCursoByCiclo) {
            if (acc.getEstadoEnum() == MAT && registroOk) {
                return acc;
            }
            if (acc.getEstadoEnum() != MAT && !registroOk) {
                return acc;
            }
        }
        return aluCicloCursoByCiclo.get(0);

    }

    private ObjectNode createAlumnoJson(Alumno alumno) {
        return JsonHelper.createJson(alumno, JsonNodeFactory.instance,
                new String[]{"id", "codigo", "persona.apellidosNombres"}
        );
    }

    private ArrayNode createHistoJson(Alumno alumno, List<HistoMy> historias, List<HistoGradMy> historiasGrad) {
        List<Curso> cursos = service.allCursosByHisto(historias);
        Map<String, Curso> mapCurso = TypesUtil.convertListToMap("codigoAnterior1", cursos);
        Map<String, List<HistoMy>> mapHisto = TypesUtil.convertListToMapList("histoPK.ciclo", historias);

        List<Curso> cursosGrad = service.allCursosByHistoGrad(historiasGrad);
        Map<String, Curso> mapCursoGrad = TypesUtil.convertListToMap("codigoAnterior1", cursosGrad);
        Map<String, List<HistoGradMy>> mapHistoGrad = TypesUtil.convertListToMapList("ciclo", historiasGrad);

        List<AlumnoCicloCurso> alumnoCursos = service.allAlumnoCursoByAlumno(alumno);

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        String ciclox = "123";
        for (HistoMy histo : historias) {
            histo.setTipoRegistroOracle("histo");
            ObjectNode node = JsonHelper.createJson(histo, JsonNodeFactory.instance, new String[]{"*"});
            node.put("ciclo", histo.getHistoPK().getCiclo());
            node.put("matricula", histo.getHistoPK().getMatricula());
            node.put("curCodigo", histo.getHistoPK().getCurCodigo());
            node.put("mov", histo.getHistoPK().getMov());

            Curso curso = mapCurso.get(histo.getHistoPK().getCurCodigo());
            node.set("curso", JsonHelper.createJson(curso, JsonNodeFactory.instance, new String[]{"codigo", "tpc", "nombre"}));

            AlumnoCicloCurso acc = getAlumnoCursoByHisto(histo, alumnoCursos, curso);
            acc = (acc == null) ? new AlumnoCicloCurso() : acc;
            node.set("aluciclocurso", JsonHelper.createJson(acc, JsonNodeFactory.instance,
                    new String[]{"id", "estado", "creditos", "nota", "registroActivo"}));
            node.put("movOk", isMovOK(histo, acc));
            node.put("notaOk", isNotasOK(histo, acc));
            node.put("creditosOk", isCreditosOK(histo, acc));
            if (acc.getAlumnoCiclo() != null) {
                node.put("estadoCiclo", acc.getAlumnoCiclo().getEstado());
            }

            if (!histo.getHistoPK().getCiclo().equals(ciclox)) {
                node.put("rowspan", mapHisto.get(histo.getHistoPK().getCiclo()).size());
            } else {
                node.put("rowspan", 0);
            }

            node.put("algoFalta", !isMovOK(histo, acc) || !isNotasOK(histo, acc) || !isCreditosOK(histo, acc));
            node.put("migrando", false);

            ciclox = histo.getHistoPK().getCiclo();
            array.add(node);
        }

        ciclox = "123";
        for (HistoGradMy histo : historiasGrad) {
            histo.setTipoRegistroOracle("histo_grad");
            ObjectNode node = JsonHelper.createJson(histo, JsonNodeFactory.instance, new String[]{"*"});
            Curso curso = mapCursoGrad.get(histo.getCurCodigo());
            node.set("curso", JsonHelper.createJson(curso, JsonNodeFactory.instance, new String[]{"codigo", "tpc", "nombre"}));

            AlumnoCicloCurso acc = getAlumnoCursoByHisto(histo, alumnoCursos, curso);
            acc = (acc == null) ? new AlumnoCicloCurso() : acc;
            node.set("aluciclocurso", JsonHelper.createJson(acc, JsonNodeFactory.instance,
                    new String[]{"id", "estado", "creditos", "nota", "registroActivo"}));
            node.put("movOk", isMovOK(histo, acc));
            node.put("notaOk", isNotasOK(histo, acc));
            node.put("creditosOk", isCreditosOK(histo, acc));
            if (acc.getAlumnoCiclo() != null) {
                node.put("estadoCiclo", acc.getAlumnoCiclo().getEstado());
            }

            if (!histo.getCiclo().equals(ciclox)) {
                node.put("rowspan", mapHistoGrad.get(histo.getCiclo()).size());
            } else {
                node.put("rowspan", 0);
            }

            node.put("algoFalta", !isMovOK(histo, acc) || !isNotasOK(histo, acc) || !isCreditosOK(histo, acc));
            node.put("migrando", false);

            ciclox = histo.getCiclo();
            array.add(node);
        }

        return array;
    }

    private ObjectNode createHistoJsonByHistograd(Alumno alumno, List<HistoMy> historias, List<HistoGradMy> historiasGrad, HistoGradMy histoGrad) {
        List<Curso> cursos = service.allCursosByHisto(historias);
        Map<String, Curso> mapCurso = TypesUtil.convertListToMap("codigoAnterior1", cursos);
        Map<String, List<HistoMy>> mapHisto = TypesUtil.convertListToMapList("histoPK.ciclo", historias);

        List<Curso> cursosGrad = service.allCursosByHistoGrad(historiasGrad);
        Map<String, Curso> mapCursoGrad = TypesUtil.convertListToMap("codigoAnterior1", cursosGrad);
        Map<String, List<HistoGradMy>> mapHistoGrad = TypesUtil.convertListToMapList("ciclo", historiasGrad);

        List<AlumnoCicloCurso> alumnoCursos = service.allAlumnoCursoByAlumno(alumno);

        //ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        String ciclox = "123";
        for (HistoMy histo : historias) {
            histo.setTipoRegistroOracle("histo");
            ObjectNode node = JsonHelper.createJson(histo, JsonNodeFactory.instance, new String[]{"*"});
            node.put("ciclo", histo.getHistoPK().getCiclo());
            node.put("matricula", histo.getHistoPK().getMatricula());
            node.put("curCodigo", histo.getHistoPK().getCurCodigo());
            node.put("mov", histo.getHistoPK().getMov());

            Curso curso = mapCurso.get(histo.getHistoPK().getCurCodigo());
            node.set("curso", JsonHelper.createJson(curso, JsonNodeFactory.instance, new String[]{"codigo", "tpc", "nombre"}));

            AlumnoCicloCurso acc = getAlumnoCursoByHisto(histo, alumnoCursos, curso);
            acc = (acc == null) ? new AlumnoCicloCurso() : acc;
            node.set("aluciclocurso", JsonHelper.createJson(acc, JsonNodeFactory.instance,
                    new String[]{"id", "estado", "creditos", "nota", "registroActivo"}));
            node.put("movOk", isMovOK(histo, acc));
            node.put("notaOk", isNotasOK(histo, acc));
            node.put("creditosOk", isCreditosOK(histo, acc));
            if (acc.getAlumnoCiclo() != null) {
                node.put("estadoCiclo", acc.getAlumnoCiclo().getEstado());
            }

            if (!histo.getHistoPK().getCiclo().equals(ciclox)) {
                node.put("rowspan", mapHisto.get(histo.getHistoPK().getCiclo()).size());
            } else {
                node.put("rowspan", 0);
            }

            node.put("algoFalta", !isMovOK(histo, acc) || !isNotasOK(histo, acc) || !isCreditosOK(histo, acc));
            node.put("migrando", false);

            ciclox = histo.getHistoPK().getCiclo();
            if (sonMismoRegistro(histoGrad, histoGrad)) {
                return node;
            }
        }

        ciclox = "123";
        for (HistoGradMy histo : historiasGrad) {
            histo.setTipoRegistroOracle("histo_grad");
            ObjectNode node = JsonHelper.createJson(histo, JsonNodeFactory.instance, new String[]{"*"});
            Curso curso = mapCursoGrad.get(histo.getCurCodigo());
            node.set("curso", JsonHelper.createJson(curso, JsonNodeFactory.instance, new String[]{"codigo", "tpc", "nombre"}));

            AlumnoCicloCurso acc = getAlumnoCursoByHisto(histo, alumnoCursos, curso);
            acc = (acc == null) ? new AlumnoCicloCurso() : acc;
            node.set("aluciclocurso", JsonHelper.createJson(acc, JsonNodeFactory.instance,
                    new String[]{"id", "estado", "creditos", "nota", "registroActivo"}));
            node.put("movOk", isMovOK(histo, acc));
            node.put("notaOk", isNotasOK(histo, acc));
            node.put("creditosOk", isCreditosOK(histo, acc));
            if (acc.getAlumnoCiclo() != null) {
                node.put("estadoCiclo", acc.getAlumnoCiclo().getEstado());
            }

            if (!histo.getCiclo().equals(ciclox)) {
                node.put("rowspan", mapHistoGrad.get(histo.getCiclo()).size());
            } else {
                node.put("rowspan", 0);
            }

            node.put("algoFalta", !isMovOK(histo, acc) || !isNotasOK(histo, acc) || !isCreditosOK(histo, acc));
            node.put("migrando", false);

            ciclox = histo.getCiclo();
            if (sonMismoRegistro(histoGrad, histo)) {
                return node;
            }
        }

        return null;
    }

    private boolean sonMismoRegistro(HistoGradMy histoGrad, HistoMy histo) {
        if (histoGrad.getMatricula().equals(histo.getHistoPK().getMatricula())
                && histoGrad.getCiclo().equals(histo.getHistoPK().getCiclo())
                && histoGrad.getCurCodigo().equals(histo.getHistoPK().getCurCodigo())
                && histoGrad.getMov().equals(histo.getHistoPK().getMov())) {
            return true;
        }
        return false;
    }

    private boolean sonMismoRegistro(HistoGradMy histoGrad, HistoGradMy histo) {
        if (histoGrad.getMatricula().equals(histo.getMatricula())
                && histoGrad.getCiclo().equals(histo.getCiclo())
                && histoGrad.getCurCodigo().equals(histo.getCurCodigo())
                && histoGrad.getMov().equals(histo.getMov())) {
            return true;
        }
        return false;
    }

}
