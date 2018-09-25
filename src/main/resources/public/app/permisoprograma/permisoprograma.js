Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#permisoProgramacionVUE',
    data: {
        colaboradorURL: APP.url("permisoprograma/colaborador/list"),
        anexoBoletin: JSON.parse(anexoBoletin),
        programaCurso: JSON.parse(programaCurso),
        programaSeccion: JSON.parse(programaSeccion),
        programaGpoSeccion: JSON.parse(programaGpoSeccion),
        programaDocente: JSON.parse(programaDocente),
        eventos: JSON.parse(eventos),
        colaboradorSelect: 0,
        facultadSelect: 0,
        modalAddPermiso: {
            id: 'modalAddPermiso',
            header: true,
            title: 'Agregar Permiso',
            showaccept: true
        },
        modalModifySecc: {
            id: 'modalModifySecc',
            header: true,
            title: 'Modificar Permiso',
            showaccept: true
        },
        addSelect: false,
        colaboradorAnexo: {},
        crud: false,
        objCrud: {}
    },
    computed: {

    },
    mounted: function () {
        let $vue = this;
        console.log($vue.eventos);
    },
    methods: {
        seleccionar(item) {
            var $vue = this;
            $vue.colaboradorSelect = item.colaborador.id;
            $vue.facultadSelect = item.id;
        },
        deseleccionar() {
            var $vue = this;
            $vue.colaboradorSelect = 0;
        },
        classTable(item) {
            var $vue = this;
            if (item.id == $vue.facultadSelect) {
                return "fondo-gray";
            }
            return "";
        },
        customLabel( { nombre }) {
            return nombre
        },
        input(value, item) {
            var $vue = this;
            if (!$vue.addSelect) {
                return;
            }
            $vue.data = {};

            $vue.data.id = value.id;
            $vue.data.colaborador = value.colaborador;
            $vue.data.anexoBoletin = value.anexoBoletin;
            $vue.permisoProgramacion = $vue.selected;
            $vue.data.permisosProgramacionHorarios = new Array();
            $vue.data.permisosProgramacionHorarios[0] = {};
            $vue.data.permisosProgramacionHorarios[0].permisoProgramacion = $vue.permisoProgramacion;
            if ($vue.permisoProgramacion.textoAgregar != null) {
                $vue.data.permisosProgramacionHorarios[0].puedeAgregar = 1;
            }
            if ($vue.permisoProgramacion.textoEliminar != null) {
                $vue.data.permisosProgramacionHorarios[0].puedeEliminar = 1;
            }
            if ($vue.permisoProgramacion.textoModificar != null) {
                $vue.data.permisosProgramacionHorarios[0].puedeModificar = 1;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('permisoprograma/colaborador/save'),
                data: JSON.stringify($vue.data),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success");
                    }
                },
                error: function (error) {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        selectPermiso(selectedOption) {
            var $vue = this;
            $vue.selected = selectedOption;
            $vue.addSelect = true;
        },
        removePermiso(value) {
            var $vue = this;
            $vue.addSelect = false;
            $vue.data = {};
            $vue.data.idPermiso = value.idPermiso;
            $.ajax({
                method: 'POST',
                url: APP.url('permisoprograma/colaborador/update'),
                data: JSON.stringify($vue.data),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success");
                    }
                },
                error: function (error) {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        modal(item) {
            let $vue = this;
            $vue.colaboradorAnexo = {};
            $vue.colaboradorAnexo.colaborador = item.colaborador;
            $vue.$refs.modalAddPermiso.open();
        },
        eventoSelect(item) {
            let $vue = this;
            $vue.crud = false;
            $vue.colaboradorAnexo.puedeAgregar = null;
            $vue.colaboradorAnexo.puedeEliminar = null;
            $vue.colaboradorAnexo.puedeModificar = null;
            $vue.objCrud = {};
            if (item.nivel == "SECCION") {
                $vue.objCrud = item;
                $vue.crud = true;
            }
            console.log(item)
        },
        save() {
            let $vue = this;
            $vue.data = {};
            $vue.data.colaborador = $vue.colaboradorAnexo.colaborador;
            $vue.data.anexoBoletin = $vue.colaboradorAnexo.anexoBoletin;
            $vue.data.permisosProgramacionHorarios = new Array();
            $vue.data.permisosProgramacionHorarios[0] = {};
            $vue.data.permisosProgramacionHorarios[0].permisoProgramacion = $vue.colaboradorAnexo.permisoProgramacion;
            if ($vue.colaboradorAnexo.puedeAgregar) {
                $vue.data.permisosProgramacionHorarios[0].puedeAgregar = 1;
            }
            if ($vue.colaboradorAnexo.puedeEliminar) {
                $vue.data.permisosProgramacionHorarios[0].puedeEliminar = 1;
            }
            if ($vue.colaboradorAnexo.puedeModificar) {
                $vue.data.permisosProgramacionHorarios[0].puedeModificar = 1;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('permisoprograma/colaborador/save'),
                data: JSON.stringify($vue.data),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.modalAddPermiso.close();
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success");
                    }
                },
                error: function (error) {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        modifySecc(item) {
            let $vue = this;
            console.log(item);
            $vue.colaboradorAnexo = {};
            $vue.colaboradorAnexo.colaborador = item.colaborador;
            $vue.colaboradorAnexo.colaborador = item.colaborador;
            $vue.$refs.modalModifySecc.open();
        },
        addPermiso() {

        },
        addSecc() {

        }
    }
});
