package pe.edu.lamolina.amauta.controller.nivelacioneegg.registronotafinal;

import java.math.BigDecimal;
import static java.math.BigDecimal.ZERO;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.helper.ChangeProgramacionNivelacionService;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.ExamenAlumnoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.ExamenCursoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.NotaAlumnoNivelacionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import static pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum.ABI;
import static pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum.CER;
import static pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum.PEN;
import static pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum.RAB;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.ExamenAlumnoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.ExamenCursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.TipoExamenNivelacion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class RegistroNotaFinalServiceImpl implements RegistroNotaFinalService {

    private final CursoNivelacionDAO cursoNivelacionDAO;
    private final ExamenAlumnoNivelacionDAO examenAlumnoNivelacionDAO;
    private final ExamenCursoNivelacionDAO examenCursoNivelacionDAO;
    private final NotaAlumnoNivelacionDAO notaAlumnoNivelacionDAO;

    private final BigDecimal VEINTE = new BigDecimal("20");
    private final BigDecimal NOTA_MIN_PARCIAL = new BigDecimal("10.5");
    private final BigDecimal NOTA_MIN_FINAL = new BigDecimal("11");
    private final List<EstadoGrupoSeccionEnum> ESTADOS_ABIERTOS = Arrays.asList(ABI, RAB);
    private final List<EstadoGrupoSeccionEnum> ESTADOS_CERRADOS = Arrays.asList(CER, PEN);

    private final ChangeProgramacionNivelacionService changeProgramacionNivelacionService;

    @Override
    public CursoNivelacion findSeccion(CursoNivelacion form, Docente docenteForm, CicloAcademico cicloForm) {
        Assert.isNotNull(docenteForm, "No existe un docente");

        CursoNivelacion seccion = cursoNivelacionDAO.find(form.getId());
        Assert.isNotNull(seccion, "No existe la sección solicitada");

        Docente docente = seccion.getDocente();
        Assert.isTrue(docente.getId().equals(docenteForm.getId()), "Esta sección no corresponde al docente");

        CicloAcademico ciclo = seccion.getCursoCiclo().getCicloAcademico();
        Assert.isTrue(ciclo.getId().equals(cicloForm.getId()), "Esta sección no corresponde al ciclo actual");

        return seccion;
    }

    @Override
    public List<ExamenCursoNivelacion> allExamenes(CursoNivelacion seccion) {
        return examenCursoNivelacionDAO.allByCursoNivelacion(seccion);
    }

    @Override
    public List<NotaAlumnoNivelacion> allAlumnos(DynatableFilter filter, CursoNivelacion seccion) {
        List<NotaAlumnoNivelacion> notasAlumnos = notaAlumnoNivelacionDAO.allByDynatableSeccion(filter, seccion);

        List<ExamenAlumnoNivelacion> examenesAlumnosAll = examenAlumnoNivelacionDAO.allByNotasAlumnos(notasAlumnos);
        Map<Long, List<ExamenAlumnoNivelacion>> mapExamenes = examenesAlumnosAll.stream()
                .collect(Collectors.groupingBy(ean -> ean.getNotaAlumnoNivelacion().getId()));

        for (NotaAlumnoNivelacion notaAlu : notasAlumnos) {
            List<ExamenAlumnoNivelacion> examenesAlumnos = TypesUtil.getListNotNull(mapExamenes.get(notaAlu.getId()));
            notaAlu.setExamenesAlumno(examenesAlumnos);
        }

        return notasAlumnos;
    }

    @Override
    @Transactional
    public void abrirActa(ExamenCursoNivelacion form, Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {
        ExamenCursoNivelacion examen = examenCursoNivelacionDAO.find(form.getId());
        Assert.isNotNull(examen, "No existe el registro que ha seleccionado");
        Assert.isTrue(ESTADOS_CERRADOS.contains(examen.getEstadoEnum()), "Ya está abierta el ingreso de notas de este examen");

        CursoNivelacion cursoNiv = this.findSeccion(examen.getCursoNivelacion(), docente, ciclo);
        Assert.isTrue(ESTADOS_ABIERTOS.contains(cursoNiv.getEstadoNotasEnum()), "No está permitido editar las notas de esta sección");

        List<ExamenCursoNivelacion> examenes = examenCursoNivelacionDAO.allByCursoNivelacion(cursoNiv);
        Optional<ExamenCursoNivelacion> examenAbierto = examenes.stream()
                .filter(excn -> excn.getId().equals(examen.getId()))
                .filter(excn -> ESTADOS_ABIERTOS.contains(excn.getEstadoEnum()))
                .findFirst();

        if (examenAbierto.isPresent()) {
            TipoExamenNivelacion tipoExamen = examenAbierto.get().getTipoExamenNivelacion();
            Assert.isFalse(examenAbierto.isPresent(), "Está aún abierto el examen " + tipoExamen.getNombre());
        }

        List<NotaAlumnoNivelacion> inscritos = notaAlumnoNivelacionDAO.allInscritosByCursoNivelacion(cursoNiv);
        Assert.isFalse(inscritos.isEmpty(), "No existe alumnos inscritos en esta sección");
        boolean noHayExamenesAlumnos = examen.getEstadoEnum() == PEN;

        examen.setEstadoEnum(ABI);
        examen.setUserModificacion(ds.getUsuario());
        examen.setFechaModificacion(new Date());
        examenCursoNivelacionDAO.update(examen);

        if (noHayExamenesAlumnos) {
            inscritos.forEach(inscrito -> {
                ExamenAlumnoNivelacion examenAlumno = new ExamenAlumnoNivelacion();
                examenAlumno.setExamenCursoNivelacion(examen);
                examenAlumno.setNotaAlumnoNivelacion(inscrito);
                examenAlumno.setUserRegistro(ds.getUsuario());
                examenAlumno.setFechaRegistro(new Date());
                examenAlumnoNivelacionDAO.save(examenAlumno);
            });
        }

    }

    @Override
    @Transactional
    public void cerrarActa(ExamenCursoNivelacion form, Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {
        ExamenCursoNivelacion examen = examenCursoNivelacionDAO.find(form.getId());
        Assert.isNotNull(examen, "No existe el registro que ha seleccionado");
        Assert.isTrue(ESTADOS_ABIERTOS.contains(examen.getEstadoEnum()), "Ya está cerrado el ingreso de notas de este examen");

        CursoNivelacion cursoNiv = this.findSeccion(examen.getCursoNivelacion(), docente, ciclo);
        Assert.isTrue(ESTADOS_ABIERTOS.contains(cursoNiv.getEstadoNotasEnum()), "No está permitido editar las notas de esta sección");

        List<ExamenAlumnoNivelacion> examenAlumnosAll = examenAlumnoNivelacionDAO.allByExamen(examen);
        List<ExamenAlumnoNivelacion> examenAlumnos = examenAlumnosAll.stream()
                .filter(exan -> exan.getNotaAlumnoNivelacion().getEstadoEnum() == MAT)
                .filter(exan -> exan.getNotaExamen() == null)
                .collect(Collectors.toList());
        Assert.isTrue(examenAlumnos.isEmpty(), "Falta registrar la nota de " + examenAlumnos.size() + " alumnos");

        examen.setEstadoEnum(CER);
        examen.setUserModificacion(ds.getUsuario());
        examen.setFechaModificacion(new Date());
        examen.setFechaEntregaNotas(new Date());
        examenCursoNivelacionDAO.update(examen);
    }

    @Override
    @Transactional
    public void registrarNota(ExamenAlumnoNivelacion form, Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {
        Assert.isNotNull(form.getNotaExamen(), "No ha indicado la nota");
        ExamenAlumnoNivelacion examenAlumno = examenAlumnoNivelacionDAO.find(form.getId());
        Assert.isNotNull(examenAlumno, "No existe el registro de la nota");

        ExamenCursoNivelacion examen = examenAlumno.getExamenCursoNivelacion();
        Assert.isTrue(examen.getEstadoEnum() == ABI, "No está activo el ingreso de notas de este examen");

        CursoNivelacion seccion = this.findSeccion(examen.getCursoNivelacion(), docente, ciclo);
        Assert.isTrue(ESTADOS_ABIERTOS.contains(seccion.getEstadoNotasEnum()), "Ya está cerrado el ingreso de notas");

        Assert.isTrue(form.getNotaExamen().compareTo(ZERO) >= 0, "La nota tiene que ser mayor a CERO");
        Assert.isTrue(form.getNotaExamen().compareTo(VEINTE) <= 0, "La nota tiene que ser menor a VEINTE");

        BigDecimal notaAntes = examenAlumno.getNotaExamen();
        BigDecimal notaNueva = form.getNotaExamen();
        int equals = notaAntes == null ? -1 : notaNueva.compareTo(notaAntes);
        log.info("[registrarNota] notaAntes={} notaNueva={} equals={}", notaAntes, notaNueva, equals);

        if (equals == 0) {
            return;
        }

        examenAlumno.setNotaExamen(form.getNotaExamen());
        examenAlumno.setAprobado(form.getNotaExamen().compareTo(NOTA_MIN_PARCIAL) >= 0);
        examenAlumno.setFechaRegistroNota(new Date());
        examenAlumnoNivelacionDAO.update(examenAlumno);

        NotaAlumnoNivelacion nota = examenAlumno.getNotaAlumnoNivelacion();

        List<ExamenAlumnoNivelacion> otrosExamenes = examenAlumnoNivelacionDAO.allByNotaAlumno(nota).stream()
                .filter(exa -> !exa.equals(examenAlumno))
                .filter(exa -> exa.getNotaExamen() != null)
                .collect(Collectors.toList());

        BigDecimal suma = otrosExamenes.stream()
                .map(exa -> exa.getNotaExamen())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(form.getNotaExamen());
        BigDecimal promedio = suma.divide(new BigDecimal(otrosExamenes.size() + 1), 0, RoundingMode.HALF_UP);
        log.info("[registrarNota] otrosExamenes={} suma={} promedio={}", otrosExamenes.size(), suma, promedio);

        nota.setNotaCurso(promedio);
        nota.setAprobado(promedio.compareTo(NOTA_MIN_FINAL) >= 0);
        notaAlumnoNivelacionDAO.update(nota);
    }

    @Override
    @Transactional
    public void cerrarNotas(CursoNivelacion form, Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {
        CursoNivelacion seccion = this.findSeccion(form, docente, ciclo);
        Assert.isTrue(ESTADOS_ABIERTOS.contains(seccion.getEstadoNotasEnum()), "Ya está cerrado el ingreso de notas");

        List<NotaAlumnoNivelacion> notas = notaAlumnoNivelacionDAO.allByCursoNivelacion(seccion);
        List<NotaAlumnoNivelacion> sinNotas = notas.stream()
                .filter(nan -> nan.getNotaCurso() == null)
                .collect(Collectors.toList());
        Assert.isTrue(sinNotas.isEmpty(), "Falta ingresar las notas de " + sinNotas.size() + " alumnos");

        if (StringUtils.isBlank(seccion.getCambios())) {
            String cambios = changeProgramacionNivelacionService.createCambiosJson(seccion);
            seccion.setCambios(cambios);
        }

        seccion.setEstadoNotasEnum(CER);
        seccion.setFechaEntregaNotas(new Date());
        seccion.setFechaModificacion(new Date());
        seccion.setUserModificacion(ds.getUsuario());

        String cambio = "Entrega del acta de notas";
        String cambiosTwo = changeProgramacionNivelacionService.createCambiosJson(seccion, cambio, null, seccion.getCambios());
        seccion.setCambios(cambiosTwo);

        cursoNivelacionDAO.update(seccion);
    }

}
