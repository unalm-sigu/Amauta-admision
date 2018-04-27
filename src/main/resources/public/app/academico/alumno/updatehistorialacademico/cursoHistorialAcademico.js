Vue.component("curso-component", {
    template: "#cursoTemplate",
    props: {
        alumnociclocurso: {curso: {}},
        alumnociclo: {},
        inx: null,
        index: null
    },
    date: function() {
        return {alumnociclocurso: {curso: {}}}
    },
    mounted: function() {
        let vue = this;
        let self = $(vue.$el);
        self.find(".selectCurso").select2(vue.selectCurso(vue));
        $('.numeric').numeric();
    },
    updated: function() {
        let vue = this;
        this.$nextTick(function() {
            let self = $(vue.$el);
            self.find(".selectCurso").select2(vue.selectCurso(vue));
        });
    },
    methods: {
        selectCurso: function(vm) {
            return {
                allowClear: true,
                placeholder: "Seleccione un curso",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("academico/alumno/updatehistorial/searchcurso"),
                    dataType: 'json',
                    type: 'post',
                    data: function(term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function(response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function(element, callback) {
                    if (vm.alumnociclocurso.curso.id != null) {
                        callback(vm.alumnociclocurso.curso);
                    }
                },
                formatResult: function(info) {
                    console.log(info.creditos);
                    return info.codigo + " - " + info.nombre;
                },
                formatSelection: function(info) {
                    vm.alumnociclocurso.curso.id = info.id;
                    vm.alumnociclocurso.curso.nombre = info.nombre;
                    vm.alumnociclocurso.curso.codigo = info.codigo;
                    vm.alumnociclocurso.curso.creditos = info.creditos;
                    vm.alumnociclocurso.creditos = info.creditos;
                    return info.codigo + " - " + info.nombre;
                },
                escapeMarkup: function(m) {
                    return m;
                }
            };
        },
        deleteAlumnoCicloCurso: function(alumnociclocurso, alumnociclo) {
            let vue = this;
            let self = $(vue.$el);
            $global.$emit("deleteAlumnoCicloCurso", alumnociclocurso, alumnociclo, self);
        }
    },
});

