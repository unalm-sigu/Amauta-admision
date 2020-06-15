Vue.component('vue-procesos', {
    template: '#vue-procesos',
    data: {
        modalProcesos: {
            id: 'modalProcesos',
            styleModal: {'background-color': '#D8D8D8'},
            dataBackdrop: 'static',
            dataKeyboard: 'false',
            header: false,
            footer: false
        }
    },
    props: {
        modalProcesos: {},
        messageAvance: {type: String, default: ""},
        porcentajeAvance: {type: Number, default: 0},
    },
    mounted() {
        let vue = this;
        $global.$on('MODAL-PROCESS-OPEN', () => {
            vue.$refs.modalProcesos.open();
        });
        $global.$on('MODAL-PROCESS-CLOSE', () => {
            vue.$refs.modalProcesos.close();
        });
    },
    methods: {
        repreload() {
            this.search = this.getParameterByName('queries[search]');
            this.search = (this.search == null) ? '' : this.search;
            if (this.getParameterByName('page')) {
                this.page.currentPage = parseInt(this.getParameterByName('page'));
            } else {
                this.loadRemoteData();
            }
        }
    }
});