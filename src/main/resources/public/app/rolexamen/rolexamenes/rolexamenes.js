Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#rolexamenesVUE',
    data: {
        rolexamenesURL: APP.url('rolexamen/rolexamenes/list'),
        confirmarModal: {
            id: 'modalConfirmar',
            header: true,
            title: 'Crear Nuevo Rol Examen',
            cancelbtn: 'Cancelar',
            okbtn: 'Guardar',
            modalsize: 'modal-md'
        },
        eventosCiclos: JSON.parse(jEventosCiclosAcademicos),
        rolExamenes: {
            eventoCicloAcademico: {},
            semanasExamen: []
        },
        horas: JSON.parse(jHoras)
    },
    mounted() {
        let $vue = this;
    },
    methods: {
        eventoAcademicoCustomLabel( { eventoAcademico }) {
            if (eventoAcademico == null) {
                return "";
            }
            return `${eventoAcademico.nombre}`;
        },
        verNuevoRolExamen() {
            let $vue = this;

            this.rolExamenes = {
                eventoCicloAcademico: {},
                semanasExamen: []
            };
            this.confirmarModal.title = 'Crear Nuevo Rol Examen';
            this.$refs.modalConfirmar.open();

            /*
             $.ajax({
             url: APP.url("rolexamen/rolexamenes/allEventoCicloAcademico"),
             dataType: 'json',
             type: 'post',
             }).then(response => {
             if (response.success) {
             $vue.eventosCiclos = response.data;
             console.log("Estoy dentro del ajax");
             console.log($vue.eventosCiclos);
             $vue.confirmarModal.title = 'Crear Nuevo Rol Examen';
             $vue.$refs.modalConfirmar.open();
             } else {
             notify(response.message, 'error');
             }
             }, error => {
             notify(MESSAGES.errorComunicacion, 'error');
             });*/
        },
        editarRolExamen(rolExamen) {
            let $vue = this;
            console.log("editarRolExamen");
            console.dir(rolExamen);
            $.ajax({
                url: APP.url("rolexamen/rolexamenes/loadRolExamenesInfo"),
                dataType: 'json',
                contentType: "application/json",
                data: JSON.stringify(rolExamen),
                type: 'post',
            }).then(response => {
                if (response.success) {
                    $vue.rolExamenes = response.data;
                    $vue.confirmarModal.title = 'Editar Rol Examen';
                    $vue.$refs.modalConfirmar.open();
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        guardarRol() {
            let $vue = this;

            if ($('#formEvento').parsley().validate() !== true) {
                return;
            }

            $.ajax({
                url: APP.url('rolexamen/rolexamenes/save'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: true,
                data: JSON.stringify($vue.rolExamenes),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.raptorRolExamenes.loadRemoteData();
                        $vue.$refs.modalConfirmar.close();
                        notify(response.message, 'info');

                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        }, changeEventoCicloAcademico() {

            AXIOS.post(APP.url('rolexamen/rolexamenes/changeEventoCicloAcademico'), this.rolExamenes.eventoCicloAcademico)
                    .then(response => {
                        if (response.data.success) {
                            this.rolExamenes.semanasExamen = response.data.data;
                        }
                    });
        }, redireccionarWithRol(ruta, rolExamen) {
            console.log(APP.url(ruta) + "/" + rolExamen.id);
            location.href = APP.url(ruta) + "/" + rolExamen.id;
        }
    }
});
