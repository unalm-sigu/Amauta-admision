package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EstadoVacanteAlumnoEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoRetiroCicloEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.matricula.MatriculaSimultaneo;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSimultaneoDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.tramite.RetiroCicloDAO;
import pe.edu.lamolina.pivot.dao.vacante.VacanteAlumnoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class TramiteRetiroCicloServiceImp implements TramiteRetiroCicloService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    RetiroCicloDAO retiroCicloDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    MatriculaSimultaneoDAO matriculaSimultaneoDAO;

    @Autowired
    VacanteAlumnoDAO vacanteAlumnoDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Override
    public List<CicloAcademico> allCiclos(CicloAcademico academico) {
        return cicloAcademicoDAO.allRegularPre(3, academico);
    }

    @Override
    public List<RetiroCiclo> allByCiclo(CicloAcademico cicloAcademico, DynatableFilter filter) {
        return retiroCicloDAO.allByCiclo(cicloAcademico, filter);
    }

    @Override
    @Transactional
    public void save(RetiroCiclo retiroCiclo, DataSessionPivot ds) {

        RetiroCiclo retiro = new RetiroCiclo();
        retiro.setEstado(TramiteEstadoEnum.PEND);
        retiro.setTipoEnum(TipoRetiroCicloEnum.EXCEP);
        retiro.setAlumno(retiroCiclo.getAlumno());
        retiro.setCicloAcademico(retiroCiclo.getCicloAcademico());
        retiro.setCicloRegistro(ds.getCicloAcademico());
        retiro.setUsuario(ds.getUsuario());
        retiroCicloDAO.save(retiro);

    }

    @Override
    @Transactional
    public void update(RetiroCiclo retiroCiclo, DataSessionPivot ds) {
        RetiroCiclo retiroCiclobd = retiroCicloDAO.find(retiroCiclo.getId());
        retiroCiclobd.setEstado(TramiteEstadoEnum.valueOf(retiroCiclo.getEstado()));
        retiroCicloDAO.update(retiroCiclobd);

        if (retiroCiclobd.getEstadoEnum() == TramiteEstadoEnum.RCHZ) {
            Alumno alumno = retiroCiclobd.getAlumno();
            CicloAcademico cicloAcademico = ds.getCicloAcademico();

            MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademico);
            matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.INH);
            matriculaResumenDAO.update(matriculaResumen);

            List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allByMatriculaResumen(matriculaResumen);
            List<Curso> cursos = matriculaCursos.stream().map(x -> x.getCurso()).collect(Collectors.toList());
            for (MatriculaCurso matriculaCurso : matriculaCursos) {
                matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.RET);
                matriculaCursoDAO.update(matriculaCurso);
            }

            List<MatriculaSimultaneo> matriculaSimultaneos = matriculaSimultaneoDAO.allByMatriculaCurso(matriculaCursos);
            for (MatriculaSimultaneo matriculaSimultaneo : matriculaSimultaneos) {
                matriculaSimultaneoDAO.delete(matriculaSimultaneo);
            }

            List<MatriculaSeccion> matriculaSeccions = matriculaSeccionDAO.allByMatriculaResumen(matriculaResumen);
            for (MatriculaSeccion matriculaSeccion : matriculaSeccions) {
                matriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.RET);
                matriculaSeccionDAO.update(matriculaSeccion);

                Seccion seccion = matriculaSeccion.getSeccion();
                if (cicloAcademico.getTipoEnum() == TipoCicloEnum.REG) {
                    seccion.setPrematriculados(seccion.getPrematriculados() - 1);
                }
                seccion.setMatriculados(seccion.getMatriculados() - 1);
                seccionDAO.update(seccion);

                VacanteAlumno vacanteAlumno = vacanteAlumnoDAO.allByAlumnoAndSeccion(alumno, seccion);
                vacanteAlumno.setEstadoEnum(EstadoVacanteAlumnoEnum.DISP);
                vacanteAlumno.setUserModificacion(null);
                vacanteAlumno.setFechaModificacion(null);
                vacanteAlumno.setAlumno(null);
                vacanteAlumnoDAO.update(vacanteAlumno);

            }
            for (Curso curso : cursos) {

                AlumnoCursoCurricula alumnoCursoCurricula = alumnoCursoCurriculaDAO.findByAlumnoCurso(alumno, curso);
                alumnoCursoCurricula.setEstadoMatriculaEnum(EstadoMatriculaEnum.RET);
                alumnoCursoCurriculaDAO.delete(alumnoCursoCurricula);
            }
            // Consultar si existe algun pago al matricularse.
        }
    }

}
