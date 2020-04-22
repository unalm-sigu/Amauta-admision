Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#topematriculaVUE',
    data: {

        matriculaBD: [],
        tipoalumnos: [],
        matricula: [],
        verForm: false,
        guardando: false
    },
    mounted() {
        let $vue = this;
        $(".numerico").numeric({negative: false});
        $vue.loadTipoAlumnos();
        $vue.loadMatriculas();
    },
    methods: {
        loadTipoAlumnos() {
            let $vue = this;

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("academico/topematricula/allTipoAlumnos")
            }).then(response => {
                console.dir(response)
                if (response.success) {
                    $vue.tipoalumnos = response.data;
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(Messages.errorComunicacion, 'error');
            });
        },
        loadMatriculas() {
            let $vue = this;

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("academico/topematricula/list")
            }).then(response => {
                $vue.matriculaBD = response.data;

                $vue.verForm = false;
                $vue.matricula = [];
                //console.log($vue.tipoalumnos.length)
                for (var i = 0; i < $vue.tipoalumnos.length; i++) {
                    let mat = $vue.getAlumnos($vue.tipoalumnos[i], $vue.matriculaBD);
                    if (mat == null) {
                        //console.dir($vue.tipoalumnos[i].length);
                        $vue.matricula.push({tipoAlumno: $vue.tipoalumnos[i].name, tipoAlumnoEnum: $vue.tipoalumnos[i], creditos: ""});
                    } else {
                        $vue.matricula.push(mat);
                    }
                }
            }, error => {
                notify(Messages.errorComunicacion, 'error');
            });
        },
        getAlumnos(al, matricula) {
            for (var i = 0; i < matricula.length; i++) {
                if (al.name == matricula[i].tipoAlumno) {
                    return matricula[i];
                }
            }
            return null;
        },
        verGuardar() {
            var form = $("#formMatriculados");
            if (!form.parsley().validate()) {
                return;
            }

            let $vue = this;
            this.guardando = true;
            bootbox.confirm({
                message: '¿Está seguro que desea guarda esta matrícula?',
                buttons: {
                    confirm: {label: 'Si, guardar', className: 'btn-success'},
                    cancel: {label: 'No', className: 'btn-link'}
                },
                callback: function (aceptar) {
                    if (aceptar) {
                        $vue.guardar();
                    } else {
                        $vue.guardando = false;
                    }
                }
            });
        },
        guardar() {
            this.guardando = true;
            let $vue = this;

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("academico/topematricula/save"),
                dataType: "json",
                data: JSON.stringify($vue.matricula)
            }).then(response => {
                if (response.success) {
                    $vue.guardando = false;
                    $vue.verForm = false;
                    notify(response.message, "info");
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                $vue.guardando = false;
                notify(Messages.errorComunicacion, 'error');
            });
        }
    }

});


