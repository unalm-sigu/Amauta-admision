package pe.edu.lamolina.amauta.controller.subvenciones.viajes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.bienestar.AlumnoViajeCursoDAO;
import pe.edu.lamolina.amauta.dao.bienestar.CronogramaEventoSubvencionadoDAO;
import pe.edu.lamolina.amauta.dao.bienestar.ProformaEventoSubvencionadoDAO;
import pe.edu.lamolina.amauta.dao.bienestar.ViajeCursoDAO;
import pe.edu.lamolina.amauta.dao.contabilidad.ItemJustificacionGastoDAO;
import pe.edu.lamolina.amauta.dao.contabilidad.JustificacionGastoAlumnoDAO;
import pe.edu.lamolina.amauta.dao.contabilidad.JustificacionGastoDAO;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.bienestar.AlumnoViajeCurso;
import pe.edu.lamolina.model.bienestar.CronogramaEventoSubvencionado;
import pe.edu.lamolina.model.bienestar.ProformaEventoSubvencionado;
import pe.edu.lamolina.model.bienestar.ViajeCurso;
import pe.edu.lamolina.model.contabilidad.ItemJustificacionGasto;
import pe.edu.lamolina.model.contabilidad.JustificacionGasto;
import pe.edu.lamolina.model.contabilidad.JustificacionGastoAlumno;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.subvenciones.SubvencionViajeEstadoEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.enums.subvenciones.JustificacionEstadoEnum;
import pe.edu.lamolina.model.enums.subvenciones.ViajeCursoEstadoEnum;
import static pe.edu.lamolina.model.enums.subvenciones.ViajeCursoEstadoEnum.APROBADO;
import static pe.edu.lamolina.model.enums.subvenciones.ViajeCursoEstadoEnum.CREADO;
import static pe.edu.lamolina.model.enums.subvenciones.ViajeCursoEstadoEnum.DESAPROBADO;
import static pe.edu.lamolina.model.enums.subvenciones.ViajeCursoEstadoEnum.JUSTIFICADO;
import static pe.edu.lamolina.model.enums.subvenciones.ViajeCursoEstadoEnum.OBSERVA_DOCENTE;
import static pe.edu.lamolina.model.enums.subvenciones.ViajeCursoEstadoEnum.PENDIENTE;
import static pe.edu.lamolina.model.enums.subvenciones.ViajeCursoEstadoEnum.VB_JUSTIFICACION;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class SubvencionViajesServiceImp implements SubvencionViajesService {

    private final AlumnoViajeCursoDAO alumnoViajeCursoDAO;
    private final ColaboradorDAO colaboradorDAO;
    private final CronogramaEventoSubvencionadoDAO cronogramaEventoSubvencionadoDAO;
    private final CursoDAO cursoDAO;
    private final DepartamentoAcademicoDAO departamentoAcademicoDAO;
    private final DocenteSeccionDAO docenteSeccionDAO;
    private final ItemJustificacionGastoDAO itemJustificacionGastoDAO;
    private final JustificacionGastoDAO justificacionGastoDAO;
    private final JustificacionGastoAlumnoDAO justificacionGastoAlumnoDAO;
    private final MatriculaSeccionDAO matriculaSeccionDAO;
    private final OficinaDAO oficinaDAO;
    private final ProformaEventoSubvencionadoDAO proformaEventoSubvencionadoDAO;
    private final SeccionDAO seccionDAO;
    private final ViajeCursoDAO viajeCursoDAO;

    @Override
    public List<DepartamentoAcademico> allDptosAcademicos(DataSessionPivot ds) {
        Persona persona = ds.getPersona();

        List<Oficina> oficinas = oficinaDAO.allByJefeTipoOficinaEnum(persona, TipoOficinaEnum.DPTO);
        log.info("jefe-dptos-academicos={}", oficinas.size());

        if (oficinas.isEmpty()) {
            return new ArrayList();
        }

        List<Long> idDptos = oficinas.stream()
                .map(ofi -> ofi.getInstanciaOficina())
                .collect(Collectors.toList());
        log.info("dptos-academicos-id={}", idDptos);

        return departamentoAcademicoDAO.allByIds(idDptos);
    }

    @Override
    public List<ViajeCurso> allDynatbleByDocente(Docente docente, List<DepartamentoAcademico> dptos, CicloAcademico ciclo, DynatableFilter filter) {
        log.info("dptos-academicos={}", dptos.size());
        log.info("docente={}", docente);

        if (docente == null && dptos.isEmpty()) {
            log.info("se retorna lista vacia");
            return new ArrayList();
        }

        return viajeCursoDAO.allByDocenteDptosCiclo(docente, dptos, ciclo, filter);
    }

    @Override
    public List<Curso> allCursos(Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {
        log.info("docente={}", docente);
        log.info("ciclo={}", ciclo);

        if (docente == null) {
            return new ArrayList();
        }

        List<DocenteSeccion> seccionesByDocente = docenteSeccionDAO.allByDocente(docente, ciclo);

        return seccionesByDocente.stream()
                .filter(docSec -> docSec.getPrincipal() == 1)
                .filter(docSec -> docSec.getSeccion().getEstadoEnum() == SeccionEstadoEnum.ACT)
                .filter(docSec -> docSec.getSeccion().getMatriculados() > 0)
                .filter(docSec -> !docSec.getSeccion().getGrupoSeccion().getAnexoBoletin().getAnexoSuperior().isAnexoCulturalesDeportes())
                .filter(docSec -> !docSec.getSeccion().getGrupoSeccion().getAnexoBoletin().getAnexoSuperior().isAnexoCursosPostgrado())
                .map(docSec -> docSec.getSeccion().getGrupoSeccion().getCurso())
                .filter(cur -> cur.getModalidadEstudio().isPregrado())
                .distinct()
                .sorted(Comparator.comparing(Curso::getNombre))
                .collect(Collectors.toList());
    }

    @Override
    public List<Seccion> allSecciones(Curso curso, Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {
        List<DocenteSeccion> seccionesByDocente = docenteSeccionDAO.allByDocente(docente, ciclo);

        return seccionesByDocente.stream()
                .filter(docSec -> docSec.getPrincipal() == 1)
                .map(docSec -> docSec.getSeccion())
                .filter(sec -> sec.getEstadoEnum() == SeccionEstadoEnum.ACT)
                .filter(sec -> sec.getMatriculados() > 0)
                .filter(sec -> !sec.getGrupoSeccion().getAnexoBoletin().getAnexoSuperior().isAnexoCulturalesDeportes())
                .filter(sec -> !sec.getGrupoSeccion().getAnexoBoletin().getAnexoSuperior().isAnexoCursosPostgrado())
                .filter(sec -> sec.getGrupoSeccion().getCurso().getId().equals(curso.getId()))
                .distinct()
                .sorted(Comparator.comparing(Seccion::getCodigo2))
                .collect(Collectors.toList());
    }

    @Override
    public List<Alumno> allAlumnos(Seccion seccion, DataSessionPivot ds) {
        List<MatriculaSeccion> matriculados = matriculaSeccionDAO.allBySeccion(seccion);

        return matriculados.stream()
                .filter(matSecc -> matSecc.getEstadoEnum() == EstadoMatriculaEnum.MAT)
                .map(matSecc -> matSecc.getMatriculaResumen().getAlumno())
                .filter(alu -> alu.getModalidadEstudio().isPregrado())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveViaje(ViajeCurso viajeCurso, CicloAcademico ciclo, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Docente docente = ds.getDocente();
        Assert.isNotNull(docente, "Usted no es docente");

        this.validarDataViaje(viajeCurso, ciclo, docente);

        viajeCurso.setEstadoViajeEnum(CREADO);
        viajeCurso.setEstadoSubvencionEnum(SubvencionViajeEstadoEnum.SIN_PRESUPUESTO);
        viajeCurso.setCicloAcademico(ciclo);
        viajeCurso.setCantidadAlumnosAprobados(0);
        viajeCurso.setCantidadAlumnosRegistrados(0);
        viajeCurso.setCantidadAlumnosSeparados(0);
        viajeCurso.setCantidadAlumnosViajaron(0);

        viajeCurso.setImporteAlumno(BigDecimal.ZERO);
        viajeCurso.setImporteDevuelto(BigDecimal.ZERO);
        viajeCurso.setImporteEntregado(BigDecimal.ZERO);
        viajeCurso.setImporteProforma(BigDecimal.ZERO);
        viajeCurso.setImporteSolicitado(BigDecimal.ZERO);
        viajeCurso.setImporteUtilizado(BigDecimal.ZERO);

        viajeCurso.setDocenteCreador(docente);
        viajeCurso.setUserRegistro(ds.getUsuario());
        viajeCurso.setFechaCreacion(today.toDate());
        viajeCurso.setFechaRegistro(today.toDate());

        viajeCursoDAO.save(viajeCurso);

    }

    private void validarDataViaje(ViajeCurso viajeCurso, CicloAcademico ciclo, Docente docente) {
        Seccion seccionForm = viajeCurso.getSeccion();
        Assert.isNotNull(seccionForm, "No ha indicado la sección");

        Curso cursoForm = viajeCurso.getCurso();
        Assert.isNotNull(seccionForm, "No ha indicado el curso");

        Alumno alumnoForm = viajeCurso.getAlumnoDelegado();
        Assert.isNotNull(alumnoForm, "No ha indicado el alumno delegado");

        Seccion seccionBD = seccionDAO.find(seccionForm);
        Assert.isNotNull(seccionBD, "No se pudo ubicar el registro de la sección");
        Assert.isTrue(seccionBD.getEstadoEnum() == SeccionEstadoEnum.ACT, "La sección no está activa");

        Curso cursoBD = seccionBD.getGrupoSeccion().getCurso();
        Assert.isTrue(cursoForm.getId().equals(cursoBD.getId()), "El registro del curso no coincide con el de la sección");

        CicloAcademico cicloBD = seccionBD.getGrupoSeccion().getCicloAcademico();
        Assert.isTrue(ciclo.getId().equals(cicloBD.getId()), "El ciclo de la sección no corresponde al ciclo activo de su sesión");

        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allBySeccion(seccionBD);
        int docentes = docentesSeccion.stream()
                .filter(x -> x.getEstadoEnum() == SeccionEstadoEnum.ACT)
                .filter(x -> x.getDocente().getId().equals(docente.getId()))
                .collect(Collectors.toList())
                .size();
        Assert.isTrue(docentes > 0, "Usted no está asignado como docente de esta sección");

        List<MatriculaSeccion> matriculadosSeccion = matriculaSeccionDAO.allBySeccion(seccionBD);
        int delegados = matriculadosSeccion.stream()
                .filter(x -> x.getEstadoEnum() == EstadoMatriculaEnum.MAT)
                .map(x -> x.getMatriculaResumen().getAlumno())
                .filter(x -> x.getId().equals(alumnoForm.getId()))
                .collect(Collectors.toList())
                .size();
        Assert.isTrue(delegados > 0, "El alumno delegado no está matriculado en esta sección");

        int matriculados = matriculadosSeccion.stream()
                .filter(x -> x.getEstadoEnum() == EstadoMatriculaEnum.MAT)
                .collect(Collectors.toList())
                .size();
        viajeCurso.setCantidadAlumnosMatriculados(matriculados);
    }

    @Override
    @Transactional
    public void updateViaje(ViajeCurso viajeCursoForm, CicloAcademico ciclo, DataSessionPivot ds) {
        Docente docente = ds.getDocente();
        Assert.isNotNull(docente, "Usted no es docente");

        this.validarDataViaje(viajeCursoForm, ciclo, docente);

        ViajeCurso viajeCursoBD = viajeCursoDAO.find(viajeCursoForm.getId());
        ViajeCursoEstadoEnum estadoActual = viajeCursoBD.getEstadoViajeEnum();
        Assert.isTrue(estadoActual == CREADO, "El registro del Viaje debe estar en estado " + CREADO.getValue());

        Docente docenteCreador = viajeCursoBD.getDocenteCreador();
        Assert.isTrue(docenteCreador.getId().equals(docente.getId()), "Este registro corresponde a otro docente");

        CicloAcademico cicloBD = viajeCursoBD.getCicloAcademico();
        Assert.isTrue(cicloBD.getId().equals(ciclo.getId()), "Este registro corresponde a otro ciclo académico");

        Seccion seccionBD = viajeCursoBD.getSeccion();
        List<MatriculaSeccion> matriculadosSeccion = matriculaSeccionDAO.allBySeccion(seccionBD);
        int matriculados = matriculadosSeccion.stream()
                .filter(x -> x.getEstadoEnum() == EstadoMatriculaEnum.MAT)
                .collect(Collectors.toList())
                .size();

        viajeCursoBD.setCantidadAlumnosMatriculados(matriculados);
        viajeCursoBD.setCurso(viajeCursoForm.getCurso());
        viajeCursoBD.setSeccion(viajeCursoForm.getSeccion());
        viajeCursoBD.setAlumnoDelegado(viajeCursoForm.getAlumnoDelegado());
        viajeCursoBD.setCicloAcademico(ciclo);

        viajeCursoDAO.update(viajeCursoBD);
    }

    @Override
    @Transactional
    public void solicitarAprobarViaje(ViajeCurso viajeCursoForm, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Docente docente = ds.getDocente();
        Assert.isNotNull(docente, "Usted no es docente");

        ViajeCurso viajeCursoBD = viajeCursoDAO.find(viajeCursoForm.getId());
        ViajeCursoEstadoEnum estadoActual = viajeCursoBD.getEstadoViajeEnum();
        Assert.isTrue(estadoActual == CREADO, "El registro del Viaje debe estar en estado " + CREADO.getValue());

        Docente docenteCreador = viajeCursoBD.getDocenteCreador();
        Assert.isTrue(docenteCreador.getId().equals(docente.getId()), "Este registro corresponde a otro docente");

        Seccion seccionBD = viajeCursoBD.getSeccion();
        List<MatriculaSeccion> matriculadosSeccion = matriculaSeccionDAO.allBySeccion(seccionBD);
        int matriculados = matriculadosSeccion.stream()
                .filter(x -> x.getEstadoEnum() == EstadoMatriculaEnum.MAT)
                .collect(Collectors.toList())
                .size();

        viajeCursoBD.setCantidadAlumnosMatriculados(matriculados);
        viajeCursoBD.setEstadoViajeEnum(PENDIENTE);
        viajeCursoBD.setFechaCreacion(today.toDate());
        viajeCursoDAO.update(viajeCursoBD);
    }

    @Override
    @Transactional
    public void aprobarViaje(ViajeCurso viajeCursoForm, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Persona persona = ds.getPersona();

        ViajeCurso viajeCursoBD = viajeCursoDAO.find(viajeCursoForm.getId());
        Curso cursoBD = cursoDAO.find(viajeCursoBD.getCurso().getId());
        DepartamentoAcademico dpto = cursoBD.getDepartamentoAcademico();

        boolean validado = false;
        Oficina oficina = oficinaDAO.findByTipoOficinaDptoAcademico(TipoOficinaEnum.DPTO, dpto);
        Persona jefe = oficina.getPersonaJefe();
        if (jefe != null && jefe.getId().equals(persona.getId())) {
            validado = true;
        }

        if (!validado) {
            Persona encargado = oficina.getJefeEncargado();
            if (encargado != null && encargado.getId().equals(persona.getId())) {
                validado = true;
            }
        }

        Assert.isTrue(validado, "Usted no es Jefe del Departamento del curso seleccionado");

        Colaborador colaborador = colaboradorDAO.findJefeByPersonaOficina(oficina, persona);

        ViajeCursoEstadoEnum estadoActual = viajeCursoBD.getEstadoViajeEnum();
        Assert.isTrue(estadoActual == PENDIENTE, "El registro del Viaje debe estar en estado " + PENDIENTE.getValue());

        List<ViajeCursoEstadoEnum> estados = Arrays.asList(APROBADO, DESAPROBADO);
        ViajeCursoEstadoEnum estadoForm = viajeCursoForm.getEstadoViajeEnum();
        Assert.isTrue(estados.contains(estadoForm), "Solo se aceptan Aprobar o Desaprobar");

        viajeCursoBD.setEstadoViajeEnum(estadoForm);
        viajeCursoBD.setFechaVoBoDepartamento(today.toDate());
        viajeCursoBD.setJefeDepartamentoVobo(colaborador);
        viajeCursoDAO.update(viajeCursoBD);

    }

    @Override
    @Transactional
    public void aprobarJustificacion(ViajeCurso viajeCursoForm, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Docente docente = ds.getDocente();
        Assert.isNotNull(docente, "Usted no es docente");

        ViajeCurso viajeCursoBD = viajeCursoDAO.find(viajeCursoForm.getId());
        ViajeCursoEstadoEnum estadoActual = viajeCursoBD.getEstadoViajeEnum();
        Assert.isTrue(estadoActual == JUSTIFICADO, "El registro del Viaje debe estar en estado " + JUSTIFICADO.getValue());

        Docente docenteCreador = viajeCursoBD.getDocenteCreador();
        Assert.isTrue(docenteCreador.getId().equals(docente.getId()), "Este registro corresponde a otro docente");

        viajeCursoBD.setEstadoViajeEnum(VB_JUSTIFICACION);
        viajeCursoBD.setObservacion(null);
        viajeCursoDAO.update(viajeCursoBD);
    }

    @Override
    @Transactional
    public void observaJustificacion(ViajeCurso viajeCursoForm, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Docente docente = ds.getDocente();
        Assert.isNotNull(docente, "Usted no es docente");

        ViajeCurso viajeCursoBD = viajeCursoDAO.find(viajeCursoForm.getId());
        ViajeCursoEstadoEnum estadoActual = viajeCursoBD.getEstadoViajeEnum();
        Assert.isTrue(estadoActual == JUSTIFICADO, "El registro del Viaje debe estar en estado " + JUSTIFICADO.getValue());

        Docente docenteCreador = viajeCursoBD.getDocenteCreador();
        Assert.isTrue(docenteCreador.getId().equals(docente.getId()), "Este registro corresponde a otro docente");

        viajeCursoBD.setEstadoViajeEnum(OBSERVA_DOCENTE);
        viajeCursoBD.setObservacion(viajeCursoForm.getObservacion());
        viajeCursoBD.setUserObservacion(ds.getUsuario());
        viajeCursoBD.setFechaObservacion(today.toDate());
        viajeCursoDAO.update(viajeCursoBD);

        JustificacionGasto justificacion = justificacionGastoDAO.findByViajeCurso(viajeCursoBD);
        justificacion.setEstadoEnum(JustificacionEstadoEnum.ABIERTA);
        justificacionGastoDAO.update(justificacion);
    }

    @Override
    public ViajeCurso findViaje(ViajeCurso viajeCursoForm, DataSessionPivot ds) {
        ViajeCurso viajeCursoBD = viajeCursoDAO.find(viajeCursoForm.getId());

        List<CronogramaEventoSubvencionado> cronogramasBD = cronogramaEventoSubvencionadoDAO.allByViajeCurso(viajeCursoForm);
        viajeCursoBD.setCronogramasViaje(cronogramasBD);

        List<ProformaEventoSubvencionado> proformasBD = proformaEventoSubvencionadoDAO.allByViajeCurso(viajeCursoForm);
        viajeCursoBD.setProformasViaje(proformasBD);

        return viajeCursoBD;
    }

    @Override
    public JustificacionGasto findJustificacion(ViajeCurso viajeCurso, DataSessionPivot ds) {
        JustificacionGasto justificacion = justificacionGastoDAO.findByViajeCurso(viajeCurso);
        if (justificacion == null) {
            ViajeCurso viajeCursoBD = viajeCursoDAO.find(viajeCurso.getId());

            justificacion = new JustificacionGasto();
            justificacion.setImporteAceptado(BigDecimal.ZERO);
            justificacion.setImporteJustificado(BigDecimal.ZERO);
            justificacion.setItemsJustificacion(new ArrayList());
            justificacion.setViajeCurso(viajeCursoBD);
            return justificacion;
        }

        List<JustificacionGastoAlumno> gastosAlumnos = justificacionGastoAlumnoDAO.allByJustificacion(justificacion);
        Map<Long, List<JustificacionGastoAlumno>> mapGastoAlumno = TypesUtil.convertListToMapList("itemJustificacionGasto.id", gastosAlumnos);

        List<ItemJustificacionGasto> itemsGastos = itemJustificacionGastoDAO.allByJustificacion(justificacion);
        for (ItemJustificacionGasto item : itemsGastos) {
            List<JustificacionGastoAlumno> gastosItem = TypesUtil.getListNotNull(mapGastoAlumno.get(item.getId()));
            item.setJustificacionesAlumnos(gastosItem);
        }

        justificacion.setItemsJustificacion(itemsGastos);
        return justificacion;
    }

    @Override
    public List<AlumnoViajeCurso> allAlumnosByViaje(ViajeCurso viajeCurso) {
        return alumnoViajeCursoDAO.allByViajeCurso(viajeCurso);
    }

}
