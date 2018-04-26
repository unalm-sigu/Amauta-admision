Vue.component("curso-component", {
    template: "#cursoTemplate",
    props: {
        curso: {},
        ciclo: {},
        inx: null,
        index: null
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
        })
    },
    methods: {
        selectCurso: function(vm) {
            return {
                allowClear: true,
                placeholder: "Seleccione un curso",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("academico/horariocachimbo/curso/searchcurso"),
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
                    if (element.val() != "") {
                        callback(vm.curso);
                    }
                },
                formatResult: function(info) {
                    return info.codigo + " - " + info.curso;
                },
                formatSelection: function(info) {
                    vm.curso.id = info.id;
                    vm.curso.curso = info.curso;
                    vm.curso.creditos = info.creditos;
                    vm.curso.nota = info.nota;
                    return info.codigo + " - " + info.curso;
                },
                escapeMarkup: function(m) {
                    return m;
                }
            };
        },
        deleteCurso: function(curso, ciclo) {
            let vue = this;
            let self = $(vue.$el);
            $global.$emit("deleteCurso", curso, ciclo, self);
        }
    },
});

