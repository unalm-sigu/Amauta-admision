Vue.component("aula-component", {
    template: "#modalAulaComp",
    props: {

    },
    data: function () {
        return {
            seccionModal: null,
            tabAulas: {
                aulaSel: {},
                oera: {
                    id: 50,
                    nombre: "oera",
                    moduloSel: null,
                    aulaSel: null,
                    modulosOera: [],
                    tblAulas: null
                },
                oficinas: {
                    oficinaSel: null,
                    aulaSel: null,
                    oficinasDisponibles: [],
                    tblAulas: null
                },
                especificas: {
                    aulasEspecificaSel: null,
                    aulasEspecificas: [],
                    errores: []
                }
            }
        }
    },
    created() {
    },
    mounted() {
        let $vue = this;
        $global.$on("saveAula", function () {
            $vue.saveAula($vue);
        });

        $global.$on("closeAula", function () {
            $vue.closeAula($vue);
        });

    },
    methods: {
        changeClassTab(tab) {
            let $vue = this;
            if (tab == 1)
                $vue.tabAulas.aulaSel.tabAula = "oera";
            if (tab == 2)
                $vue.tabAulas.aulaSel.tabAula = "oficina";
            if (tab == 3)
                $vue.tabAulas.aulaSel.tabAula = "especifica";
        },
        labelAula(aula) {
            return aula.nombrePublico+' - '+aula.aulaSuperior.nombre;
        },
        loadAula(seccion) {
            let $vue = this;
            $vue.tabAulas = {
                aulaSel: {},
                oera: {
                    id: 50,
                    nombre: "oera",
                    moduloSel: null,
                    aulaSel: null,
                    modulosOera: [],
                    tblAulas: null
                },
                oficinas: {
                    oficinaSel: null,
                    aulaSel: null,
                    oficinasDisponibles: [],
                    tblAulas: null
                },
                especificas: {
                    aulasEspecificaSel: null,
                    aulasEspecificas: [],
                    errores: []
                }
            };

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/loadModalAula'),
                data: {
                    seccion: seccion.id
                },
                success: function (response) {
                    if (response.success) {

                        $vue.seccionModal = response.data.seccion;
                        $vue.tabAulas['oera'].modulosOera = response.data.modulosOera;
                        $vue.tabAulas['oficinas'].oficinasDisponibles = response.data.oficinasDisponibles;

                        if (response.data.aulaSel != null) {

                            $vue.tabAulas.aulaSel = response.data.aulaSel;
                            if ($vue.tabAulas.aulaSel.tabAula == "oera") {
                                console.log("esOera");
                                $vue.tabAulas['oera'].moduloSel = response.data.modulosOeraSel;
                                $vue.cambiarModulo();
                            } else if ($vue.tabAulas.aulaSel.tabAula == "oficina") {
                                console.log("esOficina");
                                $vue.tabAulas['oficinas'].oficinaSel = response.data.oficinaSel;
                                $vue.cambiarOficina();
                            } else if ($vue.tabAulas.aulaSel.tabAula == "especifica") {
                                console.log("esEspecifica");
                            }
                        } else {
                            $vue.tabAulas.aulaSel = {tabAula: "oera"};
                        }

                    } else {
                        notify(GlobalMessages.errorComunicacion, "error");
                    }
                }, error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        saveAula($vue) {
            let aulaSelArg = [];
            let aulaSeleccionada = $vue.tabAulas.aulaSel;

            if (aulaSeleccionada == null) {
                notify("Seleccione un aula", "error");
                return;
            }
            console.log(aulaSeleccionada.isDisponible);
            if (!aulaSeleccionada.isDisponible) {
                notify("El aula no esta disponible", "error");
                return;
            }
            bootbox.confirm({
                message: "¿Está seguro que desea grabar?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {

                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('academico/gposeccion/saveAula'),
                            type: 'POST',
                            async: true,
                            data: {
                                seccion: $vue.seccionModal.id,
                                aula: aulaSeleccionada.id
                            },
                            success: function (response) {
                                MODAL.hideWait();
                                $global.$emit("afterSaveAula", response);
                            },
                            error: function (response) {
                                MODAL.hideWait();
                                $global.$emit("afterSaveAula", response);
                                notify(GlobalMessages.errorComunicacion, "error");
                            }
                        });
                    } else {

                    }
                }
            });
        },
        closeAula($vue) {
            $vue.tabAulas.aulaSel.tabAula = "zzz";
        },
        asyncFindAulas(nombre) {
            let $vue = this;
            $.ajax({
                url: APP.url("academico/gposeccion/asyncFindAulas"),
                dataType: 'json',
                type: 'post',
                data: {
                    seccion: $vue.seccionModal.id,
                    nombre: nombre
                },
            }).then(response => {
                $vue.tabAulas["especificas"].aulasEspecificas = response.data;
                if ($vue.tabAulas["especificas"].aulasEspecificas == null) {
                    $vue.tabAulas["especificas"].aulasEspecificas = [];
                }
            })
        },
        seleccionarAulaEspecifica() {
            let $vue = this;
            if ($vue.tabAulas['especificas'].aulasEspecificaSel == null) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/seleccionarAula'),
                data: {
                    seccion: $vue.seccionModal.id,
                    aula: $vue.tabAulas['especificas'].aulasEspecificaSel.id
                },
                success: function (response) {
                    if (response.success) {
                        $vue.tabAulas.aulaSel = response.data;
                        $vue.selectAula($vue.tabAulas['especificas'].aulasEspecificaSel);
                    } else {
                        if (response.total > 0) {
                            $vue.tabAulas['especificas'].errores = response.data;
                        } else {
                            $vue.tabAulas['especificas'].errores = [];
                        }
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });


        },
        cambiarOficina() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/aulas'),
                data: {
                    seccion: $vue.seccionModal.id,
                    aula: $vue.tabAulas['oficinas'].oficinaSel.id
                },
                success: function (response) {
                    if (response.success) {
                        $vue.tabAulas['oficinas'].tblAulas = response.data.aulas;

                        if (response.data.aulaSel != null) {
                            $vue.tabAulas.aulaSel = response.data.aulaSel;
                        }

                    } else {
                        notify(GlobalMessages.errorComunicacion, "error");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        cambiarModulo() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/aulas'),
                data: {
                    seccion: $vue.seccionModal.id,
                    aula: $vue.tabAulas['oera'].moduloSel.id
                },
                success: function (response) {
                    if (response.success) {
                        $vue.tabAulas['oera'].tblAulas = response.data.aulas;
                    } else {
                        notify(GlobalMessages.errorComunicacion, "error");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });

        },
        getClassAula(aula) {
            let $vue = this;
            console.log(
                    $vue.seccionModal.aula.id);
            if (aula.seleccionado) {
                return "btn-primary";
            }
            if ($vue.seccionModal.aula.id != '') {
                if (aula.id == $vue.seccionModal.aula.id) {
                    return "btn-warning";
                }
            }
            if (!aula.isDisponible) {
                return "btn-danger";
            }
            return "btn-default";
        },
        selectAula(aula) {
            let seleccionado = !aula.seleccionado;
            if (seleccionado) {
                this.tabAulas.aulaSel = aula;
                if (aula.tabAula == "oera") {
                    if (this.tabAulas['oficinas'].tblAulas != null) {
                        for (let key in this.tabAulas['oficinas'].tblAulas) {
                            this.tabAulas['oficinas'].tblAulas[key].seleccionado = false;
                        }
                    }

                    for (let key in this.tabAulas['oera'].tblAulas) {
                        this.tabAulas['oera'].tblAulas[key].seleccionado = false;
                        if (this.tabAulas['oera'].tblAulas[key].id == aula.id) {
                            this.tabAulas['oera'].tblAulas[key].seleccionado = seleccionado;
                        }
                    }

                } else if (aula.tabAula == "oficina") {
                    if (this.tabAulas['oera'].tblAulas != null) {
                        for (let key in this.tabAulas['oera'].tblAulas) {
                            this.tabAulas['oera'].tblAulas[key].seleccionado = false;
                        }
                    }
                    for (let key in this.tabAulas['oficinas'].tblAulas) {
                        this.tabAulas['oficinas'].tblAulas[key].seleccionado = false;
                        if (this.tabAulas['oficinas'].tblAulas[key].id == aula.id) {
                            this.tabAulas['oficinas'].tblAulas[key].seleccionado = seleccionado;
                        }
                    }

                } else if (aula.tabAula == "especifica") {

                    if (this.tabAulas['oera'].tblAulas != null) {
                        for (let key in this.tabAulas['oera'].tblAulas) {
                            this.tabAulas['oera'].tblAulas[key].seleccionado = false;
                        }
                    }

                    if (this.tabAulas['oficinas'].tblAulas != null) {
                        for (let key in this.tabAulas['oficinas'].tblAulas) {
                            this.tabAulas['oficinas'].tblAulas[key].seleccionado = false;
                        }
                    }
                }
            } else {
                if (this.tabAulas['oficinas'].tblAulas != null) {
                    for (let key in this.tabAulas['oficinas'].tblAulas) {
                        this.tabAulas['oficinas'].tblAulas[key].seleccionado = false;
                    }
                }

                if (this.tabAulas['oera'].tblAulas != null) {
                    for (let key in this.tabAulas['oera'].tblAulas) {
                        this.tabAulas['oera'].tblAulas[key].seleccionado = false;
                    }
                }
            }
        }
    }
});

