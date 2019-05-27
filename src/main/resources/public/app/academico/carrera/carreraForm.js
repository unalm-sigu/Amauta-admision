Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#carreraFormVUE',
    data: {
        carrera: JSON.parse(carreraJson),
        modalidades: JSON.parse(modalidadesJson),
        facultades: JSON.parse(facultadesJson),
        tipos: JSON.parse(tiposJson),
        areasPosgrado: JSON.parse(areasPosgradoJson),
        orientaciones: JSON.parse(orientacionesJson),
        editarOrientaciones: false,
        orientacioDelete: {},
        indexOrientaDelete: -1,
        nuevasOris: 0,
        configAnulaOrientacion: VUE_MODAL.structFormAjax({
            id: "idAnulaOrientacion",
            header: true,
            title: 'Eliminar Orientación',
            okbtn: 'Eliminar Orientación',
            okclass: "btn-danger",
            form: "formAnulaOrientacion"
        }),
        configConfirmAction: VUE_MODAL.structConfirm({}),
    },
    mounted() {
        let $vue = this;
        console.log($vue.carrera)
    },
    methods: {
        verSaveCarrera() {
            let $vue = this;
            let isSave = $vue.carrera.id == '';
            let form = $("#formCarrera");
            if (!form.parsley().validate()) {
                bootbox.alert({
                    message: "Debe completar todos los campos",
                    buttons: {ok: {label: "Aceptar"}}
                });
                return;
            }

            let msg = "¿Está seguro que desea " + (isSave ? "crear una nueva " : "actualizar los datos de esta ") + "especialidad?";
            let btn = "Si, " + (isSave ? "crear" : "actualizar");

            $vue.configConfirmAction.message = msg;
            $vue.configConfirmAction.okbtn = btn;
            $vue.configConfirmAction.okaction = $vue.saveCarrera;
            $vue.$refs.modalConfirmAction.open();
        },
        saveCarrera() {
            let $vue = this;
            let url = window.location.href;
            
            axios.post(APP.url(rutaModulo + '/saveCarrera'), $vue.carrera)
                    .then(response => {
                        $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                        if (response.data.success) {
                            $vue.carrera = response.data.data;
                            notifyBootbox(response.data.message, "success");
                            if (isSave) {
                                let newUrl = url.replace("/nuevo", "/" + $vue.carrera.id + "/editar")
                                history.pushState(null, null, newUrl);
                            }
                        } else {
                            notifyBootbox(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                        $vue.$refs.modalConfirmAction.confirmReaction(false);
                        notifyBootbox(MESSAGES.errorComunicacion, "error");
                    });

        },
        addOrientacion() {
            let $vue = this;
            let ori = {codigo: $vue.carrera.codigo + "XX", estado: "PEND", motivoAnulacion: "", estadoEnum: {value: "Sin guardar"}};
            $vue.orientaciones.push(ori);
            $vue.nuevasOris++;
        },
        verEliminar(ori, index) {
            let $vue = this;
            $vue.orientacioDelete = Object.assign({}, ori);
            $vue.indexOrientaDelete = index;
            $vue.$refs.modalAnulaOrientacion.open();
        },
        removerOrientacion() {
            let $vue = this;
            let form = $("#" + $vue.configAnulaOrientacion.form);
            if (!form.parsley().validate()) {
                $vue.$refs.modalAnulaOrientacion.opaque();
                bootbox.alert({
                    message: "Debe completar todos los campos correctamente",
                    buttons: {ok: {label: "Aceptar"}},
                    callback() {
                        $vue.$refs.modalAnulaOrientacion.removeOpaque();
                    }
                });
                return;
            }

            let ori = $vue.orientacioDelete;
            let index = $vue.indexOrientaDelete;
            $vue.$refs.modalAnulaOrientacion.beginProcessing();

            axios.post(APP.url(rutaModulo + '/deleteOrientacion'), ori)
                    .then(response => {
                        $vue.$refs.modalAnulaOrientacion.stopProcessing();
                        if (response.data.success) {
                            $vue.orientaciones.splice(index, 1);
                            if (response.data.data.eliminados == 0) {
                                $vue.orientaciones.splice(index, 0, response.data.data.orientacion);
                            }
                            notifyBootbox(response.data.message, "info");
                            $vue.$refs.modalAnulaOrientacion.close();
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                        $vue.$refs.modalAnulaOrientacion.stopProcessing();
                        notify(MESSAGES.errorComunicacion, "error");
                    });
        },
        remover(ori, index) {
            let $vue = this;
            if (ori.id == undefined) {
                if (ori.nombre == undefined || ori.nombre == "") {
                    $vue.orientaciones.splice(index, 1);
                    $vue.nuevasOris--;
                    return;
                }

                bootbox.confirm({
                    message: "¿Está seguro que ya no desea considerar esta Orientación?",
                    buttons: {
                        confirm: {label: "Si, no considerar", className: "btn-warning"},
                        cancel: {label: "Cerrar", labelName: "btn-link"}
                    },
                    callback(result) {
                        if (result) {
                            $vue.orientaciones.splice(index, 1);
                            $vue.nuevasOris--;
                        }
                    }
                });
                return;
            }

            bootbox.confirm({
                message: "¿Está seguro que eliminar esta Orientación?",
                buttons: {
                    confirm: {label: "Si, eliminar", className: "btn-danger"},
                    cancel: {label: "Cerrar", labelName: "btn-link"}
                },
                callback(result) {
                    if (result) {
                        axios.post(APP.url(rutaModulo + '/deleteOrientacion'), ori)
                                .then(response => {
                                    if (response.data.success) {
                                        $vue.orientaciones.splice(index, 1);
                                        if (response.data.data.eliminados == 0) {
                                            $vue.orientaciones.splice(index, 0, response.data.data.orientacion);
                                        }
                                        notify(response.data.message, "info");
                                    } else {
                                        notify(response.data.message, "error");
                                    }
                                })
                                .catch(function (error) {
                                    console.log(error);
                                    notify(MESSAGES.errorComunicacion, "error");
                                });
                    }
                }
            });
        },
        incluir(ori, index) {
            let $vue = this;
            $vue.configConfirmAction.message = "¿Está seguro que desea activar esta Orientación?";
            $vue.configConfirmAction.okbtn = "Si, activar";
            $vue.configConfirmAction.okaction = function () {
                axios.post(APP.url(rutaModulo + '/activarOrientacion'), ori)
                        .then(response => {
                            $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                            if (response.data.success) {
                                $vue.orientaciones.splice(index, 1);
                                $vue.orientaciones.splice(index, 0, response.data.data);
                                notifyBootbox(response.data.message, "success");
                            } else {
                                notifyBootbox(response.data.message, "error");
                            }
                        })
                        .catch(function (error) {
                            console.log(error);
                            $vue.$refs.modalConfirmAction.confirmReaction(false);
                            notifyBootbox(MESSAGES.errorComunicacion, "error");
                        });
            }
            $vue.$refs.modalConfirmAction.open();
        },
        updateOrientacion(ori, index) {
            let $vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea modificar esta Orientación?",
                buttons: {
                    confirm: {label: "Si, modificar", className: "btn-warning"},
                    cancel: {label: "Cerrar", labelName: "btn-link"}
                },
                callback(result) {
                    if (result) {
                        let orientacion = Object.assign({}, ori);
                        orientacion.nombre = ori.nombre2;
                        axios.post(APP.url(rutaModulo + '/saveOrientacion'), orientacion)
                                .then(response => {
                                    if (response.data.success) {
                                        $vue.orientaciones.splice(index, 1);
                                        $vue.orientaciones.splice(index, 0, response.data.data);
                                        notify(response.data.message, "info");
                                    } else {
                                        notify(response.data.message, "error");
                                    }
                                })
                                .catch(function (error) {
                                    console.log(error);
                                    notify(MESSAGES.errorComunicacion, "error");
                                });
                    }
                }
            });
        },
        prepararCambio() {
            let $vue = this;
            for (var i = 0; i < $vue.orientaciones.length; i++) {
                $vue.orientaciones[i].nombre2 = $vue.orientaciones[i].nombre;
            }
        },
        verificarCambio(ori) {
            console.log(ori.nombre)
            console.log(ori.nombre2)
            return ori.nombre != ori.nombre2;
        },
        saveOrientaciones() {
            let $vue = this;
            var form = $("#formOrientacion");
            if (!form.parsley().validate()) {
                bootbox.alert({
                    message: "Debe completar todos los campos",
                    buttons: {ok: {label: "Aceptar"}}
                });
                return;
            }

            bootbox.confirm({
                message: "¿Está seguro que desea guardar esta(s) Orientación(es) nueva(s)?",
                buttons: {
                    confirm: {label: "Si, guardar", className: "btn-success"},
                    cancel: {label: "Cerrar", labelName: "btn-link"}
                },
                callback(result) {
                    if (result) {
                        let carr = {
                            id: $vue.carrera.id,
                            orientacionCarrera: $vue.orientaciones
                        };
                        axios.post(APP.url(rutaModulo + '/saveOrientaciones'), carr)
                                .then(response => {
                                    if (response.data.success) {
                                        $vue.orientaciones = response.data.data;
                                        $vue.nuevasOris = 0;
                                        notify(response.data.message, "info");
                                    } else {
                                        notify(response.data.message, "error");
                                    }
                                })
                                .catch(function (error) {
                                    console.log(error);
                                    notify(MESSAGES.errorComunicacion, "error");
                                });
                    }
                }
            });
        },
        revisar(tipo, ofi, campo) {
            let $vue = this;
            if (tipo == 'CODIGO') {
                ofi[campo] = VUE.revisarCodigo(ofi[campo]);
            } else if (tipo == 'EMAIL') {
                ofi[campo] = VUE.revisarEmail(ofi[campo]);
            } else if (tipo == 'NOMBRE') {
                ofi[campo] = VUE.revisarNombreObjeto(ofi[campo]);
            } else if (tipo == 'ANEXOS') {
                ofi[campo] = VUE.revisarAnexos(ofi[campo]);
            } else if (tipo == 'TELEFONOS') {
                ofi[campo] = VUE.revisarTelefonos(ofi[campo]);
            }
        }

    }
});

