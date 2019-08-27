package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.PersonaEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.ResponsableAula;
import pe.edu.lamolina.model.general.TipoDocIdentidad;

@Repository
public class PersonaDAOH extends AbstractEasyDAO<Persona> implements PersonaDAO {
    
    public PersonaDAOH() {
        super();
        setClazz(Persona.class);
    }
    
    @Override
    public Persona find(long id) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .leftJoin("ubicacionDomicilio udd", "udd.ubicacionSuperior udp", "udp.ubicacionSuperior udde")
                .leftJoin("udd.tipoUbicacion", "udp.tipoUbicacion", "udde.tipoUbicacion")
                .leftJoin("paisNacer ")
                .leftJoin("ubicacionNacer und", "und.ubicacionSuperior unp", "unp.ubicacionSuperior unde")
                .leftJoin("und.tipoUbicacion", "unp.tipoUbicacion", "unde.tipoUbicacion")
                .leftJoin("paisDomicilio ")
                .filter("per.id", id);
        
        return find(sql);
    }
    
    @Override
    public List<Persona> all() {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td");
        return all(sql);
    }
    
    @Override
    public List<Persona> allByNombre(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("estado", PersonaEstadoEnum.ACT)
                .beginBlock()
                .__().complexFilter("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))", "like", nombre)
                .__().complexFilter("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))", "like", nombre)
                .__().filter("numeroDocIdentidad", "like", nombre)
                .endBlock()
                .limit(15);
        
        return all(sql);
    }
    
    @Override
    public List<Persona> allByFilter(DynatableFilter filter) {
        
        DynatableSql sql = new DynatableSql(filter)
                .from(Persona.class, "per")
                .leftJoin("per.tipoDocumento td")
                .searchFields("td.simbolo", "per.numeroDocIdentidad", "per.telefono", "per.celular", "per.emailCompania")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("per.id desc");
        
        return all(sql);
        
    }
    
    @Override
    public Persona findByDocIdentidad(TipoDocIdentidad tipoDocumento, String numeroDocIdentidad) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("numeroDocIdentidad", numeroDocIdentidad)
                .filter("td.id", tipoDocumento);
        
        return find(sql);
    }
    
    @Override
    public List<Persona> allByEmail(String email) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("per.email", email);
        
        return all(sql);
    }
    
    @Override
    public List<Persona> allByEmailWithoutPersona(Persona persona) {
        if (StringUtils.isEmpty(persona.getEmail())) {
            return new ArrayList();
        }
        
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("per.email", persona.getEmail())
                .filter("per.id", "<>", persona);
        
        return all(sql);
    }
    
    @Override
    public List<Persona> allByEmailEmpresa(String email) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("per.emailCompania", email);
        
        return all(sql);
    }
    
    @Override
    public List<Persona> allByEmailEmpresaWithoutPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("per.emailCompania", persona.getEmailCompania())
                .filter("per.id", "<>", persona);
        
        return all(sql);
    }
    
    @Override
    public List<Persona> allByApellidosNombres(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("per.nombres", "like", persona.getNombres());
        
        if (!StringUtils.isEmpty(persona.getPaterno())) {
            sql.filter("per.paterno", "like", persona.getPaterno());
        }
        if (!StringUtils.isEmpty(persona.getMaterno())) {
            sql.filter("per.materno", "like", persona.getMaterno());
        }
        
        return all(sql);
    }
    
    @Override
    public List<Persona> allByEmailCompania(String email) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("emailCompania", email);
        
        return all(sql);
    }
    
    @Override
    public List<Persona> allByEmailCompaniaWithoutPersona(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("emailCompania", persona.getEmailCompania())
                .filter("per.id", "<>", persona.getId());
        
        return all(sql);
    }
    
    @Override
    public Persona findByDocumento(TipoDocIdentidad tipoDocumento, String numeroDocIdentidad) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .leftJoin("ubicacionDomicilio ud", "paisNacer pn", "nacionalidad nac", "paisDomicilio paisDoc")
                .filter("td.id", tipoDocumento)
                .filter("per.numeroDocIdentidad", numeroDocIdentidad);
        return find(sql);
    }
    
    @Override
    public Persona findByDoc(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .leftJoin("tipoDocumento td")
                .filter("per.numeroDocIdentidad", persona.getNumeroDocIdentidad())
                .filter("td.id", persona.getTipoDocumento());
        return find(sql);
    }
    
    @Override
    public Persona findByEmailCompania(Persona persona) {
        Octavia sql = Octavia.query()
                .from(Persona.class, "per")
                .filter("estado", PersonaEstadoEnum.ACT)
                .filter("per.emailCompania", persona.getEmailCompania());
        return find(sql);
    }
    
    @Override
    public List<Persona> allResponsableAulas(DynatableFilter filter, EstadoEnum... estados) {
        DynatableSql sql = new DynatableSql(filter)
                .selectDistinct("per")
                .from(ResponsableAula.class, "ra")
                .join("persona per", "turnoAtencionAula ta")
                .in("ra.estado", Arrays.asList(estados));
        return all(sql);
    }
    
}
