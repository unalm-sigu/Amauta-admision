Vue.component("ciclo-component", {
    template: "#cicloTemplate",
    props: {
        alumnociclo: {cicloAcademico: {}},
        alumnociclocursos: [],
        alumnociclos: [],
        inx: null,
    },
    date: function() {
        return {alumnociclo: {cicloAcademico: {}}, alumnociclos: []}
    },
    mounted: function() {
        let vue = this;
        let self = $(vue.$el);
        self.find(".cicloSelect").
                select2(vue.selectCiclo(vue)).
                on('change.select2', function(eve) {
                    if (eve.added == undefined) {
                        vue.alumnociclo.cicloAcademico.id = null;
                        vue.alumnociclo.cicloAcademico.descripcion = null;
                    }
                });
    },
    updated: function() {
        let vue = this;
        vue.$nextTick(function() {
            let self = $(vue.$el);
            self.find(".cicloSelect").
                    select2(vue.selectCiclo(vue)).
                    on('change.select2', function(eve) {
                        if (eve.added == undefined) {
                            vue.alumnociclo.cicloAcademico.id = null;
                            vue.alumnociclo.cicloAcademicodescripcion = null;
                        }
                    });
        });
    },
    methods: {
        deleteAlumnoCiclo: function(alumnociclo) {
            let vue = this;
            let self = $(vue.$el);
            $global.$emit("deleteAlumnoCiclo", alumnociclo, self);
        },
        agregarAlumnoCicloCurso: function(alumnociclo) {
            $global.$emit("agregarAlumnoCicloCurso", alumnociclo);
        },
        selectCiclo: function(vm) {
            return {
                allowClear: true,
                placeholder: "Seleccione un ciclo",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("tramite/solicitudconstancia/updatehistorial/searchciclo"),
                    dataType: 'json',
                    type: 'post',
                    data: function(term, page) {
                        let cicloss = [0];
                        if (vm.alumnociclos.length > 0) {
                            vm.alumnociclos.map(function(v) {
                                if (v.cicloAcademico.id) {
                                    cicloss.push(v.cicloAcademico.id);
                                }
                            });
                        }
                        return {nombre: term, page: page, idCiclos: cicloss};
                    },
                    results: function(response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function(element, callback) {
                    if (vm.alumnociclo.cicloAcademico.id != null) {
                        callback(vm.alumnociclo.cicloAcademico);
                    }
                },
                formatResult: function(info) {
//                    var cursoSearch = new CursoSearch();
//                    cursoSearch.curso = info;
//                    var cmp = cursoSearch.$mount();
//                    return cmp.$el;
                    return info.descripcion;
                },
                formatSelection: function(info) {
                    vm.alumnociclo.cicloAcademico.id = info.id;
                    vm.alumnociclo.cicloAcademico.descripcion = info.descripcion;
//                    vm.alumnociclocurso.curso.id = info.id;
//                    vm.alumnociclocurso.curso.nombre = info.nombre;
//                    vm.alumnociclocurso.curso.codigo = info.codigo;
//                    vm.alumnociclocurso.curso.creditos = info.creditos;
//                    vm.alumnociclocurso.creditos = info.creditos;
                    return info.descripcion;
                },
                escapeMarkup: function(m) {
                    return m;
                }
            };
        },
    },
});

