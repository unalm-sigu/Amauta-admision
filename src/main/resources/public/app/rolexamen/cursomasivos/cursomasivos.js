Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#cursomasivosVUE',
    data: {
        cursomasivosURL: APP.url('rolexamen/cursomasivos/list'),
        confirmarModal: {
            id: 'modalConfirmar',
            header: true,
            title: 'Configurar Nuevo Curso Masivo',
            cancelbtn: 'Cancelar',
            okbtn: 'Guardar',
            modalsize: 'modal-md'
        },
        rolExamenes: null, //rol examen seleccionado
        rolesExamenes: JSON.parse(jRolexamenes), //lista
        curso: null,
        cursos: [],
        cursosMasivosByRolExamenes: []
    },
    mounted() {
        let $vue = this;
        //loadRolExamenes();
        //loadCursoMasivo();

    },
    methods: {
        loadCurso(nombre) {
            let $vue = this;
//            console.log("Estoy los Rol Exámenes");

            $.ajax({
                method: "POST",
                //contentType: "application/json",
                url: APP.url("rolexamen/cursomasivos/" + $vue.rolExamenes.id + "/loadCurso"),
                data: {nombre: nombre}

            }).then(response => {
                if (response.success) {
                    console.dir(response.data);
                    $vue.cursos = response.data;
//                    console.dir($vue.cursosMasivosByRolExamenes);
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        loadCursoMasivo() {

        },
        agregarCursoMasivo() {
            let $vue = this;
            let cursoMasivo = {
                curso: $vue.curso,
                rolExamenes: $vue.rolExamenes
            }
            console.dir(cursoMasivo);
            console.log("Estoy antes del ajax");

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("rolexamen/cursomasivos/save"),
                data: JSON.stringify(cursoMasivo)
            }).then(response => {
                if (response.success) {
                    console.dir(response);
                    notify(response.message, "info")
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });



        },
        loadCursosMasivosByRoleExamen() {
            let $vue = this;
            console.log("Estoy en cargar Cursos Masivos por Rol Examens");
            console.dir(this.rolExamenes);
            console.log("Esto paso siguiente");

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("rolexamen/cursomasivos/list"),
                data: JSON.stringify($vue.rolExamenes)
            }).then(response => {
                if (response.success) {
                    console.dir(response.data);
                    $vue.cursosMasivosByRolExamenes = response.data;
                    console.dir($vue.cursosMasivosByRolExamenes);
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        verNuevoCurso() {
        },
        listCursosMasivos() {},
        loadModalSecciones() {},
        loadModalAlumnos() {},
        loadModalAulas() {},

    }
});
