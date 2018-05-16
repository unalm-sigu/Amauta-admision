package pe.edu.lamolina.pivot.controller.academico.gposeccion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.horario.TipoGrupoHorasDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import pe.albatross.zelpers.miscelanea.CodeGenerator;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.RestriccionCarrera;
import pe.edu.lamolina.model.academico.RestriccionFacultad;
import pe.edu.lamolina.model.academico.RestriccionModalidad;
import pe.edu.lamolina.model.academico.RestriccionRepitencia;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoRepitencia;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.model.enums.EstadoVacanteAlumnoEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.GrupoAnexoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.SituacionDocenteEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionCarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionFacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionModalidadDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionRepitenciaDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoRepitenciaDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.vacante.VacanteAlumnoDAO;
import pe.edu.lamolina.pivot.zelper.enums.TipoRestriccionEnum;

@Service
@Transactional(readOnly = true)
public class GpoSeccionServiceImp implements GpoSeccionService {

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
    CursoDAO cursoDAO;

    @Autowired
    DocenteDAO docenteDAO;

    @Autowired
    TipoGrupoHorasDAO tipoGrupoHorasDAO;

    @Autowired
    DiaHoraGrupoDAO diaHoraGrupoDAO;

    @Autowired
    GrupoHorasDAO grupoHorasDAO;

    @Autowired
    DiaDAO diaDAO;

    @Autowired
    HoraDAO horaDAO;

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    FacultadDAO facultadDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    RestriccionModalidadDAO restriccionModalidadDAO;

    @Autowired
    RestriccionCarreraDAO restriccionCarreraDAO;

    @Autowired
    RestriccionFacultadDAO restriccionFacultadDAO;

    @Autowired
    RestriccionRepitenciaDAO restriccionRepitenciaDAO;

    @Autowired
    TipoRepitenciaDAO tipoRepitenciaDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    VacanteAlumnoDAO vacanteAlumnoDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Override
    public Oficina findOficinaOera() {
        return oficinaDAO.findByCode("OERA");
    }

    @Override
    public List<Oficina> allOficinas(Compania compania) {
        return oficinaDAO.allByCompania(compania);
    }

    @Override
    public List<GrupoSeccion> allByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        List<GrupoSeccion> gsecciones = grupoSeccionDAO.allByDynatable(filter, ciclo);
        List<Seccion> secciones = seccionDAO.allByGposSeccion(gsecciones);

        Map<Long, List<Seccion>> mapSecciones = TypesUtil.convertListToMapList("grupoSeccion.id", secciones);
        for (GrupoSeccion gseccion : gsecciones) {
            List<Seccion> seccionesGpo = mapSecciones.get(gseccion.getId());
            gseccion.setSecciones(seccionesGpo == null ? new ArrayList() : seccionesGpo);
        }

        List<DocenteSeccion> docenteSeccion = docenteSeccionDAO.allBySecciones(secciones);
        Map<Long, List<DocenteSeccion>> mapDocSeccion = TypesUtil.convertListToMapList("seccion.id", docenteSeccion);

        for (Seccion seccion : secciones) {
            List<DocenteSeccion> doceentesSecc = mapDocSeccion.get(seccion.getId());
            seccion.setDocenteSeccion(doceentesSecc == null ? new ArrayList() : doceentesSecc);
        }

        return gsecciones;
    }

    @Override
    @Transactional(readOnly = false)
    public void cambiarEstadoGpoSeccion(SeccionEstadoEnum estadoEnum, GrupoSeccion grupoSeccion, Usuario usuario) {
        DateTime today = new DateTime();
        grupoSeccion = grupoSeccionDAO.find(grupoSeccion.getId());
        if (estadoEnum.equals(EstadoEnum.ACT)) {
            grupoSeccion.setEstadoEnum(SeccionEstadoEnum.ACT);
            grupoSeccion.setUsuarioModificacion(usuario);
            grupoSeccion.setFechaModificacion(today.toDate());
            grupoSeccionDAO.updateEstadoFechaModUsuarioMod(grupoSeccion);
        } else if (estadoEnum.equals(EstadoEnum.INA)) {
            for (Seccion seccion : grupoSeccion.getSecciones()) {
                List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allBySeccion(seccion);
                if (!matriculasSeccion.isEmpty()) {
                    throw new PhobosException(String.format("No se puede desactivar, La sección %s, ya que cuenta con matriculas", seccion.getCodigo2()));
                }
                seccion.setEstadoEnum(SeccionEstadoEnum.ANU);
                seccion.setFechaModificacion(today.toDate());
                seccionDAO.updateEstadoFechaModUsuarioMod(seccion);
            }
            grupoSeccion.setEstadoEnum(SeccionEstadoEnum.ANU);
            grupoSeccion.setUsuarioModificacion(usuario);
            grupoSeccion.setFechaModificacion(today.toDate());
            grupoSeccionDAO.updateEstadoFechaModUsuarioMod(grupoSeccion);
        }
    }

    @Override
    public List<GrupoHoras> allGrupoHorasZetasDyna(pe.albatross.octavia.dynatable.DynatableFilter filter,
            TipoGrupoHoras tipoGrupoHoras,
            CicloAcademico cicloAcademico) {
        return grupoHorasDAO.allZetasByDynatable(filter, tipoGrupoHoras, cicloAcademico);
    }

    @Override
    public List<GrupoHoras> allGrupoHoraByTipoGrupoHoraDyna(pe.albatross.octavia.dynatable.DynatableFilter filter,
            TipoGrupoHoras tipoGrupoHoras,
            CicloAcademico cicloAcademico,
            Seccion seccion) {
        List<GrupoHoras> gruposHorasFilter = this.allGrupoHorasBySeccionAndTipoGrupoHoras(seccion, tipoGrupoHoras, cicloAcademico);
        return grupoHorasDAO.allByTipoGrupoHoraDyna(filter, tipoGrupoHoras, cicloAcademico, seccion, gruposHorasFilter);
    }

    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupo(List<GrupoHoras> grupos) {
        return diaHoraGrupoDAO.allDiaHoraGrupo(grupos);
    }

    @Override
    @Transactional(readOnly = false)
    public GrupoSeccion saveGpoSeccionHeader(GrupoSeccion grupoSeccion, CicloAcademico cicloAcademico) {
        List<String> codigosByCiclo = grupoSeccionDAO.allCodigoByCiclo(cicloAcademico);
        //logger.debug(String.join(",", codigosByCiclo));
        Curso curso = cursoDAO.find(grupoSeccion.getCurso().getId());
        String codigo = CodeGenerator.getNextCode(codigosByCiclo, 0);

        grupoSeccion.setCodigo(codigo);
        grupoSeccion.setVersion(BigDecimal.ONE.toString());
        grupoSeccion.setEstadoGrupoEnum(EstadoGrupoSeccionEnum.ABI);
        grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.PEND);
        grupoSeccion.setCicloAcademico(cicloAcademico);

        Integer horasTeoria = curso.getHorasTeoria() == null ? 0 : curso.getHorasTeoria();
        Integer horasPractica = curso.getHorasPractica() == null ? 0 : curso.getHorasPractica();

        grupoSeccion.setHorasPractica(horasPractica);
        grupoSeccion.setHorasTeoria(horasTeoria);

        //  grupoSeccion.se
        Docente docenteDefault = docenteDAO.findByCode(Constantine.DOCENTE_INDETERMINADO);

        DateTime today = new DateTime();
        final BigDecimal PORCENTAJE_CARGA = new BigDecimal(100);

        grupoSeccion.setSecciones(new ArrayList<Seccion>());
        if (curso.isTipoCursoTEO()) {
            Seccion seccionTEO = new Seccion();
            seccionTEO.setGrupoSeccion(grupoSeccion);
            seccionTEO.setCodigo(grupoSeccion.getCodigo() + "0");
            seccionTEO.setCodigo2(seccionTEO.getCodigo());
            seccionTEO.setEstadoEnum(SeccionEstadoEnum.CRE);
            seccionTEO.setTipoSeccionEnum(TipoSeccionEnum.TEO);
            seccionTEO.setSituacionDocenteEnum(SituacionDocenteEnum.ERR);
            /*
            seccionTEO.setHorasPractica(curso.getHorasPractica());
            seccionTEO.setHorasTeoria(curso.getHorasTeoria());
             */
            seccionTEO.setHorasSemanales(horasTeoria);

            seccionTEO.setDocenteSeccion(new ArrayList<>());
            DocenteSeccion docenteSeccion = new DocenteSeccion();
            docenteSeccion.setDocente(docenteDefault);
            docenteSeccion.setCodigoSeccion(seccionTEO.getCodigo());
            docenteSeccion.setEstado(EstadoEnum.ACT.name());
            docenteSeccion.setFechaInicio(cicloAcademico.getFechaRegistro());
            docenteSeccion.setPrincipal(BigDecimal.ONE.intValue());
            docenteSeccion.setSeccion(seccionTEO);
            docenteSeccion.setPorcentajeCarga(PORCENTAJE_CARGA);
            seccionTEO.getDocenteSeccion().add(docenteSeccion);

            grupoSeccion.getSecciones().add(seccionTEO);
        }
        if (curso.isTipoCursoPRA()) {
            Seccion seccionPRA = new Seccion();
            seccionPRA.setGrupoSeccion(grupoSeccion);
            seccionPRA.setCodigo(codigo + "1");
            seccionPRA.setCodigo2(seccionPRA.getCodigo());
            seccionPRA.setEstadoEnum(SeccionEstadoEnum.CRE);
            seccionPRA.setTipoSeccionEnum(TipoSeccionEnum.PRA);
            seccionPRA.setSituacionDocenteEnum(SituacionDocenteEnum.ERR);
            /*
            seccionPRA.setHorasPractica(curso.getHorasPractica());
            seccionPRA.setHorasTeoria(curso.getHorasTeoria());
             */
            seccionPRA.setHorasSemanales(horasPractica);

            seccionPRA.setDocenteSeccion(new ArrayList<>());
            DocenteSeccion docenteSeccion = new DocenteSeccion();
            docenteSeccion.setDocente(docenteDefault);
            docenteSeccion.setCodigoSeccion(seccionPRA.getCodigo());
            docenteSeccion.setEstado(EstadoEnum.ACT.name());
            docenteSeccion.setFechaInicio(cicloAcademico.getFechaRegistro());
            docenteSeccion.setPrincipal(BigDecimal.ONE.intValue());
            docenteSeccion.setSeccion(seccionPRA);
            docenteSeccion.setPorcentajeCarga(PORCENTAJE_CARGA);
            seccionPRA.getDocenteSeccion().add(docenteSeccion);

            grupoSeccion.getSecciones().add(seccionPRA);
        }
        if (curso.isTipoCursoTEOPRA()) {
            Seccion seccionTCUR = new Seccion();
            seccionTCUR.setGrupoSeccion(grupoSeccion);
            seccionTCUR.setCodigo(codigo + "0");
            seccionTCUR.setCodigo2(seccionTCUR.getCodigo());
            seccionTCUR.setEstadoEnum(SeccionEstadoEnum.CRE);
            seccionTCUR.setTipoSeccionEnum(TipoSeccionEnum.TCUR);
            seccionTCUR.setSituacionDocenteEnum(SituacionDocenteEnum.ERR);
            //   seccionTCUR.setHorasPractica(curso.getHorasPractica());
            //   seccionTCUR.setHorasTeoria(curso.getHorasTeoria());
            seccionTCUR.setHorasSemanales(horasTeoria);

            seccionTCUR.setDocenteSeccion(new ArrayList<>());
            DocenteSeccion docenteSeccion = new DocenteSeccion();
            docenteSeccion.setDocente(docenteDefault);
            docenteSeccion.setCodigoSeccion(seccionTCUR.getCodigo());
            docenteSeccion.setEstado(EstadoEnum.ACT.name());
            docenteSeccion.setFechaInicio(cicloAcademico.getFechaRegistro());
            docenteSeccion.setPrincipal(BigDecimal.ONE.intValue());
            docenteSeccion.setSeccion(seccionTCUR);
            docenteSeccion.setPorcentajeCarga(PORCENTAJE_CARGA);
            seccionTCUR.getDocenteSeccion().add(docenteSeccion);

            grupoSeccion.getSecciones().add(seccionTCUR);

            Seccion seccionPCUR = new Seccion();
            seccionPCUR.setGrupoSeccion(grupoSeccion);
            seccionPCUR.setCodigo(codigo + "1");
            seccionPCUR.setCodigo2(seccionPCUR.getCodigo());
            seccionPCUR.setEstadoEnum(SeccionEstadoEnum.CRE);
            seccionPCUR.setTipoSeccionEnum(TipoSeccionEnum.PCUR);
            seccionPCUR.setSituacionDocenteEnum(SituacionDocenteEnum.ERR);
            //  seccionPCUR.setHorasPractica(curso.getHorasPractica());
            //   seccionPCUR.setHorasTeoria(curso.getHorasTeoria());
            seccionPCUR.setHorasSemanales(horasPractica);

            seccionPCUR.setDocenteSeccion(new ArrayList<>());
            DocenteSeccion docenteSeccion2 = new DocenteSeccion();
            docenteSeccion2.setDocente(docenteDefault);
            docenteSeccion2.setCodigoSeccion(seccionPCUR.getCodigo());
            docenteSeccion2.setEstado(EstadoEnum.ACT.name());
            docenteSeccion2.setFechaInicio(cicloAcademico.getFechaRegistro());
            docenteSeccion2.setPrincipal(BigDecimal.ONE.intValue());
            docenteSeccion2.setSeccion(seccionPCUR);
            docenteSeccion2.setPorcentajeCarga(PORCENTAJE_CARGA);
            seccionPCUR.getDocenteSeccion().add(docenteSeccion2);

            grupoSeccion.getSecciones().add(seccionPCUR);
        }
        grupoSeccionDAO.save(grupoSeccion);
        return grupoSeccion;
    }

    @Override
    @Transactional(readOnly = false)
    public void addSeccion(GrupoSeccion grupoSeccion) {
        grupoSeccion = grupoSeccionDAO.find(grupoSeccion.getId());
        Curso curso = grupoSeccion.getCurso();
        Docente docenteDefault = docenteDAO.findByCode(Constantine.DOCENTE_INDETERMINADO);
        List<Seccion> secciones = seccionDAO.allByGposSeccion(grupoSeccion);
        DateTime today = new DateTime();

        Seccion seccionPCUR = new Seccion();
        seccionPCUR.setGrupoSeccion(grupoSeccion);
        seccionPCUR.setCodigo(getNextCode1(secciones));
        seccionPCUR.setCodigo2(getNextCode2(secciones));
        seccionPCUR.setEstadoEnum(SeccionEstadoEnum.CRE);
        seccionPCUR.setTipoSeccionEnum(TipoSeccionEnum.PCUR);
        seccionPCUR.setSituacionDocenteEnum(SituacionDocenteEnum.ERR);
        /*
        seccionPCUR.setHorasPractica(curso.getHorasPractica());
        seccionPCUR.setHorasTeoria(curso.getHorasTeoria());*/
        seccionPCUR.setHorasSemanales(curso.getHorasPractica());

        seccionPCUR.setDocenteSeccion(new ArrayList<>());
        DocenteSeccion docenteSeccion2 = new DocenteSeccion();
        docenteSeccion2.setDocente(docenteDefault);
        docenteSeccion2.setCodigoSeccion(seccionPCUR.getCodigo());
        docenteSeccion2.setEstado(EstadoEnum.ACT.name());
        docenteSeccion2.setFechaInicio(today.toDate());
        docenteSeccion2.setPrincipal(BigDecimal.ONE.intValue());
        docenteSeccion2.setSeccion(seccionPCUR);
        docenteSeccion2.setPorcentajeCarga(BigDecimal.valueOf(100));
        seccionPCUR.getDocenteSeccion().add(docenteSeccion2);

        seccionDAO.save(seccionPCUR);

    }

    private String getNextCode1(List<Seccion> secciones) {
        String codeGpo = secciones.get(0).getCodigo().substring(0, 3);
        List<String> codes = new ArrayList();
        for (Seccion secc : secciones) {
            codes.add(secc.getCodigo());
        }
        return CodeGenerator.getNextCode4(codes, codeGpo);

    }

    private String getNextCode2(List<Seccion> secciones) {
        String codeGpo = secciones.get(0).getCodigo2().substring(0, 3);
        List<String> codes = new ArrayList();
        for (Seccion secc : secciones) {
            codes.add(secc.getCodigo2());
        }
        return CodeGenerator.getNextCode4(codes, codeGpo);

    }

    @Override
    @Transactional(readOnly = false)
    public void addDocenteSeccion(Seccion seccion, CicloAcademico cicloAcademico) {
        seccion = seccionDAO.find(seccion.getId());

        Docente docenteDefault = docenteDAO.findByCode(Constantine.DOCENTE_INDETERMINADO);
        DateTime today = new DateTime();
        List<Date> fechas = this.allDatesEventoCicloAcademicoForPeriodo(cicloAcademico);
        Date maxDate = null;
        if (!fechas.isEmpty()) {
            maxDate = fechas.get(fechas.size() - 1);
        }

        DocenteSeccion docenteSeccion = new DocenteSeccion();
        docenteSeccion.setDocente(docenteDefault);
        docenteSeccion.setCodigoSeccion(seccion.getCodigo());
        docenteSeccion.setEstado(EstadoEnum.ACT.name());
        //   docenteSeccion.setFechaInicio(today.toDate());
        docenteSeccion.setPrincipal(BigDecimal.ZERO.intValue());
        docenteSeccion.setSeccion(seccion);
        docenteSeccionDAO.save(docenteSeccion);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateDocenteSecFechaInicio(DocenteSeccion docenteSeccion) {
        docenteSeccionDAO.updateFechaInicio(docenteSeccion);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateDocenteSecFechaFin(DocenteSeccion docenteSeccion) {
        docenteSeccionDAO.updateFechaFin(docenteSeccion);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteSeccion(Seccion seccionToDelete) {
        seccionToDelete = seccionDAO.find(seccionToDelete.getId());
        List<Seccion> secciones = seccionDAO.allByGposSeccion(seccionToDelete.getGrupoSeccion());
        if (secciones.size() == 1) {
            throw new PhobosException("No se pueden eliminar todas las secciones del grupo");
        }

        Seccion seccionTCUR = null;
        Integer vacantes = BigDecimal.ZERO.intValue();
        for (Seccion seccionEach : secciones) {
            if (seccionEach.isTipoSeccionTCUR()) {
                seccionTCUR = seccionEach;
            }
            if (seccionEach.getId().compareTo(seccionToDelete.getId()) != 0) {
                if (seccionEach.isTipoSeccionPCUR()) {
                    vacantes = vacantes + seccionEach.getVacantes();
                }
            }

        }
        if (seccionTCUR != null) {
            seccionTCUR.setVacantes(vacantes);
            if (ObjectUtil.getParentTree(seccionTCUR, "aula.id") != null) {
                if (seccionTCUR.getAula().getAforo().compareTo(seccionTCUR.getVacantes()) < 0) {
                    throw new PhobosException("Las vacantes de la sección superan, el aforo del aula de teoria");
                }
            }
            seccionDAO.updateSeccionVacantes(seccionTCUR);
        }

        //docenteSeccionDAO.deleteDocenteSeccionBySeccion(seccion);
        List<DocenteSeccion> docentesSec = docenteSeccionDAO.allBySeccion(seccionToDelete);
        for (DocenteSeccion docenteSeccion : docentesSec) {
            docenteSeccionDAO.delete(docenteSeccion);
        }
        seccionDAO.delete(seccionToDelete);
    }

    @Override
    @Transactional(readOnly = false)
    public void activarSeccion(Seccion seccion, Usuario usuario) {
        seccion = seccionDAO.find(seccion.getId());
        DateTime today = new DateTime();
        seccion.setUsuarioModificacion(usuario);
        seccion.setFechaModificacion(today.toDate());
        seccion.setEstadoEnum(SeccionEstadoEnum.ACT);
        seccionDAO.updateEstadoFechaModUsuarioMod(seccion);
        this.actualizarVacantesTCUR(seccion.getGrupoSeccion(), usuario, today);
    }

    @Override
    @Transactional(readOnly = false)
    public void bloquearSeccion(Seccion seccion, Usuario usuario) {
        seccion = seccionDAO.find(seccion.getId());
        DateTime today = new DateTime();
        seccion.setUsuarioModificacion(usuario);
        seccion.setFechaModificacion(today.toDate());
        seccion.setEstadoEnum(SeccionEstadoEnum.BLO);
        seccionDAO.updateEstadoFechaModUsuarioMod(seccion);

        this.actualizarVacantesTCUR(seccion.getGrupoSeccion(), usuario, today);
    }

    @Override
    @Transactional(readOnly = false)
    public void anularSeccion(Seccion seccion, Usuario usuario) {
        DateTime today = new DateTime();
        seccion = seccionDAO.find(seccion.getId());
        GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();

        List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allBySeccion(seccion);
        if (matriculasSeccion.isEmpty()) {
            List<DocenteSeccion> docentesSec = docenteSeccionDAO.allBySeccion(seccion);
            for (DocenteSeccion docenteSeccion : docentesSec) {
                docenteSeccionDAO.delete(docenteSeccion);
            }
            List<VacanteAlumno> vacantesAlumnos = vacanteAlumnoDAO.allActivosBySeccion(seccion);
            for (VacanteAlumno vacanteAlumno : vacantesAlumnos) {
                vacanteAlumnoDAO.delete(vacanteAlumno);
            }
            seccionDAO.delete(seccion);

            List<Seccion> seccionesActivas = seccionDAO.allOperativesByGpoSeccion(grupoSeccion);
            Collections.sort(seccionesActivas, (Seccion va1, Seccion va2) -> va1.getCodigo().compareTo(va2.getCodigo()));
            int i = 0;
            for (Seccion seccionEach : seccionesActivas) {
                seccionEach.setCodigo(grupoSeccion.getCodigo() + i);
                seccionEach.setCodigo2(seccionEach.getCodigo());
                seccionEach.setUsuarioModificacion(usuario);
                seccionEach.setFechaModificacion(today.toDate());
                seccionDAO.updateEstadoFechaModUsuarioMod(seccionEach);
                i++;
            }

        } else {
            seccion.setUsuarioModificacion(usuario);
            seccion.setFechaModificacion(today.toDate());
            seccion.setEstadoEnum(SeccionEstadoEnum.ANU);
            seccionDAO.updateEstadoFechaModUsuarioMod(seccion);
        }
        this.actualizarVacantesTCUR(seccion.getGrupoSeccion(), usuario, today);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteDocSeccion(DocenteSeccion docenteSeccion) {
        docenteSeccion = docenteSeccionDAO.find(docenteSeccion.getId());
        docenteSeccionDAO.delete(docenteSeccion);
    }

    @Override
    public List<DocenteSeccion> allDocentesSeccionBySeccion(Seccion seccion) {
        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allBySeccion(seccion);
        //Collections.sort(docentesSeccion, (DocenteSeccion va1, DocenteSeccion va2) -> va1.getId().compareTo(va2.getId()));
        return docentesSeccion;
    }

    public static String generateCodigo(String codigo) {
        if (StringUtils.isBlank(codigo)) {
            return "001";
        }
        String letterPart = codigo.substring(0, 1);
        Integer numericPart = Integer.parseInt(codigo.substring(1, 3));

        if (numericPart == 99) {
            if (StringUtils.isNumeric(letterPart)) {
                Integer letterPartInt = Integer.parseInt(letterPart);
                if (letterPartInt < 9) {
                    letterPartInt++;
                    letterPart = letterPartInt + "";
                } else {
                    letterPart = "A";
                }
            } else {
                int charValue = letterPart.charAt(0);
                letterPart = String.valueOf((char) (charValue + 1));
            }
            numericPart = 0;
        }
        numericPart++;
        return letterPart + String.format("%02d", numericPart);
    }

    @Override
    public List<AnexoBoletin> allAnexosSuperiores() {
        return anexoBoletinDAO.allAnexosSuperiores();
    }

    @Override
    public List<Curso> allCursosForProgramacion(String nomString) {
        return cursoDAO.allForProgramacion(nomString);
    }

    @Override
    public List<AnexoBoletin> allAnexoBoletionHijos() {
        return anexoBoletinDAO.allAnexosHijos();
    }

    @Override
    public AnexoBoletin findAnexoBoletin(Long idAnexoBoletin) {
        return anexoBoletinDAO.find(idAnexoBoletin);
    }

    @Override
    public Curso findCurso(Long id) {
        return cursoDAO.find(id);
    }

    @Override
    public GrupoSeccion findGpoSeccion(Long id) {
        return grupoSeccionDAO.find(id);
    }

    @Override
    public List<Seccion> allSeccionesByGrupo(GrupoSeccion grupoSeccion) {
        List<Seccion> secciones = seccionDAO.allByGposSeccion(grupoSeccion);
        for (Seccion seccion : secciones) {
            seccion.getDocenteSeccion().size();
            //optimizar
            List<RestriccionCarrera> restriccionesCarrera = restriccionCarreraDAO.allActivasBySeccion(seccion);
            List<RestriccionFacultad> restriccionesFacultad = restriccionFacultadDAO.allActivasBySeccion(seccion);
            List<RestriccionModalidad> restriccionesModalidad = restriccionModalidadDAO.allActivasBySeccion(seccion);
            List<RestriccionRepitencia> restriccionRepitencia = restriccionRepitenciaDAO.allActivasBySeccion(seccion);

            seccion.setRestriccionesCarrera(restriccionesCarrera);
            seccion.setRestriccionesFacultad(restriccionesFacultad);
            seccion.setRestriccionesModalidad(restriccionesModalidad);
            seccion.setRestriccionesRepitencia(restriccionRepitencia);
        }
        return secciones;
    }

    @Override
    public List<Docente> allDocenterByNombre(String nombre) {
        return docenteDAO.allByNombreFilter(nombre, 10);
    }

    @Override
    public List<Aula> searchAulaByName(String nombre) {
        return aulaDAO.searchByNombreFilter(nombre, Integer.SIZE);
    }

    @Override
    @Transactional(readOnly = false)
    public void cambiarDocentePrincipal(DocenteSeccion docenteSeccion) {
        docenteSeccion = docenteSeccionDAO.find(docenteSeccion.getId());
        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allBySeccion(docenteSeccion.getSeccion());
        for (DocenteSeccion docenteSeccionEach : docentesSeccion) {
            docenteSeccionEach.setPrincipal(BigDecimal.ZERO.intValue());
            docenteSeccionDAO.updatePrincipal(docenteSeccionEach);
        }
        docenteSeccion.setPrincipal(BigDecimal.ONE.intValue());
        docenteSeccionDAO.updatePrincipal(docenteSeccion);
    }

    @Override
    @Transactional(readOnly = false)
    public void actualizarDocente(Long docenteSeccionId, Long docenteId) {
        DocenteSeccion docenteSeccion = new DocenteSeccion(docenteSeccionId);
        docenteSeccion.setDocente(new Docente(docenteId));
        docenteSeccionDAO.updateDocente(docenteSeccion);
    }

    @Override
    @Transactional(readOnly = false)
    public void actualizarSeccionResctriccionCapa(Seccion seccionForm, Usuario usuario) {
        seccionDAO.updateRestriccionCapa(seccionForm);
    }

    @Override
    @Transactional(readOnly = false)
    public void actualizarSeccionVacantes(Seccion seccionForm, Usuario usuario) {
        DateTime today = new DateTime();
        Seccion seccioDB = seccionDAO.find(seccionForm.getId());
        GrupoSeccion grupoSeccion = grupoSeccionDAO.findLock(seccioDB.getGrupoSeccion().getId());

        //validar seccion seleccionada
        if (ObjectUtil.getParentTree(seccioDB, "aula.id") != null) {
            if (seccioDB.getAula().getCapacidadAula().compareTo(seccionForm.getVacantes()) < 0) {
                throw new PhobosException(String.format("Las vacantes de la sección %s superan, el aforo su aula", seccionForm.getCodigo2()));
            }
        }
        List<MatriculaSeccion> matriculasSeccionSelect = matriculaSeccionDAO.allBySeccion(seccioDB);
        if (matriculasSeccionSelect.size() > seccionForm.getVacantes()) {
            throw new PhobosException(String.format("Error. Las matriculas para la sección %s superan la cantidad de vacantes asignadas.", seccionForm.getCodigo2()));
        }
        seccioDB.setVacantes(seccionForm.getVacantes());
        seccionDAO.updateSeccionVacantes(seccioDB);
        //seccionDAO.update(seccioDB);

        //validar seccion seleccioanda
        List<VacanteAlumno> vacantesAlumnoBySeccion = vacanteAlumnoDAO.allActivosBySeccion(seccionForm);
        Collections.sort(vacantesAlumnoBySeccion, (VacanteAlumno va1, VacanteAlumno va2) -> va1.getNumero().compareTo(va2.getNumero()));
        if (vacantesAlumnoBySeccion.isEmpty()) {
            for (int i = 1; i <= seccionForm.getVacantes(); i++) {
                VacanteAlumno vacanteAlumno = new VacanteAlumno();
                vacanteAlumno.setAlumno(null);
                vacanteAlumno.setEstadoEnum(EstadoVacanteAlumnoEnum.DISP);
                vacanteAlumno.setFechaRegistro(today.toDate());
                vacanteAlumno.setNumero(i);
                vacanteAlumno.setSeccion(seccioDB);
                vacanteAlumno.setUserRegistro(usuario);
                vacanteAlumnoDAO.save(vacanteAlumno);
            }
        } else {
            //si se asignaron mas vacantes de las que habia
            if (seccionForm.getVacantes() > vacantesAlumnoBySeccion.size()) {
                int diff = seccionForm.getVacantes() - vacantesAlumnoBySeccion.size();
                for (int i = 1; i <= diff; i++) {
                    VacanteAlumno vacanteAlumnoEach = new VacanteAlumno();
                    vacanteAlumnoEach.setAlumno(null);
                    vacanteAlumnoEach.setEstadoEnum(EstadoVacanteAlumnoEnum.DISP);
                    vacanteAlumnoEach.setFechaRegistro(today.toDate());
                    vacanteAlumnoEach.setNumero(i + vacantesAlumnoBySeccion.size());
                    vacanteAlumnoEach.setSeccion(seccionForm);
                    vacanteAlumnoEach.setUserRegistro(usuario);
                    vacanteAlumnoDAO.save(vacanteAlumnoEach);
                }
            } else {
                //si se asignaron menos vacantes de las que habia
                int diff = vacantesAlumnoBySeccion.size() - seccionForm.getVacantes();
                int cantInac = 0;
                if (diff != BigDecimal.ZERO.intValue()) {
                    for (int i = vacantesAlumnoBySeccion.size() - 1; i >= 0; i--) {
                        VacanteAlumno vacanteAlumnoEach = vacantesAlumnoBySeccion.get(i);
                        if (vacanteAlumnoEach.isEstadoDisponible()) {
                            vacanteAlumnoEach.setUserModificacion(usuario);
                            vacanteAlumnoEach.setFechaModificacion(today.toDate());
                            vacanteAlumnoEach.setEstadoEnum(EstadoVacanteAlumnoEnum.INA);
                            vacanteAlumnoDAO.updateEstadoFechaModUsuarioMod(vacanteAlumnoEach);
                            cantInac++;
                        }
                        if (cantInac == diff) {
                            break;
                        }
                    }
                }
            }
        }
        this.actualizarVacantesTCUR(grupoSeccion, usuario, today);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void actualizarVacantesTCUR(GrupoSeccion grupoSeccion, Usuario usuario, DateTime today) {
        if (grupoSeccion.getCurso().isTipoCursoTEOPRA()) {
            List<Seccion> secciones = seccionDAO.allOperativesByGpoSeccion(grupoSeccion);

            Seccion seccionTCUR = null;
            Integer vacantes = BigDecimal.ZERO.intValue();

            for (Seccion seccionEach : secciones) {
                if (seccionEach.isTipoSeccionTCUR()) {
                    seccionTCUR = seccionEach;
                }
                if (seccionEach.isTipoSeccionPCUR()) {
                    vacantes = vacantes + seccionEach.getVacantes();
                }
            }

            seccionTCUR.setVacantes(vacantes);
            if (ObjectUtil.getParentTree(seccionTCUR, "aula.id") != null) {
                if (seccionTCUR.getAula().getCapacidadAula().compareTo(seccionTCUR.getVacantes()) < 0) {
                    throw new PhobosException("Las vacantes de la sección teoria superan, el aforo del aula");
                }
            }
            List<MatriculaSeccion> matriculasSeccionTCUR = matriculaSeccionDAO.allBySeccion(seccionTCUR);
            if (matriculasSeccionTCUR.size() > seccionTCUR.getVacantes()) {
                throw new PhobosException("Error. Las matriculas para la sección teoria superan la cantidad de vacantes asignadas.");
            }
            seccionDAO.updateSeccionVacantes(seccionTCUR);

            List<VacanteAlumno> vacantesAlumnoBySeccion = vacanteAlumnoDAO.allActivosBySeccion(seccionTCUR);
            Collections.sort(vacantesAlumnoBySeccion, (VacanteAlumno va1, VacanteAlumno va2) -> va1.getNumero().compareTo(va2.getNumero()));

            if (vacantesAlumnoBySeccion.isEmpty()) {
                for (int i = 1; i <= seccionTCUR.getVacantes(); i++) {
                    VacanteAlumno vacanteAlumno = new VacanteAlumno();
                    vacanteAlumno.setAlumno(null);
                    vacanteAlumno.setEstadoEnum(EstadoVacanteAlumnoEnum.DISP);
                    vacanteAlumno.setFechaRegistro(today.toDate());
                    vacanteAlumno.setNumero(i);
                    vacanteAlumno.setSeccion(seccionTCUR);
                    vacanteAlumno.setUserRegistro(usuario);
                    vacanteAlumnoDAO.save(vacanteAlumno);
                }
            } else {
                //si se asignaron mas vacantes de las que habia
                if (seccionTCUR.getVacantes() > vacantesAlumnoBySeccion.size()) {
                    int diff = seccionTCUR.getVacantes() - vacantesAlumnoBySeccion.size();
                    for (int i = 1; i <= diff; i++) {
                        VacanteAlumno vacanteAlumnoEach = new VacanteAlumno();
                        vacanteAlumnoEach.setAlumno(null);
                        vacanteAlumnoEach.setEstadoEnum(EstadoVacanteAlumnoEnum.DISP);
                        vacanteAlumnoEach.setFechaRegistro(today.toDate());
                        vacanteAlumnoEach.setNumero(i + vacantesAlumnoBySeccion.size());
                        vacanteAlumnoEach.setSeccion(seccionTCUR);
                        vacanteAlumnoEach.setUserRegistro(usuario);
                        vacanteAlumnoDAO.save(vacanteAlumnoEach);
                    }
                } else {
                    //si se asignaron menos vacantes de las que habia
                    int diff = vacantesAlumnoBySeccion.size() - seccionTCUR.getVacantes();
                    int cantInac = 0;
                    if (diff != BigDecimal.ZERO.intValue()) {
                        for (int i = vacantesAlumnoBySeccion.size() - 1; i >= 0; i--) {
                            VacanteAlumno vacanteAlumnoEach = vacantesAlumnoBySeccion.get(i);
                            if (vacanteAlumnoEach.isEstadoDisponible()) {
                                vacanteAlumnoEach.setUserModificacion(usuario);
                                vacanteAlumnoEach.setFechaModificacion(today.toDate());
                                vacanteAlumnoEach.setEstadoEnum(EstadoVacanteAlumnoEnum.INA);
                                vacanteAlumnoDAO.updateEstadoFechaModUsuarioMod(vacanteAlumnoEach);
                                cantInac++;
                            }
                            if (cantInac == diff) {
                                break;
                            }
                        }
                    }
                }
            }

        }
    }

    @Override
    @Transactional(readOnly = false)
    public void updatePorcentajeAvance(DocenteSeccion docenteSeccion) {
        docenteSeccionDAO.updatePorcentajeAvance(docenteSeccion);
    }

    @Override
    @Transactional(readOnly = false)
    public void analizedDocenteSeccion(GrupoSeccion grupoSeccion, CicloAcademico cicloAcademico) {
        List<Seccion> secciones = seccionDAO.allOperativesByGpoSeccion(grupoSeccion);
        for (Seccion seccion : secciones) {
            this.analizedDocenteSeccion(seccion, cicloAcademico);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void analizedDocenteSeccion(Seccion seccion, CicloAcademico cicloAcademico) {
        Boolean errorPorcentajeCarga = Boolean.FALSE;
        Boolean errorPeriodoClases = Boolean.FALSE;
        DateTime fechaMinEvento = null;
        DateTime fechaMaxEvento = null;

        List<Date> fechas = this.allDatesEventoCicloAcademicoForPeriodo(cicloAcademico);
        if (!fechas.isEmpty()) {
            fechaMinEvento = new DateTime(new DateTime(fechas.get(0)).toLocalDate().toDate());
            fechaMaxEvento = new DateTime(new DateTime(fechas.get(fechas.size() - 1)).toLocalDate().toDate());
        }

        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allActivosBySeccion(seccion);
        Collections.sort(docentesSeccion, (DocenteSeccion va1, DocenteSeccion va2) -> va1.getId().compareTo(va2.getId()));

        BigDecimal porcentajeCarga = BigDecimal.ZERO;
        for (DocenteSeccion docenteSeccion : docentesSeccion) {
            if (docenteSeccion.getPorcentajeCarga() == null) {
                porcentajeCarga = null;
            } else {
                if (porcentajeCarga != null) {
                    porcentajeCarga = porcentajeCarga.add(docenteSeccion.getPorcentajeCarga());
                }
            }
        }
        if (porcentajeCarga == null || porcentajeCarga.compareTo(BigDecimal.valueOf(100)) != 0) {
            errorPorcentajeCarga = Boolean.TRUE;
        }

        List<Date> fechasPeriodos = new ArrayList<>();
        for (DocenteSeccion docenteSeccion : docentesSeccion) {
            if (docenteSeccion.getFechaInicio() != null) {
                fechasPeriodos.add(docenteSeccion.getFechaInicio());
            }
            if (docenteSeccion.getFechaFin() != null) {
                fechasPeriodos.add(docenteSeccion.getFechaFin());
            }
        }
        Collections.sort(fechasPeriodos, (Date va1, Date va2) -> va1.compareTo(va2));
        if (fechasPeriodos.size() % 2 != 0 || (fechasPeriodos.size() / 2) != docentesSeccion.size()) {
            errorPeriodoClases = Boolean.TRUE;
        } else {
            DateTime fechaMinPeriodo = null;
            DateTime fechaMaxPeriodo = null;
            if (!fechasPeriodos.isEmpty()) {
                fechaMinPeriodo = new DateTime(new DateTime(fechasPeriodos.get(0)).toLocalDate().toDate());
                fechaMaxPeriodo = new DateTime(new DateTime(fechasPeriodos.get(fechasPeriodos.size() - 1)).toLocalDate().toDate());
            }
            if (fechaMinEvento != null && fechaMinPeriodo != null) {
                if (!fechaMinEvento.equals(fechaMinPeriodo)) {
                    errorPeriodoClases = Boolean.TRUE;
                }
            }
            if (fechaMaxEvento != null && fechaMaxPeriodo != null) {
                if (!fechaMaxEvento.equals(fechaMaxPeriodo)) {
                    errorPeriodoClases = Boolean.TRUE;
                }
            }
            if (!errorPeriodoClases) {
                DateTime lastFechaIni = null;
                DateTime lastFechaFin = null;

                for (int i = 0; i < fechasPeriodos.size(); i++) {
                    DateTime fechaEach = new DateTime(new DateTime(fechasPeriodos.get(i)).toLocalDate().toDate());
                    if ((i + 1) % 2 == 0) {
                        //fechas fin
                        lastFechaFin = new DateTime(new DateTime(fechasPeriodos.get(i)).toLocalDate().toDate());
                    } else {
                        //fechas ini
                        if (lastFechaFin != null) {
                            lastFechaFin = lastFechaFin.plusDays(1);
                            if (!lastFechaFin.equals(fechaEach)) {
                                errorPeriodoClases = Boolean.TRUE;
                                break;
                            }
                        }
                        lastFechaIni = new DateTime(new DateTime(fechasPeriodos.get(i)).toLocalDate().toDate());
                    }
                }
            }
        }
        if (errorPeriodoClases || errorPorcentajeCarga) {
            seccion.setSituacionDocenteEnum(SituacionDocenteEnum.ERR);
        } else {
            seccion.setSituacionDocenteEnum(SituacionDocenteEnum.COR);
        }
        seccionDAO.updateSituacionDocente(seccion);
    }

    @Override
    public Seccion findSeccion(Long seccionId) {
        return seccionDAO.find(seccionId);
    }

    @Override
    public Seccion findSeccionWithRestriccions(Long seccionId) {
        Seccion seccion = seccionDAO.find(seccionId);
        List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccion(seccion);
        seccion.setHorarioSeccion(horariosSeccion);

        List<RestriccionCarrera> restriccionesCarrera = restriccionCarreraDAO.allActivasBySeccion(seccion);
        List<RestriccionFacultad> restriccionesFacultad = restriccionFacultadDAO.allActivasBySeccion(seccion);
        List<RestriccionModalidad> restriccionesModalidad = restriccionModalidadDAO.allActivasBySeccion(seccion);
        List<RestriccionRepitencia> restriccionRepitencias = restriccionRepitenciaDAO.allActivasBySeccion(seccion);

        seccion.setRestriccionesCarrera(restriccionesCarrera);
        seccion.setRestriccionesFacultad(restriccionesFacultad);
        seccion.setRestriccionesModalidad(restriccionesModalidad);
        seccion.setRestriccionesRepitencia(restriccionRepitencias);

        return seccion;
    }

    @Override
    public List<GrupoHoras> allByTipoGrupoHorasCiclo(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico) {
        return grupoHorasDAO.allByTipoGrupoHora(tipoGrupoHoras, cicloAcademico);
    }

    @Override
    public List<GrupoHoras> allGrupoHorasBySeccionAndTipoGrupoHoras(Seccion seccion, TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico) {
        seccion = seccionDAO.find(seccion.getId());
        tipoGrupoHoras = tipoGrupoHorasDAO.find(tipoGrupoHoras.getId());
        //Buscamos los grupo horas de acuerdo al tipo grupo horas y el ciclo academico
        List<GrupoHoras> grupoHoras = grupoHorasDAO.allByTipoGrupoHora(tipoGrupoHoras, cicloAcademico);
        List<DiaHoraGrupo> diasGrupoHoras = diaHoraGrupoDAO.allDiaHoraGrupo(grupoHoras, cicloAcademico);
        List<Dia> dias = diaDAO.all();

        List<GrupoHoras> grupoHorasFiltrado = new ArrayList<>();

        //asignamos los dia horas grupos al grupo horas que les corresponde
        for (GrupoHoras grupoHora : grupoHoras) {
            grupoHora.setDiaHoraGrupo(new ArrayList<>());
            List<DiaHoraGrupo> listaDiaHoraGrupo = diasGrupoHoras.stream().filter(item -> item.getGrupoHorario().getId().compareTo(grupoHora.getId()) == 0).collect(Collectors.toList());
            Collections.sort(listaDiaHoraGrupo, (p1, p2) -> p1.getHora().getNumero().compareTo(p2.getHora().getNumero()));
            grupoHora.setDiaHoraGrupo(listaDiaHoraGrupo);
        }
        //buscando grupos con las horas requeridas por dia (filtramos los grupos horas)
        for (GrupoHoras grupoHora : grupoHoras) {
            /*
            if (grupoHora.getDiaHoraGrupo().size() >= seccion.getHorasSemanales()) {
                grupoHorasFiltrado.add(grupoHora);
            }*/
            List<Integer> horasSemana = new ArrayList<>();
            for (Dia dia : dias) {

                List<DiaHoraGrupo> horasPorDias = new ArrayList<>();

                for (DiaHoraGrupo horasPorDia : grupoHora.getDiaHoraGrupo()) {
                    if (horasPorDia.getDia().getId().equals(dia.getId())) {
                        horasPorDias.add(horasPorDia);
                    }
                }
                if (horasPorDias.size() <= seccion.getHorasSemanales()) {
                    grupoHora.setHorasMismoDia(Boolean.TRUE);
                    horasSemana.add(horasPorDias.size());
                }
            }

            List<Integer> combinations = new ArrayList<Integer>();
            getAllSums(horasSemana, 0, 0, combinations, seccion.getHorasSemanales());

            if (!combinations.isEmpty()) {
                grupoHorasFiltrado.add(grupoHora);
            }
            /*  if (grupoHora.isHorasMismoDia() && grupoHora.getDiaHoraGrupo().size() >= seccion.getHorasSemanales()) {
                grupoHorasFiltrado.add(grupoHora);
            }*/
        }

        return grupoHorasFiltrado;
    }

    private void getAllSums(List<Integer> array, int startingValue, int pos, List<Integer> result, Integer comparation) {
        for (int i = pos; i < array.size(); i++) {
            int currentValue = startingValue + array.get(i);
            if (currentValue == comparation) {
                result.add(currentValue);
            }
            getAllSums(array, currentValue, i + 1, result, comparation);
        }
    }

    @Override
    public TipoGrupoHoras findTipoGrupoHoraByTipo(TipoGrupoHorasEnum tipoGrupoHorasEnum) {
        TipoGrupoHoras tipoGrupoHoraZeta = tipoGrupoHorasDAO.findByTipo(tipoGrupoHorasEnum);
        return tipoGrupoHoraZeta;
    }

    @Override
    public TipoGrupoHoras findTipoGrupoHoraByTipoAndCiclo(TipoGrupoHorasEnum tipoGrupoHorasEnum, CicloAcademico cicloAcademico) {
        TipoGrupoHoras tipoGrupoHoraZeta = tipoGrupoHorasDAO.findByTipoCiclo(tipoGrupoHorasEnum, cicloAcademico);
        return tipoGrupoHoraZeta;
    }

    @Override
    public List<TipoGrupoHoras> allGrupoHorasActivosTipoAndCiclo(CicloAcademico cicloAcademico, TipoGrupoHorasEnum tipoGrupoHorasEnum) {
        return tipoGrupoHorasDAO.allActiveByTipoCiclo(cicloAcademico, tipoGrupoHorasEnum);
    }

    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupoByGrupo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico) {
        return diaHoraGrupoDAO.allDiaHoraGrupoByGrupo(grupoHoras, cicloAcademico);
    }

    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupoByTipo(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico) {
        return diaHoraGrupoDAO.allDiaHoraGrupoByTipo(tipoGrupoHoras, cicloAcademico);
    }

    @Override
    public TipoGrupoHoras findTipoGrupoHoras(Long idTipoGrupoHoras) {
        return tipoGrupoHorasDAO.find(idTipoGrupoHoras);
    }

    @Override
    public List<Dia> allDia() {
        return diaDAO.allDia();
    }

    @Override
    public List<Hora> allHora() {
        return horaDAO.allHora();
    }

    @Override
    @Transactional(readOnly = false)
    public void saveSeccionGrupoHorario(Long seccionId, GrupoHoras grupoHorario, CicloAcademico cicloAcademico) {

        if (!grupoHorario.isPermiteCeroHoras()) {
            if (grupoHorario.getDiaHoraGrupo().isEmpty()) {
                throw new PhobosException("Debe seleccionar las horas");
            }
        }
        Seccion seccion = seccionDAO.find(seccionId);
        List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccion(seccion);
        seccion.setHorarioSeccion(horariosSeccion);

        GrupoHoras grupoHoraOld = seccion.getGrupoHoras();
        GrupoHoras grupoHorasNew = grupoHorasDAO.find(grupoHorario.getId());
        seccion.setGrupoHoras(grupoHorasNew);

        List<HorarioAula> horariosAulasSeccion = null;
        List<HorarioAula> horariosAulas = null;

        if (ObjectUtil.getParentTree(seccion, "aula.id") != null) {
            horariosAulas = horarioAulaDAO.allByAulaCiclo(seccion.getAula(), cicloAcademico);
        }

        HorarioSeccion horarioSeccion;
        HorarioAula horarioAula;

        //borrar los deseleccionados
        for (HorarioSeccion horarioSeccionEach : seccion.getHorarioSeccion()) {

            if (!horarioSeccionEach.isTieneDiaHoraGrupo(grupoHorario.getDiaHoraGrupo())) {
                if (ObjectUtil.getParentTree(seccion, "aula.id") != null) {
                    horarioAulaDAO.deleteBySeccionDiaHoraAula(seccion, horarioSeccionEach.getDia(),
                            horarioSeccionEach.getHora(), seccion.getAula());
                }
                horarioSeccionDAO.delete(horarioSeccionEach);
            }
            //  horarioSeccionDAO.delete(horarioSeccionEach);
        }
        /*
        if (ObjectUtil.getParentTree(seccion, "aula.id") != null) {
            horarioAulaDAO.deleteBySeccionAula(seccion, seccion.getAula());
        }
         */
        for (DiaHoraGrupo diaHoraGrupoEach : grupoHorario.getDiaHoraGrupo()) {
            //verificar si ya se encuentra registrado en base de datos
            if (diaHoraGrupoEach.isTieneHorarioSeccion(seccion.getHorarioSeccion())) {
                continue;
            }
            if (horariosAulas != null) {
                for (HorarioAula horarioAulaEach : horariosAulas) {
                    if (diaHoraGrupoEach.getDia().getId().equals(horarioAulaEach.getDia().getId())
                            && diaHoraGrupoEach.getHora().getId().equals(horarioAulaEach.getHora().getId())) {
                        if (horarioAulaEach.getSeccion().getId().compareTo(seccion.getId()) != 0) {
                            throw new PhobosException("Aula ocupada para el grupo seleccionado");
                        }
                    }
                }
            }

            horarioSeccion = new HorarioSeccion();
            horarioSeccion.setDia(diaHoraGrupoEach.getDia());
            horarioSeccion.setHora(diaHoraGrupoEach.getHora());
            horarioSeccion.setSeccion(seccion);
            horarioSeccionDAO.save(horarioSeccion);

            if (ObjectUtil.getParentTree(seccion, "aula.id") != null) {
                horarioAula = new HorarioAula();
                horarioAula.setAula(seccion.getAula());
                horarioAula.setDia(diaHoraGrupoEach.getDia());
                horarioAula.setHora(diaHoraGrupoEach.getHora());
                horarioAula.setSeccion(seccion);
                horarioAulaDAO.save(horarioAula);
            }
        }
        //  }
        seccionDAO.updateSeccionGrupoHora(seccion);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveAula(Long seccionId, Long aulaId, CicloAcademico cicloAcademico) {
        Seccion seccion = seccionDAO.find(seccionId);

        Aula aula = aulaId == null ? null : aulaDAO.find(aulaId);

        if (ObjectUtil.getParentTree(seccion, "aula.id") != null && aula == null) {
            horarioAulaDAO.deleteBySeccionAula(seccion, seccion.getAula());
        }

        if (aula == null) {
            seccion.setAula(aula);
            seccionDAO.update(seccion);

            List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccion(seccion);
            for (HorarioSeccion horarioSeccion : horariosSeccion) {
                horarioSeccion.setAula(aula);
                horarioSeccionDAO.update(horarioSeccion);
            }
            return;
        }

        Aula aulaAntes = seccion.getAula();
        if (aulaAntes != null) {
            if (seccion.getAula().getId().compareTo(aula.getId()) == 0) {
                throw new PhobosException("Esta sección ya tiene asignada esta aula.");
            }
        }

        if (seccion.getVacantes() != null) {
            if (seccion.getVacantes().compareTo(aula.getCapacidadAula()) > 0) {
                throw new PhobosException("La capacidad del aula no abarca las vacantes de la sección.");
            }
        }

        List<HorarioAula> horariosAulas = horarioAulaDAO.allByAula(aula, cicloAcademico);
        List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccion(seccion);

        HorarioAula horarioAula = null;
        for (HorarioSeccion horarioSeccionEach : horariosSeccion) {
            for (HorarioAula horarioAulaEach : horariosAulas) {
                if (horarioSeccionEach.getHoraDia().equals(horarioAulaEach.getHoraDia())) {
                    if (horarioAulaEach.getSeccion().getId().compareTo(seccion.getId()) != 0) {
                        throw new PhobosException("Aula ocupada para el grupo seleccionado");
                    }
                }
            }
        }

        seccion.setAula(aula);
        if (aulaAntes != null) {
            horarioAulaDAO.deleteBySeccionAula(seccion, aulaAntes);
        }

        LOOP_HORARIO_SECCION:
        for (HorarioSeccion horarioSeccionEach : horariosSeccion) {
            horarioSeccionEach.setAula(aula);
            horarioSeccionDAO.update(horarioSeccionEach);

            for (HorarioAula horarioAulaEach : horariosAulas) {
                if (horarioSeccionEach.getHoraDia().equals(horarioAulaEach.getHoraDia())) {
                    continue LOOP_HORARIO_SECCION;
                }
            }

            horarioAula = new HorarioAula();
            horarioAula.setAula(aula);
            horarioAula.setDia(horarioSeccionEach.getDia());
            horarioAula.setHora(horarioSeccionEach.getHora());
            horarioAula.setSeccion(seccion);
            horarioAulaDAO.save(horarioAula);
        }

        seccionDAO.updateSeccionAula(seccion);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveRestriccion(Seccion seccion, Usuario usuario, TipoRestriccionEnum tipoRestriccionEnum, List<Long> restricciones) {
        DateTime today = new DateTime();

        if (tipoRestriccionEnum.equals(TipoRestriccionEnum.ESP)) {
            List<Carrera> carrerasSeleccionadas = new ArrayList<>();
            for (Long restriccionEach : restricciones) {
                carrerasSeleccionadas.add(new Carrera(restriccionEach));
            }

            List<RestriccionCarrera> restriccionesCarrera = restriccionCarreraDAO.allActivasBySeccion(seccion);
            List<RestriccionFacultad> restriccionesFacultad = restriccionFacultadDAO.allActivasBySeccion(seccion);
            List<RestriccionModalidad> restriccionesModalidad = restriccionModalidadDAO.allActivasBySeccion(seccion);

            //Desactivar los deseleccionados
            for (RestriccionCarrera restriccionCarreraEach : restriccionesCarrera) {
                if (!carrerasSeleccionadas.contains(restriccionCarreraEach.getCarrera())) {
                    restriccionCarreraEach.setEstadoEnum(EstadoEnum.INA);
                    restriccionCarreraEach.setFechaModificacion(today.toDate());
                    restriccionCarreraEach.setUsuarioModificacion(usuario);
                    restriccionCarreraDAO.updateEstadoFechaUsuario(restriccionCarreraEach);
                }
            }
            //Grabar solo las nuevas selecciones
            for (Carrera carrerasEach : carrerasSeleccionadas) {
                if (!carrerasEach.isTieneRestriccion(restriccionesCarrera)) {
                    RestriccionCarrera restriccionCarrera = new RestriccionCarrera();
                    restriccionCarrera.setCarrera(carrerasEach);
                    restriccionCarrera.setEstadoEnum(EstadoEnum.ACT);
                    restriccionCarrera.setFechaRegistro(today.toDate());
                    restriccionCarrera.setUsuarioRegistro(usuario);
                    restriccionCarrera.setSeccion(seccion);
                    restriccionCarreraDAO.save(restriccionCarrera);
                }
            }

            for (RestriccionFacultad restriccionFacultadEach : restriccionesFacultad) {
                restriccionFacultadEach.setEstadoEnum(EstadoEnum.INA);
                restriccionFacultadEach.setFechaModificacion(today.toDate());
                restriccionFacultadEach.setUsuarioModificacion(usuario);
                restriccionFacultadDAO.updateEstadoFechaUsuario(restriccionFacultadEach);
            }

            for (RestriccionModalidad restriccionModalidadEach : restriccionesModalidad) {
                restriccionModalidadEach.setEstadoEnum(EstadoEnum.INA);
                restriccionModalidadEach.setFechaModificacion(today.toDate());
                restriccionModalidadEach.setUsuarioModificacion(usuario);
                restriccionModalidadDAO.updateEstadoFechaUsuario(restriccionModalidadEach);
            }

        } else if (tipoRestriccionEnum.equals(TipoRestriccionEnum.FAC)) {
            List<Facultad> facultadesSeleccionadas = new ArrayList<>();
            for (Long restriccionEach : restricciones) {
                facultadesSeleccionadas.add(new Facultad(restriccionEach));
            }

            List<RestriccionFacultad> restriccionesFacultad = restriccionFacultadDAO.allActivasBySeccion(seccion);
            List<RestriccionCarrera> restriccionesCarrera = restriccionCarreraDAO.allActivasBySeccion(seccion);
            List<RestriccionModalidad> restriccionesModalidad = restriccionModalidadDAO.allActivasBySeccion(seccion);

            //Desactivar los deseleccionados
            for (RestriccionFacultad restriccionFacultadEach : restriccionesFacultad) {
                if (!facultadesSeleccionadas.contains(restriccionFacultadEach.getFacultad())) {
                    restriccionFacultadEach.setEstadoEnum(EstadoEnum.INA);
                    restriccionFacultadEach.setFechaModificacion(today.toDate());
                    restriccionFacultadEach.setUsuarioModificacion(usuario);
                    restriccionFacultadDAO.updateEstadoFechaUsuario(restriccionFacultadEach);
                }
            }
            //Grabar solo las nuevas selecciones
            for (Facultad facultadEach : facultadesSeleccionadas) {
                if (!facultadEach.isTieneRestriccion(restriccionesFacultad)) {
                    RestriccionFacultad restriccionFacultad = new RestriccionFacultad();
                    restriccionFacultad.setFacultad(facultadEach);
                    restriccionFacultad.setEstadoEnum(EstadoEnum.ACT);
                    restriccionFacultad.setFechaRegistro(today.toDate());
                    restriccionFacultad.setUsuarioRegistro(usuario);
                    restriccionFacultad.setSeccion(seccion);
                    restriccionFacultadDAO.save(restriccionFacultad);
                }
            }

            for (RestriccionCarrera restriccionCarreraEach : restriccionesCarrera) {
                restriccionCarreraEach.setEstadoEnum(EstadoEnum.INA);
                restriccionCarreraEach.setFechaModificacion(today.toDate());
                restriccionCarreraEach.setUsuarioModificacion(usuario);
                restriccionCarreraDAO.updateEstadoFechaUsuario(restriccionCarreraEach);
            }

            for (RestriccionModalidad restriccionModalidadEach : restriccionesModalidad) {
                restriccionModalidadEach.setEstadoEnum(EstadoEnum.INA);
                restriccionModalidadEach.setFechaModificacion(today.toDate());
                restriccionModalidadEach.setUsuarioModificacion(usuario);
                restriccionModalidadDAO.updateEstadoFechaUsuario(restriccionModalidadEach);
            }
        } else if (tipoRestriccionEnum.equals(TipoRestriccionEnum.MOD)) {
            List<ModalidadEstudio> modalidadesSeleccioandas = new ArrayList<>();
            for (Long restriccionEach : restricciones) {
                modalidadesSeleccioandas.add(new ModalidadEstudio(restriccionEach));
            }

            List<RestriccionModalidad> restriccionesModalidad = restriccionModalidadDAO.allActivasBySeccion(seccion);
            List<RestriccionCarrera> restriccionesCarrera = restriccionCarreraDAO.allActivasBySeccion(seccion);
            List<RestriccionFacultad> restriccionesFacultad = restriccionFacultadDAO.allActivasBySeccion(seccion);

            //Desactivar los deseleccionados
            for (RestriccionModalidad restriccionaModalidadEach : restriccionesModalidad) {
                if (!modalidadesSeleccioandas.contains(restriccionaModalidadEach.getModalidadEstudio())) {
                    restriccionaModalidadEach.setEstadoEnum(EstadoEnum.INA);
                    restriccionaModalidadEach.setFechaModificacion(today.toDate());
                    restriccionaModalidadEach.setUsuarioModificacion(usuario);
                    restriccionModalidadDAO.updateEstadoFechaUsuario(restriccionaModalidadEach);
                }
            }
            //Grabar solo las nuevas selecciones
            for (ModalidadEstudio modalidadEstudioEach : modalidadesSeleccioandas) {
                if (!modalidadEstudioEach.isTieneRestriccion(restriccionesModalidad)) {
                    RestriccionModalidad restriccionModalidad = new RestriccionModalidad();
                    restriccionModalidad.setModalidadEstudio(modalidadEstudioEach);
                    restriccionModalidad.setEstadoEnum(EstadoEnum.ACT);
                    restriccionModalidad.setFechaRegistro(today.toDate());
                    restriccionModalidad.setUsuarioRegistro(usuario);
                    restriccionModalidad.setSeccion(seccion);
                    restriccionModalidadDAO.save(restriccionModalidad);
                }
            }

            for (RestriccionCarrera restriccionCarreraEach : restriccionesCarrera) {
                restriccionCarreraEach.setEstadoEnum(EstadoEnum.INA);
                restriccionCarreraEach.setFechaModificacion(today.toDate());
                restriccionCarreraEach.setUsuarioModificacion(usuario);
                restriccionCarreraDAO.updateEstadoFechaUsuario(restriccionCarreraEach);
            }

            for (RestriccionFacultad restriccionFacultadEach : restriccionesFacultad) {
                restriccionFacultadEach.setEstadoEnum(EstadoEnum.INA);
                restriccionFacultadEach.setFechaModificacion(today.toDate());
                restriccionFacultadEach.setUsuarioModificacion(usuario);
                restriccionFacultadDAO.updateEstadoFechaUsuario(restriccionFacultadEach);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void saveTipoRepitenciaRestriccion(Seccion seccion, Usuario usuario, List<TipoRepitencia> tiposRestriccionesSeleccionados) {
        DateTime today = new DateTime();

        List<RestriccionRepitencia> restriccionesRepitencia = restriccionRepitenciaDAO.allActivasBySeccion(seccion);

        //desactivamos los que ya no estan seleccionados
        for (RestriccionRepitencia restriccionRepEach : restriccionesRepitencia) {
            if (!tiposRestriccionesSeleccionados.contains(restriccionRepEach.getTipoRepitencia())) {
                restriccionRepEach.setEstadoEnum(EstadoEnum.INA);
                restriccionRepEach.setFechaModificacion(today.toDate());
                restriccionRepEach.setUsuarioModificacion(usuario);
                restriccionRepitenciaDAO.updateEstadoFechaUsuario(restriccionRepEach);
            }
        }

        //Grabar solo las nuevas selecciones
        for (TipoRepitencia tipoRepitenciaEach : tiposRestriccionesSeleccionados) {
            if (!tipoRepitenciaEach.isTieneRestriccion(restriccionesRepitencia)) {
                RestriccionRepitencia restriccionRepitencia = new RestriccionRepitencia();
                restriccionRepitencia.setTipoRepitencia(tipoRepitenciaEach);
                restriccionRepitencia.setEstadoEnum(EstadoEnum.ACT);
                restriccionRepitencia.setFechaRegistro(today.toDate());
                restriccionRepitencia.setUsuarioRegistro(usuario);
                restriccionRepitencia.setSeccion(seccion);
                restriccionRepitenciaDAO.save(restriccionRepitencia);
            }
        }
    }

    @Override
    public List<Aula> allPabellonesByOficina(Oficina oficina) {
        return aulaDAO.allPabellonesByOficina(oficina);
    }

    @Override
    public List<HorarioAula> allHorariosAula(Aula aula, CicloAcademico cicloAcademico) {
        return horarioAulaDAO.allByAula(aula, cicloAcademico);
    }

    @Override
    public List<Aula> allAulasByPabellon(Seccion seccion, Aula pabellon, CicloAcademico cicloAcademico) {
        List<Aula> aulas = aulaDAO.allByPabellon(pabellon);

        seccion = seccionDAO.find(seccion.getId());
        List<String> diaHoras = new ArrayList();
        List<HorarioSeccion> horarioSeccion = horarioSeccionDAO.allBySeccion(seccion);
        for (HorarioSeccion hdiaSecc : horarioSeccion) {
            diaHoras.add(hdiaSecc.getHoraDia());
        }

        List<HorarioAula> horariosAula = new ArrayList();
        if (!diaHoras.isEmpty()) {
            horariosAula = horarioAulaDAO.allByPabellonCicloDiasHoras(pabellon, cicloAcademico, diaHoras);
        }

        for (Aula aulaEach : aulas) {
            if (horariosAula.isEmpty()) {
                aulaEach.setDisponible(Boolean.TRUE);
                continue;
            }
            HorarioAula horarioAulaFound = horariosAula.stream().filter(req -> req.getAula().getId().equals(aulaEach.getId())).findFirst().orElse(null);
            if (horarioAulaFound != null) {
                aulaEach.setDisponible(false);
                if (aulaEach.getSeccion() == null) {
                    aulaEach.setSeccion(new ArrayList<>());
                }
                aulaEach.getSeccion().add(horarioAulaFound.getSeccion());
            } else {
                aulaEach.setDisponible(true);
            }
        }

        return aulas;
    }

    //@Override
//    private List<HorarioAula> allHorarioAulaByAulaCiclo(Aula aula, Seccion seccion, CicloAcademico cicloAcademico) {
//        seccion = seccionDAO.find(seccion.getId());
//        GrupoHoras grupoHoras = grupoHorasDAO.find(seccion.getGrupoHoras());
//
//        List<Dia> dias = new ArrayList();
//        List<Hora> horas = new ArrayList();
//        for (DiaHoraGrupo diaHoraGrupo : grupoHoras.getDiaHoraGrupo()) {
//            dias.add(diaHoraGrupo.getDia());
//            horas.add(diaHoraGrupo.getHora());
//        }
//
//        List<HorarioAula> horariosAula = horarioAulaDAO.allByPabellonCicloDiasHoras(aula, cicloAcademico, dias, horas);
//        return horariosAula;
//    }
    @Override
    public Aula findAula(Long aulaId) {
        Aula aula = aulaDAO.find(aulaId);
        return aula;
    }

    @Override
    public Aula findAulaFull(Long aulaId, CicloAcademico cicloAcademico) {
        Aula aula = aulaDAO.find(aulaId);
        List<HorarioAula> horariosAulas = horarioAulaDAO.allByAula(aula, cicloAcademico);
        aula.setHorariosAula(horariosAulas);
        return aula;
    }

    @Override
    public List<Oficina> allOficinasWithAula(List<Oficina> oficinas) {
        return oficinaDAO.allByOficinaWithAulas(oficinas);
    }

    @Override
    public List<Aula> allPabellonesByOficinasNoOera(List<Oficina> oficinas) {
        return aulaDAO.allPabellonesByOficinasNoOera(oficinas);
    }

    @Override
    public GrupoHoras findGrupoHoras(GrupoHoras grupoHoras) {
        return grupoHorasDAO.find(grupoHoras);
    }

    @Override
    public GrupoHoras findGrupoHorasFull(GrupoHoras grupoHoras, CicloAcademico cicloAcademico) {
        grupoHoras = grupoHorasDAO.find(grupoHoras);
        List<DiaHoraGrupo> diasHorasGrupo = diaHoraGrupoDAO.allDiaHoraGrupoByGrupo(grupoHoras, cicloAcademico);
        grupoHoras.setDiaHoraGrupo(diasHorasGrupo);
        return grupoHoras;
    }

    @Override
    public GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo) {
        return grupoSeccionDAO.resumenByCiclo(ciclo);
    }

    @Override
    public List<AnexoBoletin> allAnexosBySuperiorCiclo(String anexoSuperior, CicloAcademico ciclo) {
        GrupoAnexoEnum gpoAnexoE = GrupoAnexoEnum.get2(anexoSuperior);
        System.out.println(gpoAnexoE.name());
        System.out.println(gpoAnexoE.getValue());

        return anexoBoletinDAO.allBySuperiorCiclo(new AnexoBoletin(gpoAnexoE.getValue()), ciclo);
    }

    @Override
    public List<Facultad> allFacultadesActivas() {
        return facultadDAO.allActivos();
    }

    @Override
    public List<ModalidadEstudio> allModalidadesEstudioActivas() {
        return modalidadEstudioDAO.allActivos();
    }

    @Override
    public List<Carrera> allCarrerasActivas() {
        return carreraDAO.allActivos();
    }

    @Override
    public List<Carrera> allCarrerasActivasPrePost() {
        List<String> modalidades = new ArrayList<>();
        modalidades.add(ModalidadEstudioEnum.PRE.name());
        modalidades.add(ModalidadEstudioEnum.EPG.name());

        return carreraDAO.allActivasByModalidadesEstudio(modalidades);
    }

    @Override
    public List<TipoRepitencia> allTipoRepitencia() {
        return tipoRepitenciaDAO.all();
    }

    @Override
    public List<EventoCicloAcademico> allEventoCicloAcademicoForPeriodo(CicloAcademico cicloAcademico) {
        List<EventoAcademicoEnum> eventos = Arrays.asList(EventoAcademicoEnum.CLASES_PRE1, EventoAcademicoEnum.CLASES_PRE2);
        List<EventoCicloAcademico> eventosCiclosAcademicos = eventoCicloAcademicoDAO.allActivosByCicloEventos(cicloAcademico, eventos);
        return eventosCiclosAcademicos;
    }

    @Override
    public List<Date> allDatesEventoCicloAcademicoForPeriodo(CicloAcademico cicloAcademico) {
        List<EventoCicloAcademico> eventosCicloAcademicos = this.allEventoCicloAcademicoForPeriodo(cicloAcademico);
        List<Date> fechas = new ArrayList<>();
        for (EventoCicloAcademico eventosCicloAcademico : eventosCicloAcademicos) {
            fechas.add(new DateTime(eventosCicloAcademico.getFechaInicio()).toLocalDate().toDate());
            fechas.add(new DateTime(eventosCicloAcademico.getFechaFin()).toLocalDate().toDate());
        }
        Collections.sort(fechas, (Date va1, Date va2) -> va1.compareTo(va2));
        return fechas;
    }

    @Override
    public Aula findAulaActiveByCode(String codigoAula) {
        Aula aula = aulaDAO.findActiveByCode(codigoAula);
        if (aula == null) {
            throw new PhobosException("Aula ingresada no existe");
        }
        return aula;
    }

    @Override
    public GrupoHoras findGrupoHorasForDirectUpdate(String code, CicloAcademico cicloAcademico, Seccion seccion) {
        seccion = seccionDAO.find(seccion.getId());
        GrupoHoras grupoHorario = grupoHorasDAO.findByCodeTipoCiclo(code, cicloAcademico.getTipoEnum());
        List<Dia> dias = diaDAO.all();
        List<Dia> utilDays = new ArrayList<>();

        if (grupoHorario == null) {
            throw new PhobosException("Grupo Horario ingresada no existe");
        }
        List<DiaHoraGrupo> diasGrupoHoras = diaHoraGrupoDAO.allDiaHoraGrupoByGrupo(grupoHorario, cicloAcademico);
        Collections.sort(diasGrupoHoras, (p1, p2) -> p1.getHora().getNumero().compareTo(p2.getHora().getNumero()));
        grupoHorario.setDiaHoraGrupo(diasGrupoHoras);

        for (Dia dia : dias) {
            dia.setDiaHoraGrupo(new ArrayList<>());

            for (DiaHoraGrupo horasPorDia : grupoHorario.getDiaHoraGrupo()) {
                if (horasPorDia.getDia().getId().equals(dia.getId())) {
                    dia.getDiaHoraGrupo().add(horasPorDia);
                }
            }
            if (!dia.getDiaHoraGrupo().isEmpty()) {
                utilDays.add(dia);
            }
        }

        if (!grupoHorario.isPermiteCeroHoras()) {
            if (seccion.getHorasSemanales().compareTo(diasGrupoHoras.size()) != 0) {
                throw new PhobosException("Grupo Horario no es compatible con las horas semanales de la sección");
            }
        }
        return grupoHorario;
    }

}
