package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.academico.PrelamolinaDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.Prelamolina;

@Repository
public class PrelamolindaDAOH extends AbstractEasyDAO<Prelamolina> implements PrelamolinaDAO {

    public PrelamolindaDAOH() {
        super();
        setClazz(Prelamolina.class);
    }

    @Override
    public List<Prelamolina> allInscritosByCicloAcademico(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Prelamolina.class, "pre")
                .join("cicloPostula cp", "cp.cicloAcademico ci", "postulante po")
                .filter("ci.id", ciclo)
                .filter("estado", PostulanteEstadoEnum.INS.name());

        return all(sql);
    }

    @Override
    public List<Prelamolina> allIngresanteByCiclo(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(Prelamolina.class, "pre")
                .join("cicloPostula cp", "cp.cicloAcademico ci")
                .filter("cp.id", ciclo)
                .filter("esIngresante", 1);

        return all(sql);
    }

    @Override
    public Prelamolina findIngresanteByPostulante(Postulante postulante) {
        Octavia sql = Octavia.query()
                .from(Prelamolina.class, "pre")
                .join("cicloPostula cp", "cp.cicloAcademico ci", "postulante po")
                .filter("po.id", postulante)
                .filter("esIngresante", 1);

        return find(sql);
    }

}
