Vue.component("ciclo-component", {
    template: "#cicloTemplate",
    props: {
        alumnociclo: {cicloAcademico: {}},
        alumnosCicloCurso: [],
        inx: null,
    },
    date: function() {
        return {alumnociclo: {cicloAcademico: {}}}
    },
    mounted: function() {
        let vue = this;
        let self = $(vue.$el);
        self.find("select.cicloSelect").
                select2({minimumResultsForSearch: -1, allowClear: true}).
                on('change.select2', function(eve) {
                    if (eve.added == undefined) {
                        vue.alumnociclo.cicloAcademico.id = null;
                        vue.alumnociclo.cicloAcademico.descripcion = null;
                    } else {
                        vue.alumnociclo.cicloAcademico.id = eve.added.id;
                        vue.alumnociclo.cicloAcademico.descripcion = eve.added.text;
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
                            vue.alumnociclo.cicloAcademico.id = null;
                            vue.alumnociclo.cicloAcademicodescripcion = null;
                        } else {
                            vue.alumnociclo.cicloAcademico.id = eve.added.id;
                            vue.alumnociclo.cicloAcademico.descripcion = eve.added.text;
                        }
                    });
        });
    },
    methods: {
        deleteAlumnoCiclo: function(alumnociclo) {
            let vue = this;
            let self = $(vue.$el);
            self.find("select.cicloSelect").select2("destroy");
            $global.$emit("deleteAlumnoCiclo", alumnociclo);
        },
        agregarAlumnoCicloCurso: function(alumnociclo) {
            $global.$emit("agregarAlumnoCicloCurso", alumnociclo);
        },
    },
});

