Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#detalleGrupoVUE',
    data: {
        grupo: JSON.parse(grupoAl),
        detalleGrupoURL: APP.url('mensajeria/gpoalumno/detalle/list'),
        gpoAlumno: {},
        detalleGrupo: {},
        modalDetalleGrupo: {
            id: 'modalDetalleGrupo',
            header: true,
            title: 'Crear Detale de Grupo',
            okbtn: 'Guardar',
            showaccept: true,
            modalsize: "modal-lg"
        },
        isLoading: false,
        modalidadEstudios: [],
        situacionAcademcas: [],
        facultades: [],
        carreras: [],
        cursos: [],
        grupoSecciones: [],
        secciones: []
    },
    computed: {

    },
    beforeMount: function () {
        let $vue = this;
        $vue.detalleGrupoURL = APP.url('mensajeria/gpoalumno/detalle/' + $vue.grupo.id + '/list');
    },
    mounted: function () {
        let $vue = this;
        $vue.allModalidad();
        $vue.allSituacion();
        $vue.allFacultad();
    },
    methods: {
        init() {
            let $vue = this;
            $vue.gpoAlumno = {};

        },
        nuevo() {
            let $vue = this;
            $vue.detalleGrupo = {};
            $vue.modalDetalleGrupo.okbtn = "Guardar";
            $vue.modalDetalleGrupo.title = "Nuevo Detalle";
            $vue.$refs.modalDetalleGrupo.open();
        },
        save(e) {
            let $vue = this;

            let form = $("#formGrupoDetalle");

            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            $vue.detalleGrupo.grupoAlumno = $vue.grupo;

            $.ajax({
                method: 'POST',
                url: APP.url('mensajeria/gpoalumno/detalle/save'),
                data: JSON.stringify($vue.detalleGrupo),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.modalDetalleGrupo.close();
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function (error) {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });

        },
        editar(item) {
            let $vue = this;
            $vue.detalleGrupo = item;
            console.log(item);
            if (item.curso != null && item.curso != undefined) {
                $vue.cursos.push(item.curso);
            }

            $vue.modalDetalleGrupo.okbtn = "Actualizar";
            $vue.modalDetalleGrupo.title = "Actualizar Detalle";
            $vue.$refs.modalDetalleGrupo.open();
        },
        eliminar: function (item) {
            let $vue = this;

            swal({
                title: "Eliminación del Registro",
                text: "¿Desea eliminar el detalle de grupo alumno?",
                icon: "warning",
                buttons: true,
                dangerMode: true,
            }).then((success) => {
                if (success) {
                    $.ajax({
                        method: 'POST',
                        url: APP.url('mensajeria/gpoalumno/detalle/eliminar'),
                        data: JSON.stringify(item),
                        contentType: "application/json",
                        success: function (response) {
                            if (response.success) {
                                $vue.$refs.load.loadRemoteData();
                                swal(response.message, {
                                    icon: "success",
                                });
                            } else {
                                notify(response.message, "error");
                            }
                        },
                        error: function (error) {
                            notify(GlobalMessages.errorComunicacion, "error");
                        }
                    });

                }
            });
        },
        searchCarrera(name) {
            this.isLoading = true
            $.ajax({
                url: APP.url("comun/buscar/allCarrera"),
                dataType: 'json',
                type: 'post',
                data: {nombre: name},
            }).then(response => {
                this.carreras = response.data;
                this.isLoading = false;
            }, error => {
                Logger.debug(error.responseText);
            });
        },
        searchCurso(codigo) {
            this.isLoading = true
            $.ajax({
                url: APP.url("comun/buscar/allCurso"),
                dataType: 'json',
                type: 'post',
                data: {codigo: codigo},
            }).then(response => {
                this.cursos = response.data;
                this.isLoading = false;
            }, error => {
                Logger.debug(error.responseText);
            });
        },
        searchGrupoSeccion(codigo) {
            let $vue = this;
            let curso = null;
            if ($vue.detalleGrupo.curso != null) {
                curso = this.detalleGrupo.curso.id;
            }
            this.isLoading = true
            $.ajax({
                url: APP.url("comun/buscar/allGrupoSeccion"),
                dataType: 'json',
                type: 'post',
                data: {codigo: codigo, curso: curso}
            }).then(response => {
                this.grupoSecciones = response.data;
                this.isLoading = false;
            }, error => {
                Logger.debug(error.responseText);
            });
        },
        allModalidad() {
            this.isLoading = true
            $.ajax({
                url: APP.url("comun/buscar/allModalidadEstudio"),
                dataType: 'json',
                type: 'post',
            }).then(response => {
                this.modalidadEstudios = response.data;
                this.isLoading = false;
            })
        },
        allSituacion() {
            this.isLoading = true
            $.ajax({
                url: APP.url("comun/buscar/allSituacionAcademica"),
                dataType: 'json',
                type: 'post',
            }).then(response => {
                this.situacionAcademcas = response.data;
                this.isLoading = false;
            })
        },
        allFacultad() {
            this.isLoading = true
            $.ajax({
                url: APP.url("comun/buscar/allFacultad"),
                dataType: 'json',
                type: 'post',
            }).then(response => {
                this.facultades = response.data;
                this.isLoading = false;
            })
        },
        cursoSelected() {
            let $vue = this;
            $vue.detalleGrupo.seccion = {};
            $vue.detalleGrupo.secciones = [];
            $vue.detalleGrupo.grupoSeccion = {};
            $vue.grupoSecciones = [];
        },
        searchSeccion(codigo) {
            let $vue = this;
            $vue.isLoading = true;
            $.ajax({
                url: APP.url("comun/buscar/allSeccion"),
                dataType: 'json',
                type: 'post',
                data: {codigo: codigo}
            }).then(response => {
                $vue.secciones = response.data;
                $vue.isLoading = false;
            }, error => {
                Logger.debug(error.responseText);
            });
        },
        grupoSeccionSelected(item) {
            let $vue = this;
            $vue.detalleGrupo.seccion = {};
            $vue.detalleGrupo.secciones = [];
        },
        grupoSeccionRemoved() {
            let $vue = this;
            $vue.detalleGrupo.seccion = {};
            $vue.detalleGrupo.secciones = [];
        },
        chkbMatrbls() {
            let $vue = this;
            let ckbMatrbls = $("#chkbMatrbls").is(':checked');
            if (ckbMatrbls) {
                $vue.detalleGrupo.matriculados = 1;
            } else {
                $vue.detalleGrupo.matriculados = 0;
            }
        },
        cursoInfo(n) {
            return n.codigo + " - " + n.nombre;
        },
        grupoSeccionInfo(n) {
            if (!jQuery.isEmptyObject(n)) {
                return n.codigo + " | " + n.curso.codigo + " | " + n.curso.nombre;
            }
            return "";
        },
        seccionInfo(n) {
            if (!jQuery.isEmptyObject(n)) {
                return n.codigo2 + " | " + n.grupoHoras.codigo + " | " + n.grupoSeccion.curso.codigo + " | " + n.grupoSeccion.curso.nombre;
            }
            return "";
        },
        situacionInfo(n) {
            if (!jQuery.isEmptyObject(n)) {
                return n.codigo + " - " + n.nombre;
            }
            return "";
        }
    }
});
