package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.InformeFinalTutoriaDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.tutoria.InformeFinalTutoria;

@Repository
public class InformeFinalTutoriaDAOH extends AbstractEasyDAO<InformeFinalTutoria> implements InformeFinalTutoriaDAO {

    public InformeFinalTutoriaDAOH() {
        super();
        setClazz(InformeFinalTutoria.class);
    }

    @Override
    public InformeFinalTutoria find(long id) {
        Octavia sql = Octavia.query()
                .from(InformeFinalTutoria.class, "inf")
                .join("consejero con", "cicloAcademico ci", "carrera ca", "tipoDocumento td")
                .filter("inf.id", id);

        return find(sql);
    }

    @Override
    public InformeFinalTutoria findActivoByConsejeroCiclo(Consejero consejero, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(InformeFinalTutoria.class, "inf")
                .join("consejero con", "cicloAcademico ci", "carrera ca", "tipoDocumento td")
                .leftJoin("userAceptacion uac", "uac.persona")
                .filter("con.id", consejero)
                .filter("ci.id", ciclo)
                .filter("estado", "<>", EstadoEnum.PEN);

        return find(sql);
    }

    @Override
    public InformeFinalTutoria findPendienteByConsejeroCiclo(Consejero consejero, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(InformeFinalTutoria.class, "inf")
                .join("consejero con", "cicloAcademico ci", "carrera ca", "tipoDocumento td")
                .filter("con.id", consejero)
                .filter("ci.id", ciclo)
                .filter("estado", EstadoEnum.PEN);

        return find(sql);
    }

    @Override
    public InformeFinalTutoria findByConsejeroCiclo(Consejero consejero, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(InformeFinalTutoria.class, "inf")
                .join("consejero con", "cicloAcademico ci", "carrera ca", "tipoDocumento td")
                .filter("con.id", consejero)
                .filter("ci.id", ciclo);

        return find(sql);
    }

    @Override
    public List<InformeFinalTutoria> allActivosByConsejerosCiclo(List<Consejero> consejeros, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(InformeFinalTutoria.class, "inf")
                .join("consejero con", "cicloAcademico ci", "carrera ca", "tipoDocumento td")
                .in("con.id", consejeros)
                .filter("ci.id", ciclo)
                .filter("estado", "<>", EstadoEnum.PEN);

        return all(sql);
    }

}
