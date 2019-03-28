package pe.edu.lamolina.pivot.dao.finanza.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;
import pe.edu.lamolina.pivot.dao.finanza.DeudaAlumnoDAO;

@Repository
public class DeudaAlumnoDAOH extends AbstractEasyDAO<DeudaAlumno> implements DeudaAlumnoDAO {

    public DeudaAlumnoDAOH() {
        super();
        setClazz(DeudaAlumno.class);
    }

    @Override
    public List<DeudaAlumno> allByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia subQuery = new Octavia()
                .from(AporteAlumnoCiclo.class, "aac")
                .join("deudaAlumno deu", "aporteCiclo ac", "ac.cicloAcademico ca")
                .filter("ca.id", ciclo);

        Octavia sql = Octavia.query()
                .from(DeudaAlumno.class, "da")
                .join("alumno alu", "cuentaBancaria")
                .exists(subQuery)
                .linkedBy("da.id", "deu.id")
                .filter("alu.id", alumno.getId());
        return all(sql);
    }

    @Override
    public DeudaAlumno allByAlumnoCicloAndCuenta(Alumno alumno, CicloAcademico ciclo, CuentaBancaria bancaria) {
        Octavia subQuery = new Octavia()
                .from(AporteAlumnoCiclo.class, "aac")
                .join("deudaAlumno deu", "aporteCiclo ac", "ac.cicloAcademico ca", "ac.aporte apo")
                .filter("ca.id", ciclo);

        Octavia sql = Octavia.query()
                .from(DeudaAlumno.class, "da")
                .join("alumno alu", "cuentaBancaria cb")
                .exists(subQuery)
                .linkedBy("da.id", "deu.id")
                .filter("alu.id", alumno.getId())
                .filter("estado", DeudaEstadoEnum.DEU)
                .filter("cb.id", bancaria);
        return find(sql);
    }

    @Override
    public List<DeudaAlumno> allByCiclo(CicloAcademico cicloAcademico) {
        Octavia subQuery = new Octavia()
                .from(AporteAlumnoCiclo.class, "aac")
                .join("deudaAlumno deu", "aporteCiclo ac", "ac.cicloAcademico ca", "ac.aporte apo")
                .filter("ca.id", cicloAcademico);

        Octavia sql = Octavia.query()
                .from(DeudaAlumno.class, "da")
                .join("alumno alu", "cuentaBancaria cb")
                .exists(subQuery)
                .linkedBy("da.id", "deu.id")
                .filter("estado", DeudaEstadoEnum.DEU);
        return all(sql);
    }
}
