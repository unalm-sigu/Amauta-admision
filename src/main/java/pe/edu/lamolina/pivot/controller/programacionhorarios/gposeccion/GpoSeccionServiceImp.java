package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import pe.albatross.zelpers.miscelanea.CodeGenerator;
import pe.albatross.zelpers.miscelanea.Commutator;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.edu.lamolina.model.academico.AmpliacionVacantes;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.PrecioCursoEstructura;
import pe.edu.lamolina.model.academico.RestriccionCarrera;
import pe.edu.lamolina.model.academico.RestriccionFacultad;
import pe.edu.lamolina.model.academico.RestriccionModalidad;
import pe.edu.lamolina.model.academico.RestriccionRepitencia;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoRepitencia;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.PeriodoEncuesta;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.model.enums.EstadoVacanteAlumnoEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.GrupoAnexoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.SituacionDocenteEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
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
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionCarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionFacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionModalidadDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionRepitenciaDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoRepitenciaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ConfiguraEncuestaDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteDAO;
import pe.edu.lamolina.pivot.dao.encuesta.PeriodoEncuestaDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.vacante.VacanteAlumnoDAO;
import pe.edu.lamolina.pivot.zelper.enums.TipoRestriccionEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.dao.academico.AmpliacionVacantesDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.PrecioCursoEstructuraDAO;

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
    @Autowired
    EncuestaDocenteDAO encuestaDocenteDAO;
    @Autowired
    PeriodoEncuestaDAO periodoEncuestaDAO;
    @Autowired
    ConfiguraEncuestaDAO configuraEncuestaDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    @Autowired
    AmpliacionVacantesDAO ampliacionVacanteDAO;
    @Autowired
    CursoCicloAcademicoDAO cursoCicloAcademicoDAO;

    @Autowired
    PrecioCursoEstructuraDAO precioCursoEstructuraDAO;

    @Override
    public CicloAcademico findCiclo(CicloAcademico cicloAcademico) {
        return cicloAcademicoDAO.find(cicloAcademico);
    }

    @Override
    public Oficina findOficinaOera() {
        return oficinaDAO.findByCode("OERA");
    }

    @Override
    public List<Oficina> allOficinas(Compania compania) {
        return oficinaDAO.allByCompania(compania);
    }

    @Override
    public GrupoSeccion findGpoSeccion(Long id) {
        GrupoSeccion gpoSecc = grupoSeccionDAO.find(id);
        List<Seccion> secciones = seccionDAO.allByGposSeccion(gpoSecc);
        gpoSecc.setSecciones(secciones);

        CicloAcademico ciclo = gpoSecc.getCicloAcademico();
        Curso curso = gpoSecc.getCurso();
        String tpc = gpoSecc.getCurso().getTpc();
        if (ciclo.getTipoEnum() == TipoCicloEnum.NIV) {
            CursoCicloAcademico cursoCiclo = cursoCicloAcademicoDAO.findByCursoCiclo(curso, ciclo);

            PrecioCursoEstructura precioCurso = precioCursoEstructuraDAO.findByTpcCiclo(tpc, ciclo);

            curso.setPrecio(cursoCiclo.getPrecio().add(cursoCiclo.getPrecioAdicional()));
            curso.setPrecioTpc(precioCurso.getPrecio());
        }

        List<DocenteSeccion> docenteSeccion = docenteSeccionDAO.allBySecciones(secciones);
        Map<Long, List<DocenteSeccion>> mapDocSeccion = TypesUtil.convertListToMapList("seccion.id", docenteSeccion);

        List<RestriccionModalidad> restriccionesMod = restriccionModalidadDAO.allActivasBySecciones(secciones);
        List<RestriccionFacultad> restriccionesFac = restriccionFacultadDAO.allActivasBySecciones(secciones);
        List<RestriccionCarrera> restriccionesCarr = restriccionCarreraDAO.allActivasBySecciones(secciones);
        List<RestriccionRepitencia> restriccionesRep = restriccionRepitenciaDAO.allActivasBySecciones(secciones);
        List<AmpliacionVacantes> ampliaciones = ampliacionVacanteDAO.allBySecciones(secciones);

        Map<Long, List<RestriccionModalidad>> mapRestriccionMod = TypesUtil.convertListToMapList("seccion.id", restriccionesMod);
        Map<Long, List<RestriccionFacultad>> mapRestriccionFac = TypesUtil.convertListToMapList("seccion.id", restriccionesFac);
        Map<Long, List<RestriccionCarrera>> mapRestriccionCarr = TypesUtil.convertListToMapList("seccion.id", restriccionesCarr);
        Map<Long, List<RestriccionRepitencia>> mapRestriccionRep = TypesUtil.convertListToMapList("seccion.id", restriccionesRep);
        Map<Long, List<AmpliacionVacantes>> mapAmpliaciones = TypesUtil.convertListToMapList("seccion.id", ampliaciones);

        for (Seccion seccion : secciones) {
            seccion.setDocenteSeccion(getList(mapDocSeccion.get(seccion.getId())));
            seccion.setRestriccionesModalidad(getList(mapRestriccionMod.get(seccion.getId())));
            seccion.setRestriccionesFacultad(getList(mapRestriccionFac.get(seccion.getId())));
            seccion.setRestriccionesCarrera(getList(mapRestriccionCarr.get(seccion.getId())));
            seccion.setRestriccionesRepitencia(getList(mapRestriccionRep.get(seccion.getId())));
            seccion.setAmpliacionesVacantes(getList(mapAmpliaciones.get(seccion.getId())));
        }

        return gpoSecc;
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

        List<RestriccionModalidad> restriccionesMod = restriccionModalidadDAO.allActivasBySecciones(secciones);
        List<RestriccionFacultad> restriccionesFac = restriccionFacultadDAO.allActivasBySecciones(secciones);
        List<RestriccionCarrera> restriccionesCarr = restriccionCarreraDAO.allActivasBySecciones(secciones);
        List<RestriccionRepitencia> restriccionesRep = restriccionRepitenciaDAO.allActivasBySecciones(secciones);

        Map<Long, List<RestriccionModalidad>> mapRestriccionMod = TypesUtil.convertListToMapList("seccion.id", restriccionesMod);
        Map<Long, List<RestriccionFacultad>> mapRestriccionFac = TypesUtil.convertListToMapList("seccion.id", restriccionesFac);
        Map<Long, List<RestriccionCarrera>> mapRestriccionCarr = TypesUtil.convertListToMapList("seccion.id", restriccionesCarr);
        Map<Long, List<RestriccionRepitencia>> mapRestriccionRep = TypesUtil.convertListToMapList("seccion.id", restriccionesRep);

        for (Seccion seccion : secciones) {
            List<DocenteSeccion> doceentesSecc = mapDocSeccion.get(seccion.getId());
            seccion.setDocenteSeccion(doceentesSecc == null ? new ArrayList() : doceentesSecc);

            List<RestriccionModalidad> restriccionesModSecc = mapRestriccionMod.get(seccion.getId());
            seccion.setRestriccionesModalidad(restriccionesModSecc == null ? new ArrayList() : restriccionesModSecc);

            List<RestriccionFacultad> restriccionesFacSecc = mapRestriccionFac.get(seccion.getId());
            seccion.setRestriccionesFacultad(restriccionesFacSecc == null ? new ArrayList() : restriccionesFacSecc);

            List<RestriccionCarrera> restriccionesCarrSecc = mapRestriccionCarr.get(seccion.getId());
            seccion.setRestriccionesCarrera(restriccionesCarrSecc == null ? new ArrayList() : restriccionesCarrSecc);

            List<RestriccionRepitencia> restriccionesRepSecc = mapRestriccionRep.get(seccion.getId());
            seccion.setRestriccionesRepitencia(restriccionesRepSecc == null ? new ArrayList() : restriccionesRepSecc);
        }

        return gsecciones;
    }

    @Override
    public List<GrupoSeccion> allCleanByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        return grupoSeccionDAO.allByDynatable(filter, ciclo);
    }

    @Override
    @Transactional
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
                List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);
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
    public List<GrupoHoras> allGrupoHorasZetasDyna(DynatableFilter filter, CicloAcademico cicloAcademico) {
        List<GrupoHoras> grupos = grupoHorasDAO.allZetasByDynatable(filter);
        List<DiaHoraGrupo> horariosGrupos = diaHoraGrupoDAO.allByGruposCiclo(grupos, cicloAcademico);
        Map<Long, List<DiaHoraGrupo>> mapHorarioByGpo = TypesUtil.convertListToMapList("grupoHorario.id", horariosGrupos);

        for (GrupoHoras grupo : grupos) {
            grupo.setDiaHoraGrupo(createList(mapHorarioByGpo.get(grupo.getId())));
        }
        return grupos;
    }

    @Override
    public List<GrupoHoras> allGrupoByTipoGpoSeccionDynatable(DynatableFilter filter,
            TipoGrupoHoras tipoGrupoHoras,
            CicloAcademico cicloAcademico,
            Seccion seccion) {
        long t1 = System.currentTimeMillis();
        List<GrupoHoras> gruposHorasFilter = this.allGrupoHorasBySeccionAndTipoGrupoHoras(seccion, tipoGrupoHoras, cicloAcademico);
        long t2 = System.currentTimeMillis();
        System.out.println("allGposByTipo gruposHorasFilter en " + (t2 - t1) + " mseg");
        List<GrupoHoras> grupos = grupoHorasDAO.allByTipoGpoDynatable(filter, tipoGrupoHoras, cicloAcademico, gruposHorasFilter);
        long t3 = System.currentTimeMillis();
        System.out.println("allGposByTipo grupos en " + (t3 - t1) + " mseg");
        List<DiaHoraGrupo> horariosGrupos = diaHoraGrupoDAO.allByGruposCiclo(grupos, cicloAcademico);
        long t4 = System.currentTimeMillis();
        System.out.println("allGposByTipo horariosGrupos en " + (t4 - t1) + " mseg");
        Map<Long, List<DiaHoraGrupo>> mapHorarioByGpo = TypesUtil.convertListToMapList("grupoHorario.id", horariosGrupos);

        for (GrupoHoras grupo : grupos) {
            grupo.setDiaHoraGrupo(createList(mapHorarioByGpo.get(grupo.getId())));
        }
        return grupos;
    }

    @Override
    @Transactional
    public List<GrupoSeccion> saveGpoSeccionHeader(GrupoSeccion gpoSeccForm, CicloAcademico ciclo) {
        List<String> codigosByCiclo = grupoSeccionDAO.allCodigoByCiclo(ciclo);
        List<String> codigos2ByCiclo = grupoSeccionDAO.allCodigo2ByCiclo(ciclo);
        Curso curso = cursoDAO.find(gpoSeccForm.getCurso().getId());

        Integer horasTeoria = curso.getHorasTeoria() == null ? 0 : curso.getHorasTeoria();
        Integer horasPractica = curso.getHorasPractica() == null ? 0 : curso.getHorasPractica();

        List<GrupoSeccion> gpoSecciones = new ArrayList();
        Integer cantidad = gpoSeccForm.getCantidad();
        for (int i = 0; i < cantidad; i++) {
            String codigo = CodeGenerator.getNextCode(codigosByCiclo, 0);
            String codigo2 = CodeGenerator.getNextCode(codigos2ByCiclo, 0);
            GrupoSeccion gpoSeccNew = new GrupoSeccion();
            gpoSeccNew.setAnexoBoletin(gpoSeccForm.getAnexoBoletin());
            gpoSeccNew.setCicloAcademico(ciclo);
            gpoSeccNew.setCodigo(codigo);
            gpoSeccNew.setCodigo2(codigo2);
            gpoSeccNew.setCurso(curso);
            gpoSeccNew.setCursoDirigido(gpoSeccForm.getCursoDirigido());
            gpoSeccNew.setHorasPractica(horasPractica);
            gpoSeccNew.setHorasTeoria(horasTeoria);
            gpoSeccNew.setEstadoEnum(SeccionEstadoEnum.ACT);

            gpoSeccNew = saveGpoSeccion(gpoSeccNew, ciclo, codigo, codigo2, curso);
            codigosByCiclo.add(codigo);
            codigos2ByCiclo.add(codigo2);
            gpoSecciones.add(gpoSeccNew);
        }
        return gpoSecciones;
    }

    private GrupoSeccion saveGpoSeccion(
            GrupoSeccion grupoSeccion,
            CicloAcademico ciclo,
            String codigo,
            String codigo2,
            Curso curso) {

        grupoSeccion.setVersion(BigDecimal.ONE.toString());
        grupoSeccion.setEstadoGrupoEnum(EstadoGrupoSeccionEnum.ABI);
        grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.PEND);

        Integer horasTeoria = grupoSeccion.getHorasTeoria();
        Integer horasPractica = grupoSeccion.getHorasPractica();

        Docente docenteDefault = docenteDAO.findByCode(Constantine.DOCENTE_INDETERMINADO);
        final BigDecimal PORCENTAJE_CARGA = new BigDecimal(100);

        grupoSeccion.setSecciones(new ArrayList<Seccion>());
        if (curso.isTipoCursoTEO()) {
            Seccion seccionTEO = new Seccion();
            seccionTEO.setGrupoSeccion(grupoSeccion);
            seccionTEO.setCodigo(codigo + "0");
            seccionTEO.setCodigo2(codigo2 + "0");
            seccionTEO.setEstadoEnum(SeccionEstadoEnum.CRE);
            seccionTEO.setTipoSeccionEnum(TipoSeccionEnum.TEO);
            seccionTEO.setSituacionDocenteEnum(SituacionDocenteEnum.ERR);
            seccionTEO.setHorasSemanales(horasTeoria);
            seccionTEO.setVacantes(0);
            seccionTEO.setMatriculados(0);
            seccionTEO.setPrematriculados(0);
            seccionTEO.setReservados(0);
            seccionTEO.setRetirados(0);

            seccionTEO.setDocenteSeccion(new ArrayList<>());
            DocenteSeccion docenteSeccion = new DocenteSeccion();
            docenteSeccion.setDocente(docenteDefault);
            docenteSeccion.setCodigoSeccion(seccionTEO.getCodigo());
            docenteSeccion.setEstado(EstadoEnum.ACT.name());
            docenteSeccion.setFechaInicio(ciclo.getFechaRegistro());
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
            seccionPRA.setCodigo2(codigo2 + "1");
            seccionPRA.setEstadoEnum(SeccionEstadoEnum.CRE);
            seccionPRA.setTipoSeccionEnum(TipoSeccionEnum.PRA);
            seccionPRA.setSituacionDocenteEnum(SituacionDocenteEnum.ERR);
            seccionPRA.setHorasSemanales(horasPractica);
            seccionPRA.setVacantes(0);
            seccionPRA.setMatriculados(0);
            seccionPRA.setPrematriculados(0);
            seccionPRA.setReservados(0);
            seccionPRA.setRetirados(0);

            seccionPRA.setDocenteSeccion(new ArrayList<>());
            DocenteSeccion docenteSeccion = new DocenteSeccion();
            docenteSeccion.setDocente(docenteDefault);
            docenteSeccion.setCodigoSeccion(seccionPRA.getCodigo());
            docenteSeccion.setEstado(EstadoEnum.ACT.name());
            docenteSeccion.setFechaInicio(ciclo.getFechaRegistro());
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
            seccionTCUR.setCodigo2(codigo2 + "0");
            seccionTCUR.setEstadoEnum(SeccionEstadoEnum.CRE);
            seccionTCUR.setTipoSeccionEnum(TipoSeccionEnum.TCUR);
            seccionTCUR.setSituacionDocenteEnum(SituacionDocenteEnum.ERR);
            seccionTCUR.setHorasSemanales(horasTeoria);
            seccionTCUR.setVacantes(0);
            seccionTCUR.setMatriculados(0);
            seccionTCUR.setPrematriculados(0);
            seccionTCUR.setReservados(0);
            seccionTCUR.setRetirados(0);

            seccionTCUR.setDocenteSeccion(new ArrayList<>());
            DocenteSeccion docenteSeccion = new DocenteSeccion();
            docenteSeccion.setDocente(docenteDefault);
            docenteSeccion.setCodigoSeccion(seccionTCUR.getCodigo());
            docenteSeccion.setEstado(EstadoEnum.ACT.name());
            docenteSeccion.setFechaInicio(ciclo.getFechaRegistro());
            docenteSeccion.setPrincipal(BigDecimal.ONE.intValue());
            docenteSeccion.setSeccion(seccionTCUR);
            docenteSeccion.setPorcentajeCarga(PORCENTAJE_CARGA);
            seccionTCUR.getDocenteSeccion().add(docenteSeccion);

            grupoSeccion.getSecciones().add(seccionTCUR);

            Seccion seccionPCUR = new Seccion();
            seccionPCUR.setGrupoSeccion(grupoSeccion);
            seccionPCUR.setCodigo(codigo + "1");
            seccionPCUR.setCodigo2(codigo2 + "1");
            seccionPCUR.setEstadoEnum(SeccionEstadoEnum.CRE);
            seccionPCUR.setTipoSeccionEnum(TipoSeccionEnum.PCUR);
            seccionPCUR.setSituacionDocenteEnum(SituacionDocenteEnum.ERR);
            seccionPCUR.setHorasSemanales(horasPractica);
            seccionPCUR.setVacantes(0);
            seccionPCUR.setMatriculados(0);
            seccionPCUR.setPrematriculados(0);
            seccionPCUR.setReservados(0);
            seccionPCUR.setRetirados(0);

            seccionPCUR.setDocenteSeccion(new ArrayList<>());
            DocenteSeccion docenteSeccion2 = new DocenteSeccion();
            docenteSeccion2.setDocente(docenteDefault);
            docenteSeccion2.setCodigoSeccion(seccionPCUR.getCodigo());
            docenteSeccion2.setEstado(EstadoEnum.ACT.name());
            docenteSeccion2.setFechaInicio(ciclo.getFechaRegistro());
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
    @Transactional
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
        seccionPCUR.setVacantes(0);
        seccionPCUR.setPrematriculados(0);
        seccionPCUR.setMatriculados(0);
        seccionPCUR.setRetirados(0);
        seccionPCUR.setReservados(0);

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
        this.actualizarBoletin();
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
    @Transactional
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
    @Transactional
    public void updateDocenteSecFechaInicio(DocenteSeccion profeSeccForm) {

        DocenteSeccion profeSeccDB = docenteSeccionDAO.find(profeSeccForm.getId());
        List<DocenteSeccion> profesSecc = docenteSeccionDAO.allBySeccion(profeSeccDB.getSeccion());

        if (profeSeccDB.getFechaFin() != null) {
            if (profeSeccForm.getFechaInicio().compareTo(profeSeccDB.getFechaFin()) >= 0) {
                throw new PhobosException("La fecha de inicio no puede ser mayor o igual a la fecha final");
            }
        }

        for (DocenteSeccion profeSec : profesSecc) {
            if (profeSec.getId() == profeSeccForm.getId().longValue()) {
                continue;
            }
            if (profeSec.getFechaInicio() == null && profeSec.getFechaFin() == null) {
                continue;
            }
            if (profeSec.getFechaInicio() != null && profeSec.getFechaFin() != null) {
                if (profeSeccForm.getFechaInicio().compareTo(profeSec.getFechaInicio()) >= 0 && profeSeccForm.getFechaInicio().compareTo(profeSec.getFechaFin()) <= 0) {
                    throw new PhobosException("La fecha seleccionada se encuentra dentro de un rango fijado");
                } else {
                    continue;
                }
            }
            if (profeSec.getFechaInicio() != null && profeSec.getFechaInicio().compareTo(profeSeccForm.getFechaInicio()) == 0) {
                throw new PhobosException("La fecha seleccionada es igual a otra fecha de inicio.");
            }
            if (profeSec.getFechaFin() != null && profeSec.getFechaFin().compareTo(profeSeccForm.getFechaInicio()) == 0) {
                throw new PhobosException("La fecha seleccionada es igual a una fecha fin.");
            }
        }
        profeSeccDB.setFechaInicio(profeSeccForm.getFechaInicio());
        docenteSeccionDAO.updateFechaInicio(profeSeccDB);
        evaluateSeccion(profeSeccDB.getSeccion());
        this.actualizarBoletin();
    }

    @Override
    @Transactional
    public void updateDocenteSecFechaFin(DocenteSeccion profeSeccForm) {

        DocenteSeccion profeSeccDB = docenteSeccionDAO.find(profeSeccForm.getId());
        List<DocenteSeccion> profesSecc = docenteSeccionDAO.allBySeccion(profeSeccDB.getSeccion());

        if (profeSeccDB.getFechaInicio() != null) {
            if (profeSeccDB.getFechaInicio().compareTo(profeSeccForm.getFechaFin()) >= 0) {
                throw new PhobosException("La fecha final no puede ser menor o igual a la fecha de inicio");
            }
        }

        for (DocenteSeccion profeSec : profesSecc) {
            if (profeSec.getId() == profeSeccForm.getId().longValue()) {
                continue;
            }
            if (profeSec.getFechaInicio() == null && profeSec.getFechaFin() == null) {
                continue;
            }
            if (profeSec.getFechaInicio() != null && profeSec.getFechaFin() != null) {
                if (profeSeccForm.getFechaFin().compareTo(profeSec.getFechaInicio()) >= 0 && profeSeccForm.getFechaFin().compareTo(profeSec.getFechaFin()) <= 0) {
                    throw new PhobosException("La fecha seleccionada se encuentra dentro de un rango fijado");
                } else if (profeSeccDB.getFechaInicio() != null) {
                    if (profeSec.getFechaFin().compareTo(profeSeccDB.getFechaInicio()) >= 0 && profeSec.getFechaFin().compareTo(profeSeccForm.getFechaFin()) <= 0) {
                        throw new PhobosException("La fecha seleccionada abarca un rango establecido.");
                    }
                } else {
                    continue;
                }
            }
            if (profeSec.getFechaInicio() != null && profeSec.getFechaInicio().compareTo(profeSeccForm.getFechaFin()) == 0) {
                throw new PhobosException("La fecha seleccionada es igual a una fecha de inicio.");
            }
            if (profeSec.getFechaFin() != null && profeSec.getFechaFin().compareTo(profeSeccForm.getFechaFin()) == 0) {
                throw new PhobosException("La fecha seleccionada es igual a otra fecha final.");
            }
        }
        profeSeccDB.setFechaFin(profeSeccForm.getFechaFin());
        docenteSeccionDAO.updateFechaFin(profeSeccDB);
        evaluateSeccion(profeSeccDB.getSeccion());

        this.actualizarBoletin();
    }

    @Override
    @Transactional
    public void evaluateSeccion(Seccion seccion) {
        List<DocenteSeccion> lstDocSec = docenteSeccionDAO.allBySeccion(seccion);

        BigDecimal total = BigDecimal.ZERO;
        boolean dateIsOk = true;
        for (DocenteSeccion docenteSeccion : lstDocSec) {
            logger.debug("DOCENTE SECCION ID {}", docenteSeccion.getId());
            if (docenteSeccion.getPorcentajeCarga() != null) {
                System.out.println(docenteSeccion.getPorcentajeCarga() + " yyyy");
                total = total.add(docenteSeccion.getPorcentajeCarga());
            }
            if (docenteSeccion.getFechaInicio() == null || docenteSeccion.getFechaFin() == null) {
                dateIsOk = false;
            }
        }
        BigDecimal cien = new BigDecimal(100L);
        if (dateIsOk && total.compareTo(cien) == 0) {
            fixEncuesta(lstDocSec);
            logger.debug("YA SE PUEDE ABRIR LA ENCUESTA");
        } else {
            logger.debug("NADA AUN ? ");
        }
    }

    private void fixEncuesta(List<DocenteSeccion> lstDocSec) {
        if (lstDocSec.size() == 1) {
            EncuestaDocente encuesta = encuestaDocenteDAO.findByDocenteSeccion(lstDocSec.get(0));
            List<PeriodoEncuesta> periodosEncuesta = periodoEncuestaDAO.allByEncuesta(encuesta.getEncuestaEstudiantil());
            encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
            encuesta.setFechaEncuestaInicio(periodosEncuesta.get(0).getFechaInicio());
            encuesta.setFechaEncuestaFin(periodosEncuesta.get(0).getFechaFin());
            encuestaDocenteDAO.update(encuesta);
        } else {
            for (DocenteSeccion docenteSeccion : lstDocSec) {
                EncuestaDocente encuesta = encuestaDocenteDAO.findByDocenteSeccion(docenteSeccion);
                ConfiguraEncuesta configuraEncuesta = configuraEncuestaDAO.findByEncuesta(encuesta.getEncuestaEstudiantil());
                Date inicioEncuesta = new DateTime(docenteSeccion.getFechaFin()).minusDays(configuraEncuesta.getDiasEncuesta().intValue()).toDate();
                encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
                encuesta.setFechaEncuestaInicio(inicioEncuesta);
                encuesta.setFechaEncuestaFin(docenteSeccion.getFechaFin());
                encuestaDocenteDAO.update(encuesta);
            }
        }
    }

    @Override
    @Transactional
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
    @Transactional
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
    @Transactional
    public void bloquearSeccion(Seccion seccion, Usuario usuario) {
        seccion = seccionDAO.find(seccion.getId());
        DateTime today = new DateTime();
        seccion.setUsuarioModificacion(usuario);
        seccion.setFechaModificacion(today.toDate());
        seccion.setEstadoEnum(SeccionEstadoEnum.BLO);
        seccionDAO.updateEstadoFechaModUsuarioMod(seccion);

        this.actualizarVacantesTCUR(seccion.getGrupoSeccion(), usuario, today);
        this.actualizarBoletin();
    }

    @Override
    @Transactional
    public void anularSeccion(Seccion seccion, Usuario usuario) {
        DateTime today = new DateTime();
        seccion = seccionDAO.find(seccion.getId());
        GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();

        List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);
        if (matriculasSeccion.isEmpty()) {
            List<DocenteSeccion> docentesSec = docenteSeccionDAO.allBySeccion(seccion);
            for (DocenteSeccion docenteSeccion : docentesSec) {
                docenteSeccionDAO.delete(docenteSeccion);
            }
            List<VacanteAlumno> vacantesAlumnos = vacanteAlumnoDAO.allBySeccion(seccion);
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
        this.actualizarBoletin();
    }

    @Override
    @Transactional
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
    public List<Seccion> allSeccionesByGrupo(GrupoSeccion grupoSeccion, List<DocenteSeccion> docentesSeccion) {
        List<Seccion> secciones = seccionDAO.allByGposSeccion(grupoSeccion);
        List<RestriccionCarrera> restriccionesCarrera = restriccionCarreraDAO.allActivasBySecciones(secciones);
        List<RestriccionFacultad> restriccionesFacultad = restriccionFacultadDAO.allActivasBySecciones(secciones);
        List<RestriccionModalidad> restriccionesModalidad = restriccionModalidadDAO.allActivasBySecciones(secciones);
        List<RestriccionRepitencia> restriccionRepitencia = restriccionRepitenciaDAO.allActivasBySecciones(secciones);

        Map<Long, List<RestriccionCarrera>> mapRestrCarr = TypesUtil.convertListToMapList("seccion.id", restriccionesCarrera);
        Map<Long, List<RestriccionFacultad>> mapRestrFacu = TypesUtil.convertListToMapList("seccion.id", restriccionesFacultad);
        Map<Long, List<RestriccionModalidad>> mapRestrModa = TypesUtil.convertListToMapList("seccion.id", restriccionesModalidad);
        Map<Long, List<RestriccionRepitencia>> mapRestrRepi = TypesUtil.convertListToMapList("seccion.id", restriccionRepitencia);
        Map<Long, List<DocenteSeccion>> mapProfeSecc = TypesUtil.convertListToMapList("seccion.id", docentesSeccion);

        for (Seccion seccion : secciones) {
            restriccionesCarrera = createList(mapRestrCarr.get(seccion.getId()));
            restriccionesFacultad = createList(mapRestrFacu.get(seccion.getId()));
            restriccionesModalidad = createList(mapRestrModa.get(seccion.getId()));
            restriccionRepitencia = createList(mapRestrRepi.get(seccion.getId()));
            List<DocenteSeccion> docentesSecc = createList(mapProfeSecc.get(seccion.getId()));

            seccion.setRestriccionesCarrera(restriccionesCarrera);
            seccion.setRestriccionesFacultad(restriccionesFacultad);
            seccion.setRestriccionesModalidad(restriccionesModalidad);
            seccion.setRestriccionesRepitencia(restriccionRepitencia);
            seccion.setDocenteSeccion(docentesSecc);
        }
        return secciones;
    }

    private List createList(List lista) {
        if (lista == null) {
            return new ArrayList();
        }
        return lista;
    }

    @Override
    public List<Docente> allDocenterByNombre(String nombre) {
        return docenteDAO.allByNombreFilter(nombre, 10);
    }

    @Override
    public List<Aula> searchAulaByName(String nombre, Long seccionId, CicloAcademico ciclo) {
        Seccion seccion = seccionDAO.find(seccionId);
        List<String> diaHoras = new ArrayList();
        List<HorarioSeccion> horarioSeccion = horarioSeccionDAO.allBySeccion(seccion);
        for (HorarioSeccion hdiaSecc : horarioSeccion) {
            diaHoras.add(hdiaSecc.getHoraDia());
        }

        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        List<Aula> aulas = aulaDAO.searchByNombreFilter(nombre, 15);

        List<HorarioAula> horariosAula = horarioAulaDAO.allByAulasCicloDiasHoras(aulas, ciclo, diaHoras);

        for (Aula aulaEach : aulas) {
            aulaEach.setDisponible(Boolean.TRUE);
            if (horariosAula.isEmpty()) {
                continue;
            }

            HorarioAula horarioAulaFound = horariosAula.stream().filter(req -> req.getAula().getId().equals(aulaEach.getId())).findFirst().orElse(null);
            if (horarioAulaFound != null) {
                aulaEach.setDisponible(false);
            }
        }

        return aulas;
    }

    @Override
    @Transactional
    public void cambiarDocentePrincipal(DocenteSeccion docenteSeccion) {
        docenteSeccion = docenteSeccionDAO.find(docenteSeccion.getId());
        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allBySeccion(docenteSeccion.getSeccion());
        for (DocenteSeccion docenteSeccionEach : docentesSeccion) {
            docenteSeccionEach.setPrincipal(BigDecimal.ZERO.intValue());
            docenteSeccionDAO.updatePrincipal(docenteSeccionEach);
        }
        docenteSeccion.setPrincipal(BigDecimal.ONE.intValue());
        docenteSeccionDAO.updatePrincipal(docenteSeccion);
        this.actualizarBoletin();
    }

    @Override
    @Transactional
    public void actualizarDocente(Long docenteSeccionId, Long docenteId) {
        DocenteSeccion docenteSeccion = new DocenteSeccion(docenteSeccionId);
        docenteSeccion.setDocente(new Docente(docenteId));
        docenteSeccionDAO.updateDocente(docenteSeccion);
    }

    @Override
    @Transactional
    public void actualizarSeccionResctriccionCapa(Seccion seccionForm, Usuario usuario) {
        seccionDAO.updateRestriccionCapa(seccionForm);
    }

    @Override
    @Transactional
    public void actualizarSeccionVacantes(Seccion seccionForm, Usuario usuario) {
        DateTime today = new DateTime();
        Seccion seccioDB = seccionDAO.find(seccionForm.getId());
        GrupoSeccion grupoSeccion = grupoSeccionDAO.findLock(seccioDB.getGrupoSeccion().getId());

        logger.debug("grupoSeccion:::  {}", grupoSeccion.getId());
        //validar seccion seleccionada
        if (ObjectUtil.getParentTree(seccioDB, "aula.id") != null) {
            if (seccioDB.getAula().getCapacidadAula().compareTo(seccionForm.getVacantes()) < 0) {
                throw new PhobosException(String.format("Las vacantes de la sección %s superan, el aforo su aula", seccionForm.getCodigo2()));
            }
        }
        List<MatriculaSeccion> matriculasSeccionSelect = matriculaSeccionDAO.allMatriculadosBySeccion(seccioDB);
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
        this.actualizarBoletin();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void actualizarVacantesTCUR(GrupoSeccion grupoSeccion, Usuario usuario, DateTime today) {
        if (grupoSeccion.getCurso().isTipoCursoTEOPRA()) {
            List<Seccion> secciones = seccionDAO.allOperativesByGpoSeccion(grupoSeccion);

            Seccion seccionTCUR = new Seccion();
            Integer vacantes = BigDecimal.ZERO.intValue();

            for (Seccion seccionEach : secciones) {
                if (seccionEach.isTipoSeccionTCUR()) {
                    seccionTCUR = seccionEach;
                }
                logger.debug("seccionEach.isTipoSeccionPCUR()::: {}", seccionEach.isTipoSeccionPCUR());
                logger.debug("seccionEach.getVacantes()::: {}", seccionEach.isTipoSeccionPCUR());
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
            List<MatriculaSeccion> matriculasSeccionTCUR = new ArrayList();
            List<VacanteAlumno> vacantesAlumnoBySeccion = new ArrayList();

            if (seccionTCUR.getId() != null) {
                matriculasSeccionTCUR = matriculaSeccionDAO.allMatriculadosBySeccion(seccionTCUR);
                vacantesAlumnoBySeccion = vacanteAlumnoDAO.allActivosBySeccion(seccionTCUR);
            }

            if (matriculasSeccionTCUR.size() > seccionTCUR.getVacantes()) {
                throw new PhobosException("Error. Las matriculas para la sección teoria superan la cantidad de vacantes asignadas.");
            }
            seccionDAO.updateSeccionVacantes(seccionTCUR);

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
    @Transactional
    public void updatePorcentajeAvance(DocenteSeccion profeSeccForm) {
        DocenteSeccion profeSeccBDMain = docenteSeccionDAO.find(profeSeccForm.getId());
        List<DocenteSeccion> profesSecc = docenteSeccionDAO.allBySeccion(profeSeccBDMain.getSeccion());

        BigDecimal total = BigDecimal.ZERO;
        for (DocenteSeccion profeSeccBD : profesSecc) {
            if (profeSeccBD.getId().longValue() == profeSeccForm.getId()) {
                continue;
            }
            if (profeSeccBD.getPorcentajeCarga() == null) {
                continue;
            }
            if (profeSeccBD.getEstadoEnum() != EstadoEnum.ACT) {
                continue;
            }
            total = total.add(profeSeccBD.getPorcentajeCarga());
        }

        total = total.add(profeSeccForm.getPorcentajeCarga());
        BigDecimal cien = new BigDecimal(100L);
        if (total.compareTo(cien) > 0) {
            throw new PhobosException("El porcentaje de carga no puede exceder el 100%");
        }

        profeSeccBDMain.setPorcentajeCarga(profeSeccForm.getPorcentajeCarga());
        docenteSeccionDAO.update(profeSeccBDMain);

        evaluateSeccion(profeSeccBDMain.getSeccion());
        this.actualizarBoletin();
    }

    @Override
    @Transactional
    public List<DocenteSeccion> analizedDocenteSeccion(GrupoSeccion grupoSeccion, CicloAcademico cicloAcademico) {
        List<Seccion> secciones = seccionDAO.allOperativesByGpoSeccion(grupoSeccion);
        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allActivosBySecciones(secciones);
        Map<Long, List<DocenteSeccion>> mapProfeSecc = TypesUtil.convertListToMapList("seccion.id", docentesSeccion);
        for (Seccion seccion : secciones) {
            docentesSeccion = createList(mapProfeSecc.get(seccion.getId()));
            this.analizedDocenteSeccion(seccion, docentesSeccion, cicloAcademico);
        }
        return docentesSeccion;
    }

    private void analizedDocenteSeccion(Seccion seccion, List<DocenteSeccion> docentesSeccion, CicloAcademico cicloAcademico) {
        Boolean errorPorcentajeCarga = Boolean.FALSE;
        Boolean errorPeriodoClases = Boolean.FALSE;
        DateTime fechaMinEvento = null;
        DateTime fechaMaxEvento = null;

        List<Date> fechas = this.allDatesEventoCicloAcademicoForPeriodo(cicloAcademico);
        if (!fechas.isEmpty()) {
            fechaMinEvento = new DateTime(new DateTime(fechas.get(0)).toLocalDate().toDate());
            fechaMaxEvento = new DateTime(new DateTime(fechas.get(fechas.size() - 1)).toLocalDate().toDate());
        }

//        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allActivosBySeccion(seccion);
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
        if (seccion.getHorasSemanales() == 0) {
            return new ArrayList();
        }

        List<GrupoHoras> grupoHoras = grupoHorasDAO.allByTipoGrupoHora(tipoGrupoHoras, cicloAcademico);
        List<DiaHoraGrupo> horariosTodosGrupos = diaHoraGrupoDAO.allByGruposCiclo(grupoHoras, cicloAcademico);
        Map<Long, List<DiaHoraGrupo>> mapHorariosByGpo = TypesUtil.convertListToMapList("grupoHorario.id", horariosTodosGrupos);

        for (GrupoHoras grupoHora : grupoHoras) {
            List<DiaHoraGrupo> horarioGpo = createList(mapHorariosByGpo.get(grupoHora.getId()));
            Collections.sort(horarioGpo, (p1, p2) -> p1.getHora().getNumero().compareTo(p2.getHora().getNumero()));
            grupoHora.setDiaHoraGrupo(horarioGpo);
        }

        List<GrupoHoras> grupoHorasFiltrado = new ArrayList();

        //buscando grupos con las horas requeridas por dia (filtramos los grupos horas)
        int loop = 0;
        long t1 = System.currentTimeMillis();
        for (GrupoHoras grupo : grupoHoras) {
            if (grupo.getDiaHoraGrupo().isEmpty()) {
                continue;
            }
//            System.out.println("Buscando en grupo " + grupo.getCodigo());

            Map<Long, Object> mapDias = TypesUtil.convertListToMapList("dia.id", grupo.getDiaHoraGrupo());
            loop++;
            if (existeCoincidencia(mapDias, seccion.getHorasSemanales())) {
                grupoHorasFiltrado.add(grupo);
            }
//            List<Map<Long, Object>> busquedas = Commutator.create(mapDias);

        }
        long t2 = System.currentTimeMillis();
        System.out.println("Busqueda de " + loop + " coincidencias en " + (t2 - t1) + " mseg");

        return grupoHorasFiltrado;
    }

    private boolean existeCoincidencia(Map<Long, Object> mapDias, int horasSemanales) {
        int minimo = 5000;
//        System.out.println("\tBuscando coincidencia de " + horasSemanales + " horas semanales");

        for (int i = 0; i < mapDias.size(); i++) {
            List<Map<Long, Object>> busquedas = new ArrayList();
            Map<Long, Object> mapTempo = new LinkedHashMap();
            Map<String, String> existentes = new LinkedHashMap();
            Commutator.findConmutation(mapDias, mapTempo, i + 1, busquedas, existentes);

            for (Map<Long, Object> busqueda : busquedas) {
                int total = 0;
                for (Map.Entry<Long, Object> entry : busqueda.entrySet()) {
                    List<DiaHoraGrupo> horasDia = (List<DiaHoraGrupo>) entry.getValue();
                    total += horasDia.size();
                }
                //System.out.println("\t>> con combinacion(" + (i + 1) + ") se hallo a " + total + " horas");
                if (total == horasSemanales) {
//                    System.out.println("\t>> coincidencia hallada");
                    return true;
                }
                if (total < minimo) {
                    minimo = total;
                }
            }
            if (minimo > horasSemanales) {
                break;
            }
        }
//        System.out.println("\tninguna coincidencia hallada");
        return false;
    }

    @Override
    public TipoGrupoHoras findTipoGrupoHoraByTipo(TipoGrupoHorasEnum tipoGrupoHorasEnum) {
        TipoGrupoHoras tipoGrupoHoraZeta = tipoGrupoHorasDAO.findByTipo(tipoGrupoHorasEnum);
        return tipoGrupoHoraZeta;
    }

    @Override
    public TipoGrupoHoras findTipoGpoByEnumCiclo(TipoGrupoHorasEnum tipoGrupoHorasEnum, CicloAcademico cicloAcademico) {
        TipoGrupoHoras tipoGrupoHoraZeta = tipoGrupoHorasDAO.findByTipoCiclo(tipoGrupoHorasEnum, cicloAcademico);
        return tipoGrupoHoraZeta;
    }

    @Override
    public List<TipoGrupoHoras> allGrupoHorasActivosTipoAndCiclo(CicloAcademico cicloAcademico, TipoGrupoHorasEnum tipoGrupoHorasEnum) {
        return tipoGrupoHorasDAO.allActiveByTipoCiclo(cicloAcademico, tipoGrupoHorasEnum);
    }

    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupoByGrupo(GrupoHoras grupoHoras, CicloAcademico cicloAcademico) {
        return diaHoraGrupoDAO.allByGrupoCiclo(grupoHoras, cicloAcademico);
    }

    @Override
    public List<DiaHoraGrupo> allDiaHoraGrupoByTipo(TipoGrupoHoras tipoGrupoHoras, CicloAcademico cicloAcademico) {
        return diaHoraGrupoDAO.allByTipoGpoCiclo(tipoGrupoHoras, cicloAcademico);
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
        return horaDAO.all();
    }

    @Override
    @Transactional
    public void saveSeccionGrupoHorario(Seccion seccion, GrupoHoras grupoHorario, CicloAcademico cicloAcademico) {

        if (grupoHorario != null && !grupoHorario.isPermiteCeroHoras()) {
            if (grupoHorario.getDiaHoraGrupo().isEmpty()) {
                throw new PhobosException("Debe seleccionar las horas");
            }
        }

        List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccion(seccion);
        List<HorarioAula> horariosAula = horarioAulaDAO.allBySeccionCiclo(seccion, cicloAcademico);
        if (grupoHorario == null) {
            horarioSeccionDAO.deleteAllInList(horariosSeccion);
            horarioAulaDAO.deleteAllInList(horariosAula);
            seccion.setGrupoHoras(grupoHorario);
            seccionDAO.update(seccion);
            return;
        }
        Map<Long, Hora> mapHoras = TypesUtil.convertListToMap("id", horaDAO.all());
        Map<Long, Dia> mapDias = TypesUtil.convertListToMap("id", diaDAO.all());

        for (DiaHoraGrupo horaDia : grupoHorario.getDiaHoraGrupo()) {
            horaDia.setHora(mapHoras.get(horaDia.getHora().getId()));
            horaDia.setDia(mapDias.get(horaDia.getDia().getId()));
        }

        Map<String, DiaHoraGrupo> mapHDiaGpo = TypesUtil.convertListToMap("horaDia", grupoHorario.getDiaHoraGrupo());
        if (seccion.getAula() != null) {
            List<HorarioAula> horarioTotalAula = horarioAulaDAO.allByAulaCiclo(seccion.getAula(), cicloAcademico);
            for (HorarioAula hdiaAula : horarioTotalAula) {
                Seccion secc = hdiaAula.getSeccion();
                if (secc.getId() == seccion.getId().longValue()) {
                    continue;
                }
                String horaDia = hdiaAula.getHoraDia();
                DiaHoraGrupo hdiaGpo = mapHDiaGpo.get(horaDia);
                if (hdiaGpo == null) {
                    continue;
                }
                Dia dia = hdiaGpo.getDia();
                Hora hora = hdiaGpo.getHora();
                throw new PhobosException("Hay cruce de horario el " + dia.getNombre() + " a la(s) " + hora.getDescripcion());
            }
        }

        if (seccion.getSeccionSuperior() != null) {
            List<HorarioSeccion> horarioTCUR = horarioSeccionDAO.allBySeccion(seccion.getSeccionSuperior());
            for (HorarioSeccion horarioSecc : horarioTCUR) {
                String horaDia = horarioSecc.getHoraDia();
                DiaHoraGrupo hdiaGpo = mapHDiaGpo.get(horaDia);
                if (hdiaGpo == null) {
                    continue;
                }
                Dia dia = hdiaGpo.getDia();
                Hora hora = hdiaGpo.getHora();
                throw new PhobosException("Hay cruce de horario con la teoria el " + dia.getNombre() + " a la(s) " + hora.getDescripcion());
            }
        }

        if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.TCUR) {
            List<Seccion> secciones = seccionDAO.allByGposSeccion(seccion.getGrupoSeccion());
            Seccion secTeoria = null;
            for (Seccion secc : secciones) {
                if (secc.getId() == seccion.getId().longValue()) {
                    secTeoria = secc;
                    break;
                }
            }

            secciones.remove(secTeoria);
            List<HorarioSeccion> horariosPCUR = horarioSeccionDAO.allBySecciones(secciones);
            for (HorarioSeccion horarioSecc : horariosPCUR) {
                String horaDia = horarioSecc.getHoraDia();
                Seccion seccPcur = horarioSecc.getSeccion();
                DiaHoraGrupo hdiaGpo = mapHDiaGpo.get(horaDia);
                if (hdiaGpo == null) {
                    continue;
                }
                Dia dia = hdiaGpo.getDia();
                Hora hora = hdiaGpo.getHora();
                throw new PhobosException("Hay cruce de horario con la práctica "
                        + seccPcur.getCodigo2() + " el "
                        + dia.getNombre() + " a la(s) " + hora.getDescripcion());
            }
        }

        //seccion.setHorarioSeccion(horariosSeccion);
        seccion.setGrupoHoras(grupoHorario);

        List<DiaHoraGrupo> horarioGpo = grupoHorario.getDiaHoraGrupo();
        ListsInspector inspector = TypesUtil.analizeLists(horariosSeccion, horarioGpo, "horaDia");
        List<HorarioSeccion> muertosHSecc = inspector.getDeadList();
        List<DiaHoraGrupo> nuevosHSecc = inspector.getNewList();

        if (!muertosHSecc.isEmpty()) {
            horarioSeccionDAO.deleteAllInList(muertosHSecc);
        }
        for (DiaHoraGrupo diaHoraGrupoEach : nuevosHSecc) {
            HorarioSeccion horarioSeccion = new HorarioSeccion();
            horarioSeccion.setDia(diaHoraGrupoEach.getDia());
            horarioSeccion.setHora(diaHoraGrupoEach.getHora());
            horarioSeccion.setSeccion(seccion);
            horarioSeccion.setAula(seccion.getAula());
            horarioSeccionDAO.save(horarioSeccion);
        }

        inspector = TypesUtil.analizeLists(horariosAula, horarioGpo, "horaDia");
        List<HorarioAula> muertosHAula = inspector.getDeadList();
        List<DiaHoraGrupo> nuevosHAula = inspector.getNewList();

        if (!muertosHSecc.isEmpty()) {
            horarioAulaDAO.deleteAllInList(muertosHAula);
        }

        if (seccion.getAula() != null) {
            for (DiaHoraGrupo diaHoraGrupoEach : nuevosHAula) {
                HorarioAula horarioAula = new HorarioAula();
                horarioAula.setAula(seccion.getAula());
                horarioAula.setDia(diaHoraGrupoEach.getDia());
                horarioAula.setHora(diaHoraGrupoEach.getHora());
                horarioAula.setSeccion(seccion);
                horarioAulaDAO.save(horarioAula);
            }
        }

        seccionDAO.updateSeccionGrupoHora(seccion);
        this.actualizarBoletin();
    }

    @Override
    @Transactional
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
        this.actualizarBoletin();
    }

    @Override
    @Transactional
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
        this.actualizarBoletin();
    }

    @Override
    @Transactional
    public void saveTipoRepitenciaRestriccion(Seccion seccion, List<TipoRepitencia> tiposRestriccionesSeleccionados, DataSessionPivot ds) {
        DateTime today = new DateTime();

        List<RestriccionRepitencia> restriccionesRepitencia = restriccionRepitenciaDAO.allActivasBySeccion(seccion);

        //desactivamos los que ya no estan seleccionados
        for (RestriccionRepitencia restriccionRepEach : restriccionesRepitencia) {
            if (!tiposRestriccionesSeleccionados.contains(restriccionRepEach.getTipoRepitencia())) {
                restriccionRepEach.setEstadoEnum(EstadoEnum.INA);
                restriccionRepEach.setFechaModificacion(today.toDate());
                restriccionRepEach.setUsuarioModificacion(ds.getUsuario());
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
                restriccionRepitencia.setUsuarioRegistro(ds.getUsuario());
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
        List<String> diaHoras = new ArrayList();
        List<HorarioSeccion> horarioSeccion = horarioSeccionDAO.allBySeccion(seccion);
        for (HorarioSeccion hdiaSecc : horarioSeccion) {
            diaHoras.add(hdiaSecc.getHoraDia());
        }

        List<HorarioAula> horariosAula = new ArrayList();
        if (!diaHoras.isEmpty()) {
            horariosAula = horarioAulaDAO.allByPabellonCicloDiasHoras(pabellon, cicloAcademico, diaHoras);
        }

        Aula aulaActual = seccion.getAula();
        List<Aula> aulas = aulaDAO.allByPabellon(pabellon);
        for (Aula aulaEach : aulas) {
            if (horariosAula.isEmpty()) {
                aulaEach.setDisponible(Boolean.TRUE);
                continue;
            }
            if (aulaActual != null && aulaActual.getId() == aulaEach.getId().longValue()) {
                aulaEach.setDisponible(Boolean.TRUE);
                continue;
            }
            HorarioAula horarioAulaFound = horariosAula.stream().filter(req -> req.getAula().getId().equals(aulaEach.getId())).findFirst().orElse(null);
            if (horarioAulaFound != null) {
                aulaEach.setDisponible(Boolean.FALSE);
                if (aulaEach.getSeccion() == null) {
                    aulaEach.setSeccion(new ArrayList());
                }
                aulaEach.getSeccion().add(horarioAulaFound.getSeccion());
            } else {
                aulaEach.setDisponible(Boolean.TRUE);
            }
        }

        return aulas;
    }

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
    public GrupoHoras findGrupoHorasWithHorario(Seccion seccion, CicloAcademico ciclo) {
        GrupoHoras grupoHoras = seccion.getGrupoHoras();
        Map<String, HorarioSeccion> mapHorarioSecc = TypesUtil.convertListToMap("horaDia", horarioSeccionDAO.allBySeccion(seccion));

        GrupoHoras gpoBD = grupoHorasDAO.find(grupoHoras);
        List<DiaHoraGrupo> horarioGpo = new ArrayList();
        List<DiaHoraGrupo> diaHoraGpo = diaHoraGrupoDAO.allByGrupoCiclo(gpoBD, ciclo);
        for (DiaHoraGrupo horaDiaGpo : diaHoraGpo) {
            String horaDia = horaDiaGpo.getHoraDia();
            HorarioSeccion hdiaSecc = mapHorarioSecc.get(horaDia);
            if (hdiaSecc != null) {
                horarioGpo.add(horaDiaGpo);
            }
        }
        gpoBD.setDiaHoraGrupo(horarioGpo);
        return gpoBD;
    }

    @Override
    public GrupoHoras findGrupoHoras(GrupoHoras grupoHoras, CicloAcademico ciclo) {
        return grupoHorasDAO.find(grupoHoras);
    }

    @Override
    public GrupoHoras findGrupoHorasFull(GrupoHoras grupoHoras, CicloAcademico cicloAcademico) {
        grupoHoras = grupoHorasDAO.find(grupoHoras);
        List<DiaHoraGrupo> diasHorasGrupo = diaHoraGrupoDAO.allByGrupoCiclo(grupoHoras, cicloAcademico);
        grupoHoras.setDiaHoraGrupo(diasHorasGrupo);
        return grupoHoras;
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
        return modalidadEstudioDAO.allRegularesActivas();
    }

    @Override
    public List<Carrera> allCarrerasActivas() {
        return carreraDAO.allActivas();
    }

    @Override
    public List<Carrera> allCarrerasActivasPrePost() {
        List<String> modalidades = new ArrayList<>();
        modalidades.add(ModalidadEstudioEnum.PRE.name());
        modalidades.add(ModalidadEstudioEnum.EPG.name());

        return carreraDAO.allActivasByModalidades(modalidades);
    }

    @Override
    public List<TipoRepitencia> allTipoRepitencia() {
        return tipoRepitenciaDAO.all();
    }

    @Override
    public List<EventoCicloAcademico> allEventoCicloAcademicoForPeriodo(CicloAcademico cicloAcademico) {
        List<EventoAcademicoEnum> eventos = Arrays.asList(EventoAcademicoEnum.CLASES_PRE, EventoAcademicoEnum.CLASES_PRE);
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
        if (StringUtils.isEmpty(code)) {
            return null;
        }

        GrupoHoras grupoHorario = grupoHorasDAO.findByCodeTipoCiclo(code, cicloAcademico.getTipoEnum());
        if (grupoHorario == null) {
            throw new PhobosException("Grupo Horario ingresado no existe");
        }

        List<Dia> dias = diaDAO.all();
        List<DiaHoraGrupo> diasHorasGpo = diaHoraGrupoDAO.allByGrupoCiclo(grupoHorario, cicloAcademico);
        if (seccion.getHorasSemanales() == 0) {
            throw new PhobosException("Esta sección no puede asignarse un grupo con horas semanales");
        }
        List<DiaHoraGrupo> diasGrupoSecc = searchDiasHorasByHorasSemanales(diasHorasGpo, seccion.getHorasSemanales(), dias);
        grupoHorario.setDiaHoraGrupo(diasGrupoSecc);

        return grupoHorario;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<DiaHoraGrupo> searchDiasHorasByHorasSemanales(List<DiaHoraGrupo> diasHorasGrupo, Integer horasSemanales, List<Dia> dias) {
        if (horasSemanales == 0) {
            throw new PhobosException("Esta sección no puede asignarse un grupo con horas semanales");
        }

        if (diasHorasGrupo.isEmpty()) {
            return new ArrayList();
        }

        Collections.sort(diasHorasGrupo, (p1, p2) -> p1.getHora().getNumero().compareTo(p2.getHora().getNumero()));

        List<DiaHoraGrupo> diasHorasTempo;
        Map<Long, Object> mapDias = new LinkedHashMap();
        for (Dia dia : dias) {
            diasHorasTempo = new ArrayList();

            for (DiaHoraGrupo diaHora : diasHorasGrupo) {
                if (diaHora.getDia().getId() == dia.getId().longValue()) {
                    diasHorasTempo.add(diaHora);
                }
            }
            if (!diasHorasTempo.isEmpty()) {
                mapDias.put(dia.getId(), diasHorasTempo);
            }
        }

        List<DiaHoraGrupo> diasHorasSeccion = new ArrayList();
        List<Map<Long, Object>> busquedas = Commutator.create(mapDias);
        for (Map<Long, Object> busqueda : busquedas) {
            int total = 0;
            diasHorasSeccion.clear();
            for (Map.Entry<Long, Object> entry : busqueda.entrySet()) {
                List<DiaHoraGrupo> horasDia = (List<DiaHoraGrupo>) entry.getValue();
                diasHorasSeccion.addAll(horasDia);
                total += horasDia.size();
            }
            if (total == horasSemanales) {
                break;
            }
        }

        if (diasHorasSeccion.isEmpty()) {
            throw new PhobosException("Grupo Horario no es compatible con las horas semanales de la sección");
        }
        return diasHorasSeccion;
    }

    @Override
    public List<HorarioSeccion> allHorarioSeccion(Seccion seccion) {
        return horarioSeccionDAO.allBySeccion(seccion);
    }

    private int cociente(int acumulador, int dvdo, int dvsor) {
        if (dvdo > dvsor) {
            return cociente(acumulador + 1, dvdo - dvsor, dvsor);
        } else {
            return acumulador;
        }
    }

    public List<String> getCodes(int inicio, int cantidad) {
        List<String> codes = new ArrayList();
        for (int i = inicio; i < cantidad + inicio; i++) {
            codes.add(getCode(i));
        }
        return codes;
    }

    private String getCode(int i) {
        if (i < 1000) {
            return NumberFormat.codigo(i, 3, '0');
        }
        int r = (i) % 100;
        int q = cociente(0, (i - 999), 100);
        String le = Character.toString((char) (65 + q));
        if (i > 1990 && i < 2010) {

            logger.debug("i = {} {}, r {}, q {} ", i, le, r, q);
        }

        return le + NumberFormat.codigo(r, 2, '0');
    }

    @Override
    @Transactional
    public List<GrupoSeccion> clonar(GrupoSeccion grupoSeccion, Integer veces, DataSessionPivot ds) {
        GrupoSeccion gsBD = grupoSeccionDAO.find(grupoSeccion.getId());
        Curso curso = gsBD.getCurso();
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        List<String> codigosByCiclo = grupoSeccionDAO.allCodigoByCiclo(cicloAcademico);
        List<String> codigos2ByCiclo = grupoSeccionDAO.allCodigo2ByCiclo(cicloAcademico);

        List<GrupoSeccion> clones = new ArrayList<>();
        for (int i = 0; i < veces; i++) {
            GrupoSeccion clon = new GrupoSeccion();

            String codigo = CodeGenerator.getNextCode(codigosByCiclo, 0);
            String codigo2 = CodeGenerator.getNextCode(codigos2ByCiclo, 0);

            codigosByCiclo.add(codigo);
            codigos2ByCiclo.add(codigo2);

            clon.setCurso(curso);
            clon.setCodigo(codigo);
            clon.setCodigo2(codigo2);
            clon.setVersion(BigDecimal.ONE.toString());
            clon.setEstadoGrupoEnum(EstadoGrupoSeccionEnum.ABI);
            clon.setEstadoPlanEnum(EstadoPlanCalificaEnum.PEND);
            clon.setCicloAcademico(cicloAcademico);
            clon.setEstadoEnum(gsBD.getEstadoEnum());

            clon.setAnexoBoletin(gsBD.getAnexoBoletin());

            Integer horasTeoria = curso.getHorasTeoria() == null ? 0 : curso.getHorasTeoria();
            Integer horasPractica = curso.getHorasPractica() == null ? 0 : curso.getHorasPractica();

            clon.setHorasPractica(horasPractica);
            clon.setHorasTeoria(horasTeoria);

            //  grupoSeccion.se
            Docente docenteDefault = docenteDAO.findByCode(Constantine.DOCENTE_INDETERMINADO);

            DateTime today = new DateTime();
            final BigDecimal PORCENTAJE_CARGA = new BigDecimal(100);

            clon.setSecciones(new ArrayList<Seccion>());
            if (curso.isTipoCursoTEO()) {
                Seccion seccionTEO = new Seccion();
                seccionTEO.setGrupoSeccion(clon);
                seccionTEO.setCodigo(clon.getCodigo() + "0");
                seccionTEO.setCodigo2(seccionTEO.getCodigo());
                seccionTEO.setEstadoEnum(SeccionEstadoEnum.CRE);
                seccionTEO.setTipoSeccionEnum(TipoSeccionEnum.TEO);
                seccionTEO.setSituacionDocenteEnum(SituacionDocenteEnum.ERR);
                seccionTEO.setPrematriculados(0);
                seccionTEO.setReservados(0);
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

                clon.getSecciones().add(seccionTEO);
            }
            if (curso.isTipoCursoPRA()) {
                Seccion seccionPRA = new Seccion();
                seccionPRA.setGrupoSeccion(clon);
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
                seccionPRA.setPrematriculados(0);
                seccionPRA.setReservados(0);

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

                clon.getSecciones().add(seccionPRA);
            }
            if (curso.isTipoCursoTEOPRA()) {
                Seccion seccionTCUR = new Seccion();
                seccionTCUR.setGrupoSeccion(clon);
                seccionTCUR.setCodigo(codigo + "0");
                seccionTCUR.setCodigo2(seccionTCUR.getCodigo());
                seccionTCUR.setEstadoEnum(SeccionEstadoEnum.CRE);
                seccionTCUR.setTipoSeccionEnum(TipoSeccionEnum.TCUR);
                seccionTCUR.setSituacionDocenteEnum(SituacionDocenteEnum.ERR);
                //   seccionTCUR.setHorasPractica(curso.getHorasPractica());
                //   seccionTCUR.setHorasTeoria(curso.getHorasTeoria());
                seccionTCUR.setHorasSemanales(horasTeoria);
                seccionTCUR.setPrematriculados(0);
                seccionTCUR.setReservados(0);

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

                clon.getSecciones().add(seccionTCUR);

                Seccion seccionPCUR = new Seccion();
                seccionPCUR.setGrupoSeccion(clon);
                seccionPCUR.setSeccionSuperior(seccionTCUR);
                seccionPCUR.setCodigo(codigo + "1");
                seccionPCUR.setCodigo2(seccionPCUR.getCodigo());
                seccionPCUR.setEstadoEnum(SeccionEstadoEnum.CRE);
                seccionPCUR.setTipoSeccionEnum(TipoSeccionEnum.PCUR);
                seccionPCUR.setSituacionDocenteEnum(SituacionDocenteEnum.ERR);
                //  seccionPCUR.setHorasPractica(curso.getHorasPractica());
                //   seccionPCUR.setHorasTeoria(curso.getHorasTeoria());
                seccionPCUR.setHorasSemanales(horasPractica);
                seccionPCUR.setPrematriculados(0);
                seccionPCUR.setReservados(0);

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

                clon.getSecciones().add(seccionPCUR);
            }
            grupoSeccionDAO.save(clon);

            clones.add(clon);

        }
        return clones;
    }

    @Override
    public List<GrupoSeccion> allCleanByDynatableGruposSeccion(DynatableFilter filter, CicloAcademico ciclo, List<GrupoSeccion> gpos) {
        return grupoSeccionDAO.allByDynatableGruposSeccion(filter, ciclo, gpos);
    }

    @Override
    public Long contarGpoSecc(CicloAcademico ciclo) {
        return grupoSeccionDAO.contarByCiclo(ciclo);
    }

    @Override
    @Transactional(readOnly = false)
    public void actualizarBoletin() {
        CicloAcademico cicloActivo = cicloAcademicoDAO.findCicloAcademicoActivoPRE();
        CicloAcademico cicloUpd = new CicloAcademico();
        cicloUpd.setId(cicloActivo.getId());
        cicloUpd.setActualizarBoletin(Boolean.TRUE);
        cicloAcademicoDAO.updateActualizarBoletin(cicloUpd);
    }

    private List getList(List lista) {
        if (lista == null) {
            return new ArrayList();
        }
        return lista;
    }
}
