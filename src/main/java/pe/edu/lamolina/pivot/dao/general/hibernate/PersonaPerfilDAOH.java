package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import pe.edu.lamolina.pivot.dao.general.PersonaPerfilDAO;
import pe.edu.lamolina.pivot.model.general.PersonaPerfil;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.general.Oficina;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Repository
public class PersonaPerfilDAOH extends AbstractEasyDAO<PersonaPerfil> implements PersonaPerfilDAO {
    
    public PersonaPerfilDAOH() {
        super();
        setClazz(PersonaPerfil.class);
    }
    
    @Override
    public PersonaPerfil find(long id) {
        Octavia sql = Octavia.query()
                .from(PersonaPerfil.class, "pp")
                .join("perfilCompania", "persona")
                .leftJoin("oficina")
                .filter("pp.id", id);
        
        return find(sql);
        
    }
    
    @Override
    public List<PersonaPerfil> allByFiltersDynaTable(DynatableFilter filter) {
        
        DynatableSql sql = new DynatableSql(filter)
                .from(PersonaPerfil.class, "pp")
                .join("perfilCompania peco", "persona per")
                .leftJoin("oficina")
                .searchFields("peco.nombre", "ofi.tipo", "ofi.nombre")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesceperpj.materno,''))")
                .orderBy("pp.id DESC");
        
        return all(sql);
    }
    
    @Override
    public List<PersonaPerfil> allByPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(PersonaPerfil.class, "pp")
                .join("perfilCompania", "persona per")
                .leftJoin("oficina")
                .filter("per.id", persona);
        
        return all(sql);
    }
    
    @Override
    public PersonaPerfil findSinCerrar(Oficina oficina, Compania cia) {
        Octavia sql = Octavia.query()
                .from(PersonaPerfil.class, "pp")
                .join("perfilCompania peco", "persona per", "compania cia", "oficina ofi")
                .filter("ofi.id", oficina)
                .filter("peco.id", oficina.getCargoJefe())
                .filter("per.id", oficina.getPersonaJefe())
                .filter("cia.id", cia)
                .filter("pp.estado", EstadoEnum.ACT)
                .isNull("pp.fechaFin");
        
        return find(sql);
    }
    
}
