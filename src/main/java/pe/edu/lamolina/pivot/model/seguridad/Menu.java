package pe.edu.lamolina.pivot.model.seguridad;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.zelper.enums.MenuTipoEnum;

@Entity
@Table(name = "seg_menu")
public class Menu implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "icono")
    private String icono;

    @Column(name = "ruta")
    private String ruta;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "orden")
    private Integer orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sistema")
    private Sistema sistema;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_menu_superior")
    private Menu menuSuperior;

    @OneToMany(mappedBy = "menuSuperior", fetch = FetchType.LAZY)
    private List<Menu> menus;

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    private List<MenuRol> menuRol;

    public Menu() {
    }

    public Menu(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    private String getRutaPadre(Menu menu) {
        String rutaPadre = "";
        if (menu.getMenuSuperior() != null) {
            rutaPadre = getRutaPadre(menu.menuSuperior);
        }
        return rutaPadre + (rutaPadre.equals("") ? "" : "\\") + menu.getNombre();
    }

    public String getRutaPadre() {
        if (this.getMenuSuperior() == null) {
            return "";
        }
        return getRutaPadre(this.getMenuSuperior());
    }

    public String getRutaCompleta() {
        return getRutaPadre(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getTipo() {
        return tipo;
    }

    public MenuTipoEnum getTipoEnum() {
        if (tipo == null) {
            return null;
        }
        return MenuTipoEnum.valueOf(tipo);
    }

    public void setTipo(MenuTipoEnum tipo) {
        this.tipo = tipo.name();
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Sistema getSistema() {
        return sistema;
    }

    public void setSistema(Sistema sistema) {
        this.sistema = sistema;
    }

    public Menu getMenuSuperior() {
        return menuSuperior;
    }

    public void setMenuSuperior(Menu menuSuperior) {
        this.menuSuperior = menuSuperior;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }

    public static class CompareOrden implements Comparator<Menu> {

        @Override
        public int compare(Menu menu1, Menu menu2) {
            return menu1.getOrden().compareTo(menu2.getOrden());
        }
    }

    public List<MenuRol> getMenuRol() {
        return menuRol;
    }

    public void setMenuRol(List<MenuRol> menuRol) {
        this.menuRol = menuRol;
    }

}
