package pe.edu.lamolina.amauta.zelper.misc;

public class Acumulador {

    private Integer valor;

    public Acumulador() {
        this.valor = 0;
    }

    public Acumulador(Integer valor) {
        this.valor = valor;
    }

    public Integer getValor() {
        return valor;
    }

    public void incrementar() {
        this.valor++;
    }

    public void incrementar(int aumento) {
        this.valor += aumento;
    }

}
