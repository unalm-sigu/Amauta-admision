package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.confignotanivelacion.dto.PuntajeMaxMinDTO;
import pe.edu.lamolina.amauta.dao.academico.PrelamolinaDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.Prelamolina;

@Repository
public class PrelamolinaDAOH extends AbstractEasyDAO<Prelamolina> implements PrelamolinaDAO {

    public PrelamolinaDAOH() {
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

    @Override
    public PuntajeMaxMinDTO findPuntajeMatematicasByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .select("min(pre.puntajeAritmetica+pre.puntajeAlgebra+pre.puntajeGeometria+pre.puntajeTrigonometria)",
                        "max(pre.puntajeAritmetica+pre.puntajeAlgebra+pre.puntajeGeometria+pre.puntajeTrigonometria)")
                .into(PuntajeMaxMinDTO.class)
                .from(Prelamolina.class, "pre")
                .join("cicloPostula cp", "cp.cicloAcademico ci")
                .filter("ci.id", ciclo)
                .filter("esIngresante", 1);

        return (PuntajeMaxMinDTO) sql.find(getCurrentSession());
    }

}
