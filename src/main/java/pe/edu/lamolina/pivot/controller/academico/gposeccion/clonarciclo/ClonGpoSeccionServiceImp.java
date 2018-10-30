package pe.edu.lamolina.pivot.controller.academico.gposeccion.clonarciclo;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.PrecioCursoEstructura;
import pe.edu.lamolina.model.academico.RestriccionCarrera;
import pe.edu.lamolina.model.academico.RestriccionFacultad;
import pe.edu.lamolina.model.academico.RestriccionModalidad;
import pe.edu.lamolina.model.academico.RestriccionRepitencia;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.CLASES_EPG;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.CLASES_PRE;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.CLASES_VER;
import pe.edu.lamolina.model.enums.SituacionDocenteEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.pivot.controller.academico.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.controller.academico.gposeccion.GpoSeccionService;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;
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
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ClonGpoSeccionServiceImp implements ClonGpoSeccionService {

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

    @Override
    @Transactional
    public void clonarCiclo(CicloAcademico cicloOrigenForm, CicloAcademico cicloDestinoForm, DataSessionPivot ds) {

        CicloAcademico cicloOrigen = cicloAcademicoDAO.find(cicloOrigenForm.getId());
        CicloAcademico cicloDestino = cicloAcademicoDAO.find(cicloDestinoForm.getId());
        logger.debug("copiar del ciclo {} al ciclo {}", cicloOrigen.getId(), cicloDestino.getId());

        if (cicloOrigen.getId().longValue() == cicloDestino.getId()) {
            throw new PhobosException("El ciclo académico es el mismo al que desea copiar");
        }

        validarClonacion(cicloDestino);

        List<PrecioCursoEstructura> precioCursoEstructura = precioCursoEstructuraDAO.allByCiclo(cicloDestino);

        List<CursoCicloAcademico> cursoCicloAcademico = cursoCicloAcademicoDAO.allByCiclo(cicloDestino);

        Set<String> tpcs = precioCursoEstructura.stream().map(PrecioCursoEstructura::getTpc).collect(Collectors.toSet());

        Set<Curso> cursos = cursoCicloAcademico.stream().map(CursoCicloAcademico::getCurso).collect(Collectors.toSet());

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
            gsOrigene.setSecciones(seccionesMap.get(gsOrigene.getId()));

        }

        List<String> codigos = new ArrayList();

        Date today = new Date();

        for (GrupoSeccion ggss : gsOrigenes) {

            String codigo = StringUtils.leftPad(CodeGenerator.getNextCode(codigos, 0), 3, '0');

            if (!cursos.contains(ggss.getCurso())) {
                cursos.add(ggss.getCurso());

                CursoCicloAcademico cca = new CursoCicloAcademico();
                cca.setCicloAcademico(cicloDestino);
                cca.setPrecio(BigDecimal.ZERO);
                cca.setPrecioAdicional(BigDecimal.ZERO);
                cca.setEstado(EstadoEnum.ACT.name());
                cca.setCurso(ggss.getCurso());
                cca.setMinimoAlumnos(BigDecimal.ZERO);

                cursoCicloAcademicoDAO.save(cca);
            }

            String tpc = ggss.getCurso().getTpc();

            if (cicloDestino.getNumeroCiclo().equals("0") && tpc != null && !tpcs.contains(tpc)) {
                tpcs.add(tpc);

                PrecioCursoEstructura pce = new PrecioCursoEstructura();

                pce.setCicloAcademico(cicloDestino);
                pce.setFechaPrecio(new Date());
                pce.setPrecio(BigDecimal.ZERO);
                pce.setTpc(tpc);
                pce.setUserPrecio(ds.getUsuario());
                pce.setEstado(EstadoEnum.ACT.name());

                precioCursoEstructuraDAO.save(pce);
            }

            GrupoSeccion gs = new GrupoSeccion();
            gs.setCicloAcademico(cicloDestino);
            gs.setCurso(ggss.getCurso());
            gs.setCodigo(codigo);

            codigos.add(codigo);

            gs.setVersion(BigDecimal.ONE.toString());
            gs.setEstadoGrupoEnum(EstadoGrupoSeccionEnum.ABI);
            gs.setEstadoPlanEnum(EstadoPlanCalificaEnum.PEND);

            gs.setHorasPractica(ggss.getHorasPractica());
            gs.setHorasTeoria(ggss.getHorasTeoria());
            gs.setAnexoBoletin(ggss.getAnexoBoletin());
            gs.setEstado(ggss.getEstado());

            grupoSeccionDAO.save(gs);

            List<Seccion> seccionesOrigen = ggss.getSecciones();

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
//                seccClone.setAula(seccOrigen.getAula());
//                seccClone.setGrupoHoras(seccOrigen.getGrupoHoras());
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

                seccionDAO.save(seccClone);

                List<DocenteSeccion> docenteSeccion = seccOrigen.getDocenteSeccion();
                if (docenteSeccion == null) {
                    docenteSeccion = new ArrayList();
                }

                for (DocenteSeccion dsOrigen : docenteSeccion) {

                    DocenteSeccion dsClone = new DocenteSeccion();
                    dsClone.setDocente(dsOrigen.getDocente());
                    dsClone.setEstado(dsOrigen.getEstado());
                    dsClone.setFechaInicio(null);
                    dsClone.setFechaFin(null);
                    dsClone.setPrincipal(dsOrigen.getPrincipal());
                    dsClone.setSeccion(seccClone);
                    dsClone.setPorcentajeCarga(dsOrigen.getPorcentajeCarga());

                    docenteSeccionDAO.save(dsClone);

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

    private void validarClonacion(CicloAcademico cicloDestino) {
        EventoAcademicoEnum even = cicloDestino.getTipo().equals(TipoCicloEnum.NIV.name()) ? CLASES_VER : CLASES_PRE;

        EventoCicloAcademico cicloAcademico = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloDestino, even);

        Assert.isNotNull(cicloAcademico, "No se asignó un evento para el ciclo " + cicloDestino.getDescripcion());

        if (even.equals(CLASES_PRE)) {
            cicloAcademico = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloDestino, CLASES_EPG);

        }
        Assert.isNotNull(cicloAcademico, "No se asignó un evento para el ciclo " + cicloDestino.getDescripcion());

        List<DiaHoraGrupo> diaHoraGrupos = diaHoraGrupoDAO.allByCicloAndTipoCiclo(cicloDestino);

        Assert.isNotNull(diaHoraGrupos, "No existe horarios para el ciclo " + cicloDestino.getDescripcion());
    }

    @Override
    public Long contarGpoSecc(CicloAcademico ciclo) {
        return grupoSeccionDAO.contarByCiclo(ciclo);
    }

    @Override
    public GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo) {
        return gpoSeccionService.resumenByCiclo(ciclo);
    }

    @Override
    @Transactional
    public void limpiarCodigo2(CicloAcademico ciclo, DataSessionPivot ds) {

        List<Seccion> secciones = seccionDAO.allByCiclo(ciclo);
        logger.debug("Seccion size  {}", secciones.size());
        for (Seccion seccione : secciones) {
            seccione.setCodigo2(null);
            seccionDAO.update(seccione);
        }
    }

    @Override
    @Transactional
    public void reordenar(CicloAcademico ciclo, DataSessionPivot ds) {

        List<GrupoSeccion> gsOrigenes = grupoSeccionDAO.allOrdenadoByCiclo(ciclo);
        logger.debug("GrupoSeccion size  {}", gsOrigenes.size());

        List<Seccion> secciones = seccionDAO.allSeccionOrderByciclo(ciclo);
        Map<Long, List<Seccion>> seccionesMap = TypesUtil.convertListToMapList("grupoSeccion.id", secciones);

        for (GrupoSeccion gsOrigene : gsOrigenes) {
            gsOrigene.setSecciones(seccionesMap.get(gsOrigene.getId()));
        }

        List<String> codigos = new ArrayList();

        for (GrupoSeccion ggss : gsOrigenes) {

            String codigo = StringUtils.leftPad(CodeGenerator.getNextCode(codigos, 0), 3, '0');

            List<Seccion> seccionesOrigen = ggss.getSecciones();

            int loopPCUR = 1;
            Seccion seccionSup = null;

            codigos.add(codigo);

            for (Seccion seccion : seccionesOrigen) {

                seccion.setSeccionSuperior(seccionSup);

                if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.TEO || seccion.getTipoSeccionEnum() == TipoSeccionEnum.TCUR) {
                    seccion.setCodigo2(codigo + "0");
                    seccionSup = seccion;
                } else if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PRA) {
                    seccion.setCodigo2(codigo + "1");
                } else if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                    seccion.setCodigo2(codigo + loopPCUR);
                    loopPCUR++;
                }

                seccionDAO.update(seccion);

            }

        }

    }

    @Override
    @Transactional
    public void limpiarCiclo(CicloAcademico ciclo) {
        restriccionRepitenciaDAO.deleteAllByCiclo(ciclo);
        restriccionModalidadDAO.deleteAllByCiclo(ciclo);
        restriccionFacultadDAO.deleteAllByCiclo(ciclo);
        restriccionCarreraDAO.deleteAllByCiclo(ciclo);
        docenteSeccionDAO.deleteAllByCiclo(ciclo);
        seccionDAO.deleteAllNotSuperiorByCiclo(ciclo);
        seccionDAO.deleteAllByCiclo(ciclo);
        grupoSeccionDAO.deleteAllByCiclo(ciclo);
        precioCursoEstructuraDAO.deleteAllByCiclo(ciclo);
        cursoCicloAcademicoDAO.deleteAllByCiclo(ciclo);
    }

}
