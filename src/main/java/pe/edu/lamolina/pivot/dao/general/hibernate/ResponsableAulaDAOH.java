package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TipoResponsableEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.ResponsableAula;
import pe.edu.lamolina.pivot.dao.general.ResponsableAulaDAO;

@Repository
public class ResponsableAulaDAOH extends AbstractEasyDAO<ResponsableAula> implements ResponsableAulaDAO {

    public ResponsableAulaDAOH() {
        this.setClazz(ResponsableAula.class);
    }

    @Override
    public List<ResponsableAula> allByPersona(Persona personaResponsable, EstadoEnum... estados) {
        return this.allByPersona(Arrays.asList(personaResponsable), estados);
    }

    @Override
    public List<ResponsableAula> allByPersona(List<Persona> personaResponsable, EstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(ResponsableAula.class, "ra")
                .join("persona per", "aula au", "turnoAtencionAula ta")
                .join("au.tipoAula tpa")
                .in("per.id", personaResponsable)
                .in("ra.estado", Arrays.asList(estados));
        return all(sql);
    }

    //DEPRECATED
    @Override
    public List<ResponsableAula> allByAulas(List<Aula> aulas, EstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(ResponsableAula.class, "ra")
                .join("persona per", "aula au", "turnoAtencionAula ta")
                .in("au.id", aulas)
                .in("ra.estado", Arrays.asList(estados));
        return all(sql);
    }

    @Override
    public List<ResponsableAula> allByEstado(EstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(ResponsableAula.class, "ra")
                .join("persona per", "turnoAtencionAula ta", "aula au", "au.tipoAula tpa")
                .in("ra.estado", Arrays.asList(estados));
        return all(sql);
    }

    @Override
    public List<ResponsableAula> allByResponsableAulas(DynatableFilter filter, EstadoEnum... estados) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ResponsableAula.class, "ra")
                .join("persona per")
                .in("ra.estado", Arrays.asList(estados))
                .searchFields("per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))");
        return all(sql);
    }

    @Override
    public ResponsableAula findByPersonaAndTipo(Persona persona, TipoResponsableEnum tipoResponsableEnum, EstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(ResponsableAula.class, "ra")
                .join("persona per")
                .filter("per.id", persona)
                .filter("ra.tipo", tipoResponsableEnum)
                .in("ra.estado", Arrays.asList(estados));
        return find(sql);
    }

}
