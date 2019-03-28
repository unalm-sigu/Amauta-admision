Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        URL: APP.url('rolexamen/cursosexcluidos'),
        rolExamenes: null,
        rolesExamenes: JSON.parse(jRolexamenes),
        curso: null,
        cursos: [],
        cursosExcluidos: []
    },
    mounted() {
        let $vue = this;
    },
    computed: {

    },
    methods: {
        excluirCurso() {
            var form = $("#frmExcluir");
            if (!form.parsley().validate()) {
                return;
            }
            let $vue = this;
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
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        }, loadCursosMasivosByRoleExamen() {

        }, loadCurso(nombre) {
            let $vue = this;

            $.ajax({
                method: "POST",
                url: APP.url("rolexamen/cursomasivos/" + $vue.rolExamenes.id + "/loadCurso"),
                data: {nombre: nombre}
            }).then(response => {
                if (response.success) {
                    $vue.cursos = response.data;
                    $vue.loadCursosExcluidosByRoleExamen();
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
