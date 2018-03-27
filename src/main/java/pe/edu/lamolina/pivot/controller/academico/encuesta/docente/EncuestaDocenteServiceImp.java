package pe.edu.lamolina.pivot.controller.academico.encuesta.docente;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.util.ListHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.encuesta.EncuestaDocente;
import pe.edu.lamolina.model.encuesta.EncuestaEstudiantil;
import pe.edu.lamolina.model.enums.EncuestaDocenteEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaEstudiantilDAO;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<EncuestaDocente> allEncuestaDocente(DynatableFilter filter, CicloAcademico ciclo) {
        return encuestaDocenteDAO.allByDynatable(filter, ciclo);
    }

    @Override
    @Transactional
    public void generarEncuesta(DataSessionPivot ds) {

        long DAYSINMS = 1000 * 60 * 60 * 24;

        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        List<DocenteSeccion> docenteSeccions = docenteSeccionDAO.allDocenteSeccionByModalidad(cicloAcademico, modalidad);
        Map<Long, DocenteSeccion> docenteSeccionMap = TypesUtil.convertListToMap("seccion.id", docenteSeccions);
        EncuestaEstudiantil encuestaEstudiantil = encuestaEstudiantilDAO.allByCicloTipo(cicloAcademico, modalidad, TipoExamenVirtualEnum.ENC_DOC);

        if (encuestaEstudiantil == null) {
            throw new PhobosException("No existe ninguna encuesta activa");
        }

        List<EncuestaDocente> encuestaDocentes = encuestaDocenteDAO.allByEncuestaEstudiantil(encuestaEstudiantil);
        Map<Long, EncuestaDocente> encuestaDocenteSeccionMap = TypesUtil.convertListToMap("docenteSeccion.id", encuestaDocentes);
        Map<Long, Map<Long, Seccion>> grupoSeccionMap = new LinkedHashMap();

        for (DocenteSeccion docenteSeccion : docenteSeccions) {
            Seccion seccion = docenteSeccion.getSeccion();
            GrupoSeccion grupo = seccion.getGrupoSeccion();
            Map<Long, Seccion> seccionMap = grupoSeccionMap.get(grupo.getId());
            if (seccionMap == null) {
                seccionMap = new ListHashMap<>();
            }
            seccionMap.put(grupo.getId(), seccion);
            grupoSeccionMap.put(grupo.getId(), seccionMap);
        }

        for (DocenteSeccion docenteSeccion : docenteSeccions) {

            EncuestaDocente sd = encuestaDocenteSeccionMap.get(docenteSeccion.getId());
            if (sd != null) {
                continue;
            }

            EncuestaDocente encuestaDocente = new EncuestaDocente();

            encuestaDocente.setDocenteSeccion(docenteSeccion);
            encuestaDocente.setEncuestaEstudiantil(encuestaEstudiantil);
            encuestaDocente.setEstadoEnum(EncuestaDocenteEstadoEnum.ACT);

            Docente docente = docenteSeccion.getDocente();
            Seccion seccion = docenteSeccion.getSeccion();
            GrupoSeccion grupo = seccion.getGrupoSeccion();
            Map<Long, Seccion> seccionMap = grupoSeccionMap.get(grupo.getId());

            if (seccionMap != null) {
                if (seccion.getTipoSeccionEnum() != TipoSeccionEnum.TEO) {
                    Seccion seccionTeoria = this.getSeccionTeoria(seccionMap);
                    if (seccionTeoria != null) {
                        DocenteSeccion docs = docenteSeccionMap.get(seccionTeoria.getId());
                        Docente doc = docs.getDocente();
                        if (doc.getId() == docente.getId().longValue()) {
                            encuestaDocente.setEstadoEnum(EncuestaDocenteEstadoEnum.TEO);
                        }
                    }
                }
            }

            Date fechaFinSeccion = docenteSeccion.getFechaFin();
            if (fechaFinSeccion != null) {
                Date fechaInicio = new Date(fechaFinSeccion.getTime() - 14 * DAYSINMS);
                Date fechaFin = new Date(fechaFinSeccion.getTime() - 7 * DAYSINMS);
                encuestaDocente.setFechaInicio(fechaInicio);
                encuestaDocente.setFechaFin(fechaFin);
            }

            encuestaDocente.setAlumnoFin(0L);
            encuestaDocente.setAlumnosInicio(0L);
            encuestaDocente.setAlumnosEncuestados(0L);
            encuestaDocente.setEsTeoriaPractica(0);
            encuestaDocente.setFechaEncuesta(new Date());

            encuestaDocenteDAO.save(encuestaDocente);
            encuestaDocenteSeccionMap.put(docenteSeccion.getId(), encuestaDocente);
        }
    }

    private Seccion getSeccionTeoria(Map<Long, Seccion> seccionMap) {
        for (Seccion sexxxion : seccionMap.values()) {
            if (sexxxion.getTipoSeccionEnum() == TipoSeccionEnum.TEO) {
                return sexxxion;
            }
        }
        return null;
    }
}
