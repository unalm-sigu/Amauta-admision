package pe.edu.lamolina.pivot.dao.finanza.hibernate;

import java.util.List;
import java.util.stream.Collectors;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.model.bienestar.TallerCiclo;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.dao.finanza.AcreenciaDAO;

@Repository
public class AcreenciaDAOH extends AbstractEasyDAO<Acreencia> implements AcreenciaDAO {

    public AcreenciaDAOH() {
        super();
        setClazz(Acreencia.class);
    }

    @Override
    public Acreencia findPersonaAndTaller(TallerCiclo tallerCiclo, Persona persona) {
        Octavia sql = new Octavia()
                .from(Acreencia.class, "acr")
                .join("persona per")
                .filter("per.id", persona)
                .filter("instanciaTabla", tallerCiclo.getId());
        return find(sql);
    }

    @Override
    public List<Acreencia> allDeudaByPersona(Oficina oficina, Long idPersona) {
        Octavia sql = new Octavia()
                .from(Acreencia.class, "acr")
                .join("persona per", "oficina ofi", "cuentaBancaria")
                .filter("per.id", idPersona)
                .filter("estado", DeudaEstadoEnum.DEU)
                .filter("ofi.id", oficina);
        return all(sql);
    }

    @Override
    public List<Acreencia> allAnuByPersona(Oficina oficina, Long idPersona) {
        Octavia sql = new Octavia()
                .from(Acreencia.class, "acr")
                .join("persona per", "oficina ofi", "cuentaBancaria")
                .filter("per.id", idPersona)
                .filter("estado", DeudaEstadoEnum.ANU)
                .filter("ofi.id", oficina);
        return all(sql);
    }

    @Override
    public Acreencia findByDeudaAlumno(DeudaAlumno deudaAlumno) {
        Octavia sql = new Octavia()
                .from(Acreencia.class, "acr")
                .join("persona per", "oficina ofi", "cuentaBancaria")
                .filter("estado", DeudaEstadoEnum.DEU)
                .filter("instanciaTabla", deudaAlumno.getId());
        return find(sql);
    }

    @Override
    public List<Acreencia> allByDeudaAlumno(List<DeudaAlumno> allDeudaAlumno) {
        List<Long> id = allDeudaAlumno.stream().map(DeudaAlumno::getId).collect(Collectors.toList());
        Octavia sql = new Octavia()
                .from(Acreencia.class, "acr")
                .join("persona per", "oficina ofi", "cuentaBancaria")
                .filter("estado", DeudaEstadoEnum.DEU)
                .in("instanciaTabla", id);
        return all(sql);
    }

}
