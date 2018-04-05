package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.docente;

import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.util.ListHashMap;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.CursoSinEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaAlumno;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ConfiguraEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.CursoSinEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaEstudiantilDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class EncuestaDocenteServiceImp implements EncuestaDocenteService {

    @Autowired
    EncuestaDocenteDAO encuestaDocenteDAO;
    @Autowired
    DocenteDAO docenteDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    EncuestaEstudiantilDAO encuestaEstudiantilDAO;
    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;
    @Autowired
    ConfiguraEncuestaDAO configuraEncuestaDAO;
    @Autowired
    CursoSinEncuestaDAO cursoSinEncuestaDAO;
    @Autowired
    EncuestaAlumnoDAO encuestaAlumnoDAO;
    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<EncuestaDocente> allEncuestaDocente(DynatableFilter filter, CicloAcademico ciclo) {
        return encuestaDocenteDAO.allByDynatable(filter, ciclo);
    }

    @Override
    @Transactional
    public void generarEncuesta(DataSessionPivot ds) {
        logger.debug("generar encuesta");
        long DAYSINMS = 1000 * 60 * 60 * 24;

        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        List<EventoAcademicoEnum> eventos = Arrays.asList(EventoAcademicoEnum.CLASES_PRE1, EventoAcademicoEnum.CLASES_PRE2);
        List<EventoCicloAcademico> eventosCicloAcademico = eventoCicloAcademicoDAO.allActivosByCicloEventos(cicloAcademico, eventos);

        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        List<MatriculaSeccion> matriculaSeccions = matriculaSeccionDAO.allByCiclo(cicloAcademico);
        Map<Long, List<MatriculaSeccion>> matriculaSeccionPorSeccion = TypesUtil.convertListToMapList("seccion.id", matriculaSeccions);
        logger.debug("matriculaSeccions {}", matriculaSeccions.size());

        List<DocenteSeccion> docenteSeccions = docenteSeccionDAO.allDocenteSeccionByModalidad(cicloAcademico, modalidad);
        Map<Long, List<DocenteSeccion>> docenteSeccionPorSeccion = TypesUtil.convertListToMapList("seccion.id", docenteSeccions);
        Map<Long, List<DocenteSeccion>> docenteSeccionPorGrupo = TypesUtil.convertListToMapList("seccion.grupoSeccion.id", docenteSeccions);
        logger.debug("docenteSeccions {}", docenteSeccions.size());

        EncuestaEstudiantil encuestaEstudiantil = encuestaEstudiantilDAO.allByCicloTipo(cicloAcademico, modalidad, TipoExamenVirtualEnum.ENC_DOC);
        if (encuestaEstudiantil == null) {
            throw new PhobosException("No existe ninguna encuesta activa");
        }
        ConfiguraEncuesta configuraEncuesta = configuraEncuestaDAO.findConfiguraEncuestaByEncuestaEstudiantil(encuestaEstudiantil);
        if (configuraEncuesta == null) {
            throw new PhobosException("No esta configurada la encuesta activa");
        }

        Long maximoDocentes = configuraEncuesta.getCantidadMaximaDocentes();
        Long minimoAlumnos = configuraEncuesta.getCantidadMinimaAlumnos();
        logger.debug("cantidadMaximaDocentes {} cantidadMinimaAlumnos {}", maximoDocentes, minimoAlumnos);

        List<CursoSinEncuesta> cursosSinEncuesta = cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuestaEstudiantil);
        Map<Long, Curso> cursoSinEncuestaMap = TypesUtil.convertListToMap("curso.id", "curso", cursosSinEncuesta);

        List<EncuestaDocente> encuestaDocentes = encuestaDocenteDAO.allByEncuestaEstudiantil(encuestaEstudiantil);
        Map<Long, EncuestaDocente> encuestaDocenteSeccionPorDocenteSeccion = TypesUtil.convertListToMap("docenteSeccion.id", encuestaDocentes);
        Map<Long, Map<Long, Seccion>> grupoSeccionPorGrupo = new LinkedHashMap();

        for (DocenteSeccion docenteSeccion : docenteSeccions) {
            Seccion seccion = docenteSeccion.getSeccion();
            GrupoSeccion grupo = seccion.getGrupoSeccion();
            Map<Long, Seccion> seccionMap = grupoSeccionPorGrupo.get(grupo.getId());
            if (seccionMap == null) {
                seccionMap = new ListHashMap<>();
            }
            seccionMap.put(seccion.getId(), seccion);
            grupoSeccionPorGrupo.put(grupo.getId(), seccionMap);
        }

        Date fechaInicio = null;
        Date fechaFin = null;
        for (EventoCicloAcademico eventoCicloAcademico : eventosCicloAcademico) {
            Date fechaInicioEvento = eventoCicloAcademico.getFechaInicio();
            Date fechaFinEvento = eventoCicloAcademico.getFechaFin();
            if (fechaInicio != null) {
                if (fechaInicioEvento.before(fechaInicio)) {
                    fechaInicio = fechaInicioEvento;
                }
            } else {
                fechaInicio = fechaInicioEvento;
            }
            if (fechaFin != null) {
                if (fechaFinEvento.after(fechaFin)) {
                    fechaFin = fechaFinEvento;
                }
            } else {
                fechaFin = fechaFinEvento;
            }
        }

        Date fechaInicio01 = null;
        Date fechaFin01 = null;

        Date fechaInicio02 = null;
        Date fechaFin02 = null;

        Date fechaInicio03 = null;
        Date fechaFin03 = null;

        if (fechaInicio == null) {
            throw new PhobosException(Constantine.REQ_EVENTO);
        }
        if (fechaFin == null) {
            throw new PhobosException(Constantine.REQ_EVENTO);
        }

        logger.debug(" fechaInicio {} ", new DateTime(fechaInicio).toString("dd-MM-yyyy"));
        logger.debug(" fechaFin {} ", new DateTime(fechaFin).toString("dd-MM-yyyy"));

        long inicio = fechaInicio.getTime();
        long fin = fechaFin.getTime();
        long rango = fin - inicio;
        long intervalo = rango / 3;

        long f1 = inicio + intervalo;
        long f2 = inicio + intervalo * 2;
        long f3 = inicio + intervalo * 3;

        fechaInicio01 = new Date(f1 - 15 * DAYSINMS);
        fechaInicio02 = new Date(f2 - 15 * DAYSINMS);
        fechaInicio03 = new Date(f3 - 15 * DAYSINMS);

        fechaFin01 = new Date(f1 - 7 * DAYSINMS);
        fechaFin02 = new Date(f2 - 7 * DAYSINMS);
        fechaFin03 = new Date(f3 - 7 * DAYSINMS);

        Map<Integer, Map<Integer, Date>> fechas = new LinkedHashMap();

        Map<Integer, Date> fm1 = new LinkedHashMap();
        fm1.put(1, fechaInicio01);
        fm1.put(2, fechaFin01);
        fechas.put(1, fm1);

        Map<Integer, Date> fm2 = new LinkedHashMap();
        fm2.put(1, fechaInicio02);
        fm2.put(2, fechaFin02);
        fechas.put(2, fm2);

        Map<Integer, Date> fm3 = new LinkedHashMap();
        fm3.put(1, fechaInicio03);
        fm3.put(2, fechaFin03);
        fechas.put(3, fm3);
        logger.debug("fechas # {}", fechas.size());

        Map<Long, Integer> grupoFecha = new LinkedHashMap();

        for (DocenteSeccion docenteSeccion : docenteSeccions) {

            EncuestaDocente sd = encuestaDocenteSeccionPorDocenteSeccion.get(docenteSeccion.getId());
            if (sd != null) {
                continue;
            }

            Docente docente = docenteSeccion.getDocente();
            Seccion seccion = docenteSeccion.getSeccion();
            GrupoSeccion grupo = seccion.getGrupoSeccion();
            Curso curso = grupo.getCurso();

            Curso cursoSinEncuesta = cursoSinEncuestaMap.get(curso.getId());
            if (cursoSinEncuesta != null) {
                continue;
            }

            int cantidadDocentes = this.cantidadDocentes(grupo, docenteSeccionPorGrupo);
            boolean cumpleCantMaxDocentes = this.validarCantidadMaximaDocentes(grupo, maximoDocentes, docenteSeccionPorGrupo);
            boolean esDocentePractica = this.esDocentePractica(grupo, grupoSeccionPorGrupo, seccion, docenteSeccionPorSeccion, docente);
            if (cantidadDocentes != 1) {
                if (cantidadDocentes < 1) {
                    continue;
                }
                if (cantidadDocentes > 3) {
                    this.makeEncuestaDocente(seccion, minimoAlumnos, ds, docenteSeccion, encuestaEstudiantil,
                            matriculaSeccionPorSeccion, fechaInicio03, fechaFin03, cumpleCantMaxDocentes, esDocentePractica);
                    continue;
                }
                Integer ordenAsignacion = grupoFecha.get(grupo.getId());
                if (ordenAsignacion == null) {
                    ordenAsignacion = 1;
                }
                if (ordenAsignacion > 3) {
                    //logger.debug("orden sobrepasado para docente {} del grupo {} ", docente.getId(), grupo.getId());
                    ordenAsignacion = 1;
                }
                //logger.debug("cantidad docentes {}", cantidadDocentes);
                //logger.debug("grupo orden {}", ordenAsignacion);
                Map<Integer, Date> gFecha = fechas.get(ordenAsignacion);
                ordenAsignacion++;
                grupoFecha.put(grupo.getId(), ordenAsignacion);
                Date fInicio = gFecha.get(1);
                Date fFin = gFecha.get(2);
                //logger.debug("grupo de tres {}  {}  {} ", grupo.getId(), new DateTime(fInicio).toString("dd-MM-yyyy"), new DateTime(fFin).toString("dd-MM-yyyy"));
                this.makeEncuestaDocente(seccion, minimoAlumnos, ds, docenteSeccion, encuestaEstudiantil,
                        matriculaSeccionPorSeccion, fInicio, fFin, cumpleCantMaxDocentes, esDocentePractica);
                continue;
            }
            for (EventoCicloAcademico eventoCicloAcademico : eventosCicloAcademico) {
                Date fechaFinEvento = eventoCicloAcademico.getFechaFin();
                Date fechaInicio1 = new Date(fechaFinEvento.getTime() - 15 * DAYSINMS);
                Date fechaFin1 = new Date(fechaFinEvento.getTime() - 7 * DAYSINMS);
                this.makeEncuestaDocente(seccion, minimoAlumnos, ds, docenteSeccion, encuestaEstudiantil,
                        matriculaSeccionPorSeccion, fechaInicio1, fechaFin1, cumpleCantMaxDocentes, esDocentePractica);
            }
        }
    }

    private void makeEncuestaDocente(
            Seccion seccion, Long minimoAlumnos, DataSessionPivot ds,
            DocenteSeccion docenteSeccion, EncuestaEstudiantil encuestaEstudiantil,
            Map<Long, List<MatriculaSeccion>> matriculaSeccionPorSeccion,
            Date fechaInicio, Date fechaFin,
            boolean cumpleCantMaxDocentes, boolean esDocentePractica) {

        EncuestaDocente encuestaDocente = new EncuestaDocente();
        encuestaDocente.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);

        if (esDocentePractica) {
            //logger.debug("{}", Constantine.REQ_CUR_TEORIA);
            encuestaDocente.setDescripcion(Constantine.REQ_CUR_TEORIA);
            encuestaDocente.setEstadoEnum(EncuestaEstudiantilEstadoEnum.TEO);
        }

        if (cumpleCantMaxDocentes) {
            //logger.debug("{}", Constantine.REQ_MAX_DOCENTE);
            encuestaDocente.setDescripcion(Constantine.REQ_MAX_DOCENTE);
            encuestaDocente.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
        }

        encuestaDocente.setFechaEncuestaInicio(fechaInicio);
        encuestaDocente.setFechaEncuestaFin(fechaFin);
        encuestaDocente.setDocenteSeccion(docenteSeccion);
        encuestaDocente.setEncuestaEstudiantil(encuestaEstudiantil);
        encuestaDocente.setAlumnoFin(0L);
        encuestaDocente.setAlumnosInicio(0L);
        encuestaDocente.setAlumnosEncuestados(0L);
        encuestaDocente.setEsTeoriaPractica(0);
        encuestaDocenteDAO.save(encuestaDocente);
        this.makeEncuestaAlumno(matriculaSeccionPorSeccion, encuestaDocente, seccion, ds, minimoAlumnos);
    }

    private boolean esDocentePractica(GrupoSeccion grupo, Map<Long, Map<Long, Seccion>> grupoSeccionPorGrupo,
            Seccion seccion, Map<Long, List<DocenteSeccion>> docenteSeccionPorSeccion, Docente docente) {
        Map<Long, Seccion> seccionMap = grupoSeccionPorGrupo.get(grupo.getId());
        if (seccion.getTipoSeccionEnum() != TipoSeccionEnum.PCUR) {
            return false;
        }
        Docente docenteTeoria = this.getDocenteTeoria(seccionMap, docenteSeccionPorSeccion);
        if (docenteTeoria.getId() == docente.getId().longValue()) {
            return true;
        }
        return false;
    }

    private Docente getDocenteTeoria(Map<Long, Seccion> seccionMap, Map<Long, List<DocenteSeccion>> docenteSeccionMap) {
        if (seccionMap == null || seccionMap.isEmpty()) {
            return new Docente(0);
        }
        Seccion seccionTeoria = this.getSeccionTeoria(seccionMap);
        if (seccionTeoria == null) {
            return new Docente(0);
        }
        List<DocenteSeccion> docs = docenteSeccionMap.get(seccionTeoria.getId());
        for (DocenteSeccion doc : docs) {
            if (doc.getSeccion().getTipoSeccionEnum() == TipoSeccionEnum.TCUR) {
                return doc.getDocente();
            }
        }
        return new Docente(0);
    }

    private Seccion getSeccionTeoria(Map<Long, Seccion> seccionMap) {
        for (Seccion seccion : seccionMap.values()) {
            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.TCUR) {
                return seccion;
            }
        }
        return null;
    }

    private void makeEncuestaAlumno(Map<Long, List<MatriculaSeccion>> matriculaSeccionMap,
            EncuestaDocente encuestaDocente, Seccion seccion, DataSessionPivot ds, Long minimoAlumnos) {

        List<MatriculaSeccion> matriculaSeccion = matriculaSeccionMap.get(seccion.getId());
        if (matriculaSeccion == null) {
            //logger.debug("1 {}", Constantine.REQ_MIN_ALUMNO);
            encuestaDocente.setDescripcion(Constantine.REQ_MIN_ALUMNO);
            encuestaDocente.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
            encuestaDocenteDAO.update(encuestaDocente);
            return;
        }

        Map<Long, Alumno> alumnos = TypesUtil.convertListToMap("matriculaResumen.alumno.id", "matriculaResumen.alumno", matriculaSeccion);
        if (alumnos == null || alumnos.isEmpty()) {
            // logger.debug("2 {}", Constantine.REQ_MIN_ALUMNO);
            encuestaDocente.setDescripcion(Constantine.REQ_MIN_ALUMNO);
            encuestaDocente.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
            encuestaDocenteDAO.update(encuestaDocente);
            return;
        }

        if (alumnos.size() < minimoAlumnos) {
            //logger.debug("3 {}", Constantine.REQ_MIN_ALUMNO);
            encuestaDocente.setDescripcion(Constantine.REQ_MIN_ALUMNO);
            encuestaDocente.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
        }

        for (Alumno alumno : alumnos.values()) {
            EncuestaAlumno encuesta = new EncuestaAlumno();
            encuesta.setAlumno(alumno);
            encuesta.setEncuestaDocente(encuestaDocente);
            if (encuestaDocente.getEstadoEnum() == EncuestaEstudiantilEstadoEnum.ACT) {
                encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
            } else {
                encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
            }
            encuesta.setUserRegistro(ds.getUsuario());
            encuesta.setFechaRegistro(new Date());
            encuestaAlumnoDAO.save(encuesta);
        }
        encuestaDocente.setAlumnosInicio(new Long(alumnos.size()));
        encuestaDocente.setAlumnoFin(new Long(alumnos.size()));
        encuestaDocente.setAlumnosEncuestados(new Long(alumnos.size()));
        encuestaDocenteDAO.update(encuestaDocente);
    }

    private boolean validarCantidadMaximaDocentes(GrupoSeccion grupo,
            Long maximoDocentes, Map<Long, List<DocenteSeccion>> docenteSeccionPorGrupo) {
        List<DocenteSeccion> docentesSeccion = docenteSeccionPorGrupo.get(grupo.getId());
        if (docentesSeccion == null || docentesSeccion.isEmpty()) {
            return true;
        }
        Map<Long, Docente> docentes = new LinkedHashMap();
        for (DocenteSeccion docenteSeccion : docentesSeccion) {
            Docente docente = docenteSeccion.getDocente();
            docentes.put(docente.getId(), docente);
        }
        //logger.debug("# docs {} grupo {} ", docentes.size(), grupo.getId());
        if (docentes.size() > maximoDocentes) {
            return true;
        }
        return false;
    }

    private int cantidadDocentes(GrupoSeccion grupo, Map<Long, List<DocenteSeccion>> docenteSeccionPorGrupo) {
        List<DocenteSeccion> docentesSeccion = docenteSeccionPorGrupo.get(grupo.getId());
        if (docentesSeccion == null || docentesSeccion.isEmpty()) {
            return 0;
        }
        Map<Long, Docente> docentes = new LinkedHashMap();
        for (DocenteSeccion docenteSeccion : docentesSeccion) {
            Docente docente = docenteSeccion.getDocente();
            docentes.put(docente.getId(), docente);
        }
        return docentes.size();
    }

    @Override
    @Transactional
    public void cambiarEstadoEncuesta(EncuestaDocente encuestaForm) {
        EncuestaDocente encuesta = encuestaDocenteDAO.findEncuestaDocente(encuestaForm);
        if (Strings.isNullOrEmpty(encuesta.getEstado())) {
            encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
        }
        if (encuesta.getEstadoEnum() == EncuestaEstudiantilEstadoEnum.ACT
                || encuesta.getEstadoEnum() == EncuestaEstudiantilEstadoEnum.TEO) {
            encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
            List<EncuestaAlumno> encuestas = encuestaAlumnoDAO.allByEncuestaDocente(encuesta);
            for (EncuestaAlumno encuestaAlumno : encuestas) {
                encuestaAlumno.setEstadoEnum(EncuestaEstudiantilEstadoEnum.INA);
                encuestaAlumnoDAO.update(encuestaAlumno);
            }
            return;
        }
        encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
        List<EncuestaAlumno> encuestas = encuestaAlumnoDAO.allByEncuestaDocente(encuesta);
        if (encuestas.size() < 1) {
            throw new PhobosException("No existe encuestas para el docente ");
        }
        for (EncuestaAlumno encuestaAlumno : encuestas) {
            encuestaAlumno.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
            encuestaAlumnoDAO.update(encuestaAlumno);
        }
    }
}
