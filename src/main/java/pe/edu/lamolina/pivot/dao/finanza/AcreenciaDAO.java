package pe.edu.lamolina.pivot.dao.finanza;

import java.util.List;
import org.joda.time.DateTime;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.bienestar.TallerCiclo;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.deuda.DeudaDTO;

public interface AcreenciaDAO extends EasyDAO<Acreencia> {

    public Acreencia findPersonaAndTaller(TallerCiclo tallerCiclo, Persona persona);

    public List<Acreencia> allDeudaByPersona(Oficina oficina, Long idPersona);

    public List<Acreencia> allAnuByPersona(Oficina oficina, Long idPersona);

    public Acreencia findByDeudaAlumno(DeudaAlumno deudaAlumno);

    public List<Acreencia> allByDeudaAlumno(List<DeudaAlumno> allDeudaAlumno);

    List<DeudaDTO> allDeudasRepetidasAffetDate(DateTime dateTime);

    public List<Acreencia> allByPersonasAndEstado(List<Persona> personas, DeudaEstadoEnum... deudaEstadoEnums);

    public void updateColumns(Acreencia acreenciaUpd, String... estadoEnum);

}
