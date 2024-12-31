Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#restriccionVUE',
    data: {
        deudasURL: APP.url('oficinas/matricula/restriccionmatricula/list'),
        oficinas: JSON.parse(oficinasJson),
        modalEdit: {
            id: 'modalEdit',
            title: 'Actualizar',
            header: true,
            okbtn: "Actualizar",
            showaccept: true
        },
        modalAnular: {
            id: 'modalAnular',
            header: true,
            title: 'Anular',
            okbtn: "Anular",
            showaccept: true
        },
        modalNuevo: {
            id: 'modalNuevo',
            header: true,
            title: 'Nuevo',
            okbtn: "Guardar",
            showaccept: true
        },
        oficina: null,
        temp: {},
        alumnos: []
    },
    mounted: function () {
        $(".numeric").numeric({negative: false});
    },
    computed: {

    },
    methods: {
        guardar() {

        },
        loadAlumno(nombre) {
            let $vue = this;
            this.isLoading = true

            if (nombre != '' || nombre != null || nombre != undefined) {

                $.ajax({
                    url: APP.url("academico/matriculable/allAlumnoByNombre"),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre}
                }).then(response => {
                    if (response.success) {
                        $vue.alumnos = response.data;
                    }

                    this.isLoading = false;
                })

            }
        },
        customLabel( {persona, codigo}){
            if (persona != null) {
                return  'Matrícula: '+ codigo + " - " + persona.nombreCompleto;
            }
            return "";
        },
        findbyOficina(item, idx) {
            let $vue = this;
            if (idx == 1) {
                $vue.$refs.load.querie.push({name: 'oficina', value: item.id});
                $vue.$refs.load.repreload();
            } else {
                $vue.$refs.load.querie = [];
                $vue.$refs.load.loadRemoteData();
            }
        },
        modal(item, idx) {
            let $vue = this;
            $vue.temp = Object.assign({}, item);
            if (idx == 1) {
                $vue.$refs.modalEdit.open();
            } else if (idx == 2) {
                $vue.$refs.modalAnular.open();
            } else {
                $vue.$refs.modalNuevo.open();
            }
        },
        editar() {
            let $vue = this;
            var form = $("#formEdit");
            if (!form.parsley().validate()) {
                return;
            }
            $.ajax({
                url: APP.url('oficinas/matricula/restriccionmatricula/guardar'),
                type: 'POST',
                contentType: "application/json",
                data: JSON.stringify($vue.temp),
                success: function (response) {
                    if (response.success) {
                        MODAL.hide();
                        notify(response.message, "info");
                        $vue.$refs.load.loadRemoteData();

                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
            $vue.$refs.modalEdit.close();
        },
        levantar(item) {
            let $vue = this;

            bootbox.confirm({
                message: "¿Está seguro que desea levantar la restricción?",
                buttons: {
                    confirm: {label: "Sí, seguro", className: "btn-info"},
                    cancel: {label: "No", className: "btn-link"}
                },
                callback: function (result) {
                    if (!result) {
                        return;
                    }
                    $.ajax({
                        url: APP.url('oficinas/matricula/restriccionmatricula/levantar'),
                        type: 'POST',
                        async: true,
                        data: {id: item.id},
                        success: function (response) {
                            if (response.success) {
                                notify(response.message, "info");
                                $vue.$refs.load.loadRemoteData();
                            } else {
                                notify(response.message, "error");
                            }
                        },
                        error: function () {
                            notify(Messages.errorComunicacion, "error");
                        }
                    });
                }
            });
        }, 
        anular() {
            let $vue = this;
            var form = $("#formAnular");
            if (!form.parsley().validate()) {
                return;
            }
            $.ajax({
                url: APP.url('oficinas/matricula/restriccionmatricula/anular'),
                type: 'POST',
                contentType: "application/json",
                data: JSON.stringify($vue.temp),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.load.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
            $vue.$refs.modalAnular.close();
        },
        getClass(item) {
            switch (item) {
                case "REST":
                    return "label-danger"
                    break;
                case "LEV":
                    return "label-success"
                    break;
                case "ANU":
                    return "label-primary"
                    break;
                case "POST":
                    return "label-warning"
                    break;
            }
        },
        save() {
            let $vue = this;
            var form = $("#formNuevo");
            if (!form.parsley().validate()) {
                return;
            }
            $.ajax({
                url: APP.url('oficinas/matricula/restriccionmatricula/save'),
                type: 'POST',
                contentType: "application/json",
                data: JSON.stringify($vue.temp),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.load.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
            $vue.$refs.modalNuevo.close();
        }
    }
});