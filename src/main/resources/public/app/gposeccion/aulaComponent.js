Vue.component("aula-componente", {
    template: "#modalAulaComp",
    props: {

    },
    data: function () {
        return {
            seccionModal: null,
            tabAulas: {
                aulaSel: null,
                oera: {
                    id: 50,
                    nombre: "oera",
                    moduloSel: null,
                    aulaSel: null,
                    modulosCombo: [],
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
    mounted: function () {
        let $vue = this;
        $global.$on("loadAulaComponent", function (seccion) {
            $vue.loadAula($vue, seccion);
        });

        $global.$on("saveAula", function () {
            $vue.saveAula($vue);
        });
    },
    methods: {
        loadAula($vue, seccion) {
            $vue.tabAulas = {
                aulaSel: null,
                oera: {
                    id: 50,
                    nombre: "oera",
                    moduloSel: null,
                    aulaSel: null,
                    modulosCombo: [],
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

            //  $vue.dynatable.queries.add("seccion", seccion);
            //    $vue.dynatable.process();
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/loadModalAula'),
                data: {
                    seccion: seccion
                },
                success: function (response) {
                    if (response.success) {
                        $vue.tabAulas.aulaSel = response.data.aulaSel;
                        $vue.seccionModal = response.data.seccion;
                        $vue.tabAulas['oera'].modulosCombo = response.data.modulosOera;
                        $vue.tabAulas['oficinas'].oficinasDisponibles = response.data.oficinasDisponibles;
                        // $vue.modulosCombo = response.data.modulosOera;

                        if ($vue.tabAulas.aulaSel != null) {
                            if ($vue.tabAulas.aulaSel.esOera) {
                                console.log("esOera");
                                console.dir(response.data.modulosOeraSel);
                                $vue.tabAulas['oera'].moduloSel = response.data.modulosOeraSel;
                                $vue.cambiarModulo();
                            } else if ($vue.tabAulas.aulaSel.esOficina) {
                                console.log("esOficina");
                                $vue.tabAulas['oficinas'].oficinaSel = response.data.oficinaSel;
                                $vue.cambiarOficina();
                            } else if ($vue.tabAulas.aulaSel.esEspecifica) {
                                console.log("esEspecifica");
                            }
                        }

                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        saveAula($vue) {
            let aulaSelArg = [];
            let aulaSeleccionada = $vue.tabAulas.aulaSel;

            if (aulaSeleccionada == null) {
                alert("Seleccione un aula");
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
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    } else {

                    }
                }
            });
        },
        asyncFindAulas(nombre) {
            //this.isLoading = true
            let $vue = this;
            $.ajax({
                url: APP.url("academico/gposeccion/asyncFindAulas"),
                dataType: 'json',
                type: 'post',
                data: {nombre: nombre},
            }).then(response => {
                // tabAulas especificas aulasEspecificaSel  aulasEspecificas
                $vue.tabAulas["especificas"].aulasEspecificas = response.data;
                //  this.isLoading = false
                if ($vue.tabAulas["especificas"].aulasEspecificas == null) {
                    $vue.tabAulas["especificas"].aulasEspecificas = [];
                }
            })
        }, seleccionarAulaEspecifica() {
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
                        //  $vue.tabAulas['especificas'].aulasEspecificaSel = response.data;
                        //   $vue.tabAulas['especificas'].aulasEspecificaSel.seleccionado = true;

                        $vue.tabAulas.aulaSel = response.data;
                        $vue.selectAula($vue.tabAulas['especificas'].aulasEspecificaSel);
                        // $vue.tabAulas['especificas'].aulasEspecificaSel.seleccionado = true;
                        /*
                         this.tabAulas['oficinas'].aulaSel = null;
                         this.tabAulas['oera'].aulaSel = null;*/
                    } else {
                        if (response.total > 0) {
                            $vue.tabAulas['especificas'].errores = response.data;
                        } else {
                            $vue.tabAulas['especificas'].errores = [];
                        }
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });


        }, cambiarOficina() {
            let $vue = this;
            //    $vue.tabAulas.aulaSel = null;
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
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }, cambiarModulo() {
            let $vue = this;
            //  $vue.tabAulas.aulaSel = null;
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
                        /*
                         if (response.data.aulaSel != null && response.data.aulaSel != "") {
                         $vue.tabAulas['oera'].aulaSel = response.data.aulaSel;
                         }*/
                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        }, getClassAula(aula) {
            if (aula.seleccionado) {
                return "btn-primary";
            }/*
             if ((this.tabAulas.aulaSel != null && this.tabAulas['oera'].aulaSel != "")
             && parseInt(aula.id) == parseInt(this.tabAulas['oera'].aulaSel.id)) {
             return "btn-primary";
             }*/
            return "btn-default";
        }, selectAula(aula) {
            let seleccionado = !aula.seleccionado;
            console.log("select aula");
            console.dir(aula);
            if (seleccionado) {
                this.tabAulas.aulaSel = aula;
                if (aula.esOera) {
                    /*
                     this.tabAulas['oera'].aulaSel = aula;
                     this.tabAulas['oficinas'].aulaSel = null;
                     this.tabAulas['especificas'].aulasEspecificaSel = null;
                     */
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

                } else if (aula.esOficina) {
                    /*
                     this.tabAulas['oficinas'].aulaSel = aula;
                     this.tabAulas['oera'].aulaSel = null;
                     this.tabAulas['especificas'].aulasEspecificaSel = null;
                     */
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

                } else if (aula.esEspecifica) {

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
                /*
                 this.tabAulas['oera'].aulaSel = null;
                 this.tabAulas['oficinas'].aulaSel = null;
                 */
                this.tabAulas.aulaSel = null;
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

