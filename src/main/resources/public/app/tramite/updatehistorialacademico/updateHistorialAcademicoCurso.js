var CursoSearch = Vue.component("cursoSearch", {
    template: "#itemCursoTemplate",
    data: function() {
        return {curso: {}};
    }
});
Vue.component("curso-component", {
    template: "#cursoTemplate",
    props: {
        alumnociclocurso: {curso: {}},
        alumnociclo: {},
        inx: null,
        index: null
    },
    date: function() {
        return {alumnociclocurso: {curso: {}}, alumnociclo: {}}
    },
    mounted: function() {
        let vue = this;
        let self = $(vue.$el);
        self.find(".selectCurso").select2(vue.selectCurso(vue)).on('change.select2', function(eve) {
            if (eve.added == undefined) {
                vue.alumnociclocurso.curso = {id: null};
            }
        });
        self.find('.numeric').numeric();
        self.find('.upperCase').upperCase();
    },
    updated: function() {
        let vue = this;
        this.$nextTick(function() {
            let self = $(vue.$el);
            self.find(".selectCurso").select2(vue.selectCurso(vue)).on('change.select2', function(eve) {
                if (eve.added == undefined) {
                    vue.alumnociclocurso.curso = {id: null};
                }
            });
            self.find('.numeric').numeric();
            self.find('.upperCase').upperCase();
        });
    },
    methods: {
        selectCurso: function(vm) {
            return {
                allowClear: true,
                placeholder: "Seleccione un curso",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("tramite/solicitudconstancia/updatehistorial/searchcurso"),
                    dataType: 'json',
                    type: 'post',
                    data: function(term, page) {
                        let cursoss = [0];
                        if (vm.alumnociclo.alumnociclocursos.length > 0) {
                            vm.alumnociclo.alumnociclocursos.map(function(v) {
                                if (v.curso.id) {
                                    cursoss.push(v.curso.id);
                                }
                            });
                        }
                        return {nombre: term, page: page, idCursos: cursoss};
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
                    var cursoSearch = new CursoSearch();
                    cursoSearch.curso = info;
                    var cmp = cursoSearch.$mount();
                    return cmp.$el;
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

