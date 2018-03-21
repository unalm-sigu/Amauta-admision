package pe.edu.lamolina.pivot.controller.academico.encuesta.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaCicloDAO;
import pe.edu.lamolina.pivot.dao.encuesta.OpcionPreguntaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ExamenVirtualDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PreguntaExamenDAO;
import pe.edu.lamolina.pivot.dao.encuesta.TipoExamenVirtualDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.encuesta.CursoSinEncuesta;
import pe.edu.lamolina.model.encuesta.EncuestaEstudiantil;
import pe.edu.lamolina.model.enums.ExamenVirtualEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.PreguntaEstadoEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.examen.TipoExamenVirtual;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.EncuestaCiclo;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.encuesta.CicloPostulaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.CursoSinEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaEstudiantilDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class EditorEncuestaServiceImp implements EditorEncuestaService {

    @Autowired
    PreguntaExamenDAO preguntaExamenDAO;
    @Autowired
    OpcionPreguntaDAO opcionPreguntaDAO;
    @Autowired
    ExamenVirtualDAO examenVirtualDAO;
    @Autowired
    EncuestaCicloDAO encuestaCicloDAO;
    @Autowired
    CicloPostulaDAO cicloPostulaDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    TipoExamenVirtualDAO tipoExamenVirtualDAO;
    @Autowired
    CursoDAO cursoDAO;
    @Autowired
    EncuestaEstudiantilDAO encuestaEstudiantilDAO;
    @Autowired
    CursoSinEncuestaDAO cursoSinEncuestaDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public CicloPostula findCicloActivo() {
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        return cicloPostulaDAO.findActivo(modalidad);
    }

    @Override
    public List<ExamenVirtual> allEncuesta(DynatableFilter filter) {
        List<ExamenVirtual> encuestas = examenVirtualDAO.allEncuestasByDynatable(filter);
        List<EncuestaCiclo> encuestasCiclos = encuestaCicloDAO.allByEncuestas(encuestas);
        Map<Long, List<EncuestaCiclo>> mapEncuestaCiclos = TypesUtil.convertListToMapList("examenVirtual.id", encuestasCiclos);

        for (ExamenVirtual encuesta : encuestas) {
            List<EncuestaCiclo> encuestaCiclos = mapEncuestaCiclos.get(encuesta.getId());
            if (encuestaCiclos == null) {
                continue;
            }
            Collections.sort(encuestaCiclos, new EncuestaCiclo.ComparePeriodo());
            encuesta.setCicloInicio(encuestaCiclos.get(0).getCicloPostula());
            encuesta.setCicloFin(encuestaCiclos.get(encuestaCiclos.size() - 1).getCicloPostula());
        }
        return encuestas;
    }

    @Override
    @Transactional
    public void saveEncuesta(ExamenVirtual encuesta, DataSessionPivot ds) {

        encuesta.setEstado(ExamenVirtualEstadoEnum.CRE);
        encuesta.setPreguntasDisponibles(0);
        encuesta.setPreguntasVisibles(0);
        encuesta.setUserCreacion(ds.getUsuario());
        encuesta.setFechaCreacion(new Date());

        ExamenVirtual ultimaEncuesta = examenVirtualDAO.findEncuestaUltimoCodigo();
        if (ultimaEncuesta == null) {
            encuesta.setCodigo("ENC001");
        } else {
            Integer nroEnc = Integer.valueOf(ultimaEncuesta.getCodigo().substring(3)) + 1;
            encuesta.setCodigo("ENC" + NumberFormat.codigo(nroEnc, 3));
        }

        examenVirtualDAO.save(encuesta);
    }

    @Override
    @Transactional
    public void updateEncuesta(ExamenVirtual encuestaForm) {
        ExamenVirtual encuestaBD = examenVirtualDAO.find(encuestaForm.getId());
        encuestaBD.setNombre(encuestaForm.getNombre());
        encuestaBD.setTipoExamen(encuestaForm.getTipoExamen());
        examenVirtualDAO.update(encuestaBD);
    }

    @Override
    public ExamenVirtual findEncuesta(Long idEncuesta) {
        return examenVirtualDAO.find(idEncuesta);
    }

    @Override
    @Transactional
    public void delete(ExamenVirtual encuesta) {
        examenVirtualDAO.delete(encuesta);
    }

    @Override
    @Transactional
    public void duplicar(ExamenVirtual encuesta, DataSessionPivot ds) {
        ExamenVirtual encuestaBD = examenVirtualDAO.find(encuesta.getId());
        List<PreguntaExamen> preguntas = preguntaExamenDAO.allByEncuesta(encuesta);
        List<OpcionPregunta> opciones = opcionPreguntaDAO.allByPreguntas(preguntas);
        Map<Long, List<OpcionPregunta>> mapOpciones = TypesUtil.convertListToMapList("pregunta.id", opciones);

        for (PreguntaExamen pregunta : preguntas) {
            pregunta.setOpcionPregunta(mapOpciones.get(pregunta.getId()));
            if (pregunta.getOpcionPregunta() == null) {
                pregunta.setOpcionPregunta(new ArrayList());
            }
        }

        ExamenVirtual encuestaNew = new ExamenVirtual();
        encuestaNew.setNombre("Encuesta nueva copia del " + encuestaBD.getCodigo());
        this.saveEncuesta(encuestaNew, ds);

        Map<Integer, PreguntaExamen> mapPreguntasNew = new LinkedHashMap();
        Map<String, OpcionPregunta> mapOpcionesNew = new LinkedHashMap();

        for (PreguntaExamen pregunta : preguntas) {
            PreguntaExamen preguntaNew = new PreguntaExamen(pregunta);
            preguntaNew.setUserCreacion(ds.getUsuario());
            preguntaNew.setFechaCreacion(new Date());
            preguntaNew.setExamenVirtual(encuestaNew);
            preguntaNew.setOpcionPregunta(new ArrayList());
            preguntaExamenDAO.save(preguntaNew);

            encuestaNew.setPreguntasDisponibles(encuestaNew.getPreguntasDisponibles() + 1);
            if (preguntaNew.getEstadoEnum() == PreguntaEstadoEnum.ACT) {
                encuestaNew.setPreguntasVisibles(encuestaNew.getPreguntasVisibles() + 1);
            }

            mapPreguntasNew.put(pregunta.getNumero(), preguntaNew);

            List<OpcionPregunta> opcionPgta = pregunta.getOpcionPregunta();
            for (OpcionPregunta opcion : opcionPgta) {
                OpcionPregunta opcionNew = new OpcionPregunta(opcion);
                opcionNew.setUserCreacion(ds.getUsuario());
                opcionNew.setFechaCreacion(new Date());
                opcionNew.setPregunta(preguntaNew);
                opcionPreguntaDAO.save(opcionNew);

                preguntaNew.getOpcionPregunta().add(opcionNew);
                mapOpcionesNew.put(pregunta.getNumero() + "-" + opcion.getLetra(), opcionNew);
            }

        }

        for (PreguntaExamen pregunta : preguntas) {
            OpcionPregunta opcionRef = pregunta.getOpcionReferencia();
            if (opcionRef != null) {
                PreguntaExamen preguntaNew = mapPreguntasNew.get(pregunta.getNumero());
                OpcionPregunta opcionNew = mapOpcionesNew.get(opcionRef.getPregunta().getNumero() + "-" + opcionRef.getLetra());

                preguntaNew.setOpcionReferencia(opcionNew);
                preguntaExamenDAO.update(preguntaNew);
            }
        }

        examenVirtualDAO.update(encuestaNew);

    }

    @Override
    @Transactional
    public void cambiarEstadoEncuesta(ExamenVirtual encuesta, DataSessionPivot ds) {

        ExamenVirtual encuestaBD = examenVirtualDAO.find(encuesta.getId());
        if (encuestaBD.getEstado() == null) {
            encuestaBD.setEstado(ExamenVirtualEstadoEnum.INA);
        }

        if (encuestaBD.getEstadoEnum() == ExamenVirtualEstadoEnum.ACT) {
            Long respuestas = examenVirtualDAO.countRespuestas(encuestaBD);

            encuestaBD.setEstado(respuestas == 0L ? ExamenVirtualEstadoEnum.CRE : ExamenVirtualEstadoEnum.INA);
            examenVirtualDAO.update(encuestaBD);
            return;
        }

        if (encuestaBD.getPreguntasVisibles() < 1) {
            throw new PhobosException("No puede activarse una encuesta que no tiene preguntas visibles");
        }

        ExamenVirtual encuestaActiva = examenVirtualDAO.findEncuestaActiva();
        if (encuestaActiva != null) {
            Long respuestas = examenVirtualDAO.countRespuestas(encuestaActiva);
            encuestaActiva.setEstado(respuestas == 0L ? ExamenVirtualEstadoEnum.CRE : ExamenVirtualEstadoEnum.INA);
            examenVirtualDAO.update(encuestaActiva);
        }

        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloPostula cicloActivo = cicloPostulaDAO.findActivo(modalidad);
        CicloAcademico cicloAca = cicloActivo.getCicloAcademico();

        if (encuestaActiva != null) {
            Long respuestas = examenVirtualDAO.countRespuestasByCiclo(encuestaActiva, cicloActivo);
            if (respuestas > 0) {
                throw new PhobosException("Existe respuestas a la encuesta " + encuestaActiva.getCodigo() + " para el ciclo " + cicloAca.getDescripcion());
            }
        }

        encuestaBD.setEstado(ExamenVirtualEstadoEnum.ACT);
        examenVirtualDAO.update(encuestaBD);

        EncuestaCiclo encuestaCiclo = encuestaCicloDAO.findByCiclo(cicloActivo);
        if (encuestaCiclo == null) {
            encuestaCiclo = new EncuestaCiclo();
            encuestaCiclo.setCicloPostula(cicloActivo);
            encuestaCiclo.setExamenVirtual(encuestaBD);
            encuestaCiclo.setUserCreacion(ds.getUsuario());
            encuestaCiclo.setFechaCreacion(new Date());
            encuestaCicloDAO.save(encuestaCiclo);
            return;
        }

        encuestaCiclo.setExamenVirtual(encuestaBD);
        encuestaCiclo.setUserModificacion(ds.getUsuario());
        encuestaCiclo.setFechaModificacion(new Date());
        encuestaCicloDAO.update(encuestaCiclo);

    }

    @Override
    public List<PreguntaExamen> allPreguntasByEncuesta(ExamenVirtual encuesta) {
        List<PreguntaExamen> preguntas = preguntaExamenDAO.allByEncuesta(encuesta);
        List<OpcionPregunta> opciones = opcionPreguntaDAO.allByPreguntas(preguntas);

        Map<Long, List<OpcionPregunta>> mapOpciones = TypesUtil.convertListToMapList("pregunta.id", opciones);
        Map<Long, PreguntaExamen> mapPreguntas = TypesUtil.convertListToMap("id", preguntas);

        preguntas.forEach((pregunta) -> {
            pregunta.setOpcionPregunta(mapOpciones.get(pregunta.getId()));
        });

        Map<String, OpcionPregunta> mapOpcionesStr = new LinkedHashMap();
        opciones.forEach((opcion) -> {
            PreguntaExamen pregunta = mapPreguntas.get(opcion.getPregunta().getId());
            mapOpcionesStr.put(pregunta.getNumero() + "-" + opcion.getLetra(), opcion);
            opcion.setPregunta(pregunta);
            opcion.setPreguntaReferencia(new ArrayList());
        });

        preguntas.forEach((pgta) -> {
            if (pgta.getOpcionReferencia() != null) {
                OpcionPregunta opcion = pgta.getOpcionReferencia();
                PreguntaExamen preguntaRef = mapPreguntas.get(opcion.getPregunta().getId());
                OpcionPregunta opcionRef = mapOpcionesStr.get(preguntaRef.getNumero() + "-" + opcion.getLetra());
                pgta.setOpcionReferencia(opcionRef);
                opcionRef.getPreguntaReferencia().add(pgta);
            }
        });

        preguntas.forEach((pgta) -> {
            pgta.setOrden((pgta.getOrden() == null) ? pgta.getNumero() : pgta.getOrden());
        });

        Collections.sort(preguntas, new PreguntaExamen.CompareOrden());
        return preguntas;

    }

    @Override
    public List<TipoExamenVirtual> allTipoEncuesta() {
        return tipoExamenVirtualDAO.allEncuestaEstudiantil();
    }

    @Override
    public List<Curso> allCursoByName(String nombre) {
        return cursoDAO.allCursoByName(nombre);
    }

    @Override
    @Transactional
    public void addCursoSinEncuesta(CursoSinEncuesta cursoSinEncuestaForm, DataSessionPivot ds) {

        CicloAcademico ciclo = ds.getCicloAcademico();
        Curso curso = cursoSinEncuestaForm.getCurso();
        ExamenVirtual encuesta = cursoSinEncuestaForm.getEncuestaEstudiantil().getEncuesta();
        EncuestaEstudiantil encuestaEstudiantil = encuestaEstudiantilDAO.findByCicloEncuesta(ciclo, encuesta);

        if (encuestaEstudiantil == null) {
            encuestaEstudiantil = new EncuestaEstudiantil();
            encuestaEstudiantil.setCicloAcademico(ciclo);
            encuestaEstudiantil.setEncuesta(encuesta);
            encuestaEstudiantil.setFechaCreacion(new Date());
            encuestaEstudiantil.setUserCreacion(ds.getUsuario());
            encuestaEstudiantilDAO.save(encuestaEstudiantil);
        }

        CursoSinEncuesta cursoSinEncuesta = cursoSinEncuestaDAO.findByEncuestaEstudiantilCurso(encuestaEstudiantil, curso);

        if (cursoSinEncuesta != null) {
            throw new PhobosException("Curso ya registrado");
        }

        cursoSinEncuesta = new CursoSinEncuesta();
        cursoSinEncuesta.setCurso(curso);
        cursoSinEncuesta.setEncuestaEstudiantil(encuestaEstudiantil);
        cursoSinEncuesta.setFechaCreacion(new Date());
        cursoSinEncuesta.setUserCreacion(ds.getUsuario());
        cursoSinEncuestaDAO.save(cursoSinEncuesta);

    }

    @Override
    public List<Curso> allCursoSinEncuesta(ExamenVirtual encuestaForm, DataSessionPivot ds) {
        CicloAcademico ciclo = ds.getCicloAcademico();
        EncuestaEstudiantil encuestaEstudiantil = encuestaEstudiantilDAO.findByCicloEncuesta(ciclo, encuestaForm);
        if (encuestaEstudiantil == null) {
            return new ArrayList();
        }
        List<CursoSinEncuesta> cursoSinEncuestas = cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuestaEstudiantil);
        if (cursoSinEncuestas.isEmpty()) {
            return new ArrayList();
        }
        Map<Long, Curso> cursosMap = TypesUtil.convertListToMap("curso.id", "curso", cursoSinEncuestas);
        return cursosMap.values().stream().collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeCursoSinEncuesta(CursoSinEncuesta cursoSinEncuestaForm, DataSessionPivot ds) {
        CicloAcademico ciclo = ds.getCicloAcademico();
        Curso curso = cursoSinEncuestaForm.getCurso();
        ExamenVirtual encuesta = cursoSinEncuestaForm.getEncuestaEstudiantil().getEncuesta();
        EncuestaEstudiantil encuestaEstudiantil = encuestaEstudiantilDAO.findByCicloEncuesta(ciclo, encuesta);
        CursoSinEncuesta cursoSinEncuesta = cursoSinEncuestaDAO.findByEncuestaEstudiantilCurso(encuestaEstudiantil, curso);
        if (cursoSinEncuesta == null) {
            throw new PhobosException("Curso no existe en el registro");
        }
        cursoSinEncuestaDAO.delete(cursoSinEncuesta);
    }

}
