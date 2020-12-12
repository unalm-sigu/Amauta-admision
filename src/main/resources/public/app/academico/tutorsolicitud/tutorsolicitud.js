//Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#tutorSolicitudVUE',
    data: {
        tutorSolicitudURL: APP.url(`academico/tutorsolicitud/list`),
//        motivos: JSON.parse(motivosJson),
//        modalOmisoEleccion: {
//            id: 'modalOmisoEleccion',
//            header: true,
//            title: 'Agregar Deuda ',
//            okbtn: "Guardar",
//            showaccept: true
//        },
    },
    mounted: function () {

    },
    computed: {

    },
    methods: {
        style(item) {
            var colorEstado = {PEN: 'warning', ACEP: 'success', NACEP: 'danger'};
            var res = colorEstado[item];

            return "label label-" + res;
        },
        estadoValue(item) {
            var estadoEnum = {PEN: 'Pendiente', ACEP: 'Aceptado', NACEP: 'No Procede'};
            return estadoEnum[item];
        },
        updateEstado(item, estado) {
            let $vue = this;

            bootbox.confirm({
                message: "Esta seguro de dar el beneficio de ultimo ciclo?",
                buttons: {
                    confirm: {
                        label: 'Si',
                        className: 'btn-success'
                    },
                    cancel: {
                        label: 'No',
                        className: 'btn-danger'
                    }
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url("academico/tutorsolicitud/updateEstado"),
                            data: {id: item.id, estado: estado},
                        }).then(response => {
                            notify(response.message, 'info');
                            $vue.$refs.load.loadRemoteData();
                        });
                    }
                }
            });

        }
    }
});