package pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.NotaAlumnoNivelacionDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoTemaExamenDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.calificacion.TemaExamen;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.CursoTemaExamen;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class MatriculablesNivelacionServiceImpl implements MatriculablesNivelacionService {

    private final CursoNivelacionDAO cursoNivelacionDAO;
    private final CursoTemaExamenDAO cursoTemaExamenDAO;
    private final NotaAlumnoNivelacionDAO notaAlumnoNivelacionDAO;

    private final BigDecimal VEINTE = new BigDecimal("20");

    @Override
    public List<NotaAlumnoNivelacion> allMatriculablesByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        List<NotaAlumnoNivelacion> matriculables = notaAlumnoNivelacionDAO.allByDynatable(filter, ciclo);

        return matriculables;
    }

    @Override
    @Transactional
    public int generarMatriculables(CicloAcademico ciclo, DataSessionPivot ds) {
        List<NotaAlumnoNivelacion> nuevosMtbles = notaAlumnoNivelacionDAO.allSinCursoByCiclo(ciclo);
        if (nuevosMtbles.isEmpty()) {
            return nuevosMtbles.size();
        }

        List<CursoNivelacion> cursosNiv = cursoNivelacionDAO.allActivosByCiclo(ciclo);
        if (cursosNiv.isEmpty()) {
            return cursosNiv.size();
        }

        Map<Long, List<CursoNivelacion>> mapCursoNiv = cursosNiv.stream()
                .collect(Collectors.groupingBy(cn -> cn.getCursoCiclo().getCurso().getId()));

        List<CursoTemaExamen> cursosTemas = cursoTemaExamenDAO.all();
        Map<Long, List<CursoTemaExamen>> mapCursoTema = cursosTemas.stream()
                .collect(Collectors.groupingBy(cte -> cte.getTemaExamen().getId()));

        int nuevos = 0;
        for (NotaAlumnoNivelacion mtble : nuevosMtbles) {
            if (mtble.getTemaAprobado()) {
                continue;
            }

            TemaExamen tema = mtble.getTemaExamen();
            List<CursoTemaExamen> cursosTema = mapCursoTema.get(tema.getId());
            if (cursosTema.isEmpty()) {
                continue;
            }

            for (CursoTemaExamen cursoTema : cursosTema) {
                Curso curso = cursoTema.getCurso();
                List<CursoNivelacion> cursosMtbles = mapCursoNiv.get(curso.getId());
                if (cursosMtbles.isEmpty()) {
                    continue;
                }

                mtble.setEsMatriculable(Boolean.TRUE);
                mtble.setCurso(curso);
                notaAlumnoNivelacionDAO.update(mtble);
                nuevos++;
            }
        }

        return nuevos;
    }

    @Override
    @Transactional
    public int matriculaMasivaTipo1(CicloAcademico ciclo, DataSessionPivot ds) {
        List<NotaAlumnoNivelacion> nuevosMtbles = notaAlumnoNivelacionDAO.allConCursoByCiclo(ciclo);
        if (nuevosMtbles.isEmpty()) {
            return nuevosMtbles.size();
        }

        List<CursoNivelacion> cursosNiv = cursoNivelacionDAO.allActivosByCiclo(ciclo);
        Map<Long, List<CursoNivelacion>> mapCursoNiv = cursosNiv.stream()
                .collect(Collectors.groupingBy(cn -> cn.getCursoCiclo().getCurso().getId()));

        int nuevos = 0;
        for (NotaAlumnoNivelacion mtble : nuevosMtbles) {
            if (mtble.getTemaAprobado()) {
                continue;
            }

            Curso curso = mtble.getCurso();
            List<CursoNivelacion> cursosMtbles = mapCursoNiv.get(curso.getId());
            for (CursoNivelacion cursoNiv : cursosMtbles) {
                if (cursoNiv.getDisponibles() > 0) {
                    cursoNiv.setDisponibles(cursoNiv.getDisponibles() - 1);
                    cursoNiv.setMatriculados(cursoNiv.getMatriculados() + 1);
                    cursoNivelacionDAO.update(cursoNiv);

                    mtble.setCursoNivelacion(cursoNiv);
                    mtble.setEstadoEnum(MAT);
                    mtble.setUserModificacion(ds.getUsuario());
                    mtble.setFechaModificacion(new Date());
                    notaAlumnoNivelacionDAO.update(mtble);
                    nuevos++;
                    break;
                }
            }

        }

        return nuevos;
    }

    @Override
    @Transactional
    public void matricularCurso(NotaAlumnoNivelacion form, CicloAcademico ciclo, DataSessionPivot ds) {
        Assert.isNotNull(form.getCursoNivelacion(), "No ha indicado la sección al cual matricularse");

        NotaAlumnoNivelacion mtble = notaAlumnoNivelacionDAO.find(form.getId());
        Assert.isNotNull(mtble, "No existe el registro que ha seleccionado");
        Assert.isNull(mtble.getCursoNivelacion(), "Ya se encuetra matriculado");
        Assert.isTrue(mtble.getEstadoEnum() == NMAT, "No se encuetra habilitado este registro");

        CicloAcademico cicloBD = mtble.getAlumnoNivelacion().getCicloAcademico();
        Assert.isTrue(cicloBD.getId().equals(ciclo.getId()), "El registro no corresponde el ciclo actual");
        Assert.isTrue(!mtble.getTemaAprobado(), "Este tema ya aprobó");
        Assert.isTrue(mtble.getEsMatriculable(), "Este registro no es matriculable");
        Assert.isNotNull(mtble.getCurso(), "Este registro debe estar relacionado a algún curso");

        CursoNivelacion cursoNiv = cursoNivelacionDAO.find(form.getCursoNivelacion().getId());
        Assert.isNotNull(cursoNiv, "No existe la sección que ha seleccionado");
        Assert.isTrue(cursoNiv.getEstadoEnum() == SeccionEstadoEnum.ACT, "Esta sección no está habilitada para inscribirse");
        Assert.isTrue(cursoNiv.getDisponibles() > 0, "Ya no existe vacantes disponibles");

        cursoNiv.setDisponibles(cursoNiv.getDisponibles() - 1);
        cursoNiv.setMatriculados(cursoNiv.getMatriculados() + 1);
        cursoNivelacionDAO.update(cursoNiv);

        mtble.setCursoNivelacion(cursoNiv);
        mtble.setEstadoEnum(MAT);
        mtble.setUserModificacion(ds.getUsuario());
        mtble.setFechaModificacion(new Date());
        notaAlumnoNivelacionDAO.update(mtble);
    }

}
