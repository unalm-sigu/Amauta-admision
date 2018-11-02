Vue.component("multiselect", window.VueMultiselect.default);
Vue.component("seccion-det-component", {
    template: "#seccionDetComp",
    props: {
        seccion: null
    }, watch: {
        seccion(newValue) {
        }
    }
});

new Vue({
    el: '#gpoSeccionesVUE',
    mixins: [VueLoader],
    data: {
        gpoSeccionesURL: APP.url('academico/gposeccion/list'),
        seleccionado: '',
        bgColorClass: {ingresantes: '', departamentos: '', postgrados: '', actividades: ''},
        anexosSup: {ingresantes: 1, departamentos: 2, postgrados: 4, actividades: 3},
        anexos: [],
        anexosVisibles: [],
        anexoSelect: {},
        restriccionModal: {
            id: 'modalRestriccion',
            header: true,
            title: 'Restricciones Modalidad / Facultad / Especialidad',
            modalsize: 'modal-md'
        },
        nuevoGpoSeccModal: {
            id: 'modalNuevoGpoSecc',
            header: true,
            title: 'Nuevo grupo de secciones',
            modalsize: 'modal-md'
        },
        dataCloneCiclo: {
            id: 'modalCloneCiclo',
            title: 'Copiar Ciclo',
            header: true,
        },
        seccionSelect: {},
        tipoRestriccion: '',
        ciclo: {descripcion: ""},
        //cantidadGrupoSeccion: cantidad,
//        clonar: 0,
//        cerrarClonacion: false,
//        cicloSeccion: {},
        resumen: {
            ingresantes: 0,
            departamentos: 0,
            postGrados: 0,
            actividades: 0
        },
        orderbycodigo: false,
        cursos: [],
        isLoadingCursos: false,
        anexosPadres: [],
        anexosHijos: [],
        anexoPadreCurso: {},
        newGrupoSeccion: {curso: {}, anexoBoletin: {}}
    },
    computed: {
        condicion1() {
            let $vue = this;
            return $vue.ciclo.fechaClonacion != '';
        },
        condicion2() {
            let $vue = this;
            return $vue.ciclo.fechaClonacion == '';
        },
        condicion3() {
            let $vue = this;
            return $vue.ciclo.fechaClonacion != '' && $vue.ciclo.fechaCierreClonacion == '';
        },
        condicion4() {
            let $vue = this;
            return $vue.ciclo.fechaCierreOrden == '' && $vue.ciclo.fechaClonacion != '';
        },

    },
    watch: {
        orderbycodigo() {
            let $vue = this;
            $vue.loadRegistros(null);
        }
    },
    mounted: function () {
        let $vue = this;

        let tipo = $vue.$refs.load.getParameterByName('queries[anexo-superior]');
        tipo = (tipo == null) ? '' : tipo;
        if (tipo != '') {
            $vue.bgColorClass[tipo] = 'bg-light';
            $vue.seleccionado = tipo;
            $vue.$refs.load.querie.push({name: 'anexo-superior', value: tipo});
        }

        let orderBy = $vue.$refs.load.getParameterByName('queries[order-codigo]');
        orderBy = (orderBy == null) ? '' : orderBy;
        if (orderBy != '') {
            $vue.orderbycodigo = true;
            $vue.$refs.load.querie.push({name: 'order-codigo', value: orderBy});
        }

        $vue.loadDataInicial();

        let anx = $vue.$refs.load.getParameterByName('queries[anexo]');
        anx = (anx == null) ? '' : anx;
        if (anx == '') {
            $vue.$refs.load.repreload();
        }

        $vue.updateDataCiclo();

    },
    methods: {
        nuevoGpoSecc() {
            let $vue = this;
            $vue.newGrupoSeccion = {curso: {}, anexoBoletin: {}};
            $vue.$refs.modalNuevoGpoSecc.open();
        },
        saveGpoSecc() {
            let $vue = this;
            var form = $("#formNuevoGpoSecc");
            if (!form.parsley().validate()) {
                return;
            }

            $.ajax({
                url: APP.url('academico/gposeccion/saveGpoHeader'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: true,
                data: JSON.stringify($vue.newGrupoSeccion),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.modalNuevoGpoSecc.close();
                        let rpta = response.data;
                        let lista = Base64.encode(rpta.lista);
                        location.href = APP.url("academico/gposeccion/" + rpta.primero + "/editar") + $vue.getOrigenURL() + "&ids=" + lista;
                        notify(response.message, 'info');

                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        verAnexoSuperior(item) {
            let $vue = this;
            let anxSup = {};
            $vue.anexosHijos = [];

            for (var i = 0; i < $vue.anexosPadres.length; i++) {
                if (item.modalidadEstudio.codigo == 'PRE' && $vue.anexosPadres[i].id == 2) {
                    anxSup = $vue.anexosPadres[i];
                }
                if (item.modalidadEstudio.codigo == 'EPG' && $vue.anexosPadres[i].id == 4) {
                    anxSup = $vue.anexosPadres[i];
                }
            }

            for (var i = 0; i < $vue.anexos.length; i++) {
                let anx = $vue.anexos[i];
                if (anxSup.id == anx.anexoSuperior.id) {

                    $vue.anexosHijos.push(anx);
                    if (item.modalidadEstudio.codigo == 'PRE' && item.departamentoAcademico.codigo == anx.codigo) {
                        $vue.newGrupoSeccion.anexoBoletin = anx;
                    }
                    if (item.modalidadEstudio.codigo == 'EPG' && item.carrera.codigo == anx.codigo) {
                        $vue.newGrupoSeccion.anexoBoletin = anx;
                    }
                }
            }
            $vue.anexoPadreCurso = anxSup;
        },
        verAnexosHijos(item) {
            let $vue = this;
            let modal = $vue.newGrupoSeccion.curso.modalidadEstudio;
            let dpto = $vue.newGrupoSeccion.curso.departamentoAcademico;
            let carr = $vue.newGrupoSeccion.curso.carrera;
            console.log(modal.codigo + " ::: " + dpto.codigo + " ::: " + carr.codigo)

            $vue.anexosHijos = [];
            $vue.newGrupoSeccion.anexoBoletin = {};

            //*
            for (var i = 0; i < $vue.anexos.length; i++) {
                let anx = $vue.anexos[i];
                if (item.id == anx.anexoSuperior.id) {
                    $vue.anexosHijos.push(anx);
                    if (modal.codigo == 'PRE' && dpto.codigo == anx.codigo) {
                        $vue.newGrupoSeccion.anexoBoletin = anx;
                    }
                    if (modal.codigo == 'EPG' && carr.codigo == anx.codigo) {
                        $vue.newGrupoSeccion.anexoBoletin = anx;
                    }
                }
            }
            //*/
        },
        labelCurso(item) {
            if (item.id == undefined) {
                return "";
            }
            return item.codigo + " - " + item.nombre;
        },
        searchCursos(nombre) {
            let $vue = this;
            $vue.isLoadingCursos = true;
            $.ajax({
                url: APP.url('academico/gposeccion//allCursos'),
                dataType: 'json',
                type: 'POST',
                async: true,
                data: {nombre: nombre},
                success(response) {
                    $vue.isLoadingCursos = false;
                    if (response.success) {
                        $vue.cursos = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        verRestriccion(seccion, gpoSecc, tipo) {
            let $vue = this;
            seccion.grupoSeccion = gpoSecc;
            $vue.seccionSelect = seccion;
            $vue.tipoRestriccion = tipo;
            if (seccion.restriccionesRepitencia.length > 0 && tipo == "REP") {
                $vue.restriccionModal.title = "Restricciones de Repitencia";
            } else if (seccion.restriccionesCarrera.length > 0 && tipo == "CARR") {
                $vue.restriccionModal.title = "Restricciones de Carrera";
            } else if (seccion.restriccionesFacultad.length > 0 && tipo == "FAC") {
                $vue.restriccionModal.title = "Restricciones de Facultad";
            } else if (seccion.restriccionesModalidad.length > 0 && tipo == "MOD") {
                $vue.restriccionModal.title = "Restricciones de Modalidad de Estudio";
            }
            $vue.$refs.modalRestriccion.open();
        },
        tieneRestricciones(seccion) {
            if (seccion.restriccionCapa != "") {
                return true;
            }
            if (seccion.restriccionesRepitencia.length > 0) {
                return true;
            }
            if (seccion.restriccionesModalidad.length > 0) {
                return true;
            }
            if (seccion.restriccionesFacultad.length > 0) {
                return true;
            }
            if (seccion.restriccionesCarrera.length > 0) {
                return true;
            }
            return false;
        },
        existeAnexoSelect() {
            let $vue = this;
            if ($vue.anexoSelect == null) {
                return false;
            }
            if ($vue.anexoSelect.id === undefined) {
                return false;
            }
            return true;
        },
        clearAll() {
            let $vue = this;
            $vue.anexoSelect = {};
            $vue.loadRegistros(null);
        },
        verificarAnexoSelect() {
            let $vue = this;
            if ($vue.seleccionado == '') {
                return;
            }
            if ($vue.anexoSelect.id === undefined) {
                return;
            }
            let sup = $vue.anexosSup[$vue.seleccionado];
            if (sup !== $vue.anexoSelect.anexoSuperior.id) {
                $vue.anexoSelect = {};
            }

        },
        loadAnexosVisibles() {
            let $vue = this;
            $vue.anexosVisibles = [];
            for (var i = 0; i < $vue.anexos.length; i++) {
                let anx = $vue.anexos[i];
                if ($vue.seleccionado == '') {
                    $vue.anexosVisibles.push(anx);
                } else {
                    let sup = $vue.anexosSup[$vue.seleccionado];
                    if (sup == anx.anexoSuperior.id) {
                        $vue.anexosVisibles.push(anx);
                    }
                }
            }
        },
        loadDataInicial() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: APP.url('academico/gposeccion/allData'),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.anexos = response.data.anexos;
                        $vue.anexosPadres = response.data.anexosSup;
                        $vue.loadAnexosVisibles();

                        let anx = $vue.$refs.load.getParameterByName('queries[anexo]');
                        anx = (anx == null) ? '' : anx;
                        if (anx == '') {
                            return;
                        }
                        for (var i = 0; i < $vue.anexosVisibles.length; i++) {
                            let anexo = $vue.anexosVisibles[i];
                            let idAnx = parseInt(anx);
                            if (idAnx == anexo.id) {
                                $vue.anexoSelect = anexo;
                                $vue.$refs.load.querie.push({name: 'anexo', value: $vue.anexoSelect.id});
                            }
                        }

                        $vue.$refs.load.repreload();
                    }
                }
            });
        },
        getRowspanGpoSecc(item) {
            let rows = 0;
            for (var i = 0; i < item.secciones.length; i++) {
                var secc = item.secciones[i];
                var docs = secc.docenteSeccion.length;
                rows += (docs == 0) ? 1 : docs;
            }
            rows = (rows == 0) ? 1 : rows;
            return rows;
        },
        getRowspanSecc(item) {
            let rows = item.docenteSeccion.length;
            rows = (rows == 0) ? 1 : rows;
            return rows;
        },
        tipoSeccion(item) {
            return item.tipoSeccionEnum.value.replace("Curso", "");
        },
        esPrincipal(profeSecc) {
            if (profeSecc.estadoEnum.name == 'ACT' && profeSecc.principal == 1) {
                return true;
            }
            return false;
        },
        editarGpoSeccion(item) {
            let $vue = this;
            location.href = APP.url("academico/gposeccion/" + item.id + "/editar") + $vue.getOrigenURL();
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        verAnexo(tipo) {
            let $vue = this;
            $vue.$refs.load.querie = [];

            if ($vue.seleccionado === '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;
                $vue.$refs.load.querie.push({name: 'anexo-superior', value: tipo});

            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;
                $vue.$refs.load.querie.push({name: 'anexo-superior', value: tipo});

            } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';
                $vue.$refs.load.changeUrl('queries[anexo-superior]', null);
            }

            $vue.loadAnexosVisibles();
            $vue.verificarAnexoSelect();
            $vue.settingUrlAnexoInferior();
            $vue.settingUrlOrderCodigo();
            $vue.$refs.load.loadRemoteData();

        },
        settingUrlOrderCodigo() {
            let $vue = this;

            if (!$vue.orderbycodigo) {
                $vue.$refs.load.changeUrl('queries[order-codigo]', null);
                return;
            }
            $vue.$refs.load.querie.push({name: 'order-codigo', value: "asc"});
        },
        settingUrlAnexoInferior() {
            let $vue = this;

            if ($vue.anexoSelect == null) {
                $vue.$refs.load.changeUrl('queries[anexo]', null);
                return;
            }
            if ($vue.anexoSelect.id === undefined) {
                $vue.$refs.load.changeUrl('queries[anexo]', null);
                return;
            }
            $vue.$refs.load.querie.push({name: 'anexo', value: $vue.anexoSelect.id});
        },
        loadRegistros(item) {
            let $vue = this;
            $vue.$refs.load.querie = [];
            if ($vue.seleccionado !== '') {
                $vue.$refs.load.querie.push({name: 'anexo-superior', value: $vue.seleccionado});
            }
            $vue.settingUrlAnexoInferior();
            $vue.settingUrlOrderCodigo();
            $vue.$refs.load.loadRemoteData();

        },
        clonarCiclo() {
            let $vue = this;
            $vue.ciclo = {id: null};
            $vue.$refs.modalCloneCiclo.open();
        },
        saveCloneCiclo() {

            let $vue = this;

            if ($vue.ciclo.id == null) {
                return;
            }
            var mibox = bootbox.dialog({message: APP.template.wait, closeButton: false});
            $vue.showLoader();

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/clonarciclo'),
                async: false,
                data: {id: $vue.ciclo.id},
                success: function (response) {
                    if (response.success) {
                        $vue.updateDataCiclo();
                        $vue.$refs.load.loadRemoteData();
                        $vue.$refs.modalCloneCiclo.close();
                    }
                    bootbox.alert({
                        message: response.message,
                        buttons: {ok: {label: "Aceptar"}}
                    });
                    mibox.modal('hide');
                    $vue.hideLoader();
                },
                error: function () {
                    $vue.hideLoader();
                    notify(MESSAGES.errorComunicacion, "error");
                    mibox.modal('hide');
                }
            });
        },
        updateDataCiclo() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/findDataCiclo'),
                async: false,
                success: function (response) {
                    if (response.success) {
                        $vue.resumen = response.data.resumen;
                        $vue.ciclo = response.data.ciclo;
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        ordenarCiclo() {
            let $vue = this;

            bootbox.confirm({
                message: "<h4>¿Desea ordenar el código de todos las secciones?</h4>",
                buttons: {
                    cancel: {label: "Cancelar", className: "btn-link"},
                    confirm: {label: "Si, ordenar", className: "btn-danger"}
                },
                callback(result) {
                    if (result) {
                        $vue.showLoader();
                        $.ajax({
                            method: 'POST',
                            async: false,
                            url: APP.url('academico/gposeccion/reordenar'),
                            success: function (response) {
                                if (response.success) {
                                    $vue.$refs.load.loadRemoteData();
                                    $vue.updateDataCiclo();
                                }
                                bootbox.alert({
                                    message: response.message,
                                    buttons: {ok: {label: "Aceptar"}}
                                });
                                $vue.hideLoader();
                            },
                            error: function () {
                                bootbox.alert({
                                    message: MESSAGES.errorComunicacion,
                                    buttons: {ok: {label: "Aceptar"}}
                                });
                                $vue.hideLoader();
                            }
                        });
                    }
                }
            });
        },
        limpiarCiclo() {
            let $vue = this;

            bootbox.confirm({
                message: "<h4>¿Desea eliminar todos los registros?</h4>",
                buttons: {
                    cancel: {label: "Cancelar", className: "btn-link"},
                    confirm: {label: "Si, limpiar", className: "btn-danger"}
                },
                callback(result) {
                    if (result) {
                        $vue.showLoader();
                        $.ajax({
                            method: 'POST',
                            async: false,
                            url: APP.url('academico/gposeccion/limpiarciclo'),
                            success: function (response) {
                                if (response.success) {
                                    $vue.$refs.load.loadRemoteData();
                                    $vue.updateDataCiclo();

                                }
                                bootbox.alert({
                                    message: response.message,
                                    buttons: {ok: {label: "Aceptar"}}
                                });
                                $vue.hideLoader();
                            },
                            error: function () {
                                bootbox.alert({
                                    message: MESSAGES.errorComunicacion,
                                    buttons: {ok: {label: "Aceptar"}}
                                });
                                $vue.hideLoader();
                            }
                        });
                    }
                }
            });

        },
        finalizarClonacion() {

            let $vue = this;

            bootbox.confirm({
//                title: "Finalizar clonación",
                message: '<h4 class="text-danger m-t-xs">¿Seguro que desea dar por finalizada la clonación?</h4> <p>Recuerde que después de esta acción ya no podrá limpiar los datos de este ciclo.</p>',
                buttons: {
                    cancel: {label: "Cancelar", className: "btn-link"},
                    confirm: {label: "Si, finalizar", className: "btn-warning"}
                },
                callback(result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            async: false,
                            url: APP.url('academico/gposeccion/cerrarClonacion'),
                            success: function (response) {
                                if (response.success) {
                                    $vue.updateDataCiclo();
                                    notify(response.message, 'info');
                                } else {
                                    notify(response.message, 'error');
                                }
                            },
                            error: function () {
                                notify(MESSAGES.errorComunicacion, 'error');
                            }
                        });
                    }
                }
            });

        },
        cerrarOrden() {

            let $vue = this;

            bootbox.confirm({
//                title: "Finalizar ordenamiento de códigos",
                message: '<h4 class="m-t-xs">¿Seguro que desea dar por finalizado el ordenamiento de códigos?</h4>',
                buttons: {
                    cancel: {label: "Cancelar", className: "btn-link"},
                    confirm: {label: "Si, finalizar", className: "btn-warning"}
                },
                callback(result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            async: false,
                            url: APP.url('academico/gposeccion/cerrarorden'),
                            success: function (response) {
                                if (response.success) {
                                    $vue.updateDataCiclo();
                                    notify(response.message, 'info');
                                } else {
                                    notify(response.message, 'error');
                                }
                            },
                            error: function () {
                                notify(MESSAGES.errorComunicacion, 'error');
                            }
                        });
                    }
                }
            });

//            swal({
//                title: "Cerrar Orden",
//                text: "¿Desea cerrar la opción ordenar código?",
//                icon: "warning",
//                dangerMode: true,
//                buttons: {
//                    cancel: {text: "Cancelar", closeModal: true, visible: true},
//                    confirm: {text: "Aceptar", closeModal: false}
//                }
//            }).then((value) => {
//
//                if (value != true) {
//                    return;
//                }
//
//                $.ajax({
//                    method: 'POST',
//                    async: false,
//                    url: APP.url('academico/gposeccion/cerrarorden'),
//                    success: function (response) {
//                        if (response.success) {
//
//                            notify(response.message, 'info');
//
//                            $vue.$refs.load.loadRemoteData();
//
//                            swal({text: response.message, icon: "success", button: false, timer: 1000});
//
//                        } else {
//
//                            swal({text: response.message, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
//                        }
//                    },
//                    error: function () {
//
//                        swal({text: MESSAGES.errorComunicacion, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
//                    }
//                });
//
//            }).catch(err => {
//
//                swal(MESSAGES.errorComunicacion, "error");
//
//            });

        }
    }
});

