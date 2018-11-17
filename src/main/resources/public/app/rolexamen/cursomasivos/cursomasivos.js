Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        cursomasivosURL: APP.url('rolexamen/cursomasivos/list'),
        configAddAulasModal: {
            id: 'modalConfirmar',
            header: true,
            title: 'Asignar Aulas',
            cancelbtn: 'Cancelar',
            okbtn: 'Asignar',
            modalsize: 'modal-lg'
        },
        rolExamenes: null,
        rolesExamenes: JSON.parse(jRolexamenes),
        curso: null,
        cursos: [],
        cursosMasivosByRolExamenes: [],
        modulo: {},
        modulos: [],
        cursoMasivoExamen: [],
        aulasModulo: [],
        aulas: [],
        aula: null
    },
    mounted() {
        let $vue = this;
        $vue.loadModulos();
    },
    methods: {
        loadCurso(nombre) {
            let $vue = this;

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
        },
        loadModulos() {
            let $vue = this;
            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("rolexamen/cursomasivos/allModulos")
            }).then(response => {
                if (response.success) {
                    $vue.modulos = response.data;
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        allAulasModulo(nombre) {
            let $vue = this;
            console.log(nombre);
            console.log("Buscar Módulos");
            $.ajax({
                url: APP.url("rolexamen/cursomasivos/allAulasModulo"),
                data: JSON.stringify(nombre),
                dataType: 'json',
                contentType: "application/json",
                type: 'POST',
                success: function (response) {
                    if (response.success) {
                        console.log(response.data);
                        $vue.aulasModulo = response.data;
                    }
                }
            });
        },
        agregarCursoMasivo() {
            let $vue = this;
            let cursoMasivo = {
                curso: $vue.curso,
                rolExamenes: $vue.rolExamenes
            }
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("rolexamen/cursomasivos/save"),
                data: JSON.stringify(cursoMasivo)
            }).then(response => {
                if (response.success) {
                    $vue.loadCursosMasivosByRoleExamen();
                    $vue.curso = null;
                    notify(response.message, "info")
                } else {
                    notify(response.message, 'error');
                }
                MODAL.hideWait();
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        loadCursosMasivosByRoleExamen() {
            let $vue = this;

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("rolexamen/cursomasivos/list"),
                data: JSON.stringify($vue.rolExamenes)
            }).then(response => {
                if (response.success) {
                    $vue.cursosMasivosByRolExamenes = response.data;
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        verAsignarAulas(item) {
            let $vue = this;
            $vue.modulo = {};
            $vue.aulasModulo = [];
            $vue.cursoMasivoExamen = jQuery.extend(true, {}, item);
            $vue.aulas = $vue.cursoMasivoExamen.aulasCursosMasivos;
            $vue.$refs.addAulasModal.open();
        },
        saveAulas() {
            var form = $("#formAulas");
            if (!form.parsley().validate()) {
                return;
            }

            let $vue = this;
            bootbox.confirm({
                message: '¿Está seguro que desea asignar estas aulas?',
                buttons: {
                    confirm: {label: 'Si, guardar', className: 'btn-success'},
                    cancel: {label: 'No', className: 'btn-link'}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            method: "POST",
                            contentType: "application/json",
                            url: APP.url("rolexamen/cursomasivos/saveAulas"),
                            data: JSON.stringify($vue.cursoMasivoExamen)
                        }).then(response => {
                            if (response.success) {
                                $vue.$refs.addAulasModal.close();
                                $vue.loadCursosMasivosByRoleExamen();
                                $vue.curso = null;
                                notify(response.message, "info")
                            } else {
                                notify(response.message, 'error');
                            }
                            MODAL.hideWait();
                        }, error => {
                            notify(MESSAGES.errorComunicacion, 'error');
                        });
                    }
                }
            });
        },
        eliminar(item) {
            let $vue = this;
            var del = item;
            bootbox.confirm({
                message: '¿Está seguro que desea eliminar el registro curso <b>' + item.curso.nombre + '</b>?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: 'btn-danger'},
                    cancel: {label: 'Cancelar', className: 'btn-link'}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('rolexamen/cursomasivos/eliminar'),
                            type: 'POST',
                            async: true,
                            data: {id: del.id},
                            success: function (response) {
                                if (response.success) {
                                    $vue.loadCursosMasivosByRoleExamen();
                                    $vue.curso = null;
                                    notify(response.message, "info");
                                } else {
                                    notify(response.message, "error");
                                }
                                MODAL.hideWait();
                            },
                            error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        noExiste(aula) {
            let $vue = this;
            for (var i = 0; i < $vue.aulas.length; i++) {
                if (aula.id == $vue.aulas[i].aula.id) {
                    return false;
                }
            }
            return true;
        },
        addAula(aula) {
            let $vue = this;
            $vue.aulas.push({id: '', aula: aula});
            $vue.cursoMasivoExamen.aulas = $vue.cursoMasivoExamen.aulas + 1;
            $vue.cursoMasivoExamen.capacidadAulas = $vue.cursoMasivoExamen.capacidadAulas + aula.capacidadAula;
        },
        removeAula(aula, idx) {
            let $vue = this;
            if (aula.id == '') {
                $vue.aulas.splice(idx, 1);
                $vue.cursoMasivoExamen.aulas = $vue.cursoMasivoExamen.aulas - 1;
                $vue.cursoMasivoExamen.capacidadAulas = $vue.cursoMasivoExamen.capacidadAulas - aula.aula.capacidadAula;

            } else {
                bootbox.confirm({
                    message: '¿Está seguro que desea eliminar esta aula?',
                    buttons: {
                        confirm: {label: 'Si, eliminar', className: 'btn-danger'},
                        cancel: {label: 'No', className: 'btn-link'}
                    },
                    callback: function (result) {
                        if (result) {
                            $vue.aulas.splice(idx, 1);
                            $vue.cursoMasivoExamen.aulas = $vue.cursoMasivoExamen.aulas - 1;
                            $vue.cursoMasivoExamen.capacidadAulas = $vue.cursoMasivoExamen.capacidadAulas - aula.aula.capacidadAula;
                        }
                    }
                });
            }

        },
        styleBgColor(aula) {
            if (aula.id == '') {
                return "background-color: #FFC300;";
            }
            return "background-color: #21B021;";
        }

    }
});
