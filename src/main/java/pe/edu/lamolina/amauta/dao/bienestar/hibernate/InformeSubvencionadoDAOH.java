package pe.edu.lamolina.amauta.dao.bienestar.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.bienestar.InformeSubvencionadoDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.bienestar.InformeSubvencionado;
import pe.edu.lamolina.model.general.Persona;

@Repository
public class InformeSubvencionadoDAOH extends AbstractEasyDAO<InformeSubvencionado> implements InformeSubvencionadoDAO {

    public InformeSubvencionadoDAOH() {
        super();
        setClazz(InformeSubvencionado.class);
    }

    @Override
    public List<InformeSubvencionado> allBySupervisorCiclo(Persona supervisor, CicloAcademico ciclo, DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(InformeSubvencionado.class, "isb")
                .join("alumnoSubvencionado asb", "asb.cicloAcademico ci", "asb.alumno alu", "asb.tipoSubvencion")
                .join("alu.persona per", "alu.carrera car", "car.facultad")
                .join("calendarioInforme cal", "cal.mes mes")
                .join("supervisorVoBo sp", "sp.persona sper")
                .leftJoin("personaCuentaBancaria pcb", "pcb.banco ba", "ba.empresa")
                .leftJoin("archivoInforme", "per.tipoDocumento")
                .filter("sper.id", supervisor)
                .filter("ci.id", ciclo)
                .orderBy("cal.year DESC", "mes.codigo DESC", "isb.id DESC");

        return all(sql);
    }
}
