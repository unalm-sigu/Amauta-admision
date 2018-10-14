package pe.edu.lamolina.pivot.controller.academico.gposeccion.clonarciclo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import pe.edu.lamolina.model.academico.AmpliacionVacantes;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.RestriccionCarrera;
import pe.edu.lamolina.model.academico.RestriccionFacultad;
import pe.edu.lamolina.model.academico.RestriccionModalidad;
import pe.edu.lamolina.model.academico.RestriccionRepitencia;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AmpliacionVacanteEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.SituacionDocenteEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.academico.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.controller.academico.gposeccion.GpoSeccionService;
import pe.edu.lamolina.pivot.controller.general.oficina.OficinaService;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionCarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionFacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionModalidadDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionRepitenciaDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.dao.academico.AmpliacionVacantesDAO;

@Service
@Transactional(readOnly = true)
public class ClonGpoSeccionServiceImp implements ClonGpoSeccionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;

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
    AmpliacionVacantesDAO ampliacionVacanteDAO;

    @Autowired
    OficinaService oficinaService;

    @Autowired
    ColaboradorDAO colaboradorDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Override
    @Transactional
    public void clonarCiclo(CicloAcademico cicloOrigenForm, CicloAcademico cicloDestinoForm, DataSessionPivot ds) {

        CicloAcademico cicloOrigen = cicloAcademicoDAO.find(cicloOrigenForm.getId());
        CicloAcademico cicloDestino = cicloAcademicoDAO.find(cicloDestinoForm.getId());
        logger.debug("copiar del ciclo {} al ciclo {}", cicloOrigen.getId(), cicloDestino.getId());
        if (cicloOrigen.getId().longValue() == cicloDestino.getId()) {
            throw new PhobosException("El ciclo académico es el mismo al que desea copiar");
        }

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
    public List<AmpliacionVacantes> allAmpliacionVacante(Seccion seccion) {

        return ampliacionVacanteDAO.allBySeccion(seccion);
    }

    @Override
    @Transactional
    public void saveAmpliacionVacante(AmpliacionVacantes ampliacionVacante, DataSessionPivot ds) {

        Seccion seccion = seccionDAO.find(ampliacionVacante.getSeccion());
        List<AmpliacionVacantes> ampliaciones = ampliacionVacanteDAO.allPendientesBySeccion(seccion);
        Assert.isTrue(ampliaciones.isEmpty(), "Aún existe solicitudes de ampliación pendientes de atención");

        Seccion seccionSuperior = seccion.getSeccionSuperior();
        if (seccionSuperior != null) {
            List<Seccion> secciones = seccionDAO.allByGposSeccion(seccion.getGrupoSeccion());
            for (Seccion seccBD : secciones) {
                if (seccion.getId() == seccBD.getId().longValue()) {
                    continue;
                }
                List<AmpliacionVacantes> ampliacionesOtras = ampliacionVacanteDAO.allPendientesBySeccion(seccBD);
                Assert.isTrue(ampliacionesOtras.isEmpty(), "Aún existe solicitudes de ampliación pendientes para la sección " + seccBD.getCodigo2());
            }
        }

        Persona persona = ds.getPersona();
        Oficina oficinaMain = ampliacionVacante.getOficina();
        Oficina oficinaReal = oficinaService.findOficinaHija(persona, oficinaMain);
        Assert.isNotNull(oficinaReal, "Usted no se encuentra activo en la oficina " + oficinaMain.getNombre());

        Colaborador colaborador = colaboradorDAO.findActivoByPersonaOficina(oficinaReal, persona);
        Assert.isNotNull(colaborador, "Usted no se encuentra activo en la oficina " + oficinaReal.getNombre());

        Assert.isTrue(seccion.getVacantesOcupadas() <= ampliacionVacante.getVacantesFin().intValue(), "No puede disminuir las vacantes menor a la cantidad de matriculados + reservados");
        Aula aula = seccion.getAula();
        if (aula != null && aula.getCapacidadAula() != null) {
            Assert.isTrue(aula.getCapacidadAula().intValue() >= ampliacionVacante.getVacantesFin().intValue(), "No puede exceder la capacidad del aula");
        }

        if (seccionSuperior != null) {
            aula = seccionSuperior.getAula();
            if (aula != null && aula.getCapacidadAula() != null) {
                int total = seccionSuperior.getVacantesOcupadas() + ampliacionVacante.getIncremento();
                Assert.isTrue(aula.getCapacidadAula().intValue() >= total, "No puede exceder la capacidad del aula de la sección teórica");
            }
        }

        ampliacionVacante.setColaborador(colaborador);
        ampliacionVacante.setFechaRegistro(new Date());
        ampliacionVacante.setUserRegistro(ds.getUsuario());
        ampliacionVacante.setFechaSolicitud(new Date());
        ampliacionVacante.setUserRegistro(ds.getUsuario());
        ampliacionVacante.setEstadoEnum(AmpliacionVacanteEstadoEnum.PENDIENTE);
        ampliacionVacanteDAO.save(ampliacionVacante);
    }

//    @Override
//    @Transactional
//    public void updateAmpliacionVacante(AmpliacionVacantes ampliacionVacanteForm, DataSessionPivot ds) {
//        AmpliacionVacantes ampliacionVacante = ampliacionVacanteDAO.find(ampliacionVacanteForm);
//
//        Persona persona = ds.getPersona();
//        Oficina oficinaMain = ampliacionVacante.getOficina();
//        Oficina oficinaReal = oficinaService.findOficinaHija(persona, oficinaMain);
//        Assert.isNotNull(oficinaReal, "Usted no se encuentra activo en la oficina " + oficinaMain.getNombre());
//
//        Colaborador colaborador = colaboradorDAO.findActivoByPersonaOficina(oficinaReal, persona);
//        Assert.isNotNull(colaborador, "Usted no se encuentra activo en la oficina " + oficinaReal.getNombre());
//
//        ampliacionVacante.setColaborador(colaborador);
//        ampliacionVacante.setMotivo(ampliacionVacanteForm.getMotivo());
//        ampliacionVacante.setIncremento(ampliacionVacanteForm.getIncremento());
//        ampliacionVacante.setVacantesFin(ampliacionVacanteForm.getVacantesFin());
//
//        ampliacionVacante.setUserModificacion(ds.getUsuario());
//        ampliacionVacante.setFechaModificacion(new Date());
//
//        ampliacionVacanteDAO.update(ampliacionVacante);
//    }
//    
    @Override
    @Transactional
    public void deleteAmpliacionVacante(AmpliacionVacantes ampliacionForm, DataSessionPivot ds) {
        AmpliacionVacantes ampliacionBD = ampliacionVacanteDAO.find(ampliacionForm);
        Assert.isTrue(ampliacionBD.getEstadoEnum() == AmpliacionVacanteEstadoEnum.PENDIENTE, "No puede eliminarse ni anularse esta solicitud");

        Persona personaAnulador = ds.getPersona();
        Persona personaSolicitud = ampliacionBD.getColaborador().getPersona();
        if (personaAnulador.getId() != personaSolicitud.getId().longValue()) {
            Oficina oficinaMain = ampliacionBD.getOficina();
            boolean esMismaOficina = false;
            List<Oficina> oficinasPersona = oficinaService.allOficinasMain(personaAnulador);
            for (Oficina oficina : oficinasPersona) {
                if (oficina.getId() == oficinaMain.getId().longValue()) {
                    esMismaOficina = true;
                    break;
                }
            }
            Assert.isTrue(esMismaOficina, "Solo una persona de la misma oficina del solicitante puede anular esta solicitud");
        }

        ampliacionBD.setEstadoEnum(AmpliacionVacanteEstadoEnum.ANULADA);
        ampliacionBD.setUserModificacion(ds.getUsuario());
        ampliacionBD.setFechaModificacion(new Date());
        ampliacionVacanteDAO.update(ampliacionBD);
    }

    @Override
    public AmpliacionVacantes findAmpliacionVacante(AmpliacionVacantes ampliacionVacanteForm) {
        return ampliacionVacanteDAO.find(ampliacionVacanteForm);
    }

    @Override
    public List<Oficina> allOficinaByPersona(Persona persona) {
        return oficinaService.allOficinasMain(persona);
    }

    @Override
    @Transactional
    public void aceptarAmpliacionVacante(AmpliacionVacantes ampliacionForm, DataSessionPivot ds) {

        AmpliacionVacantes ampliacionBD = ampliacionVacanteDAO.find(ampliacionForm);

        Seccion seccion = seccionDAO.find(ampliacionBD.getSeccion());

        if (seccion.getMatriculados() > ampliacionBD.getVacantesFin()) {
            throw new PhobosException("Todas las vacantes ya fueron acupadas");
        }

        Aula aula = seccion.getAula();

        if (aula.getCapacidadAula() != null) {
            if (ampliacionBD.getVacantesFin() > aula.getCapacidadAula()) {
                throw new PhobosException("Ya ha completo la capacidad del aula");
            }
        }

        ampliacionBD.setEstadoEnum(AmpliacionVacanteEstadoEnum.ACEPTADO);
        ampliacionBD.setUserModificacion(ds.getUsuario());
        ampliacionBD.setFechaModificacion(new Date());
        ampliacionBD.setFechaRespuesta(new Date());
        ampliacionVacanteDAO.update(ampliacionBD);

        seccion.setVacantes(ampliacionBD.getVacantesFin());
        seccionDAO.update(seccion);

        Seccion secSuperior = seccion.getSeccionSuperior();
        if (secSuperior != null) {
            secSuperior.setVacantes(secSuperior.getVacantes() + ampliacionBD.getIncremento());
            seccionDAO.update(secSuperior);
        }

    }

    @Override
    @Transactional
    public void rechazarAmpliacionVacante(AmpliacionVacantes ampliacionVacanteForm, DataSessionPivot ds) {

        AmpliacionVacantes ampliacionVacante = ampliacionVacanteDAO.find(ampliacionVacanteForm);
        ampliacionVacante.setEstadoEnum(AmpliacionVacanteEstadoEnum.RECHAZADO);
        ampliacionVacante.setUserModificacion(ds.getUsuario());
        ampliacionVacante.setFechaModificacion(new Date());
        ampliacionVacante.setFechaRespuesta(new Date());
        ampliacionVacante.setComentarioRespuesta(ampliacionVacanteForm.getComentarioRespuesta());
        ampliacionVacanteDAO.update(ampliacionVacante);
    }
}
