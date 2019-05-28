Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        URL: APP.url('rolexamen/cursosexcluidos'),
        rolExamenes: JSON.parse(jRolexamen),
        rolesExamenes: JSON.parse(jRolesxamenes),
        curso: null,
        cursos: [],
        seccion: null,
        secciones: [],
        cursosExcluidos: [],
        cursoExcluido: null,
        seccionesExcluidas: []
    },
    mounted() {
        let $vue = this;
        if (this.rolExamenes != null && this.rolExamenes.id != null && this.rolExamenes.id != "") {
            $vue.loadCursosExcluidosByRoleExamen();
        }
    }, computed: {
        existsRolExamenes() {
            if (this.rolExamenes == null || this.rolExamenes.id == null || this.rolExamenes.id == "") {
                return false;
            }
            return true;
        }
    },
    methods: {
        excluirCurso() {
//            var form = $("#frmExcluir");
//            if (!form.parsley().validate()) {
//                return;
//            }
            let $vue = this;
            if (!$vue.existsRolExamenes) {
                notify("Seleccione el rol examenes", 'error');
            }
            let cursoExcluido = {
                curso: $vue.curso,
                rolExamenes: $vue.rolExamenes
            };
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: `${$vue.URL}/excluirCurso`,
                data: JSON.stringify(cursoExcluido)
            }).then(response => {
                if (response.success) {
                    $vue.loadCursosExcluidosByRoleExamen();
                    $vue.curso = null;
                    notify(response.message, "info")
                } else {
                    notify(response.message, 'error');
                }
                MODAL.hideWait();
            });
        }, excluirSeccion() {
//            var form = $("#frmExcluir");
//            if (!form.parsley().validate()) {
//                return;
//            }
            let $vue = this;
            if (!this.existsRolExamenes) {
                notify("Seleccione la sección", 'error');
                return;
            }
            let seccionExcluido = {
                seccion: $vue.seccion,
                rolExamenes: $vue.rolExamenes
            };
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: `${$vue.URL}/excluirSeccion`,
                data: JSON.stringify(seccionExcluido)
            }).then(response => {
                if (response.success) {
                    $vue.loadCursosExcluidosByRoleExamen();
                    $vue.seccion = null;
                    notify(response.message, "info")
                } else {
                    notify(response.message, 'error');
                }
                MODAL.hideWait();
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        }, loadCursosMasivosByRoleExamen() {

        }, loadCurso(nombre) {
            let $vue = this;
            if (!this.existsRolExamenes) {
                notify("Seleccione el rol examenes", 'error');
                return;
            }
            $.ajax({
                method: "POST",
                url: APP.url("rolexamen/cursomasivos/" + $vue.rolExamenes.id + "/loadCurso"),
                data: {nombre: nombre}
            }).then(response => {
                if (response.success) {
                    $vue.cursos = response.data;
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        }, loadSecciones(nombre) {
            let $vue = this;
            if (!this.existsRolExamenes) {
                notify("Seleccione el rol examenes", 'error');
                return;
            }
            $.ajax({
                method: "POST",
                url: APP.url("rolexamen/cursosexcluidos/" + $vue.rolExamenes.id + "/loadSecciones"),
                data: {nombre: nombre}
            }).then(response => {
                if (response.success) {
                    $vue.secciones = response.data;
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        }, loadCursosExcluidosByRoleExamen() {
            let $vue = this;
            if ($vue.rolExamenes == null) {
                return;
            }
            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: `${$vue.URL}/listCursoExcluido`,
                data: JSON.stringify($vue.rolExamenes)
            }).then(response => {
                if (response.success) {
                    $vue.cursosExcluidos = response.data;
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        }, anular(cursoExcluido) {
            let $vue = this;

            bootbox.confirm({
                message: "Al anular la exclusión del curso, se anularán la exclusiones de las secciones, ¿desea continuar?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning btn-modal btn-procesar"},
                    cancel: {label: 'Cancelar', className: "btn-link btn-modal"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: "POST",
                            contentType: "application/json",
                            url: `${$vue.URL}/anularExcluision`,
                            data: JSON.stringify(cursoExcluido)
                        }).then(response => {
                            if (response.success) {
                                $vue.loadCursosExcluidosByRoleExamen();
                            } else {
                                notify(response.message, 'error');
                            }
                        }, error => {
                            notify(MESSAGES.errorComunicacion, 'error');
                        });
                    }
                }
            });

        }, abrirSeccionesExcluidas(cursoExcluido) {
            let vue = this;
            vue.cursoExcluido = Object.assign({}, cursoExcluido);
            this.loadSeccionesExcluidas(cursoExcluido);
            vue.$refs.seccionesExcluidasModal.title = 'Exclusión de Secciones';
            vue.$refs.seccionesExcluidasModal.open();
        }, loadSeccionesExcluidas() {
            let vue = this;
            axios.post(`${this.URL}/loadModalSeccionesExcluidas`, vue.cursoExcluido)
                    .then(response => {
                        if (response.data.success) {
                            vue.seccionesExcluidas = response.data.data;
                        }
                    });
        }, anularExclusion(seccionExcluido) {
            let vue = this;
            axios.post(`${this.URL}/anularExclusion`, seccionExcluido)
                    .then(response => {
                        if (response.data.success) {
                            vue.loadSeccionesExcluidas();
                            vue.loadCursosExcluidosByRoleExamen();
                            vue.cursoExcluido = response.data.data.cursoExcluido;
                            notify(response.data.message, "info");
                        } else {
                            notify(response.data.message, "error");
                        }
                    });
        }, activarExclusion(seccionExcluido) {
            let vue = this;
            axios.post(`${this.URL}/activarExclusion`, seccionExcluido)
                    .then(response => {
                        if (response.data.success) {
                            vue.loadSeccionesExcluidas();
                            vue.loadCursosExcluidosByRoleExamen();
                            vue.cursoExcluido = response.data.data.cursoExcluido;
                            notify(response.data.message, "info");
                        } else {
                            notify(response.data.message, "error");
                        }
                    });
        }
    }
});
