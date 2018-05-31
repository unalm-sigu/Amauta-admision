var AlumnoItem = Vue.component("alumnoItem", {
    template: "#alumnoItem",
    data: function () {
        return {alumno: {}};
    }
});
Vue.component("alumnosearch", {
    template: "#alumnoTemp",
    data: function () {
        return {
            alumno: {
                id: null
            }
        }
    },
    props: {
        alumno: {id: null},
        nombre: {type: String, default: ''},
    },
    mounted: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2(vue.buscarAlumno(vue));
    },
    updated: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2(vue.buscarAlumno(vue));
    },
    methods: {
        buscarAlumno: function (vue) {
           return {
                allowClear: true,
                placeholder: "Seleccione un alumno de prueba",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("tramite/solicitudconstancia/updatehistorial/searchalumno"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (vue.alumno.id != null) {
                        callback(vue.alumno);
                    }
                },
                formatResult: function (info) {
                    var alumnoItem = new AlumnoItem();
                    alumnoItem.alumno = info;
                    var cmp = alumnoItem.$mount();
                    return cmp.$el;
                },
                formatSelection: function (info) {
                    vue.alumno = info;
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        }
    }
});