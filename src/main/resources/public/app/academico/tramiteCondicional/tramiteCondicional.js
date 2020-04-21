Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('date-picker', VueBootstrapDatetimePicker.default);

var app = new Vue({
    el: '#condicional',
    data: {
        URL_RETIROS: APP.url("academico/tramitecondicional/list"),
        ciclos: JSON.parse(ciclosJson),
        tiposTramite: JSON.parse(tipoTramiteJson),
        oficinas: JSON.parse(oficinasJson),
        configDate: {
            format: 'DD/MM/YYYY',
            useCurrent: false
        },
        matriculaResumen: {},
        tramite: {},
        modalTramiteCondicional: {
            id: 'modalTramiteCondicional',
            header: true,
            title: 'Agregar Tramite Condicional ',
            okbtn: "Guardar",
            showaccept: true
        },
        modalTramiteResolucion: {
            id: 'modalTramiteResolucion',
            header: true,
            title: 'Agregar Resolucion ',
            okbtn: "Guardar",
            showaccept: true
        },
        alumnos: [],
        cursos: [],
        resolucion: {},
        dataTemp: {}
    },
    mounted: function () {
        $(".numeric").numeric({negative: false});
    },
    methods: {
        modal() {
            let $vue = this;
            $vue.tramite = {};
            $vue.cursos = [];
            $vue.$refs.modalTramiteCondicional.open();
        },
        loadAlumno(nombre) {
            let $vue = this;
            this.isLoading = true

            if (nombre != '' || nombre != null || nombre != undefined) {

                $.ajax({
                    url: APP.url("academico/tramitecondicional/allAlumnoByNombre"),
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
        },
        loadCursos(nombre) {
            let $vue = this;
            this.isLoading = true
            if ($vue.tramite.alumno == null || $vue.tramite.cicloAcademicoResolucion == null) {
                return;
            }
            if (nombre != '' || nombre != null || nombre != undefined) {

                $.ajax({
                    url: APP.url("academico/tramitecondicional/allCursosAlumnoByName"),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre, idAlumno: $vue.tramite.alumno.id, idCiclo: $vue.tramite.cicloAcademicoResolucion.id}
                }).then(response => {
                    if (response.success) {
                        $vue.cursos = response.data;
                    }

                    this.isLoading = false;
                })

            }
        },
        customLabel( {persona, codigo}){
            if (persona != null) {
                return  codigo + " - " + persona.nombreCompleto;
            }
            return "";
        },
        saveTramiteCondicional() {
            let $vue = this;
            if (!$("#form").parsley().validate()) {
                return;
            }
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/tramitecondicional/save'),
                data: JSON.stringify($vue.tramite),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success");
                    } else {
                        notify(response.message, "error");
                    }
                    $vue.$refs.modalTramiteCondicional.close();
                    MODAL.hideWait();
                },
                error: function () {
                    $vue.$refs.modalTramiteCondicional.close();
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });

        },
        openTramiteResol(item, val) {
            let $vue = this;
            $vue.resolucion = {};
            $vue.dataTemp = {};
            $vue.dataTemp = Object.assign({}, item);
            $vue.dataTemp.estado = val == 0 ? 'RCHZ' : 'ACEP'
            if (val == 1) {
                $vue.$refs.modalTramiteResolucion.open();
            } else {
                $vue.update();
            }
        },
        aceptar() {
            let $vue = this;
            if (!$("#formRes").parsley().validate()) {
                return;
            }
            $vue.dataTemp.resolucion = $vue.resolucion;
            $vue.update();
        },
        update() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/tramitecondicional/update'),
                data: JSON.stringify($vue.dataTemp),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        if ($vue.dataTemp.estado == 'ACEP' && $vue.dataTemp.tipoTramite.codigo == 'CAM_NOTA') {
                            $vue.verificarEliminarMat();
                        }
                        $vue.dataTemp = {};
                        notify(response.message, "success");
                    }
                    $vue.$refs.modalTramiteResolucion.close();
                    MODAL.hideWait();
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        verificarEliminarMat() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/tramitecondicional/verificarEliminarMat'),
                data: JSON.stringify($vue.dataTemp),
                contentType: "application/json",
                success: function (response) {
                   
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        }
    }
})