package pe.edu.lamolina.pivot.controller.general.oficina.colaborador;

import java.util.List;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.PerfilCompania;

public class ColaboradorBean {

    Colaborador colaborador;
    List<PerfilCompania> perfilCompanias;
    Oficina oficinaMean;

    public Oficina getOficinaMean() {
        return oficinaMean;
    }

    public void setOficinaMean(Oficina oficinaMean) {
        this.oficinaMean = oficinaMean;
    }

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
