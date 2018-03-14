package pe.edu.lamolina.pivot.controller.encuesta.pregunta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.dao.encuesta.OpcionPreguntaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ExamenVirtualDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PreguntaExamenDAO;
import pe.edu.lamolina.model.enums.EstadoOpcionPreguntaEnum;
import pe.edu.lamolina.model.enums.ExamenVirtualEstadoEnum;
import pe.edu.lamolina.model.enums.PreguntaEstadoEnum;
import pe.edu.lamolina.model.enums.TipoPreguntaEncuestaEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PreguntaEncuestaServiceImp implements PreguntaEncuestaService {

    @Autowired
    PreguntaExamenDAO preguntaExamenDAO;
    @Autowired
    OpcionPreguntaDAO opcionPreguntaDAO;
    @Autowired
    ExamenVirtualDAO evaluacionVirtualDAO;
    @Autowired
    ExamenVirtualDAO examenVirtualDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<PreguntaExamen> allPreguntaEvaluacionVirtual(DynatableFilter filter, ExamenVirtual encuesta) {
        List<PreguntaExamen> preguntas = preguntaExamenDAO.allForEncuestaByDynatable(filter, encuesta);
        List<OpcionPregunta> opciones = opcionPreguntaDAO.allByPreguntas(preguntas);
        Map<Long, List<OpcionPregunta>> mapOpciones = TypesUtil.convertListToMapList("pregunta.id", opciones);

        for (PreguntaExamen pregunta : preguntas) {
            pregunta.setOpcionPregunta(mapOpciones.get(pregunta.getId()));
            if (pregunta.getOpcionPregunta() == null) {
                pregunta.setOpcionPregunta(new ArrayList());
            }
        }
        return preguntas;
    }

    @Override
    @Transactional
    public void savePregunta(PreguntaExamen pregunta, DataSessionPivot ds) {
        ExamenVirtual encuesta = examenVirtualDAO.find(pregunta.getExamenVirtual().getId());
        if (encuesta.getEstadoEnum() != ExamenVirtualEstadoEnum.CRE) {
            throw new PhobosException("No puede modificar el contenido de una encuesta Activa o Inactiva");
        }

        pregunta.setEstado(PreguntaEstadoEnum.ACT);

        PreguntaExamen preguntaTopNumero = preguntaExamenDAO.findMayorNumero(pregunta.getExamenVirtual());
        Integer mayorNumero = 1;

        if (preguntaTopNumero != null) {
            mayorNumero = preguntaTopNumero.getNumero() + 1;
        }

        pregunta.setNumero(mayorNumero);
        pregunta.setFechaCreacion(new Date());
        pregunta.setUserCreacion(ds.getUsuario());

        pregunta.setRespuestaMultiple(0);
        if (pregunta.getTipo().equals(TipoPreguntaEncuestaEnum.MULTIPLE.name())) {
            pregunta.setRespuestaMultiple(1);
        }

        preguntaExamenDAO.save(pregunta);

        encuesta.setPreguntasDisponibles(encuesta.getPreguntasDisponibles() + 1);
        encuesta.setPreguntasVisibles(encuesta.getPreguntasVisibles() + 1);
        examenVirtualDAO.update(encuesta);

        ObjectUtil.eliminarAttrSinId(pregunta, "opcionReferenciaTrans");
        List<OpcionPregunta> opciones = pregunta.getOpcionPregunta();

        for (OpcionPregunta opcion : opciones) {
            Integer indice = opciones.indexOf(opcion);
            if (indice > 25) {
                continue;
            }
            String letra = this.getChartFromInt(indice);
            opcion.setLetra(letra);
            opcion.setNumero(++indice);
            opcion.setPregunta(pregunta);
            opcion.setEstado(EstadoOpcionPreguntaEnum.ACT.name());
            opcionPreguntaDAO.save(opcion);
        }

    }

    private String getChartFromInt(int indice) {
        String abecedario = "abcdefghijklmnopqrstuvwxyz";
        return Character.toString(abecedario.charAt(indice));
    }

    @Override
    @Transactional
    public void updatePregunta(PreguntaExamen pregunta, DataSessionPivot ds) {
        ExamenVirtual encuesta = examenVirtualDAO.find(pregunta.getExamenVirtual().getId());
        if (encuesta.getEstadoEnum() != ExamenVirtualEstadoEnum.CRE) {
            throw new PhobosException("No puede modificar el contenido de una encuesta Activa o Inactiva");
        }

        PreguntaExamen preguntaBD = preguntaExamenDAO.find(pregunta.getId());
        preguntaBD.setTexto(pregunta.getTexto());
        preguntaBD.setTipo(pregunta.getTipo());
        preguntaBD.setOpcionReferencia(pregunta.getOpcionReferencia());
        ObjectUtil.eliminarAttrSinId(preguntaBD, "opcionReferencia");

        preguntaExamenDAO.update(preguntaBD);
        List<OpcionPregunta> opcionesBD = opcionPreguntaDAO.allByPregunta(preguntaBD);
        Map<String, OpcionPregunta> mapOpciones = TypesUtil.convertListToMap("letra", opcionesBD);

        preguntaBD.setRespuestaMultiple(0);
        if (preguntaBD.getTipo().equals(TipoPreguntaEncuestaEnum.MULTIPLE.name())) {
            preguntaBD.setRespuestaMultiple(1);
        }

        List<OpcionPregunta> opciones = pregunta.getOpcionPregunta();
        for (OpcionPregunta opcion : opciones) {
            Integer indice = opciones.indexOf(opcion);
            if (indice > 25) {
                continue;
            }
            String letra = this.getChartFromInt(indice);
            OpcionPregunta opcionBD = mapOpciones.get(letra);

            if (opcionBD == null) {
                opcion.setLetra(letra);
                opcion.setNumero(++indice);
                opcion.setPregunta(preguntaBD);
                opcion.setEstado(EstadoOpcionPreguntaEnum.ACT.name());
                opcionPreguntaDAO.save(opcion);

            } else {
                opcionBD.setContenido(opcion.getContenido());
                opcionBD.setEsCorrecta(opcion.getEsCorrecta());
                opcionBD.setEsMulti(opcion.getEsMulti());
                opcionBD.setEsOtro(opcion.getEsOtro());
                opcionBD.setEsTexto(opcion.getEsTexto());
                opcionBD.setRutaImagen(opcion.getRutaImagen());
                opcionPreguntaDAO.update(opcionBD);

                opcionesBD.remove(opcionBD);
            }
        }

        for (OpcionPregunta opcionBD : opcionesBD) {
            opcionPreguntaDAO.delete(opcionBD);
        }

    }

    @Override
    public PreguntaExamen findPregunta(Long idPregunta) {
        PreguntaExamen pregunta = preguntaExamenDAO.find(idPregunta);
        List<OpcionPregunta> opciones = opcionPreguntaDAO.allByPregunta(pregunta);
        pregunta.setOpcionPregunta(opciones);

        return pregunta;
    }

    @Override
    public List<PreguntaExamen> allReferencia(PreguntaExamen pregunta) {
        return preguntaExamenDAO.allReferencia(pregunta);
    }

    @Override
    @Transactional
    public void deletePregunta(PreguntaExamen pregunta) {
        PreguntaExamen preguntaBD = preguntaExamenDAO.find(pregunta.getId());
        ExamenVirtual encuesta = preguntaBD.getExamenVirtual();
        if (encuesta.getEstadoEnum() != ExamenVirtualEstadoEnum.CRE) {
            throw new PhobosException("No puede modificar el contenido de una encuesta Activa o Inactiva");
        }

        Integer numero = preguntaBD.getNumero();
        opcionPreguntaDAO.deleteByPregunta(preguntaBD);
        preguntaExamenDAO.delete(preguntaBD);

        List<PreguntaExamen> preguntas = preguntaExamenDAO.allMayoresByNumero(numero, encuesta);
        for (PreguntaExamen pgta : preguntas) {
            pgta.setNumero(pgta.getNumero() - 1);
            preguntaExamenDAO.update(pgta);
        }

        encuesta.setPreguntasDisponibles(encuesta.getPreguntasDisponibles() - 1);
        if (preguntaBD.getEstadoEnum() == PreguntaEstadoEnum.ACT) {
            encuesta.setPreguntasVisibles(encuesta.getPreguntasVisibles() - 1);
        }
        examenVirtualDAO.update(encuesta);
    }

    @Override
    @Transactional
    public void cambiarEstadoPregunta(PreguntaExamen pregunta, DataSessionPivot ds) {

        PreguntaExamen preguntaBD = preguntaExamenDAO.findPregunta(pregunta.getId());
        ExamenVirtual encuesta = preguntaBD.getExamenVirtual();
        if (encuesta.getEstadoEnum() != ExamenVirtualEstadoEnum.CRE) {
            throw new PhobosException("No puede modificar el contenido de una encuesta Activa o Inactiva");
        }

        if (pregunta.getEstadoEnum() == preguntaBD.getEstadoEnum()) {
            throw new PhobosException("La pregunta ya se encuentra en estado " + pregunta.getEstadoEnum().getValue());
        }

        if (preguntaBD.getEstadoEnum() == PreguntaEstadoEnum.ACT) {
            List<OpcionPregunta> opciones = opcionPreguntaDAO.allByPregunta(preguntaBD);
            List<PreguntaExamen> pgtasRefenciadas = preguntaExamenDAO.allByOpcionesReferencia(opciones);
            for (PreguntaExamen preguntaRef : pgtasRefenciadas) {
                if (preguntaRef.getEstadoEnum() == PreguntaEstadoEnum.ACT) {
                    throw new PhobosException("La pregunta " + preguntaRef.getNumero() + " se encuentra referenciada a una de las opciones de esta pregunta que desea deactivar ");
                }
            }
        }

        if (pregunta.getEstadoEnum() == PreguntaEstadoEnum.ACT) {
            encuesta.setPreguntasVisibles(encuesta.getPreguntasVisibles() + 1);
        } else {
            encuesta.setPreguntasVisibles(encuesta.getPreguntasVisibles() - 1);
        }

        preguntaBD.setEstado(pregunta.getEstadoEnum());
        preguntaBD.setUserModificacion(ds.getUsuario());
        preguntaBD.setFechaModificacion(new Date());
        preguntaExamenDAO.update(preguntaBD);

    }

    @Override
    public List<OpcionPregunta> allOpcionesByName(String nombre, ExamenVirtual encuesta) {
        return opcionPreguntaDAO.allByName(nombre, encuesta);
    }

    @Override
    public List<PreguntaExamen> allPreguntasActivasByEncuesta(ExamenVirtual encuesta) {
        return preguntaExamenDAO.allActivasByEncuesta(encuesta);
    }

    @Override
    public PreguntaExamen findPreguntaMaxOrden(List<PreguntaExamen> preguntas) {
        if (preguntas.isEmpty()) {
            return null;
        }
        Collections.sort(preguntas, new PreguntaExamen.CompareOrden());
        Collections.reverse(preguntas);
        return preguntas.get(0);
    }

    @Override
    public PreguntaExamen findPreguntaNumeroTop(Long idEncuesta) {
        List<PreguntaExamen> preguntas = preguntaExamenDAO.allByEncuesta(new ExamenVirtual(idEncuesta));
        if (preguntas.isEmpty()) {
            return null;
        }
        Collections.sort(preguntas, new PreguntaExamen.CompareOrden());
        Collections.reverse(preguntas);
        return preguntas.get(0);
    }

    @Override
    public ExamenVirtual findEncuesta(Long idEncuesta) {
        return evaluacionVirtualDAO.find(idEncuesta);
    }

    @Override
    @Transactional
    public void upateNumeroPregunta(PreguntaExamen preguntaForm) {
        PreguntaExamen preguntaBD = preguntaExamenDAO.find(preguntaForm.getId());
        ExamenVirtual encuesta = preguntaBD.getExamenVirtual();
        List<PreguntaExamen> preguntas = preguntaExamenDAO.allByEncuesta(encuesta);
        Map<Integer, PreguntaExamen> mapPreguntas = TypesUtil.convertListToMap("numero", preguntas);

        Integer nroReemplazo = (preguntaForm.getNumero() + preguntaForm.getDelta());
        if (nroReemplazo < 1) {
            throw new PhobosException("Este cambio de número de pregunta no está permitido");
        }

        PreguntaExamen preguntaTop = findPreguntaMaxOrden(preguntas);
        if (nroReemplazo > preguntaTop.getNumero()) {
            throw new PhobosException("Este cambio de número de pregunta no está permitido");
        }

        PreguntaExamen preguntaReemplazo = mapPreguntas.get(nroReemplazo);
        ObjectUtil.printAttr(preguntaReemplazo);

        preguntaBD.setNumero(nroReemplazo);
        preguntaExamenDAO.update(preguntaBD);

        if (preguntaReemplazo != null) {
            preguntaReemplazo.setNumero(preguntaForm.getNumero());
            preguntaExamenDAO.update(preguntaReemplazo);
        }
    }

}
