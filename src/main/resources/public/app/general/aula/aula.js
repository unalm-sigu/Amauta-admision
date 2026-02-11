Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#aulaVUE',
    data: {
        aulasURL: APP.url(rutaModulo + '/list'),
        aula: {},
        aulaZoom: {},
        tipoAulas: JSON.parse(tipoAulasJson),
        isSoporteDera: JSON.parse(validarSoporteDera),
        isPersonalDera: JSON.parse(validarPersonalDera),
        // soporteAulas:JSON.parse(soporteAulasJson),
        tipoAula: null,
        // soporteAula: null,
        modalCambioEstado: {
            id: 'modalCambioEstado',
            header: true,
            okbtn: 'Guardar',
            showaccept: true,
            cancelbtn: 'Cancelar',
            cancelclass: 'btn btn-link',
            form: "formCambioPeso"
        },
        modalAulaContenido: {
            id: 'modalAulaContenido',
            header: true,
            cancelbtn: 'Cerrar',
            cancelclass: 'btn btn-link',
        },
        modalInventario: {
            id: 'modalInventario',
            header: true,
            cancelbtn: 'Cerrar',
            cancelclass: 'btn btn-link',
        },
        modalDataZoom: {
            id: 'modalDataZoom',
            header: true,
            title: "Data Zoom",
            cancelbtn: 'Cerrar',
            showaccept: true,
            okbtn: 'Guardar',
            form: "formDataZoom",
            cancelclass: 'btn btn-link',
        }
    },
    computed: {
    },
    mounted: function () {
        let $vue = this;
        let tipo = $vue.$refs.raptorAulas.getParameterByName('queries[tipo-aula]');
        if (tipo != null) {
            $vue.tipoAula = {codigo: tipo};
            $vue.$refs.raptorAulas.querie.push({name: 'tipo-aula', value: tipo});
        }
        $vue.$refs.raptorAulas.loadRemoteData();
    },
    methods: {
        verHorarioAula(item) {
            MODAL.init("lg");

            var aulaHorarioVue = new AulaHorarioVue();
            aulaHorarioVue.aula = {id: item.id};
            aulaHorarioVue.dias = [];
            aulaHorarioVue.horas = [];
            var component = aulaHorarioVue.$mount();

            MODAL.title("Horario Ambiente " + item.codigo);
            MODAL.buttons('');
            MODAL.body(component.$el);
            MODAL.show();
        },
        verAulas(item) {
            let $vue = this;
            $vue.aula = {};
            $vue.aula = Object.assign({}, item);
            $vue.openModal("aulaContenido");
        }, changeTipoAula() {
            let $vue = this;
            $vue.$refs.raptorAulas.querie = [];
            if ($vue.tipoAula != null) {
                $vue.$refs.raptorAulas.querie.push({name: 'tipo-aula', value: $vue.tipoAula.codigo});
            }
            $vue.$refs.raptorAulas.loadRemoteData();
        },
        verInventario(item) {
            let $vue = this;
            $vue.aula = {};
            $vue.aula = Object.assign({}, item);
            $vue.openModal("inventarioContenido");
        },
        cambiarEstado(item, newEstado) {
            let $vue = this;
            $vue.aula = {};
            $vue.aula = Object.assign({}, item);
            $vue.aula.estado = newEstado; //set nuevo estado
            $("#" + $vue.modalCambioEstado.form).parsley().destroy();
            $vue.openModal("cambioEstado");
        },
        editar(item) {
            location.href = APP.url(rutaModulo + '/editar/' + item.id);
        },
        verInventarioPage(item) {
            location.href = APP.url(rutaModulo + '/inventario/' + item.id);
        },
        verInventarioResumen(item) {
            location.href = APP.url(rutaModulo + '/inventario/' + item.id + '/resumen');
        },
        horarioSemanalPDF(item) {
            var now = moment();
            var day = now.day();
            var first = parseInt(day) - 1;
            var init = now.add(-first, 'days').format('DD/MM/YYYY');
            var end = now.add(6, 'days').format('DD/MM/YYYY');

            $.fileDownload("/general/aula/generatorpdf", {
                httpMethod: "POST",
                data: {
                    strAula: JSON.stringify({id: item.id}),
                    strAulaSuperior: JSON.stringify({id: item.aulaSuperior.id}),
                    fechaInicio: init,
                    fechaFin: end
                },
                successCallback: function (responseHtml, url) {

                },
                onFail: function (e) {
                    console.log(e);
                },
                failCallback: function (responseHtml, url) {
                    notify(Messages.errorComunicacion, 'error')
                }
            });
        },
        reporteProgramacion(item) {
            $.fileDownload("/general/aula/reporteProgramacion", {
                httpMethod: "POST",
                data: {
                    strAula: JSON.stringify({id: item.id})
                },
                successCallback: function (responseHtml, url) {

                },
                onFail: function (e) {
                    console.log(e);
                },
                failCallback: function (responseHtml, url) {
                    notify(Messages.errorComunicacion, 'error')
                }
            });
        },
        reporteLaboratorios(item) {
            $.fileDownload("/general/aula/reporteLaboratorios", {
                httpMethod: "POST",
                data: {
                    strAula: JSON.stringify({id: item.id})
                },
                successCallback: function (responseHtml, url) {

                },
                onFail: function (e) {
                    console.log(e);
                },
                failCallback: function (responseHtml, url) {
                    notify(Messages.errorComunicacion, 'error')
                }
            });
        },
        openModal(tipoModal) {
            let $vue = this;

            if (tipoModal == "cambioEstado") {
                $vue.modalCambioEstado.title = ($vue.aula.estado == 'ACT' ? 'Activación de Ambiente' : 'Desactivación de Ambiente');
                $vue.modalCambioEstado.okbtn = "Guardar";
                $vue.$refs.modalCambioEstado.open();
            }

            if (tipoModal == "aulaContenido") {
                $vue.modalAulaContenido.title = $vue.aula.nombre;
                $vue.$refs.modalAulaContenido.open();
            }

            if (tipoModal == "inventarioContenido") {
                $vue.modalInventario.title = $vue.aula.nombre;
                $vue.$refs.modalInventario.open();
            }
        },
        saveCambioEstado() {
            let $vue = this;
            if ($("#" + $vue.modalCambioEstado.form).parsley().validate() !== true) {
                notify("Debe completar todos los campos requeridos", "error");
                return;
            }

            $vue.aula.aulasContenido = undefined;
            axios.post("/" + rutaModulo + "/cambioEstado", $vue.aula)
                    .then(response => {
                        if (response.data.success) {
                            notify(response.data.message, "info");
                            $vue.$refs.raptorAulas.loadRemoteData();
                            $vue.$refs.modalCambioEstado.close();
                        } else {
                            notify(response.data.message, "warning");
                        }
                    }).catch(e => {
                notify(Messages.errorComunicacion, "error");
            });
        },
        eliminarAula(item) {
            let $vue = this;
            bootbox.confirm({
                message: '¿Está seguro que desea eliminar el registro del aula <b>' + item.codigo + '</b>?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: 'btn-danger'},
                    cancel: {label: 'Cancelar', className: 'btn-link'}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('general/aula/eliminar'),
                            type: 'POST',
                            async: true,
                            data: {id: item.id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    $vue.$refs.raptorAulas.loadRemoteData();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                notify(Messages.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        dataZoom(item) {
            let $vue = this;
            $vue.aulaZoom = Object.assign({}, item);
            $vue.$refs.modalDataZoom.open();
        },
        saveDataZoom() {
            let $vue = this;
            if ($("#" + $vue.modalDataZoom.form).parsley().validate() !== true) {
                notify("Debe completar todos los campos requeridos", "error");
                return;
            }

            var data = {};
            data.id = $vue.aulaZoom.id;
            data.usuarioZoom = $vue.aulaZoom.usuarioZoom;
            data.passZoom = $vue.aulaZoom.passZoom;
            bootbox.confirm({
                message: '¿Está seguro que desea agregar estos datos al aula?',
                buttons: {
                    confirm: {label: 'Si, agregar', className: 'btn-danger'},
                    cancel: {label: 'Cancelar', className: 'btn-link'}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('general/aula/agregarZoom'),
                            type: 'POST',
                            dataType: "json",
                            contentType: "application/json",
                            data: JSON.stringify(data),
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    $vue.$refs.raptorAulas.loadRemoteData();
                                } else {
                                    notify(response.message, "error");
                                }
                                $vue.$refs.modalDataZoom.close();
                            },
                            error: function () {
                                notify(Messages.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        }
    }
});
 