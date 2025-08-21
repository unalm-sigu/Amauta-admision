package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.tramite.SancionDisciplinaDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.TipoRetiroCicloEnum;
import pe.edu.lamolina.model.tramite.*;

import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.ACEP;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.PEND;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.SOL;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static pe.edu.lamolina.model.enums.TipoRetiroCicloEnum.EXCEP;

@Repository
public class SancionDisciplinaDAOH extends AbstractEasyDAO<SancionDisciplina> implements SancionDisciplinaDAO {

    public SancionDisciplinaDAOH() {
        super();
        setClazz(SancionDisciplina.class);
    }

    @Override
    public List<SancionDisciplina> allByCicloDynatable(CicloAcademico cicloAcademico, DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(SancionDisciplina.class, "sd")
                .join("tramite tr", "tr.alumno al", "al.persona per")
                .join("al.carrera car", "car.facultad")
                .left("resolucion")
                .searchFields("per.numeroDocIdentidad", "al.codigo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("sd.id desc");

        return all(sql);
    }

    @Override
    public List<SancionDisciplina> allSancionDisciplina() {
        Octavia sql = new Octavia()
                .from(SancionDisciplina.class, "sd")
                .join("tramite tr", "tr.alumno al", "al.persona per")
                .join("per.tipoDocumento")
                .beginBlock()
                .in("sd.estado", Arrays.asList(PEND,SOL))
                .endBlock();
        return all(sql);
    }

    @Override
    public List<SancionDisciplina> allSancionDisciplinaByResolucion(Resolucion resolucionDB) {
        Octavia sql = new Octavia()
                .from(SancionDisciplina.class, "sd")
                .join("alumno al", "al.persona per")
                .join("resolucion re")
                .filter("re.id", resolucionDB)
                .orderBy("per.paterno");
        return all(sql);

    }

    @Override
    public SancionDisciplina findBySancion(Alumno alumnoDB) {
        Octavia sql = new Octavia()
                .from(SancionDisciplina.class, "sd")
                .join("tramite tram", "cicloAcademico ", "tram.alumno al")
                .beginBlock()
                .in("sd.estado", Arrays.asList(PEND, SOL))
                .endBlock()
                .filter("al.id", alumnoDB);

        return find(sql);
    }

    @Override
    public SancionDisciplina findByAlumnoAct(Alumno alumno) {
        Octavia sql = new Octavia();
        sql.from(SancionDisciplina.class, "sd")
                .join("tramite tr", "tr.alumno al", "al.persona", "al.carrera car")
                .join("car.facultad")
                .filter("sd.estado", SOL)
                .filter("al.id", alumno);

        return find(sql);
    }

    @Override
    public List<SancionDisciplina> findAlumnosSancionadosPorCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = new Octavia();
        return null;
    }
}
