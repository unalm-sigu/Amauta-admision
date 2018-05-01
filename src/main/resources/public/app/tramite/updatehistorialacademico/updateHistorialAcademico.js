new Vue({
    el: '#main',
    data: {
        alumno: JSON.parse(alumnoJson),
        guardando: false,
        alumnociclo: {cicloAcademico: {id: null}},
        alumnociclos: [{id: null, cicloAcademico: {id: null}, alumnociclocursos: [{curso: {id: null}}]}]
    },
    mounted() {
        let vue = this;
        $('.numeric').numeric();
        $global.$on("agregarAlumnoCicloCurso", function(alumnociclo) {
            vue.agregarAlumnoCicloCurso(alumnociclo);
        });
        $global.$on("deleteAlumnoCiclo", function(alumnociclo, self) {
            vue.deleteAlumnoCiclo(alumnociclo, self);
        });
        $global.$on("deleteAlumnoCicloCurso", function(alumnociclocurso, alumnociclo, self) {
            vue.deleteAlumnoCicloCurso(alumnociclocurso, alumnociclo, self);
        });
        vue.notas();
    },
    methods: {
        deleteAlumnoCiclo: function(alumnociclo, self) {
            let vue = this;
            if (vue.alumnociclos.length < 2) {
                notify("Debe haber un ciclo como mínimo", 'error');
                return;
            }

            bootbox.confirm({
                message: '¿Seguro que desea eliminar el ciclo académico?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {

                        self.find("select.cicloSelect").select2("destroy");
                        vue.$delete(vue.alumnociclos, vue.alumnociclos.indexOf(alumnociclo));

                    }
                }
            });

        },
        deleteAlumnoCicloCurso: function(alumnociclocurso, alumnociclo, self) {
            let vue = this;
            if (alumnociclo.alumnociclocursos.length < 2) {
                notify("Debe haber un curso como mínimo", 'error');
                return;
            }

            bootbox.confirm({
                message: '¿Seguro que desea eliminar el curso ?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {

                        self.find(".selectCurso").select2("destroy");
                        vue.$delete(alumnociclo.alumnociclocursos, alumnociclo.alumnociclocursos.indexOf(alumnociclocurso));

                    }
                }
            });

        },
        agregarAlumnoCicloCurso: function(alumnociclo) {
            let vue = this;
            alumnociclo.alumnociclocursos.push({curso: {id: null}});
        },
        agregarAlumnoCiclo: function() {
            let vue = this;
            vue.alumnociclos.push({id: null, cicloAcademico: {id: null}, alumnociclocursos: [{curso: {id: null}}]});
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
                url: APP.url('tramite/solicitudconstancia/updatehistorial/updatehistorial'),
                data: $('#form').serialize(),
                success: function(response) {
                    if (response.success) {
                        location.href = APP.url("academico/alumno");
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
                url: APP.url('tramite/solicitudconstancia/updatehistorial/notas'),
                data: {id: vue.alumno.id},
                success: function(response) {
                    if (response.success) {
                        if (response.total > 0) {
                            vue.alumnociclos = response.data;
                        }
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
