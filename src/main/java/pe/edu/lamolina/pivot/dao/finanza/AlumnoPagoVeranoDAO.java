package pe.edu.lamolina.pivot.dao.finanza;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.finanzas.AlumnoPagoVerano;

public interface AlumnoPagoVeranoDAO extends EasyDAO<AlumnoPagoVerano> {

    public AlumnoPagoVerano findAlumnoByCiclo(Alumno alumno, CicloAcademico cicloAcademico);

    public void updateColumns(AlumnoPagoVerano pagoVeranoDb, String... deuda);

}
