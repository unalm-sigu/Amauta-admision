package pe.edu.lamolina.pivot.dao.finanza.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.finanzas.AlumnoPagoVerano;
import pe.edu.lamolina.pivot.dao.finanza.AlumnoPagoVeranoDAO;

@Repository
public class AlumnoPagoVeranoDAOH extends AbstractEasyDAO<AlumnoPagoVerano> implements AlumnoPagoVeranoDAO {

    public AlumnoPagoVeranoDAOH() {
        super();
        setClazz(AlumnoPagoVerano.class);
    }

    @Override
    public AlumnoPagoVerano findAlumnoByCiclo(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = new Octavia()
                .from(AlumnoPagoVerano.class, "apv")
                .join("cicloAcademico ca", "alumno al", "cuentaBancaria")
                .filter("al.id", alumno)
                .filter("ca.id", cicloAcademico);

        return find(sql);
    }

    @Override
    public void updateColumns(AlumnoPagoVerano alumnoPagoVerano, String... params) {
        Octavia octavia = Octavia.update(AlumnoPagoVerano.class);
        for (String column : params) {
            octavia.set(alumnoPagoVerano, column);
        }

        this.update(octavia);
    }
}
