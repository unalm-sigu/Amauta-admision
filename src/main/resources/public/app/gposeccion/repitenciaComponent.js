Vue.component("repitencia-component", {
    template: "#repitenciaComp",
    props: {

    },
    data: function () {
        return {
            seccionModal: null,
            tiposRepitenciaOpt: [],
            tiposRepitenciaArr: []
        }
    },
    mounted: function () {

        let $vue = this;
        $global.$on("loadRepitenciaComponent", function (seccion) {
            //  $vue.loadComponent($vue, seccion);
        });

        $global.$on("saveTipoRepRestriccion", function () {
            $vue.saveTipoRepRestriccion($vue);
        });
    },
    methods: {
        loadComponent(seccion) {
            let $vue = this;
            $vue.seccionModal = null;
            $vue.tiposRestriccionOptions = [];
            $vue.tiposRepitenciaArr = [];

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/loadModalRepitenciaRestriccion'),
                data: {
                    seccion: seccion
                }, success: function (response) {
                    if (response.success) {
                        MODAL.hideWait();
                        $vue.seccionModal = response.data.seccion;
                        $vue.tiposRepitenciaOpt = response.data.tiposRepitenciaJson;
                        if (response.data.restriccionesRepitencia != null) {
                            $vue.tiposRepitenciaArr = response.data.restriccionesRepitencia;
                        }
                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                },
                error: function (error) {
                    MODAL.hideWait();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        saveTipoRepRestriccion($vue) {
            var form = $("[id='frmTipoRepitencia']");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            let restriccionForm = {
                seccion: $vue.seccionModal.id,
                tipoRestriccion: $vue.tipoRestriccionSel,
                restriccionesArr: $vue.restriccionesArr
            }
            MODAL.showWait("Espere un momento por favor");

            $.ajax({
                url: APP.url('academico/gposeccion/' + $vue.seccionModal.id + '/saveTipoRepRestriccion'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: true,
                data: JSON.stringify($vue.tiposRepitenciaArr),
                success: function (response) {
                    if (response.success) {
                        MODAL.hideWait();
                        $global.$emit("afterSaveTipoRepRestriccion", response);
                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                }, error: function () {
                    MODAL.hideWait();
                    $global.$emit("afterSaveTipoRepRestriccion", response);
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

            console.dir(restriccionForm);
        }, cambiarTipoRestriccion() {
            let $vue = this;
            $vue.restriccionesArr = [];
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/cambiarTipoRestriccion'),
                data: {
                    seccion: $vue.seccionModal.id,
                    tipoRestriccion: $vue.tipoRestriccionSel.codigo
                },
                success: function (response) {
                    if (response.success) {
                        console.log("cambiarTipoRestriccion success");
                        $vue.tblRestricciones = response.data.tblRestricciones;
                        if (response.data.restriccionesSeleccionadas != null) {
                            $vue.restriccionesArr = response.data.restriccionesSeleccionadas;
                        }

                        $vue.tipoRestriccion.esEspecialidad = response.data.esEspecialidad;
                        $vue.tipoRestriccion.esFacultad = response.data.esFacultad;
                        $vue.tipoRestriccion.esModalidad = response.data.esModalidad;

                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }, nameWithCodeEspecialidad( { codigo, nombre, tipoDescripcion, modalidadEstudio, esTipoDOC, esTipoMAE }) {
            let result = `${codigo} ${nombre}`;
            if (esTipoDOC || esTipoMAE) {
                result += ` (Tipo Carrera : ${tipoDescripcion})`;
            } else {
                result += ` (Modalidad : ${modalidadEstudio.nombre})`;
            }
            return result;
        }, nameWithCode( { codigo, nombre}) {
            return `${codigo} - ${nombre}`;
        }
    }
});

