Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#vueReporte', data: {
        carrera: '',
        carreras: JSON.parse(carrerasJson),
        modalCarrera: VUE_MODAL.structFormAjax({
            id: 'modalCarrera', header: true, title: 'Lista de Carreras', okbtn: 'Generar Reporte',
        })
    }, computed: {}, mounted: function () {
    }, methods: {
        openModal() {
            let $vue = this;
            $vue.$refs.modalCarrera.open();
        }, generarReporte() {
            let $vue = this;
            console.log($vue.carrera.id);
            if ($vue.carrera === undefined || $vue.carrera === 'undefined' || $vue.carrera === '') {
                console.log($vue.carrera);
                notify("Debe seleccionar una carrera", "error");
                return;
            }

            location.href = APP.url('nivelacioneegg/reporte/informeNivelacionByCarrera/' + $vue.carrera.id);

        }

    }
});
