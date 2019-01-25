Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#consejeriaVUE',
    data: {
        consjerosURL: APP.url('consejeria/consejero/listConsjeros'),
        añadirConsejeroModal: {

            id: 'añadirConsejeroModal',
            header: 'true',
            title: "Añadir Consejeros",
            okbtn: 'Agregar',
            showaccept: true
        },
        ciclo: JSON.parse(cicloJson),
        carreras: JSON.parse(carrerasJson),
        btndisabled: '',
        listadoDocentes: [],
        listadoCarreras: [],
        nombreCarreraSelect: '',
        objeto_docenteSelect: '',
        departamentoAcademicoNombre: '',
        docente_resquest: {
            id: '',
            estado: '',
            id_persona: '',
            id_depart: '',
            id_facultad: '',
        },
        pagination: {'total-items': 0, 'items-per-page': 100, 'max-size': 3, 'boundary-link-numbers': true},
        isLoading: false,
    },
    computed: {
        btnAñadir() {
            let $vue = this;
            return $vue.btndisabled;
        }
    },
    mounted: function () {
        let $vue = this;

    },
    created: function () {
        let $vue = this;
        $vue.btndisabled = true;
    },
    methods: {
        nombreforShow(item) {
            return item.persona.nombreCompleto;
        },
        newConsejero() {
            let $vue = this;
            if ($vue.btndisabled === false) {
                $vue.openModal();
            }
        },
        openModal() {
            let $vue = this;
            $vue.$refs.añadirConsejeroModal.open();
        },
        getCarrera(nombreCar) {
            this.isLoading = true
            $.ajax({
                url: APP.url("consejeria/consejero/listCarrera"),
                data: {nombre: nombreCar},
                dataType: 'json',
                type: 'post',
            }).then(response => {
                this.listadoCarreras = response.data;
                this.isLoading = false;
            })
        },
        getDocentes(nombreDoc) {
            /// listado de docente por carrera
            let $vue = this;
            let idfacultad = $vue.nombreCarreraSelect.facultad.id;
            let idCarrera = $vue.nombreCarreraSelect.id;
            this.isLoading = true
            $.ajax({
                url: APP.url("consejeria/consejero/list"),
                data: {nombre: nombreDoc, idFacultad: idfacultad},
                dataType: 'json',
                type: 'post',
            }).then(response => {
                this.listadoDocentes = response.data;
                this.isLoading = false;
            });
        },
        CargaDepartamento() {
            let $vue = this;
            if ($vue.objeto_docenteSelect === null) {
                $vue.departamentoAcademicoNombre = "";
                $vue.docente_resquest = null;
            } else {
                $vue.departamentoAcademicoNombre = $vue.objeto_docenteSelect.departamentoAcademico.nombre;
                $vue.docente_resquest.estado = $vue.objeto_docenteSelect.estado;
                $vue.docente_resquest.id_depart = $vue.objeto_docenteSelect.departamentoAcademico.id;
                $vue.docente_resquest.id_facultad = $vue.objeto_docenteSelect.departamentoAcademico.facultad.id;
                $vue.docente_resquest.id_persona = $vue.objeto_docenteSelect.persona.id;
            }

        },
        cargaConsejeros() {
            let $vue = this;
            $vue.$refs.load.querie = [];
            this.listadoDocentes = '';
            this.objeto_docenteSelect = '';
            this.departamentoAcademicoNombre = '';
            if ($vue.nombreCarreraSelect === null) {
                $vue.btndisabled = true;
                //   $vue.$refs.load.loadRemoteData();
            } else {
                let nombreCarrera = $vue.nombreCarreraSelect.nombre;
                $vue.$refs.load.querie.push({name: 'car.nombre', value: nombreCarrera});
                $vue.btndisabled = false;
                $vue.$refs.load.loadRemoteData();
            }
        },
        cambiarEstado(item, estado) {
            let $vue = this;
            let idConsejero = item.id;
            this.isLoading = true
            $.ajax({
                url: APP.url("consejeria/consejero/cambiarEstado"),
                data: {idConsejero: idConsejero, estado: estado},
                dataType: 'json',
                type: 'post',
            }).then(response => {
                this.isLoading = false;
                notify(response.message, 'info');
                $vue.$refs.load.loadRemoteData();
            })

        },
        saveConsejero() {
            let $vue = this;
            console.log($vue.nombreCarreraSelect);
            /// let res = $vue.docente_responsive = id_docente_tets;
            // alert(JSON.stringify($vue.docente_resquest));
            bootbox.confirm({
                message: '¿Seguro que desea añadir como Consejero el docente seleccionado?',
                buttons: {
                    confirm: {label: 'Si, Añadir', className: "btn-success"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {

                    if (result) {

                        $.ajax({
                            method: 'POST',
                            url: APP.url("consejeria/consejero/saveConsejero"),
                            data: JSON.stringify({
                                estado: $vue.docente_resquest.estado,
                                persona: {id: $vue.docente_resquest.id_persona
                                },
                                departamentoAcademico: {
                                    id: $vue.docente_resquest.id_depart,
                                    facultad: {
                                        id: $vue.docente_resquest.id_facultad
                                    }
                                },
                                carrera: $vue.nombreCarreraSelect
                            }),
                            contentType: "application/json",
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    $vue.$refs.añadirConsejeroModal.close();
                                    $vue.objeto_docenteSelect = '';
                                    $vue.departamentoAcademicoNombre = '';
                                    $vue.$refs.load.loadRemoteData();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        asignarAlummnos() {


        },
        editRecorrido(item) {
        }
    }
});







        