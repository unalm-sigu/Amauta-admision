Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#omisioVUE',
    data: {
        alumnosURL: APP.url(`${rutaModulo}/list`),
        ciclos: JSON.parse(cicloJson),
        motivos: JSON.parse(motivosJson),
        modalOmisoEleccion: {
            id: 'modalOmisoEleccion',
            header: true,
            title: 'Agregar Deuda ',
            okbtn: "Guardar",
            showaccept: true
        },
        modalAnular: VUE_MODAL.structFormAjax({
            id: 'modalAnular',
            header: true,
            title: 'Anular deuda',
            okbtn: "Anular deuda",
            showaccept: true
        }),
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
        omisionAnular: {},
        confAporteAlumno: VUE_MODAL.structInfo({
            id: 'modalAporteAlumno',
            modalsize: 'modal-lg'
        }),
        modalBoletaAlumno: VUE_MODAL.structInfo({
            id: 'modalBoletaAlumno',
            title: 'Boletas del Alumno'
        }),
        resumenModal: {},
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
        },
        modalTitulo() {
            let $vue = this;
            return $vue.resumenModal.nombre;
        },
        modalSubtitulo() {
            let $vue = this;
            if ($vue.resumenModal.modalidadEstudio !== "Visitante" && $vue.resumenModal.modalidadEstudio !== "Especial") {
                return $vue.resumenModal.carrera + " - " + $vue.resumenModal.modalidadEstudio;
            } else {
                return $vue.resumenModal.carrera;
            }
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
                url: APP.url(`${rutaModulo}/saveOmision`),
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
            $vue.omisionAnular = JSON.parse(JSON.stringify(item));
            $vue.$refs.modalAnular.open();
        },
        saveAnular() {
            let $vue = this;
            var form = $("#formAnular");
            if (!form.parsley().validate()) {
                return;
            }

            let loop = 0;
            for (let i = 0; i < $vue.omisionAnular.alumnoOmisoEleccions.length; i++) {
                let item = $vue.omisionAnular.alumnoOmisoEleccions[i];
                if (item.seleccionado) {
                    loop++;
                }
            }

            if (loop === 0) {
                notify("No ha seleccionado que multas van a ser anuladas", "error");
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
                        $vue.$refs.modalAnular.beginProcessing();
                        $.ajax({
                            method: 'POST',
                            url: APP.url(`${rutaModulo}/anularOmision`),
                            data: JSON.stringify($vue.omisionAnular),
                            contentType: "application/json",
                            success: function (response) {
                                $vue.$refs.modalAnular.confirmReaction(response.success);
                                if (response.success) {
                                    $vue.$refs.load.loadRemoteData();
                                    notify(response.message, "success");
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                $vue.$refs.modalAnular.confirmReaction(false);
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
                    url: APP.url(`${rutaModulo}/allAlumnoByNombre`),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre}
                }).then(response => {
                    if (response.success) {
                        $vue.alumnos = response.data;
                    }
                    this.isLoading = false;
                });

            }
        },
        verAportes(item) {
            let $vue = this;
            $vue.resumenModal = {};
            $vue.$refs.modalAporteAlumno.open();
            $vue.$refs.modalAporteAlumno.showWait("Cargando aportes");

            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/getInfoAportes/${item.id}`),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.modalAporteAlumno.hideWait();
                        $vue.resumenModal = response.data;
                    } else {
                        $vue.$refs.modalAporteAlumno.close();
                        notify(response.message, "error");
                    }

                },
                error() {
                    $vue.$refs.modalAporteAlumno.close();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        verBoletas(item) {
            let $vue = this;

            $vue.resumenModal = {};
            $vue.$refs.modalBoletaAlumno.open();
            $vue.$refs.modalBoletaAlumno.showWait("Buscando boletas..");

            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/findBoleta/${item.id}`),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.modalBoletaAlumno.hideWait();
                        if (response.data.boletas.length == 0) {
                            $vue.$refs.modalBoletaAlumno.close();
                            notify("No existen boletas generadas para este alumno", "warning");
                            return;
                        }
                        $vue.resumenModal = response.data;

                    } else {
                        $vue.$refs.modalBoletaAlumno.close();
                        notify(response.message, "error");
                    }
                },
                error() {
                    $vue.$refs.modalBoletaAlumno.close();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
    }
});