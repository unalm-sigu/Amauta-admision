package pe.edu.lamolina.amauta.controller.nivelacioneegg.registronotafinal;

import java.math.BigDecimal;
import static java.math.BigDecimal.ZERO;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.amauta.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.AsistenciaNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.NotaAlumnoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.TemaAsistenciaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import static pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum.ABI;
import static pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum.CER;
import static pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum.RAB;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class RegistroNotaFinalServiceImpl implements RegistroNotaFinalService {

    private final AsistenciaNivelacionDAO asistenciaNivelacionDAO;
    private final CursoNivelacionDAO cursoNivelacionDAO;
    private final HorarioAulaDAO horarioAulaDAO;
    private final NotaAlumnoNivelacionDAO notaAlumnoNivelacionDAO;
    private final TemaAsistenciaDAO temaAsistenciaDAO;

    private final DespliegueConfig despliegueConfig;
    private final BigDecimal VEINTE = new BigDecimal("20");
    private final List<EstadoGrupoSeccionEnum> ESTADOS_NOTAS = Arrays.asList(ABI, RAB);

    @Override
    public CursoNivelacion findSeccion(CursoNivelacion form, Docente docenteForm, CicloAcademico cicloForm) {
        Assert.isNull(docenteForm, "No existe un docente");

        CursoNivelacion seccion = cursoNivelacionDAO.find(form.getId());
        Assert.isNotNull(seccion, "No existe la sección solicitada");

        Docente docente = seccion.getDocente();
        Assert.isTrue(docente.getId().equals(docenteForm.getId()), "Esta sección no corresponde al docente");

        CicloAcademico ciclo = seccion.getCursoCiclo().getCicloAcademico();
        Assert.isTrue(ciclo.getId().equals(cicloForm.getId()), "Esta sección no corresponde al ciclo actual");

        return seccion;
    }

    @Override
    public List<NotaAlumnoNivelacion> allAlumnos(DynatableFilter filter, CursoNivelacion seccion) {
        return notaAlumnoNivelacionDAO.allSeccionByDynatable(filter, seccion);
    }

    @Override
    @Transactional
    public void registrarNota(NotaAlumnoNivelacion form, Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {
        Assert.isNotNull(form.getNotaCurso(), "No ha indicado la nota");
        NotaAlumnoNivelacion notaAlumno = notaAlumnoNivelacionDAO.find(form.getId());
        Assert.isNotNull(notaAlumno, "No existe el registro de la nota");

        CursoNivelacion seccion = this.findSeccion(notaAlumno.getCursoNivelacion(), docente, ciclo);
        Assert.isTrue(ESTADOS_NOTAS.contains(seccion.getEstadoNotasEnum()), "Ya está cerrado el ingreso de notas");

        List<NotaAlumnoNivelacion> inscritos = notaAlumnoNivelacionDAO.allInscritosByCursoNivelacion(seccion);
        Assert.isFalse(inscritos.isEmpty(), "No existe alumnos inscritos en esta sección");

        Assert.isTrue(form.getNotaCurso().compareTo(ZERO) >= 0, "La nota tiene que ser mayor a CERO");
        Assert.isTrue(form.getNotaCurso().compareTo(VEINTE) <= 0, "La nota tiene que ser menor a VEINTE");

        notaAlumno.setNotaCurso(form.getNotaCurso());
        notaAlumnoNivelacionDAO.update(notaAlumno);
    }

    @Override
    @Transactional
    public void cerrarNotas(CursoNivelacion form, Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {
        CursoNivelacion seccion = this.findSeccion(form, docente, ciclo);
        Assert.isTrue(ESTADOS_NOTAS.contains(seccion.getEstadoNotasEnum()), "Ya está cerrado el ingreso de notas");

        List<NotaAlumnoNivelacion> notas = notaAlumnoNivelacionDAO.allByCursoNivelacion(seccion);
        List<NotaAlumnoNivelacion> sinNotas = notas.stream()
                .filter(nan -> nan.getNotaCurso() == null)
                .collect(Collectors.toList());
        Assert.isTrue(sinNotas.isEmpty(), "Falta ingresar las notas de " + sinNotas.size() + " alumnos");

        seccion.setEstadoNotasEnum(CER);
        seccion.setFechaEntregaNotas(new Date());
        seccion.setFechaModificacion(new Date());
        seccion.setUserModificacion(ds.getUsuario());
        cursoNivelacionDAO.update(seccion);
    }

}
