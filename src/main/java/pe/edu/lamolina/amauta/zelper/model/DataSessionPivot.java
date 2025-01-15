package pe.edu.lamolina.amauta.zelper.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;

@Getter
@Setter
public class DataSessionPivot implements Serializable {

    private Compania compania;

    private CicloAcademico cicloAcademico;

    private String email;

    private Persona persona;

    private Usuario usuario;

    private Docente docente;

    private DepartamentoAcademico departamentoAcademico;

    private List<Rol> roles;

    private List<Rol> rolesMain;

    private Rol rolActivo;

    private List<Menu> menu;

    private List<Facultad> facultades;

    private List<Carrera> carreras;

    private List<ModalidadEstudio> modalidades;

    private List<DepartamentoAcademico> departamentos;

    private List<Oficina> oficinas;

    private List<Colaborador> colaborador;

    private Oficina oficinaMain;

    private String browser;

    private String direccionIp;

    private String sistemaOperativo;

    private Date fechaAccionAudit;

    private Boolean encuestaSunedu;

    private String rutaEncuentaSunedu;

    private String provider;

    private List<Menu> menusAcceso;

    public Map<Long, Rol> getMapRoles() {

        return roles.stream()
                .collect(Collectors.toMap(x -> x.getId(), x -> x));
    }

}
