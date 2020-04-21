package pe.edu.lamolina.amauta.controller.programacionhorarios.boletinacademico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.RestriccionCarrera;
import pe.edu.lamolina.model.academico.RestriccionFacultad;
import pe.edu.lamolina.model.academico.RestriccionModalidad;
import pe.edu.lamolina.model.academico.RestriccionRepitencia;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.RestriccionCarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.RestriccionFacultadDAO;
import pe.edu.lamolina.amauta.dao.academico.RestriccionModalidadDAO;
import pe.edu.lamolina.amauta.dao.academico.RestriccionRepitenciaDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class BoletinAcademicoServiceImp implements BoletinAcademicoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    RestriccionModalidadDAO restriccionModalidadDAO;

    @Autowired
    RestriccionFacultadDAO restriccionFacultadDAO;

    @Autowired
    RestriccionCarreraDAO restriccionCarreraDAO;

    @Autowired
    RestriccionRepitenciaDAO restriccionRepitenciaDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    VerificadorService verificadorService;

    @Override
    public void reporteAnexoBoletin(DataSessionPivot ds) {
        CicloAcademico ciclo = this.findCicloAcademicoActivo();
        List<AnexoBoletin> anexosBoletin = this.allAnexosByCiclo(ciclo, ds);
        for (AnexoBoletin anexoBoletin : anexosBoletin) {
            logger.debug("Anexo Boletin Padre {} id {}", anexoBoletin.getNombre(), anexoBoletin.getId());
            for (AnexoBoletin anexosBoletinHijo : anexoBoletin.getAnexosBoletinHijos()) {
                logger.debug("          Anexo Boletin Hijo {} id {}", anexosBoletinHijo.getNombre(), anexosBoletinHijo.getId());
                for (Curso curso : anexosBoletinHijo.getCursos()) {
                    logger.debug("                     Curso {}", curso.getNombre());
                    for (GrupoSeccion grupoSeccion : curso.getGrupoSeccion()) {
                        for (Seccion seccion : grupoSeccion.getSecciones()) {
                            logger.debug("                      Seccion {} tipo {}", seccion.getCodigo2(), seccion.getTipoSeccion());
                        }
                    }
                }
            }
        }
    }

    @Override
    public GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo) {
        List<AnexoBoletin> anexos = anexoBoletinDAO.allAnexosHijos();
        return grupoSeccionDAO.resumenByCiclo(ciclo, anexos);
    }

    @Override
    public List<AnexoBoletin> allAnexosSuperiores() {
        return anexoBoletinDAO.allAnexosSuperiores();
    }

    @Override
    public List<AnexoBoletin> allAnexoBoletionHijos(CicloAcademico ciclo) {
        return anexoBoletinDAO.allHijosWithCursos(ciclo);
    }

    @Override
    public List<AnexoBoletin> allAnexosByCiclo(CicloAcademico ciclo, DataSessionPivot ds) {
        long t1 = System.currentTimeMillis();

        List<AnexoBoletin> anexosAll = anexoBoletinDAO.all();
        List<AnexoBoletin> anexosSuperiores = verificadorService.anexosSuperioresByOficina(ds);
        List<AnexoBoletin> anexosInferiores = verificadorService.anexosInferioresByOficina(ds, anexosAll);
        List<AnexoBoletin> anexos = anexoBoletinDAO.allTodosByCiclo(ciclo, anexosSuperiores, anexosInferiores);

        List<DocenteSeccion> docentesSecciones = docenteSeccionDAO.allByCiclo(
                ciclo,
                Arrays.asList(EstadoEnum.ACT),
                Arrays.asList(SeccionEstadoEnum.ACT, SeccionEstadoEnum.BLO),
                anexos);

        Map<Long, Seccion> mapSeccionAll = TypesUtil.convertListToMap("seccion.id", "seccion", docentesSecciones);
        List<Seccion> seccionAll = clearDuplicadoSecc(new ArrayList(mapSeccionAll.values()));
        List<String> seccionStr = seccionAll.stream().map(x -> x.getId().toString()).collect(Collectors.toList());
        logger.debug("Secciones encontradas {}", String.join(",", seccionStr));

        List<RestriccionCarrera> restriccionCarreras = restriccionCarreraDAO.allActivasBySecciones(seccionAll);
        List<RestriccionFacultad> restriccionFacultads = restriccionFacultadDAO.allActivasBySecciones(seccionAll);
        List<RestriccionModalidad> restriccionModalidads = restriccionModalidadDAO.allActivasBySecciones(seccionAll);
        List<RestriccionRepitencia> restriccionRepitencias = restriccionRepitenciaDAO.allActivasBySecciones(seccionAll);
        List<HorarioSeccion> horariosSecciones = horarioSeccionDAO.allBySecciones(seccionAll);

        long t2 = System.currentTimeMillis();
        logger.debug("Boletin cargado a la memoria en {} mseg", (t2 - t1));

        Map<Long, List<RestriccionCarrera>> mapRestriccionCarrera = TypesUtil.convertListToMapList("seccion.id", restriccionCarreras);
        Map<Long, List<RestriccionFacultad>> mapRestriccionFacultad = TypesUtil.convertListToMapList("seccion.id", restriccionFacultads);
        Map<Long, List<RestriccionModalidad>> mapRestriccionModalidad = TypesUtil.convertListToMapList("seccion.id", restriccionModalidads);
        Map<Long, List<RestriccionRepitencia>> mapRestriccionRepitencia = TypesUtil.convertListToMapList("seccion.id", restriccionRepitencias);
        Map<Long, List<HorarioSeccion>> mapHorariosSecciones = TypesUtil.convertListToMapList("seccion.id", horariosSecciones);

        Map<Long, AnexoBoletin> mapAnexoSup = TypesUtil.convertListToMap("seccion.grupoSeccion.anexoBoletin.anexoSuperior.id", "seccion.grupoSeccion.anexoBoletin.anexoSuperior", docentesSecciones);
        Map<Long, List<AnexoBoletin>> mapAnexosHijos = TypesUtil.convertListToMapList("seccion.grupoSeccion.anexoBoletin.anexoSuperior.id", "seccion.grupoSeccion.anexoBoletin", docentesSecciones);
        Map<Long, List<Curso>> mapCursos = TypesUtil.convertListToMapList("seccion.grupoSeccion.anexoBoletin.id", "seccion.grupoSeccion.curso", docentesSecciones);
        Map<Long, List<GrupoSeccion>> mapGpoSecciones = TypesUtil.convertListToMapList("seccion.grupoSeccion.idCursoIdAnexo", "seccion.grupoSeccion", docentesSecciones);
        Map<Long, List<Seccion>> mapSecciones = TypesUtil.convertListToMapList("seccion.grupoSeccion.id", "seccion", docentesSecciones);
        Map<Long, List<DocenteSeccion>> mapProfeSecciones = TypesUtil.convertListToMapList("seccion.id", docentesSecciones);

        Map<Long, Curso> mapCursoTotal = new LinkedHashMap();

        List<AnexoBoletin> anexosSup = new ArrayList(mapAnexoSup.values());
        for (AnexoBoletin anexoSup : anexosSup) {
            List<AnexoBoletin> anexosHijos = clearDuplicadoAnexo(mapAnexosHijos.get(anexoSup.getId()));
            anexoSup.setAnexosBoletinHijos(anexosHijos);
            for (AnexoBoletin anexo : anexosHijos) {
                anexo.setAnexoSuperior(anexoSup);
                List<Curso> cursos = clearDuplicadoCurso(mapCursos.get(anexo.getId()));
                Collections.sort(cursos, new Curso.CompareNombre());
                anexo.setCursos(new ArrayList());
                for (Curso cursoBoletin : cursos) {
                    Curso curso = mapCursoTotal.get(cursoBoletin.getId());
                    curso = (curso == null) ? cursoBoletin : cursoBoletin.clone();
                    mapCursoTotal.put(curso.getId(), curso);
                    anexo.getCursos().add(curso);
                    List<GrupoSeccion> gposSeccs = clearDuplicadoGpoSecc(mapGpoSecciones.get(curso.getId() + "-" + anexo.getId()));
                    Collections.sort(gposSeccs, new GrupoSeccion.CompareCodigo2());
                    curso.setGrupoSeccion(gposSeccs);
                    for (GrupoSeccion gpoSecc : gposSeccs) {
                        gpoSecc.setCurso(curso);
                        gpoSecc.setAnexoBoletin(anexo);
                        List<Seccion> secciones = clearDuplicadoSecc(mapSecciones.get(gpoSecc.getId()));
                        Collections.sort(secciones, new Seccion.CompareCodigo2());
                        gpoSecc.setSecciones(secciones);
                        for (Seccion seccion : secciones) {
                            seccion.setGrupoSeccion(gpoSecc);
                            seccion.setRestriccionesCarrera(getList(mapRestriccionCarrera.get(seccion.getId())));
                            seccion.setRestriccionesFacultad(getList(mapRestriccionFacultad.get(seccion.getId())));
                            seccion.setRestriccionesModalidad(getList(mapRestriccionModalidad.get(seccion.getId())));
                            seccion.setRestriccionesRepitencia(getList(mapRestriccionRepitencia.get(seccion.getId())));
                            seccion.setHorarioSeccion(getList(mapHorariosSecciones.get(seccion.getId())));
                            List<DocenteSeccion> profesSecc = mapProfeSecciones.get(seccion.getId());
                            seccion.setDocenteSeccion(profesSecc);
                            for (DocenteSeccion profeSecc : profesSecc) {
                                profeSecc.setSeccion(seccion);
                            }
                        }

                    }
                }

            }
        }
        long t3 = System.currentTimeMillis();
        logger.debug("Boletin ordenado en {} mseg", (t3 - t2));
        logger.debug("Boletin listo en {} mseg", (t3 - t1));

        return anexosSup;
    }

    private List getList(List lista) {
        if (lista == null) {
            return new ArrayList();
        }
        return lista;
    }

    private List<AnexoBoletin> clearDuplicadoAnexo(List<AnexoBoletin> anexos) {
        Map<Long, AnexoBoletin> mapAnexos = TypesUtil.convertListToMap("id", anexos);
        return new ArrayList(mapAnexos.values());
    }

    private List<Curso> clearDuplicadoCurso(List<Curso> cursos) {
        Map<Long, Curso> mapCursos = TypesUtil.convertListToMap("id", cursos);
        return new ArrayList(mapCursos.values());
    }

    private List<GrupoSeccion> clearDuplicadoGpoSecc(List<GrupoSeccion> gposSeccs) {
        Map<Long, GrupoSeccion> mapGpoSecc = TypesUtil.convertListToMap("id", gposSeccs);
        return new ArrayList(mapGpoSecc.values());
    }

    private List<Seccion> clearDuplicadoSecc(List<Seccion> secciones) {
        Map<Long, Seccion> mapSeccion = TypesUtil.convertListToMap("id", secciones);
        return new ArrayList(mapSeccion.values());
    }

    @Override
    public CicloAcademico findCicloAcademicoActivo() {
        CicloAcademico ciclo = cicloAcademicoDAO.findVerBoletin(ModalidadEstudioEnum.PRE);
        if (ciclo == null) {
            ciclo = cicloAcademicoDAO.findActivoPregrado();
        }
        return ciclo;
    }
}
