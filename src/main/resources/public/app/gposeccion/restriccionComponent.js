Vue.component("restriccion-component", {
    template: "#restriccionComp",
    props: {

    },
    data: function () {
        return {
            seccionModal: null,
            tiposRestriccionOptions: [],
            tipoRestriccionSel: null,
            tblRestricciones: null,
            restriccionesArr: [],
            tipoRestriccion: {
                esEspecialidad: false,
                esFacultad: false,
                esModalidad: false
            }
        }
    },
    mounted: function () {

        let $vue = this;
        $global.$on("loadRestriccionComponent", function (seccion) {
            $vue.loadComponent($vue, seccion);
        });

        $global.$on("saveRestriccion", function () {
            $vue.saveRestriccion($vue);
        });
    },
    methods: {
        loadComponent($vue, seccion) {

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/loadModalRestricciones'),
                data: {
                    seccion: seccion
                },
                success: function (response) {
                    if (response.success) {
                        $vue.seccionModal = response.data.seccion;
                        $vue.tiposRestriccionOptions = response.data.tiposRestriccion;
                        if ($vue.seccionModal.tieneRestriccionCarrera ||
                                $vue.seccionModal.tieneRestriccionFacultad ||
                                $vue.seccionModal.tieneRestriccionModalidad) {
                            $vue.tipoRestriccionSel = response.data.tipoRestriccionSel;
                            $vue.cambiarTipoRestriccion();
                        }
                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        saveRestriccion($vue) {

            $(".multiselect__input").each(function () {
                $(this).attr("required", true);
            });
            //.parsley-error
            $('.multiselect__input').each(function () {
                var input = $(this);
                /*
                 if (input.closest('.multiselect').find('.multiselect__content li.multiselect__option--selected span').val() != '') {
                 //  input.removeClass('parsley-error');
                 //  input.parent().find('.parsley-errors-list').children().remove();
                 $(this).removeAttr("required");
                 }*/
                let element = input.closest('.multiselect').find('.multiselect__tags-wrap');

                if (element.css('display') != 'none' && element.html() != "") {
                    $(this).removeAttr("required");
                }
            });



            var form = $("[id='frmRestriccion']");
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
                method: 'POST',
                url: APP.url('academico/gposeccion/saveRestriccion'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: true,
                data:
                        JSON.stringify(restriccionForm)
                ,
                success: function (response) {
                    if (response.success) {
                        MODAL.hideWait();
                        $global.$emit("afterSaveRestriccion", response);
                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                }, error: function () {
                    MODAL.hideWait();
                    $global.$emit("afterSaveRestriccion", response);
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

