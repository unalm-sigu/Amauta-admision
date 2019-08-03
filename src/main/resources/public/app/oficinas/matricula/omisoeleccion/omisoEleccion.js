Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#omisioVUE',
    data: {
        alumnosURL: APP.url('oficinas/matricula/omisoeleccion/list'),
        ciclos: JSON.parse(cicloJson),
        motivos: JSON.parse(motivosJson),
        modalOmisoEleccion: {
            id: 'modalOmisoEleccion',
            header: true,
            title: 'Agregar Deuda ',
            okbtn: "Guardar",
            showaccept: true
        },
        modalAnular: {
            id: 'modalAnular',
            header: true,
            title: 'Anular Deuda ',
            okbtn: "Aceptar",
            showaccept: true
        },
        modalLoadModal: {
            id: 'modalLoadModal',
            header: true,
            title: 'Cargar Deuda ',
            okbtn: "Cargar",
            showaccept: true
        },
        isLoading: false,
        alumnos: [],
        configConfirmAction: VUE_MODAL.structConfirm({}),
        alumnoOmisoEleccion: {},
        omisionAnular: {}
    },
    mounted: function () {
        $(".numeric").numeric({negative: false});
    },
    computed: {
        // a computed getter
        sum: function () {
            let $vue = this;
            var sum = 0;
            $vue.omisionAnular.alumnoOmisoEleccions.forEach(function (item) {
                if (item.seleccionado) {
                    sum += item.multa;
                }
            })
            return sum;
        }
    },
    methods: {
        customLabel( { persona, codigo }){
            return `${persona.nombreCompleto} — ${codigo}`;
        },
        openNuevo() {
            let $vue = this;
            $vue.alumnoOmisoEleccion = {};
            $vue.$refs.modalOmisoEleccion.open();
        },
        cargar() {

        },
        save() {
            let $vue = this;
            var form = $("#formNuevo");
            if (!form.parsley().validate()) {
                return;
            }
            MODAL.showWait("Espere un momento por favor");
            $vue.alumnoOmisoEleccion.motivo = $vue.alumnoOmisoEleccion.motivo.name;
            $.ajax({
                method: 'POST',
                url: APP.url('oficinas/matricula/omisoeleccion/saveOmision'),
                data: JSON.stringify($vue.alumnoOmisoEleccion),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success");
                    } else {
                        notify(response.message, "error");
                    }
                    $vue.$refs.modalOmisoEleccion.close();
                    MODAL.hideWait();
                },
                error: function () {
                    $vue.$refs.modalOmisoEleccion.close();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        openAnular(item) {
            let $vue = this;
            $vue.omisionAnular = {};
            $vue.omisionAnular = Object.assign({}, item);
            $vue.$refs.modalAnular.open();
        },
        saveAnular() {
            let $vue = this;
            var form = $("#formAnular");
            if (!form.parsley().validate()) {
                return;
            }
            bootbox.confirm({
                message: '¿Seguro que desea anular las deudas?',
                buttons: {
                    confirm: {label: 'Si, anular', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('oficinas/matricula/omisoeleccion/anularOmision'),
                            data: JSON.stringify($vue.omisionAnular),
                            contentType: "application/json",
                            success: function (response) {
                                if (response.success) {
                                    $vue.$refs.load.loadRemoteData();
                                    notify(response.message, "success");
                                } else {
                                    notify(response.message, "error");
                                }
                                $vue.$refs.modalAnular.close();
                            },
                            error: function () {
                                $vue.$refs.modalAnular.close();
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });

        },
        getClass(estado) {
            switch (estado) {
                case "DEU":
                    return "label-warning";
                    break;
                case "ANU":
                    return "label-danger";
                    break;
                case "PAG":
                    return "label-primary";
                    break;
                default:
                    break;
            }
        },
        getRows(item) {
            return item.alumnoOmisoEleccions.length;
        },
        loadAlumno(nombre) {
            let $vue = this;
            this.isLoading = true

            if (nombre != '' || nombre != null || nombre != undefined) {

                $.ajax({
                    url: APP.url("oficinas/matricula/omisoeleccion/allAlumnoByNombre"),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre}
                }).then(response => {
                    if (response.success) {
                        $vue.alumnos = response.data;
                    }

                    this.isLoading = false;
                })

            }
        }
    }
});