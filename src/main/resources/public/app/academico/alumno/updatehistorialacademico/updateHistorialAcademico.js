new Vue({
    el: '#main',
    data: {
        alumno: JSON.parse(alumnoJson),
        guardando: false,
        alumnociclo: {cicloAcademico: {}},
        alumnociclos: [{cicloAcademico: {}}]
    },
    mounted: function() {
        let vue = this;
        $('.numeric').numeric();
        $global.$on("agregarAlumnoCicloCurso", function(alumnociclo) {
            vue.agregarAlumnoCicloCurso(alumnociclo);
        });
        $global.$on("deleteAlumnoCiclo", function(alumnociclo) {
            vue.deleteAlumnoCiclo(alumnociclo);
        });
        $global.$on("deleteAlumnoCicloCurso", function(alumnociclocurso, alumnociclo, self) {
            vue.deleteAlumnoCicloCurso(alumnociclocurso, alumnociclo, self);
        });
        vue.notas();
    },
    methods: {
        deleteAlumnoCiclo: function(alumnoCiclo) {
            let vue = this;
            if (vue.alumnosCiclo.length < 2) {
                notify("Debe haber un ciclo como mínimo", 'error');
                return;
            }
            vue.$delete(vue.alumnosCiclo, vue.alumnosCiclo.indexOf(alumnoCiclo));
        },
        deleteAlumnoCicloCurso: function(alumnoCicloCurso, alumnociclo, self) {
            let vue = this;
            if (alumnociclo.alumnosCicloCurso.length < 2) {
                notify("Debe haber un curso como mínimo", 'error');
                return;
            }
            self.find(".selectCurso").select2("destroy");
            vue.$delete(alumnociclo.alumnosCicloCurso, alumnociclo.alumnosCicloCurso.indexOf(alumnoCicloCurso));
        },
        agregarAlumnoCicloCurso: function(alumnociclo) {
            let vue = this;
            alumnociclo.alumnosCicloCurso.push({});
        },
        agregarAlumnoCiclo: function() {
            let vue = this;
            vue.alumnosCiclo.push({id: null, cicloAcademico: {}, alumnosCicloCurso: [{curso: {}}]});
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
                url: APP.url('academico/alumno/updatehistorial/update'),
                data: $('#form').serialize(),
                success: function(response) {
                    if (response.success) {
                        location.href = APP.url("academico/alumno/updatehistorial");
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
        notas: function() {
            let vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/alumno/updatehistorial/notas'),
                data: {id: vue.alumno.id},
                success: function(response) {
                    if (response.success) {
                        vue.alumnociclos = response.data;
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
    }
});
