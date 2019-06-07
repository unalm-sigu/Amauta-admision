Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#cursoDirigidoFacultadVUE',
    data: {
        cursoDirigidoFacURL: APP.url("configuracion/cursodirigidofacultad/list"),
        cursoDirigidoFacultad: null,
        facultad: null,
        listFacultad: JSON.parse(facultadesJson),
        isDisabled: false,
        listCurso: [],
        modalCursoDirigidoFAC: VUE_MODAL.structFormAjax({
            id: 'modalCursoDirigidoFAC',
            header: true,
            title: 'Elija el curso',
            okbtn: 'Agregar',
        })
    },
    mounted() {
        let $vue = this;

        $vue.listFacultad.length == 0 ? $vue.isDisabled = true : $vue.isDisabled = false;
        let facultad = $vue.$refs.loadCursoDirigidoFAC.getParameterByName('queries[facultad-dirigido]');
        if (facultad == null && $vue.listFacultad.length > 0) {
            $vue.facultad = $vue.listFacultad[0];
            facultad = $vue.facultad.id;
        }

        if (facultad != '' && $vue.listFacultad.length != 0) {
            $vue.setFacultadSelected(facultad);
            $vue.$refs.loadCursoDirigidoFAC.querie.push({name: 'facultad-dirigido', value: facultad});
            $vue.$refs.loadCursoDirigidoFAC.repreload();
        }
    },
    methods: {
        setFacultadSelected(idFacultad) {
            let $vue = this;
            for (var i = 0; i < $vue.listFacultad.length; i++) {
                if (idFacultad == $vue.listFacultad[i].id) {
                    $vue.facultad = $vue.listFacultad[i];
                }
            }
        },
        nuevoCursoDirigidoFAC() {
            let $vue = this;
            $vue.listCurso = [];
            $vue.cursoDirigidoFacultad = {
                curso: null,
                facultad: Object.assign({}, $vue.facultad)
            };
            $vue.$refs.modalCursoDirigidoFAC.open();
        },
        searchCurso(parametro) {
            let $vue = this;
            if (parametro == '')
                return;
            const params = new URLSearchParams();
            params.append('parametro', parametro);
            params.append('idFacultad', $vue.cursoDirigidoFacultad.facultad.id);
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
            $vue.$refs.loadCursoDirigidoFAC.querie.push({name: 'facultad-dirigido', value: $vue.facultad.id});
            $vue.$refs.loadCursoDirigidoFAC.repreload();
        },
        eliminar(item) {
            let $vue = this;

            bootbox.confirm({
                message: '¿Está seguro que desea remover el curso dirigido <b>' + item.curso.nombre + '</b> de la facultad <b>' + item.facultad.nombre + ' </b>?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        axios.post(rutaModulo + "/eliminar", item)
                                .then(function (response) {
                                    if (response.data.success) {
                                        notify(response.data.message, "success");
                                        $vue.changeFacultadSelected();
                                    } else {
                                        notify(response.data.message, 'error');
                                    }
                                })
                                .catch(function (error) {
                                    notify(error.errorComunicacion, "error");
                                });
                    }
                }
            });
        }
    }
});
