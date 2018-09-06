new Vue({
    el: '#main',
    data: {
        silaboURL: APP.url('academico/silabo/list'),
        silaboCurso: {},
        modalSilabo: {
            id: 'modalSilabo',
            modalsize: 'modal-md',
            header: false
        }
    },
    mounted: function () {
        let vue = this;

    },
    methods: {
        save() {
            console.log("Saved =) ");
        },
        cancelSave() {

        },
        openModalSilabo() {
            let vue = this;
            vue.$refs.modalSilabo.open();
        }
    }
});
