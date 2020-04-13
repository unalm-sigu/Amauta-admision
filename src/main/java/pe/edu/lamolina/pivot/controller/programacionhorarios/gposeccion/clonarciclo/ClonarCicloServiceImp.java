package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.clonarciclo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.CodeGenerator;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.PrecioCursoEstructura;
import pe.edu.lamolina.model.academico.RestriccionCarrera;
import pe.edu.lamolina.model.academico.RestriccionFacultad;
import pe.edu.lamolina.model.academico.RestriccionModalidad;
import pe.edu.lamolina.model.academico.RestriccionRepitencia;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.EstadoHorarioAulaEnum;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.CLASES_EPG;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.CLASES_PRE;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.CLASES_VER;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.SituacionDocenteEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoCreditoEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import pe.edu.lamolina.model.enums.TipoDictadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.TipoHorarioAulaEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.GpoSeccionService;
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionExpandidaDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.PrecioCursoEstructuraDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionCarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionFacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionModalidadDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionRepitenciaDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.vacante.VacanteAlumnoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ClonarCicloServiceImp implements ClonarCicloService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    @Autowired
    CursoCicloAcademicoDAO cursoCicloAcademicoDAO;
    @Autowired
    CursoCurriculaDAO cursoCurriculaDAO;
    @Autowired
    DiaDAO diaDAO;
    @Autowired
    DiaHoraGrupoDAO diaHoraGrupoDAO;
    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;
    @Autowired
    EvaluacionDAO evaluacionDAO;
    @Autowired
    EvaluacionExpandidaDAO evaluacionExpandidaDAO;
    @Autowired
    EvaluacionSeccionDAO evaluacionSeccionDAO;
    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;
    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;
    @Autowired
    HorarioAulaDAO horarioAulaDAO;
    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;
    @Autowired
    PrecioCursoEstructuraDAO precioCursoEstructuraDAO;
    @Autowired
    RestriccionCarreraDAO restriccionCarreraDAO;
    @Autowired
    RestriccionFacultadDAO restriccionFacultadDAO;
    @Autowired
    RestriccionModalidadDAO restriccionModalidadDAO;
    @Autowired
    RestriccionRepitenciaDAO restriccionRepitenciaDAO;
    @Autowired
    SeccionDAO seccionDAO;
    @Autowired
    TipoCursoCurriculaDAO tipoCursoCurriculaDAO;
    @Autowired
    VacanteAlumnoDAO vacanteAlumnoDAO;

    @Autowired
    GpoSeccionService gpoSeccionService;
    @Autowired
    VerificadorService verificadorService;

    @Override
    @Transactional
    public void clonarCiclo(CicloClonacionBean cicloClonacionBean, DataSessionPivot ds) {

        long t01 = System.currentTimeMillis();

        CicloAcademico cicloOrigenForm = cicloClonacionBean.getCicloOrigen();
        CicloAcademico cicloDestinoForm = cicloClonacionBean.getCicloDestino();

        List<Dia> dias = diaDAO.all();
        CicloAcademico cicloOrigen = cicloAcademicoDAO.find(cicloOrigenForm.getId());
        CicloAcademico cicloDestino = cicloAcademicoDAO.find(cicloDestinoForm.getId());

        logger.debug("copiar del ciclo {} al ciclo {}", cicloOrigen.getId(), cicloDestino.getId());

        if (cicloOrigen.getId().longValue() == cicloDestino.getId()) {
            throw new PhobosException("El ciclo académico es el mismo al que desea copiar");
        }

        cicloDestino.setFechaClonacion(new Date());

        logger.debug("Fecha del  ciclo {}", cicloDestino.getFechaClonacion());

        validarClonacion(cicloDestino);
        List<PrecioCursoEstructura> precioCursoEstructura = precioCursoEstructuraDAO.allByCiclo(cicloDestino);

        List<CursoCicloAcademico> cursoCicloAcademico = cursoCicloAcademicoDAO.allByCiclo(cicloDestino);
        logger.debug("CursoCicloAcademico size  {}", cursoCicloAcademico.size());

        Set<String> tpcs = precioCursoEstructura.stream().map(PrecioCursoEstructura::getTpc).collect(Collectors.toSet());

        Set<Curso> cursos = cursoCicloAcademico.stream().map(CursoCicloAcademico::getCurso).collect(Collectors.toSet());
        logger.debug("Curso size  {}", cursos.size());

        List<GrupoSeccion> gsOrigenes = grupoSeccionDAO.allWithDocenteSeccionActivosByCiclo(cicloOrigen);
        logger.debug("GrupoSeccion size  {}", gsOrigenes.size());

        List<Seccion> secciones = seccionDAO.allWithMatriculadosByGposSeccion(gsOrigenes);
        logger.debug("Seccion size  {}", secciones.size());

        List<DocenteSeccion> dsOrigenes = docenteSeccionDAO.allActivosBySecciones(secciones);
        logger.debug("DocenteSeccion size  {}", dsOrigenes.size());

        List<RestriccionCarrera> restriccionCarreraOrigen = restriccionCarreraDAO.allActivasBySecciones(secciones);
        logger.debug("RestriccionCarrera size  {}", restriccionCarreraOrigen.size());

        List<RestriccionFacultad> restriccionFacultadOrigen = restriccionFacultadDAO.allActivasBySecciones(secciones);
        logger.debug("RestriccionFacultad size  {}", restriccionFacultadOrigen.size());

        List<RestriccionModalidad> restriccionModalidadOrigen = restriccionModalidadDAO.allActivasBySecciones(secciones);
        logger.debug("RestriccionModalidad size  {}", restriccionModalidadOrigen.size());

        List<RestriccionRepitencia> restriccionRepitenciaOrigen = restriccionRepitenciaDAO.allActivasBySecciones(secciones);
        logger.debug("RestriccionRepitencia size  {}", restriccionRepitenciaOrigen.size());

        List<DiaHoraGrupo> diaHoraGrupos = diaHoraGrupoDAO.allByCiclo(cicloDestino);
        Map<Long, List<DiaHoraGrupo>> mapGrupoHorario = TypesUtil.convertListToMapList("grupoHorario.id", diaHoraGrupos);

        Map<Long, List<RestriccionCarrera>> restriccionCarreraMap = TypesUtil.convertListToMapList("seccion.id", restriccionCarreraOrigen);
        Map<Long, List<RestriccionFacultad>> restriccionFacultadMap = TypesUtil.convertListToMapList("seccion.id", restriccionFacultadOrigen);
        Map<Long, List<RestriccionModalidad>> restriccionModalidadMap = TypesUtil.convertListToMapList("seccion.id", restriccionModalidadOrigen);
        Map<Long, List<RestriccionRepitencia>> restriccionRepitenciaMap = TypesUtil.convertListToMapList("seccion.id", restriccionRepitenciaOrigen);

        Map<Long, List<DocenteSeccion>> docentesSeccionMap = TypesUtil.convertListToMapList("seccion.id", dsOrigenes);
        for (Seccion secOrigen : secciones) {
            secOrigen.setDocenteSeccion(docentesSeccionMap.get(secOrigen.getId()));
            secOrigen.setRestriccionesCarrera(restriccionCarreraMap.get(secOrigen.getId()));
            secOrigen.setRestriccionesFacultad(restriccionFacultadMap.get(secOrigen.getId()));
            secOrigen.setRestriccionesModalidad(restriccionModalidadMap.get(secOrigen.getId()));
            secOrigen.setRestriccionesRepitencia(restriccionRepitenciaMap.get(secOrigen.getId()));
        }

        Map<Long, List<Seccion>> seccionesMap = TypesUtil.convertListToMapList("grupoSeccion.id", secciones);
        for (GrupoSeccion gsOrigene : gsOrigenes) {
            List<Seccion> seccionesGpo = seccionesMap.get(gsOrigene.getId());
            Collections.sort(seccionesGpo, new Seccion.CompareCodigo2());
            gsOrigene.setSecciones(seccionesGpo);

        }

        List<String> codigos = new ArrayList();

        Date today = new Date();
        int factorHoras = 0;
        if (cicloDestino.getTipoEnum() == TipoCicloEnum.REG) {
            factorHoras = 1;
        } else if (cicloDestino.getTipoEnum() == TipoCicloEnum.NIV) {
            factorHoras = 3;
        }

        EventoCicloAcademico eventoDictadoVeranoPregrado = this.getEventoDictadoClases(cicloDestino);
        EventoCicloAcademico eventoDictadoPosgrado = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloDestino, CLASES_EPG);
        EventoCicloAcademico eventoDictadoClases;

        List<CursoCurricula> cursosCurricula = cursoCurriculaDAO.allByTipoCursoCurriculaEnum(TipoCursoCurriculaEnum.GEN);
        Map<Long, CursoCurricula> curCurriculaMap = TypesUtil.convertListToMap("curso.id", cursosCurricula);
        TipoCursoCurricula tipoCursoGeneral = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.GEN);
        TipoCursoCurricula tipoCursoObligatorio = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.OBL);

        List<HorarioAula> horarariosPregrado = horarioAulaDAO.allByFechas(eventoDictadoVeranoPregrado.getFechaInicio(), eventoDictadoVeranoPregrado.getFechaFin());
        Map<String, List<HorarioAula>> mapHorarioAulaPre = TypesUtil.convertListToMapList("key", horarariosPregrado);
        List<HorarioAula> horarariosPosgrado = new ArrayList();
        if (eventoDictadoPosgrado != null) {
            horarariosPosgrado = horarioAulaDAO.allByFechas(eventoDictadoPosgrado.getFechaInicio(), eventoDictadoPosgrado.getFechaFin());
        }
        Map<String, List<HorarioAula>> mapHorarioAulaPos = TypesUtil.convertListToMapList("key", horarariosPosgrado);

        List<CursoCicloAcademico> cursosCicloGen = new ArrayList();
        Map<Long, CursoCicloAcademico> mapCursoCicloGen = new LinkedHashMap();
        List<GrupoSeccion> gposSeccionesGen = new ArrayList();
        List<PrecioCursoEstructura> precioCursoGen = new ArrayList();

        long t02 = System.currentTimeMillis();
        System.out.println("Data ciclo anterior obtenida en " + (t02 - t01) + " mseg");

        boolean esCursoPosgrado;
        boolean esCursoPregrado;

        for (GrupoSeccion gpoSeccOrigen : gsOrigenes) {
            esCursoPosgrado = gpoSeccOrigen.getAnexoBoletin().getAnexoSuperior().isAnexoCursosPostgrado();
            esCursoPregrado = !esCursoPosgrado;
            eventoDictadoClases = eventoDictadoVeranoPregrado;
            if (cicloDestino.getTipoEnum() == TipoCicloEnum.REG && esCursoPosgrado) {
                eventoDictadoClases = eventoDictadoPosgrado;
            }

            int horasTeoria = 0;
            int horasPractica = 0;

            Curso curso = gpoSeccOrigen.getCurso();
            try {
                if (curso.getTipoCreditoEnum() == TipoCreditoEnum.FIJO) {
                    horasTeoria = curso.getHorasTeoria() * factorHoras;
                    horasPractica = curso.getHorasPractica() * factorHoras;
                }
            } catch (Exception e) {
                throw new PhobosException("Error en la estructura del curso " + curso.getCodigo() + " " + curso.getNombre());
            }

            if (!cursos.contains(curso)) {
                CursoCicloAcademico cc = mapCursoCicloGen.get(curso.getId());
                if (cc == null) {
                    cursos.add(curso);

                    CursoCicloAcademico cursoCiclo = new CursoCicloAcademico();
                    cursoCiclo.setCicloAcademico(cicloDestino);
                    cursoCiclo.setPrecio(BigDecimal.ZERO);
                    cursoCiclo.setPrecioAdicional(BigDecimal.ZERO);
                    cursoCiclo.setEstado(EstadoEnum.ACT.name());

                    cursoCiclo.setHorasSemanalesTeoria(horasTeoria);
                    cursoCiclo.setHorasSemanalesPractica(horasPractica);
                    cursoCiclo.setCurso(curso);
                    cursoCiclo.setMinimoAlumnos(BigDecimal.ZERO);

                    cursoCiclo.setTipoCursoCurricula(tipoCursoObligatorio);
                    if (curCurriculaMap.get(curso.getId()) != null) {
                        cursoCiclo.setTipoCursoCurricula(tipoCursoGeneral);
                    }
                    cursosCicloGen.add(cursoCiclo);
                    mapCursoCicloGen.put(curso.getId(), cursoCiclo);
                }
            }

            String tpc = gpoSeccOrigen.getCurso().getTpc();
            Integer creditos = gpoSeccOrigen.getCurso().getCreditos();

            if (cicloDestino.isTipoNivelacion() && tpc != null && !tpcs.contains(tpc)) {
                tpcs.add(tpc);

                PrecioCursoEstructura precioCurso = new PrecioCursoEstructura();

                precioCurso.setCicloAcademico(cicloDestino);
                precioCurso.setFechaPrecio(new Date());
                precioCurso.setPrecio(BigDecimal.ZERO);
                precioCurso.setTpc(tpc);
                precioCurso.setCreditos(creditos);
                precioCurso.setUserPrecio(ds.getUsuario());
                precioCurso.setEstado(EstadoEnum.ACT.name());

                precioCursoGen.add(precioCurso);
            }

            String codigo = StringUtils.leftPad(CodeGenerator.getNextCode(codigos, 0), 3, '0');
            codigos.add(codigo);

            GrupoSeccion gpoSeccNew = new GrupoSeccion();
            gpoSeccNew.setCicloAcademico(cicloDestino);
            gpoSeccNew.setCurso(gpoSeccOrigen.getCurso());
            gpoSeccNew.setCodigo(codigo);
            gpoSeccNew.setCodigo2(codigo);
            gpoSeccNew.setVersion(BigDecimal.ONE.toString());
            gpoSeccNew.setEstadoGrupoEnum(EstadoGrupoSeccionEnum.ABI);
            gpoSeccNew.setEstadoPlanEnum(EstadoPlanCalificaEnum.PEND);
            gpoSeccNew.setHorasPractica(horasPractica);
            gpoSeccNew.setHorasTeoria(horasTeoria);
            gpoSeccNew.setAnexoBoletin(gpoSeccOrigen.getAnexoBoletin());
            gpoSeccNew.setEstado(gpoSeccOrigen.getEstado());
            gpoSeccNew.setTipoDictadoEnum(TipoDictadoGrupoSeccionEnum.SEM);

            gpoSeccNew.setSecciones(new ArrayList());
            gposSeccionesGen.add(gpoSeccNew);

            List<Seccion> seccionesOrigen = gpoSeccOrigen.getSecciones();
            List<HorarioSeccion> horarioTCUR = new ArrayList();

            int loopPCUR = 1;
            Seccion seccionSup = null;
            for (Seccion seccOrigen : seccionesOrigen) {

                Seccion seccNew = new Seccion();
                seccNew.setGrupoSeccion(gpoSeccNew);
                seccNew.setEstado(seccOrigen.getEstado());
                seccNew.setTipoSeccion(seccOrigen.getTipoSeccion());
                seccNew.setSituacionDocenteEnum(SituacionDocenteEnum.COR);
                seccNew.setHorasSemanales(seccOrigen.getHorasSemanales());
                seccNew.setVacantes(seccOrigen.getVacantes());
                seccNew.setGrupoHoras(seccOrigen.getGrupoHoras());
                seccNew.setRestriccionCapa(seccOrigen.getRestriccionCapa());
                seccNew.setFechaRegistro(today);
                seccNew.setUserRegistro(ds.getUsuario());
                seccNew.setSeccionSuperior(seccionSup);

                if (null != seccOrigen.getTipoSeccionEnum()) {
                    switch (seccOrigen.getTipoSeccionEnum()) {
                        case TEO:
                        case TCUR:
                            seccNew.setCodigo(codigo + "0");
                            seccNew.setCodigo2(codigo + "0");
                            seccionSup = seccNew;
                            break;
                        case PRA:
                            seccNew.setCodigo(codigo + "1");
                            seccNew.setCodigo2(codigo + "1");
                            break;
                        case PCUR:
                            seccNew.setCodigo(codigo + loopPCUR);
                            seccNew.setCodigo2(codigo + loopPCUR);
                            loopPCUR++;
                            break;
                        default:
                            break;
                    }
                }

                boolean tieneAula = false;

                Aula aula = seccOrigen.getAula();
                if (aula != null) {
                    Oficina oficina = aula.getOficinaSupervisora();
                    if (oficina != null) {
                        //  option copiar aulas oera
                        if ((oficina.isOficinaOera() && esCursoPregrado) && cicloClonacionBean.getCopiarAulasOera()) {
                            tieneAula = true;
                            seccNew.setAula(seccOrigen.getAula());

                            // option copiar aulas depts
                        } else if ((!oficina.isOficinaOera() && (esCursoPregrado || esCursoPosgrado)) && cicloClonacionBean.getCopiarAulasDptos()) {
                            tieneAula = true;
                            seccNew.setAula(seccOrigen.getAula());

                            // option copiar aulas posgrado
                        } else if ((oficina.isOficinaOera() && esCursoPosgrado) && cicloClonacionBean.getCopiarAulasPosgrado()) {
                            tieneAula = true;
                            seccNew.setAula(seccOrigen.getAula());
                        }
                    }
                }

                GrupoHoras gpoNew = seccNew.getGrupoHoras();
                List<DiaHoraGrupo> diasHorasSecc = new ArrayList();

                if (gpoNew != null) {
                    List<DiaHoraGrupo> diasHorasGpo = TypesUtil.getListNotNull(mapGrupoHorario.get(gpoNew.getId()));
                    diasHorasSecc = gpoSeccionService.searchDiasHorasByHorasSemanales(diasHorasGpo, seccNew.getHorasSemanales(), dias, false);
                    if (diasHorasSecc.isEmpty()) {
                        gpoNew = null;
                        seccNew.setGrupoHoras(null);
                    }
                }

                Aula aulaNew = (Aula) ObjectUtil.getParentTree(seccNew, "aula");
                boolean existeAula = aulaNew != null;
                if (gpoNew != null) {
                    boolean hayCruce = false;
                    Map<String, HorarioSeccion> mapHorarioTCUR = TypesUtil.convertListToMap("horaDia", horarioTCUR);
                    if (seccNew.getIsTipoSeccionPCUR()) { // si es PCUR
                        for (DiaHoraGrupo diaHoraSecc : diasHorasSecc) {
                            String horaDia = diaHoraSecc.getHoraDia();
                            HorarioSeccion hs = mapHorarioTCUR.get(horaDia);
                            if (hs != null) {
                                hayCruce = true;
                            }
                        }
                    }
                    if (hayCruce) {
                        gpoNew = null;
                        seccNew.setGrupoHoras(null);
                    }

                    if (gpoNew != null && existeAula) {
                        List<String> diasHorasSeccion = diasHorasSecc.stream().map(x -> x.getKey()).collect(Collectors.toList());
                        if (!seccNew.getAula().getPermiteCruceBoolean() && !diasHorasSeccion.isEmpty()) {
                            List<HorarioAula> horariosAulasFound = new ArrayList();
                            for (String key : diasHorasSeccion) {
                                String keyHorario = aulaNew.getId() + "-" + key;
                                if (esCursoPosgrado) {
                                    horariosAulasFound = TypesUtil.getListNotNull(mapHorarioAulaPos.get(keyHorario));
                                } else {
                                    horariosAulasFound = TypesUtil.getListNotNull(mapHorarioAulaPre.get(keyHorario));
                                }
                                if (!horariosAulasFound.isEmpty()) {
                                    break;
                                }
                            }

                            if (!horariosAulasFound.isEmpty()) {
                                seccNew.setAula(null);
                            }
                        }
                    }

                }
                seccNew.setHorarioSeccion(new ArrayList());
                seccNew.setHorariosAula(new ArrayList());
                seccNew.setDocenteSeccion(new ArrayList());
                seccNew.setRestriccionesCarrera(new ArrayList());
                seccNew.setRestriccionesFacultad(new ArrayList());
                seccNew.setRestriccionesModalidad(new ArrayList());
                seccNew.setRestriccionesRepitencia(new ArrayList());
                gpoSeccNew.getSecciones().add(seccNew);

                if (gpoNew != null) {
                    existeAula = ObjectUtil.getParentTree(seccNew, "aula.id") != null;

                    for (DiaHoraGrupo diaHoraSecc : diasHorasSecc) {
                        HorarioSeccion horarioSecc = new HorarioSeccion();
                        horarioSecc.setDia(diaHoraSecc.getDia());
                        horarioSecc.setHora(diaHoraSecc.getHora());
                        horarioSecc.setSeccion(seccNew);
                        horarioSecc.setFechaInicio(eventoDictadoClases.getFechaInicio());
                        horarioSecc.setFechaFin(eventoDictadoClases.getFechaFin());
                        horarioSecc.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                        if (ObjectUtil.getParentTree(seccNew, "aula.id") != null) {
                            horarioSecc.setAula(seccNew.getAula());
                        }
                        seccNew.getHorarioSeccion().add(horarioSecc);

                        if (existeAula && !aula.getPermiteCruceBoolean()) {
                            HorarioAula horarioAula = new HorarioAula();
                            horarioAula.setAula(seccNew.getAula());
                            horarioAula.setDia(diaHoraSecc.getDia());
                            horarioAula.setHora(diaHoraSecc.getHora());
                            horarioAula.setSeccion(seccNew);
                            horarioAula.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                            horarioAula.setTipoEnum(TipoHorarioAulaEnum.DICT);
                            horarioAula.setFechaInicio(eventoDictadoClases.getFechaInicio());
                            horarioAula.setFechaFin(eventoDictadoClases.getFechaFin());
                            seccNew.getHorariosAula().add(horarioAula);

                            String key = horarioAula.getKey();
                            List<HorarioAula> tmp1 = mapHorarioAulaPos.get(key);
                            if (tmp1 == null) {
                                tmp1 = new ArrayList();
                                tmp1.add(horarioAula);
                                mapHorarioAulaPos.put(key, tmp1);
                            } else {
                                tmp1.add(horarioAula);
                            }

                            List<HorarioAula> tmp2 = mapHorarioAulaPre.get(key);
                            if (tmp2 == null) {
                                tmp2 = new ArrayList();
                                tmp2.add(horarioAula);
                                mapHorarioAulaPre.put(key, tmp2);
                            } else {
                                tmp2.add(horarioAula);
                            }
                        }

                        if (seccNew.getIsTipoSeccionTCUR()) { // is es TCUR
                            horarioTCUR.add(horarioSecc);
                        }
                    }
                }

                List<DocenteSeccion> docenteSeccion = seccOrigen.getDocenteSeccion();
                docenteSeccion = (docenteSeccion == null) ? new ArrayList() : docenteSeccion;

                for (DocenteSeccion profeSeccOrigen : docenteSeccion) {
                    DocenteSeccion profeSeccNew = new DocenteSeccion();
                    profeSeccNew.setDocente(profeSeccOrigen.getDocente());
                    profeSeccNew.setEstado(profeSeccOrigen.getEstado());
                    profeSeccNew.setFechaInicio(eventoDictadoClases.getFechaInicio());
                    profeSeccNew.setFechaFin(eventoDictadoClases.getFechaFin());
                    profeSeccNew.setPrincipal(profeSeccOrigen.getPrincipal());
                    profeSeccNew.setSeccion(seccNew);
                    profeSeccNew.setPorcentajeCarga(profeSeccOrigen.getPorcentajeCarga());

                    seccNew.getDocenteSeccion().add(profeSeccNew);
                }

                if (docenteSeccion.size() == 1) {
                    seccNew.setSituacionDocenteEnum(SituacionDocenteEnum.COR);
                }

                List<RestriccionCarrera> restriccionCarreras = seccOrigen.getRestriccionesCarrera();
                if (restriccionCarreras == null) {
                    restriccionCarreras = new ArrayList();
                }
                for (RestriccionCarrera restriccCarrOrigen : restriccionCarreras) {
                    RestriccionCarrera restriccCarrNew = new RestriccionCarrera();
                    restriccCarrNew.setCarrera(restriccCarrOrigen.getCarrera());
                    restriccCarrNew.setEstado(restriccCarrOrigen.getEstado());
                    restriccCarrNew.setFechaRegistro(today);
                    restriccCarrNew.setSeccion(seccNew);
                    restriccCarrNew.setUsuarioRegistro(ds.getUsuario());

                    seccNew.getRestriccionesCarrera().add(restriccCarrNew);
                }

                List<RestriccionFacultad> restriccionFacultads = seccOrigen.getRestriccionesFacultad();
                if (restriccionFacultads == null) {
                    restriccionFacultads = new ArrayList();
                }
                for (RestriccionFacultad restriccFacOrigen : restriccionFacultads) {
                    RestriccionFacultad restriccFacNew = new RestriccionFacultad();
                    restriccFacNew.setEstado(restriccFacOrigen.getEstado());
                    restriccFacNew.setFacultad(restriccFacOrigen.getFacultad());
                    restriccFacNew.setFechaRegistro(today);
                    restriccFacNew.setSeccion(seccNew);
                    restriccFacNew.setUsuarioRegistro(ds.getUsuario());

                    seccNew.getRestriccionesFacultad().add(restriccFacNew);
                }

                List<RestriccionModalidad> restriccionModalidads = seccOrigen.getRestriccionesModalidad();
                if (restriccionModalidads == null) {
                    restriccionModalidads = new ArrayList();
                }
                for (RestriccionModalidad restriccModalOrigen : restriccionModalidads) {
                    RestriccionModalidad restriccModalNew = new RestriccionModalidad();
                    restriccModalNew.setEstado(restriccModalOrigen.getEstado());
                    restriccModalNew.setFechaRegistro(today);
                    restriccModalNew.setModalidadEstudio(restriccModalOrigen.getModalidadEstudio());
                    restriccModalNew.setSeccion(seccNew);
                    restriccModalNew.setUsuarioRegistro(ds.getUsuario());

                    seccNew.getRestriccionesModalidad().add(restriccModalNew);
                }

                List<RestriccionRepitencia> restriccionRepitencias = seccOrigen.getRestriccionesRepitencia();
                if (restriccionRepitencias == null) {
                    restriccionRepitencias = new ArrayList();
                }
                for (RestriccionRepitencia restriccRepiteOrigen : restriccionRepitencias) {
                    RestriccionRepitencia restriccRepiteNew = new RestriccionRepitencia();
                    restriccRepiteNew.setEstado(restriccRepiteOrigen.getEstado());
                    restriccRepiteNew.setFechaRegistro(today);
                    restriccRepiteNew.setSeccion(seccNew);
                    restriccRepiteNew.setTipoRepitencia(restriccRepiteOrigen.getTipoRepitencia());
                    restriccRepiteNew.setUsuarioRegistro(ds.getUsuario());

                    seccNew.getRestriccionesRepitencia().add(restriccRepiteNew);
                }
            }
        }

        long t03 = System.currentTimeMillis();
        System.out.println("Generacion de data en " + (t03 - t02) + " mseg");

        cursoCicloAcademicoDAO.saveList(cursosCicloGen);
        precioCursoEstructuraDAO.saveList(precioCursoGen);

        grupoSeccionDAO.saveList(gposSeccionesGen);
        List<GrupoSeccion> gposSeccionesBD = grupoSeccionDAO.allActivoByCiclo(cicloDestino);
        Map<String, GrupoSeccion> mapGpoSeccBD = TypesUtil.convertListToMap("codigo", gposSeccionesBD);

        for (GrupoSeccion grupoSeccion : gposSeccionesGen) {
            GrupoSeccion gsBD = mapGpoSeccBD.get(grupoSeccion.getCodigo());
            grupoSeccion.setId(gsBD.getId());
        }

        List<Seccion> seccionesGen = new ArrayList();
        for (GrupoSeccion grupoSeccion : gposSeccionesGen) {
            seccionesGen.addAll(grupoSeccion.getSecciones());
        }

        seccionDAO.saveList(seccionesGen);
        List<Seccion> seccionesPCUR = new ArrayList();
        List<Seccion> seccionesBD = seccionDAO.allActivosByGposSeccion(gposSeccionesGen);
        Map<String, Seccion> mapSeccionBD = TypesUtil.convertListToMap("codigo", seccionesBD);
        for (Seccion seccion : seccionesGen) {
            Seccion seccBD = mapSeccionBD.get(seccion.getCodigo());
            if (seccion.getSeccionSuperior() != null) {
                Seccion seccSuperBD = mapSeccionBD.get(seccion.getSeccionSuperior().getCodigo());
                seccion.setSeccionSuperior(seccSuperBD);
                seccionesPCUR.add(seccion);
            }
            seccion.setId(seccBD.getId());
        }
        seccionDAO.updateList(seccionesPCUR, "seccionSuperior");

        List<HorarioSeccion> horariosSecc = new ArrayList();
        List<HorarioAula> horariosAulaSecc = new ArrayList();
        List<DocenteSeccion> docentesSecc = new ArrayList();
        List<RestriccionCarrera> restriccionCarr = new ArrayList();
        List<RestriccionFacultad> restriccionFac = new ArrayList();
        List<RestriccionModalidad> restriccionModa = new ArrayList();
        List<RestriccionRepitencia> restriccionRep = new ArrayList();

        for (Seccion seccion : seccionesGen) {
            horariosSecc.addAll(seccion.getHorarioSeccion());
            horariosAulaSecc.addAll(seccion.getHorariosAula());
            docentesSecc.addAll(seccion.getDocenteSeccion());
            restriccionCarr.addAll(seccion.getRestriccionesCarrera());
            restriccionFac.addAll(seccion.getRestriccionesFacultad());
            restriccionModa.addAll(seccion.getRestriccionesModalidad());
            restriccionRep.addAll(seccion.getRestriccionesRepitencia());
        }

        horarioSeccionDAO.saveList(horariosSecc);
        horarioAulaDAO.saveList(horariosAulaSecc);
        docenteSeccionDAO.saveList(docentesSecc);
        restriccionCarreraDAO.saveList(restriccionCarr);
        restriccionFacultadDAO.saveList(restriccionFac);
        restriccionModalidadDAO.saveList(restriccionModa);
        restriccionRepitenciaDAO.saveList(restriccionRep);

        long t04 = System.currentTimeMillis();
        System.out.println("Salvando data en " + (t04 - t03) + " mseg");

    }

    private void validarClonacion(CicloAcademico cicloAnalisis) {
        EventoAcademicoEnum eventoEnum = cicloAnalisis.getTipoEnum() == TipoCicloEnum.NIV ? CLASES_VER : CLASES_PRE;

        EventoCicloAcademico eventoClases1 = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAnalisis, eventoEnum);
        Assert.isNotNull(eventoClases1, "No se configuró el evento " + eventoEnum.getValue() + " para el ciclo " + cicloAnalisis.getDescripcion());

        if (eventoEnum == CLASES_PRE) {
            EventoCicloAcademico eventoClases2 = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAnalisis, CLASES_EPG);
            Assert.isNotNull(eventoClases2, "No se configuró el evento " + CLASES_EPG.getValue() + " para el ciclo " + cicloAnalisis.getDescripcion());
        }

        List<DiaHoraGrupo> diaHoraGrupos = diaHoraGrupoDAO.allByCicloAndTipoCiclo(cicloAnalisis);
        Assert.isNotNull(diaHoraGrupos, "No existe grupo horarios para el ciclo " + cicloAnalisis.getDescripcion());
        Assert.isFalse(diaHoraGrupos.isEmpty(), "No existe grupo horarios para el ciclo " + cicloAnalisis.getDescripcion());
    }

    @Override
    public GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo, DataSessionPivot ds) {
        List<AnexoBoletin> anexosAll = anexoBoletinDAO.allAnexosHijos();
        List<AnexoBoletin> anexos = verificadorService.anexosInferioresByOficina(ds, anexosAll);
        GpoSeccionResumen resumen = grupoSeccionDAO.resumenByCiclo(ciclo, anexos);
        resumen.setActividades(resumen.getActividades() == null ? 0 : resumen.getActividades());
        resumen.setDepartamentos(resumen.getDepartamentos() == null ? 0 : resumen.getDepartamentos());
        resumen.setIngresantes(resumen.getIngresantes() == null ? 0 : resumen.getIngresantes());
        resumen.setPostGrados(resumen.getPostGrados() == null ? 0 : resumen.getPostGrados());

        return resumen;
    }

    @Override
    @Transactional
    public void limpiarCodigo2(CicloAcademico cicloForm, DataSessionPivot ds) {
        CicloAcademico cicloBD = cicloAcademicoDAO.find(cicloForm);
        Assert.isTrue(cicloBD.getFechaCierreOrden() == null, "Ya no está permitido limpiar el código de las secciones");
        seccionDAO.setNullCodigo2ByCiclo(cicloBD);
    }

    @Override
    @Transactional
    public void reordenar(CicloAcademico ciclo, DataSessionPivot ds) {

        CicloAcademico cicloDB = cicloAcademicoDAO.find(ciclo);
        Assert.isNull(cicloDB.getFechaCierreOrden(), "Ya no se permite ordenar código");

        cicloDB.setFechaOrdenHorarios(new Date());
        cicloAcademicoDAO.update(cicloDB);

        List<GrupoSeccion> gpoSecciones = grupoSeccionDAO.allOrdenadoByCiclo(ciclo);
        logger.debug("GrupoSeccion size  {}", gpoSecciones.size());

        List<Seccion> secciones = seccionDAO.allSeccionOrderByciclo(ciclo);
        Map<Long, List<Seccion>> mapSecciones = TypesUtil.convertListToMapList("grupoSeccion.id", secciones);

        for (GrupoSeccion gs : gpoSecciones) {
            gs.setSecciones(mapSecciones.get(gs.getId()));
        }

        List<String> codigos = new ArrayList();
        List<Seccion> seccionesUpd = new ArrayList();
        List<GrupoSeccion> gpoSeccionesUpd = new ArrayList();

        for (GrupoSeccion gpoSeccBD : gpoSecciones) {

            String codigo = StringUtils.leftPad(CodeGenerator.getNextCode(codigos, 0), 3, '0');

            List<Seccion> seccionesGpoSecc = gpoSeccBD.getSecciones();
            int loopPCUR = 1;
            codigos.add(codigo);

            for (Seccion seccion : seccionesGpoSecc) {
                Seccion secc = new Seccion(seccion.getId());

                if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.TEO || seccion.getTipoSeccionEnum() == TipoSeccionEnum.TCUR) {
                    secc.setCodigo2(codigo + "0");
                } else if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PRA) {
                    secc.setCodigo2(codigo + "1");
                } else if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                    secc.setCodigo2(codigo + loopPCUR);
                    loopPCUR++;
                }

                seccionesUpd.add(secc);
            }

            GrupoSeccion gpoSecc = new GrupoSeccion(gpoSeccBD.getId());
            gpoSecc.setCodigo2(codigo);
            gpoSeccionesUpd.add(gpoSecc);
        }
        logger.debug("Ya se terminó de actualizar los {} gpo-secciones", gpoSecciones.size());

        grupoSeccionDAO.updateCodigo2(gpoSeccionesUpd);
        seccionDAO.updateCodigo2(seccionesUpd);

    }

    @Async
    @Override
    @Transactional
    public void limpiarCicloAsyn(CicloAcademico ciclo) {
        this.limpiarCiclo(ciclo);
    }

    @Override
    @Transactional
    public void limpiarCiclo(CicloAcademico ciclo) {
        CicloAcademico cicloDB = cicloAcademicoDAO.find(ciclo);

        Assert.isNull(cicloDB.getFechaCierreClonacion(), "El ciclo ya no permite limpiar la clonación");
        Assert.isNotNull(cicloDB.getFechaClonacion(), "El ciclo aun no ha sido clonado");
        cicloDB.setFechaClonacion(null);
        cicloDB.setFechaCierreOrden(null);
        cicloDB.setFechaOrdenHorarios(null);
        cicloAcademicoDAO.update(cicloDB);

        restriccionRepitenciaDAO.deleteAllByCiclo(ciclo);
        restriccionModalidadDAO.deleteAllByCiclo(ciclo);
        restriccionFacultadDAO.deleteAllByCiclo(ciclo);
        restriccionCarreraDAO.deleteAllByCiclo(ciclo);
        docenteSeccionDAO.deleteAllByCiclo(ciclo);
        horarioSeccionDAO.deleteAllByCiclo(ciclo);
        horarioAulaDAO.deleteAllByCiclo(ciclo);
        vacanteAlumnoDAO.deleteAllByCiclo(ciclo);
        matriculaSeccionDAO.deleteAllByCiclo(ciclo); // No deberia
        evaluacionDAO.deleteAllByCiclo(ciclo); // No deberia
        seccionDAO.deleteAllNotSuperiorByCiclo(ciclo);
        seccionDAO.deleteAllByCiclo(ciclo);
        evaluacionExpandidaDAO.deleteAllByCiclo(ciclo); // No deberia
        evaluacionSeccionDAO.deleteAllByCiclo(ciclo); // No deberia
        grupoSeccionDAO.deleteAllByCiclo(ciclo);
        precioCursoEstructuraDAO.deleteAllByCiclo(ciclo);
        cursoCicloAcademicoDAO.deleteAllByCiclo(ciclo);

    }

    @Override
    public CicloAcademico findCicloPregrado(CicloAcademico ciclo) {
        return cicloAcademicoDAO.find(ciclo, ModalidadEstudioEnum.PRE);
    }

    @Override
    public CicloAcademico findCicloPosgrado(CicloAcademico ciclo) {
        return cicloAcademicoDAO.find(ciclo, ModalidadEstudioEnum.EPG);
    }

    @Override
    @Transactional
    public void cerrarClonacion(CicloAcademico cicloForm) {
        CicloAcademico cicloDB = cicloAcademicoDAO.find(cicloForm);
        Assert.isNull(cicloDB.getFechaCierreClonacion(), "Ya se efectúo la finalización de la clonación de este ciclo");
        cicloDB.setFechaCierreClonacion(new Date());
        cicloAcademicoDAO.update(cicloDB);
    }

    @Override
    @Transactional
    public void cerrarOrden(CicloAcademico cicloOrdenForm) {
        CicloAcademico cicloOrdenDB = cicloAcademicoDAO.find(cicloOrdenForm);
        Assert.isNull(cicloOrdenDB.getFechaCierreOrden(), "La opción ordenar código ya está cerrado");
        cicloOrdenDB.setFechaCierreOrden(new Date());
    }

    @Override
    @Transactional
    public void verBoletin(CicloAcademico cicloProgForm, ModalidadEstudioEnum modalidadEnum) {
        CicloAcademico cicloProgDB = cicloAcademicoDAO.find(cicloProgForm, modalidadEnum).clone();
        CicloAcademico cicloBoletin = cicloAcademicoDAO.findVerBoletin(modalidadEnum);

        if (cicloBoletin != null) {
            if (cicloBoletin.getId() != cicloProgDB.getId().longValue()) {
                cicloBoletin = cicloBoletin.clone();
                cicloBoletin.setVerBoletin(Boolean.FALSE);
                cicloAcademicoDAO.updateColumns(cicloBoletin, "verBoletin");
            }
        }

        cicloProgDB.setVerBoletin(Boolean.TRUE);
        cicloProgDB.setActualizarBoletin(Boolean.TRUE);
        cicloAcademicoDAO.updateColumns(cicloProgDB, "verBoletin", "actualizarBoletin");
    }

    private EventoCicloAcademico getEventoDictadoClases(CicloAcademico cicloAcademico) {
        EventoAcademicoEnum eventoClasesEnum = cicloAcademico.getTipoEnum() == TipoCicloEnum.NIV ? CLASES_VER : CLASES_PRE;
        EventoCicloAcademico eventoCiclo = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, eventoClasesEnum);
        return eventoCiclo;
    }

}
