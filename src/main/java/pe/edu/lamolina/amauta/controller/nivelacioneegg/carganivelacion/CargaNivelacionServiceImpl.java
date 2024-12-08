package pe.edu.lamolina.amauta.controller.nivelacioneegg.carganivelacion;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoNivelacionDAO;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class CargaNivelacionServiceImpl implements CargaNivelacionService {

    private final CursoNivelacionDAO cursoNivelacionDAO;
    private final HorarioAulaDAO horarioAulaDAO;

    @Override
    public List<CursoNivelacion> allCargaAcademica(DynatableFilter filter, CicloAcademico ciclo, Docente docente) {
        return cursoNivelacionDAO.allDocenteByDynatable(filter, ciclo, docente);
    }

    @Override
    public List<HorarioAula> getHorarioGrupo(Docente docente, CicloAcademico ciclo) {
        List<HorarioAula> horarios = horarioAulaDAO.allByDocente(docente, ciclo);
        return horarios;
    }

}
