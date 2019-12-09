package pe.edu.lamolina.pivot.controller.rolexamen.docente;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.bean.RolExamenDocente;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.RolExamenesEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.rolexamen.AulaCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.DocenteCursoMasivo;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AulaCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.DocenteCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.FechaHoraGrupoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoEspecialDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoRegularDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class RolExamenDocenteServiceImp implements RolExamenDocenteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DocenteCursoMasivoDAO docenteCursoMasivoDAO;

    @Autowired
    SeccionGrupoRegularDAO seccionGrupoRegularDAO;

    @Autowired
    SeccionGrupoEspecialDAO seccionGrupoEspecialDAO;

    @Autowired
    SeccionCursoMasivoDAO seccionCursoMasivoDAO;

    @Autowired
    AulaCursoMasivoDAO aulaCursoMasivoDAO;

    @Autowired
    FechaHoraGrupoExamenDAO fechaHoraGrupoExamenDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    RolExamenesDAO rolExamenesDAO;

    @Override
    public List<RolExamenDocente> listExamenDocente(Docente docente, CicloAcademico ciclo) {

        RolExamenes rolExam = rolExamenesDAO.findByEstadoCiclo(RolExamenesEstadoEnum.PUB, ciclo);
        if (rolExam == null) {
            return new ArrayList();
        }
        List<Seccion> listSeccion = seccionDAO.allSeccionByCicloDocente(docente, ciclo);
        List<RolExamenDocente> seccionGrupoRegulars = seccionGrupoRegularDAO.allBySeccionesAndRolExam(rolExam, listSeccion);
        List<RolExamenDocente> seccionGrupoEspecials = seccionGrupoEspecialDAO.allBySeccionesAndRolExam(rolExam, listSeccion);

//        RolExamenes rolExam = rolExamenesDAO.findByCicloAndEstadoAndEventoAcademico(ds.getCicloAcademico(), RolExamenesEstadoEnum.PUB, EventoAcademicoEnum.EXAMEN_PARC);
//        List<RolExamenDocente> seccionGrupoRegulars = seccionGrupoRegularDAO.allByDocenteAndCiclo(docente, ds.getCicloAcademico());
//        List<RolExamenDocente> seccionGrupoEspecials = seccionGrupoEspecialDAO.allByDocenteAndCiclo(docente, ds.getCicloAcademico());
        List<DocenteCursoMasivo> docenteCursoMasivos = docenteCursoMasivoDAO.allByDocenteAndCiclo(docente, ciclo);
        List<CursoMasivoExamen> cursosMasivos = docenteCursoMasivos.stream().map(DocenteCursoMasivo::getCursoMasivoExamen).collect(Collectors.toList());
        List<SeccionCursoMasivo> seccionCursoMasivos = seccionCursoMasivoDAO.allByCursosMasivos(cursosMasivos);
        List<AulaCursoMasivo> aulaCursoMasivos = aulaCursoMasivoDAO.allByCursosMasivos(cursosMasivos);

        List<RolExamenDocente> examenDocentes = new ArrayList();
        for (DocenteCursoMasivo docenteCursoMasivo : docenteCursoMasivos) {
            CursoMasivoExamen cursoMasivoExamen = docenteCursoMasivo.getCursoMasivoExamen();
            RolExamenes rolExamenes = cursoMasivoExamen.getRolExamenes();

            List<Seccion> secciones = new ArrayList();
            List<SeccionCursoMasivo> seccionCursoMasivo = seccionCursoMasivos.stream()
                    .filter(x -> cursoMasivoExamen.getId() == x.getCursoMasivoExamen().getId())
                    .collect(Collectors.toList());
            secciones.addAll(seccionCursoMasivo.stream().map(SeccionCursoMasivo::getSeccion).collect(Collectors.toList()));

            List<Aula> aulas = new ArrayList();
            List<AulaCursoMasivo> aulaCursoMasivo = aulaCursoMasivos.stream()
                    .filter(x -> cursoMasivoExamen.getId() == x.getCursoMasivoExamen().getId())
                    .collect(Collectors.toList());
            aulas.addAll(aulaCursoMasivo.stream().map(AulaCursoMasivo::getAula).collect(Collectors.toList()));

            examenDocentes.add(new RolExamenDocente(cursoMasivoExamen.getCurso(),
                    cursoMasivoExamen.getGrupoHorasExamen(),
                    aulas,
                    secciones,
                    rolExamenes.getEstado(),
                    rolExamenes.getId(),
                    rolExamenes.getNombre()));
        }
        examenDocentes.addAll(seccionGrupoRegulars);
        examenDocentes.addAll(seccionGrupoEspecials);
        return examenDocentes;
    }

    @Override
    public List<FechaHoraGrupoExamen> allFechaHoraGrupoExamenBySemanaExamen(SemanaExamen semanaExamen, List<GrupoHorasExamen> grupoHorasExamens) {
        List<Long> ids = grupoHorasExamens.stream().map(GrupoHorasExamen::getId).collect(Collectors.toList());
        return fechaHoraGrupoExamenDAO.allBySemanaExamenAndGrupoHoraSecc(semanaExamen, ids);
    }
}
