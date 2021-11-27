Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#main',
    mixins: [VueLoader],
    data: {
        docenteSession: {id: parseInt(sessiondocente)},
        raptorurl: APP.url('docente/ampliacionvacante/list'),
        dataModalAmpliacionVacante: {
            id: 'idModalAmpliacionVacante',
            header: true,
            title: 'Ampliación Vacante',
            okbtn: 'Matricular',
            modalsize: "modal-lg",
            showaccept: true
        },
        seccion: {grupoHoras: {}, aula: {}, seccionSuperior: {grupoHoras: {}, aula: {}}},
        alumnos: [],
        alumnoeleccionado: {},
        alumnoeleccionados: [],
        isLoadingAlumnos: false,
        solicitudesMatriculaSec: []
    },
    created: function () {
        let $vue = this;
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        changeVacante(seccion,grupoSeccion) {
            let $vue = this;
    
            if(!grupoSeccion.anexoBoletin.anexoSuperior.codigo){
                swal({
                    title: " ",
                    text: "No se puede determinar el anexo boletín",
                    icon: "warning",
                    button: {
                        text: "Aceptar",
                    },
                });
                return;
            }
            
            if(!(grupoSeccion.anexoBoletin.anexoSuperior.codigo=='G01'||
                    grupoSeccion.anexoBoletin.anexoSuperior.codigo=='G02'||
                    grupoSeccion.anexoBoletin.anexoSuperior.codigo=='G03')){
                 swal({
                    title: " ",
                    text: "Solo se permite ampliación vacante para cursos de pregrado",
                    icon: "warning",
                    button: {
                        text: "Aceptar",
                    },
                });
                return;
            }
    
            if (!seccion.docentePrincipaTcurLogeado) {
                var nombre=seccion.seccionSuperior.docentePrincipal.persona.emailCompania;
                swal({
                    title: " ",
                    text: "Solo el docente principal del curso de teoría puede ampliar vacantes "+nombre,
                    icon: "warning",
                    button: {
                        text: "Aceptar",
                    },
                });
                return;
            }

            $vue.$refs.modalAmpliacionVacante.open();
            $vue.seccion = seccion;
            $vue.alumnoeleccionado = {};
            $vue.alumnoeleccionados = [];
        },
        saveModalAmpliacionVacante() {
            let $vue = this;
            if ($vue.alumnoeleccionados.length < 1) {
                notify("Tiene que seleccionar un alumno", "error");
                return;
            }

            $vue.showLoader();

            $vue.formm = {};
            $vue.formm.alumnos = $vue.alumnoeleccionados;
            $vue.formm.seccion = $vue.seccion;

            $.ajax({
                url: APP.url('docente/ampliacionvacante/matricular'),
                type: 'POST',
                dataType: 'json',
                contentType: "application/json",
                data: JSON.stringify($vue.formm),
                success(response) {
                    if (response.success) {
                        $vue.$refs.raptor.repreload();
                        $vue.$refs.modalAmpliacionVacante.close();
                        notify("Registro guardado", "info");
                    } else {
                        notify(response.message, "error");
                    }
                    $vue.hideLoader();
                },
                error() {
                    $vue.hideLoader();
                    notify(Messages.errorComunicacion, "error");
                }
            });

        },
        agregarAlumno() {
            let $vue = this;
            if ($vue.alumnoeleccionado.id == null) {
                notify("Seleccione un alumno", "error");
                return;
            }
            let idstu = $vue.alumnoeleccionados.map(function (v, i) {
                return v.id;
            });
            let inx = idstu.indexOf($vue.alumnoeleccionado.id);
            if (inx >= 0) {
                notify("El Alumno ya se encuentra en la lista", "error");
                $vue.alumnoeleccionado = {};
                return;
            }
            if ($vue.alumnoeleccionado.situacion == '0') {
                notify("El Alumno no es matriculable", "error");
                return;
            }
            $vue.alumnoeleccionados.push($vue.alumnoeleccionado);
            $vue.alumnoeleccionado = {};
        },
        labelAlumno(item) {
            if (item.id == undefined) {
                return "";
            }
            return item.codigo + " - " + item.persona.nombreCompleto;
        },
        searchAlumnos(search) {
            let $vue = this;
            $vue.isLoadingAlumnos = true;
            $.ajax({
                url: APP.url('docente/ampliacionvacante/allAlumno'),
                type: 'POST',
                data: {nombre: search, id: $vue.seccion.id},
                success(response) {
                    $vue.isLoadingAlumnos = false;
                    if (response.success) {
                        $vue.alumnos = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        eliminarAlumno(alumno) {
            let $vue = this;
            $vue.alumnoeleccionados.splice($vue.alumnoeleccionados.indexOf(alumno), 1);
        },
    }
});


