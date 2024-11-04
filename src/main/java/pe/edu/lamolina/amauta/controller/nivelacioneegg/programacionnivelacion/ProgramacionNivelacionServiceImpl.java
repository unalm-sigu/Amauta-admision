package pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.amauta.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoNivelacionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class ProgramacionNivelacionServiceImpl implements ProgramacionNivelacionService {

    private final CursoCicloAcademicoDAO cursoCicloAcademicoDAO;
    private final CursoDAO cursoDAO;
    private final CursoNivelacionDAO cursoNivelacionDAO;
    private final DocenteDAO docenteDAO;

    @Override
    public List<CursoNivelacion> allCursosNivelacionByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        return cursoNivelacionDAO.allByDynatable(filter, ciclo);
    }

    @Override
    @Transactional
    public void addCurso(CursoNivelacion form, CicloAcademico ciclo, DataSessionPivot ds) {
        Assert.isNotNull(form.getDocente(), "No ha indicado el docente");
        Assert.isNotNull(form.getDocente().getId(), "No ha indicado el docente");
        Assert.isNotNull(form.getDocente().getCodigo(), "No ha indicado el docente");
        Assert.isNotNull(form.getFechaInicio(), "No ha indicado la fecha de inicio de la semana");
        Assert.isNotNull(form.getFechaFin(), "No ha indicado la fecha final de la semana");

        Assert.isNotNull(form.getCursoCiclo(), "No ha indicado el curso");
        Assert.isNotNull(form.getCursoCiclo().getCurso(), "No ha indicado el curso");
        Assert.isNotNull(form.getCursoCiclo().getCurso().getId(), "No ha indicado el curso");

        Curso curso = cursoDAO.find(form.getCursoCiclo().getCurso().getId());
        Assert.isNotNull(curso, "El identificador del curso no existe en la base de datos");
        Assert.isNotNull(curso.getModalidadEstudio(), "El curso no pertenece a alguna modalidad");
        Assert.isTrue(curso.getModalidadEstudio().getIsNivelaIngresantes(), "El curso debe pertenecer a la modalidad de nivelación de ingresantes");

        CursoCicloAcademico cursoCiclo = cursoCicloAcademicoDAO.findByCursoCiclo(curso, ciclo);
        if (cursoCiclo == null) {
            cursoCiclo = new CursoCicloAcademico();
            cursoCiclo.setCicloAcademico(ciclo);
            cursoCiclo.setCurso(curso);
            cursoCiclo.setEstadoEnum(EstadoEnum.ACT);
            cursoCiclo.setHorasSemanalesTeoria(0);
            cursoCiclo.setHorasSemanalesPractica(0);
            cursoCiclo.setCreditos(0);
            cursoCicloAcademicoDAO.save(cursoCiclo);
        }

        form.setCursoCiclo(cursoCiclo);

        Docente docente = docenteDAO.findByCode(form.getDocente().getCodigo());
        Assert.isNotNull(docente, "No se pudo ubicar el registro del docente seleccionado");
        Assert.isNotNull(docente.getModalidadEstudio(), "El docente seleccionado debe pertenecer a alguna modalidad");
        Assert.isTrue(docente.getModalidadEstudio().isPregrado(), "El docente seleccionado debe pertenecer a pregrado");
        form.setDocente(docente);

        form.setEstado(EstadoEnum.ACT.name());
        form.setUserRegistro(ds.getUsuario());
        form.setFechaRegistro(new Date());
        cursoNivelacionDAO.save(form);

    }

}
