/* global APP, cursosCurriculas, rutaModulo, MESSAGES, axios */

Vue.component('multiselect', {mixins: [window.VueMultiselect.default]});
new Vue({
    el: '#alumnoCursosVUE',
    data: {
        urlCursos: APP.url(rutaModulo + `/allCursoCurriculaAlumno/${idAlumno}`),
        alumno: JSON.parse(alumnoJson),
        cursoCurricula: {alumno: {}, curso: {}},
        cursos: [],
        cursoCurriculaModal: VUE_MODAL.structFormAjax({
            id: 'cursoCurriculaModal',
            header: true,
            title: 'Nuevo Curso Curricula',
            okbtn: 'Registrar',
            modalsize: 'modal-lg',
            processing: false
        })
    },
    mounted: function () {
        let $vue = this;
        $vue.urlCursos = APP.url(rutaModulo + `/allCursoCurriculaAlumno/${$vue.alumno.id}`);
        $vue.$refs.cursosload.repreload();
    },
    methods: {
        nombreforShow(item) {
            if (item.id == null) {
                return;
            }
            return item.nombre + " - " + item.codigo;
        },
        searchCurso(nombre) {
            let $vue = this;
            if (nombre.trim().length === 0) {
                return;
            }
            axios.get(APP.url(rutaModulo + '/allCursoCiclo'), {params: {nombre: nombre}}).
                    then(response => {
                        if (response.data.success) {
                            $vue.cursos = response.data.data;
                        }
                    }).
                    catch(err => {
                        notify(Messages.errorComunicacion, "error");
                    });
        },
        opencursoCurrila() {
            let $vue = this;
            $vue.cursoCurricula = {alumno: {}, curso: {}};
            $vue.$refs.cursoCurriculaModal.open();
        },
        saveCursoCurriculaAlumno() {
            let $vue = this;
            var valid = $('#cursocurriculaForm').parsley().validate();
            if (!valid) {
                notify("Debe seleccionar un curso.", "warning")
                return;
            }
            $vue.cursoCurricula.alumno = $vue.alumno;
            $vue.$refs.cursoCurriculaModal.processing = true;
            axios.post(APP.url(rutaModulo + '/saveCursoCurricula'), $vue.cursoCurricula).
                    then(response => {
                        if (response.data.success) {
                            $vue.$refs.cursoCurriculaModal.processing = false;
                            $vue.$refs.cursoCurriculaModal.close();
                            $vue.urlCursos = APP.url(rutaModulo + `/allCursoCurriculaAlumno/${$vue.alumno.id}`);
                            $vue.$refs.cursosload.repreload();
                            notify(response.data.message, "success");
                        } else {
                            $vue.$refs.cursoCurriculaModal.processing = false;
                            notify(response.data.message, "warning");
                        }
                    }).
                    catch(err => {
                        $vue.$refs.cursoCurriculaModal.processing = false;
                        notify(Messages.errorComunicacion, "error");
                    });
        }
    }
});

