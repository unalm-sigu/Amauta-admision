package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;

@Repository
public class MatriculaSeccionDAOH extends AbstractEasyDAO<MatriculaSeccion> implements MatriculaSeccionDAO {

    public MatriculaSeccionDAOH() {
        super();
        setClazz(MatriculaSeccion.class);
    }

    @Override
    public List<MatriculaSeccion> allBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs")
                .join("gs.curso cur", "alu.persona per", "per.tipoDocumento tdoc")
                .leftJoin("alu.carrera carr", "carr.facultad fac")
                .filter("ms.estado", EstadoMatriculaEnum.MAT.name())
                .filter("sec.id", seccion)
                .orderBy("per.paterno", "per.materno", "per.nombres");

        return all(sql);
    }

    @Override
    public MatriculaSeccion find(Long id) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs")
                .join("gs.curso cur", "alu.persona per", "per.tipoDocumento tdoc")
                .filter("ms.id", id);

        return find(sql);
    }

    @Override
    public MatriculaSeccion findByAlumnoSeccion(Alumno alumno, Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs")
                .join("gs.curso cur", "alu.persona per", "per.tipoDocumento tdoc")
                .filter("sec.id", seccion)
                .filter("alu.id", alumno);

        return find(sql);
    }

    @Override
    public List<MatriculaSeccion> allByMatriculaSeccion(MatriculaResumen resumen) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .join("gs.curso cur", "alu.persona per", "per.tipoDocumento tdoc")
                .filter("mr.id", resumen);

        return all(sql);
    }

    @Override
    public List<MatriculaSeccion> allByGpoSeccion(GrupoSeccion grupoSeccion, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .join("gs.curso cur", "alu.persona per", "per.tipoDocumento tdoc")
                .filter("ms.estado", MAT)
                .filter("gs.id", grupoSeccion)
                .filter("ca.id", ciclo);

        return all(sql);
    }

    @Override
    public List<MatriculaSeccion> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(MatriculaSeccion.class, "ms")
                .join("matriculaResumen mr", "seccion sec", "mr.alumno alu", "sec.grupoSeccion gs", "gs.cicloAcademico ca")
                .join("gs.curso cur", "alu.persona per", "per.tipoDocumento tdoc")
                .leftJoin("gs.planCalificacion")
                .filter("ms.estado", MAT)
                .filter("ca.id", ciclo);

        return all(sql);
    }

}
