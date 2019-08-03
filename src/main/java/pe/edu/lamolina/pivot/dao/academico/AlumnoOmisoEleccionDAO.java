package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.AlumnoOmisoEleccion;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface AlumnoOmisoEleccionDAO extends EasyDAO<AlumnoOmisoEleccion> {

    public List<AlumnoOmisoEleccion> allOrder(DynatableFilter filter);

    public void updateAnulacion(AlumnoOmisoEleccion alumnoOmisoEleccion);

    public List<AlumnoOmisoEleccion> allByCiclo(List<CicloAcademico> cicloAcademicos);

    public AlumnoOmisoEleccion findByAlumnoCicloMotivo(AlumnoOmisoEleccion omisoEleccion);

}
