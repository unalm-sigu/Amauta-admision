package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.clonarciclo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.CodeGenerator;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
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
import pe.edu.lamolina.model.enums.SituacionDocenteEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoCreditoEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.GpoSeccionService;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
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
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.vacante.VacanteAlumnoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ClonarCicloServiceImp implements ClonarCicloService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    RestriccionCarreraDAO restriccionCarreraDAO;

    @Autowired
    RestriccionFacultadDAO restriccionFacultadDAO;

    @Autowired
    RestriccionModalidadDAO restriccionModalidadDAO;

    @Autowired
    RestriccionRepitenciaDAO restriccionRepitenciaDAO;

    @Autowired
    GpoSeccionService gpoSeccionService;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    PrecioCursoEstructuraDAO precioCursoEstructuraDAO;

    @Autowired
    CursoCicloAcademicoDAO cursoCicloAcademicoDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Autowired
    DiaHoraGrupoDAO diaHoraGrupoDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    DiaDAO diaDAO;

    @Autowired
    CursoCurriculaDAO cursoCurriculaDAO;

    @Autowired
    TipoCursoCurriculaDAO tipoCursoCurriculaDAO;

    @Autowired
    VacanteAlumnoDAO vacanteAlumnoDAO;

    @Override
    @Transactional
    public void clonarCiclo(CicloAcademico cicloOrigenForm, CicloAcademico cicloDestinoForm, DataSessionPivot ds) {
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

        EventoCicloAcademico eventoDictadoPregrado = this.getEventoCicloAcademico(cicloDestino);
        EventoCicloAcademico eventoDictadoPosgrado = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloDestino, CLASES_EPG);
        EventoCicloAcademico eventoDictadoClases = null;

        List<CursoCurricula> cursosCurricula = cursoCurriculaDAO.allByTipoCursoCurriculaEnum(TipoCursoCurriculaEnum.GEN);
        Map<Long, CursoCurricula> curCurriculaMap = TypesUtil.convertListToMap("curso.id", cursosCurricula);
        TipoCursoCurricula tipocursogeneral = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.GEN);
        TipoCursoCurricula tipocursoobligatorio = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.OBL);

        for (GrupoSeccion ggss : gsOrigenes) {
            Curso curso = ggss.getCurso();
            eventoDictadoClases = eventoDictadoPregrado;
            if (cicloDestino.getTipoEnum() == TipoCicloEnum.REG && curso.getModalidadEstudio().isPostgrado()) {
                eventoDictadoClases = eventoDictadoPosgrado;
            }

            int horasTeoria = 0;
            int horasPractica = 0;

            try {
                if (curso.getTipoCreditoEnum() == TipoCreditoEnum.FIJO) {
                    horasTeoria = curso.getHorasTeoria() * factorHoras;
                    horasPractica = curso.getHorasPractica() * factorHoras;
                }
            } catch (Exception e) {
                throw new PhobosException("Error en la estructura del curso " + curso.getCodigo() + " " + curso.getNombre());
            }

            if (!cursos.contains(curso)) {
                cursos.add(curso);

                CursoCicloAcademico cca = new CursoCicloAcademico();
                cca.setCicloAcademico(cicloDestino);
                cca.setPrecio(BigDecimal.ZERO);
                cca.setPrecioAdicional(BigDecimal.ZERO);
                cca.setEstado(EstadoEnum.ACT.name());

                cca.setHorasSemanalesTeoria(horasTeoria);
                cca.setHorasSemanalesPractica(horasPractica);
                cca.setCurso(curso);
                cca.setMinimoAlumnos(BigDecimal.ZERO);

                cca.setTipoCursoCurricula(tipocursoobligatorio);
                if (curCurriculaMap.get(curso.getId()) != null) {
                    cca.setTipoCursoCurricula(tipocursogeneral);
                }
                cursoCicloAcademicoDAO.save(cca);
            }

            String tpc = ggss.getCurso().getTpc();
            Integer creditos = ggss.getCurso().getCreditos();

            if (cicloDestino.getNumeroCiclo().equals("0") && tpc != null && !tpcs.contains(tpc)) {
                tpcs.add(tpc);

                PrecioCursoEstructura pce = new PrecioCursoEstructura();

                pce.setCicloAcademico(cicloDestino);
                pce.setFechaPrecio(new Date());
                pce.setPrecio(BigDecimal.ZERO);
                pce.setTpc(tpc);
                pce.setCreditos(creditos);
                pce.setUserPrecio(ds.getUsuario());
                pce.setEstado(EstadoEnum.ACT.name());

                precioCursoEstructuraDAO.save(pce);
            }

            String codigo = StringUtils.leftPad(CodeGenerator.getNextCode(codigos, 0), 3, '0');

            GrupoSeccion gs = new GrupoSeccion();
            gs.setCicloAcademico(cicloDestino);
            gs.setCurso(ggss.getCurso());
            gs.setCodigo(codigo);
            gs.setCodigo2(codigo);

            codigos.add(codigo);

            gs.setVersion(BigDecimal.ONE.toString());
            gs.setEstadoGrupoEnum(EstadoGrupoSeccionEnum.ABI);
            gs.setEstadoPlanEnum(EstadoPlanCalificaEnum.PEND);

            gs.setHorasPractica(horasPractica);
            gs.setHorasTeoria(horasTeoria);
            gs.setAnexoBoletin(ggss.getAnexoBoletin());
            gs.setEstado(ggss.getEstado());

            grupoSeccionDAO.save(gs);

            List<Seccion> seccionesOrigen = ggss.getSecciones();
            List<HorarioSeccion> horarioTCUR = new ArrayList();

            int loopPCUR = 1;
            Seccion seccionSup = null;
            for (Seccion seccOrigen : seccionesOrigen) {

                Seccion seccClone = new Seccion();
                seccClone.setGrupoSeccion(gs);

                seccClone.setEstado(seccOrigen.getEstado());
                seccClone.setTipoSeccion(seccOrigen.getTipoSeccion());
                seccClone.setSituacionDocenteEnum(SituacionDocenteEnum.ERR);
                seccClone.setHorasSemanales(seccOrigen.getHorasSemanales());
                seccClone.setVacantes(seccOrigen.getVacantes());
                seccClone.setFechaRegistro(today);
                seccClone.setUserRegistro(ds.getUsuario());
                seccClone.setMatriculados(0);
                seccClone.setReservados(0);
                seccClone.setPrematriculados(0);
                seccClone.setRetirados(0);
                seccClone.setGrupoHoras(seccOrigen.getGrupoHoras());
                seccClone.setRestriccionCapa(seccOrigen.getRestriccionCapa());

                seccClone.setSeccionSuperior(seccionSup);

                if (seccOrigen.getTipoSeccionEnum() == TipoSeccionEnum.TEO || seccOrigen.getTipoSeccionEnum() == TipoSeccionEnum.TCUR) {
                    seccClone.setCodigo(codigo + "0");
                    seccClone.setCodigo2(codigo + "0");
                    seccionSup = seccClone;
                } else if (seccOrigen.getTipoSeccionEnum() == TipoSeccionEnum.PRA) {
                    seccClone.setCodigo(codigo + "1");
                    seccClone.setCodigo2(codigo + "1");
                } else if (seccOrigen.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                    seccClone.setCodigo(codigo + loopPCUR);
                    seccClone.setCodigo2(codigo + loopPCUR);
                    loopPCUR++;
                }

                GrupoHoras gpoNew = seccClone.getGrupoHoras();
                List<DiaHoraGrupo> diasHorasSecc = new ArrayList();

                try {
                    if (gpoNew != null) {
                        List<DiaHoraGrupo> diasHorasGpo = mapGrupoHorario.get(gpoNew.getId());
                        diasHorasGpo = (diasHorasGpo == null) ? new ArrayList() : diasHorasGpo;
                        diasHorasSecc = gpoSeccionService.searchDiasHorasByHorasSemanales(diasHorasGpo, seccClone.getHorasSemanales(), dias);
                    }
                } catch (Exception e) {
                    gpoNew = null;
                    seccClone.setGrupoHoras(null);
                }

                if (gpoNew != null) {
                    boolean hayCruce = false;
                    Map<String, HorarioSeccion> mapHorarioTCUR = TypesUtil.convertListToMap("horaDia", horarioTCUR);
                    if (seccClone.getIsTipoSeccionPCUR()) { // si es PCUR
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
                        seccClone.setGrupoHoras(null);
                        System.out.println("Cruzado");
                    }
                }
                seccionDAO.save(seccClone);

                if (gpoNew != null) {

                    for (DiaHoraGrupo diaHoraSecc : diasHorasSecc) {
                        HorarioSeccion horarioSecc = new HorarioSeccion();
                        horarioSecc.setDia(diaHoraSecc.getDia());
                        horarioSecc.setHora(diaHoraSecc.getHora());
                        horarioSecc.setSeccion(seccClone);
                        horarioSecc.setFechaInicio(eventoDictadoClases.getFechaInicio());
                        horarioSecc.setFechaFin(eventoDictadoClases.getFechaFin());
                        horarioSecc.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                        horarioSeccionDAO.save(horarioSecc);

                        if (seccClone.getIsTipoSeccionTCUR()) { // is es TCUR
                            horarioTCUR.add(horarioSecc);
                        }
                    }

                }

                List<DocenteSeccion> docenteSeccion = seccOrigen.getDocenteSeccion();
                docenteSeccion = (docenteSeccion == null) ? new ArrayList() : docenteSeccion;

                for (DocenteSeccion dsOrigen : docenteSeccion) {
                    DocenteSeccion dsClone = new DocenteSeccion();
                    dsClone.setDocente(dsOrigen.getDocente());
                    dsClone.setEstado(dsOrigen.getEstado());
                    dsClone.setFechaInicio(eventoDictadoClases.getFechaInicio());
                    dsClone.setFechaFin(eventoDictadoClases.getFechaFin());
                    dsClone.setPrincipal(dsOrigen.getPrincipal());
                    dsClone.setSeccion(seccClone);
                    dsClone.setPorcentajeCarga(dsOrigen.getPorcentajeCarga());

                    docenteSeccionDAO.save(dsClone);
                }

                if (docenteSeccion.size() == 1) {
                    seccClone.setSituacionDocenteEnum(SituacionDocenteEnum.COR);
                    seccionDAO.update(seccClone);
                }

                List<RestriccionCarrera> restriccionCarreras = seccOrigen.getRestriccionesCarrera();
                if (restriccionCarreras == null) {
                    restriccionCarreras = new ArrayList();
                }
                for (RestriccionCarrera rc : restriccionCarreras) {
                    RestriccionCarrera rcClon = new RestriccionCarrera();
                    rcClon.setCarrera(rc.getCarrera());
                    rcClon.setEstado(rc.getEstado());
                    rcClon.setFechaRegistro(today);
                    rcClon.setSeccion(seccClone);
                    rcClon.setUsuarioRegistro(ds.getUsuario());
                    restriccionCarreraDAO.save(rcClon);
                }

                List<RestriccionFacultad> restriccionFacultads = seccOrigen.getRestriccionesFacultad();
                if (restriccionFacultads == null) {
                    restriccionFacultads = new ArrayList();
                }
                for (RestriccionFacultad rf : restriccionFacultads) {
                    RestriccionFacultad rfClon = new RestriccionFacultad();
                    rfClon.setEstado(rf.getEstado());
                    rfClon.setFacultad(rf.getFacultad());
                    rfClon.setFechaRegistro(today);
                    rfClon.setSeccion(seccClone);
                    rfClon.setUsuarioRegistro(ds.getUsuario());
                    restriccionFacultadDAO.save(rfClon);
                }

                List<RestriccionModalidad> restriccionModalidads = seccOrigen.getRestriccionesModalidad();
                if (restriccionModalidads == null) {
                    restriccionModalidads = new ArrayList();
                }
                for (RestriccionModalidad rm : restriccionModalidads) {
                    RestriccionModalidad rmClon = new RestriccionModalidad();
                    rmClon.setEstado(rm.getEstado());
                    rmClon.setFechaRegistro(today);
                    rmClon.setModalidadEstudio(rm.getModalidadEstudio());
                    rmClon.setSeccion(seccClone);
                    rmClon.setUsuarioRegistro(ds.getUsuario());
                    restriccionModalidadDAO.save(rmClon);
                }

                List<RestriccionRepitencia> restriccionRepitencias = seccOrigen.getRestriccionesRepitencia();
                if (restriccionRepitencias == null) {
                    restriccionRepitencias = new ArrayList();
                }
                for (RestriccionRepitencia rp : restriccionRepitencias) {
                    RestriccionRepitencia rpClon = new RestriccionRepitencia();
                    rpClon.setEstado(rp.getEstado());
                    rpClon.setFechaRegistro(today);
                    rpClon.setSeccion(seccClone);
                    rpClon.setTipoRepitencia(rp.getTipoRepitencia());
                    rpClon.setUsuarioRegistro(ds.getUsuario());
                    restriccionRepitenciaDAO.save(rpClon);
                }

            }

        }

    }

    private void validarClonacion(CicloAcademico cicloAnalisis) {
        EventoAcademicoEnum eventoEnum = cicloAnalisis.getTipoEnum() == TipoCicloEnum.NIV ? CLASES_VER : CLASES_PRE;

        EventoCicloAcademico eventoCiclo = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAnalisis, eventoEnum);

        Assert.isNotNull(eventoCiclo, "No se configuró el evento " + eventoEnum.getValue() + " para el ciclo " + cicloAnalisis.getDescripcion());

        if (eventoEnum == CLASES_PRE) {
            eventoCiclo = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAnalisis, CLASES_EPG);
            Assert.isNotNull(eventoCiclo, "No se configuró el evento " + CLASES_EPG.getValue() + " para el ciclo " + cicloAnalisis.getDescripcion());
        }

        List<DiaHoraGrupo> diaHoraGrupos = diaHoraGrupoDAO.allByCicloAndTipoCiclo(cicloAnalisis);

        Assert.isNotNull(diaHoraGrupos, "No existe grupo horarios para el ciclo " + cicloAnalisis.getDescripcion());
        Assert.isFalse(diaHoraGrupos.isEmpty(), "No existe grupo horarios para el ciclo " + cicloAnalisis.getDescripcion());
    }

    @Override
    public GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo) {
        GpoSeccionResumen resumen = grupoSeccionDAO.resumenByCiclo(ciclo);
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
        Map<Long, List<Seccion>> seccionesMap = TypesUtil.convertListToMapList("grupoSeccion.id", secciones);

        for (GrupoSeccion gsOrigene : gpoSecciones) {
            gsOrigene.setSecciones(seccionesMap.get(gsOrigene.getId()));
        }

        List<String> codigos = new ArrayList();
        long t1 = System.currentTimeMillis();

        for (GrupoSeccion gpoSecc : gpoSecciones) {

            String codigo = StringUtils.leftPad(CodeGenerator.getNextCode(codigos, 0), 3, '0');

            List<Seccion> seccionesGpoSecc = gpoSecc.getSecciones();

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
                seccionDAO.updateCodigo2(secc);
                long t2 = System.currentTimeMillis();
                if (t2 - t1 > 1000) {
                    logger.debug("Ya se han actualizado {} de {} gpo-secciones", codigos.size(), gpoSecciones.size());
                    t1 = System.currentTimeMillis();
                }
            }

        }
        logger.debug("Ya se terminó de actualizar los {} gpo-secciones", gpoSecciones.size());

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
        seccionDAO.deleteAllNotSuperiorByCiclo(ciclo);
        seccionDAO.deleteAllByCiclo(ciclo);
        grupoSeccionDAO.deleteAllByCiclo(ciclo);
        precioCursoEstructuraDAO.deleteAllByCiclo(ciclo);
        cursoCicloAcademicoDAO.deleteAllByCiclo(ciclo);

    }

    @Override
    public CicloAcademico findCiclo(CicloAcademico ciclo) {
        return cicloAcademicoDAO.find(ciclo);
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
    public void verBoletin(CicloAcademico cicloProgForm) {
        CicloAcademico cicloProgDB = cicloAcademicoDAO.find(cicloProgForm);
        CicloAcademico cicloBoletin = cicloAcademicoDAO.findVerBoletin();

        if (cicloBoletin != null) {
            if (cicloBoletin.getId() != cicloProgDB.getId().longValue()) {
                cicloBoletin.setVerBoletin(Boolean.FALSE);
            }
        }
        cicloProgDB.setVerBoletin(Boolean.TRUE);
        cicloProgDB.setActualizarBoletin(Boolean.TRUE);
        cicloAcademicoDAO.update(cicloProgDB);
    }

    private EventoCicloAcademico getEventoCicloAcademico(CicloAcademico cicloAcademico) {
        EventoAcademicoEnum eventoEnum = cicloAcademico.getTipoEnum() == TipoCicloEnum.NIV ? CLASES_VER : CLASES_PRE;
        EventoCicloAcademico eventoCiclo = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, eventoEnum);
        return eventoCiclo;
    }

}
