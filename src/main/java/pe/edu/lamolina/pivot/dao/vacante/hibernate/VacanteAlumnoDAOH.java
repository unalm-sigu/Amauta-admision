package pe.edu.lamolina.pivot.dao.vacante.hibernate;

import java.util.Arrays;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoVacanteAlumnoEnum;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;
import pe.edu.lamolina.pivot.dao.vacante.VacanteAlumnoDAO;

@Repository
public class VacanteAlumnoDAOH extends AbstractEasyDAO<VacanteAlumno> implements VacanteAlumnoDAO {

    public VacanteAlumnoDAOH() {
        super();
        setClazz(VacanteAlumno.class);
    }

    @Override
    public List<VacanteAlumno> allBySeccion(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(VacanteAlumno.class, "va")
                .join("seccion se", "alumno alu")
                .in("se.id", secciones);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<VacanteAlumno> allActivosBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(VacanteAlumno.class, "va")
                .join("seccion se")
                .left("alumno alu")
                .filter("se.id", seccion);
        sql.in("va.estado", Arrays.asList(EstadoVacanteAlumnoEnum.DISP.name(), EstadoVacanteAlumnoEnum.RSV.name(), EstadoVacanteAlumnoEnum.RSVR.name(), EstadoVacanteAlumnoEnum.OCUP.name()));
        return sql.all(getCurrentSession());
    }

    @Override
    public List<VacanteAlumno> allByAlumno(Alumno alumno) {
        Octavia sql = Octavia.query()
                .from(VacanteAlumno.class, "va")
                .join("seccion se", "alumno alu")
                .filter("alu.id", alumno);
        return sql.all(getCurrentSession());
    }

    @Override
    public void deleteAllByCiclo(CicloAcademico cicloAcademico) {

        StringBuilder sql = new StringBuilder();
        sql.append("  delete from ").append(VacanteAlumno.class.getName()).append(" va ");
        sql.append("  where va.alumno.id in ( ");
        sql.append("    select ah.alumno.id  from ").append(AlumnoHorario.class.getName()).append(" ah ");
        sql.append("    where ah.cicloAcademico.id = :CICLO ");
        sql.append("  ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", cicloAcademico.getId());
        query.executeUpdate();
    }

    @Override
    public void updateEstadoFechaModUsuarioMod(VacanteAlumno vacanteAlumno) {
        Octavia octavia = Octavia.update(VacanteAlumno.class);
        octavia.set(vacanteAlumno, "estado");
        octavia.set(vacanteAlumno, "userModificacion");
        octavia.set(vacanteAlumno, "fechaModificacion");
        this.update(vacanteAlumno);
    }
}
