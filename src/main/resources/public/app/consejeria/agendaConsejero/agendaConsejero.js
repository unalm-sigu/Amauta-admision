Vue.component("multiselect", window.VueMultiselect.default)
Vue.component('date-picker', VueBootstrapDatetimePicker);
new Vue({
    el: '#agendaConsejeroVUE',
    data: {
        ciclo: JSON.parse(cicloJson),
        horas: JSON.parse(jHora),
        consejeros: JSON.parse(jConsejeros),
        agendaConsejeroURL: APP.url(rutaModulo + '/list'),
        consejeroSelect: null,
        agendaModal: {
            id: 'agendaModal',
            header: true,
            title: "Agenda Consejero",
            okbtn: 'Guardar',
            modalsize: "modal-lg",
            showaccept: true
        },
        agendaConsejero: {},
        alumnosConsejeros: [],
        alumnosConsejerosTemp: [],
        configDate: {
            format: "DD/MM/YYYY",
            useCurrent: false
        }
    },
    mounted: function () {
        let $vue = this;
        $vue.consejeroSelect = $vue.consejeros[0];
        if ($vue.consejeroSelect.id != undefined) {
            $vue.loadAgendasURL();
            $vue.cargaAconsejados($vue.consejeroSelect);
        }
    },
    methods: {
        customLabel(item) {
            if (item.carrera.id == null) {
                return;
            }
            return item.carrera.nombre;
        },
        loadAgendasURL() {
            let $vue = this;
            $vue.$refs.load.url = APP.url(rutaModulo + '/list/' + $vue.consejeroSelect.carrera.id);
            $vue.$refs.load.loadRemoteData();
        },
        asistio(item) {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + "/asistenciaReunion"),
                contentType: "application/json",
                data: JSON.stringify(item),
                type: 'post',
            }).then(response => {
                if (response.success) {
                    $vue.$refs.load.loadRemoteData();
                    notify(response.message, "success");
                }
            });
        },
        noAsistio(item) {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + "/inasistenciaReunion"),
                contentType: "application/json",
                data: JSON.stringify(item),
                type: 'post',
            }).then(response => {
                if (response.success) {
                    $vue.$refs.load.loadRemoteData();
                    notify(response.message, "success");
                }
            });
        },
        anular(item) {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + "/anularReunion"),
                contentType: "application/json",
                data: JSON.stringify(item),
                type: 'post',
            }).then(response => {
                if (response.success) {
                    $vue.$refs.load.loadRemoteData();
                    notify(response.message, "success");
                }
            });
        },
        styleColor(item) {
            switch (item.name) {
                case "AGEN":
                    return "label label-primary";
                case "ANU" :
                    return "label label-danger";
                case "ASIS" :
                    return "label label-success";
                case "NASIS" :
                    return "label label-warning";
            }
        },
        openModal() {
            let $vue = this;
            $vue.init();
            $vue.$refs.agendaModal.title = 'Agenda Consejero';
            $vue.$refs.agendaModal.okbtn = 'Guardar';
            $vue.$refs.agendaModal.open();
        },
        openUpdateModal(item) {
            let $vue = this;
            $vue.init();
            $vue.obtenerInfo(item);
            $vue.$refs.agendaModal.title = 'Actualizar Agenda';
            $vue.$refs.agendaModal.okbtn = 'Actualizar';
            $vue.$refs.agendaModal.open();
        },
        obtenerInfo(item) {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + "/findAgenda/" + item.id),
                contentType: "application/json",
                type: 'get'
            }).then(response => {
                if (response.success) {
                    $vue.agendaConsejero = response.data;
                    $vue.alumnosConsejerosTemp = response.data.alumnoConsejeros;
                }
            });
        },
        save() {
            let $vue = this;
            var reunionAlumnoConsejeros = [];
            for (var i = 0; i < $vue.alumnosConsejerosTemp.length; i++) {
                if ($vue.alumnosConsejerosTemp[i].seleccionado) {
                    var data = {alumnoConsejero: $vue.alumnosConsejerosTemp[i]};
                    reunionAlumnoConsejeros.push(data);
                }
            }
            $vue.agendaConsejero.reunionAlumnoConsejeros = reunionAlumnoConsejeros;
            $vue.agendaConsejero.consejero = $vue.consejeroSelect;

            $.ajax({
                url: APP.url(rutaModulo + "/save"),
                contentType: "application/json",
                data: JSON.stringify($vue.agendaConsejero),
                type: 'post',
            }).then(response => {
                if (response.success) {
                    $vue.$refs.load.loadRemoteData();
                    $vue.$refs.agendaModal.close();
                    $vue.init();
                    notify(response.message, "success");
                } else {
                    $vue.$refs.agendaModal.close();
                    $vue.init();
                    notify(response.message, "error");
                }
            });
        },
        update() {
            let $vue = this;
            var reunionAlumnoConsejeros = [];
            for (var i = 0; i < $vue.alumnosConsejerosTemp.length; i++) {
                if ($vue.alumnosConsejerosTemp[i].seleccionado) {
                    var data = {alumnoConsejero: $vue.alumnosConsejerosTemp[i]};
                    reunionAlumnoConsejeros.push(data);
                }
            }
            $vue.agendaConsejero.reunionAlumnoConsejeros = reunionAlumnoConsejeros;

            $.ajax({
                url: APP.url(rutaModulo + "/update"),
                contentType: "application/json",
                data: JSON.stringify($vue.agendaConsejero),
                type: 'post',
            }).then(response => {
                if (response.success) {
                    $vue.$refs.load.loadRemoteData();
                    $vue.$refs.agendaModal.close();
                    $vue.init();
                    notify(response.message, "success");
                } else {
                    $vue.$refs.agendaModal.close();
                    $vue.init();
                    notify(response.message, "error");
                }
            });
        },
        cargaAconsejados(item) {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + "/listAlumnos/" + item.carrera.id),
                contentType: "application/json",
                type: 'get'
            }).then(response => {
                if (response.success) {
                    $vue.alumnosConsejeros = JSON.parse(JSON.stringify(response.data));
                    $vue.alumnosConsejerosTemp = JSON.parse(JSON.stringify(response.data));
                }
            });
        },
        init() {
            let $vue = this;
            $vue.agendaConsejero = {};
            $vue.alumnosConsejerosTemp = JSON.parse(JSON.stringify($vue.alumnosConsejeros));
        }
    }
});







        