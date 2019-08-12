package pe.edu.lamolina.pivot.dao.aporte.hibernate;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import static pe.edu.lamolina.model.enums.AportesEnum.A05;
import pe.edu.lamolina.model.enums.EstadoAporteEnum;
import pe.edu.lamolina.pivot.dao.aporte.AporteAlumnoCicloDAO;

@Repository
public class AporteAlumnoCicloDAOH extends AbstractEasyDAO<AporteAlumnoCiclo> implements AporteAlumnoCicloDAO {

    public AporteAlumnoCicloDAOH() {
        super();
        setClazz(AporteAlumnoCiclo.class);
    }

    @Override
    public List<AporteAlumnoCiclo> allByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(AporteAlumnoCiclo.class, "aac")
                .join("aporteCiclo ac", "ac.aporte apo", "ac.cicloAcademico ca")
                .join("ac.cuentaBancaria cta")
                .join("resumenAporteAlumno raa", "raa.matriculaResumen mr", "mr.alumno alu")
                .filter("alu.id", alumno)
                .filter("ca.id", ciclo)
                .in("aac.estado", Arrays.asList(EstadoAporteEnum.DEBE, EstadoAporteEnum.PAGO))
                .orderBy("aac.numeroCuota", "apo.nombre");
        return all(sql);
    }

    @Override
    public List<AporteAlumnoCiclo> allAporteCarnetByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AporteAlumnoCiclo.class, "aac")
                .join("aporteCiclo ac", "ac.aporte apo", "ac.cicloAcademico ca")
                .join("ac.cuentaBancaria cta")
                .join("resumenAporteAlumno raa", "raa.matriculaResumen mr", "mr.alumno alu")
                .filter("ca.codigo", cicloAcademico.getCodigo())
                .filter("apo.codigo", A05.name().substring(1))
                .in("aac.estado", Arrays.asList(EstadoAporteEnum.DEBE, EstadoAporteEnum.PAGO))
                .orderBy("aac.numeroCuota", "apo.nombre");
        return all(sql);
    }

}
