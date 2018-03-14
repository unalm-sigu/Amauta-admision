package pe.edu.lamolina.pivot.controller.encuesta.respuesta;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaPostulanteDAO;
import pe.edu.lamolina.pivot.dao.encuesta.OpcionPreguntaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PreguntaExamenDAO;
import pe.edu.lamolina.pivot.dao.encuesta.hibernate.ExamenVirtualDAOH;
import static pe.edu.lamolina.model.enums.TipoPreguntaEncuestaEnum.ABIERTA;
import static pe.edu.lamolina.model.enums.TipoPreguntaEncuestaEnum.MULTIPLE;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.EncuestaPostulante;

@Service
@Transactional(readOnly = true)
public class RespuestaEncuestaServiceImp implements RespuestaEncuestaService {

    @Autowired
    EncuestaPostulanteDAO encuestaPostulanteDAO;
    @Autowired
    PreguntaExamenDAO preguntaExamenDAO;
    @Autowired
    OpcionPreguntaDAO opcionPreguntaDAO;
    @Autowired
    ExamenVirtualDAOH examenVirtualDAOH;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ExamenVirtual findEncuestaActivaByCiclo(CicloPostula ciclo) {
        return examenVirtualDAOH.findEncuestaActivaByCiclo(ciclo);
    }

    @Override
    public PreguntaExamen findPregunta(Long idPregunta) {
        return preguntaExamenDAO.find(idPregunta);
    }

    @Override
    public List<RespuestaItem> allResumenRespuestasOtro(DynatableFilter filter, CicloPostula ciclo) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return new ArrayList();
        }

        PreguntaExamen pregunta = null;
        OpcionPregunta opcion = null;
        for (String key : queries.keySet()) {
            if (key.equals("pregunta")) {
                String idPregunta = (String) queries.get(key);
                pregunta = preguntaExamenDAO.find(Long.valueOf(idPregunta));
            }
            if (key.equals("opcion")) {
                String idOpcion = (String) queries.get(key);
                opcion = opcionPreguntaDAO.find(Long.valueOf(idOpcion));
            }
        }
        if (pregunta == null) {
            return new ArrayList();
        }

        if (!Arrays.asList(ABIERTA, MULTIPLE).contains(pregunta.getTipoEnum()) && opcion == null) {
            return new ArrayList();
        }

        if (Arrays.asList(ABIERTA, MULTIPLE).contains(pregunta.getTipoEnum())) {
            opcion = null;
        }

        List<EncuestaPostulante> respuestas = encuestaPostulanteDAO.allByPreguntaOpcionCiclo(pregunta, opcion, ciclo);
        Map<String, List<EncuestaPostulante>> mapRespuestaOtro;

        if (pregunta.getTipoEnum() == ABIERTA) {
            mapRespuestaOtro = createMapOtros(respuestas);
        } else {
            mapRespuestaOtro = createMapOtros(respuestas);
        }

        List<RespuestaItem> respuetasItem = new ArrayList();
        SortedSet<String> otros = new TreeSet(mapRespuestaOtro.keySet());
        for (String otro : otros) {
            List<EncuestaPostulante> respuestasOtro = mapRespuestaOtro.get(otro);
            RespuestaItem rpta = new RespuestaItem(otro, respuestasOtro.size());
            respuetasItem.add(rpta);
        }

        return respuetasItem;
    }

    private Map<String, List<EncuestaPostulante>> createMapOtros(List<EncuestaPostulante> respuestas) {

        Map<String, List<EncuestaPostulante>> mapRespuestaOtro = new LinkedHashMap();
        for (EncuestaPostulante respuesta : respuestas) {
            String otro = respuesta.getRespuestaOtro();
            if (Strings.isNullOrEmpty(otro)) {
                continue;
            }
            List<EncuestaPostulante> respuestasOtros = mapRespuestaOtro.get(otro);
            if (respuestasOtros == null) {
                respuestasOtros = new ArrayList();
                mapRespuestaOtro.put(otro, respuestasOtros);
            }
            respuestasOtros.add(respuesta);
        }
        return mapRespuestaOtro;
    }

    @Override
    public List<PreguntaExamen> allPreguntasOtros(ExamenVirtual encuesta) {
        return preguntaExamenDAO.allWithOtrosByEncuesta(encuesta);
    }

    @Override
    public List<OpcionPregunta> allOpcionesOtrosByPregunta(PreguntaExamen pregunta) {
        if (pregunta.getTipoEnum() == ABIERTA) {
            return new ArrayList();
        }
        if (pregunta.getTipoEnum() == MULTIPLE) {
            return new ArrayList();
        }
        return opcionPreguntaDAO.allOtrosByPregunta(pregunta);
    }

    @Override
    @Transactional
    public void unirFrases(OpcionPregunta opcion, CicloPostula ciclo) {
        String permanece = opcion.getIndiceFrase() == 0 ? opcion.getFrase0() : opcion.getFrase1();
        String modifica = opcion.getIndiceFrase() == 0 ? opcion.getFrase1() : opcion.getFrase0();

        encuestaPostulanteDAO.unificarFrases(opcion, permanece, modifica, ciclo);
    }

    @Override
    @Transactional
    public void modificarFrase(OpcionPregunta opcion, CicloPostula ciclo) {
        String permanece = opcion.getFrase1();
        String modifica = opcion.getFrase0();

        encuestaPostulanteDAO.unificarFrases(opcion, permanece, modifica, ciclo);
    }

}
