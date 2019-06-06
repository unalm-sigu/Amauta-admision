Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#cursoDirigidoFacultadVUE',
    data: {
        cursoDirigidoFacURL: APP.url(rutaModulo + '/list'),
        cursoDirigidoFacultad: {curso: {}, facultad: {}},
        listFacultad: JSON.parse(facultadesJson),
        isDisabled: "false",
        listCurso: [],
        modalCursoDirigidoFAC: VUE_MODAL.structFormAjax({
            id: 'modalCursoDirigidoFAC',
            header: true,
            title: 'Elija el curso dirigido',
            okbtn: 'Agregar',
        })
    },
    mounted() {
        let $vue = this;
        $vue.cursoDirigidoFacultad.facultad = $vue.listFacultad[0];
        $vue.listFacultad.length == 0 ? $vue.isDisabled = true : $vue.isDisabled = false;

        console.log(JSON.stringify($vue.cursoDirigidoFacultad.facultad));
        $vue.changeFacultadSelected();
    },
    methods: {
        nuevoCursoDirigidoFAC() {
            let $vue = this;
            $vue.listCurso = [];
            $vue.cursoDirigidoFacultad.curso = {};
            $vue.$refs.modalCursoDirigidoFAC.open();
        },
        searchCurso(parametro) {
            let $vue = this;
            if (parametro == '')
                return;
            const params = new URLSearchParams();
            params.append('parametro', parametro);
            axios.post(rutaModulo + "/allLikeCurso", params)
                    .then(function (response) {
                        if (response.data.success) {
                            $vue.listCurso = response.data.data;
                        }
                    })
                    .catch(function (error) {
                        notify(error.errorComunicacion, "error");
                    });
        },
        save() {
            let $vue = this;
            if ($vue.cursoDirigidoFacultad.curso == undefined) {
                notify("Debe seleccionar el curso para ser agregado a la facultad", "error");
                return;
            }

            console.log(JSON.stringify($vue.cursoDirigidoFacultad));
            axios.post(rutaModulo + "/save", $vue.cursoDirigidoFacultad)
                    .then(function (response) {
                        if (response.data.success) {
                            notify(response.data.message, "success");
                            $vue.$refs.modalCursoDirigidoFAC.close();
                            $vue.changeFacultadSelected();
                        } else {
                            notify(response.data.message, 'error');
                        }
                    })
                    .catch(function (error) {
                        notify(error.errorComunicacion, "error");
                    });
        },
        changeFacultadSelected() {
            let $vue = this;
            $vue.$refs.loadCursoDirigidoFAC.url = APP.url("docente/cursodirigidofacultad/list/" + $vue.cursoDirigidoFacultad.facultad.id);
            $vue.$refs.loadCursoDirigidoFAC.loadRemoteData();
        },
        convertFecha(datetime) {
            return datetime.substr(0, 10);
        }

//        deleteMiembro(miembroItem, escuelaCarreraItem) {
//            let $vue = this;
//
//            var apellidosNombres = miembroItem.docente.persona.apellidosNombres;
//            var tipo = escuelaCarreraItem.carrera.tipoEnum.value;
//            var carrera = escuelaCarreraItem.carrera.nombre;
//            var con = tipo == "Maestría" ? 'de la' : 'de';
//
//            bootbox.confirm({
//                message: '¿Está seguro que desea remover a <b>' + apellidosNombres + '</b> del comité de evaluación ' + con + ' <b>' + tipo + ' de ' + carrera + '</b>?',
//                buttons: {
//                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
//                    cancel: {label: 'Cancelar', className: "btn-link"}
//                },
//                callback: function (result) {
//                    if (result) {
//                        axios.post('/' + rutaModulo + "/deleteMiembro", miembroItem)
//                                .then(function (response) {
//                                    if (response.data.success) {
//                                        $vue.$refs.loadComite.loadRemoteData();
//                                        notify(response.data.message, "success");
//                                    } else {
//                                        notify(response.data.message, 'error');
//                                    }
//                                })
//                                .catch(function (error) {
//                                    notify(error.errorComunicacion, "error");
//                                });
//                    }
//                }
//            });
//        }
    }
});
