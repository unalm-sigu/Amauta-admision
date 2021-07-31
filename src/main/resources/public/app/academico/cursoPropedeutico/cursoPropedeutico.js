Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#main',
    mixins: [VueLoader],
    data: {
        alumnoCurosURL: APP.url("academico/cursoPropedeutico/list"),
        modalAlumnoCurso: {
            id: 'modalAlumnoCurso',
            header: true,
            title: 'Agregar Curso Propedeutico ',
            okbtn: "Guardar",
            showaccept: true
        },
        matriculaResumen: null,
        matriculaResumenes: [],
        secciones: [],
        cursoPropedeuticoBean: {matriculaResumens: []}

    },
    mounted: function () {

    },
    methods: {
        openModal(item) {
            let $vue = this;
            if (item == null) {
                $vue.modalAlumnoCurso.title = 'Agregar Curso Propedeutico';
                $vue.modalAlumnoCurso.okbtn = 'Guardar';
                $vue.$refs.modalAlumnoCurso.open();
            } else {
                $vue.modalAlumnoCurso.title = 'Actualizar Curso Propedeutico';
                $vue.modalAlumnoCurso.okbtn = 'Actualizar ';
                $vue.$refs.modalAlumnoCurso.open();
            }
        },
        customLabel(item) {
            if (item.grupoSeccion == null) {
                return;
            }
            return item.codigo2 + " - [ " + item.grupoSeccion.curso.codigo + " - " + item.grupoSeccion.curso.nombre + " ]";
        },
        loadAlumno(nombre) {
            let $vue = this;
            if (nombre == '') {
                return;
            }
            $.ajax({
                dataType: 'json',
                type: 'post',
                url: APP.url("academico/cursoPropedeutico/findMatriculaResumen"),
                data: {nombre: nombre}
            }).then(response => {
                console.dir(response)
                if (response.success) {
                    $vue.matriculaResumenes = response.data;
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(Messages.errorComunicacion, 'error');
            });
        },
        customLabelAlumno(item) {
            if (item.alumno == null) {
                return;
            }
            return item.alumno.codigo + " - " + item.alumno.persona.apellidosNombres;
        },
        loadSeccion(nombre) {
            let $vue = this;
            if (nombre == '') {
                return;
            }
            $.ajax({
                dataType: 'json',
                type: 'post',
                url: APP.url("academico/cursoPropedeutico/findSeccion"),
                data: {nombre: nombre}
            }).then(response => {
                console.dir(response)
                if (response.success) {
                    $vue.secciones = response.data;
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(Messages.errorComunicacion, 'error');
            });
        },
        selectAlumno(item) {
            let $vue = this;
            $vue.cursoPropedeuticoBean.matriculaResumens.push(item);
        },
        removeAlumno(idx) {
            let $vue = this;
            console.log(idx);
            $vue.cursoPropedeuticoBean.matriculaResumens.splice(idx, 1);
        },
        saveAlumnoCurso() {
            let $vue = this;
            console.log($vue.cursoPropedeuticoBean);

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("academico/cursoPropedeutico/save"),
                data: JSON.stringify($vue.cursoPropedeuticoBean)
            }).then(response => {
                console.dir(response)
                if (response.success) {
                    $vue.$refs.load.loadRemoteData();
                    $vue.$refs.modalAlumnoCurso.close();
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(Messages.errorComunicacion, 'error');
            });
        },
        removerDeuda(alumnoCursoPropedeutico) {
            let $vue = this;
            bootbox.confirm({
                size: "small",
                message: "Seguro que desea remover su deuda de curso propedeútico",
                buttons: {
                    confirm: {label: 'Eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $vue.showLoader();
                        axios.get(APP.url('academico/cursoPropedeutico/eliminardeuda/'+alumnoCursoPropedeutico.id)).
                                then(response => {
                                    if(response.data.success){
                                        notify(response.data.message,'info');
                                    }else{
                                        notify(response.data.message, "error");
                                    }
                                    $vue.hideLoader();
                                }, error => {
                                    $vue.hideLoader();
                                    notify(Messages.errorComunicacion, "error");
                                });
                    }
                }
            });
        }
    }
})