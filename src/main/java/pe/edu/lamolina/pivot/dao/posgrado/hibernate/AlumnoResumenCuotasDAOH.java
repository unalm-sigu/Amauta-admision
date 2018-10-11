package pe.edu.lamolina.pivot.dao.posgrado.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.posgrado.AlumnoResumenCuotas;
import pe.edu.lamolina.pivot.dao.posgrado.AlumnoResumenCuotasDAO;

@Repository
public class AlumnoResumenCuotasDAOH extends AbstractEasyDAO<AlumnoResumenCuotas> implements AlumnoResumenCuotasDAO {

    public AlumnoResumenCuotasDAOH() {
        super();
        setClazz(AlumnoResumenCuotas.class);
    }

    @Override
    public AlumnoResumenCuotas findByAlumnoAndCiclo(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoResumenCuotas.class, "cp")
                .join("cicloAcademico ca", "alumno alu")
                .filter("alu.id", alumno)
                .filter("ca.id", cicloAcademico);
        return find(sql);
    }

}
