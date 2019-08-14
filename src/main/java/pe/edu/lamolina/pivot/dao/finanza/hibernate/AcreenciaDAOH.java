package pe.edu.lamolina.pivot.dao.finanza.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.bienestar.TallerCiclo;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.deuda.DeudaDTO;
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

    @Override
    public List<DeudaDTO> allDeudasRepetidasAffetDate(DateTime dateTime) {
        Octavia sql = Octavia.query()
                .select("per.id", "per.numeroDocIdentidad", "acre.tabla", "acre.estado",
                        "acre.monto", "cb.id", "acre.descripcion", " count(*) ")
                .into(DeudaDTO.class)
                .from(Acreencia.class, "acre")
                .join("persona per", "cuentaBancaria cb")
                .filter("acre.fechaRegistro", ">", dateTime.toDate())
                .groupBy("per.id", "per.numeroDocIdentidad",
                        "acre.tabla", "acre.estado",
                        "acre.monto", "cb.id", "acre.descripcion");
        List<DeudaDTO> deudas = (List<DeudaDTO>) sql.all(getCurrentSession());
        return deudas.stream().filter(x -> x.getCantidad() > 1).collect(Collectors.toList());
    }

    @Override
    public List<Acreencia> allByPersonasAndEstado(List<Persona> personas, DeudaEstadoEnum... deudaEstadoEnums) {
        Octavia sql = new Octavia()
                .from(Acreencia.class, "acr")
                .join("persona per", "oficina ofi", "cuentaBancaria")
                .in("per.id", personas);
        if (deudaEstadoEnums != null) {
            sql.in("acr.estado", Arrays.asList(deudaEstadoEnums));
        }
        return all(sql);
    }

    @Override
    public void updateColumns(Acreencia acreecia, String... columns) {
        Octavia sql = Octavia.update(Acreencia.class, "acre");
        for (String column : columns) {
            sql.set(acreecia, column);
        }
        this.update(sql);
    }

}
