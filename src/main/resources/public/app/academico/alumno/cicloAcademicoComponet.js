Vue.component("ciclo-component", {
    template: "#cicloTemplate",
    props: {
        ciclo: {},
        alumno: {},
        cursos: [],
        inx: null,
    },
    date: function() {
        return {ciclo: {}, cursos: []}
    },
    mounted: function() {
        let vue = this;
        let self = $(vue.$el);
        self.find("select.cicloSelect").
                select2({minimumResultsForSearch: -1, allowClear: true}).
                on('change.select2', function(eve) {
                    if (eve.added == undefined) {
                        vue.ciclo.id = null;
                        vue.ciclo.descripcion = null;
                    } else {
                        vue.ciclo.id = eve.added.id;
                        vue.ciclo.descripcion = eve.added.text;
                    }
                });
    },
    updated: function() {
        let vue = this;
        vue.$nextTick(function() {
            let self = $(vue.$el);
            self.find("select.cicloSelect").
                    select2({minimumResultsForSearch: -1, allowClear: true}).
                    on('change.select2', function(eve) {
                        if (eve.added == undefined) {
                            vue.ciclo.id = null;
                            vue.ciclo.descripcion = null;
                        } else {
                            vue.ciclo.id = eve.added.id;
                            vue.ciclo.descripcion = eve.added.text;
                        }
                    });
        })
    },
    methods: {
        deleteCiclo: function(ciclo) {
            let vue = this;
            let self = $(vue.$el);
            self.find("select.cicloSelect").select2("destroy");
            $global.$emit("deleteCiclo", ciclo);
        },
        agregarCurso: function(ciclo) {
            $global.$emit("agregarCurso", ciclo);
        },
    },
});

