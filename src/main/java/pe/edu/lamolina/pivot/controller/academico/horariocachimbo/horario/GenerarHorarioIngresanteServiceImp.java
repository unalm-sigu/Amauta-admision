package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.horario;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.pivot.dao.academico.AlumnoHorarioDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCachimbosDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioCachimbosDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionHorarioCachimbosDAO;
import pe.edu.lamolina.pivot.model.academico.AlumnoHorario;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.horario.HorarioCachimbos;
import pe.edu.lamolina.pivot.model.horario.SeccionHorarioCachimbos;

@Service
@Transactional(readOnly = true)
public class GenerarHorarioIngresanteServiceImp implements GenerarHorarioIngresanteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CursoCachimbosDAO cursoCachimbosDAO;

    @Autowired
    HorarioCachimbosDAO horarioCachimbosDAO;

    @Autowired
    AlumnoHorarioDAO alumnoHorarioDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    SeccionHorarioCachimbosDAO seccionHorarioCachimbosDAO;

    @Override
    public List<HorarioCachimbos> allHorarioCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return horarioCachimbosDAO.allHorarioCachimbos(filter, cicloAcademico);
    }

    @Override
    @Transactional
    public void delete(HorarioCachimbos horarioCachimbos) {
        horarioCachimbosDAO.delete(horarioCachimbos);
    }

    @Override
    @Transactional
    public void delete(HorarioCachimboForm form) {
        for (HorarioCachimbos horarioCachimbos : form.getHorarioCachimbos()) {
            horarioCachimbosDAO.delete(horarioCachimbos);
        }
    }

    @Override
    public List<AlumnoHorario> allAlumnoHorarioByName(String nombre, CicloAcademico cicloAcademico) {
        return alumnoHorarioDAO.allAlumnoHorarioByName(nombre, cicloAcademico);
    }

    @Override
    public List<Carrera> allCarrera(ModalidadEstudio modalidadEstudio) {
        return carreraDAO.allCarreraByModalidadEstudio(modalidadEstudio);
    }

    @Override
    public List<Curso> allCursoCachimbosByCicloAcademico(CicloAcademico cicloAcademico, Carrera carrera) {
        return cursoDAO.allCursoCachimbosByCicloAcademico(cicloAcademico, carrera);
    }

    @Override
    public List<HorarioCachimbos> allHorarioCachimbosByCicloAcademico(CicloAcademico cicloAcademico, Carrera carrera) {
        return horarioCachimbosDAO.allByCicloAcademico(cicloAcademico, carrera);
    }

    @Override
    public List<SeccionHorarioCachimbos> allSeccionHorarioCachimbosByCursoHora(Carrera carrera, List<Curso> cursos, CicloAcademico cicloAcademico) {
        return seccionHorarioCachimbosDAO.allByCursoHora(carrera, cursos, cicloAcademico);
    }

    @Override
    public String getClave(String codigo, List<SeccionHorarioCachimbos> shcHorario) {
        if(shcHorario==null){
            return "";
        }
        for (SeccionHorarioCachimbos shc : shcHorario) {
            if (shc.getSeccion().getGrupoHoras().getTipoSeccion().equalsIgnoreCase(codigo)) {
                return ObjectUtil.getParentTree(shc, "seccion.grupoHoras.codigo").toString();
            }
        }
        return "";
    }

}
