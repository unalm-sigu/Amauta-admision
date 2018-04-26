new Vue({
    el: '#main',
    data: {
        alumno: JSON.parse(alumnoJson),
        guardando: false,
        ciclos: [{id: null, cursos: [{}]}]
    },
    created() {
        let vue = this;
    },
    mounted: function() {
        let vue = this;
        $('.numeric').numeric();
        $global.$on("agregarCurso", function(ciclo) {
            vue.agregarCurso(ciclo);
        });
        $global.$on("deleteCiclo", function(ciclo) {
            vue.deleteCiclo(ciclo);
        });
        $global.$on("deleteCurso", function(curso, ciclo, self) {
            vue.deleteCurso(curso, ciclo, self);
        });
    },
    methods: {
        deleteCiclo: function(ciclo) {
            let vue = this;
            if (vue.ciclos.length < 2) {
                notify("Debe haber un ciclo como mínimo", 'error');
                return;
            }
            vue.$delete(vue.ciclos, vue.ciclos.indexOf(ciclo));
        },
        deleteCurso: function(curso, ciclo, self) {
            let vue = this;
            if (ciclo.cursos.length < 2) {
                notify("Debe haber un curso como mínimo", 'error');
                return;
            }
            self.find(".selectCurso").select2("destroy");
            vue.$delete(ciclo.cursos, ciclo.cursos.indexOf(curso));
        },
        agregarCurso: function(ciclo) {
            let vue = this;
            ciclo.cursos.push({});
        },
        agregarCiclo: function() {
            let vue = this;
            vue.ciclos.push({id: null, cursos: [{}]});
        },
        guardarInformacion: function() {
            var vue = this;
            vue.guardando = true;
            var valid = $('#form').parsley().validate();
            if (valid != true) {
                vue.guardando = false;
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/alumno/updateinfoacademica'),
                data: $('#form').serialize(),
                success: function(response) {
                    if (response.success) {
                    } else {
                        notify(response.message, 'error');
                    }
                    vue.guardando = false;
                }, error: function() {
                    vue.guardando = false;
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
    }
});
