Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#pageAnexosVUE',
    data: {
        cicloAnexos: JSON.parse(cicloJson),
        anexosSuper: JSON.parse(anexosSuperJson),
        departamentos: JSON.parse(departamentosJson),
        carreras: JSON.parse(carrerasJson),
        anexosURL: APP.url('academico/anexo/list'),
        pagination: {'total-items': 0, 'items-per-page': 100, 'max-size': 3, 'boundary-link-numbers': true},
        seleccionado: "",
        bgColorClass: {ingresantes: '', posgrados: '', departamentos: '', actividades: ''},
        ordenSelect: 0,
        cfgAnularAnexo: {
            id: 'anularAnexoModal',
            header: true,
            title: 'Anulación de Anexo',
            showaccept: false,
            cancelbtn: 'Cancelar',
            okbtn: 'Si, anular anexo',
            okclass: 'btn-danger'
        },
        cfgNuevoAnexo: {
            id: 'nuevoAnexoModal',
            header: true,
            title: 'Nuevo Anexo',
            showaccept: true,
            cancelbtn: 'Cancelar',
            okbtn: 'Guardar',
            okclass: 'btn-primary'
        },
        anexoTempo: {id: '', departamentoAcademico: {}, carrera: {}, anexoSuperior: {}},
        anularAnexo: "abc",
        ciclos: [],
        isSearchingCiclos: false
    },
    mounted: function () {
        $(".numerico").numeric({negative: false});

        let $vue = this;
        let tipo = $vue.$refs.raptorAnexos.getParameterByName('queries[anexo-superior]');
        tipo = (tipo == null) ? 'ingresantes' : tipo;
        if (tipo != '') {
            $vue.bgColorClass[tipo] = 'bg-light';
            $vue.seleccionado = tipo;
            $vue.$refs.raptorAnexos.querie.push({name: 'anexo-superior', value: tipo});
        }
        $vue.$refs.raptorAnexos.repreload();
    },
    methods: {
        getAnexoSuper() {
            let $vue = this;
            let idsAnexosSuper = {ingresantes: 1, posgrados: 4, departamentos: 2, actividades: 3};
            for (var i = 0; i < $vue.anexosSuper.length; i++) {
                if ($vue.anexosSuper[i].id == idsAnexosSuper[$vue.seleccionado]) {
                    return $vue.anexosSuper[i];
                }
            }
            return {};
        },
        verEditar(item) {
            let $vue = this;
            $vue.cfgNuevoAnexo.title = "Edición: " + item.nombre;
            $vue.anexoTempo = Object.assign({}, item);
            $vue.$refs.nuevoAnexoModal.open();
        },
        verNuevoAnexo() {
            let $vue = this;
            $vue.cfgNuevoAnexo.title = "Nuevo Anexo";
            $vue.anexoTempo = {id: '', departamentoAcademico: {}, carrera: {}, anexoSuperior: $vue.getAnexoSuper()};
            $vue.$refs.nuevoAnexoModal.open();
        },
        upper(e) {
            e.target.value = e.target.value.toUpperCase();
        },
        labelCarrera(item) {
            if (item.id == undefined) {
                return "";
            }
            if (item.tipoEnum == undefined) {
                return "";
            }
            return item.tipoEnum.value + ' - ' + item.nombre;
        },
        verSaveAnexo() {
            var form = $("#formAnexo");
            if (!form.parsley().validate()) {
                return;
            }

            let $vue = this;
            bootbox.confirm({
                message: '¿Está seguro que desea guarda este anexo?',
                buttons: {
                    confirm: {label: 'Si, guardar', className: 'btn-success'},
                    cancel: {label: 'No', className: 'btn-link'}
                },
                callback: function (aceptar) {
                    if (aceptar) {
                        setTimeout(function () {
                            $vue.saveAnexo();
                        }, 200);
                    }
                }
            });
        },
        saveAnexo() {
            console.log("save save save save save save save save save ")
            let $vue = this;
            $.ajax({
                url: APP.url('academico/anexo/save'),
                dataType: 'json',
                type: 'POST',
                contentType: "application/json",
                async: true,
                data: JSON.stringify($vue.anexoTempo),
                success(response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.nuevoAnexoModal.close();
                        $vue.$refs.raptorAnexos.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        changeCiclo(item) {
            let $vue = this;
            $.ajax({
                url: APP.url('academico/anexo/changeCiclo'),
                dataType: 'json',
                type: 'POST',
                contentType: "application/json",
                async: true,
                data: JSON.stringify(item),
                success(response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.raptorAnexos.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        searchCiclos(search) {
            let $vue = this;
            $vue.isSearchingCiclos = true;

            $.ajax({
                url: APP.url('academico/anexo/allCiclos'),
                dataType: 'json',
                type: 'POST',
                async: true,
                data: {nombre: search},
                success(response) {
                    $vue.isSearchingCiclos = false;
                    if (response.success) {
                        $vue.ciclos = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        changeAnular() {
            let $vue = this;
            if ($vue.anularAnexo == "SI") {
                $vue.cfgAnularAnexo.showaccept = true;
            } else {
                $vue.cfgAnularAnexo.showaccept = false;
            }
        },
        textColorClass(item) {
            let $vue = this;
            if (item.id == $vue.ordenSelect) {
                setTimeout(function () {
                    $vue.ordenSelect = 0;
                }, 1500);
                return "text-warning";
            }
            return "text-primary";
        },
        cambiarOrden(item, direccion) {
            let $vue = this;
            $vue.ordenSelect = item.id;
            $.ajax({
                url: APP.url('academico/anexo/' + item.id + '/cambiarOrden/' + direccion),
                dataType: "json",
                type: 'POST',
                async: true,
                success(response) {
                    if (response.success) {
                        $vue.$refs.raptorAnexos.loadRemoteData();
                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error(response) {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        verCambiarEstado(item, accion) {
            let $vue = this;
            if (accion == 'activar') {
                bootbox.confirm({
                    message: '¿Está seguro que desea activar este anexo?',
                    buttons: {
                        confirm: {label: 'Si, activar', className: 'btn-success'},
                        cancel: {label: 'No', className: 'btn-link'}
                    },
                    callback: function (aceptar) {
                        if (aceptar) {
                            $vue.cambiarEstado(item, accion);
                        }
                    }
                });

            } else if (accion == 'desactivar') {
                $vue.anularAnexo = "abc";
                $vue.anexoTempo = Object.assign({}, item);
                $vue.$refs.anularAnexoModal.open();
            }
        },
        saveCambioEstado() {
            let $vue = this;
            $vue.cambiarEstado($vue.anexoTempo, "desactivar");
        },
        cambiarEstado(item, accion) {
            let $vue = this;
            $vue.ordenSelect = item.id;
            $.ajax({
                url: APP.url('academico/anexo/cambiarEstado/' + accion),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: true,
                data: JSON.stringify(item),
                success(response) {
                    if (response.success) {
                        $vue.$refs.anularAnexoModal.close();
                        $vue.$refs.raptorAnexos.loadRemoteData();
                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error(response) {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        verAnexosInferiores(tipo) {
            let $vue = this;
            if ($vue.seleccionado === '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

                $vue.$refs.raptorAnexos.querie.push({name: 'anexo-superior', value: tipo});
                $vue.$refs.raptorAnexos.loadRemoteData();

            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

                $vue.$refs.raptorAnexos.querie.push({name: 'anexo-superior', value: tipo});
                $vue.$refs.raptorAnexos.loadRemoteData();

            } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
            }
        }
    }
});