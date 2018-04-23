Vue.component("historial-component", {
    template: "#historialComponent",
    props: {
        alumno: {},
    },
    data: function() {
        return {
            cursos: [],
            alumnoCurso: [],
            listCiclos: [],
            typeSearch: false,
            typeSearch2: false,
            typeSearch3: false,
            general: true,
        }
    },
    computed: {
        titulo() {
            return 'Historial Académico';
        }
    },
    beforeMount() {
        this.cargaHistorial();
    },
    methods: {
        cargaHistorial() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + this.alumno.id + '/historial'),
                contentType: "application/json",
                success: function(response) {
                    if (response.success) {
                        $vue.alumnoCurso = response.data;
                        var i = 1;
                        $vue.alumnoCurso.forEach(function(element) {
                            var obj = {id: 1, value: element.descripción};
                            $vue.listCiclos.push(obj);
                            i++;
                        })
                    }
                }
            });
        },
        changeSearch() {
            let $vue = this;
            $vue.alumnoCurso = this.alumnoCurso;
            $vue.alumnoCursoTemp = this.alumnoCursoTemp;
        },
        changeSearch2() {
            let $vue = this;
            if (!$vue.typeSearch2) {
                $vue.general = true;
            } else {
                $.ajax({
                    method: 'GET',
                    url: APP.url('academico/alumno/' + this.alumno.id + '/listHistorial'),
                    contentType: "application/json",
                    success: function(response) {
                        $vue.cursos = response.data.cursos;
                        $vue.general = false;
                    }
                });
            }
        },
        changeSearch3() {
            let $vue = this;
            if (!$vue.typeSearch3) {
            } else {
            }
        },
        styleNota(nota) {
            if (nota < 11 || nota == 'DE') {
                return "text-danger";
            } else {
                return "text-primary";
            }
        },
        validarNota(curso, tipo) {
            if (!tipo) {
                return true;
            } else {
                if (curso.nota >= 11)
                    return true;
            }
        },
        calcularPromedio: function() {
            var vue = this;
            if (vue.alumno.id == null) {
                return;
            }
            bootbox.confirm({
                message: '¿Seguro que desea calcular el promedio?',
                buttons: {
                    confirm: {label: 'Si, Calcular', className: "btn-primary"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/alumno/calcularpromedio'),
                            data: {id: vue.alumno.id},
                            success: function(response) {
                                if (response.success) {
                                    location.reload();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function() {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
    }
});