package pe.edu.lamolina.pivot.dao.consejeria;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.general.Persona;

public interface ConsejeroDAO extends EasyDAO<Consejero> {

    public List<Consejero> allByCarreraDynatable(DynatableFilter filter);

    public List<DepartamentoAcademico> allByIdFacultad(String facultadid);

    public List<Docente> allByNombreAndDeparts(String nombre, List<DepartamentoAcademico> departs);

    public Consejero finByIdPersona(Persona persona);

    public List<Carrera> findAllCarreraByIdDocente(long idDocente);
    
}
