package pe.edu.lamolina.pivot.controller.academico.gposeccion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.model.enums.GrupoAnexoEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.horario.TipoGrupoHoras;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;

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
        return grupoHorasDAO.allByTipoGrupoHoraDyna(filter, tipoGrupoHoras, cicloAcademico, seccion);
    }

    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupo(List<GrupoHoras> grupos) {
        return diaHoraGrupoDAO.allDiaHoraGrupo(grupos);
    }

    @Override
    @Transactional(readOnly = false)
    public GrupoSeccion saveGpoSeccionHeader(GrupoSeccion grupoSeccion, CicloAcademico cicloAcademico) {
        GrupoSeccion lastGrupoSeccion = grupoSeccionDAO.findLast();
        Curso curso = cursoDAO.find(grupoSeccion.getCurso().getId());

        String codigo = generateCodigo(lastGrupoSeccion.getCodigo());
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

        grupoSeccion.setSecciones(new ArrayList<Seccion>());
        if (curso.isTipoCursoTEO()) {
            Seccion seccionTEO = new Seccion();
            seccionTEO.setGrupoSeccion(grupoSeccion);
            seccionTEO.setCodigo(codigo + "0");
            seccionTEO.setCodigo2(seccionTEO.getCodigo());
            seccionTEO.setEstadoEnum(EstadoEnum.CRE);
            seccionTEO.setTipoSeccionEnum(TipoSeccionEnum.TEO);
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
            docenteSeccion.setPorcentajeCarga(BigDecimal.valueOf(100));
            seccionTEO.getDocenteSeccion().add(docenteSeccion);

            grupoSeccion.getSecciones().add(seccionTEO);
        }
        if (curso.isTipoCursoPRA()) {
            Seccion seccionPRA = new Seccion();
            seccionPRA.setGrupoSeccion(grupoSeccion);
            seccionPRA.setCodigo(codigo + "1");
            seccionPRA.setCodigo2(seccionPRA.getCodigo());
            seccionPRA.setEstadoEnum(EstadoEnum.CRE);
            seccionPRA.setTipoSeccionEnum(TipoSeccionEnum.PRA);
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
            docenteSeccion.setPorcentajeCarga(BigDecimal.valueOf(100));
            seccionPRA.getDocenteSeccion().add(docenteSeccion);

            grupoSeccion.getSecciones().add(seccionPRA);
        }
        if (curso.isTipoCursoTEOPRA()) {
            Seccion seccionTCUR = new Seccion();
            seccionTCUR.setGrupoSeccion(grupoSeccion);
            seccionTCUR.setCodigo(codigo + "0");
            seccionTCUR.setCodigo2(seccionTCUR.getCodigo());
            seccionTCUR.setEstadoEnum(EstadoEnum.CRE);
            seccionTCUR.setTipoSeccionEnum(TipoSeccionEnum.TCUR);
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
            docenteSeccion.setPorcentajeCarga(BigDecimal.valueOf(50));
            seccionTCUR.getDocenteSeccion().add(docenteSeccion);

            grupoSeccion.getSecciones().add(seccionTCUR);

            Seccion seccionPCUR = new Seccion();
            seccionPCUR.setGrupoSeccion(grupoSeccion);
            seccionPCUR.setCodigo(codigo + "1");
            seccionPCUR.setCodigo2(seccionPCUR.getCodigo());
            seccionPCUR.setEstadoEnum(EstadoEnum.CRE);
            seccionPCUR.setTipoSeccionEnum(TipoSeccionEnum.PCUR);
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
            docenteSeccion2.setPorcentajeCarga(BigDecimal.valueOf(50));
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
        seccionPCUR.setCodigo(grupoSeccion.getCodigo() + (secciones.size() + 1));
        seccionPCUR.setCodigo2(seccionPCUR.getCodigo());
        seccionPCUR.setEstadoEnum(EstadoEnum.CRE);
        seccionPCUR.setTipoSeccionEnum(TipoSeccionEnum.PCUR);
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
        seccionPCUR.getDocenteSeccion().add(docenteSeccion2);

        seccionDAO.save(seccionPCUR);

    }

    @Override
    @Transactional(readOnly = false)
    public void addDocenteSeccion(Seccion seccion) {
        seccion = seccionDAO.find(seccion.getId());

        Docente docenteDefault = docenteDAO.findByCode(Constantine.DOCENTE_INDETERMINADO);
        DateTime today = new DateTime();

        DocenteSeccion docenteSeccion = new DocenteSeccion();
        docenteSeccion.setDocente(docenteDefault);
        docenteSeccion.setCodigoSeccion(seccion.getCodigo());
        docenteSeccion.setEstado(EstadoEnum.ACT.name());
        docenteSeccion.setFechaInicio(today.toDate());
        docenteSeccion.setPrincipal(BigDecimal.ZERO.intValue());
        docenteSeccion.setSeccion(seccion);
        docenteSeccionDAO.save(docenteSeccion);
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
    public void deleteDocSeccion(DocenteSeccion docenteSeccion) {
        docenteSeccion = docenteSeccionDAO.find(docenteSeccion.getId());
        docenteSeccionDAO.delete(docenteSeccion);
    }

    @Override
    public List<DocenteSeccion> allDocentesSeccionBySeccion(Seccion seccion) {
        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allBySeccion(seccion);
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
    public GpoSeccionResumen resumen() {
        return grupoSeccionDAO.resumen();
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
    public void actualizarSeccionVacantes(Seccion seccion) {
        Seccion seccioDB = seccionDAO.find(seccion.getId());
        List<Seccion> secciones = seccionDAO.allByGposSeccion(seccioDB.getGrupoSeccion());
        //todo verificar cantidad de matriculados
        Seccion seccionTCUR = null;
        Integer vacantes = BigDecimal.ZERO.intValue();

        for (Seccion seccionEach : secciones) {
            if (seccionEach.getId().compareTo(seccion.getId()) == 0) {
                seccionEach.setVacantes(seccion.getVacantes());
            }
            if (seccionEach.isTipoSeccionTCUR()) {
                seccionTCUR = seccionEach;
            }
            if (seccionEach.isTipoSeccionPCUR()) {
                vacantes = vacantes + seccionEach.getVacantes();
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
        //  List<Seccion> secciones = service.allSeccionesByGrupo(new GrupoSeccion(gruposeccionId));

        seccionDAO.updateSeccionVacantes(seccion);

    }

    @Override
    @Transactional(readOnly = false)
    public void updatePorcentajeAvance(DocenteSeccion docenteSeccion) {
        docenteSeccionDAO.updatePorcentajeAvance(docenteSeccion);
    }

    @Override
    public Seccion findSeccion(Long seccionId) {
        Seccion seccion = seccionDAO.find(seccionId);
        Curso curso = cursoDAO.find(seccion.getGrupoSeccion().getCurso().getId());
        if (ObjectUtil.getParentTree(seccion, "grupoHoras.id") != null) {
            GrupoHoras grupoHoras = grupoHorasDAO.find(seccion.getGrupoHoras());
            seccion.setGrupoHoras(grupoHoras);
        }
        if (ObjectUtil.getParentTree(seccion, "aula.id") != null) {
            Aula aula = aulaDAO.find(seccion.getAula().getId());
            seccion.setAula(aula);
        }
        seccion.getGrupoSeccion().setCurso(curso);

        List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccion(seccion);
        seccion.setHorarioSeccion(horariosSeccion);
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
        //buscando grupos con las horas requeridas por dia
        for (GrupoHoras grupoHora : grupoHoras) {
            for (Dia dia : dias) {
                List<DiaHoraGrupo> horasPorDias = new ArrayList<>();

                for (DiaHoraGrupo horasPorDia : grupoHora.getDiaHoraGrupo()) {
                    if (horasPorDia.getDia().getId().equals(dia.getId())) {
                        horasPorDias.add(horasPorDia);
                    }
                }
                if (horasPorDias.size() == seccion.getHorasSemanales()) {
                    Integer numeroAnterior = null;
                    boolean success = true;
                    for (DiaHoraGrupo diaHoraGrupo : horasPorDias) {
                        if (numeroAnterior == null) {
                            numeroAnterior = diaHoraGrupo.getHora().getNumero();
                        } else {
                            numeroAnterior++;
                            if (numeroAnterior != diaHoraGrupo.getHora().getNumero().intValue()) {
                                success = false;
                            }
                        }
                    }
                    if (success) {
                        grupoHora.setHorasMismoDia(Boolean.TRUE);
                        grupoHora.setDiaHoraGrupo(horasPorDias);
                        grupoHorasFiltrado.add(grupoHora);
                    }
                }
            }
        }

        return grupoHorasFiltrado;
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
    public void saveSeccionGrupoHorario(Long seccionId, List<DiaHoraGrupo> diasHorasGrupo, CicloAcademico cicloAcademico) {
        if (diasHorasGrupo.isEmpty()) {
            throw new PhobosException("Debe seleccionar las horas");
        }

        Seccion seccion = seccionDAO.find(seccionId);
        List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccion(seccion);
        seccion.setHorarioSeccion(horariosSeccion);

        GrupoHoras grupoHoraOld = seccion.getGrupoHoras();
        GrupoHoras grupoHorasNew = grupoHorasDAO.find(diasHorasGrupo.get(0).getGrupoHorario().getId());
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

            if (!horarioSeccionEach.isTieneDiaHoraGrupo(diasHorasGrupo)) {
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
        for (DiaHoraGrupo diaHoraGrupoEach : diasHorasGrupo) {
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

        Aula aula = aulaDAO.find(aulaId);

        if (ObjectUtil.getParentTree(seccion, "aula.id") != null) {
            if (seccion.getAula().getId().compareTo(aula.getId()) != 0) {
                horarioAulaDAO.deleteBySeccionAula(seccion, seccion.getAula());
            }
        } else {
            //en caso de que no cambio a un aula diferente, se puede eliminar algunos horarios aula determinados
            //todo
            horarioAulaDAO.deleteBySeccionAula(seccion, aula);
        }

        seccion.setAula(aula);

        if (seccion.getVacantes() != null) {
            if (seccion.getVacantes().compareTo(aula.getAforo()) > 0) {
                throw new PhobosException("El aforo del aula no abarca las vacantes de la sección.");
            }
        }

        if (ObjectUtil.getParentTree(seccion, "grupoHoras.id") != null) {
            List<HorarioAula> horariosAulas = horarioAulaDAO.allByAula(aula, cicloAcademico);
            List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccion(seccion);

            GrupoHoras grupoHoras = grupoHorasDAO.find(seccion.getGrupoHoras().getId());
            seccion.setGrupoHoras(grupoHoras);

            HorarioAula horarioAula = null;
            for (HorarioSeccion horarioSeccionEach : horariosSeccion) {

                for (HorarioAula horarioAulaEach : horariosAulas) {
                    if (horarioSeccionEach.getDia().getId().equals(horarioAulaEach.getDia().getId())
                            && horarioSeccionEach.getHora().getId().equals(horarioAulaEach.getHora().getId())) {
                        if (horarioAulaEach.getSeccion().getId().compareTo(seccion.getId()) != 0) {
                            throw new PhobosException("Aula ocupada para el grupo seleccionado");
                        }
                    }
                }

                horarioAula = new HorarioAula();
                horarioAula.setAula(aula);
                horarioAula.setDia(horarioSeccionEach.getDia());
                horarioAula.setHora(horarioSeccionEach.getHora());
                horarioAula.setSeccion(seccion);
                horarioAulaDAO.save(horarioAula);
            }
        }
        seccionDAO.updateSeccionAula(seccion);
    }

    @Override
    public List<Aula> allAulasSuperiorByOficina(Oficina oficina) {
        return aulaDAO.allAulasSuperiorByOficina(oficina);
    }

    @Override
    public List<HorarioAula> allHorariosAula(Aula aula, CicloAcademico cicloAcademico) {
        return horarioAulaDAO.allByAula(aula, cicloAcademico);
    }

    @Override
    public List<Aula> allAulasBySuperior(Seccion seccion, Aula aula, CicloAcademico cicloAcademico) {
        List<Aula> aulas = aulaDAO.allBySuperior(aula);

        seccion = seccionDAO.find(seccion.getId());
        GrupoHoras grupoHoras = null;
        if (ObjectUtil.getParentTree(seccion, "grupoHoras.id") != null) {
            grupoHoras = grupoHorasDAO.find(seccion.getGrupoHoras());
        }

        List<HorarioAula> horariosAula = null;
        if (grupoHoras != null) {
            List<Dia> dias = new ArrayList<>();
            List<Hora> horas = new ArrayList<>();
            for (DiaHoraGrupo diaHoraGrupo : grupoHoras.getDiaHoraGrupo()) {
                dias.add(diaHoraGrupo.getDia());
                horas.add(diaHoraGrupo.getHora());
            }
            horariosAula = horarioAulaDAO.allByAulaCicloDiasHoras(aula, cicloAcademico, dias, horas);
        }

        for (Aula aulaEach : aulas) {
            if (horariosAula == null || horariosAula.isEmpty()) {
                aulaEach.setDisponible(Boolean.TRUE);
                continue;
            }
            if (horariosAula != null && !horariosAula.isEmpty()) {
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
        }

        return aulas;
    }

    @Override
    public List<HorarioAula> allHorarioAulaByAulaCiclo(Aula aula, Seccion seccion, CicloAcademico cicloAcademico) {
        seccion = seccionDAO.find(seccion.getId());
        GrupoHoras grupoHoras = grupoHorasDAO.find(seccion.getGrupoHoras());

        List<Dia> dias = new ArrayList<>();
        List<Hora> horas = new ArrayList<>();
        for (DiaHoraGrupo diaHoraGrupo : grupoHoras.getDiaHoraGrupo()) {
            dias.add(diaHoraGrupo.getDia());
            horas.add(diaHoraGrupo.getHora());
        }

        List<HorarioAula> horariosAula = horarioAulaDAO.allByAulaCicloDiasHoras(aula, cicloAcademico, dias, horas);
        return horariosAula;
    }

    @Override
    public Aula findAula(Long aulaId) {
        Aula aula = aulaDAO.find(aulaId);
        return aula;
    }

    @Override
    public List<Oficina> allOficinasWithAula(List<Oficina> oficinas) {
        return oficinaDAO.allByOficinaWithAulas(oficinas);
    }

    @Override
    public List<Aula> allAulaSuperiorByOficinasWithAula(List<Oficina> oficinas) {
        return aulaDAO.allSuperiorByOficinaWithAulas(oficinas);
    }

    @Override
    public GrupoHoras findGrupoHoras(GrupoHoras grupoHoras) {
        return grupoHorasDAO.find(grupoHoras);
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

}
