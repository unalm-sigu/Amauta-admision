package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion;

import com.google.common.base.Strings;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
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
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.CodeGenerator;
import pe.albatross.zelpers.miscelanea.Commutator;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.math.Fraxtion;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.model.academico.AmpliacionVacantes;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CambioAulaGrupo;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CuotasGrupoHoras;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.DescuentoSeccionVerano;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Evaluacion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.PrecioCursoEstructura;
import pe.edu.lamolina.model.academico.RestriccionCarrera;
import pe.edu.lamolina.model.academico.RestriccionFacultad;
import pe.edu.lamolina.model.academico.RestriccionModalidad;
import pe.edu.lamolina.model.academico.RestriccionRepitencia;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.academico.TipoRepitencia;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.PeriodoEncuesta;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.EstadoHorarioAulaEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.model.enums.EstadoVacanteAlumnoEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.CLASES_EPG;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.CLASES_PRE;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.CLASES_VER;
import pe.edu.lamolina.model.enums.GrupoAnexoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.NombreTablasEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.SituacionDocenteEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoCreditoEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import pe.edu.lamolina.model.enums.TipoDeudaEnum;
import pe.edu.lamolina.model.enums.TipoDictadoGrupoSeccionEnum;
import static pe.edu.lamolina.model.enums.TipoDictadoGrupoSeccionEnum.MOD;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.enums.TipoHorarioAulaEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.AlumnoPagoVerano;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;
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
import pe.edu.lamolina.model.rrhh.ContratoDocente;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;
import pe.edu.lamolina.pivot.controller.responserest.ResponseRestService;
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoEvaluacionDAO;
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
import pe.edu.lamolina.pivot.dao.academico.CambioAulaGrupoDAO;
import pe.edu.lamolina.pivot.dao.academico.CuotaGpoHorasDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DescuentoSeccionVeranoDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionExpandidaDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.PrecioCursoEstructuraDAO;
import pe.edu.lamolina.pivot.dao.finanza.AcreenciaDAO;
import pe.edu.lamolina.pivot.dao.finanza.AlumnoPagoVeranoDAO;
import pe.edu.lamolina.pivot.dao.finanza.DeudaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.finanza.PagoHoraDocenteDAO;
import pe.edu.lamolina.pivot.dao.rrhh.ContratoDocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoCursoCurriculaDAO;

import static pe.edu.lamolina.pivot.zelper.constant.Constantine.GRUPO_ZPRA;
import static pe.edu.lamolina.pivot.zelper.constant.Constantine.GRUPO_ZTEO;

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
    AlumnoEvaluacionDAO alumnoEvaluacionDAO;

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
    CambioAulaGrupoDAO cambioAulaGrupoDAO;

    @Autowired
    PrecioCursoEstructuraDAO precioCursoEstructuraDAO;

    @Autowired
    PagoHoraDocenteDAO pagoHoraDocenteDAO;

    @Autowired
    CuotaGpoHorasDAO cuotaGpoHorasDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    ContratoDocenteDAO contratoDocenteDAO;

    @Autowired
    EvaluacionDAO evaluacionDAO;

    @Autowired
    EvaluacionSeccionDAO evaluacionSeccionDAO;

    @Autowired
    EvaluacionExpandidaDAO evaluacionExpandidaDAO;

    @Autowired
    DescuentoSeccionVeranoDAO descuentoSeccionVeranoDAO;

    @Autowired
    DeudaAlumnoDAO deudaAlumnoDAO;

    @Autowired
    AlumnoPagoVeranoDAO alumnoPagoVeranoDAO;

    @Autowired
    AcreenciaDAO acreenciaDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    CursoCurriculaDAO cursoCurriculaDAO;

    @Autowired
    TipoCursoCurriculaDAO tipoCursoCurriculaDAO;

    @Autowired
    ResponseRestService responseRestService;
    @Autowired
    VerificadorService verificadorService;

    final String PORCENTAJE_CARGA_FRACCION = "100";
    final BigDecimal PORCENTAJE_CARGA = new BigDecimal(100);

    @Override
    public CicloAcademico findCicloPregrado(CicloAcademico cicloAcademico) {
        return cicloAcademicoDAO.find(cicloAcademico, ModalidadEstudioEnum.PRE);
    }

    @Override
    public CicloAcademico findCicloPosgrado(CicloAcademico cicloAcademico) {
        return cicloAcademicoDAO.find(cicloAcademico, ModalidadEstudioEnum.EPG);
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
    public GrupoSeccion findGpoSeccion(Long id, DataSessionPivot ds) {
        GrupoSeccion gpoSecc = grupoSeccionDAO.find(id);

        List<Seccion> secciones = seccionDAO.allByGposSeccion(gpoSecc);
        List<CuotasGrupoHoras> allCountCuotasGrupoHorases = cuotaGpoHorasDAO.allByAnexoCiclo(gpoSecc.getAnexoBoletin(), gpoSecc.getCicloAcademico());

        for (Seccion secc : secciones) {
            if (secc.getGrupoHoras() != null) {
                secc.setGrupoHoras(secc.getGrupoHoras().clone());
            }
        }

        // Cuotas para grupos con aulas OERA
        secciones.stream()
                .filter(z -> z.getAula() != null)
                .filter(z -> z.getGrupoHoras() != null)
                .filter(z -> z.getAula().getOficinaSupervisora() != null)
                .filter(z -> z.getAula().getOficinaSupervisora().getCodigoEnum() == OficinaEnum.OERA)
                .forEach(x -> {
                    CuotasGrupoHoras cuotasGrupoHoras = allCountCuotasGrupoHorases.stream()
                            .filter(y -> y.getGrupoHoras().getCodigo().equals(x.getGrupoHoras().getLetra()))
                            .findFirst().orElse(null);
                    if (cuotasGrupoHoras != null) {
                        x.getGrupoHoras().setCuotasGrupoHoras(cuotasGrupoHoras);
                    }
                });

        // Cuotas para grupos sin aula
        secciones.stream()
                .filter(z -> z.getAula() == null)
                .filter(z -> z.getGrupoHoras() != null)
                .forEach(x -> {
                    CuotasGrupoHoras cuotasGrupoHoras = allCountCuotasGrupoHorases.stream()
                            .filter(y -> y.getGrupoHoras().getCodigo().equals(x.getGrupoHoras().getLetra()))
                            .findFirst().orElse(null);
                    if (cuotasGrupoHoras != null) {
                        x.getGrupoHoras().setCuotasGrupoHoras(cuotasGrupoHoras);
                    }
                });
        gpoSecc.setSecciones(secciones);

        CicloAcademico ciclo = gpoSecc.getCicloAcademico();
        Curso curso = gpoSecc.getCurso();
        String tpc = gpoSecc.getCurso().getTpc();
        if (ciclo.getTipoEnum() == TipoCicloEnum.NIV) {
            CursoCicloAcademico cursoCiclo = cursoCicloAcademicoDAO.findByCursoCiclo(curso, ciclo);

            PrecioCursoEstructura precioCurso = precioCursoEstructuraDAO.findByTpcCiclo(tpc, ciclo);

            if (cursoCiclo != null) {
                curso.setPrecio(cursoCiclo.getPrecio().add(cursoCiclo.getPrecioAdicional()));
            } else {
                curso.setPrecio(BigDecimal.ZERO);
            }
            if (precioCurso != null) {
                curso.setPrecioTpc(precioCurso.getPrecio());
            } else {
                curso.setPrecioTpc(BigDecimal.ZERO);
            }
        }

        List<DocenteSeccion> docenteSeccion = docenteSeccionDAO.allBySecciones(secciones);
        List<Long> idsDoc = docenteSeccion.stream().map(x -> x.getDocente().getId()).collect(Collectors.toList());
        List<ContratoDocente> contratos = contratoDocenteDAO.allByDocente(idsDoc);
        Map<Long, ContratoDocente> mapDocente = TypesUtil.convertListToMap("docente.id", contratos);
        for (DocenteSeccion docSeccion : docenteSeccion) {
            ContratoDocente contrDoc = mapDocente.get(docSeccion.getDocente().getId());
            docSeccion.setContratoDocente(contrDoc);
        }
        Map<Long, List<DocenteSeccion>> mapDocSeccion = TypesUtil.convertListToMapList("seccion.id", docenteSeccion);
        List<RestriccionModalidad> restriccionesMod = restriccionModalidadDAO.allActivasBySecciones(secciones);
        List<RestriccionFacultad> restriccionesFac = restriccionFacultadDAO.allActivasBySecciones(secciones);
        List<RestriccionCarrera> restriccionesCarr = restriccionCarreraDAO.allActivasBySecciones(secciones);
        List<RestriccionRepitencia> restriccionesRep = restriccionRepitenciaDAO.allActivasBySecciones(secciones);
        List<AmpliacionVacantes> ampliaciones = ampliacionVacanteDAO.allBySecciones(secciones);
        List<CambioAulaGrupo> cambiosAulaGpo = cambioAulaGrupoDAO.allBySecciones(secciones);
        List<DescuentoSeccionVerano> descuentosSeccionsVerano = descuentoSeccionVeranoDAO.findSecciones(secciones);
        List<MatriculaSeccion> matriculaSecciones = matriculaSeccionDAO.allBySeccionesMat(secciones);

        Map<Long, List<RestriccionModalidad>> mapRestriccionMod = TypesUtil.convertListToMapList("seccion.id", restriccionesMod);
        Map<Long, List<RestriccionFacultad>> mapRestriccionFac = TypesUtil.convertListToMapList("seccion.id", restriccionesFac);
        Map<Long, List<RestriccionCarrera>> mapRestriccionCarr = TypesUtil.convertListToMapList("seccion.id", restriccionesCarr);
        Map<Long, List<RestriccionRepitencia>> mapRestriccionRep = TypesUtil.convertListToMapList("seccion.id", restriccionesRep);
        Map<Long, List<AmpliacionVacantes>> mapAmpliaciones = TypesUtil.convertListToMapList("seccion.id", ampliaciones);
        Map<Long, List<CambioAulaGrupo>> mapCambioAulaGpo = TypesUtil.convertListToMapList("seccion.id", cambiosAulaGpo);
        Map<Long, List<DescuentoSeccionVerano>> mapDescuentoVerano = TypesUtil.convertListToMapList("seccion.id", descuentosSeccionsVerano);
        Map<Long, List<MatriculaSeccion>> mapAlumnosMatriculados = TypesUtil.convertListToMapList("seccion.id", matriculaSecciones);

        for (Seccion seccion : secciones) {
            seccion.setDocenteSeccion(getList(mapDocSeccion.get(seccion.getId())));
            seccion.setRestriccionesModalidad(getList(mapRestriccionMod.get(seccion.getId())));
            seccion.setRestriccionesFacultad(getList(mapRestriccionFac.get(seccion.getId())));
            seccion.setRestriccionesCarrera(getList(mapRestriccionCarr.get(seccion.getId())));
            seccion.setRestriccionesRepitencia(getList(mapRestriccionRep.get(seccion.getId())));
            seccion.setAmpliacionesVacantes(getList(mapAmpliaciones.get(seccion.getId())));
            seccion.setCambioAulaGrupos(getList(mapCambioAulaGpo.get(seccion.getId())));
            seccion.setDescuentoSeccionVeranos(getList(mapDescuentoVerano.get(seccion.getId())));
            seccion.setMatriculaSeccion(getList(mapAlumnosMatriculados.get(seccion.getId())));
        }

        return gpoSecc;
    }

    @Override
    public List<GrupoSeccion> allByDynatable(DynatableFilter filter, CicloAcademico ciclo, List<AnexoBoletin> anexosUser) {
        List<GrupoSeccion> gsecciones = grupoSeccionDAO.allByDynatable(filter, ciclo, anexosUser);
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
            List<DocenteSeccion> doceentesSecc = getList(mapDocSeccion.get(seccion.getId()));
            seccion.setDocenteSeccion(doceentesSecc);

            List<RestriccionModalidad> restriccionesModSecc = getList(mapRestriccionMod.get(seccion.getId()));
            seccion.setRestriccionesModalidad(restriccionesModSecc);

            List<RestriccionFacultad> restriccionesFacSecc = getList(mapRestriccionFac.get(seccion.getId()));
            seccion.setRestriccionesFacultad(restriccionesFacSecc);

            List<RestriccionCarrera> restriccionesCarrSecc = getList(mapRestriccionCarr.get(seccion.getId()));
            seccion.setRestriccionesCarrera(restriccionesCarrSecc);

            List<RestriccionRepitencia> restriccionesRepSecc = getList(mapRestriccionRep.get(seccion.getId()));
            seccion.setRestriccionesRepitencia(restriccionesRepSecc);
        }

        return gsecciones;
    }

    @Override
    public List<GrupoSeccion> allCleanByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        List<AnexoBoletin> anexos = anexoBoletinDAO.allAnexosHijos();
        return grupoSeccionDAO.allByDynatable(filter, ciclo, anexos);
    }

    @Override
    public List<GrupoSeccion> allCleanByDynatable(DynatableFilter filter, CicloAcademico ciclo, DataSessionPivot ds) {
        List<AnexoBoletin> anexosAll = anexoBoletinDAO.allAnexosHijos();
        List<AnexoBoletin> anexos = verificadorService.anexosInferioresByOficina(ds, anexosAll);
        return grupoSeccionDAO.allByDynatable(filter, ciclo, anexos);
    }

    @Override
    @Transactional
    public void cambiarEstadoGpoSeccion(SeccionEstadoEnum estadoEnum, GrupoSeccion grupoSeccion, DataSessionPivot ds) {
        DateTime today = new DateTime();
        grupoSeccion = grupoSeccionDAO.find(grupoSeccion.getId());
        if (estadoEnum == SeccionEstadoEnum.ACT) {
            grupoSeccion.setEstadoEnum(SeccionEstadoEnum.ACT);
            grupoSeccion.setUsuarioModificacion(ds.getUsuario());
            grupoSeccion.setFechaModificacion(today.toDate());
            grupoSeccionDAO.updateEstadoFechaModUsuarioMod(grupoSeccion);

        } else if (estadoEnum == SeccionEstadoEnum.ANU) {
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
            grupoSeccion.setUsuarioModificacion(ds.getUsuario());
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
        logger.debug("gruposHorasFilter {}", gruposHorasFilter.stream().map(x -> x.getCodigo()).collect(Collectors.joining(",")));
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
    public List<GrupoSeccion> saveGpoSeccionHeader(GrupoSeccion gpoSeccForm, CicloAcademico ciclo, DataSessionPivot ds) {
        if (gpoSeccForm.getCursoDirigido() == null) {
            gpoSeccForm.setCursoDirigido(Boolean.FALSE);
        }

        CicloAcademico cicloBD = cicloAcademicoDAO.find(ciclo);
        List<String> codigosByCiclo = grupoSeccionDAO.allCodigoByCiclo(cicloBD);
        List<String> codigos2ByCiclo = grupoSeccionDAO.allCodigo2ByCiclo(cicloBD);
        Curso curso = cursoDAO.find(gpoSeccForm.getCurso().getId());
        CursoCicloAcademico cursoCiclo = this.findCursoCicloAcademico(curso, ciclo);

        Integer horasTeoria = getHorasCurso(curso, cicloBD, TipoSeccionEnum.TEO);
        Integer horasPractica = getHorasCurso(curso, cicloBD, TipoSeccionEnum.PRA);

        List<GrupoSeccion> gpoSecciones = new ArrayList();
        Integer cantidad = gpoSeccForm.getCantidad();
        for (int i = 0; i < cantidad; i++) {
            String codigo = CodeGenerator.getNextCode(codigosByCiclo, 0);
            String codigo2 = CodeGenerator.getNextCode(codigos2ByCiclo, 0);
            GrupoSeccion gpoSeccNew = new GrupoSeccion();
            gpoSeccNew.setAnexoBoletin(gpoSeccForm.getAnexoBoletin());
            gpoSeccNew.setCicloAcademico(cicloBD);
            gpoSeccNew.setCodigo(codigo);
            gpoSeccNew.setCodigo2(codigo2);
            gpoSeccNew.setCurso(curso);
            gpoSeccNew.setCursoDirigido(gpoSeccForm.getCursoDirigido());
            gpoSeccNew.setHorasPractica(horasPractica);
            gpoSeccNew.setHorasTeoria(horasTeoria);
            gpoSeccNew.setEstadoEnum(SeccionEstadoEnum.ACT);
            gpoSeccNew.setCursoDirigido(gpoSeccForm.getCursoDirigido());
            if (gpoSeccForm.getCursoDirigido()) {
                gpoSeccNew.setDocenteResponsable(gpoSeccForm.getDocenteResponsable());
            }
            gpoSeccNew = saveGpoSeccion(gpoSeccNew, cicloBD, codigo, codigo2, curso, cursoCiclo, ds);
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
            Curso curso,
            CursoCicloAcademico cursoCiclo, DataSessionPivot ds) {

        grupoSeccion.setVersion(BigDecimal.ONE.toString());
        grupoSeccion.setEstadoGrupoEnum(EstadoGrupoSeccionEnum.ABI);
        grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.PEND);
        grupoSeccion.setTipoDictadoEnum(TipoDictadoGrupoSeccionEnum.SEM);

        Integer horasTeoria = grupoSeccion.getHorasTeoria();
        Integer horasPractica = grupoSeccion.getHorasPractica();
        EventoCicloAcademico eventoDictadoClases = getEventoDictadoClases(ciclo, grupoSeccion.getAnexoBoletin().getAnexoSuperior());

        Docente docenteDefault;
        if (!grupoSeccion.getCursoDirigido()) {
            docenteDefault = docenteDAO.findByCode(Constantine.DOCENTE_INDETERMINADO);
        } else {
            docenteDefault = grupoSeccion.getDocenteResponsable();
        }
        if (docenteDefault == null) {
            docenteDefault = docenteDAO.findByCode(Constantine.DOCENTE_INDETERMINADO);
        }

        grupoSeccion.setSecciones(new ArrayList());
        GrupoHoras grupoHorasZTEO = grupoHorasDAO.findByCode(GRUPO_ZTEO);
        GrupoHoras grupoHorasZPRA = grupoHorasDAO.findByCode(GRUPO_ZPRA);

        if (curso.isTipoCursoTEO()) {
            Seccion seccionTEO = new Seccion();
            seccionTEO.setCodigo(codigo + "0");
            seccionTEO.setCodigo2(codigo2 + "0");
            createSeccion(seccionTEO, TipoSeccionEnum.TEO, grupoSeccion, grupoHorasZTEO, horasTeoria, cursoCiclo, ds);

            DocenteSeccion docenteSeccion = new DocenteSeccion();
            createDocenteSeccion(docenteSeccion, docenteDefault, seccionTEO, eventoDictadoClases);

            grupoSeccion.getSecciones().add(seccionTEO);
        }

        if (curso.isTipoCursoPRA()) {
            Seccion seccionPRA = new Seccion();
            seccionPRA.setCodigo(codigo + "1");
            seccionPRA.setCodigo2(codigo2 + "1");
            createSeccion(seccionPRA, TipoSeccionEnum.PRA, grupoSeccion, grupoHorasZPRA, horasPractica, cursoCiclo, ds);

            DocenteSeccion docenteSeccion = new DocenteSeccion();
            createDocenteSeccion(docenteSeccion, docenteDefault, seccionPRA, eventoDictadoClases);

            grupoSeccion.getSecciones().add(seccionPRA);
        }

        if (curso.isTipoCursoTEOPRA()) {
            Seccion seccionTCUR = new Seccion();
            seccionTCUR.setCodigo(codigo + "0");
            seccionTCUR.setCodigo2(codigo2 + "0");
            createSeccion(seccionTCUR, TipoSeccionEnum.TCUR, grupoSeccion, grupoHorasZTEO, horasTeoria, null, ds);

            DocenteSeccion docenteSeccion = new DocenteSeccion();
            createDocenteSeccion(docenteSeccion, docenteDefault, seccionTCUR, eventoDictadoClases);

            grupoSeccion.getSecciones().add(seccionTCUR);

            Seccion seccionPCUR = new Seccion();
            seccionPCUR.setCodigo(codigo + "1");
            seccionPCUR.setCodigo2(codigo2 + "1");
            seccionPCUR.setSeccionSuperior(seccionTCUR);
            createSeccion(seccionPCUR, TipoSeccionEnum.PCUR, grupoSeccion, grupoHorasZPRA, horasPractica, cursoCiclo, ds);

            DocenteSeccion docenteSeccion2 = new DocenteSeccion();
            createDocenteSeccion(docenteSeccion2, docenteDefault, seccionPCUR, eventoDictadoClases);

            grupoSeccion.getSecciones().add(seccionPCUR);
        }

        grupoSeccionDAO.save(grupoSeccion);
        for (Seccion seccion : grupoSeccion.getSecciones()) {
            seccionDAO.save(seccion);
            for (DocenteSeccion docSecc : seccion.getDocenteSeccion()) {
                docenteSeccionDAO.save(docSecc);
            }
        }

        return grupoSeccion;
    }

    private void createDocenteSeccion(DocenteSeccion docenteSeccion, Docente docente, Seccion seccion, EventoCicloAcademico eventoDictadoClases) {
        docenteSeccion.setDocente(docente);
        docenteSeccion.setCodigoSeccion(seccion.getCodigo());
        docenteSeccion.setEstado(EstadoEnum.ACT.name());
        docenteSeccion.setFechaInicio(eventoDictadoClases.getFechaInicio());
        docenteSeccion.setFechaFin(eventoDictadoClases.getFechaFin());
        docenteSeccion.setPrincipal(BigDecimal.ONE.intValue());
        docenteSeccion.setSeccion(seccion);
        docenteSeccion.setPorcentajeCarga(PORCENTAJE_CARGA);
        docenteSeccion.setPorcentajeCargaFraccion(PORCENTAJE_CARGA_FRACCION);
        seccion.getDocenteSeccion().add(docenteSeccion);

    }

    private void createSeccion(
            Seccion seccion,
            TipoSeccionEnum tipoSeccion,
            GrupoSeccion grupoSeccion,
            GrupoHoras gpoHoras,
            int horasSemanales,
            CursoCicloAcademico cursoCiclo, DataSessionPivot ds) {

        seccion.setGrupoSeccion(grupoSeccion);
        seccion.setEstadoEnum(SeccionEstadoEnum.CRE);
        if (grupoSeccion.getCursoDirigido()) {
            seccion.setEstadoEnum(SeccionEstadoEnum.ACT);
        }
        seccion.setTipoSeccionEnum(tipoSeccion);
        seccion.setSituacionDocenteEnum(SituacionDocenteEnum.COR);
        seccion.setHorasSemanales(horasSemanales);
        seccion.setFechaRegistro(new Date());
        seccion.setUserRegistro(ds.getUsuario());
        seccion.setGrupoHoras(gpoHoras);

        if (tipoSeccion != TipoSeccionEnum.TCUR && cursoCiclo.getPrecio() != null) {
            seccion.setPrecio(cursoCiclo.getPrecio().add(cursoCiclo.getPrecioAdicional()));
            seccion.setPrecioBase(seccion.getPrecio().multiply(cursoCiclo.getMinimoAlumnos()));
            seccion.setDescuentoPrecio(BigDecimal.ZERO);
            seccion.setAbonoVerano(BigDecimal.ZERO);
        }

        seccion.setDocenteSeccion(new ArrayList());
    }

    @Override
    @Transactional
    public void addSeccion(GrupoSeccion grupoSeccion, DataSessionPivot ds) {
        grupoSeccion = grupoSeccionDAO.find(grupoSeccion.getId());
        Curso curso = grupoSeccion.getCurso();
        CicloAcademico ciclo = grupoSeccion.getCicloAcademico();
        CursoCicloAcademico cursoCiclo = cursoCicloAcademicoDAO.findByCursoCiclo(curso, ciclo);
        Docente docenteDefault = docenteDAO.findByCode(Constantine.DOCENTE_INDETERMINADO);
        List<Seccion> secciones = seccionDAO.allByGposSeccion(grupoSeccion);
        DateTime today = new DateTime();

        final BigDecimal PORCENTAJE_CARGA = new BigDecimal(100);
        final String PORCENTAJE_CARGA_FRACCION = "100";

        Seccion seccionTCUR = secciones.stream().filter(x -> x.getIsTipoSeccionTCUR()).findAny().orElse(null);
        Seccion seccionPCUR = new Seccion();
        seccionPCUR.setSeccionSuperior(seccionTCUR);
        seccionPCUR.setCodigo(getNextCode1(secciones));
        seccionPCUR.setCodigo2(getNextCode2(secciones));

        this.createSeccion(seccionPCUR, TipoSeccionEnum.PCUR, grupoSeccion, null, grupoSeccion.getHorasPractica(), cursoCiclo, ds);

        DocenteSeccion docenteSeccion2 = new DocenteSeccion();
        docenteSeccion2.setDocente(docenteDefault);
        docenteSeccion2.setCodigoSeccion(seccionPCUR.getCodigo());
        docenteSeccion2.setEstado(EstadoEnum.ACT.name());
        docenteSeccion2.setFechaInicio(today.toDate());
        docenteSeccion2.setPrincipal(BigDecimal.ONE.intValue());
        docenteSeccion2.setSeccion(seccionPCUR);
        docenteSeccion2.setPorcentajeCarga(PORCENTAJE_CARGA);
        docenteSeccion2.setPorcentajeCargaFraccion(PORCENTAJE_CARGA_FRACCION);
        seccionPCUR.getDocenteSeccion().add(docenteSeccion2);

        seccionDAO.save(seccionPCUR);
        for (DocenteSeccion docSecc : seccionPCUR.getDocenteSeccion()) {
            docenteSeccionDAO.save(docSecc);
        }
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
        //Curso curso = seccion.getGrupoSeccion().getCurso();
        EventoCicloAcademico eventoDictadoClases = getEventoDictadoClases(cicloAcademico, seccion.getGrupoSeccion().getAnexoBoletin().getAnexoSuperior());

        Docente docenteDefault = docenteDAO.findByCode(Constantine.DOCENTE_INDETERMINADO);
        List<DocenteSeccion> docenteSeccions = docenteSeccionDAO.allActivosBySeccion(seccion);

        Fraxtion fraxtion100 = new Fraxtion(100);
        Fraxtion fraxtionSum = new Fraxtion(0);
        for (DocenteSeccion profeSecc : docenteSeccions) {
            fraxtionSum = fraxtionSum.add(new Fraxtion(profeSecc.getPorcentajeCargaFraccion()));
        }
        Fraxtion restFraccion = fraxtion100.substract(fraxtionSum);

        DocenteSeccion docenteSeccion = new DocenteSeccion();
        docenteSeccion.setDocente(docenteDefault);
        docenteSeccion.setCodigoSeccion(seccion.getCodigo());
        docenteSeccion.setEstado(EstadoEnum.ACT.name());
        if (TipoDictadoGrupoSeccionEnum.MOD.equals(seccion.getGrupoSeccion().getTipoDictadoEnum())) {
            docenteSeccion.setFechaInicio(seccion.getGrupoSeccion().getFechaInicioModular());
            docenteSeccion.setFechaFin(seccion.getGrupoSeccion().getFechaFinModular());
        } else {
            docenteSeccion.setFechaInicio(eventoDictadoClases.getFechaInicio());
            docenteSeccion.setFechaFin(eventoDictadoClases.getFechaFin());
        }
        docenteSeccion.setPrincipal(BigDecimal.ZERO.intValue());
        docenteSeccion.setSeccion(seccion);
        docenteSeccion.setPorcentajeCarga(restFraccion.getValue(2));
        docenteSeccion.setPorcentajeCargaFraccion(restFraccion.toString());
        docenteSeccionDAO.save(docenteSeccion);

        docenteSeccions.add(docenteSeccion);
        //    EventoCicloAcademico eventoClases = getEventoDictadoClases(cicloAcademico, seccionDB.getGrupoSeccion().getCurso());
        this.analizedDocenteSeccion(seccion, docenteSeccions, eventoDictadoClases);
    }

    @Override
    @Transactional
    public void updateDocenteSecFechaInicio(DocenteSeccion profeSeccForm, CicloAcademico cicloAcademico) {

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
//                if (profeSeccForm.getFechaInicio().compareTo(profeSec.getFechaInicio()) >= 0 && profeSeccForm.getFechaInicio().compareTo(profeSec.getFechaFin()) <= 0) {
//                    throw new PhobosException("La fecha seleccionada se encuentra dentro de un rango fijado");
//                } else {
//                    continue;
//                }
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

        EventoCicloAcademico eventoClases = getEventoDictadoClases(cicloAcademico, profeSeccDB.getSeccion().getGrupoSeccion().getAnexoBoletin().getAnexoSuperior());
        this.analizedDocenteSeccion(profeSeccDB.getSeccion(), profesSecc, eventoClases);
    }

    @Override
    @Transactional
    public void updateDocenteSecFechaFin(DocenteSeccion profeSeccForm, CicloAcademico cicloAcademico) {

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
//                if (profeSeccForm.getFechaFin().compareTo(profeSec.getFechaInicio()) >= 0 && profeSeccForm.getFechaFin().compareTo(profeSec.getFechaFin()) <= 0) {
//                    throw new PhobosException("La fecha seleccionada se encuentra dentro de un rango fijado");
//                } else if (profeSeccDB.getFechaInicio() != null) {
//                    if (profeSec.getFechaFin().compareTo(profeSeccDB.getFechaInicio()) >= 0 && profeSec.getFechaFin().compareTo(profeSeccForm.getFechaFin()) <= 0) {
//                        throw new PhobosException("La fecha seleccionada abarca un rango establecido.");
//                    }
//                } else {
//                    continue;
//                }
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
        EventoCicloAcademico eventoClases = getEventoDictadoClases(cicloAcademico, profeSeccDB.getSeccion().getGrupoSeccion().getAnexoBoletin().getAnexoSuperior());
        this.analizedDocenteSeccion(profeSeccDB.getSeccion(), profesSecc, eventoClases);
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
            if (encuesta != null) {
                List<PeriodoEncuesta> periodosEncuesta = periodoEncuestaDAO.allByEncuesta(encuesta.getEncuestaEstudiantil());
                if (!periodosEncuesta.isEmpty()) {
                    encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
                    encuesta.setFechaEncuestaInicio(periodosEncuesta.get(0).getFechaInicio());
                    encuesta.setFechaEncuestaFin(periodosEncuesta.get(0).getFechaFin());
                    encuestaDocenteDAO.update(encuesta);
                }
            }
        } else {
            for (DocenteSeccion docenteSeccion : lstDocSec) {
                EncuestaDocente encuesta = encuestaDocenteDAO.findByDocenteSeccion(docenteSeccion);
                if (encuesta != null) {
                    ConfiguraEncuesta configuraEncuesta = configuraEncuestaDAO.findByEncuesta(encuesta.getEncuestaEstudiantil());
                    Date inicioEncuesta = new DateTime(docenteSeccion.getFechaFin()).minusDays(configuraEncuesta.getDiasEncuesta().intValue()).toDate();
                    encuesta.setEstadoEnum(EncuestaEstudiantilEstadoEnum.ACT);
                    encuesta.setFechaEncuestaInicio(inicioEncuesta);
                    encuesta.setFechaEncuestaFin(docenteSeccion.getFechaFin());
                    encuestaDocenteDAO.update(encuesta);
                }
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
            // FALTA REVISAR ESTE METODO Q SE ESTA DESACTIVANDO YA QUE TODO LO DE VACANTES
            // DEBE SER GESTIONADO POR EL SISTEMA MATRICULA
            //
            //seccionDAO.updateSeccionVacantes(seccionTCUR);
            //seccionDAO.updateSeccionVacantes(seccionTCUR);
            //seccionDAO.updateSeccionVacantes(seccionTCUR);
            //seccionDAO.updateSeccionVacantes(seccionTCUR);
            //seccionDAO.updateSeccionVacantes(seccionTCUR);
            //seccionDAO.updateSeccionVacantes(seccionTCUR);
            //seccionDAO.updateSeccionVacantes(seccionTCUR);
            //seccionDAO.updateSeccionVacantes(seccionTCUR);
            //seccionDAO.updateSeccionVacantes(seccionTCUR);
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
    public void activarSeccion(Seccion seccionForm, DataSessionPivot ds) {
        Seccion seccionBD = seccionDAO.find(seccionForm.getId());
        if (seccionBD.getEstadoEnum() == SeccionEstadoEnum.ACT) {
            throw new PhobosException("Esta sección ya esta activada");
        }

        DateTime today = new DateTime();
        {
            Seccion seccionUpd = new Seccion(seccionBD.getId());
            seccionUpd.setUsuarioModificacion(ds.getUsuario());
            seccionUpd.setFechaModificacion(today.toDate());
            seccionUpd.setEstadoEnum(SeccionEstadoEnum.ACT);
            seccionDAO.updateColumns(seccionUpd, "usuarioModificacion", "fechaModificacion", "estado");
            logger.debug("seccionDAO.updateEstadoFechaModUsuarioMod(seccionBD);");
        }

        GrupoSeccion gpoSecc = seccionBD.getGrupoSeccion();
        Curso curso = seccionBD.getGrupoSeccion().getCurso();
        if (curso.isTipoCursoTEOPRA()) {
            Seccion seccionTCUR = seccionDAO.findByGpoSeccionTipoSeccion(gpoSecc, TipoSeccionEnum.TCUR);
            if (seccionTCUR.getEstadoEnum() != SeccionEstadoEnum.ACT) {
                Seccion seccionUpd = new Seccion(seccionTCUR.getId());
                seccionUpd.setUsuarioModificacion(ds.getUsuario());
                seccionUpd.setFechaModificacion(today.toDate());
                seccionUpd.setEstadoEnum(SeccionEstadoEnum.ACT);
                seccionDAO.updateColumns(seccionUpd, "usuarioModificacion", "fechaModificacion", "estado");
                logger.debug("seccionDAO.updateEstadoFechaModUsuarioMod(seccionTCUR);");
            }
        }

        //this.actualizarVacantesTCUR(gpoSecc, ds, today);
        this.actualizarBoletin();
    }

    @Override
    @Transactional
    public void bloquearSeccion(Seccion seccion, DataSessionPivot ds) {
        seccion = seccionDAO.find(seccion.getId());
        DateTime today = new DateTime();
        seccion.setUsuarioModificacion(ds.getUsuario());
        seccion.setFechaModificacion(today.toDate());
        seccion.setEstadoEnum(SeccionEstadoEnum.BLO);
        seccionDAO.updateEstadoFechaModUsuarioMod(seccion);

        this.actualizarVacantesTCUR(seccion.getGrupoSeccion(), ds, today);
        this.actualizarBoletin();
    }

    @Override
    @Transactional
    public GrupoSeccion anularSeccion(Seccion seccioForm, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Seccion seccionBD = seccionDAO.find(seccioForm.getId());
        GrupoSeccion grupoSeccion = seccionBD.getGrupoSeccion().clone();
        Curso curso = grupoSeccion.getCurso().clone();

        // validar matricula seccion, sin importar estado
        List<AlumnoEvaluacion> alumnoEvaluacion = alumnoEvaluacionDAO.allBySeccion(seccionBD.getId());
        Assert.isTrue(alumnoEvaluacion.isEmpty(), "La sección tiene notas registradas");
        Assert.isFalse(seccionBD.isTipoSeccionTCUR(), "Este tipo de sección no puede ser anulada");

        List<Evaluacion> evaluacionesBySeccion = evaluacionDAO.allBySeccion(seccionBD);
        if (!evaluacionesBySeccion.isEmpty()) {
            for (Evaluacion evaluacion : evaluacionesBySeccion) {
                evaluacionDAO.delete(evaluacion);
            }
        }

        this.deleteHorarioSeccion(seccionBD);

        List<MatriculaSeccion> matriculasSeccionAll = matriculaSeccionDAO.allBySeccion(seccionBD);
        List<MatriculaSeccion> matriculados = new ArrayList();
        for (MatriculaSeccion matSeccion : matriculasSeccionAll) {
            if (matSeccion.isEstadoMAT() || matSeccion.isEstadoPMAT()) {
                matriculados.add(matSeccion);
            }
        }

        List<MatriculaResumen> resumenes = matriculados.stream().map(x -> x.getMatriculaResumen()).collect(Collectors.toList());
        List<MatriculaCurso> matriculasCurso = matriculaCursoDAO.allActivosByMatriculaResumenCurso(resumenes, curso);

        SeccionEstadoEnum estadoFinal = matriculados.isEmpty() ? SeccionEstadoEnum.ANU : SeccionEstadoEnum.CAN;
        EstadoMatriculaEnum estadoRet = matriculados.isEmpty() ? EstadoMatriculaEnum.RAN : EstadoMatriculaEnum.RCA;
        for (MatriculaCurso matCurso : matriculasCurso) {
            if (matCurso.isEstadoMAT() || matCurso.isEstadoPMAT()) {
                TokenIngresante token = responseRestService.createToken(ds);
                JsonResponse response = responseRestService.retirarMatriculaCurso(matCurso, ds, estadoRet, token);
                Assert.isTrue(response.getSuccess(), response.getMessage());
            }
        }

        TokenIngresante token = responseRestService.createToken(ds);
        JsonResponse response = responseRestService.ampliarVacante(seccionBD, -seccionBD.getVacantes(), ds, token);
        Assert.isTrue(response.getSuccess(), response.getMessage());

        if (matriculasSeccionAll.isEmpty()) {
            this.deleteDependenciasSeccion(seccionBD);
            seccionDAO.delete(seccionBD);

            List<Seccion> seccionesAll = seccionDAO.allByGpoSeccion(grupoSeccion);
            if (seccionesAll.size() <= 1) {
                for (Seccion seccion : seccionesAll) {
                    this.deleteDependenciasSeccion(seccion);
                    seccionDAO.delete(seccion);
                }
                evaluacionExpandidaDAO.deleteByGrupoSeccion(grupoSeccion);
                evaluacionSeccionDAO.deleteByGrupoSeccion(grupoSeccion);
                grupoSeccionDAO.deleteGrupoSeccion(grupoSeccion);

                this.actualizarBoletin();

                GrupoSeccion grupoSeccionReturn = new GrupoSeccion();
                grupoSeccionReturn.setCurso(curso);
                return grupoSeccionReturn;
            }

            List<Seccion> seccionesActivas = seccionDAO.allOperativesByGpoSeccion(grupoSeccion);
            if (seccionesActivas.size() <= 1) {
                for (Seccion seccion : seccionesActivas) {
                    Seccion seccionUpd = new Seccion(seccion.getId());
                    seccionUpd.setUsuarioModificacion(ds.getUsuario());
                    seccionUpd.setFechaModificacion(today.toDate());
                    seccionUpd.setEstadoEnum(estadoFinal);
                    seccionDAO.updateColumns(seccionUpd, "usuarioModificacion", "fechaModificacion", "estado");
                }

                GrupoSeccion grupoSeccionUpd = new GrupoSeccion(grupoSeccion.getId());
                grupoSeccionUpd.setEstadoEnum(SeccionEstadoEnum.ANU);
                grupoSeccionUpd.setUsuarioModificacion(ds.getUsuario());
                grupoSeccionUpd.setFechaModificacion(today.toDate());
                grupoSeccionDAO.updateColumns(grupoSeccionUpd, "estado", "usuarioModificacion", "fechaModificacion");

                evaluacionExpandidaDAO.deleteByGrupoSeccion(grupoSeccion);
                evaluacionSeccionDAO.deleteByGrupoSeccion(grupoSeccion);

                this.actualizarBoletin();
                return grupoSeccion;
            }

            Collections.sort(seccionesActivas, (Seccion va1, Seccion va2) -> va1.getCodigo2().compareTo(va2.getCodigo2()));
            int i = 0;
            for (Seccion seccionEach : seccionesActivas) {
                String cod2 = grupoSeccion.getCodigo2() + i;
                if (!seccionEach.getCodigo2().equals(cod2)) {
                    Seccion seccionUpd = new Seccion(seccionEach.getId());
                    seccionUpd.setCodigo2(cod2);
                    seccionUpd.setUsuarioModificacion(ds.getUsuario());
                    seccionUpd.setFechaModificacion(today.toDate());
                    seccionDAO.updateColumns(seccionUpd, "usuarioModificacion", "fechaModificacion", "codigo2");
                }
                i++;
            }

            this.actualizarBoletin();
            return grupoSeccion;

        }

        {
            Seccion seccionUpd = new Seccion(seccionBD.getId());
            seccionUpd.setUsuarioModificacion(ds.getUsuario());
            seccionUpd.setFechaModificacion(today.toDate());
            seccionUpd.setEstadoEnum(estadoFinal);
            seccionDAO.updateColumns(seccionUpd, "usuarioModificacion", "fechaModificacion", "estado");
        }

        this.actualizarBoletin();

        int operativas = 0;
        if (grupoSeccion.getCurso().isTipoCursoTEOPRA()) {
            List<Seccion> secciones = seccionDAO.allOperativesByGpoSeccion(grupoSeccion);
            Seccion seccionTCUR = secciones.stream().filter(x -> x.isTipoSeccionTCUR()).findFirst().orElse(null);
            List<Seccion> seccionesPCUR = secciones.stream()
                    .filter(x -> x.isTipoSeccionPCUR())
                    .filter(x -> x.getId().compareTo(seccionBD.getId()) != 0)
                    .collect(Collectors.toList());

            operativas = seccionesPCUR.size();

            if (seccionesPCUR.isEmpty() && seccionTCUR != null) {
                Seccion seccionUpd = new Seccion(seccionTCUR.getId());
                seccionUpd.setUsuarioModificacion(ds.getUsuario());
                seccionUpd.setFechaModificacion(today.toDate());
                seccionUpd.setEstadoEnum(estadoFinal);
                seccionDAO.updateColumns(seccionUpd, "usuarioModificacion", "fechaModificacion", "estado");
            }

        }

        if (operativas == 0) {
            GrupoSeccion grupoSeccionUpd = new GrupoSeccion(grupoSeccion.getId());
            grupoSeccionUpd.setEstadoEnum(SeccionEstadoEnum.ANU);
            grupoSeccionUpd.setUsuarioModificacion(ds.getUsuario());
            grupoSeccionUpd.setFechaModificacion(today.toDate());
            grupoSeccionDAO.updateColumns(grupoSeccionUpd, "estado", "usuarioModificacion", "fechaModificacion");
        }

        for (MatriculaSeccion matriculado : matriculados) {
            MatriculaSeccion matSeccUpd = new MatriculaSeccion(matriculado.getId());
            matSeccUpd.setEstadoEnum(EstadoMatriculaEnum.RCA);
            matriculaSeccionDAO.updateColumns(matSeccUpd, "estado");
        }

        return grupoSeccion;
    }

    private void deleteHorarioSeccion(Seccion seccion) {
        List<HorarioSeccion> horarioSecc = horarioSeccionDAO.allBySeccion(seccion);
        for (HorarioSeccion hSecc : horarioSecc) {
            horarioSeccionDAO.delete(hSecc);
        }

        List<HorarioAula> horarioAula = horarioAulaDAO.allBySeccion(seccion);
        for (HorarioAula hSecc : horarioAula) {
            horarioAulaDAO.delete(hSecc);
        }
    }

    private void deleteDependenciasSeccion(Seccion seccion) {
        List<DocenteSeccion> docentesSec = docenteSeccionDAO.allBySeccion(seccion);
        for (DocenteSeccion docenteSeccion : docentesSec) {
            docenteSeccionDAO.delete(docenteSeccion);
        }
        List<VacanteAlumno> vacantesAlumnos = vacanteAlumnoDAO.allBySeccion(seccion);
        for (VacanteAlumno vacanteAlumno : vacantesAlumnos) {
            vacanteAlumnoDAO.delete(vacanteAlumno);
        }
        List<RestriccionCarrera> restriccionesCarr = restriccionCarreraDAO.allBySeccion(seccion);
        for (RestriccionCarrera restricc : restriccionesCarr) {
            restriccionCarreraDAO.delete(restricc);
        }
        List<RestriccionFacultad> restriccionesFac = restriccionFacultadDAO.allBySeccion(seccion);
        for (RestriccionFacultad restricc : restriccionesFac) {
            restriccionFacultadDAO.delete(restricc);
        }
        List<RestriccionModalidad> restriccionesMod = restriccionModalidadDAO.allBySeccion(seccion);
        for (RestriccionModalidad restricc : restriccionesMod) {
            restriccionModalidadDAO.delete(restricc);
        }
        List<RestriccionRepitencia> restriccionRep = restriccionRepitenciaDAO.allBySeccion(seccion);
        for (RestriccionRepitencia restricc : restriccionRep) {
            restriccionRepitenciaDAO.delete(restricc);
        }
    }

    @Override
    @Transactional
    public void cancelarSeccion(Seccion seccionForm, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Seccion seccionBD = seccionDAO.find(seccionForm.getId());
        Assert.isNotNull(seccionBD, "La sección que desea cancelar no existe en el sistema");
        Assert.isTrue(seccionBD.getEstadoEnum() == SeccionEstadoEnum.ACT, "La sección que desea cancelar debe estar activa");

        if (seccionBD.isTipoSeccionTCUR()) {
            List<Seccion> seccionesBySup = seccionDAO.allBySeccionSuperior(seccionBD);
            List<Seccion> seccionesNoCanceladas = seccionesBySup.stream()
                    .filter(x -> !x.isEstadoCancelado())
                    .collect(Collectors.toList());
            Assert.isTrue(seccionesNoCanceladas.isEmpty(), "Debe cancelar las secciones de practicas.");
            return;
        }

        List<MatriculaSeccion> matriculadosSeccionPRA = matriculaSeccionDAO.allMatriculadosBySeccion(seccionBD);
        Assert.isFalse(matriculadosSeccionPRA.isEmpty(), "Solo se puede cancelar una sección con alumnos matriculados");
        Assert.isTrue(matriculadosSeccionPRA.size() == seccionBD.getMatriculados(), "La cantidad de matriculados no coincide con el dato en la sección");

        GrupoSeccion grupoSeccion = seccionBD.getGrupoSeccion();
        Curso curso = grupoSeccion.getCurso();
        List<MatriculaResumen> resumenes = matriculadosSeccionPRA.stream().map(x -> x.getMatriculaResumen()).collect(Collectors.toList());
        List<MatriculaCurso> matriculasCurso = matriculaCursoDAO.allActivosByMatriculaResumenCurso(resumenes, curso);

        for (MatriculaCurso matCurso : matriculasCurso) {
            if (matCurso.isEstadoMAT() || matCurso.isEstadoPMAT()) {
                TokenIngresante token = responseRestService.createToken(ds);
                JsonResponse response = responseRestService.retirarMatriculaCurso(matCurso, ds, EstadoMatriculaEnum.RCA, token);
                Assert.isTrue(response.getSuccess(), response.getMessage());
            }
        }

        TokenIngresante token = responseRestService.createToken(ds);
        JsonResponse response = responseRestService.ampliarVacante(seccionBD, -seccionBD.getVacantes(), ds, token);
        Assert.isTrue(response.getSuccess(), response.getMessage());

        horarioAulaDAO.deleteBySecciones(Arrays.asList(seccionBD));

        {
            Seccion seccionUpd = new Seccion(seccionBD.getId());
            seccionUpd.setUsuarioModificacion(ds.getUsuario());
            seccionUpd.setFechaModificacion(today.toDate());
            seccionUpd.setEstadoEnum(SeccionEstadoEnum.CAN);
            seccionUpd.setMotivoCancelacion(seccionForm.getMotivoCancelacion());
            if (seccionBD.getAula() != null) {
                seccionUpd.setAulaBorrada(new Aula(seccionBD.getAula().getId()));
            }

            seccionDAO.updateColumns(seccionUpd,
                    "usuarioModificacion", "fechaModificacion", "estado", "aulaBorrada", "motivoCancelacion");
        }

        this.actualizarBoletin();

        int operativas = 0;
        if (grupoSeccion.getCurso().isTipoCursoTEOPRA()) {
            List<Seccion> secciones = seccionDAO.allOperativesByGpoSeccion(grupoSeccion);
            Seccion seccionTCUR = secciones.stream().filter(x -> x.isTipoSeccionTCUR()).findFirst().orElse(null);
            List<Seccion> seccionesPCUR = secciones.stream()
                    .filter(x -> x.isTipoSeccionPCUR())
                    .filter(x -> x.getId().compareTo(seccionBD.getId()) != 0)
                    .collect(Collectors.toList());

            operativas = seccionesPCUR.size();

            if (seccionesPCUR.isEmpty() && seccionTCUR != null) {
                horarioAulaDAO.deleteBySecciones(Arrays.asList(seccionTCUR));

                Seccion seccionUpd = new Seccion(seccionTCUR.getId());
                seccionUpd.setUsuarioModificacion(ds.getUsuario());
                seccionUpd.setFechaModificacion(today.toDate());
                seccionUpd.setEstadoEnum(SeccionEstadoEnum.CAN);
                if (seccionTCUR.getAula() != null) {
                    seccionUpd.setAulaBorrada(new Aula(seccionTCUR.getAula().getId()));
                }
                seccionDAO.updateColumns(seccionUpd, "usuarioModificacion", "fechaModificacion", "estado", "aulaBorrada");
            }

        }

        if (operativas == 0) {
            GrupoSeccion grupoSeccionUpd = new GrupoSeccion(grupoSeccion.getId());
            grupoSeccionUpd.setEstadoEnum(SeccionEstadoEnum.ANU);
            grupoSeccionUpd.setUsuarioModificacion(ds.getUsuario());
            grupoSeccionUpd.setFechaModificacion(today.toDate());
            grupoSeccionDAO.updateColumns(grupoSeccionUpd, "estado", "usuarioModificacion", "fechaModificacion");
        }

        for (MatriculaSeccion matriculado : matriculadosSeccionPRA) {
            MatriculaSeccion matSeccUpd = new MatriculaSeccion(matriculado.getId());
            matSeccUpd.setEstadoEnum(EstadoMatriculaEnum.RCA);
            matriculaSeccionDAO.updateColumns(matSeccUpd, "estado");
        }

    }

    @Override
    @Transactional
    public void deleteDocSeccion(DocenteSeccion docenteSeccion, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        docenteSeccion = docenteSeccionDAO.find(docenteSeccion.getId());
        Seccion seccion = docenteSeccion.getSeccion().clone();

        List<EncuestaDocente> encuestasProfeSecc = encuestaDocenteDAO.allByDocenteSeccion(docenteSeccion);
        if (encuestasProfeSecc.isEmpty()) {
            docenteSeccionDAO.delete(docenteSeccion);

        } else {
            docenteSeccion.setEstadoEnum(SeccionEstadoEnum.INA);
            docenteSeccion.setUserAnulacion(ds.getUsuario());
            docenteSeccion.setFechaAnulacion(new Date());
            docenteSeccionDAO.update(docenteSeccion);
        }

        List<DocenteSeccion> docentesSec = docenteSeccionDAO.allBySeccion(seccion);

        EventoCicloAcademico eventoCslases = getEventoDictadoClases(cicloAcademico, docenteSeccion.getSeccion().getGrupoSeccion().getAnexoBoletin().getAnexoSuperior());
        this.analizedDocenteSeccion(docenteSeccion.getSeccion(), docentesSec, eventoCslases);
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
    public List<AnexoBoletin> allAnexoBoletionHijos(CicloAcademico ciclo, DataSessionPivot ds) {
        List<AnexoBoletin> anexosAll = anexoBoletinDAO.allAnexosHijos();
        List<AnexoBoletin> anexos = verificadorService.anexosInferioresByOficina(ds, anexosAll);
        List<GrupoSeccion> gpoSecciones = grupoSeccionDAO.allByCiclo(ciclo);
        Map<Long, List<GrupoSeccion>> mapGpoSeccion = TypesUtil.convertListToMapList("anexoBoletin.id", gpoSecciones);
        for (AnexoBoletin anexo : anexos) {
            List<GrupoSeccion> gpoSeccionesAnx = TypesUtil.getListNotNull(mapGpoSeccion.get(anexo.getId()));
            anexo.setCantidadGpoSeccInt(gpoSeccionesAnx.size());
        }
        return anexos;
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
    public List<Docente> allDocenterByNombre(String nombre, String codigoDep) {
        return docenteDAO.allByNombreFilter(nombre, 10, codigoDep);
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
        Seccion seccionDb = seccionDAO.find(seccion);

        ModalidadEstudio modalidadCurso = seccionDb.getGrupoSeccion().getCurso().getModalidadEstudio();
        EventoCicloAcademico eventoAcademico = this.getEventoDictadoClasesByCicloAcademico(seccion.getGrupoSeccion(), ciclo, modalidadCurso);

        logger.debug("***eventoAcademico*** {}", eventoAcademico != null);
        if (eventoAcademico != null) {
            logger.debug("***eventoAcademico {}", eventoAcademico.getId());
            logger.debug("***inicio  {} fin {}", eventoAcademico.getFechaInicio(), eventoAcademico.getFechaFin());
        }

        List<HorarioAula> horariosAula = new ArrayList();
        if (!aulas.isEmpty() && !diaHoras.isEmpty()) {
            horariosAula = horarioAulaDAO.allByAulasCicloDiasHoras(aulas, eventoAcademico, diaHoras);
        }
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
    public void actualizarDocente(Long docenteSeccionId, Long docenteId, CicloAcademico cicloAcademico) {
        DocenteSeccion docenteSeccion = docenteSeccionDAO.find(docenteSeccionId);
        docenteSeccion.setDocente(new Docente(docenteId));
        docenteSeccionDAO.updateDocente(docenteSeccion);

        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allBySeccion(docenteSeccion.getSeccion());

        EventoCicloAcademico eventoClases = getEventoDictadoClases(cicloAcademico, docenteSeccion.getSeccion().getGrupoSeccion().getAnexoBoletin().getAnexoSuperior());
        this.analizedDocenteSeccion(docenteSeccion.getSeccion(), docentesSeccion, eventoClases);
    }

    @Override
    @Transactional
    public void actualizarSeccionResctriccionCapa(Seccion seccionForm, DataSessionPivot ds) {
        seccionDAO.updateRestriccionCapa(seccionForm);
    }

    @Override
    @Transactional
    public void actualizarSeccionVacantes(Seccion seccionForm, DataSessionPivot ds) {
        Seccion seccioDB = seccionDAO.find(seccionForm.getId());

        if (ObjectUtil.getParentTree(seccioDB, "aula.id") != null) {
            if (seccioDB.getAula().getCapacidadAula().compareTo(seccionForm.getVacantes()) < 0) {
                throw new PhobosException(String.format("Las vacantes de la sección %s superan, el aforo su aula", seccionForm.getCodigo2()));
            }
        }
        List<MatriculaSeccion> matriculasSeccionSelect = matriculaSeccionDAO.allMatriculadosBySeccion(seccioDB);
        if (matriculasSeccionSelect.size() > seccionForm.getVacantes()) {
            throw new PhobosException(String.format("Error. Las matriculas para la sección %s superan la cantidad de vacantes asignadas.", seccionForm.getCodigo2()));
        }

        TokenIngresante token = responseRestService.createToken(ds);
        JsonResponse response = responseRestService.ampliarVacante(seccioDB, seccionForm.getVacantes() - seccioDB.getVacantes(), ds, token);
        Assert.isTrue(response.getSuccess(), response.getMessage());

        this.actualizarBoletin();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    private void actualizarVacantesTCUR(GrupoSeccion grupoSeccion, DataSessionPivot ds, DateTime today) {
        if (1 == 1) {
            return;
        }
        if (!grupoSeccion.getCurso().isTipoCursoTEOPRA()) {
            return;
        }

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
        // FALTA REVISAR ESTE METODO Q SE ESTA DESACTIVANDO YA QUE TODO LO DE VACANTES
        // DEBE SER GESTIONADO POR EL SISTEMA MATRICULA
        //
        //seccionDAO.updateSeccionVacantes(seccionTCUR);
        //seccionDAO.updateSeccionVacantes(seccionTCUR);
        //seccionDAO.updateSeccionVacantes(seccionTCUR);
        //seccionDAO.updateSeccionVacantes(seccionTCUR);
        //seccionDAO.updateSeccionVacantes(seccionTCUR);
        //seccionDAO.updateSeccionVacantes(seccionTCUR);
        //seccionDAO.updateSeccionVacantes(seccionTCUR);
        //seccionDAO.updateSeccionVacantes(seccionTCUR);
        //seccionDAO.updateSeccionVacantes(seccionTCUR);
        //seccionDAO.updateSeccionVacantes(seccionTCUR);

    }

    @Override
    @Transactional
    public void updatePorcentajeAvance(DocenteSeccion profeSeccForm, CicloAcademico cicloAcademico) {
        DocenteSeccion profeSeccBDMain = docenteSeccionDAO.find(profeSeccForm.getId());
        List<DocenteSeccion> profesSecc = docenteSeccionDAO.allBySeccion(profeSeccBDMain.getSeccion());

        Fraxtion total = new Fraxtion(BigDecimal.ZERO);
        for (DocenteSeccion profeSecc : profesSecc) {
            if (profeSecc.getId() == profeSeccForm.getId().longValue()) {
                continue;
            }
            if (profeSecc.getEstadoEnum() != SeccionEstadoEnum.ACT) {
                continue;
            }
            if (profeSecc.getPorcentajeCargaFraccion() == null) {
                continue;
            }
            total = total.add(new Fraxtion(profeSecc.getPorcentajeCargaFraccion()));
        }
        Fraxtion porcentaje = new Fraxtion(profeSeccForm.getPorcentajeCargaFraccion());
        total = total.add(porcentaje);
        Fraxtion cien = new Fraxtion(100L);

        if (total.compareTo(cien) > 0) {
            throw new PhobosException("El porcentaje de carga no puede exceder el 100%. Usted ingresó " + porcentaje.getValue(2) + "%");
        }
        profeSeccBDMain.setPorcentajeCargaFraccion(porcentaje.toString());
        profeSeccBDMain.setPorcentajeCarga(porcentaje.getValue(2));
        docenteSeccionDAO.update(profeSeccBDMain);

        evaluateSeccion(profeSeccBDMain.getSeccion());
        this.actualizarBoletin();

        EventoCicloAcademico eventoClases = getEventoDictadoClases(cicloAcademico, profeSeccBDMain.getSeccion().getGrupoSeccion().getAnexoBoletin().getAnexoSuperior());
        this.analizedDocenteSeccion(profeSeccBDMain.getSeccion(), profesSecc, eventoClases);
    }

    @Override
    @Transactional
    public List<DocenteSeccion> analizedDocenteSeccion(GrupoSeccion grupoSeccion, CicloAcademico cicloAcademico) {
        List<Seccion> secciones = seccionDAO.allOperativesByGpoSeccion(grupoSeccion);
        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allActivosBySecciones(secciones);
        Map<Long, List<DocenteSeccion>> mapProfeSecc = TypesUtil.convertListToMapList("seccion.id", docentesSeccion);
        EventoCicloAcademico eventoClases = getEventoDictadoClases(cicloAcademico, secciones.get(0).getGrupoSeccion().getAnexoBoletin().getAnexoSuperior());
        for (Seccion seccion : secciones) {
            docentesSeccion = createList(mapProfeSecc.get(seccion.getId()));
            this.analizedDocenteSeccion(seccion, docentesSeccion, eventoClases);
        }
        return docentesSeccion;
    }

    private void analizedDocenteSeccion(
            Seccion seccion, List<DocenteSeccion> docentesSeccion,
            EventoCicloAcademico eventoClases) {

        Boolean errorPorcentajeCarga = Boolean.FALSE;
        Boolean errorPeriodoClases = Boolean.FALSE;

        //EventoCicloAcademico eventoClases = getEventoDictadoClases(cicloAcademico, seccion.getGrupoSeccion().getCurso());
        DateTime fechaIniClases = new DateTime(eventoClases.getFechaInicio());
        DateTime fechaFinClases = new DateTime(eventoClases.getFechaFin());
        if (TipoDictadoGrupoSeccionEnum.MOD.equals(seccion.getGrupoSeccion().getTipoDictadoEnum())) {
            fechaIniClases = new DateTime(seccion.getGrupoSeccion().getFechaInicioModular());
            fechaFinClases = new DateTime(seccion.getGrupoSeccion().getFechaFinModular());
        }

        Collections.sort(docentesSeccion, (DocenteSeccion va1, DocenteSeccion va2) -> va1.getId().compareTo(va2.getId()));

        List<Date> fechasPeriodos = new ArrayList();
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

            if (!fechaIniClases.equals(fechaMinPeriodo)) {
                errorPeriodoClases = Boolean.TRUE;
            }
            if (!fechaFinClases.equals(fechaMaxPeriodo)) {
                errorPeriodoClases = Boolean.TRUE;
            }

            if (!errorPeriodoClases) {
                DateTime lastFechaFin = null;

                for (int i = 0; i < fechasPeriodos.size(); i++) {
                    DateTime fechaEach = new DateTime(new DateTime(fechasPeriodos.get(i)).toLocalDate().toDate());
                    if ((i + 1) % 2 == 0) {
                        //fechas fin
                        lastFechaFin = new DateTime(new DateTime(fechasPeriodos.get(i)).toLocalDate().toDate());
                    } else //fechas ini
                    {
                        if (lastFechaFin != null) {
                            lastFechaFin = lastFechaFin.plusDays(1);
                            if (!lastFechaFin.equals(fechaEach)) {
                                errorPeriodoClases = Boolean.TRUE;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (errorPeriodoClases || errorPorcentajeCarga) {
            seccion.setSituacionDocenteEnum(SituacionDocenteEnum.ERR);
        } else {
            fixEncuesta(docentesSeccion);
            seccion.setSituacionDocenteEnum(SituacionDocenteEnum.COR);
        }
        seccionDAO.updateSituacionDocente(seccion);
    }

    @Override
    public Seccion findSeccion(Long seccionId) {
        Seccion seccion = seccionDAO.find(seccionId);

        List<HorarioSeccion> horariosSecc = horarioSeccionDAO.allBySeccion(seccion);
        seccion.setHorarioSeccion(horariosSecc);

        return seccion;
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
        if (seccion.getTotalHorasSemanales() == 0) {
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
            if (Arrays.asList("O3*", "P4*").contains(grupo.getCodigo())) {
                logger.debug("");
            }
            if (grupo.getDiaHoraGrupo().isEmpty()) {
                continue;
            }
//            System.out.println("Buscando en grupo " + grupo.getCodigo());

            loop++;
            Map<Long, Object> mapDias = TypesUtil.convertListToMapList("dia.id", grupo.getDiaHoraGrupo());
            if (existeCoincidencia(mapDias, seccion.getTotalHorasSemanales())) {
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
                if (total == horasSemanales) {
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
    public void saveSeccionGrupoHorario(Seccion seccion, GrupoHoras gpoHoras, CicloAcademico cicloAcademico) {
        Seccion seccionDB = seccionDAO.find(seccion);
        if (gpoHoras != null) {
            GrupoHoras gpoHorasBD = grupoHorasDAO.find(gpoHoras);
            if (!gpoHorasBD.isPermiteCeroHoras()) {
                Assert.isFalse(gpoHoras.getDiaHoraGrupo().isEmpty(), "Debe seleccionar las horas");
            }
        }

        ModalidadEstudio modalidadCurso = seccion.getGrupoSeccion().getCurso().getModalidadEstudio();
        EventoCicloAcademico eventoAcademico = this.getEventoDictadoClasesByCicloAcademico(seccionDB.getGrupoSeccion(), cicloAcademico, modalidadCurso);

        List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccion(seccion);
        List<HorarioAula> horariosAula = horarioAulaDAO.allBySeccionCiclo(seccion, cicloAcademico);
        if (gpoHoras == null) {
            horarioSeccionDAO.deleteAllInList(horariosSeccion);
            horarioAulaDAO.deleteAllInList(horariosAula);
            seccion.setGrupoHoras(gpoHoras);
            seccionDAO.updateColumns(seccion, "grupoHoras");
            return;
        }

        Map<Long, Hora> mapHoras = TypesUtil.convertListToMap("id", horaDAO.all());
        Map<Long, Dia> mapDias = TypesUtil.convertListToMap("id", diaDAO.all());

        for (DiaHoraGrupo horaDia : gpoHoras.getDiaHoraGrupo()) {
            horaDia.setHora(mapHoras.get(horaDia.getHora().getId()));
            horaDia.setDia(mapDias.get(horaDia.getDia().getId()));
        }

        Map<String, DiaHoraGrupo> mapDiaHoraGpo = TypesUtil.convertListToMap("horaDia", gpoHoras.getDiaHoraGrupo());
        List<String> diasHorasSeccion = gpoHoras.getDiaHoraGrupo().stream().map(x -> x.getIdDiaHora()).collect(Collectors.toList());

        if (seccion.getAula() != null && seccion.getAula().getPermiteCruceBoolean()) {
            if (diasHorasSeccion != null && !diasHorasSeccion.isEmpty()) {
                List<HorarioAula> horariosAulasFound = horarioAulaDAO
                        .allRangoDiaAndAulaByDiasHoras(diasHorasSeccion, seccion.getAula(), eventoAcademico.getFechaInicio(), eventoAcademico.getFechaFin());
                horariosAulasFound.removeIf(x -> seccion.equals(x.getSeccion()));

                List<String> errors = new ArrayList<>();
                for (HorarioAula horarioAula : horariosAulasFound) {
                    Dia dia = horarioAula.getDia();
                    Hora hora = horarioAula.getHora();
                    String seccionStr = "N.N.";
                    if (horarioAula.getSeccion() != null && horarioAula.getSeccion().getId() != null) {
                        seccionStr = horarioAula.getSeccion().getCodigo2();
                    }
                    String error = String.format("Cruce Horario (%s), Sección %s Dia %s Hora %s.",
                            horarioAula.getTipoEnum().getValue(),
                            seccionStr,
                            dia.getNombre(),
                            hora.getDescripcion()
                    );
                    errors.add(error);
                }
                if (!errors.isEmpty()) {
                    throw new PhobosException(String.join("</br>", errors));
                }
            }
        }

        //si es pcur
        if (seccion.getSeccionSuperior() != null) {
            List<HorarioSeccion> horarioTCUR = horarioSeccionDAO.allBySeccion(seccion.getSeccionSuperior());
            for (HorarioSeccion horarioSeccTCUR : horarioTCUR) {
                String horaDia = horarioSeccTCUR.getHoraDia();
                DiaHoraGrupo hdiaGpo = mapDiaHoraGpo.get(horaDia);
                if (hdiaGpo == null) {
                    continue;
                }
                Dia dia = hdiaGpo.getDia();
                Hora hora = hdiaGpo.getHora();
                throw new PhobosException("Hay cruce de horario con la teoria el " + dia.getNombre() + " a la(s) " + hora.getDescripcion());
            }
        }

        if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.TCUR) {
            List<Seccion> seccionesPCUR = seccionDAO.allByGposSeccion(seccion.getGrupoSeccion());
            seccionesPCUR.removeIf(Seccion::isTipoSeccionTCUR);

            List<HorarioSeccion> horariosPCUR = horarioSeccionDAO.allBySecciones(seccionesPCUR);
            for (HorarioSeccion horarioSecc : horariosPCUR) {
                String horaDia = horarioSecc.getHoraDia();
                Seccion seccPcur = horarioSecc.getSeccion();
                DiaHoraGrupo hdiaGpo = mapDiaHoraGpo.get(horaDia);
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
        seccion.setGrupoHoras(gpoHoras);

        List<DiaHoraGrupo> horarioGpo = gpoHoras.getDiaHoraGrupo();
        ListsInspector inspector = TypesUtil.analizeLists(horariosSeccion, horarioGpo, "horaDia");
        List<HorarioSeccion> muertosHSecc = inspector.getDeadList();
        List<DiaHoraGrupo> nuevosHSecc = inspector.getNewList();

        if (!muertosHSecc.isEmpty()) {
            horarioSeccionDAO.deleteAllInList(muertosHSecc);
        }

        Assert.isNotNull(eventoAcademico, "Debe configurar el evento de dictado de clases.");

        for (DiaHoraGrupo diaHoraGrupoEach : nuevosHSecc) {
            HorarioSeccion horarioSeccion = new HorarioSeccion();
            horarioSeccion.setDia(diaHoraGrupoEach.getDia());
            horarioSeccion.setHora(diaHoraGrupoEach.getHora());
            horarioSeccion.setSeccion(seccion);
            horarioSeccion.setAula(seccion.getAula());
            horarioSeccion.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
            horarioSeccion.setFechaInicio(eventoAcademico.getFechaInicio());
            horarioSeccion.setFechaFin(eventoAcademico.getFechaFin());
            horarioSeccionDAO.save(horarioSeccion);
        }

        inspector = TypesUtil.analizeLists(horariosAula, horarioGpo, "horaDia");
        List<HorarioAula> muertosHAula = inspector.getDeadList();
        List<DiaHoraGrupo> nuevosHAula = inspector.getNewList();
        List<HorarioAula> viejosHAula = inspector.getOldListDB();

        Date lunes = new LocalDate().withDayOfWeek(1).toDate();
        Date dominPasado = new LocalDate(lunes).minusDays(1).toDate();

        boolean horarioInterrumpido = false;
        for (HorarioAula ha : muertosHAula) {
            if (ha.getFechaInicio().before(lunes)) {
                ha.setFechaFin(dominPasado);
                horarioAulaDAO.update(ha);
                horarioInterrumpido = true;
            } else {
                horarioAulaDAO.delete(ha);
            }
        }

        Date ultimoDomin = new LocalDate(eventoAcademico.getFechaFin()).withDayOfWeek(7).toDate();
        Date domingo = new LocalDate().withDayOfWeek(7).toDate();
        Map<String, HorarioAula> mapPeriodo = TypesUtil.convertListToMap("periodo", viejosHAula);
        Map<String, List<HorarioAula>> mapHorarioByPeriodo = TypesUtil.convertListToMapList("periodo", viejosHAula);

        for (HorarioAula ha : viejosHAula) {
            if (ha.getFechaFin().after(ultimoDomin)) {
                ha.setFechaFin(eventoAcademico.getFechaFin());
                horarioAulaDAO.update(ha);
            }
        }

        if (seccion.getAula() != null) {
            for (DiaHoraGrupo diaHoraGrupoEach : nuevosHAula) {
                HorarioAula horarioAula = new HorarioAula();
                horarioAula.setAula(seccion.getAula());
                horarioAula.setDia(diaHoraGrupoEach.getDia());
                horarioAula.setHora(diaHoraGrupoEach.getHora());
                horarioAula.setSeccion(seccion);
                horarioAula.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                horarioAula.setTipoEnum(TipoHorarioAulaEnum.DICT);
                horarioAula.setFechaFin(eventoAcademico.getFechaFin());

                if (horarioInterrumpido) {
                    horarioAula.setFechaInicio(lunes);
                } else {
                    horarioAula.setFechaInicio(eventoAcademico.getFechaInicio());
                }

                horarioAulaDAO.save(horarioAula);
            }
        }

        seccionDAO.updateSeccionGrupoHora(seccion);
        this.actualizarBoletin();
        //actualizar grupo horas anterior
        if (seccionDB.getGrupoHoras() != null) {
            this.actualizarCuotaAnexo(seccionDB, seccionDB.getGrupoSeccion().getCicloAcademico());
        }
        //actualizar grupo horas actual
        this.validarCruceAlumnos(seccion);
        this.actualizarCuotaAnexo(seccion, seccionDB.getGrupoSeccion().getCicloAcademico());
    }

    private void actualizarCuotaAnexo(Seccion seccion, CicloAcademico cicloAcademico) {
        GrupoHoras gpoHoras = seccion.getGrupoHoras();
        if (gpoHoras != null && gpoHoras.getId() != null) {
            GrupoSeccion grupoSeccionBD = grupoSeccionDAO.find(seccion.getGrupoSeccion().getId());

            logger.debug("grupo horas, codigo {}, letra {}", gpoHoras.getCodigo(), gpoHoras.getLetra());
            if (StringUtils.isEmpty(gpoHoras.getLetra())) {
                gpoHoras = grupoHorasDAO.find(gpoHoras);
            }

            AnexoBoletin anexoBoletin = grupoSeccionBD.getAnexoBoletin();

            CuotasGrupoHoras cuotasGrupoHoras = cuotaGpoHorasDAO.findByAnexoAndCicloAndGpoHoras(anexoBoletin, cicloAcademico, gpoHoras.getLetra());
            if (cuotasGrupoHoras != null) {
                Integer countForCuotasGrupoHoras = cuotaGpoHorasDAO.countSeccionesByAnexoCicloLetraGpo(anexoBoletin, cicloAcademico, gpoHoras.getLetra(), gpoHoras.getTipoSeccion());
                CuotasGrupoHoras cuotasGrupoHorasUpd = new CuotasGrupoHoras();
                cuotasGrupoHorasUpd.setId(cuotasGrupoHoras.getId());

                String columna = "utilizadasTeoria";
                if (gpoHoras.getTipoSeccion().equals("TEO")) {
                    cuotasGrupoHorasUpd.setUtilizadasTeoria(countForCuotasGrupoHoras);
                } else {
                    columna = "utilizadasPractica";
                    cuotasGrupoHorasUpd.setUtilizadasPractica(countForCuotasGrupoHoras);
                }
                cuotaGpoHorasDAO.updateColumns(cuotasGrupoHorasUpd, columna);
            }
        }
    }

    @Override
    @Transactional
    public void saveAula(Seccion seccionForm, Aula aulaForm, DataSessionPivot ds) {

        Seccion seccion = seccionDAO.find(seccionForm);
        String codigoAula = aulaForm.getCodigo();
        Aula aula = null;
        if (aulaForm.getId() != null) {
            aula = aulaDAO.find(aulaForm.getId());
        } else if (!Strings.isNullOrEmpty(aulaForm.getCodigo())) {
            aula = aulaDAO.findActiveByCode(aulaForm.getCodigo());
        }
        if (StringUtils.isNotBlank(codigoAula) && aula == null) {
            throw new PhobosException("El Aula %s, no existe", codigoAula);
        }
        if (ObjectUtil.getParentTree(seccion, "aula.id") != null && aula == null) {
            horarioAulaDAO.deleteBySeccionAula(seccion, seccion.getAula());
        }

        if (aula == null) {
            Seccion seccionUpd = new Seccion(seccion.getId());
            seccionUpd.setAula(aula);
            seccionDAO.updateColumns(seccionUpd, "aula");

            List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccion(seccion);
            for (HorarioSeccion horarioSeccion : horariosSeccion) {
                horarioSeccion.setAula(aula);
                horarioSeccionDAO.update(horarioSeccion);
            }
            return;
        }

        Assert.isNotNull(aula.getCapacidadAula(), "Esta aula no tiene configurado la capacidad");

        Aula aulaAntes = seccion.getAula();
        if (aulaAntes != null) {
            if (seccion.getAula().getId().compareTo(aula.getId()) == 0) {
                throw new PhobosException("Esta sección ya tiene asignada esta aula.");
            }
        }
        Assert.isNotNull(seccion.getVacantes(), "La sección no tiene vacantes, verifique.");
        if (seccion.getVacantes() != null) {
            int vacantes = TypesUtil.getInt(seccion.getVacantes(), 0);
            int matriculados = TypesUtil.getInt(seccion.getMatriculados(), 0);
            String compareStr = "vacantes";
            int compareInt = vacantes;
            if (matriculados != 0) {
                compareStr = "matriculados";
                compareInt = matriculados;
            }

            if (compareInt > aula.getCapacidadTotal()) {
                throw new PhobosException("La capacidad del aula no abarca %s de la sección.", compareStr);
            }
        }

        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        List<HorarioAula> horariosAulas = horarioAulaDAO.allByAula(aula, cicloAcademico);
        List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccion(seccion);

        ModalidadEstudio modalidadCurso = seccion.getGrupoSeccion().getCurso().getModalidadEstudio();
        EventoCicloAcademico eventoAcademico = this.getEventoDictadoClasesByCicloAcademico(seccion.getGrupoSeccion(), cicloAcademico, modalidadCurso);

        Date fechaInicioClases = new DateTime(eventoAcademico.getFechaInicio()).withDayOfWeek(1).toDate();
        Date hoy = new LocalDate().toDate();
        Date lunes = new DateTime(hoy).withDayOfWeek(1).toDate();
        if (lunes.after(fechaInicioClases)) {
            fechaInicioClases = lunes;
        }

        if (aula.getPermiteCruce() == 0 && !horariosSeccion.isEmpty()) {
            logger.debug("no permite cruce horario");
            List<String> diasHorasSeccion = horariosSeccion.stream().map(x -> x.getIdDiaHora()).collect(Collectors.toList());

            List<HorarioAula> horariosAulasFound = horarioAulaDAO.allRangoDiaAndAulaByDiasHoras(
                    diasHorasSeccion,
                    aula,
                    fechaInicioClases,
                    eventoAcademico.getFechaFin());

            horariosAulasFound.removeIf(x -> seccionForm.equals(x.getSeccion()));
            if (!horariosAulasFound.isEmpty()) {
                List<String> cruces = new ArrayList();
                for (HorarioAula horarioAula : horariosAulasFound) {
                    String msg = String.format("Tipo %s", horarioAula.getTipoEnum().name());
                    if (horarioAula.getSeccion() != null) {
                        msg = String.format("Sección %s", horarioAula.getSeccion().getCodigo2());
                    }
                    String cruce = String.format("*%s Día %s, Hora %s",
                            msg,
                            horarioAula.getDia().getSimbolo(),
                            horarioAula.getHora().getDescripcion());

                    cruces.add(cruce);
                }
                String secciones = String.join("</br>", cruces);
                throw new PhobosException("Cruce horario con : </br>" + secciones);
            }
        }

        if (aulaAntes != null) {
            horarioAulaDAO.deleteBySeccionAula(seccion, aulaAntes);
        }

        if (aula.getPermiteCruce() == 0) {
            LOOP_HORARIO_SECCION:
            for (HorarioSeccion horarioSeccionEach : horariosSeccion) {
                horarioSeccionEach.setAula(aula);
                horarioSeccionDAO.update(horarioSeccionEach);

                for (HorarioAula horarioAulaEach : horariosAulas) {
                    if (horarioSeccionEach.getHoraDia().equals(horarioAulaEach.getHoraDia())) {
                        continue LOOP_HORARIO_SECCION;
                    }
                }

                HorarioAula horarioAula = new HorarioAula();
                horarioAula.setAula(aula);
                horarioAula.setDia(horarioSeccionEach.getDia());
                horarioAula.setHora(horarioSeccionEach.getHora());
                horarioAula.setSeccion(seccion);
                horarioAula.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                horarioAula.setTipoEnum(TipoHorarioAulaEnum.DICT);

                horarioAula.setFechaInicio(fechaInicioClases);
                horarioAula.setFechaFin(eventoAcademico.getFechaFin());

                horarioAulaDAO.save(horarioAula);
            }
        }

        Seccion seccionUpd = new Seccion(seccion.getId());
        seccionUpd.setAula(aula);
        seccionDAO.updateColumns(seccionUpd, "aula");
        this.actualizarBoletin();
        this.actualizarCuotaAnexo(seccion, seccion.getGrupoSeccion().getCicloAcademico());
    }

    public void validarCruceAlumnos(Seccion seccion) {
        List<HorarioSeccion> horariosBySeccion = horarioSeccionDAO.allBySeccion(seccion);
        seccion.setHorarioSeccion(horariosBySeccion);

        List<MatriculaSeccion> matriculasSeccionBySeccion = matriculaSeccionDAO.allMatriculadosBySeccion(Arrays.asList(seccion), EstadoMatriculaEnum.MAT, EstadoMatriculaEnum.PMAT);
        List<MatriculaResumen> matriculasResumenes = matriculasSeccionBySeccion.stream()
                .map(x -> x.getMatriculaResumen()).distinct().collect(Collectors.toList());

        List<MatriculaSeccion> matriculasSecciones = matriculaSeccionDAO.allMatriculadosByMatriculaSeccion(matriculasResumenes, EstadoMatriculaEnum.MAT, EstadoMatriculaEnum.PMAT);
        List<Seccion> secciones = matriculasSecciones.stream().map(x -> x.getSeccion()).collect(Collectors.toList());

        List<HorarioSeccion> horariosSecciones = horarioSeccionDAO.allBySecciones(secciones);
        Map<Long, List<HorarioSeccion>> mapHorarioSeccion = TypesUtil.convertListToMapList("seccion.id", horariosSecciones);

        List<String> errors = new ArrayList<>();

        matriculasSecciones.removeIf(x -> x.getSeccion().equals(seccion));
        for (MatriculaSeccion matriculaSeccion : matriculasSecciones) {
            List<HorarioSeccion> horariosBySeccionEach = mapHorarioSeccion.get(matriculaSeccion.getSeccion().getId());
            if (horariosBySeccionEach == null || horariosBySeccionEach.isEmpty()) {
                continue;
            }
            Map<String, List<HorarioSeccion>> mapHorarioSeccionByHor = TypesUtil.convertListToMapList("horaDia", horariosBySeccionEach);

            for (HorarioSeccion horarioSeccion : seccion.getHorarioSeccion()) {
                String horaDia = horarioSeccion.getHoraDia();
                List<HorarioSeccion> hdiaGpo = mapHorarioSeccionByHor.get(horaDia);
                if (hdiaGpo == null || hdiaGpo.isEmpty()) {
                    continue;
                }
                for (HorarioSeccion horarioSeccion1 : hdiaGpo) {
                    Dia dia = horarioSeccion1.getDia();
                    Hora hora = horarioSeccion1.getHora();
                    String error = String.format("Cruce Horario, Dia %s, Hora %s, Seccion %s, Alumno %s",
                            dia.getNombre(), hora.getDescripcion(),
                            matriculaSeccion.getSeccion().getCodigo2(),
                            matriculaSeccion.getMatriculaResumen().getAlumno().getCodigo()
                    );
                    errors.add(error);
                }

            }
        }
        if (!errors.isEmpty()) {
            throw new PhobosException(String.join("</br>", errors));
        }
    }

    @Override
    @Transactional
    public void saveRestriccion(Seccion seccion, DataSessionPivot ds, TipoRestriccionEnum tipoRestriccionEnum, List<Long> restricciones) {
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
                    restriccionCarreraEach.setUsuarioModificacion(ds.getUsuario());
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
                    restriccionCarrera.setUsuarioRegistro(ds.getUsuario());
                    restriccionCarrera.setSeccion(seccion);
                    restriccionCarreraDAO.save(restriccionCarrera);
                }
            }

            for (RestriccionFacultad restriccionFacultadEach : restriccionesFacultad) {
                restriccionFacultadEach.setEstadoEnum(EstadoEnum.INA);
                restriccionFacultadEach.setFechaModificacion(today.toDate());
                restriccionFacultadEach.setUsuarioModificacion(ds.getUsuario());
                restriccionFacultadDAO.updateEstadoFechaUsuario(restriccionFacultadEach);
            }

            for (RestriccionModalidad restriccionModalidadEach : restriccionesModalidad) {
                restriccionModalidadEach.setEstadoEnum(EstadoEnum.INA);
                restriccionModalidadEach.setFechaModificacion(today.toDate());
                restriccionModalidadEach.setUsuarioModificacion(ds.getUsuario());
                restriccionModalidadDAO.updateEstadoFechaUsuario(restriccionModalidadEach);
            }

        } else if (tipoRestriccionEnum.equals(TipoRestriccionEnum.FAC)) {
            List<Facultad> facultadesSeleccionadas = new ArrayList();
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
                    restriccionFacultadEach.setUsuarioModificacion(ds.getUsuario());
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
                    restriccionFacultad.setUsuarioRegistro(ds.getUsuario());
                    restriccionFacultad.setSeccion(seccion);
                    restriccionFacultadDAO.save(restriccionFacultad);
                }
            }

            for (RestriccionCarrera restriccionCarreraEach : restriccionesCarrera) {
                restriccionCarreraEach.setEstadoEnum(EstadoEnum.INA);
                restriccionCarreraEach.setFechaModificacion(today.toDate());
                restriccionCarreraEach.setUsuarioModificacion(ds.getUsuario());
                restriccionCarreraDAO.updateEstadoFechaUsuario(restriccionCarreraEach);
            }

            for (RestriccionModalidad restriccionModalidadEach : restriccionesModalidad) {
                restriccionModalidadEach.setEstadoEnum(EstadoEnum.INA);
                restriccionModalidadEach.setFechaModificacion(today.toDate());
                restriccionModalidadEach.setUsuarioModificacion(ds.getUsuario());
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
                    restriccionaModalidadEach.setUsuarioModificacion(ds.getUsuario());
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
                    restriccionModalidad.setUsuarioRegistro(ds.getUsuario());
                    restriccionModalidad.setSeccion(seccion);
                    restriccionModalidadDAO.save(restriccionModalidad);
                }
            }

            for (RestriccionCarrera restriccionCarreraEach : restriccionesCarrera) {
                restriccionCarreraEach.setEstadoEnum(EstadoEnum.INA);
                restriccionCarreraEach.setFechaModificacion(today.toDate());
                restriccionCarreraEach.setUsuarioModificacion(ds.getUsuario());
                restriccionCarreraDAO.updateEstadoFechaUsuario(restriccionCarreraEach);
            }

            for (RestriccionFacultad restriccionFacultadEach : restriccionesFacultad) {
                restriccionFacultadEach.setEstadoEnum(EstadoEnum.INA);
                restriccionFacultadEach.setFechaModificacion(today.toDate());
                restriccionFacultadEach.setUsuarioModificacion(ds.getUsuario());
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

        Seccion seccionDB = seccionDAO.find(seccion);
        ModalidadEstudio modalidadCurso = seccionDB.getGrupoSeccion().getCurso().getModalidadEstudio();
        EventoCicloAcademico eventoAcademico = this.getEventoDictadoClasesByCicloAcademico(seccionDB.getGrupoSeccion(), cicloAcademico, modalidadCurso);
        logger.debug("***eventoAcademico*** {}", eventoAcademico != null);
        if (eventoAcademico != null) {
            logger.debug("***eventoAcademico {}", eventoAcademico.getId());
            logger.debug("***inicio  {} fin {}", eventoAcademico.getFechaInicio(), eventoAcademico.getFechaFin());
        }

        List<HorarioAula> horariosAula = new ArrayList();
        logger.debug("***pabellon *** {}", pabellon.getId());
        if (!diaHoras.isEmpty()) {
            if (eventoAcademico != null) {
                horariosAula = horarioAulaDAO.allByPabellonCicloDiasHoras(pabellon, eventoAcademico, diaHoras);
            }
        }
        logger.debug("***horariosAula existe *** {}", horariosAula.isEmpty());

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
        this.completarDocentes(horariosAulas);
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
        return this.findGrupoHorasWithHorario(seccion, grupoHoras, ciclo);
    }

    @Override
    public GrupoHoras findGrupoHorasWithHorario(Seccion seccion, GrupoHoras grupoHoras, CicloAcademico ciclo) {
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
        List<EventoAcademicoEnum> eventos = Arrays.asList(EventoAcademicoEnum.CLASES_PRE, EventoAcademicoEnum.CLASES_VER);
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
    public EventoCicloAcademico findEventoAcademico(CicloAcademico cicloAcademico, AnexoBoletin anexoSup) {
        return this.getEventoDictadoClases(cicloAcademico, anexoSup);
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
    public List<DiaHoraGrupo> searchDiasHorasByHorasSemanales(List<DiaHoraGrupo> diasHorasGrupo, Integer horasSemanales, List<Dia> dias) {
        return this.searchDiasHorasByHorasSemanales(diasHorasGrupo, horasSemanales, dias, true);
    }

    @Override
    public List<DiaHoraGrupo> searchDiasHorasByHorasSemanales(List<DiaHoraGrupo> diasHorasGrupo, Integer horasSemanales, List<Dia> dias, boolean throwError) {
        if (horasSemanales == 0) {
            if (throwError) {
                throw new PhobosException("Esta sección no puede asignarse un grupo con horas semanales");
            }
        }

        if (diasHorasGrupo.isEmpty()) {
            return new ArrayList();
        }

        Collections.sort(diasHorasGrupo, (p1, p2) -> p1.getHora().getNumero().compareTo(p2.getHora().getNumero()));
        Map<Long, Object> mapDias = TypesUtil.convertListToMapList("dia.id", diasHorasGrupo);

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
            if (throwError) {
                throw new PhobosException("Grupo Horario no es compatible con las horas semanales de la sección");
            }
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
        GrupoSeccion gpoSeccBD = grupoSeccionDAO.find(grupoSeccion.getId());
        Curso curso = gpoSeccBD.getCurso();
        CicloAcademico ciclo = gpoSeccBD.getCicloAcademico();
        CursoCicloAcademico cursoCiclo = this.findCursoCicloAcademico(curso, ciclo);

        EventoCicloAcademico eventoDictadoClases = getEventoDictadoClases(ciclo, gpoSeccBD.getAnexoBoletin().getAnexoSuperior());
        List<String> codigosByCiclo = grupoSeccionDAO.allCodigoByCiclo(ciclo);
        List<String> codigos2ByCiclo = grupoSeccionDAO.allCodigo2ByCiclo(ciclo);

        int horasTeoria = getHorasCurso(curso, ciclo, TipoSeccionEnum.TEO);
        int horasPractica = getHorasCurso(curso, ciclo, TipoSeccionEnum.PRA);

        Docente docenteDefault = docenteDAO.findByCode(Constantine.DOCENTE_INDETERMINADO);

        List<GrupoSeccion> gpoSeccClones = new ArrayList<>();
        for (int i = 0; i < veces; i++) {
            GrupoSeccion gpoSeccClon = new GrupoSeccion();

            String codigo = CodeGenerator.getNextCode(codigosByCiclo, 0);
            String codigo2 = CodeGenerator.getNextCode(codigos2ByCiclo, 0);

            codigosByCiclo.add(codigo);
            codigos2ByCiclo.add(codigo2);

            gpoSeccClon.setCurso(curso);
            gpoSeccClon.setCodigo(codigo);
            gpoSeccClon.setCodigo2(codigo2);
            gpoSeccClon.setVersion(BigDecimal.ONE.toString());
            gpoSeccClon.setEstadoGrupoEnum(EstadoGrupoSeccionEnum.ABI);
            gpoSeccClon.setEstadoPlanEnum(EstadoPlanCalificaEnum.PEND);
            gpoSeccClon.setEstadoEnum(SeccionEstadoEnum.ACT);
            gpoSeccClon.setCicloAcademico(ciclo);
            gpoSeccClon.setEstadoEnum(gpoSeccBD.getEstadoEnum());
            gpoSeccClon.setAnexoBoletin(gpoSeccBD.getAnexoBoletin());
            gpoSeccClon.setHorasPractica(horasPractica);
            gpoSeccClon.setHorasTeoria(horasTeoria);

            DateTime today = new DateTime();

            gpoSeccClon.setSecciones(new ArrayList<Seccion>());

            if (curso.isTipoCursoTEO()) {
                Seccion seccionTEO = new Seccion();
                seccionTEO.setCodigo(gpoSeccClon.getCodigo() + "0");
                seccionTEO.setCodigo2(seccionTEO.getCodigo());
                createSeccion(seccionTEO, TipoSeccionEnum.TEO, grupoSeccion, null, horasTeoria, cursoCiclo, ds);

                DocenteSeccion docenteSeccion = new DocenteSeccion();
                createDocenteSeccion(docenteSeccion, docenteDefault, seccionTEO, eventoDictadoClases);

                gpoSeccClon.getSecciones().add(seccionTEO);
            }

            if (curso.isTipoCursoPRA()) {
                Seccion seccionPRA = new Seccion();
                seccionPRA.setCodigo(codigo + "1");
                seccionPRA.setCodigo2(seccionPRA.getCodigo());
                createSeccion(seccionPRA, TipoSeccionEnum.PRA, grupoSeccion, null, horasPractica, cursoCiclo, ds);

                DocenteSeccion docenteSeccion = new DocenteSeccion();
                createDocenteSeccion(docenteSeccion, docenteDefault, seccionPRA, eventoDictadoClases);

                gpoSeccClon.getSecciones().add(seccionPRA);
            }

            if (curso.isTipoCursoTEOPRA()) {
                Seccion seccionTCUR = new Seccion();
                seccionTCUR.setCodigo(codigo + "0");
                seccionTCUR.setCodigo2(seccionTCUR.getCodigo());
                createSeccion(seccionTCUR, TipoSeccionEnum.TCUR, grupoSeccion, null, horasTeoria, null, ds);

                DocenteSeccion docenteSeccion = new DocenteSeccion();
                createDocenteSeccion(docenteSeccion, docenteDefault, seccionTCUR, eventoDictadoClases);

                gpoSeccClon.getSecciones().add(seccionTCUR);

                Seccion seccionPCUR = new Seccion();
                seccionPCUR.setCodigo(codigo + "1");
                seccionPCUR.setCodigo2(seccionPCUR.getCodigo());
                seccionPCUR.setSeccionSuperior(seccionTCUR);
                createSeccion(seccionPCUR, TipoSeccionEnum.PCUR, grupoSeccion, null, horasPractica, cursoCiclo, ds);

                DocenteSeccion docenteSeccion2 = new DocenteSeccion();
                createDocenteSeccion(docenteSeccion2, docenteDefault, seccionPCUR, eventoDictadoClases);

                gpoSeccClon.getSecciones().add(seccionPCUR);
            }

            grupoSeccionDAO.save(gpoSeccClon);
            for (Seccion seccion : gpoSeccClon.getSecciones()) {
                seccionDAO.save(seccion);
                for (DocenteSeccion docSecc : seccion.getDocenteSeccion()) {
                    docenteSeccionDAO.save(docSecc);
                }
            }

            gpoSeccClones.add(gpoSeccClon);

        }
        return gpoSeccClones;
    }

    private Integer getHorasCurso(Curso curso, CicloAcademico ciclo, TipoSeccionEnum tipoSecc) {
        int factorHoras = 0;
        if (ciclo.getTipoEnum() == TipoCicloEnum.REG) {
            factorHoras = 1;
        } else if (ciclo.getTipoEnum() == TipoCicloEnum.NIV) {
            factorHoras = 3;
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
        if (tipoSecc == TipoSeccionEnum.PCUR || tipoSecc == TipoSeccionEnum.PRA) {
            return horasPractica;
        }
        if (tipoSecc == TipoSeccionEnum.TCUR || tipoSecc == TipoSeccionEnum.TEO) {
            return horasTeoria;
        }
        throw new PhobosException("Error en la estructura del curso " + curso.getCodigo() + " " + curso.getNombre());
    }

    private EventoCicloAcademico getEventoDictadoClases(CicloAcademico ciclo, AnexoBoletin anexoSup) {
        System.out.println("anexoSup.codigo=" + anexoSup.getCodigo());
        if (anexoSup.isAnexoCursosPostgrado()) {
            System.out.println("return evento-epg");
            return eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(ciclo, CLASES_EPG);
        }
        EventoAcademicoEnum eventoClasesEnum = ciclo.isTipoRegular() ? CLASES_PRE : CLASES_VER;
        System.out.println("ciclo={id:" + ciclo.getId() + ", codigo:" + ciclo.getCodigo() + ", tipo:" + ciclo.getTipo() + "}");
        System.out.println("return evento-" + eventoClasesEnum.name());
        return eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(ciclo, eventoClasesEnum);
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
    @Transactional
    public void actualizarBoletin() {
        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivoPregrado();
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

    private EventoCicloAcademico getEventoDictadoClasesByCicloAcademico(GrupoSeccion gpoSeccion, CicloAcademico ciclo, ModalidadEstudio modalidad) {
        if (gpoSeccion.getTipoDictadoEnum() == MOD) {
            EventoCicloAcademico eventoModular = new EventoCicloAcademico();
            eventoModular.setFechaInicio(gpoSeccion.getFechaInicioModular());
            eventoModular.setFechaFin(gpoSeccion.getFechaFinModular());
            return eventoModular;
        }
        EventoAcademicoEnum eventoEnum = ciclo.getTipoEnum() == TipoCicloEnum.NIV ? CLASES_VER
                : (modalidad.isPostgrado() ? CLASES_EPG : (modalidad.isPregrado() ? CLASES_PRE : null));
        Assert.isNotNull(eventoEnum, "No se ha encontrado algun evento de clases.");
        EventoCicloAcademico eventoCiclo = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(ciclo, eventoEnum);
        return eventoCiclo;
    }

    @Override
    @Transactional
    public CursoCicloAcademico findCursoCicloAcademico(Curso cursoForm, CicloAcademico cicloForm) {
        CursoCicloAcademico cca = cursoCicloAcademicoDAO.findByCursoCiclo(cursoForm, cicloForm);
        if (cca != null) {
            return cca;
        }

        Curso cursoBD = cursoDAO.find(cursoForm.getId());
        CicloAcademico cicloBD = cicloAcademicoDAO.find(cicloForm);

        List<CursoCurricula> cursosCurricula = cursoCurriculaDAO.allByCursoTipoCurriculaEnum(cursoBD, TipoCursoCurriculaEnum.GEN);
        TipoCursoCurricula tipoCursoGeneral = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.GEN);
        TipoCursoCurricula tipoCursoObligatorio = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.OBL);

        PrecioCursoEstructura precioTpc = precioCursoEstructuraDAO.findByTpcCiclo(cursoBD.getTpc(), cicloBD);
        TipoCursoCurricula tipoCurr = tipoCursoObligatorio;
        int minimoAlumnos = cicloBD.getAlumnosMinimoTipoObligatorio() == null ? 0 : cicloBD.getAlumnosMinimoTipoObligatorio();
        if (cursosCurricula.isEmpty()) {
            minimoAlumnos = cicloBD.getAlumnosMinimoTipoGeneral() == null ? 0 : cicloBD.getAlumnosMinimoTipoGeneral();
            tipoCurr = tipoCursoGeneral;
        }

        cca = new CursoCicloAcademico();
        cca.setCurso(cursoBD);
        cca.setCicloAcademico(cicloForm);
        cca.setMinimoAlumnos(new BigDecimal(minimoAlumnos));
        cca.setTipoCursoCurricula(tipoCurr);
        cca.setPrecio(precioTpc == null ? BigDecimal.ZERO : precioTpc.getPrecio());
        cca.setPrecioAdicional(BigDecimal.ZERO);
        cca.setPrecioPersonalizado(false);
        cca.setEstado("ACT");

        if (cicloBD.getTipoEnum() == TipoCicloEnum.REG) {
            cca.setHorasSemanalesTeoria(cursoBD.getHorasTeoria());
            cca.setHorasSemanalesPractica(cursoBD.getHorasPractica());

        } else if (cicloBD.getTipoEnum() == TipoCicloEnum.NIV) {
            cca.setHorasSemanalesTeoria(cursoBD.getHorasTeoriaVerano());
            cca.setHorasSemanalesPractica(cursoBD.getHorasPracticaVerano());
        }
        cursoCicloAcademicoDAO.save(cca);

        return cca;
    }

    @Override
    @Transactional
    public void recrearVacanteAlumno(CicloAcademico ciclo, DataSessionPivot ds) {
        List<Seccion> secciones = seccionDAO.allByCiclo(ciclo);
        List<VacanteAlumno> vacAlumnos = vacanteAlumnoDAO.allActivoBySecciones(secciones);
        Map<Long, List<VacanteAlumno>> mapVacAlumnos = TypesUtil.convertListToMapList("seccion.id", vacAlumnos);

        for (Seccion secc : secciones) {
            if (secc.getEstadoEnum() != SeccionEstadoEnum.ACT) {
                continue;
            }
            List<VacanteAlumno> vacAluSecc = mapVacAlumnos.get(secc.getId());
            vacAluSecc = (vacAluSecc == null) ? new ArrayList() : vacAluSecc;
            Map<Integer, VacanteAlumno> mapVacAluSecc = TypesUtil.convertListToMap("numero", vacAluSecc);
            for (int i = 1; i < secc.getVacantes() + 1; i++) {
                VacanteAlumno va  = mapVacAluSecc.get(i);
                if (va  != null) {
                    continue;
                }
                va  = new VacanteAlumno();
                va.setNumero(i);
                va.setSeccion(secc);
                va.setEstadoEnum(EstadoVacanteAlumnoEnum.DISP);
                va.setFechaRegistro(new Date());
                va.setUserRegistro(ds.getUsuario());
                va.setActivo(1);
                vacanteAlumnoDAO.save(va);
            }

        }

    }

    private void completarDocentes(List<HorarioAula> horariosAulas) {
        List<Seccion> secciones = horariosAulas.stream().map(x -> x.getSeccion()).collect(Collectors.toList());
        List<DocenteSeccion> docenteSecciones = docenteSeccionDAO.allActivosBySeccionesOrderPrincipalLimit(secciones);
        Map<Long, List<DocenteSeccion>> docenteSeccionesMap = TypesUtil.convertListToMapList("seccion.id", docenteSecciones);
        for (HorarioAula horariosAula : horariosAulas) {
            Seccion seccion = horariosAula.getSeccion();
            if (seccion != null) {
                seccion.setDocenteSeccion(null);
                List<DocenteSeccion> docenteSeccioness = docenteSeccionesMap.get(seccion.getId());
                seccion.setSizeDocente(docenteSeccioness != null ? docenteSeccioness.size() : 0);
                if (docenteSeccioness != null) {
                    if (docenteSeccioness.size() > 2) {
                        List<DocenteSeccion> misdocentes = new ArrayList();
                        misdocentes.add(docenteSeccioness.get(0));
                        misdocentes.add(docenteSeccioness.get(1));
                        docenteSeccioness = misdocentes;
                    }
                }
                seccion.setDocenteSeccion(docenteSeccioness);
            }
        }
    }

    @Override
    public List<RestriccionRepitencia> allRestriccionRepitenciaActivasBySeccion(Seccion seccion) {
        List<RestriccionRepitencia> restricciones = restriccionRepitenciaDAO.allActivasBySeccion(seccion);
        return restricciones;
    }

    @Override
    public GrupoSeccion findByCursoAndDocenteDirigido(Curso curso, Docente docenteAsignado, CicloAcademico academico) {
        List<GrupoSeccion> gruposSecciones = grupoSeccionDAO.allByCursoAndDirigido(curso, academico);
        if (gruposSecciones.isEmpty()) {
            return null;
        }

        GrupoSeccion grupoSeccion = null;

        for (GrupoSeccion gpoSecc : gruposSecciones) {
            List<Seccion> seccions = seccionDAO.allActivosByGpoSeccion(gpoSecc);
            List<DocenteSeccion> docenteSeccions = docenteSeccionDAO.allActivosBySecciones(seccions);

            if (docenteSeccions.stream().allMatch(x -> Objects.equals(x.getDocente().getId(), docenteAsignado.getId()))) {
                grupoSeccion = gpoSecc;
                break;
            }
        }

        return grupoSeccion;
    }

    @Override
    @Transactional(readOnly = false)
    public void eliminarGrupos(List<GrupoSeccion> gruposSeccion, DataSessionPivot ds) {
        List<String> errors = new ArrayList<>();

        List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allByGrupoSeccion(gruposSeccion, EstadoMatriculaEnum.MAT, EstadoMatriculaEnum.RET);
        List<Seccion> seccionesWithMatriculados = matriculasSeccion.stream().map(x -> x.getSeccion()).distinct().collect(Collectors.toList());
        for (Seccion seccionMat : seccionesWithMatriculados) {
            String message = "Seccion %s, ya tiene matriculados";
            message = String.format(message, seccionMat.getCodigo2());
            errors.add(message);
        }
        if (!errors.isEmpty()) {
            throw new PhobosException(String.join("\n", errors));
        }

        List<AlumnoEvaluacion> alumnosEvaluacion = alumnoEvaluacionDAO.allByGrupoSeccion(gruposSeccion);
        List<Seccion> seccionesWithAlumnoEvaluacion = alumnosEvaluacion.stream().map(x -> x.getEvaluacion().getSeccionResponsable()).collect(Collectors.toList());
        for (Seccion seccionWithEva : seccionesWithAlumnoEvaluacion) {
            String message = "Seccion %s, tiene notas registradas";
            message = String.format(message, seccionWithEva.getCodigo2());
            errors.add(message);
        }
        if (!errors.isEmpty()) {
            throw new PhobosException(String.join("\n", errors));
        }

        List<Evaluacion> evaluacionesByGrupos = evaluacionDAO.allByGruposSecciones(gruposSeccion);
        for (Evaluacion evaluacion : evaluacionesByGrupos) {
            evaluacionDAO.delete(evaluacion);
        }

        List<Seccion> secciones = seccionDAO.allByGrupoSecciones(gruposSeccion);
        Map<Long, List<Seccion>> seccionesGroupByGrupoSeccion = TypesUtil.convertListToMapList("grupoSeccion.id", secciones);

        for (GrupoSeccion grupoSeccion : gruposSeccion) {
            List<Seccion> seccionesByGpoSeccion = seccionesGroupByGrupoSeccion.get(grupoSeccion.getId());
            Seccion seccionTCUR = null;
            if (grupoSeccion.getCurso().isTipoCursoTEOPRA()) {
                Seccion seccionTCURR = seccionesByGpoSeccion.stream().filter(x -> x.getIsTipoSeccionTCUR()).findFirst().orElse(null);
                seccionTCUR = seccionTCURR;
                seccionesByGpoSeccion.removeIf(x -> x.equals(seccionTCURR));
            }
            for (Seccion seccion : seccionesByGpoSeccion) {
                this.deleteHorarioSeccion(seccion);
                this.deleteDependenciasSeccion(seccion);
                seccionDAO.delete(seccion);
            }
            if (seccionTCUR != null) {
                this.deleteHorarioSeccion(seccionTCUR);
                this.deleteDependenciasSeccion(seccionTCUR);
                seccionDAO.delete(seccionTCUR);
            }
        }
        for (GrupoSeccion grupoSeccion : gruposSeccion) {
            evaluacionExpandidaDAO.deleteByGrupoSeccion(grupoSeccion);
            evaluacionSeccionDAO.deleteByGrupoSeccion(grupoSeccion);
            GrupoSeccion gs = grupoSeccionDAO.find(grupoSeccion.getId());
            grupoSeccionDAO.delete(gs);
        }
    }

    @Override
    @Transactional
    public void solucionarCruzados(CicloAcademico cicloAcademico) {
        int cantidadCruces = 0;
        List<Seccion> seccionesCruzadas = this.allSeccionesConCruce(cicloAcademico);
        Map<String, List<Seccion>> seccionesGroupByGpoHoraAndAula = TypesUtil.convertListToMapList("horarioAndAula", seccionesCruzadas);

//        Map<String, List<Seccion>> seccionesGroupByGpoHoraAndAula = TypesUtil.convertListToMapList("horariosConcat", seccionesCruzadas);
        for (Map.Entry<String, List<Seccion>> entry : seccionesGroupByGpoHoraAndAula.entrySet()) {
            String horarioAula = entry.getKey();
            logger.debug("key " + horarioAula);
            List<Seccion> secciones = entry.getValue();
            if (secciones.size() == 1) {
                continue;
            }
            cantidadCruces = cantidadCruces + secciones.size();
            logger.debug("Seccion {}, Cruces {}", horarioAula, secciones.size());
            int idx = 0;
            for (Seccion seccion : secciones) {
                if (idx == 0) {
                    idx++;
                    continue;
                }

                Seccion seccionUpd = new Seccion(seccion.getId());
                seccionUpd.setAula(null);
                seccionUpd.setAulaBorradaPorCruce(Boolean.TRUE);
                seccionUpd.setAulaBorrada(seccion.getAula());

                horarioAulaDAO.deleteBySeccionAula(seccion, seccion.getAula());
                seccionDAO.updateColumns(seccionUpd, "aula", "aulaBorradaPorCruce", "aulaBorrada");
                for (HorarioSeccion hSec : seccion.getHorarioSeccion()) {
                    hSec.setAula(null);
                    horarioSeccionDAO.update(hSec);
                }
            }
        }
        logger.debug("cantidad cruces {}", cantidadCruces);
    }

    public List<Seccion> allSeccionesConCruce(CicloAcademico cicloAcademico) {
        List<Seccion> secciones = seccionDAO.allConCruceHorario(cicloAcademico);
        if (secciones == null || secciones.isEmpty()) {
            return new ArrayList<>();
        }
        List<GrupoHoras> gruposHoras = secciones.stream().map(x -> x.getGrupoHoras()).distinct().collect(Collectors.toList());
        List<DiaHoraGrupo> diasHorasGrupos = diaHoraGrupoDAO.allByGruposCiclo(gruposHoras, cicloAcademico);
        for (GrupoHoras gruposHora : gruposHoras) {
            List<DiaHoraGrupo> diasHorasGruposByGpoHoras = diasHorasGrupos.stream()
                    .filter(x -> x.getGrupoHorario().equals(gruposHora))
                    .collect(Collectors.toList());
            gruposHora.setDiaHoraGrupo(diasHorasGruposByGpoHoras);
        }

        List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccionesSortByDiaHora(secciones);
        horariosSeccion = horariosSeccion.stream().filter(x -> x.isEstadoActivo()).collect(Collectors.toList());

        List<HorarioAula> horarioAulas = horarioAulaDAO.allBySeccionesSortByDiaHora(secciones, cicloAcademico);
        horarioAulas = horarioAulas.stream().filter(x -> x.isEstadoActivo()).collect(Collectors.toList());

        List<DocenteSeccion> docenteSeccions = docenteSeccionDAO.allPrincipalesBySecciones(secciones);

        for (Seccion seccion : secciones) {
            GrupoHoras grupoHorasBySeccion = gruposHoras.stream().filter(x -> x.equals(seccion.getGrupoHoras())).findFirst().orElse(null);
            List<HorarioSeccion> horarioSeccionBySeccion = horariosSeccion.stream().filter(x -> x.getSeccion().equals(seccion)).collect(Collectors.toList());
            List<HorarioAula> horariosAulasBySeccion = horarioAulas.stream().filter(x -> x.getSeccion().equals(seccion)).collect(Collectors.toList());
            List<DocenteSeccion> docentesSeccionBySeccion = docenteSeccions.stream().filter(x -> x.getSeccion().equals(seccion)).collect(Collectors.toList());
            if (!docentesSeccionBySeccion.isEmpty() && docentesSeccionBySeccion.size() == 1) {
                seccion.setDocentePrincipal(docentesSeccionBySeccion.get(0).getDocente());
            }
            seccion.setGrupoHoras(grupoHorasBySeccion);
            seccion.setHorarioSeccion(horarioSeccionBySeccion);
            seccion.setHorariosAula(horariosAulasBySeccion);
            seccion.setDocenteSeccion(docentesSeccionBySeccion);
        }
        return secciones;
    }

    @Override
    @Transactional
    public void saveDescuento(DescuentoSeccionVerano descuentoSeccionVeranoForm, DataSessionPivot ds) {
        DescuentoSeccionVerano descuentoSeccionVeranoDB = descuentoSeccionVeranoDAO.findSeccion(descuentoSeccionVeranoForm.getSeccion());

        if (descuentoSeccionVeranoDB != null) {
            descuentoSeccionVeranoDB.setEstadoEnum(EstadoEnum.ANU);
            descuentoSeccionVeranoDAO.update(descuentoSeccionVeranoDB);
        }

        descuentoSeccionVeranoForm.setEstadoEnum(EstadoEnum.ACT);
        descuentoSeccionVeranoForm.setUserRegistro(ds.getUsuario());
        descuentoSeccionVeranoForm.setFechaRegistro(new Date());
        descuentoSeccionVeranoDAO.save(descuentoSeccionVeranoForm);

        Seccion seccionBD = seccionDAO.find(descuentoSeccionVeranoForm.getSeccion());
        Seccion seccionUpd = new Seccion(seccionBD.getId());
        seccionUpd.setDescuentoPrecio(descuentoSeccionVeranoForm.getMonto());
        seccionUpd.setDevolucion(0);
        seccionDAO.updateColumns(seccionUpd, "descuentoPrecio", "devolucion");

    }

    @Override
    @Transactional
    public void saveAlumnoelegido(AlumnoPagoVerano alumnoPagoVeranoForm, DataSessionPivot ds) {
        Usuario usuario = ds.getUsuario();
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        Alumno alumno = alumnoDAO.find(alumnoPagoVeranoForm.getAlumno());

        Seccion seccionBD = seccionDAO.find(alumnoPagoVeranoForm.getSeccion());
        Seccion seccion = new Seccion(seccionBD.getId());
        seccion.setAlumnoPagador(alumno);
        seccion.setDevolucion(0);
        seccionDAO.updateColumns(seccion, "alumnoPagador", "devolucion");

        Acreencia acreencia = new Acreencia();
        AlumnoPagoVerano pagoVeranoDb = alumnoPagoVeranoDAO.findAlumnoByCiclo(alumnoPagoVeranoForm.getAlumno(), cicloAcademico);

        if (pagoVeranoDb != null && alumnoPagoVeranoForm.getDeuda().equals(pagoVeranoDb.getDeuda())) {
            return;
        }

        EventoCicloAcademico eventoAcademico = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, EventoAcademicoEnum.MAT_VER);

        Date fechaVencimiento = eventoAcademico.getFechaFin();

        CuentaBancaria ctaBanco = pagoVeranoDb.getCuentaBancaria();

        pagoVeranoDb.setDeudaSeccion(alumnoPagoVeranoForm.getDeuda());
        alumnoPagoVeranoDAO.updateColumns(pagoVeranoDb, "deudaSeccion");

        DeudaAlumno deudaAlumno = deudaAlumnoDAO.allByAlumnoPagoVerano(pagoVeranoDb);
        if (deudaAlumno == null) {
            deudaAlumno = new DeudaAlumno();
            createDeudaAlumno(deudaAlumno, pagoVeranoDb, ctaBanco, fechaVencimiento, usuario, seccion);
            createAcreecia(acreencia, deudaAlumno, alumno, fechaVencimiento, usuario);

        } else {
            Acreencia acreenciaExist = acreenciaDAO.findPersonaAndInstancia(alumno.getPersona(), deudaAlumno.getId());

            if (acreenciaExist != null) {
                acreenciaExist.setEstadoEnum(DeudaEstadoEnum.ANU);
                acreenciaExist.setFechaAnulacion(new Date());
                acreenciaExist.setUsuarioAnulacion(usuario);
                acreenciaDAO.update(acreenciaExist);
            }
            deudaAlumno.setMonto(alumnoPagoVeranoForm.getDeuda());
            createAcreecia(acreencia, deudaAlumno, alumno, fechaVencimiento, usuario);
            deudaAlumnoDAO.updateColumns(deudaAlumno, "monto");
        }
    }

    private void createAcreecia(Acreencia acreencia, DeudaAlumno deudaAlumnoNew, Alumno alumno, Date fechaVencimiento, Usuario usuario) {
        acreencia.setOficina(new Oficina(OficinaEnum.OBUAE.getId()));
        acreencia.setTablaEnum(NombreTablasEnum.FIN_DEUDA_ALUMNO);
        acreencia.setInstanciaTabla(deudaAlumnoNew.getId());
        acreencia.setEstadoEnum(DeudaEstadoEnum.DEU);
        acreencia.setDescripcion("Matricula Verano");
        acreencia.setMonto(deudaAlumnoNew.getMonto());
        acreencia.setAbono(BigDecimal.ZERO);
        acreencia.setPersona(alumno.getPersona());
        acreencia.setCuentaBancaria(deudaAlumnoNew.getCuentaBancaria());
        acreencia.setFechaDocumento(new Date());
        acreencia.setUsuarioRegistro(usuario);
        acreencia.setFechaVencimiento(fechaVencimiento);
        acreencia.setFechaRegistro(new Date());
        acreenciaDAO.save(acreencia);
    }

    private void createDeudaAlumno(DeudaAlumno deudaAlumnoNew, AlumnoPagoVerano pagoVeranoDb, CuentaBancaria ctaBanco, Date fechaVencimiento, Usuario usuario, Seccion seccion) {

        deudaAlumnoNew.setAlumno(pagoVeranoDb.getAlumno());
        deudaAlumnoNew.setAlumnoPagoVerano(pagoVeranoDb);
        deudaAlumnoNew.setConcepto("Deuda Verano");
        deudaAlumnoNew.setTipoDeudaEnum(TipoDeudaEnum.VERANO);
        deudaAlumnoNew.setCuentaBancaria(ctaBanco);
        deudaAlumnoNew.setEstadoEnum(DeudaEstadoEnum.DEU);
        deudaAlumnoNew.setFechaRegistro(new Date());
        deudaAlumnoNew.setFechaEmision(new Date());
        deudaAlumnoNew.setFechaVencimiento(fechaVencimiento);
        deudaAlumnoNew.setUserRegistro(usuario);
        deudaAlumnoNew.setMonto(pagoVeranoDb.getDeudaSeccion());
        deudaAlumnoNew.setNumeroCuota(1);
        deudaAlumnoNew.setAbono(BigDecimal.ZERO);
        deudaAlumnoNew.setSeccion(seccion);
        deudaAlumnoDAO.save(deudaAlumnoNew);
    }

    @Override
    public void deleteDescuento(DescuentoSeccionVerano descuentoSeccionVerano, DataSessionPivot ds) {
        DescuentoSeccionVerano descuentoSeccionVeranoDB = descuentoSeccionVeranoDAO.find(descuentoSeccionVerano.getId());

        if (descuentoSeccionVeranoDB != null) {
            descuentoSeccionVeranoDB.setEstadoEnum(EstadoEnum.ANU);
            descuentoSeccionVeranoDAO.update(descuentoSeccionVeranoDB);
        }

        Seccion seccion = seccionDAO.find(descuentoSeccionVeranoDB.getSeccion());
        seccion.setDescuentoPrecio(BigDecimal.ZERO);
        seccion.setDevolucion(1);
        seccionDAO.updateColumns(seccion, "descuentoPrecio", "devolucion");

    }

    @Override
    public List<AnexoBoletin> allAnexosUser(DataSessionPivot ds) {
        List<AnexoBoletin> anexosAll = anexoBoletinDAO.allAnexosHijos();
        return verificadorService.anexosInferioresByOficina(ds, anexosAll);
    }

}
