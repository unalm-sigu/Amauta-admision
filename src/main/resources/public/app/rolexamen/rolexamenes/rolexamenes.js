Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#rolexamenesVUE',
    data: {
        rolexamenesURL: APP.url('rolexamen/rolexamenes/list'),
        confirmarModal: {
            id: 'modalConfirmar',
            header: true,
            title: 'Configurar Nuevo Rol Examen',
            cancelbtn: 'Cancelar',
            okbtn: 'Guardar',
            modalsize: 'modal-md'
        },
        eventosCiclosAcademicos: []

    },
    mounted() {
        let $vue = this;
    },
    methods: {
        verNuevoRolExamen() {
            let $vue = this;

            $.ajax({
                url: APP.url("rolexamen/rolexamenes/allEventoCicloAcademico"),
                dataType: 'json',
                type: 'post',
            }).then(response => {
                if (response.success) {
                    $vue.eventosCiclosAcademicos = response.data;
                    $vue.$refs.modalConfirmar.open();
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        verGuardar() {
            let $vue = this;

        },
    }
});
