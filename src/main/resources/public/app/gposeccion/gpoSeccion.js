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
        gpoSeccionesURL: APP.url(rutaModulo + '/list'),
        seleccionado: '',
        estadoSeleccionada: '',
        dictadoSeleccionado: '',
        bgColorClass: {ingresantes: '', departamentos: '', postgrados: '', actividades: ''},
        bgColorEstadoClass: {activos: '', inactivos: ''},
        bgColorDictadoClass: {modulares: '', semestrales: ''},
        anexosSup: {ingresantes: 1, departamentos: 2, postgrados: 4, actividades: 3},
        anexos: [],
        anexosVisibles: [],
        anexoSelect: null,
        restriccionModal: {
            id: 'modalRestriccion',
            header: true,
            title: 'Restricciones Modalidad / Facultad / Especialidad',
            modalsize: 'modal-md'
        },
        nuevoGpoSeccModal: VUE_MODAL.structFormAjax({
            id: 'modalNuevoGpoSecc',
            form: 'formNuevoGpoSecc',
            header: true,
            title: 'Nuevo grupo de secciones',
            revisarForm: false
        }),
        dataCloneCiclo: VUE_MODAL.structFormAjax({
            id: 'modalCloneCiclo',
            title: 'Clonación de información por ciclo',
            header: true
        }),
        seccionSelect: {},
        tipoRestriccion: '',
        ciclo: {descripcion: ""},
        cicloPosgrado: {descripcion: ""},
        cicloClonacion: {descripcion: ""},
        resumen: {
            ingresantes: 0,
            departamentos: 0,
            postGrados: 0,
            actividades: 0,
            activos: 0,
            inactivos: 0,
            semestrales: 0,
            modulares: 0
        },
        cursos: [],
        isLoadingCursos: false,
        anexosPadres: [],
        anexosHijos: [],
        listCicloAcademico: [],
        anexoPadreCurso: {},
        newGrupoSeccion: {curso: {}, anexoBoletin: {}},
        configConfirmAction: VUE_MODAL.structConfirm({}),
        cicloClonacionBean: {copiarAulasOera: false, copiarAulasDptos: false, copiarAulasPosgrado: false},
        gpoSeccionesSelects: [],
        direccionSeccion: 0,
        direccionCurso: 0,
        styleOrdenSeccion: "",
        styleOrdenCurso: "",
        ordenRegistros: "",

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
        }
    },
    mounted: function () {
        let $vue = this;
        $vue.loadDataInicial();
    },
    methods: {
        getParameterQuery(param) {
            let $vue = this;
            let value = $vue.$refs.raptorGpoSecc.getParameterByName('queries[' + param + ']');
            value = (value === null) ? '' : value;
            return value;
        },
        setParameterQuery(param, value) {
            let $vue = this;
            if (value !== '') {
                $vue.$refs.raptorGpoSecc.querie.push({name: param, value: value});
            }
        },
        configInicio() {
            let $vue = this;
            let anxSup = $vue.getParameterQuery('anexo-superior');
            if (anxSup !== '') {
                $vue.bgColorClass[anxSup] = 'bg-light';
                $vue.seleccionado = anxSup;
            }

            $vue.loadAnexosVisibles();
            let anx = $vue.getParameterQuery('anexo');
            if (anx !== '') {
                for (var i = 0; i < $vue.anexosVisibles.length; i++) {
                    let anxVis = $vue.anexosVisibles[i];
                    if (anxVis.id == anx) {
                        $vue.anexoSelect = anxVis;
                    }
                }
            }

            let orden = $vue.getParameterQuery('orden-registros');
            if (orden !== '') {
                $vue.ordenRegistros = orden;
                $vue.direccionSeccion = $vue.getOrdenador(orden, "seccion");
                $vue.direccionCurso = $vue.getOrdenador(orden, "curso");
            }

            let estado = $vue.getParameterQuery('estado');
            if (estado !== '') {
                $vue.bgColorEstadoClass[estado] = 'bg-light';
                $vue.estadoSeleccionada = estado;
            }

            let dictado = $vue.getParameterQuery('dictado');
            if (dictado !== '') {
                $vue.bgColorDictadoClass[dictado] = 'bg-light';
                $vue.dictadoSeleccionado = dictado;
            }

            $vue.setStyleOrdenSeccion();
            $vue.setStyleOrdenCurso();
            $vue.loadRegistros();
        },
        nuevoGpoSecc() {
            let $vue = this;
            $vue.newGrupoSeccion = {curso: {}, anexoBoletin: {}};
            $vue.nuevoGpoSeccModal.revisarForm = false;
            $vue.revisarFormNuevoGpoSeccion();
            $vue.$refs.modalNuevoGpoSecc.open();
        },
        revisarFormNuevoGpoSeccion() {
            let $vue = this;
            var form = $("#" + $vue.nuevoGpoSeccModal.form);
            form.parsley().destroy();
            if ($vue.nuevoGpoSeccModal.revisarForm) {
                setTimeout(function () {
                    form.parsley().validate();
                }, 300);
            }
        },
        saveGpoSecc() {
            let $vue = this;
            $vue.nuevoGpoSeccModal.revisarForm = true;
            var form = $("#" + $vue.nuevoGpoSeccModal.form);
            if (!form.parsley().validate()) {
                return;
            }

            $vue.$refs.modalNuevoGpoSecc.beginProcessing();

            $.ajax({
                url: APP.url(rutaModulo + '/saveGpoHeader'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: true,
                data: JSON.stringify($vue.newGrupoSeccion),
                success: function (response) {
                    $vue.$refs.modalNuevoGpoSecc.confirmReaction(response.success);
                    if (response.success) {
                        $vue.$refs.modalNuevoGpoSecc.close();
                        let rpta = response.data;
                        let lista = Base64.encode(rpta.lista);
                        location.href = APP.url(rutaModulo + "/" + rpta.primero + "/editar") + $vue.getOrigenURL() + "&ids=" + lista;
                        notify(response.message, 'info');

                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    $vue.$refs.modalNuevoGpoSecc.confirmReaction(false);
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
            $vue.revisarFormNuevoGpoSeccion();
        },
        verAnexosHijos(item) {
            let $vue = this;
            let modal = $vue.newGrupoSeccion.curso.modalidadEstudio;
            let dpto = $vue.newGrupoSeccion.curso.departamentoAcademico;
            let carr = $vue.newGrupoSeccion.curso.carrera;
            console.log(modal.codigo + " ::: " + dpto.codigo + " ::: " + carr.codigo)

            $vue.anexosHijos = [];
            $vue.newGrupoSeccion.anexoBoletin = {};

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
                url: APP.url(rutaModulo + '/allCursos'),
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
        clearAnexo() {
            let $vue = this;
            $vue.anexoSelect = null;
            $vue.loadRegistros();
        },
        verificarAnexoSelect() {
            let $vue = this;
            if ($vue.seleccionado === '') {
                return;
            }
            if ($vue.anexoSelect === null) {
                return;
            }
            let sup = $vue.anexosSup[$vue.seleccionado];
            if (sup !== $vue.anexoSelect.anexoSuperior.id) {
                $vue.anexoSelect = null;
            }

        },
        loadAnexosVisibles() {
            console.log("33333")
            let $vue = this;
            $vue.anexosVisibles = [];
            for (var i = 0; i < $vue.anexos.length; i++) {
                let anx = $vue.anexos[i];
                if ($vue.seleccionado === '') {
                    console.log("1111")
                    $vue.anexosVisibles.push(anx);
                } else {
                    console.log("2222")
                    let sup = $vue.anexosSup[$vue.seleccionado];
                    if (sup === anx.anexoSuperior.id) {
                        $vue.anexosVisibles.push(anx);
                    }
                }
            }
        },
        loadDataInicial() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: APP.url(rutaModulo + '/allData'),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.anexos = response.data.anexos;
                        $vue.anexosPadres = response.data.anexosSup;
                        console.log("$vue.anexos=" + $vue.anexos.length)
                        $vue.configInicio();
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
            location.href = APP.url(rutaModulo + "/" + item.id + "/editar") + $vue.getOrigenURL();
        },
        getOrigenURL() {
            var url = window.location.href;
            console.log(url)
            return "?origen=" + Base64.encode(url);
        },
        verAnexo(tipo) {
            let $vue = this;
            $vue.$refs.raptorGpoSecc.querie = [];

            if ($vue.seleccionado === '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

            } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';
            }

            $vue.loadAnexosVisibles();
            $vue.verificarAnexoSelect();
            $vue.loadRegistros();
        },
        loadRegistros() {
            let $vue = this;
            $vue.$refs.raptorGpoSecc.querie = [];
            $vue.$refs.raptorGpoSecc.changeUrl('queries[anexo-superior]', null);
            $vue.$refs.raptorGpoSecc.changeUrl('queries[anexo]', null);
            $vue.$refs.raptorGpoSecc.changeUrl('queries[orden-registros]', null);
            $vue.$refs.raptorGpoSecc.changeUrl('queries[estado]', null);
            $vue.$refs.raptorGpoSecc.changeUrl('queries[dictado]', null);

            if ($vue.seleccionado !== '') {
                $vue.setParameterQuery('anexo-superior', $vue.seleccionado);
            }
            if ($vue.anexoSelect !== null) {
                $vue.setParameterQuery('anexo', $vue.anexoSelect.id);
            }
            if ($vue.ordenRegistros !== "") {
                $vue.setParameterQuery('orden-registros', $vue.ordenRegistros);
            }
            if ($vue.estadoSeleccionada !== "") {
                $vue.setParameterQuery('estado', $vue.estadoSeleccionada);
            }
            if ($vue.dictadoSeleccionado !== "") {
                $vue.setParameterQuery('dictado', $vue.dictadoSeleccionado);
            }
            $vue.$refs.raptorGpoSecc.loadRemoteData(true);
            $vue.updateDataCiclo();
        },
        clonarCiclo() {
            let $vue = this;
            $vue.cicloClonacion = {id: null};
            $vue.$refs.modalCloneCiclo.open();
        },
        saveCloneCiclo() {
            let $vue = this;
            if ($vue.cicloClonacionBean.cicloOrigen.id == null) {
                return;
            }

            $vue.$refs.modalCloneCiclo.beginProcessing();
            $.ajax({
                method: 'POST',
                url: APP.url(rutaModulo + '/clonarciclo'),
                data: JSON.stringify($vue.cicloClonacionBean),
                contentType: "application/json",
                success: function (response) {
                    $vue.$refs.modalCloneCiclo.confirmReaction(response.success);
                    if (response.success) {
                        $vue.updateDataCiclo();
                        $vue.$refs.raptorGpoSecc.loadRemoteData();
                        notifyBootbox(response.message, "success");

                    } else {
                        notifyBootbox(response.message, "warning");
                    }
                },
                error: function () {
                    $vue.$refs.modalCloneCiclo.confirmReaction(false);
                    notifyBootbox(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        updateDataCiclo() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(rutaModulo + '/findDataCiclo'),
                async: false,
                success: function (response) {
                    if (response.success) {
                        $vue.resumen = response.data.resumen;
                        $vue.ciclo = response.data.ciclo;
                        $vue.cicloPosgrado = response.data.cicloPosgrado;
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        verOrdenarCiclo() {
            let $vue = this;
            $vue.configConfirmAction = VUE_MODAL.structConfirm({
                message: "¿Desea ordenar el código de todos las secciones?",
                okbtn: "Si, ordenar",
                okclass: "btn-warning",
                okaction: $vue.ordenarCiclo,
                okbtnprocessing: '<i class="fa fa-spinner fa-pulse fa-fw"></i> Ordenando...'
            });
            $vue.$refs.modalConfirmAction.open();
        },
        ordenarCiclo() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(rutaModulo + '/reordenar'),
                success: function (response) {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.success);
                    if (response.success) {
                        $vue.$refs.raptorGpoSecc.loadRemoteData();
                        $vue.updateDataCiclo();
                        notifyBootbox(response.message, "success");
                    } else {
                        notifyBootbox(response.message, "warning");
                    }
                },
                error: function () {
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                    notifyBootbox(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        verLimpiarCiclo() {
            let $vue = this;
            $vue.configConfirmAction = VUE_MODAL.structConfirm({
                message: "¿Desea eliminar todos los registros del ciclo?",
                okbtn: "Si, eliminar",
                okclass: "btn-danger",
                okaction: $vue.limpiarCiclo,
                okbtnprocessing: '<i class="fa fa-spinner fa-pulse fa-fw"></i> Elimnando...'
            });
            $vue.$refs.modalConfirmAction.open();

        },
        limpiarCiclo() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(rutaModulo + '/limpiarciclo'),
                success: function (response) {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.success);
                    if (response.success) {
                        $vue.$refs.raptorGpoSecc.loadRemoteData();
                        $vue.updateDataCiclo();
                        notifyBootbox(response.message, "success");

                    } else {
                        notifyBootbox(response.message, "warning");
                    }
                },
                error: function () {
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                    notifyBootbox(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        verBoletinPregrado() {
            let $vue = this;
            $vue.configConfirmAction = VUE_MODAL.structConfirm({
                message: "¿Seguro que desea que se visualice este ciclo en el boletín de pregrado?",
                okbtn: "Si, visibilizar boletín",
                okclass: "btn-success",
                okbtnprocessing: '<i class="fa fa-spinner fa-pulse fa-fw"></i> Visibilizando...',
                okaction: function () {

                    $.ajax({
                        method: 'POST',
                        async: false,
                        url: APP.url(rutaModulo + '/verBoletinPregrado'),
                        success: function (response) {
                            $vue.$refs.modalConfirmAction.confirmReaction(response.success);
                            if (response.success) {
                                $vue.updateDataCiclo();
                                notify(response.message, 'info');
                            } else {
                                notify(response.message, 'error');
                            }
                        },
                        error: function () {
                            $vue.$refs.modalConfirmAction.confirmReaction(false);
                            notify(MESSAGES.errorComunicacion, 'error');
                        }
                    });
                }
            });

            $vue.$refs.modalConfirmAction.open();
        },
        verBoletinPosgrado() {
            let $vue = this;
            $vue.configConfirmAction = VUE_MODAL.structConfirm({
                message: "¿Seguro que desea que se visualice este ciclo en el boletín de posgrado?",
                okbtn: "Si, visibilizar boletín",
                okclass: "btn-success",
                okbtnprocessing: '<i class="fa fa-spinner fa-pulse fa-fw"></i> Visibilizando...',
                okaction: function () {

                    $.ajax({
                        method: 'POST',
                        async: false,
                        url: APP.url(rutaModulo + '/verBoletinPosgrado'),
                        success: function (response) {
                            $vue.$refs.modalConfirmAction.confirmReaction(response.success);
                            if (response.success) {
                                $vue.updateDataCiclo();
                                notify(response.message, 'info');
                            } else {
                                notify(response.message, 'error');
                            }
                        },
                        error: function () {
                            $vue.$refs.modalConfirmAction.confirmReaction(false);
                            notify(MESSAGES.errorComunicacion, 'error');
                        }
                    });
                }
            });

            $vue.$refs.modalConfirmAction.open();
        },
        finalizarClonacion() {

            let $vue = this;

            $vue.configConfirmAction = VUE_MODAL.structConfirm({
                message: "¿Seguro que desea dar por finalizada la clonación? <p>Recuerde que después de esta acción ya no podrá limpiar los datos de este ciclo.</p>",
                okbtn: "Si, finalizar",
                okclass: "btn-warning",
                okbtnprocessing: '<i class="fa fa-spinner fa-pulse fa-fw"></i> Cerrando clonación...',
                okaction: function () {
                    $.ajax({
                        method: 'POST',
                        async: false,
                        url: APP.url(rutaModulo + '/cerrarClonacion'),
                        success: function (response) {
                            $vue.$refs.modalConfirmAction.confirmReaction(response.success);
                            if (response.success) {
                                $vue.updateDataCiclo();
                                notify(response.message, 'info');
                            } else {
                                notify(response.message, 'error');
                            }
                        },
                        error: function () {
                            $vue.$refs.modalConfirmAction.confirmReaction(false);
                            notify(MESSAGES.errorComunicacion, 'error');
                        }
                    });
                },
            });
            $vue.$refs.modalConfirmAction.open();
        },
        cerrarOrden() {

            let $vue = this;

            $vue.configConfirmAction = VUE_MODAL.structConfirm({
                message: "¿Seguro que desea dar por finalizado el ordenamiento de códigos?",
                okbtn: "Si, finalizar",
                okclass: "btn-success",
                okbtnprocessing: '<i class="fa fa-spinner fa-pulse fa-fw"></i> Cerrando ordenamiento...',
                okaction: function () {
                    $.ajax({
                        method: 'POST',
                        async: false,
                        url: APP.url(rutaModulo + '/cerrarorden'),
                        success: function (response) {
                            $vue.$refs.modalConfirmAction.confirmReaction(response.success);
                            if (response.success) {
                                $vue.updateDataCiclo();
                                notify(response.message, 'info');
                            } else {
                                notify(response.message, 'error');
                            }
                        },
                        error: function () {
                            $vue.$refs.modalConfirmAction.confirmReaction(false);
                            notify(MESSAGES.errorComunicacion, 'error');
                        }
                    });
                },
            });
            $vue.$refs.modalConfirmAction.open();
        },
        searchCicloAcademico(param) {
            let $vue = this;
            if (param == '')
                return;
            $vue.listCicloAcademico = [];
            const params = new URLSearchParams();
            params.append('nombre', param);
            axios.post("/comun/buscar/allCicloDescendent", params)
                    .then(function (response) {
                        if (response.data.success) {
                            $vue.listCicloAcademico = response.data.data;
                        }
                    })
                    .catch(function (error) {
                        notify(error.errorComunicacion, "error");

                    });
        },
        verEliminarGruposSecciones() {
            let $vue = this;
            $vue.configConfirmAction = VUE_MODAL.structConfirm({
                message: "¿Desea eliminar el/los grupos(s) sección(es)?",
                okbtn: "Si, Eliminar",
                okclass: "btn-warning",
                okaction: $vue.eliminarGrupos,
                okbtnprocessing: '<i class="fa fa-spinner fa-pulse fa-fw"></i> Ordenando...'
            });
            $vue.$refs.modalConfirmAction.open();
        },
        eliminarGrupos() {
            console.log("eliminar grupos");
            let $vue = this;
            console.dir($vue.gpoSeccionesSelects);

            axios.post(APP.url(rutaModulo + '/eliminarGrupos'), $vue.gpoSeccionesSelects)
                    .then(function (response) {
                        $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                        if (response.data.success) {
                            $vue.listCicloAcademico = response.data.data;
                            notify(response.data.message, "success");
                            $vue.gpoSeccionesSelects = [];
                            $vue.$refs.raptorGpoSecc.loadRemoteData();
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        notify(error.errorComunicacion, "error");
                    });
        },
        downloadAlumnosSeccion(item) {
            console.log(item);
            location.href = APP.url('docente/cargaacademica/reporteAlumno?seccion=') + item.id;
        },
        changeOrdenSeccion() {
            let $vue = this;
            if ($vue.direccionSeccion === 0) {
                $vue.direccionSeccion = 1;
            } else if ($vue.direccionSeccion === 1) {
                $vue.direccionSeccion = -1;
            } else if ($vue.direccionSeccion === -1) {
                $vue.direccionSeccion = 0;
            }
            console.log("$vue.ordenRegistros=" + $vue.ordenRegistros)
            $vue.ordenRegistros = $vue.removeOrdenador($vue.ordenRegistros, "seccion");
            let previo = $vue.ordenRegistros;
            if ($vue.direccionSeccion === 1) {
                $vue.ordenRegistros = "seccion.asc";
                $vue.ordenRegistros += (previo === "") ? "" : ("," + previo);
            } else if ($vue.direccionSeccion === -1) {
                $vue.ordenRegistros = "seccion.desc";
                $vue.ordenRegistros += (previo === "") ? "" : ("," + previo);
            }
            console.log("$vue.ordenRegistros=" + $vue.ordenRegistros)
            $vue.setStyleOrdenSeccion();
            $vue.loadRegistros();
        },
        setStyleOrdenSeccion() {
            let $vue = this;
            console.log("serwert-wertwert-wertwertwe")

            if ($vue.direccionSeccion === 1) {
                $vue.styleOrdenSeccion = "fa-chevron-circle-up text-primary";
            } else if ($vue.direccionSeccion === -1) {
                $vue.styleOrdenSeccion = "fa-chevron-circle-down text-primary";
            } else if ($vue.direccionSeccion === 0) {
                $vue.styleOrdenSeccion = "fa-stop-circle-o";
            }
        },
        changeOrdenCurso() {
            let $vue = this;
            if ($vue.direccionCurso === 0) {
                $vue.direccionCurso = 1;
            } else if ($vue.direccionCurso === 1) {
                $vue.direccionCurso = -1;
            } else if ($vue.direccionCurso === -1) {
                $vue.direccionCurso = 0;
            }
            console.log("$vue.ordenRegistros=" + $vue.ordenRegistros)
            $vue.ordenRegistros = $vue.removeOrdenador($vue.ordenRegistros, "curso");
            let previo = $vue.ordenRegistros;
            if ($vue.direccionCurso === 1) {
                $vue.ordenRegistros = "curso.asc";
                $vue.ordenRegistros += (previo === "") ? "" : ("," + previo);
            } else if ($vue.direccionCurso === -1) {
                $vue.ordenRegistros = "curso.desc";
                $vue.ordenRegistros += (previo === "") ? "" : ("," + previo);
            }
            console.log("$vue.ordenRegistros=" + $vue.ordenRegistros)
            $vue.setStyleOrdenCurso();
            $vue.loadRegistros();
        },
        setStyleOrdenCurso() {
            let $vue = this;
            if ($vue.direccionCurso === 1) {
                $vue.styleOrdenCurso = "fa-chevron-circle-up text-primary";
            } else if ($vue.direccionCurso === -1) {
                $vue.styleOrdenCurso = "fa-chevron-circle-down text-primary";
            } else if ($vue.direccionCurso === 0) {
                $vue.styleOrdenCurso = "fa-stop-circle-o";
            }
        },
        removeOrdenador(string, parte) {
            let ords = string.split(",");
            let idx = -100;
            for (var i = 0; i < ords.length; i++) {
                let existe = (ords[i].indexOf(parte) === 0);
                if (existe) {
                    idx = i;
                }
            }
            if (idx > -100) {
                ords.splice(idx, 1);
            }
            return ords.join(",");
        },
        getOrdenador(string, parte) {
            let ords = string.split(",");
            let ordenador = "";
            for (var i = 0; i < ords.length; i++) {
                let existe = (ords[i].indexOf(parte) === 0);
                if (existe) {
                    ordenador = ords[i];
                }
            }
            if (ordenador === "") {
                return 0;
            }

            let dir = ordenador.split(".")[1];
            if (dir === "asc")
                return 1;
            if (dir === "desc")
                return -1;
            return 0;
        },
        verEstado(tipo) {
            let $vue = this;
            if ($vue.estadoSeleccionada === '') {
                $vue.bgColorEstadoClass[tipo] = 'bg-light';
                $vue.estadoSeleccionada = tipo;
                $vue.loadRegistros();

            } else if ($vue.estadoSeleccionada !== '' && $vue.estadoSeleccionada !== tipo) {
                $vue.bgColorEstadoClass[$vue.estadoSeleccionada] = '';
                $vue.bgColorEstadoClass[tipo] = 'bg-light';
                $vue.estadoSeleccionada = tipo;
                $vue.loadRegistros();

            } else if ($vue.estadoSeleccionada !== '' && $vue.estadoSeleccionada === tipo) {
                $vue.bgColorEstadoClass[$vue.estadoSeleccionada] = '';
                $vue.estadoSeleccionada = '';
                $vue.loadRegistros();
            }
        },
        verDictado(tipo) {
            let $vue = this;
            if ($vue.dictadoSeleccionado === '') {
                $vue.bgColorDictadoClass[tipo] = 'bg-light';
                $vue.dictadoSeleccionado = tipo;
                $vue.loadRegistros();

            } else if ($vue.dictadoSeleccionado !== '' && $vue.dictadoSeleccionado !== tipo) {
                $vue.bgColorDictadoClass[$vue.dictadoSeleccionado] = '';
                $vue.bgColorDictadoClass[tipo] = 'bg-light';
                $vue.dictadoSeleccionado = tipo;
                $vue.loadRegistros();

            } else if ($vue.dictadoSeleccionado !== '' && $vue.dictadoSeleccionado === tipo) {
                $vue.bgColorDictadoClass[$vue.dictadoSeleccionado] = '';
                $vue.dictadoSeleccionado = '';
                $vue.loadRegistros();
            }
        }
    }
});

