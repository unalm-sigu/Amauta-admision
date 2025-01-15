package pe.edu.lamolina.amauta.controller.nivelacioneegg.alumnado;

import java.util.List;
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
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class AlumnadoServiceImpl implements AlumnadoService {

    private final CursoNivelacionDAO cursoNivelacionDAO;
    private final NotaAlumnoNivelacionDAO notaAlumnoNivelacionDAO;

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
    public List<NotaAlumnoNivelacion> allMatriculados(DynatableFilter filter, CursoNivelacion seccion) {
        return notaAlumnoNivelacionDAO.allByDynatableSeccion(filter, seccion);
    }

}
