package pe.edu.lamolina.pivot.dao.finanza.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.finanzas.PagoHoraDocente;
import pe.edu.lamolina.pivot.dao.finanza.PagoHoraDocenteDAO;

@Repository
public class PagoHoraDocenteDAOH extends AbstractEasyDAO<PagoHoraDocente> implements PagoHoraDocenteDAO {

    public PagoHoraDocenteDAOH() {
        super();
        setClazz(PagoHoraDocente.class);
    }

    @Override
    public PagoHoraDocente findByCicloMatriculados(CicloAcademico cicloAcademico, Integer matriculados) {
        Octavia sql = new Octavia()
                .from(PagoHoraDocente.class, "phd")
                .join("cicloAcademico ci")
                .filter("ci.id", cicloAcademico)
                .filter("phd.alumnosInicio","<=", matriculados)
                .filter("phd.alumnosFin",">=", matriculados);
        return find(sql);
    }
}
