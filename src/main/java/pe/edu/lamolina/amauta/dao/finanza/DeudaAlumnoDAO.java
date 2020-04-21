package pe.edu.lamolina.amauta.dao.finanza;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.finanzas.AlumnoPagoVerano;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;

public interface DeudaAlumnoDAO extends EasyDAO<DeudaAlumno> {

    List<DeudaAlumno> allByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    DeudaAlumno allByAlumnoCicloAndCuenta(Alumno alumno, CicloAcademico ciclo, CuentaBancaria bancaria);

    List<DeudaAlumno> allByCiclo(CicloAcademico cicloAcademico);

    List<DeudaAlumno> allById(List<DeudaAlumno> ids);

    List<DeudaAlumno> allDeudaAlumnoByCicloAlumno(List<Alumno> alumnos, CicloAcademico cicloAcademico);

    public DeudaAlumno allByAlumnoPagoVerano(AlumnoPagoVerano pagoVeranoDb);

    public void updateColumns(DeudaAlumno deudaAlumno, String... params);
}
