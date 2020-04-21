package pe.edu.lamolina.amauta.dao.aporte.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.aporte.SaldoAfavorAlumno;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.amauta.dao.aporte.SaldoAfavorAlumnoDAO;

@Repository
public class SaldoAfavorAlumnoDAOH extends AbstractEasyDAO<SaldoAfavorAlumno> implements SaldoAfavorAlumnoDAO {

    public SaldoAfavorAlumnoDAOH() {
        super();
        setClazz(SaldoAfavorAlumno.class);
    }

    @Override
    public List<SaldoAfavorAlumno> allByAlumno(Alumno alumnoDB) {
        Octavia sql = Octavia.query()
                .from(SaldoAfavorAlumno.class, "saa")
                .join("alumno a", "cuentaBancaria cb")
                .filter("a.id", alumnoDB);
        return all(sql);
    }

    @Override
    public SaldoAfavorAlumno findByAlumnoCuentaBank(Alumno alumno, CuentaBancaria cuentaBancaria) {
        Octavia sql = Octavia.query()
                .from(SaldoAfavorAlumno.class, "saa")
                .join("alumno a", "cuentaBancaria cb")
                .filter("cb.id", cuentaBancaria)
                .filter("a.id", alumno);
        return find(sql);
    }

    @Override
    public SaldoAfavorAlumno find(SaldoAfavorAlumno saldoAfavorAlumno) {

        Octavia sql = Octavia.query()
                .from(SaldoAfavorAlumno.class, "saa")
                .join("alumno a", "cuentaBancaria cb")
                .filter("saa.id", saldoAfavorAlumno);
        return find(sql);
    }

    @Override
    public List<SaldoAfavorAlumno> allByPersonas(List<Persona> personas) {
        Octavia sql = Octavia.query()
                .from(SaldoAfavorAlumno.class, "saa")
                .join("alumno a", "a.persona per", "cuentaBancaria cb")
                .in("per.id", personas);
        return all(sql);
    }

}
