package pe.edu.lamolina.pivot.dao.aporte;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.aporte.SaldoAfavorAlumno;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.general.Persona;

public interface SaldoAfavorAlumnoDAO extends EasyDAO<SaldoAfavorAlumno> {

    List<SaldoAfavorAlumno> allByAlumno(Alumno alumnoDB);

    public SaldoAfavorAlumno findByAlumnoCuentaBank(Alumno alumno, CuentaBancaria cuentaBancaria);

    public SaldoAfavorAlumno find(SaldoAfavorAlumno saldoAfavorAlumno);

    List<SaldoAfavorAlumno> allByPersonas(List<Persona> personas);

}
