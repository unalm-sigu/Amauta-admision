package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Insecto;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.tramite.SancionDisciplinaCicloDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.SancionDisciplina;
import pe.edu.lamolina.model.tramite.SancionDisciplinaCiclo;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.ACEP;

import java.util.Collections;
import java.util.List;

@Repository
public class SancionDisciplinaCicloDAOH extends AbstractEasyDAO<SancionDisciplinaCiclo> implements SancionDisciplinaCicloDAO {

    public SancionDisciplinaCicloDAOH() {
        super();
        setClazz(SancionDisciplinaCiclo.class);
    }

    @Override
    public int saveAll(List<SancionDisciplinaCiclo> ciclosAcademicos) {
        if (ciclosAcademicos.isEmpty()) {
            return 0;
        }

        Insecto sql = Insecto.createInsert()
                .into(SancionDisciplinaCiclo.class)
                .columns("sancionDisciplina", "ciclo")
                .values(ciclosAcademicos);

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();

        return rows;
    }

    @Override
    public List<SancionDisciplinaCiclo> findBySancionDisciplina(SancionDisciplina sancion) {
        Octavia sql = new Octavia()
                .from(SancionDisciplinaCiclo.class, "sdc")
                .join("sancionDisciplina sd")
                .filter("sd.id", sancion.getId());
        return all(sql);
    }

    @Override
    public List<SancionDisciplinaCiclo> allSancionDisciplinaByResolucion(Resolucion resolucionDB) {
        Octavia sql = new Octavia()
                .from(SancionDisciplinaCiclo.class, "sc")
                .join("sancionDisciplina sd","ciclo cl")
                .join("sd.alumno al", "al.persona per")
                .join("sd.resolucion re")
                .filter("re.id", resolucionDB)
                .orderBy("per.paterno");
        return all(sql);

    }

    @Override
    public List<SancionDisciplinaCiclo> findAlumnosSancionadosPorCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = new Octavia()
                .from(SancionDisciplinaCiclo.class, "sdc")
                .join("sancionDisciplina sd","ciclo cl")
                .filter("cl.id", cicloAcademico.getId())
                .filter("sd.estado", ACEP);
        return all(sql);
    }
}
