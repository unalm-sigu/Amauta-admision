package pe.edu.lamolina.pivot.controller.general.oficina;

import java.util.List;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.PerfilCompania;

public class ColaboradorBean {

    Colaborador colaborador;
    List<PerfilCompania> perfilCompanias;

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public List<PerfilCompania> getPerfilCompanias() {
        return perfilCompanias;
    }

    public void setPerfilCompanias(List<PerfilCompania> perfilCompanias) {
        this.perfilCompanias = perfilCompanias;
    }
    
    
}
